package com.voice.assistant.hardware.deviceCase;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaRecorder;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import com.iii360.base.common.utl.KeyList;
import com.iii360.base.common.utl.LogManager;
import com.iii360.base.inf.BasicServiceUnion;
import com.iii360.base.inf.ITTSController;
import com.iii360.sup.common.utl.AudioFileUtil;
import com.voice.assistant.hardware.ButtonHandler;
import com.voice.assistant.hardware.HardWare;
import com.voice.assistant.hardware.IHardWare;
import com.voice.assistant.hardware.NetLightControl;

public class DeviceCase extends Thread {

	private BasicServiceUnion mBasicServiceUnion;

	public DeviceCase(BasicServiceUnion union) {
		mBasicServiceUnion = union;
		init();
	}

	private Map<String, ButtonHandler> caseButtonMap = null;

	private IHardWare hardWare;

	private ITTSController ttsController = null;

	private int index = -1;

	private List<Runnable> steps = new ArrayList<Runnable>();

	private ButtonHandler emptyButtonHandler = new ButtonHandler() {

		@Override
		public void onShortClick() {
			// TODO Auto-generated method stub

		}

		@Override
		public void onLongClick() {
			// TODO Auto-generated method stub

		}

		@Override
		public void onLongLongClick() {
			// TODO Auto-generated method stub

		}

		@Override
		public void onClickInTouch() {
			// TODO Auto-generated method stub

		}

		@Override
		public void onLongClickInTouch() {
			// TODO Auto-generated method stub

		}

	};

