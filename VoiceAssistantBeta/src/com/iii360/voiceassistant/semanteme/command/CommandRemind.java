package com.iii360.voiceassistant.semanteme.command;

//hefeng begin
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.AlarmManager;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.text.TextUtils;

import com.base.data.CommandInfo;
import com.iii360.base.common.utl.BaseContext;
import com.iii360.base.common.utl.LogManager;
import com.iii360.base.inf.BasicServiceUnion;
import com.iii360.base.inf.ITTSController;
import com.iii360.base.inf.ITTSController.ITTSStateListener;
import com.iii360.base.inf.parse.IVoiceCommand;
import com.iii360.base.inf.recognise.IRecogniseSystem;
import com.iii360.base.inf.recognise.IRecogniseSystem.IOnResultListener;
import com.smallzhi.TTS.Main.TTSSameple;
import com.util.VolumeUtil;
import com.voice.assistant.hardware.ButtonHandler;
import com.voice.assistant.hardware.HardWare;
import com.voice.assistant.hardware.IHardWare;
import com.voice.assistant.main.KeyList;
import com.voice.assistant.main.R;

//hefeng end
public class CommandRemind extends AbstractVoiceCommand {
	private CommandInfo mCommandInfo;
	private String confirmText = "";
	private Boolean canNotDelay = true;
	private BasicServiceUnion mUnion;
	private MediaPlayer mPlayer;
	private boolean isQuit = false;
	private String mContent;
	private HardWare hardWare;
	private boolean isShortClick = false;

	// 原来的按钮，用于恢复
	private ButtonHandler mianButtonHandler = null;
	// 记录所有的闹铃
	private static List<CommandRemind> remindList = new LinkedList<CommandRemind>();

	public CommandRemind(BasicServiceUnion union, CommandInfo commandInfo) {
		super(union, commandInfo, COMMAND_NAME_REMIND, "备忘录通知");

		LogManager.e("debug_zheng CommandRemind: " + commandInfo.toCommandString());
		mCommandInfo = commandInfo;
		mUnion = union;
		this.hardWare = (HardWare) mUnion.getBaseContext().getGlobalObject(KeyList.GKEY_HARDWARE);
		canNotDelay = (mCommandInfo.getArg(1) != null);
		LogManager.e("is canNotDelay: " + canNotDelay);
	}

	@Override
	public IVoiceCommand execute() {
		super.execute();

		mContent = mCommandInfo._question;
		// 记录闹铃命令
		remindList.add(this);
		// 保存按钮
		mianButtonHandler = IHardWare.buttonHandlers.get(IHardWare.BUTTON_LOGO);
		// 播放闹铃-闹铃开始后替换按键
		playContents();

		return null;
	}

	// 触发
	private void fire() {
		// 屏蔽按钮
		ButtonHandler remindButtonHandler = new ButtonHandler() {

			@Override
			public void onShortClick() {
				// 停止所有闹铃
				LogManager.e("onShortClick button cancle  alarm  canNotDelay=" + canNotDelay);
				isShortClick = true;
				quitAll();
			}

			@Override
			public void onLongClick() {

			}

			@Override
			public void onLongLongClick() {

			}

			@Override
			public void onClickInTouch() {
				LogManager.e("长按按钮延时闹钟提示 canNotDelay=" + canNotDelay);
				if (!canNotDelay && !isShortClick) {// 是否可以延时
					isShortClick = false;
					// 闹铃延时
					delay();
					quitOther();
				} else {
					quit(true);
				}
			}

			@Override
			public void onLongClickInTouch() {

			}

		};
		remindButtonHandler.prepare();
		IHardWare.buttonHandlers.put(IHardWare.BUTTON_LOGO, remindButtonHandler);
		// 添加闹铃开关识别
		IRecogniseSystem getRecogniseSystem11 = mUnion.getRecogniseSystem();
		if (getRecogniseSystem11 == null) {
			LogManager.e("getRecogniseSystem11 == null");
		} else {
			LogManager.e("register setOnResultListener");
			getRecogniseSystem11.setOnResultListener(new IOnResultListener() {

				@Override
				public void onStart() {
				}

				@Override
				public void onResult(String text) {
					if (text.contains("关") || text.contains("停") || text.contains("结束")) {
						LogManager.e(" 定时闹钟的设置 onResult" + text);
						quitAll();
					}
				}

				@Override
				public void onError(int errorCode) {

				}

				@Override
				public void onEnd() {

				}
			});
		}
	}

	// 恢复
	private void recover() {
		// 恢复tts
		mUnion.getTTSController().setListener(null);
		// 恢复识别
		mUnion.getRecogniseSystem().setOnResultListener(null);
		// 恢复按键
		hardWare.recover();
		// 恢复唤醒
		mUnion.getRecogniseSystem().startWakeup();
	}

	// 1分钟后自动延时
	private Runnable autoDelay = new Runnable() {

		@Override
		public void run() {
			if (!canNotDelay) {// 是否可以延时
				delay();
			} else {
				// 停止闹铃
				quit(true);
				// 恢复按键
				recover();
			}
		}

	};

	// 延时闹铃
	private void delay() {
		// 停止闹铃
		quit(false);
		// 恢复按键
		recover();
		// 延时5分钟
		Runnable delayRunnable = new Runnable() {

			@Override
			public void run() {
				LogManager.e(mContent);
				mUnion.getCommandEngine().handleText(mContent);
				mUnion.getTaskSchedu().removeStack(this);
			}

		};
		mUnion.getTaskSchedu().pushStackDelay(delayRunnable, 5 * 60 * 1000);
		// tts播报
		ITTSController mTTSController2 = mUnion.getTTSController();
		if (mTTSController2 == null) {
			LogManager.e("mTTSController2 == null");
		} else {
			mTTSController2.play("闹钟已延迟了5分钟");
		}
	}

