
package com.smallzhi.push.manager;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import com.smallzhi.push.entity.Message;
import com.smallzhi.push.util.DataConfig;
import com.smallzhi.push.util.LogManager;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;


/**
 * 与服务端连接服务
 * @author 3979434
 *
 */
public class PushService extends Service {
	
	public static Executor executor=Executors.newFixedThreadPool(3);//线程池中放入3个线程
	private static ConnectorManager manager;

	private IBinder binder=new PushService.LocalBinder();//绑定本地服务

	@Override
	public int onStartCommand(Intent intent,int flags, int startId) {
		//获得连接管理器
		manager = ConnectorManager.getManager(this.getApplicationContext());    	
		//意图为空
		if(intent==null)
		{
			return super.onStartCommand(intent, flags, startId);
		}
		//以下为连接操作
		executor.execute(new PushThread(manager,intent));
		//若为连接操作
		return Service.START_STICKY;//粘性启动
	}


	@Override
	public IBinder onBind(Intent arg0) {
		return binder;
	}

	public class LocalBinder extends Binder{

		public PushService getService()
		{
			return PushService.this;
		}
	}
	class PushThread implements Runnable{
		private String action;
		private ConnectorManager manager;
		private Intent intent;
		public PushThread(ConnectorManager manager,Intent intent){
			//获得意图需要执行的操作
			this.intent = intent;
			this.action = intent.getStringExtra(PushManager.SERVICE_ACTION);
			this.manager = manager;
		}
		@Override
		public void run() {
			if(PushManager.ACTION_CONNECTION.equals(action))
			{
				String host = intent.getStringExtra(DataConfig.SERVIER_DOMAIN);//获得domain
				int port = intent.getIntExtra(DataConfig.SERVIER_PORT, 23456);//获得端口号
				//连接
				manager.connect(host,port);
			}
			//若为发送请求操作
			if(PushManager.ACTION_SENDREQUEST.equals(action))
			{	
				//发送请求
				manager.send((Message) intent.getSerializableExtra(PushManager.KEY_SEND_BODY));
			}
			if(PushManager.ACTION_CONNECTION_STATUS.equals(action))
			{
				manager.deliverIsConnected();
			}
		}
		
	}
}
