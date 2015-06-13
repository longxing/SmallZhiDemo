package com.iii.wifiserver.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.RemoteException;
import android.text.TextUtils;

import com.iii.wifi.util.HardwareUtils;
import com.iii.wifi.util.KeyList;
import com.iii.wifiserver.DogControllerService;
import com.iii360.sup.common.utl.LogManager;
import com.iii360.sup.common.utl.SuperBaseContext;
import com.iii360.sup.common.utl.net.UdpClient;

/**
 * 全局广播
 * 
 * @author Administrator
 * 
 */
public class GlobalReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		String action = intent.getAction();
		// 主程序播放歌曲开始
		if (action.equals(KeyList.AKEY_MUSIC_PLAY)) {
			LogManager.i("主程序播放歌曲开始");

			// 主程序播放歌曲结束
		} else if (action.equals(KeyList.AKEY_MUSIC_STOP)) {
			LogManager.i("主程序播放歌曲结束");
			send(HardwareUtils.ACTION_MUSIC_STOP, context);

		} else if (action.equals(KeyList.AKEY_COMMAND_CHANGE)) {
			// 通知主程序，更新家电命令
			LogManager.i("通知主程序，更新家电命令");
			try {
				DogControllerService.getCommandListen().onCommandChange();
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else if (action.equals(KeyList.AKEY_HEZI_MUSIC_CHANGE)) {
			// 主程序歌曲播放状态变化
			Bundle bundle = intent.getExtras();
			// [playNext|playPre|pause|resume|start]
			String type = bundle.getString(KeyList.AKEY_HEZI_MUSIC_TYPE);
			String data = bundle.getString(KeyList.AKEY_HEZI_MUSIC_DATA);

			LogManager.i("KeyList.AKEY_HEZI_MUSIC_CHANGE type=" + type + "||data=" + data);

			send(HardwareUtils.AKEY_MUSIC_STATUS_CHANGE, context);
		} else if (action.equals(KeyList.BOOT_COMPLETED)) {
			LogManager.d("GlobalReceiver boot completed to start dogcontrollerServices");
			context.startService(new Intent(context, DogControllerService.class));
		}
	}

	private void send(final String data, final Context context) {
		if (TextUtils.isEmpty(data)) {
			return;
		}
		new Thread(new Runnable() {

			@Override
			public void run() {
				UdpClient.getInstance(new SuperBaseContext(context), true).sendBroadcast(HardwareUtils.AKEY_MUSIC_STATUS_CHANGE.getBytes());
			}
		}).start();
	}

}