	private boolean mIsPause = false;
	private int current;

	// 关闭闹铃
	private void quit(boolean real) {
		flag = false;
		isQuit = true;
		if (mPlayer != null) {
			mPlayer.stop();
			mPlayer.release();
			mPlayer = null;
			if (mIsPause) {
				mIsPause = false;
				VolumeUtil.autoIncreaseVolume(mContext);
			}
		}
		// 取消自动延时
		mUnion.getTaskSchedu().removeStack(autoDelay);
		//
		if (real) {
			remindList.remove(this);
		}
	}

	// 关闭所有闹铃
	private void quitAll() {
		for (int i = 0; i < remindList.size(); i++) {
			CommandRemind commandRemind = remindList.get(i);
			if (commandRemind != null) {
				commandRemind.quit(false);
			}
		}
		// 恢复现场
		recover();
		remindList.clear();
	}

	// 关闭其他闹铃
	private void quitOther() {
		List<CommandRemind> reminds = new ArrayList<CommandRemind>();
		for (int i = 0; i < remindList.size(); i++) {
			CommandRemind commandRemind = remindList.get(i);
			if (commandRemind != null) {
				if (!commandRemind.equals(this)) {
					reminds.add(commandRemind);
				}
				commandRemind.quit(false);
			}
		}
		// 恢复现场
		recover();
		remindList.removeAll(reminds);
	}

	// 播放内容
	private void playContents() {
		if (!TextUtils.isEmpty(mContent)) {

			Pattern p = Pattern.compile("(.{3,})?(提醒我|叫我|通知我|叫醒我|喊我|喊醒我)(.*)?了?啦?");
			Matcher m;
			String ttsContent;
			if ((m = p.matcher(mContent)) != null) {
				if (m.find()) {
					String realContent = m.group(3);
					LogManager.e(realContent);

					if (realContent != null && realContent.length() > 0) {
						if (realContent.endsWith("了")) {
							realContent = realContent.substring(0, realContent.length() - 1);
						}
						ttsContent = "该" + realContent + "了";
					} else {
						ttsContent = "";
					}

				} else {
					ttsContent = "";
				}

			} else {
				ttsContent = "";
			}

			StringBuffer buffer = new StringBuffer(ttsContent);
			buffer.append(",");
			buffer.append(ttsContent);
			buffer.append(",");
			buffer.append(ttsContent);
			LogManager.e("StringBuffer:" + buffer);
			ITTSController mTTSController1 = null;

			mTTSController1 = mUnion.getTTSController();

			if (mTTSController1 == null) {
				LogManager.e("mTTSController1 == null");
			} else {
				// 播放tts
				ITTSStateListener stateListener = new ITTSStateListener() {
					@Override
					public void onStart() {
						LogManager.e("hefeng onStart");
						// 屏蔽
						fire();
					}

					@Override
					public void onInit() {
						LogManager.e("hefeng  onInit");
					}

					@Override
					public void onError() {
						LogManager.e("hefeng onError");
						// playMusic();
					}

					@Override
					public void onEnd() {
						LogManager.e("hefeng onEnd");
						playMusic();
					}
				};
				mTTSController1.setListener(stateListener);
				mTTSController1.play(buffer.toString());
			}
		}
	}

	private boolean flag;

	private void playMusic() {
		if (isQuit) {
			return;
		}
		LogManager.e("playMusic");
		// 播放闹铃
		// 一分钟后自动延时
		mUnion.getTaskSchedu().pushStackDelay(autoDelay, 1 * 60 * 1000);
		// 播放音乐
		try {
			mPlayer = new MediaPlayer();
			mPlayer.setAudioStreamType(TTSSameple.CURRENT_STREAM_TYPE);
			String key = new BaseContext(mContext).getPrefString("KEY_RING_FOR_REMIND", "");
			File file = new File("/mnt/sdcard/VoiceAssistant/RingTone/" + key);
			LogManager.e("RingTong:" + key + ",file:" + file.exists());
			if (key != null && !key.equals("") && file.exists()) {
				mPlayer.setDataSource(file.getAbsolutePath());
			} else {
				AssetFileDescriptor assetFileDescriptor = mUnion.getBaseContext().getContext().getResources().openRawResourceFd(R.raw.remind_man);
				mPlayer.setDataSource(assetFileDescriptor.getFileDescriptor(), assetFileDescriptor.getStartOffset(), assetFileDescriptor.getLength());

				assetFileDescriptor.close();
			}
			mPlayer.setLooping(true);
			mPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
			// mPlayer.prepare();
			mPlayer.prepareAsync();
			mPlayer.setOnPreparedListener(new OnPreparedListener() {

				@Override
				public void onPrepared(MediaPlayer arg0) {
					mPlayer.start();
					if (getUnion().getMediaInterface().isPlaying()) {
						VolumeUtil.autoDecreaseVolume(mContext);
						mIsPause = true;
					} else {
						new Thread(new Runnable() {
							public void run() {
								flag = true;
								synchronized (this) {
									while (flag) {
										if (getUnion().getMediaInterface().isPlaying()) {
											VolumeUtil.autoDecreaseVolume(mContext);
											mIsPause = true;
											break;
										}
									}
								}
							}
						}).start();
					}
				}
			});

		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
