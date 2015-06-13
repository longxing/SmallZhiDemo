package com.smallzhi.push.manager;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.Future;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.future.WriteFuture;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

import com.smallzhi.push.entity.Message;
import com.smallzhi.push.exception.NetWorkDisableException;
import com.smallzhi.push.exception.SessionDisableException;
import com.smallzhi.push.exception.WriteToClosedSessionException;
import com.smallzhi.push.filter.ClientMessageCodecFactory;
import com.smallzhi.push.util.CommonUtil;
import com.smallzhi.push.util.LogManager;
import com.smallzhi.push.util.PushConstant;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;


/**
 * 连接服务端管理，cim核心处理类，管理连接，以及消息处理
 * 
 */
public class ConnectorManager  {

	private  NioSocketConnector connector;
	private ConnectFuture connectFuture;
	private  IoSession session;

	Context context;


	private static ConnectorManager manager;
	private ConnectorManager(){};

	// 收到命令广播action
	public static final String ACTION_COMMAND_RECEIVED = "com.smallzhi.COMMAND_RECEIVED";

	// 发送sendbody失败广播
	public static final String ACTION_SENT_FAILED = "com.smallzhi.SENT_FAILED";

	// 发送sendbody成功广播
	public static final String ACTION_SENT_SUCCESS = "com.smallzhi.SENT_SUCCESS";
	// 连接意外关闭广播
	public static final String ACTION_CONNECTION_CLOSED = "com.smallzhi.CONNECTION_CLOSED";
	// 连接失败广播
	public static final String ACTION_CONNECTION_FAILED = "com.smallzhi.CONNECTION_FAILED";
	// 连接成功广播
	public static final String ACTION_CONNECTION_SUCCESS = "com.smallzhi.CONNECTION_SUCCESS";
	// 发送sendbody成功后获得replaybody回应广播
	public static final String ACTION_REPLY_RECEIVED = "com.smallzhi.REPLY_RECEIVED";
	// 网络变化广播
	public static final String ACTION_NETWORK_CHANGED = "android.net.conn.CONNECTIVITY_CHANGE";

	// 未知异常
	public static final String ACTION_UNCAUGHT_EXCEPTION = "com.smallzhi.UNCAUGHT_EXCEPTION";

	// CIM连接状态
	public static final String ACTION_CONNECTION_STATUS = "com.smallzhi.CONNECTION_STATUS";
	protected static final String REPLY_TIME = "REPLY_TIME";


	private  ExecutorService executor;


	private ConnectorManager(Context ctx) {
		context = ctx;
		//创建一个线程管理器，放入3个线程
		executor = Executors.newFixedThreadPool(3);
		//创建nioSoket
		connector = new NioSocketConnector();
		LogManager.d("监控：connector初始化");
		//设置连接超时时间   30s
		connector.setConnectTimeoutMillis(PushConstant.TIME_OUT);
		//设置空闲时间间隔  发送心跳间隔 10s
		connector.getSessionConfig().setBothIdleTime(PushConstant.IDLE_TIME);
		//设置连接为长连接
		connector.getSessionConfig().setKeepAlive(true);
		//添加过滤器
		connector.getFilterChain().addLast("logger", new LoggingFilter());
		connector.getFilterChain().addLast("codec", new ProtocolCodecFilter(new ClientMessageCodecFactory()));
		//设置业务实现
		connector.setHandler(iohandler);
	}

	public synchronized static ConnectorManager getManager(Context context) {
		if (manager == null) {
			manager = new ConnectorManager(context);
			LogManager.d("监控：ConnectorManager初始化"+context.getClass());
		}
		return manager;
	}

	private synchronized void  syncConnection(final String domain,final int port) {
		try {
			//若已连接则直接返回
			if(isConnected()){
				return ;
			}
			//若未连接,根据域名获得ip
			String ip = InetAddress.getByName(domain).getHostAddress();
			InetSocketAddress remoteSocketAddress = new InetSocketAddress(ip, port);
			connectFuture = connector.connect(remoteSocketAddress);
			connectFuture.awaitUninterruptibly();//等待连接成功,相当于将异步执行转为同步执行
			session = connectFuture.getSession();//获取连接成功后的会话对象

		} catch (Exception e) {
			//若未连接成功则发广播
			Intent intent = new Intent();
			intent.setAction(ACTION_CONNECTION_FAILED);
			intent.putExtra(PushConstant.EXCEPTION_KEY, e);
			context.sendBroadcast(intent);
		}
	}

	public  void connect(final String domain, final int port) {
		if (!netWorkAvailable(context)) {//若网络不正常
			Intent intent = new Intent();
			//广播通知网络连接失败
			intent.setAction(ACTION_CONNECTION_FAILED);
			intent.putExtra(PushConstant.EXCEPTION_KEY, new NetWorkDisableException());
			context.sendBroadcast(intent);
			return;
		}
		//放入线程
		Future<?> future = executor.submit(new Runnable() {
			@Override
			public void run() {
				//连接服务器
				syncConnection(domain, port);
			}
		});
		try {
			if(future.get()!=null)
			{
				connect(domain,port);
			}
		} catch (Exception e) {

			connect(domain,port);
			e.printStackTrace();
		}  
	}

