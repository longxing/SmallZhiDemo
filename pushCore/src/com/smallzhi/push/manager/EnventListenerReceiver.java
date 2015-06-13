package com.smallzhi.push.manager;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.smallzhi.push.entity.Message;
import com.smallzhi.push.exception.SessionDisableException;
import com.smallzhi.push.util.LogManager;
import com.smallzhi.push.util.PushConstant;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
/**
 *  消息入口，所有消息都会经过这里，msgMembebrList中智能添加一个监听
 *
 */
public class EnventListenerReceiver extends BroadcastReceiver {

	private static List<MessageListener> msgMemberList = new ArrayList<MessageListener>();

	//局域网定时广播器
	private static Timer broadcastTimer = new Timer();
	private static Context context;
	@Override
	public void onReceive(Context ctx, Intent it) {
		context = ctx;
		LogManager.d("总接收器收到广播："+it.getAction());
		if(it.getAction().equals(ConnectorManager.ACTION_NETWORK_CHANGED))
		{//若网络改变
			//获得网络连接对象
			ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService("connectivity");
			android.net.NetworkInfo info = connectivityManager.getActiveNetworkInfo();
			//重连服务端
			LogManager.d("网络改变，开始重连");
			onDevicesNetworkChanged(info);
		}

		else if(it.getAction().equals(ConnectorManager.ACTION_CONNECTION_CLOSED))
		{//连接关闭
			LogManager.d("连接关闭");
			dispatchConnectionClosed();//重连
		}

		else if(it.getAction().equals(ConnectorManager.ACTION_CONNECTION_FAILED))
		{//连接失败
			LogManager.d("连接失败");
			onConnectionFailed((Exception) it.getSerializableExtra(PushConstant.EXCEPTION_KEY));
		}

		else if(it.getAction().equals(ConnectorManager.ACTION_CONNECTION_SUCCESS))
		{//连接成功
			LogManager.d("连接成功");
			dispatchConnectionSucceed();
		}

		else if(it.getAction().equals(ConnectorManager.ACTION_COMMAND_RECEIVED))
		{//服务端命令
			LogManager.d("收到服务端命令");
			for(MessageListener member:msgMemberList){
				member.onMessageReceived((Message)it.getSerializableExtra(PushConstant.MESSAGE_KEY));
			}
		}

		else if(it.getAction().equals(ConnectorManager.ACTION_REPLY_RECEIVED))
		{//获得返回
			//若返回为绑定成功，获得sessionKey 进行局域网广播

			//获得返回对象
			Message msg = (Message)it.getSerializableExtra(PushConstant.MESSAGE_KEY);
			if(PushConstant.ACTION_BIND.equals(msg.getAction()) && PushConstant.STATUS_SUCCESS.equals(msg.getStatus())){//若返回为绑定且状态为成功
				LogManager.d("收到服务端绑定成功返回");
				//获得设备标识进行广播
				String deviceKey = msg.getContent();
				//广播设备标识
				LogManager.d("开始广播");
				broadcastDeviceKey(deviceKey);
				for(MessageListener member:msgMemberList){
					member.onReplyReceived(msg);
				}
			}

		}else if(it.getAction().equals(ConnectorManager.ACTION_SENT_FAILED))
		{//发送失败
			Message msg = (Message)it.getSerializableExtra(PushConstant.MESSAGE_KEY);
			LogManager.d("请求发送失败"+msg.getAction()+"|"+msg.getContent());
			onSentFailed((Exception) it.getSerializableExtra(PushConstant.EXCEPTION_KEY),msg);
		}

		else if(it.getAction().equals(ConnectorManager.ACTION_SENT_SUCCESS))
		{//发送成功
			LogManager.d("请求发送成功");
			for(MessageListener member:msgMemberList){
				member.onSentSucceed((Message)it.getSerializableExtra(PushConstant.MESSAGE_KEY));
			}
		}

		else if(it.getAction().equals(ConnectorManager.ACTION_UNCAUGHT_EXCEPTION))
		{//抛出异常
			LogManager.d("抛出异常");
			onUncaughtException((Exception)it.getSerializableExtra(PushConstant.EXCEPTION_KEY));
		}

		else if(it.getAction().equals(ConnectorManager.ACTION_CONNECTION_STATUS))
		{	
			LogManager.d("获得连接状态");
			for(MessageListener member:msgMemberList){
				member.onConnectionStatus(it.getBooleanExtra(PushConstant.CONNECT_STATUS, false));//默认为false
			}
		}

	}

