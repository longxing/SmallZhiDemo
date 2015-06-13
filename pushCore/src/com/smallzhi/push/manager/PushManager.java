package com.smallzhi.push.manager;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import com.smallzhi.push.entity.Message;
import com.smallzhi.push.util.CommonUtil;
import com.smallzhi.push.util.DataConfig;
import com.smallzhi.push.util.LogManager;
import com.smallzhi.push.util.PushConstant;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;


/**
 * CIM 功能接口
 * 
 */
public class PushManager  {

	private static Timer timer = new Timer();
	static String  ACTION_CONNECTION ="ACTION_CONNECTION";//连接

	static String  ACTION_CONNECTION_STATUS ="ACTION_CONNECTION_STATUS";//连接状态

	static String  ACTION_SENDREQUEST ="ACTION_SENDREQUEST";//发送请求

	static String  SERVICE_ACTION ="SERVICE_ACTION";//key 

	static String  KEY_SEND_BODY ="KEY_SEND_BODY";//发送内容

	/**
	 * 初始化塞入设备标识一级软件版本
	 * @author tart
	 * @date 2014-11-20 下午12:05:28
	 * @param @param context
	 * @param @param domain
	 * @param @param port
	 * @param @param driverName
	 * @return void
	 * @throws
	 */
	public static  void init(Context ctx,String domain,int port,String hardwareVersion,String softwareVersion,String sessionKey){
		//保存主机domain和端口
		DataConfig.putString(ctx, DataConfig.HARDWARE_VERSION, hardwareVersion);
		DataConfig.putString(ctx, DataConfig.SOFTWARE_VERSION, softwareVersion);
		//保存主机domain和端口
		DataConfig.putString(ctx, DataConfig.SERVIER_DOMAIN, domain);
		DataConfig.putInt(ctx, DataConfig.SERVIER_PORT, port);
		DataConfig.putString(ctx, DataConfig.SESSION_KEY, sessionKey);
		init(ctx);
	}
	/**
	 * 初始化塞入设备标识一级软件版本
	 * @author tart
	 * @date 2014-11-20 下午12:05:28
	 * @param @param context
	 * @param @param domain
	 * @param @param port
	 * @param @param driverName
	 * @return void
	 * @throws
	 */
	public static  void init(Context ctx,String domain,int port,String hardwareVersion,String softwareVersion,String sessionKey,boolean showLog,boolean wirteToFile){
		init(ctx,domain,port,hardwareVersion,softwareVersion,sessionKey);
		LogManager.initLogManager(showLog,wirteToFile);
	}
	/**
	 * 
	 * @date 2014-11-14 下午12:02:09
	 * @param @param context
	 * @return void
	 * @throws
	 */
	protected static  void init(Context ctx){
		//获得host,port
		String domain = DataConfig.getString(ctx, DataConfig.SERVIER_DOMAIN);
		int port =DataConfig.getInt(ctx, DataConfig.SERVIER_PORT);
		//进行初始化
		//意图为服务
		Intent serviceIntent  = new Intent(ctx, PushService.class);
		//传入参数 domain,port,行为
		serviceIntent.putExtra(DataConfig.SERVIER_DOMAIN, domain);
		serviceIntent.putExtra(DataConfig.SERVIER_PORT, port);
		serviceIntent.putExtra(SERVICE_ACTION, ACTION_CONNECTION);//意图为连接
		ctx.startService(serviceIntent);//启动服务
		//开启监控
		monitorPushServer(ctx);
	}


	/**
	 * @author tart
	 * @date 2014-12-11 下午4:54:09
	 * @param @param ctx
	 * @return void
	 * @throws
	 */
	private static void monitorPushServer(final Context ctx) {
		//清空广播器
		timer.cancel();
		//重新添加任务
		timer = new Timer();
		TimerTask timerTask = new TimerTask(){
			@Override
			public void run() {
				LogManager.d("监控服务状态：网络状态："+ConnectorManager.netWorkAvailable(ctx)+"|程序是否运行："+serverIsRun(ctx)+"|是否进行连接："+ConnectorManager.getManager(ctx).isConnected());
				if(!(serverIsRun(ctx)&&ConnectorManager.getManager(ctx).isConnected())){//若服务在，且session是联通的   取反
					if(ConnectorManager.netWorkAvailable(ctx)){//若网络正常
						LogManager.d("服务死亡die,重启服务");
						init(ctx);	
					}
				}
			}
		};
		//添加广播
		timer.schedule(timerTask, PushConstant.MONITOR_PUSH_SERVER_DELAY, PushConstant.MONITOR_PUSH_SERVER_PERIOD);
	}

	/**
	 * 通过唯一标示登录服务器
	 * @param account 用户唯一ID
	 */
	public static  void bind(Context context){
		Message msg = CommonUtil.createBind(context);
		//发送绑定请求
		sendRequest(context,msg);
	}



	/**
	 * 发送一个请求
	 * @param context
	 * @body
	 */
	public static  void sendRequest(Context context,Message msg){
		//发送请求
		Intent serviceIntent  = new Intent(context, PushService.class);
		serviceIntent.putExtra(KEY_SEND_BODY, msg);
		serviceIntent.putExtra(SERVICE_ACTION, ACTION_SENDREQUEST);
		context.startService(serviceIntent);

	}
	/**
	 * 异步获取连接状态
	 * @param context
	 */
	public void detectIsConnected(Context context){
		Intent serviceIntent  = new Intent(context, PushService.class);
		serviceIntent.putExtra(SERVICE_ACTION, ACTION_CONNECTION_STATUS);
		context.startService(serviceIntent);
	}
	public static boolean serverIsRun(Context ctx)  
	{  
		ActivityManager myManager=(ActivityManager)ctx.getSystemService(Context.ACTIVITY_SERVICE);  
		ArrayList<RunningServiceInfo> runningService = (ArrayList<RunningServiceInfo>) myManager.getRunningServices(70);  
		for(int i = 0 ; i<runningService.size();i++)  
		{
			if(runningService.get(i).service.getClassName().toString().equals(PushConstant.PUSH_SERVER_NAME))  
			{  
				return true;  
			}  
		}  
		return false;  
	}
}