	public void send(final Message message) {


		executor.execute(new Runnable() {
			@Override
			public void run() {

				android.os.Message msg = new android.os.Message();
				msg.getData().putSerializable(PushConstant.MESSAGE_KEY, message);
				//若session不为空
				if(session!=null && session.isConnected())
				{
					WriteFuture wf = session.write(message);
					// 消息发送超时 10秒
					wf.awaitUninterruptibly(5, TimeUnit.SECONDS);//超时时间5秒，间隔单位
					//若
					if (!wf.isWritten()) {//若没有写入完成
						Intent intent = new Intent();
						intent.setAction(ACTION_SENT_FAILED);
						intent.putExtra(PushConstant.EXCEPTION_KEY, new WriteToClosedSessionException());
						intent.putExtra(PushConstant.MESSAGE_KEY, message);
						context.sendBroadcast(intent);
					}
				}else
				{//若session为空

					Intent intent = new Intent();
					intent.setAction(ACTION_SENT_FAILED);
					intent.putExtra(PushConstant.EXCEPTION_KEY, new SessionDisableException());
					intent.putExtra(PushConstant.MESSAGE_KEY, message);
					context.sendBroadcast(intent);
				}
			}
		});
	}
	public boolean isConnected() {
		LogManager.d("监控session:"+(session != null?true:false)+"|connector:"+(connector != null?true:false));
		if (session == null || connector == null) {
			return false;
		}
		LogManager.d("监控isConnected:"+session.isConnected());
		return session.isConnected() ;
	}

	public void deliverIsConnected() {//异步获得连接状态
		Intent intent = new Intent();
		intent.setAction(ACTION_CONNECTION_STATUS);
		intent.putExtra(PushConstant.CONNECT_STATUS, isConnected());
		context.sendBroadcast(intent);
	}


	IoHandlerAdapter iohandler = new IoHandlerAdapter() {

		@Override
		public void sessionCreated(IoSession session) throws Exception {

			LogManager.d("client******************CIM连接服务器成功:"+session.getLocalAddress());

			Intent intent = new Intent();
			intent.setAction(ACTION_CONNECTION_SUCCESS);
			context.sendBroadcast(intent);

		}

		@Override
		public void sessionOpened(IoSession session) throws Exception {
		}

		@Override
		public void sessionClosed(IoSession session) throws Exception {

			LogManager.d("client******************CIM与服务器断开连接:"+session.getLocalAddress());
			if(ConnectorManager.this.session.getId()==session.getId())
			{

				Intent intent = new Intent();
				intent.setAction(ACTION_CONNECTION_CLOSED);
				context.sendBroadcast(intent);

			}
		}

		@Override
		public void sessionIdle(IoSession session, IdleStatus status)
				throws Exception {
			//获得最新回复收到时间
			Long timestamp = (Long) session.getAttribute(ConnectorManager.REPLY_TIME);
			LogManager.d("最新收到回复时间戳："+timestamp);
			//若当前时间与最后收到时间差超过10s则判断已断开
			if(new Date().getTime() - timestamp>PushConstant.TIME_OUT){
				LogManager.d("心跳超时客户端关闭session,最后心跳时间为："+timestamp+"|当前时间为："+new Date().getTime());
				sessionClosed(session);//调用关闭连接
				return;
			}
			LogManager.d("client******************CIM与服务器连接空闲:"+session.getLocalAddress());
			//生成心跳请求
			Message msg = CommonUtil.createHeartBeat(context);
			//空闲时发送心跳
			send(msg);
		}

		@Override
		public void exceptionCaught(IoSession session, Throwable cause)
				throws Exception {

			Intent intent = new Intent();
			intent.setAction(ACTION_UNCAUGHT_EXCEPTION);
			intent.putExtra(PushConstant.EXCEPTION_KEY, cause);
			context.sendBroadcast(intent);
		}

		@Override
		public void messageReceived(IoSession session, Object obj)
				throws Exception {
			//保存收到服务端回复的最新时间
			session.setAttribute(ConnectorManager.REPLY_TIME,new Date().getTime());
			Message msg = (Message) obj;
			LogManager.d("msgReceived："+msg.toString());
			if(PushConstant.TYPE_RESPONSE.equals(msg.getType())){//若是响应
				//continue ... 若是心跳返回则直接处理 不发广播
				if(PushConstant.ACTION_HEART_BEAT.equals(msg.getAction())){//若是心跳返回
					
				}else{//若是普通返回
					Intent intent = new Intent();
					intent.setAction(ACTION_REPLY_RECEIVED);
					intent.putExtra(PushConstant.MESSAGE_KEY, msg);
					LogManager.d("ReplysendBroadcast："+msg.toString());
					context.sendBroadcast(intent);
				}
			}else if(PushConstant.TYPE_COMMAND.equals(msg.getType())){//若是命令
				Intent intent = new Intent();
				intent.setAction(ACTION_COMMAND_RECEIVED);
				intent.putExtra(PushConstant.MESSAGE_KEY,msg);
				LogManager.d("CMDsendBroadcast："+msg.toString());
				context.sendBroadcast(intent);
			}
		}

		@Override
		public void messageSent(IoSession session, Object message)
				throws Exception {
			Message msg = (Message) message;
			if(!PushConstant.ACTION_HEART_BEAT.equals(msg.getAction())){//若发送非心跳
				Intent intent = new Intent();
				intent.setAction(ACTION_SENT_SUCCESS);
				intent.putExtra(PushConstant.MESSAGE_KEY,msg);
				context.sendBroadcast(intent);	
			}
		}
	};

	public static boolean netWorkAvailable(Context context) {
		try {
			//获得系统连接管理器
			ConnectivityManager nw = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
			//获得网络信息
			NetworkInfo networkInfo = nw.getActiveNetworkInfo();
			//若网络信息不为null返回true 否则返回false
			return networkInfo != null;
		} catch (Exception e) {
			LogManager.d("******************************网络异常****");
		}
		return false;
	}


}