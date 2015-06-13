package com.iii360.box;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.Parcelable;

import com.iii360.box.common.BasePreferences;
import com.iii360.box.protocol.MulticastSender;
import com.iii360.box.protocol.UDPBroadcastReceiver;
import com.iii360.box.util.KeyList;
import com.iii360.box.util.LogManager;
import com.iii360.box.util.LogUtil;
import com.iii360.box.util.WifiUtils;

/**
 * 后台Service
 * 
 * @author Administrator
 * 
 */
public class MyService extends Service {
	private boolean isLoop;
	private int time = 5000;
	private Thread checkThread;
	public static MyService instance;
	private BasePreferences preferences;
	public static String testIp = "192.168.30.113";
	private BroadcastReceiver screenReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals(Intent.ACTION_SCREEN_ON)) {
				new Handler().postDelayed(new Runnable() {

					@Override
					public void run() {
						MulticastSender.sendLongTime(MyService.this, "group", 10000);
						isLoop = true;
						checkThread = new Thread(runnable);
						checkThread.start();
					}
				}, 1000);

			} else if (action.equals(Intent.ACTION_SCREEN_OFF)) {
				isLoop = false;
			} else if (WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(action)) {
				Parcelable parcelable = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
				if (parcelable == null)
					return;
				NetworkInfo networkInfo = (NetworkInfo) parcelable;
				State state = networkInfo.getState();

				switch (state) {
				case CONNECTED:// wifi连接成功
					LogUtil.i("MyService  wifi连接成功 ");
					if (System.currentTimeMillis() - createdTime > 2000) {
						MulticastSender.sendLongTime(MyService.this, "group", 10000);
					}
					break;
				case DISCONNECTED:// wifi连接失败
				case UNKNOWN:// 未知错误
					// valiSSID();
					break;
				default:
					break;
				}
			}
		}
	};
	private Runnable runnable = new Runnable() {

		@Override
		public void run() {
			handleInThread();
		}
	};
	private long createdTime;

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		LogUtil.i("MyService onCreate ... ");
		UDPBroadcastReceiver.getInstance(this);
		// MulticastSender.sendLongTime(this,"group", 10000);
		// UdpRunService.getInstance(this, BoxManagerUtils.getBoxUdpPort(this));
		IntentFilter filter = new IntentFilter();
		filter.addAction(Intent.ACTION_SCREEN_ON);
		filter.addAction(Intent.ACTION_SCREEN_OFF);
		filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
		registerReceiver(screenReceiver, filter);
		instance = this;
		preferences = new BasePreferences(this);
		// terry start
		isLoop = true;
		checkThread = new Thread(runnable);
		checkThread.start();
		// terry end
		createdTime = System.currentTimeMillis();
	}

	/**
	 * 当前连接的盒子离线时发送广播
	 */
	protected void checkBoxOnline() {

		Map<String, Long> map = MyApplication.getBoxAdds();
		String onboxip = preferences.getPrefString(KeyList.GKEY_BOX_IP_ADDRESS);
		if (onboxip == null || "".equals(onboxip)) {
			return;
		}
		if (!map.containsKey(onboxip) && WifiUtils.isConnectWifi(this)) {
			Intent intent = new Intent(KeyList.ACTION_BOX_OUT_OF_LINE);
			sendBroadcast(intent);
		}
	}

	private void handleInThread() {
		while (isLoop) {
			try {
				Map<String, Long> map = MyApplication.getBoxAdds();
				LogUtil.i("在线盒子" + map);
				Map<String, Long> temp = cloneMap(map);
				try {
					Thread.sleep(time);
				} catch (Exception e) {
					e.printStackTrace();
				}
				checkBoxOnline();
				Iterator<Entry<String, Long>> it = temp.entrySet().iterator();
				while (it.hasNext()) {
					Entry<String, Long> entry = it.next();
					long value = entry.getValue();
					String key = entry.getKey();
					if (map.containsKey(key)) {
						if (map.get(key) == value) {
							map.remove(entry.getKey());
							// sendBroadcast(new Intent(
							// KeyList.PKEY_HAVE_BOX_GONE));
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		// check();
	}

	protected Map<String, Long> cloneMap(Map<String, Long> map) {

		try {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(out);
			oos.writeObject(map);
			oos.flush();
			byte[] data = out.toByteArray();
			ByteArrayInputStream in = new ByteArrayInputStream(data);
			ObjectInputStream ois = new ObjectInputStream(in);
			return (Map<String, Long>) ois.readObject();
		} catch (Exception e) {
			e.printStackTrace();
			return new ConcurrentHashMap<String, Long>();
		}

	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		LogManager.i("MyService onStartCommand ... ");
		return super.onStartCommand(intent, flags, START_STICKY);
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		LogUtil.i("MyService onDestroy ... ");
		isLoop = false;
		try {
			checkThread.interrupt();
		} catch (Exception e) {
			e.printStackTrace();
		}
		unregisterReceiver(screenReceiver);
		UDPBroadcastReceiver.getInstance(this).stopReceiver();
		startService(new Intent(this, MyService.class));
	}
}
