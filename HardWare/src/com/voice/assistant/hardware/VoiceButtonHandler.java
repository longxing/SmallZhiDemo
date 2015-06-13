package com.voice.assistant.hardware;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;

import com.iii360.base.common.utl.IGloableHeap;
import com.iii360.base.common.utl.KeyList;
import com.iii360.base.common.utl.LogManager;
import com.iii360.base.inf.BasicServiceUnion;
import com.iii360.base.inf.ITTSController;
import com.iii360.base.umeng.UmengUtil;

public class VoiceButtonHandler extends ButtonHandler {

	private static final String TAG = "HardWare VoiceButtonHandler";
	private BasicServiceUnion mUnion;
	private boolean mIsAddVolume;
	private ExecutorService mService;
	private MediaPlayer mPlayer;
	private Ringtone ring = null;

	public VoiceButtonHandler(BasicServiceUnion union, boolean add) {
		this.mUnion = union;
		mIsAddVolume = add;
		Context context = mUnion.getBaseContext().getContext();
		// mPlayer = MediaPlayer.create(mUnion.getBaseContext().getContext(),
		// R.raw.volume);
		Uri uri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.getPackageName() + "/" + R.raw.volume);
		ring = RingtoneManager.getRingtone(context, uri);
		ring.setStreamType(AudioManager.STREAM_MUSIC);
	}

	@Override
	public void onShortClick() {
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
		WakeUpLightControl wakeUpLightControl = (WakeUpLightControl) mUnion.getBaseContext().getGlobalObject(KeyList.GKEY_WAKEUP_LIGHT_CONTROL);
		if (wakeUpLightControl != null) {
			wakeUpLightControl.onShortClickOnRunnable();
		}

		AudioManager audioManager = (AudioManager) mUnion.getBaseContext().getContext().getSystemService(Context.AUDIO_SERVICE);
		int current = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
		if (mIsAddVolume) {
			int max = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
			if (current == max) {
				LogManager.d(TAG, "增加音量+1 : 音量最大是" + max);
				play();
				return;
			}
			if (!mUnion.getBaseContext().getPrefBoolean(KeyList.PKEY_INCRICE_VOICE_FIRSTCLICK)) {
				mUnion.getBaseContext().setPrefBoolean(KeyList.PKEY_INCRICE_VOICE_FIRSTCLICK, true);
				mUnion.getTTSController().play("点击红心调大音量，长按红心表示喜欢正在播放的歌曲");
			}

			if (mUnion.getBaseContext().getPrefBoolean("PKEY_CUREENT_MUSIC_IS_AIRPLAY")) {
				LogManager.d(TAG, "airplay 增加音量+1");
				mUnion.getBaseContext().getContext().sendBroadcast(new Intent("IKEY_INCREASE_VOLUME"));

			} else if (mUnion.getBaseContext().getPrefBoolean("PKEY_CUREENT_MUSIC_IS_DLAN")) {
				int curr = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
				LogManager.d(TAG, "dlan 增加音量+1 : " + curr);
				audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, (int) (curr + 1), 0);
				mUnion.getBaseContext().getContext().sendBroadcast(new Intent("IKEY_VOLUME_CHANGED"));

			} else {
				LogManager.d(TAG, "普通 增加音量+1 : " + current);
				audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, (int) (current + 1), 0);
			}

			// 红心点击事件
			mUnion.getBaseContext().sendUmengEvent(UmengUtil.RED_HEART_SHORTCLICK, UmengUtil.RED_HEART_SHORTCLICK_CONTENT);

		} else {
			if (current == 1) {
				LogManager.d(TAG, "减小音量-1 : 音量最小是1");
				play();
				return;
			}
			if (!mUnion.getBaseContext().getPrefBoolean(KeyList.PKEY_DINCRICE_VOICE_FIRSTCLICK)) {
				mUnion.getBaseContext().setPrefBoolean(KeyList.PKEY_DINCRICE_VOICE_FIRSTCLICK, true);
				mUnion.getTTSController().play("点击垃圾桶调小音量，长按垃圾桶表示讨厌正在播放的歌曲");
			}
			if (mUnion.getBaseContext().getPrefBoolean("PKEY_CUREENT_MUSIC_IS_AIRPLAY")) {
				LogManager.d(TAG, "airplay 减小音量-1");
				mUnion.getBaseContext().getContext().sendBroadcast(new Intent("IKEY_DECREASE_VOLUME"));

			} else if (mUnion.getBaseContext().getPrefBoolean("PKEY_CUREENT_MUSIC_IS_DLAN")) {
				int curr = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
				LogManager.d(TAG, "dlan 减小音量-1 : " + curr);
				audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, (int) (curr - 1), 0);
				mUnion.getBaseContext().getContext().sendBroadcast(new Intent("IKEY_VOLUME_CHANGED"));

			} else {
				LogManager.d(TAG, "普通 减小音量-1 : " + current);
				audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, (int) (current - 1), 0);
			}

			// 垃圾桶点击事件
			mUnion.getBaseContext().sendUmengEvent(UmengUtil.TRASH_SHORTCLICK, UmengUtil.TRASH_SHORTCLICK_CONTENT);
		}
		play();

	}

	private void play() {
		if (!mUnion.getMediaInterface().isPlaying() && !mUnion.getBaseContext().getPrefBoolean("PKEY_CUREENT_MUSIC_IS_DLAN")) {
			ring.play();
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			ring.stop();
		}
	}

	@Override
	public void onLongClick() {
		// TODO Auto-generated method stub
		// if (isNewTouch) {
		// if (mIsAddVolume) {
		// mUnion.getCommandEngine().handleText("好听");
		// } else {
		// mUnion.getCommandEngine().handleText("难听");
		// }
		// }

	}

	@Override
	public void onLongLongClick() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onLongClickInTouch() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onClickInTouch() {
		if (mService == null) {
			mService = Executors.newCachedThreadPool();
		}
		mService.execute(new Runnable() {

			@Override
			public void run() {
				setOnClickInTouch();
			}
		});
	}

	private synchronized void setOnClickInTouch() {

		if (isNewTouch) {
			// 无线媒体协议，不支持收藏
			if (mUnion.getBaseContext().getPrefBoolean("PKEY_CUREENT_MUSIC_IS_AIRPLAY")) {
				return;
			} else if (mUnion.getBaseContext().getPrefBoolean("PKEY_CUREENT_MUSIC_IS_DLAN")) {
				ITTSController mTTSController = (ITTSController) ((IGloableHeap) mUnion.getBaseContext().getContext().getApplicationContext()).getGlobalObjectMap().get(KeyList.GKEY_TTS_CONTORLLER);

				if (mIsAddVolume) {
					mTTSController.play("DLNA暂不支持收藏功能");
				} else {
					mTTSController.play("DLNA暂不支持歌曲切换");
				}
				return;
			}
			if (mIsAddVolume) {
				mUnion.getCommandEngine().handleText("好听");
				// 红心长按事件
				mUnion.getBaseContext().sendUmengEvent(UmengUtil.RED_HEART_LONGCLICK, UmengUtil.RED_HEART_LONGCLICK_CONTENT);
			} else {
				mUnion.getCommandEngine().handleText("难听");
				// 垃圾桶长按事件
				mUnion.getBaseContext().sendUmengEvent(UmengUtil.TRASH_LONGCLICK, UmengUtil.TRASH_LONGCLICK_CONTENT);
			}
		}

	}

}