	private void init() {
		hardWare = (IHardWare) mBasicServiceUnion.getBaseContext().getGlobalObject(KeyList.GKEY_HARDWARE);
		ttsController = mBasicServiceUnion.getTTSController();
		// 进入设备测试阶段
		mBasicServiceUnion.getBaseContext().setGlobalBoolean(KeyList.GKEY_DEVICE_CASE, true);
		// 过程
		index = -1;
		// 屏蔽现场
		steps.add(new Runnable() {

			@Override
			public void run() {
				// 暂停音乐
				if (mBasicServiceUnion.getMediaInterface().isPlaying()) {
					mBasicServiceUnion.getMediaInterface().pause();
				}
				// 屏蔽按钮
				emptyButtonHandler.prepare();
				IHardWare.buttonHandlers.put(IHardWare.BUTTON_VOLUME_INCREASE, emptyButtonHandler);
				emptyButtonHandler.prepare();
				IHardWare.buttonHandlers.put(IHardWare.BUTTON_VOLUME_DECREASE, emptyButtonHandler);
				emptyButtonHandler.prepare();
				IHardWare.buttonHandlers.put(IHardWare.BUTTON_LOGO, emptyButtonHandler);
				// 屏蔽声纹
				mBasicServiceUnion.getBaseContext().getContext().sendBroadcast(new Intent("wifiserver.action.stop.soundwave"));
				// 屏蔽唤醒
				mBasicServiceUnion.getRecogniseSystem().stopCaptureVoice();
				// 下一步
				nextStep();
			}
		});
		steps.add(hornCase);
		steps.add(headsetCase);
		steps.add(voiceUpCase);
		steps.add(logoCase);
		steps.add(voiceDownCase);
		steps.add(battery);
		steps.add(wakelight);
		steps.add(logolight);
		steps.add(netlight);
		steps.add(wifi);
		steps.add(mic);
		// 播报结果
		steps.add(new Runnable() {

			@Override
			public void run() {
				List<String> list = new ArrayList();
				if (!KeyList.HORN_CASE) {
					list.add("喇叭");
				}
				if (!KeyList.HEADSET_CASE) {
					list.add("耳机孔");
				}
				if (!KeyList.VOICEUP_BUTTON_CASE) {
					list.add("红心键");
				}
				if (!KeyList.LOGO_BUTTON_CASE) {
					list.add("楼狗键");
				}
				if (!KeyList.VOICEDOWN_BUTTON_CASE) {
					list.add("垃圾桶键");
				}
				if (!KeyList.BATTERY_CASE) {
					list.add("电量");
				}
				if (!KeyList.WAKELIGHT_CASE) {
					list.add("唤醒灯");
				}
				if (!KeyList.LOGOLIGHT_CASE) {
					list.add("楼狗灯");
				}
				if (!KeyList.NETLIGHT_CASE) {
					list.add("网络灯");
				}
				if (!KeyList.WIFI_CASE) {
					list.add("why five模块");
				}
				if (!KeyList.MIC_CASE) {
					list.add("麦克疯");
				}
				//
				if (!list.isEmpty()) {
					ttsController.syncPlay("硬件测试结束，以下模块不正确");
					for (String str : list) {
						try {
							Thread.sleep(100);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						ttsController.syncPlay(str);
					}
				} else {
					ttsController.syncPlay("硬件测试结束，所有设备正常工作");
				}
				nextStep();
			}
		});
		// 恢复现场
		steps.add(new Runnable() {

			@Override
			public void run() {
				// 完成测试
				mBasicServiceUnion.getBaseContext().setGlobalBoolean(KeyList.GKEY_DEVICE_CASE, false);
				// 恢复按钮
				HardWare hardWare = (HardWare) mBasicServiceUnion.getBaseContext().getGlobalObject(KeyList.GKEY_HARDWARE);
				hardWare.recover();
				// 恢复灯与唤醒
				NetLightControl netLightControl = (NetLightControl) mBasicServiceUnion.getBaseContext().getGlobalObject(KeyList.GKEY_NET_LIGHT_CONTROL);
				netLightControl.adjust();
				// 恢复声纹
				mBasicServiceUnion.getBaseContext().getContext().sendBroadcast(new Intent("wifiserver.action.restore.soundwave"));
				nextStep();
			}
		});
	}

	@Override
	public void run() {
		// 测试耳麦
		new Thread(new Runnable() {

			@Override
			public void run() {
				nextStep();
			}
		}).start();
	}

	public void nextStep() {
		index++;
		if (index < steps.size()) {
			steps.get(index).run();
		} else {
			index = steps.size() - 1;
		}
	}

	// case
	Runnable hornCase = new Runnable() {
		public void run() {
			ttsController.syncPlay("喇叭检测成功");
			KeyList.HORN_CASE = true;
			nextStep();
		}
	};

	Runnable headsetCase = new Runnable() {
		public void run() {
			ttsController.syncPlay("测试耳机孔，请插入耳机");
			final Thread thread = new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						ttsController.syncPlay("倒属");
						Thread.sleep(100);
						ttsController.syncPlay("5");
						Thread.sleep(800);
						ttsController.syncPlay("4");
						Thread.sleep(800);
						ttsController.syncPlay("3");
						Thread.sleep(800);
						ttsController.syncPlay("2");
						Thread.sleep(800);
						ttsController.syncPlay("1");
						ttsController.syncPlay("耳机孔检测成功");
						KeyList.HEADSET_CASE = true;
						nextStep();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			});
			final BroadcastReceiver headsetBroadcastReceiver = new BroadcastReceiver() {

				@Override
				public void onReceive(Context context, Intent intent) {
					if (intent.getIntExtra("state", 0) == 1) {
						// timer.cancel();
						thread.interrupt();
						// 下一步
						new Thread(new Runnable() {

							@Override
							public void run() {
								// 成功
								ttsController.syncPlay("耳机孔检测成功");
								KeyList.HEADSET_CASE = true;

								nextStep();
							}
						}).start();

					}
				}

			};
			mBasicServiceUnion.getBaseContext().getContext().registerReceiver(headsetBroadcastReceiver, new IntentFilter(Intent.ACTION_HEADSET_PLUG));
			thread.start();
		}
	};

	Runnable voiceUpCase = new Runnable() {

		@Override
		public void run() {
			ttsController.syncPlay("请按红心键");
			final Timer timer = new Timer(true);
			KeyList.VOICEUP_BUTTON_CASE = false;
			IHardWare.buttonHandlers.put(IHardWare.BUTTON_VOLUME_INCREASE, new ButtonHandler() {

				@Override
				public void onShortClick() {
					if (!KeyList.VOICEUP_BUTTON_CASE) {
						timer.cancel();
						// 成功
						ttsController.syncPlay("红心键检测成功");
						KeyList.VOICEUP_BUTTON_CASE = true;
						emptyButtonHandler.prepare();
						IHardWare.buttonHandlers.put(IHardWare.BUTTON_VOLUME_INCREASE, emptyButtonHandler);
						nextStep();
					}
				}

				@Override
				public void onLongClick() {
					// TODO Auto-generated method stub

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
					// TODO Auto-generated method stub

				}

			});
			// 倒计时
			timer.schedule(new TimerTask() {

				@Override
				public void run() {
					// 失败
					ttsController.syncPlay("红心键检测失败");
					KeyList.VOICEUP_BUTTON_CASE = false;
					emptyButtonHandler.prepare();
					IHardWare.buttonHandlers.put(IHardWare.BUTTON_VOLUME_INCREASE, emptyButtonHandler);
					nextStep();
				}

			}, 5000);
		}

	};

	Runnable logoCase = new Runnable() {

		@Override
		public void run() {
			ttsController.syncPlay("请按楼狗键");

			final Timer timer = new Timer(true);
			KeyList.LOGO_BUTTON_CASE = false;
			IHardWare.buttonHandlers.put(IHardWare.BUTTON_LOGO, new ButtonHandler() {

				@Override
				public void onShortClick() {
					if (!KeyList.LOGO_BUTTON_CASE) {
						timer.cancel();
						// 成功
						ttsController.syncPlay("楼狗键检测成功");
						KeyList.LOGO_BUTTON_CASE = true;
						emptyButtonHandler.prepare();
						IHardWare.buttonHandlers.put(IHardWare.BUTTON_LOGO, emptyButtonHandler);
						nextStep();
					}
				}

				@Override
				public void onLongClick() {
					// TODO Auto-generated method stub

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
					// TODO Auto-generated method stub

				}

			});
			// 倒计时
			timer.schedule(new TimerTask() {

				@Override
				public void run() {
					// 失败
					ttsController.syncPlay("楼狗键检测失败");
					KeyList.LOGO_BUTTON_CASE = false;
					emptyButtonHandler.prepare();
					IHardWare.buttonHandlers.put(IHardWare.BUTTON_LOGO, emptyButtonHandler);
					nextStep();
				}

			}, 5000);
		}

	};

	Runnable voiceDownCase = new Runnable() {

		@Override
		public void run() {
			ttsController.syncPlay("请按垃圾桶键");

			final Timer timer = new Timer(true);
			KeyList.VOICEDOWN_BUTTON_CASE = false;
			IHardWare.buttonHandlers.put(IHardWare.BUTTON_VOLUME_DECREASE, new ButtonHandler() {

				@Override
				public void onShortClick() {
					if (!KeyList.VOICEDOWN_BUTTON_CASE) {
						timer.cancel();
						// 成功
						ttsController.syncPlay("垃圾桶键检测成功");
						KeyList.VOICEDOWN_BUTTON_CASE = true;
						emptyButtonHandler.prepare();
						IHardWare.buttonHandlers.put(IHardWare.BUTTON_VOLUME_DECREASE, emptyButtonHandler);
						nextStep();
					}
				}

				@Override
				public void onLongClick() {
					// TODO Auto-generated method stub

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
					// TODO Auto-generated method stub

				}

			});
			// 倒计时
			timer.schedule(new TimerTask() {

				@Override
				public void run() {
					// 失败
					ttsController.syncPlay("垃圾桶键检测失败");
					KeyList.VOICEDOWN_BUTTON_CASE = false;
					emptyButtonHandler.prepare();
					IHardWare.buttonHandlers.put(IHardWare.BUTTON_VOLUME_DECREASE, emptyButtonHandler);
					nextStep();
				}

			}, 5000);
		}

	};

	Runnable battery = new Runnable() {

		@Override
		public void run() {
			ttsController.syncPlay("测试电池");

			BroadcastReceiver batteryBroadcastReceiver = new BroadcastReceiver() {

				@Override
				public void onReceive(Context context, Intent intent) {
					// 获取当前电量
					int level = intent.getIntExtra("level", 0);
					// 电量的总刻度
					int scale = intent.getIntExtra("scale", 100);
					// 把它转成百分比
					ttsController.syncPlay("电量为" + ((level * 100) / scale) + "%");

					KeyList.BATTERY_CASE = true;
					mBasicServiceUnion.getBaseContext().getContext().unregisterReceiver(this);
					// 下一步
					new Thread(new Runnable() {

						@Override
						public void run() {
							nextStep();
						}
					}).start();
				}

			};
			mBasicServiceUnion.getBaseContext().getContext().registerReceiver(batteryBroadcastReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
		}

	};

	Runnable wakelight = new Runnable() {

		@Override
		public void run() {
			try {
				ttsController.syncPlay("测试顶部蓝灯");
				// 5次
				hardWare.controlLight(IHardWare.LIGHT_WAKE_UP, IHardWare.LIGHT_ON);// 1
				Thread.sleep(100);
				hardWare.controlLight(IHardWare.LIGHT_WAKE_UP, IHardWare.LIGHT_CLOSE);
				Thread.sleep(100);
				hardWare.controlLight(IHardWare.LIGHT_WAKE_UP, IHardWare.LIGHT_ON);// 2
				Thread.sleep(100);
				hardWare.controlLight(IHardWare.LIGHT_WAKE_UP, IHardWare.LIGHT_CLOSE);
				Thread.sleep(100);
				hardWare.controlLight(IHardWare.LIGHT_WAKE_UP, IHardWare.LIGHT_ON);// 3
				Thread.sleep(100);
				hardWare.controlLight(IHardWare.LIGHT_WAKE_UP, IHardWare.LIGHT_CLOSE);
				Thread.sleep(100);
				hardWare.controlLight(IHardWare.LIGHT_WAKE_UP, IHardWare.LIGHT_ON);// 4
				Thread.sleep(100);
				hardWare.controlLight(IHardWare.LIGHT_WAKE_UP, IHardWare.LIGHT_CLOSE);
				Thread.sleep(100);
				hardWare.controlLight(IHardWare.LIGHT_WAKE_UP, IHardWare.LIGHT_ON);// 5
				Thread.sleep(100);
				hardWare.controlLight(IHardWare.LIGHT_WAKE_UP, IHardWare.LIGHT_CLOSE);
				Thread.sleep(100);
				KeyList.WAKELIGHT_CASE = true;
				nextStep();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

	};

	Runnable logolight = new Runnable() {

		@Override
		public void run() {
			try {
				ttsController.syncPlay("测试顶部绿灯");
				// 5次
				hardWare.controlLight(IHardWare.LIGHT_LOGO, IHardWare.LIGHT_ON);// 1
				Thread.sleep(100);
				hardWare.controlLight(IHardWare.LIGHT_LOGO, IHardWare.LIGHT_CLOSE);
				Thread.sleep(100);
				hardWare.controlLight(IHardWare.LIGHT_LOGO, IHardWare.LIGHT_ON);// 2
				Thread.sleep(100);
				hardWare.controlLight(IHardWare.LIGHT_LOGO, IHardWare.LIGHT_CLOSE);
				Thread.sleep(100);
				hardWare.controlLight(IHardWare.LIGHT_LOGO, IHardWare.LIGHT_ON);// 3
				Thread.sleep(100);
				hardWare.controlLight(IHardWare.LIGHT_LOGO, IHardWare.LIGHT_CLOSE);
				Thread.sleep(100);
				hardWare.controlLight(IHardWare.LIGHT_LOGO, IHardWare.LIGHT_ON);// 4
				Thread.sleep(100);
				hardWare.controlLight(IHardWare.LIGHT_LOGO, IHardWare.LIGHT_CLOSE);
				Thread.sleep(100);
				hardWare.controlLight(IHardWare.LIGHT_LOGO, IHardWare.LIGHT_ON);// 5
				Thread.sleep(100);
				hardWare.controlLight(IHardWare.LIGHT_LOGO, IHardWare.LIGHT_CLOSE);
				Thread.sleep(100);
				KeyList.LOGOLIGHT_CASE = true;
				nextStep();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

	};

	Runnable netlight = new Runnable() {

		@Override
		public void run() {
			try {
				ttsController.syncPlay("测试底部绿灯");
				// 5次
				hardWare.controlLight(IHardWare.LIGHT_NET, IHardWare.LIGHT_ON);// 1
				Thread.sleep(100);
				hardWare.controlLight(IHardWare.LIGHT_NET, IHardWare.LIGHT_CLOSE);
				Thread.sleep(100);
				hardWare.controlLight(IHardWare.LIGHT_NET, IHardWare.LIGHT_ON);// 2
				Thread.sleep(100);
				hardWare.controlLight(IHardWare.LIGHT_NET, IHardWare.LIGHT_CLOSE);
				Thread.sleep(100);
				hardWare.controlLight(IHardWare.LIGHT_NET, IHardWare.LIGHT_ON);// 3
				Thread.sleep(100);
				hardWare.controlLight(IHardWare.LIGHT_NET, IHardWare.LIGHT_CLOSE);
				Thread.sleep(100);
				hardWare.controlLight(IHardWare.LIGHT_NET, IHardWare.LIGHT_ON);// 4
				Thread.sleep(100);
				hardWare.controlLight(IHardWare.LIGHT_NET, IHardWare.LIGHT_CLOSE);
				Thread.sleep(100);
				hardWare.controlLight(IHardWare.LIGHT_NET, IHardWare.LIGHT_ON);// 5
				Thread.sleep(100);
				hardWare.controlLight(IHardWare.LIGHT_NET, IHardWare.LIGHT_CLOSE);
				Thread.sleep(100);
				KeyList.NETLIGHT_CASE = true;
				nextStep();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

	};

	Runnable wifi = new Runnable() {

		@Override
		public void run() {
			ttsController.syncPlay("测试why five模块");

			try {
				Context context = mBasicServiceUnion.getBaseContext().getContext();
				WifiManager wifi_service = (WifiManager) context.getSystemService(context.WIFI_SERVICE);
				WifiInfo wifiInfo = wifi_service.getConnectionInfo();
				int rssi = wifiInfo.getRssi();
				if (rssi < -100) {
					ttsController.syncPlay("网络没有连接");
				} else {
					String s = String.valueOf(rssi).replace('-', '负');
					ttsController.syncPlay("网络信号强度为" + s + "DBM");
				}
				KeyList.WIFI_CASE = true;
			} catch (Exception e) {
				ttsController.syncPlay("why five模块检测失败");
				KeyList.WIFI_CASE = false;
			} finally {
				nextStep();
			}
		}

	};

	Runnable mic = new Runnable() {
		private boolean hasResult = false;
		private MediaPlayer mPlayer = null;

		@Override
		public void run() {
			// 延时
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			//
			ttsController.syncPlay("开始录音，请随便说一句话");

			final String file = "/mnt/sdcard/equirCase.wav";
			File f = new File(file);
			f.delete();
			try {
				// 开启录音
				micRecord(file);
				Thread.sleep(1000);
				if (!f.exists()) {
					errorMic(null);
					return;
				}
				// 播放录音
				ttsController.syncPlay("播放录音");
				hasResult = false;
				mPlayer = new MediaPlayer();
				mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
				mPlayer.setDataSource(file);
				mPlayer.setLooping(false);
				mPlayer.setOnCompletionListener(new OnCompletionListener() {

					@Override
					public void onCompletion(final MediaPlayer mPlayer) {
						hasResult = true;
						try {
							LogManager.d("success");
							if (mPlayer != null) {
								if (mPlayer.isPlaying()) {
									mPlayer.stop();
								}
								mPlayer.release();
							}
							// new File(file).delete();
							KeyList.MIC_CASE = true;

							nextStep();
						} catch (Exception e) {
							LogManager.printStackTrace(e);
							errorMic(mPlayer);
						}
					}
				});
				mPlayer.setOnErrorListener(new OnErrorListener() {

					@Override
					public boolean onError(MediaPlayer mPlayer, int arg1, int arg2) {
						errorMic(mPlayer);
						return true;
					}
				});
				//
				mPlayer.prepare();
				mPlayer.start();
				// 过了15秒仍没有答复
				Timer timer = new Timer();
				timer.schedule(new TimerTask() {

					@Override
					public void run() {
						// 失败
						if (!hasResult) {
							errorMic(mPlayer);
						}
					}

				}, 15000);
			} catch (InterruptedException e) {
				LogManager.printStackTrace(e);
			} catch (Exception e) {
				LogManager.printStackTrace(e);
				errorMic(mPlayer);
			}
		}

		private void micRecord1(String file) throws IllegalStateException, IOException, InterruptedException {
			// String file = "/mnt/sdcard/equirCase.wav";
			MediaRecorder mRecorder = new MediaRecorder();
			mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
			mRecorder.setAudioChannels(1);
			mRecorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
			mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
			mRecorder.setAudioSamplingRate(16000);
			mRecorder.setAudioEncodingBitRate(16000 * 16 * 1);
			mRecorder.setOutputFile(file);
			mRecorder.prepare();
			Thread.sleep(1000);
			mRecorder.start();
			Thread.sleep(5000);
			mRecorder.stop();
			mRecorder.release();
			mRecorder = null;
		}

		private void micRecord(String file) throws Exception {
			// String file = "/mnt/sdcard/equirCase.wav";
			int buffSize = (int) (16000 * 2 // 2 seconds in the buffer
			* 2);

			AudioRecord arec = new AudioRecord(MediaRecorder.AudioSource.MIC, 16000, // 11025,
					AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, 
					buffSize);
			Queue<byte[]> mQueue = new LinkedList<byte[]>();
			arec.startRecording();
			double kSamplingFrequency = 8000; 
			double kDuration = 0.18; 
			// Number of samples per duration
			int kSamplesPerDuration = (int) (kSamplingFrequency * kDuration);
			int biteSize = kSamplesPerDuration;
			byte[] data_16bit = new byte[biteSize * 2];

			long t = System.currentTimeMillis();

			// 录音5秒
			int bufferLength = 0;
			while (System.currentTimeMillis() - t < 5000) {
				int numBytesRead = arec.read(data_16bit, 0, data_16bit.length);
				byte[] data = new byte[numBytesRead];
				System.arraycopy(data_16bit, 0, data, 0, numBytesRead);
				mQueue.add(data);
				bufferLength += numBytesRead;
			}
			arec.stop();
			arec.release();
			// 保存文件
			AudioFileUtil.generateWAVFile(file, mQueue, bufferLength);
		}

		private void errorMic(MediaPlayer mPlayer) {
			hasResult = true;
			LogManager.d("failure");
			if (mPlayer != null) {
				if (mPlayer.isPlaying()) {
					mPlayer.stop();
				}
				mPlayer.release();
			}
			ttsController.syncPlay("麦克风检测失败");
			KeyList.MIC_CASE = false;

			nextStep();
		}
	};

}
