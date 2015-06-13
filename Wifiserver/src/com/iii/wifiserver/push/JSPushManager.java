package com.iii.wifiserver.push;

import android.content.Context;
import android.net.NetworkInfo;

import com.iii.wifi.util.BoxSystemUtils;
import com.iii360.sup.common.utl.LogManager;
import com.iii360.sup.common.utl.SystemUtil;
import com.smallzhi.push.entity.Message;
import com.smallzhi.push.manager.EnventListenerReceiver;
import com.smallzhi.push.manager.MessageListener;
import com.smallzhi.push.manager.PushManager;

public class JSPushManager {

	public static void init(final Context ctx) {
		EnventListenerReceiver.addListener(new MessageListener() {

			@Override
			public void onMessageReceived(Message msg) {
				System.out.println("收到广播************************" + msg.toString());
				JSPushParser.excute(ctx, msg);

			}

			@Override
			public void onNetworkChanged(NetworkInfo info) {
				System.out.println("收到广播************************网络状态改变");
			}

			@Override
			public void onReplyReceived(Message msg) {
				System.out.println("收到广播********回复****************" + msg.toString());

			}

			@Override
			public void onConnectionClosed() {
				System.out.println("收到广播************************连接断开");

			}

			@Override
			public void onConnectionStatus(boolean isConnected) {
				System.out.println("收到广播************************连接状态" + isConnected);
			}

			@Override
			public void onConnectionSucceed() {
				System.out.println("收到广播***********************连接成功");
			}

			@Override
			public void onSentSucceed(Message msg) {
				System.out.println("收到广播***********************发送成功");

			}

		});

		LogManager.e("硬件版本号:" + SystemUtil.getProductName() + "||音箱序列号:" + BoxSystemUtils.getFirmwareVersion() + "||系统固件版本号:"
				+ BoxSystemUtils.getSerialNumber());

		// 硬件版本号
		// 音箱序列号:000000000000
		// 系统固件版本号:1.0.0.0
//		PushManager.init(ctx, "192.168.20.30", 8088, SystemUtil.getProductName(), BoxSystemUtils.getFirmwareVersion(),
//				BoxSystemUtils.getSerialNumber());
		PushManager.init(ctx, "push.smallzhi.com", 8088, SystemUtil.getProductName(), BoxSystemUtils.getFirmwareVersion(),
				BoxSystemUtils.getSerialNumber());
	}
}
