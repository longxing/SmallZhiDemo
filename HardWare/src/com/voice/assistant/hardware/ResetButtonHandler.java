package com.voice.assistant.hardware;

import java.io.File;
import java.io.IOException;

import android.content.Intent;

import com.iii360.base.common.utl.KeyList;
import com.iii360.base.common.utl.LogManager;
import com.iii360.base.inf.BasicServiceUnion;
import com.iii360.base.inf.ITTSController.ITTSStateListener;
import com.iii360.base.inf.recognise.ILightController;
import com.iii360.base.umeng.UmengUtil;
import com.iii360.sup.common.utl.CommandLineExcute;
import com.iii360.sup.common.utl.ShellUtils;

public class ResetButtonHandler extends ButtonHandler {

	private static final String TAG = "HardWare ResetButtonHandler";
	private BasicServiceUnion mBasicServiceUnion;

	public ResetButtonHandler(BasicServiceUnion union) {
		mBasicServiceUnion = union;
	}

	@Override
	public void onShortClick() {
		LogManager.d(TAG, "onshort Click");
		KeyList.RESET_BEGIN = System.currentTimeMillis();
	}

	@Override
	public void onLongClick() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onLongLongClick() {

	}

	@Override
	public void onClickInTouch() {

	}

	@Override
	public void onLongClickInTouch() {
		LogManager.d(TAG, "on onLongClickInTouch");
		mBasicServiceUnion.getBaseContext().getContext().sendBroadcast(new Intent("ASS_RESET"));
		mBasicServiceUnion.getTTSController().play("正在重置中，请稍等。");
		// 统计重置事件
		mBasicServiceUnion.getBaseContext().sendUmengEvent(UmengUtil.RESET_APP_SYSTEM, UmengUtil.RESET_APP_SYSTEM_CONTENT);
		mBasicServiceUnion.getTTSController().setListener(new ITTSStateListener() {

			@Override
			public void onStart() {

			}

			@Override
			public void onInit() {

			}

			@Override
			public void onError() {
				onEnd();
			}

			@Override
			public void onEnd() {
				reset();
				mBasicServiceUnion.getTTSController().setListener(null);
			}
		});
	}

	private void reset() {
		try {
			// 清除主程序配置文件
			String path = "/mnt/sdcard/com.voice.assistant.main";
			if (new File(path).exists()) {
				ShellUtils.execute(false, "rm", "-fr", path);
			}
			// 清除wifiserver配置文件
			path = "/mnt/sdcard/com.iii.wifiserver";
			if (new File(path).exists()) {
				ShellUtils.execute(false, "rm", "-fr", path);
			}
			// 清除主程序安装子文件
			CommandLineExcute.execCommand("su -c rm -r /data/data/com.voice.assistant.main");
			// 清除wifiserver安装子文件
			CommandLineExcute.execCommand("su -c rm -r /data/data/com.iii.wifiserver");
			// 清除已经连接过的wifi账号密码配置文件
			CommandLineExcute.execCommand("su -c rm -f /data/misc/wifi/wpa_supplicant.conf");
			CommandLineExcute.execCommand("su -c rm -f /data/misc/wifi/softap.conf");
			CommandLineExcute.execCommand("su -c rm -f /data/misc/wifi/p2p_supplicant.conf");
			// 重新启动程序
			CommandLineExcute.execCommand("su -c reboot");

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			LogManager.printStackTrace(e);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private int preNetlightMode = ILightController.MODE_NORMAL;

	@Override
	public void onTouchBegin() {
		LogManager.d(TAG, "onTouchBegin");
		NetLightControl netlightControl = (NetLightControl) mBasicServiceUnion.getBaseContext().getGlobalObject(KeyList.GKEY_NET_LIGHT_CONTROL);
		preNetlightMode = netlightControl.getMode();
		// 快速闪烁
		netlightControl.updateMode(ILightController.MODE_HURRY);
	}

	@Override
	public void onTouchEnd() {
		LogManager.d(TAG, "onTouchEnd");
		NetLightControl netlightControl = (NetLightControl) mBasicServiceUnion.getBaseContext().getGlobalObject(KeyList.GKEY_NET_LIGHT_CONTROL);
		// 恢复原状态
		netlightControl.updateMode(preNetlightMode);
	}

}