	private static void broadcastDeviceKey(final String deviceKey) {
		//清空广播器
		broadcastTimer.cancel();
		//重新添加任务
		broadcastTimer = new Timer();
		TimerTask timerTask = new TimerTask(){

			@Override
			public void run() {
				byte[] buff = null;
				try {
					buff = deviceKey.getBytes("UTF-8");
					DatagramPacket packet = new DatagramPacket(buff,buff.length, InetAddress.getByName(PushConstant.BROADCAST_IP), PushConstant.BROADCAST_PORT);
					MulticastSocket ms = new MulticastSocket();
					ms.send(packet);
					ms.close();
					LogManager.d("发送数据"+deviceKey);
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				} catch (UnknownHostException e) {
					e.printStackTrace();
				} catch (SocketException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

		};

		//添加广播
		broadcastTimer.scheduleAtFixedRate(timerTask, 0, PushConstant.BROADCAST_PERIOD);
	}

	public static void addListener(MessageListener listener){
		msgMemberList.clear();//先清空再添加
		msgMemberList.add(listener);
	}
	public static void removeListener(MessageListener listener){
		msgMemberList.remove(listener);
	}
	/**
	 * 直接重连
	 * @author tart
	 * @date 2014-11-17 下午5:04:34
	 * @param 
	 * @return void
	 * @throws
	 */
	private void dispatchConnectionClosed() {
		if(ConnectorManager.netWorkAvailable(context))
		{	
			LogManager.d("网络连通，进行直接重连");
			PushManager.init(context);
		}

		//onConnectionClosed();
	}

	/**
	 * 程序是否在后台运行
	 * @author tart
	 * @date 2014-11-17 下午5:10:55
	 * @param @param context
	 * @param @return
	 * @return boolean
	 * @throws
	 */
	protected boolean isInBackground(Context context) {
		List<RunningTaskInfo> tasksInfo = ((ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE)).getRunningTasks(1);
		if (tasksInfo.size() > 0) {

			if (context.getPackageName().equals(
					tasksInfo.get(0).topActivity.getPackageName())) {//若首层activity的包名和本包名相同则是前台否则是后台

				return false;
			}
		}
		return true;
	}
	/**
	 *连接失败后
	 * @author tart
	 * @date 2014-11-17 下午5:11:13
	 * @param @param e
	 * @return void
	 * @throws
	 */
	private void onConnectionFailed(Exception e){
		LogManager.d("连接失败，30s后继续重连");
		if(ConnectorManager.netWorkAvailable(context))
		{//若网络良好则间隔30秒后重连
			connectionHandler.sendMessageDelayed(connectionHandler.obtainMessage(), PushConstant.CONNECT_FAIL_TIME);
		}
	}


	Handler connectionHandler = new Handler()
	{
		@Override
		public void handleMessage(android.os.Message message){

			PushManager.init(context);
		}

	};
	/**
	 * 若连接成功
	 * @author tart
	 * @date 2014-11-17 下午5:15:37
	 * @param 
	 * @return void
	 * @throws
	 */
	private void dispatchConnectionSucceed() {
		LogManager.d("绑定到服务器");
		PushManager.bind(context);//绑定
		for(MessageListener member:msgMemberList){
			member.onConnectionSucceed();//调用连接成功
		}
	}



	private void onUncaughtException(Throwable arg0) {}



	private  void onDevicesNetworkChanged(NetworkInfo info) {
		if(info !=null)
		{//若网络连通
			LogManager.d("网络连通，进行重连");
			PushManager.init(context);
		} 
		for(MessageListener member:msgMemberList){
			member.onNetworkChanged(info);//网络状态改变
		}

	}
	/**
	 * 发送失败
	 * @author tart
	 * @date 2014-11-17 下午5:20:48
	 * @param @param e
	 * @param @param body
	 * @return void
	 * @throws
	 */
	private   void onSentFailed(Exception e, Message msg){

		//与服务端端开链接，重新连接
		if(e instanceof SessionDisableException)
		{
			PushManager.init(context);
		}else
		{
			//发送失败 重新发送
			PushManager.sendRequest(context, msg);
		}

	}

}