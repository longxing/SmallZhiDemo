package com.voice.assistant.hardware;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.iii360.base.common.utl.KeyList;
import com.iii360.base.inf.BasicServiceUnion;
import com.iii360.base.umeng.UmengUtil;
import com.iii360.sup.common.utl.LogManager;
import com.voice.assistant.hardware.deviceCase.DeviceCase;

public class MainButtonhandler extends ButtonHandler {

	private static final String TAG = "HardWare MainButtonhandler";
	private BasicServiceUnion mBasicServiceUnion;
	private ExecutorService mService;

	public MainButtonhandler(BasicServiceUnion union) {
		mBasicServiceUnion = union;
	}

	@Override
	public void onShortClick() {
		LogManager.e("on ShortClick");
		if (mService == null) {
			mService = Executors.newCachedThreadPool();
		}
		mService.execute(new Runnable() {

			@Override
			public void run() {
				setOnShortClick();
			}
		});
	}

	private void setOnShortClick() {

		mBasicServiceUnion.getBaseContext().sendUmengEvent(UmengUtil.LOGO_SHORTCLICK, UmengUtil.LOGO_SHORTCLICK_CONTENT);

		if (mBasicServiceUnion.getBaseContext().getGlobalBoolean(KeyList.GKEY_BOOL_AUTO_CHATED_MODE)) {
			quitChatMode();
			return;
		}
		// 设备测试
		if (System.currentTimeMillis() - KeyList.RESET_BEGIN <= 3000 && !mBasicServiceUnion.getBaseContext().getGlobalBoolean(KeyList.GKEY_DEVICE_CASE, false)) {
			LogManager.e(TAG, "进入硬件测试模式");
			mBasicServiceUnion.getTTSController().syncPlay("进入硬件测试模式");
			KeyList.RESET_BUTTON_CASE = true;
			new DeviceCase(mBasicServiceUnion).start();
			return;
		}

		WakeUpLightControl wakeUpLightControl = (WakeUpLightControl) mBasicServiceUnion.getBaseContext().getGlobalObject(KeyList.GKEY_WAKEUP_LIGHT_CONTROL);
		if (wakeUpLightControl != null) {
			wakeUpLightControl.onShortClickOnRunnable();
		}

		if (mBasicServiceUnion.getBaseContext().getGlobalBoolean(KeyList.GKEY_IS_MUSIC_PLAYING) && mBasicServiceUnion.getBaseContext().getGlobalBoolean(KeyList.GKEY_IS_MUSIC_IN_PLAYING)) {
			mBasicServiceUnion.getCommandEngine().handleText("暂停");
		} else {
			if (mBasicServiceUnion.getBaseContext().getGlobalBoolean(KeyList.GKEY_IS_MUSIC_PLAYING)) {
				mBasicServiceUnion.getCommandEngine().handleText("继续");
			} else {
				mBasicServiceUnion.getCommandEngine().handleText("唱歌");
			}
		}
	}

	@Override
	public void onLongClick() {
		LogManager.d(TAG, "on onLongClick");
	}

	@Override
	public void onLongLongClick() {
		LogManager.d(TAG, "on onLongLongClick");
	}

	@Override
	public void onClickInTouch() {
		LogManager.d(TAG, "on onClickInTouch start");
		mBasicServiceUnion.getBaseContext().sendUmengEvent(UmengUtil.LOGO_LONGCLICK, UmengUtil.LOGO_LONGCLICK_CONTENT);
		if (mBasicServiceUnion.getBaseContext().getGlobalBoolean(KeyList.GKEY_BOOL_AUTO_CHATED_MODE)) {
			quitChatMode();
			return;
		} else {
			mBasicServiceUnion.getHandler().post(new Runnable() {
				@Override
				public void run() {
					LogManager.d(TAG, "doing onClickInTouch");
					// 打断聊天模式、识别过程
					mBasicServiceUnion.getBaseContext().setGlobalBoolean(KeyList.GKEY_BOOL_AUTO_CHATED_MODE, false);
					mBasicServiceUnion.getBaseContext().setGlobalBoolean(KeyList.GKEY_IS_NOW_RECOGNING, false);
					mBasicServiceUnion.getBaseContext().setGlobalBoolean(KeyList.GKEY_IS_NOW_BUFF_RECOGNING, false);
					mBasicServiceUnion.getBaseContext().setGlobalBoolean(KeyList.GKEY_IS_WAKEUP_TO_RECOGNISE, false);

					mBasicServiceUnion.getBaseContext().setGlobalBoolean(KeyList.GKEY_FORCE_RECOGNISE, true);
					mBasicServiceUnion.getBaseContext().setGlobalBoolean(KeyList.PKEY_NEED_START_IMEDIATELEY_AFTER_RECOGNISE, true);
					mBasicServiceUnion.getBaseContext().setGlobalBoolean(KeyList.PKEY_STRING_SPEECH_LOGO_IS_START, true);

					// 设置长按场景
					mBasicServiceUnion.getBaseContext().setGlobalInteger(KeyList.GKEY_VOICE_COMMUNICATION_CAUSE, KeyList.LONG_CLICK_CAUSE);
					mBasicServiceUnion.getRecogniseSystem().startCaptureVoice();
					mBasicServiceUnion.getBaseContext().setGlobalBoolean(KeyList.PKEY_NEED_START_IMEDIATELEY_AFTER_RECOGNISE, false);
				}
			});
		}

	}

	private void quitChatMode() {
		mBasicServiceUnion.getBaseContext().setGlobalBoolean(KeyList.GKEY_BOOL_CHATMODE,false);
		mBasicServiceUnion.getBaseContext().setGlobalBoolean(KeyList.GKEY_BOOL_AUTO_CHATED_MODE, false);
		mBasicServiceUnion.getRecogniseSystem().stopCaptureVoice();
		mBasicServiceUnion.getTTSController().play("小智已退出聊天模式");
	}

	public void onLongClickInTouch() {

	}

}
