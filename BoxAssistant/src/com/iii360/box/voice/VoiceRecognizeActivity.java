package com.iii360.box.voice;

import java.util.List;
import java.util.Random;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.ui.RecognizerDialog;
import com.iii.wifi.dao.info.WifiMusicInfo;
import com.iii.wifi.dao.info.WifiMusicInfos;
import com.iii.wifi.dao.newmanager.WifiCRUDForMusic;
import com.iii.wifi.dao.newmanager.WifiCRUDForMusic.ResultForMusicListener;
import com.iii360.box.MyApplication;
import com.iii360.box.PlayingMusicActivity;
import com.iii360.box.R;
import com.iii360.box.base.BaseActivity;
import com.iii360.box.util.BoxManagerUtils;
import com.iii360.box.util.HttpUtils;
import com.iii360.box.util.JsonParser;
import com.iii360.box.util.LogUtil;
import com.iii360.box.util.ToastUtils;
import com.iii360.box.util.WifiCRUDUtil;
import com.iii360.box.view.CircleWaveView;
import com.iii360.box.view.MyProgressDialog;
import com.iii360.box.view.NewViewHead;
import com.iii360.sup.common.utl.LogManager;

public class VoiceRecognizeActivity extends BaseActivity {
	private SpeechRecognizer speechRecognizer;
	RecognizerDialog iatDialog;
	private ImageView volumnIv;
	private MyProgressDialog myProgressDialog;
	private TextView Keytv;
	private TextView recommendTv;
	private TextView clickTv;
	private RelativeLayout clickArea;
	private boolean needJump;
	private LinearLayout recommendArea;
	private static final String MUSIC_STATE_STOP = "0";
	private static final String MUSIC_STATE_PLAYING = "1";
	private static final String MUSIC_STATE_PAUSE = "2";
	private static final String MUSIC_STATE_DLAN = "3";
	private int minRadius;
	private int maxRadius;
	private OnClickListener speechListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			start();
		}
	};
	private OnLongClickListener longClickListener = new OnLongClickListener() {

		@Override
		public boolean onLongClick(View v) {
			start();
			return false;
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_voice_recoginze);
		getIntentData();
		setupView();
		initSpeech();
	}

	private void getIntentData() {
		needJump = getIntent().getBooleanExtra("NEED_JUMP", false);
	}

	private String[] recommend = { "大海", "邓紫棋的泡沫", "刘德华", "Yesterday Once More", "来首钢琴曲", "讲个故事" };
	private int[] res = { R.drawable.ba_main_voice01, R.drawable.ba_main_voice02, R.drawable.ba_main_voice03, R.drawable.ba_main_voice04, R.drawable.ba_main_voice05, R.drawable.ba_main_voice06,
			R.drawable.ba_main_voice07, R.drawable.ba_main_voice08, R.drawable.ba_main_voice09, R.drawable.ba_main_voice10, R.drawable.ba_main_voice11 };
	private WifiCRUDForMusic mWifiCRUDForMusic;
	private CircleWaveView circleWaveView;

	private void setupView() {
		NewViewHead.showLeft(context, "语音点歌");
		volumnIv = (ImageView) findViewById(R.id.voice_volumn_iv);
		recommendTv = (TextView) findViewById(R.id.voice_recommend_tv);
		clickTv = (TextView) findViewById(R.id.voice_click_tip);
		Keytv = (TextView) findViewById(R.id.voice_tip_tv);
		recommendArea = (LinearLayout) findViewById(R.id.voice_recommend);
		recommendArea.setVisibility(View.INVISIBLE);
		circleWaveView = (CircleWaveView) findViewById(R.id.circleWaveView);
		clickArea = (RelativeLayout) findViewById(R.id.voice_click_area);
		volumnIv.setImageResource(res[0]);
		int w = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
		int h = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
		volumnIv.measure(w, h);
		int height = volumnIv.getMeasuredHeight();
		int width = volumnIv.getMeasuredWidth();
		minRadius = (height < width ? height : width) / 2;
		maxRadius = minRadius + (int) (40 * BoxManagerUtils.getScreenDensity(context));
		recommendTv.setText(recommend[new Random().nextInt(recommend.length)]);
		mWifiCRUDForMusic = new WifiCRUDForMusic(getBoxIp(), getBoxTcpPort());
	}

	private void initSpeech() {
		speechRecognizer = SpeechRecognizer.createRecognizer(this, mInitListener);
		// iatDialog = new RecognizerDialog(this, mInitListener);
	}

	private InitListener mInitListener = new InitListener() {

		@Override
		public void onInit(int code) {
			LogUtil.d("SpeechRecognizer init() code = " + code);
			if (code != ErrorCode.SUCCESS) {
				ToastUtils.show(context, "识别引擎初始化失败");
				// finishActivity(false, "识别引擎初始化失败,请重试");
			} else {
				start();
			}
		}
	};

	public void start() {
		setParam();
		int ret = speechRecognizer.startListening(recognizerListener);
		Keytv.setText("\"告诉我想听什么\"");
		recommendArea.setVisibility(View.VISIBLE);
		if (ret != ErrorCode.SUCCESS) {
			toastOrChangeView(false, false, "听写失败,请重试");
		} else {
			// ToastUtils.show(context, "请开始说话");
			clickTv.setVisibility(View.VISIBLE);
			circleWaveView.setVisibility(View.VISIBLE);
			clickTv.setText("请开始说话");
			clickArea.setOnClickListener(null);
			clickArea.setOnLongClickListener(null);
			circleWaveView.setMinRadius(minRadius);
			circleWaveView.setMaxRadius(maxRadius);
			circleWaveView.setFloatRadius(minRadius);
			circleWaveView.setCenter(BoxManagerUtils.getScreenWidthPx(context) / 2, getY() + minRadius);
			circleWaveView.postInvalidate();
		}
	}

	private int getY() {
		int margin = (int) ((50 + 20 + 40) * BoxManagerUtils.getScreenDensity(context));
		int w = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
		int h = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
		Keytv.measure(w, h);
		int height = Keytv.getMeasuredHeight();
		margin += height;
		recommendArea.measure(w, h);
		height = recommendArea.getMeasuredHeight();
		return height + margin;
	}

	/*****
	 * 判断字符串中有没有英文
	 * 
	 * @param text
	 * @return
	 */
	private boolean containEng(String text) {
		return text.matches("^.*[a-zA-Z]+.*$");
	}

	private RecognizerListener recognizerListener = new RecognizerListener() {

		@Override
		public void onBeginOfSpeech() {
			// ToastUtils.show(context, "开始说话");
		}

		@Override
		public void onError(SpeechError error) {
			volumnIv.setImageResource(res[0]);
			// ToastUtils.show(context, error.getPlainDescription(true));
			toastOrChangeView(false, false, error.getErrorDescription());

		}

		@Override
		public void onEndOfSpeech() {
			// ToastUtils.show(context, "结束说话");
		}

		private StringBuilder sb = new StringBuilder();

		@Override
		public void onResult(RecognizerResult results, boolean isLast) {
			String text = JsonParser.parseIatResult(results.getResultString());
			sb.append(text);
			if (isLast) {
				circleWaveView.setVisibility(View.GONE);
				clickTv.setVisibility(View.GONE);
				recommendArea.setVisibility(View.INVISIBLE);
				volumnIv.setImageResource(res[0]);
				String finalText = sb.toString();
				String reg = "^.+[？，。！!,.?]$";
				if (finalText.matches(reg)) {
					finalText = finalText.substring(0, finalText.length() - 1).trim();
				}
				LogUtil.d("voice--搜索关键字：" + finalText);
				showDialog(getString(R.string.ba_update_date));
				Keytv.setText("\"" + finalText + "\"");
				if (containEng(finalText)) {
					search(finalText);
				} else {
					searchNew(finalText);
				}
				sb.delete(0, sb.length());
			}
		}

		@Override
		public void onVolumeChanged(int volume) {
			volume = volume / 2;
			if (volume >= res.length) {
				volumnIv.setImageResource(res[res.length - 1]);
			} else
				volumnIv.setImageResource(res[volume % res.length]);
			// ToastUtils.show(context, "当前正在说话，音量大小：" + volume);
		}

		@Override
		public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {
		}
	};

	/****
	 * 播放或下一首
	 */
	private void playOrNext() {
		mWifiCRUDForMusic.playState(new ResultForMusicListener() {
			public void onResult(String errorCode, List<WifiMusicInfo> infos) {
				if (!WifiCRUDUtil.isSuccessAll(errorCode)) {
					dismissDialog();
					toastOrChangeView(true, false, "操作失败,请检音箱和网络是否正常");
				} else {
					if (infos == null || infos.size() == 0) {
						dismissDialog();
						toastOrChangeView(true, false, "操作失败,请检音箱和网络是否正常");
						return;
					}
					String state = infos.get(0).getPlayStatus();
					if (MUSIC_STATE_PAUSE.equals(state) || MUSIC_STATE_STOP.equals(state)) {
						LogUtil.d("voice--play");
						playOrPause();
					} else {
						// TODO 有问题，如果是dlna怎么处理
						playNext();
					}
				}
			}
		});
	}

	/*****
	 * 其它控制
	 * 
	 * @param control
	 */
	private void control(String control) {
		LogUtil.d("voice--control=" + control);
		if ("换".equals(control) || "下".equals(control)) {
			playNext();
		} else if ("喜欢".equals(control)) {
			LogUtil.d("voice--goodMusic");
			mWifiCRUDForMusic.goodMusic(new ResultForMusicListener() {
				public void onResult(String errorCode, List<WifiMusicInfo> infos) {
					handlerResult(errorCode);
				}
			});
		} else if ("讨厌".equals(control)) {
			LogUtil.d("voice--badMusic");
			mWifiCRUDForMusic.badMusic(new ResultForMusicListener() {
				public void onResult(String errorCode, List<WifiMusicInfo> infos) {
					handlerResult(errorCode);
				}
			});
		} else if ("开始".equals(control)) {
			startPlay();
		} else if ("停止".equals(control) || "关闭".equals(control)) {
			stopPlay();
		} else if ("上".equals(control)) {
			LogUtil.d("voice--playPre");
			mWifiCRUDForMusic.playPre(new ResultForMusicListener() {
				public void onResult(String errorCode, List<WifiMusicInfo> infos) {
					handlerResult(errorCode);
				}
			});
		} else {
			toastOrChangeView(true, false, "未找到歌曲");
		}
	}

	private void playNext() {
		LogUtil.d("voice--playNext");
		if (handler != null)
			handler.post(new Runnable() {

				@Override
				public void run() {
					mWifiCRUDForMusic.playNext(new ResultForMusicListener() {
						public void onResult(String errorCode, List<WifiMusicInfo> infos) {
							handlerResult(errorCode);
						}
					});
				}
			});
	}

	private void startPlay() {
		/****
		 * 先获取状态，若正在播放不向音箱发送开始命令
		 */
		mWifiCRUDForMusic.playState(new ResultForMusicListener() {
			public void onResult(String errorCode, List<WifiMusicInfo> infos) {
				if (!WifiCRUDUtil.isSuccessAll(errorCode) && infos != null && !infos.isEmpty()) {
					dismissDialog();
					toastOrChangeView(true, false, "操作失败,请检音箱和网络是否正常");
				} else {
					String state = infos.get(0).getPlayStatus();
					LogUtil.d("startPlay,now=" + state);
					if (MUSIC_STATE_PLAYING.equals(state) || MUSIC_STATE_DLAN.equals(state)) {
						dismissDialog();
						toastOrChangeView(true, true, "");
					} else {
						LogUtil.d("voice--play");
						playOrPause();
					}
				}
			}
		});
	}

	private void stopPlay() {
		/****
		 * 先获取状态，若停止或者是暂停不向音箱发送开始命令
		 */
		mWifiCRUDForMusic.playState(new ResultForMusicListener() {
			public void onResult(String errorCode, List<WifiMusicInfo> infos) {
				if (!WifiCRUDUtil.isSuccessAll(errorCode) && infos != null && !infos.isEmpty()) {
					dismissDialog();
					toastOrChangeView(true, false, "操作失败,请检音箱和网络是否正常");
				} else {
					String state = infos.get(0).getPlayStatus();
					LogUtil.d("stopPlay,now=" + state);
					if (MUSIC_STATE_STOP.equals(state) || MUSIC_STATE_PAUSE.equals(state)) {
						dismissDialog();
						toastOrChangeView(true, true, "");
					} else {
						LogUtil.d("voice--pause");
						playOrPause();
					}
				}
			}
		});
	}

	private void handlerResult(String errorCode) {
		dismissDialog();
		// TODO Auto-generated method stub
		if (!WifiCRUDUtil.isSuccessAll(errorCode)) {
			// ToastUtils.show(context,
			// R.string.ba_config_box_info_error_toast);
			LogManager.e("发送网络播放资源失败");
			toastOrChangeView(true, false, "操作失败,请检音箱和网络是否正常");
		} else {
			toastOrChangeView(true, true, "");
		}
	}

	private void searchNew(final String text) {
		LogUtil.d("voice--音箱命令识别接口==" + text);
		final String sn = MyApplication.getSerialNums().get(BoxManagerUtils.getBoxIP(context));
		new Thread() {
			public void run() {
				try {
					WifiMusicInfos infos;
					try {
						infos = HttpUtils.searchLikeBox(text, sn);
					} catch (Exception e) {
						String msg = e.getMessage().trim();
						if ("CommandPlayMedia".equals(msg)) {
							if (handler != null)
								handler.post(new Runnable() {
									public void run() {
										playOrNext();
									}
								});
							return;
						} else if (msg.contains("CommandMediaControl")) {
							final String control = msg.split(":")[1];

							if (handler != null)
								handler.post(new Runnable() {
									public void run() {
										control(control);
									}
								});
							return;
						} else {
							throw e;
						}
					}
					if (infos == null) {
						dismissDialog();
						// ToastUtils.show(context, "未找到歌曲");
						toastOrChangeView(true, false, "未找到歌曲");
						return;
					}
					sendToBox(infos);
				} catch (Exception e) {
					LogUtil.e("voice--searchNew" + e);
					dismissDialog();
					e.printStackTrace();
					// ToastUtils.show(getApplicationContext(), "获取数据失败");
					toastOrChangeView(true, false, "获取数据失败");
				}
			};
		}.start();
	}

	protected void search(String key) {
		LogUtil.d("voice--原搜索接口==" + key);
		key = key.replaceAll(" ", "%20").trim();
		final String url = "http://hezi.360iii.net:48080/webapi/queryMusic_queryMusicApp.action?key=" + key + "&page=" + 1 + "&per_page=" + 20;
		new Thread() {
			public void run() {
				try {
					WifiMusicInfos infos = HttpUtils.searchOld(url);
					if (infos == null) {
						dismissDialog();
						// ToastUtils.show(context, "未找到歌曲");
						toastOrChangeView(true, false, "未找到歌曲");
						return;
					}
					sendToBox(infos);
				} catch (Exception e) {
					e.printStackTrace();
					// ToastUtils.show(getApplicationContext(), "获取数据失败");
					LogUtil.d("voice--search"+e);
					dismissDialog();
					toastOrChangeView(true, false, "获取数据失败");
				}
			};
		}.start();
	}

	public static final int RESULT_SUCCESS = 100;
	public static final int RESULT_FAIL = 200;

	/***
	 * 
	 * @param isResult
	 *            是不是识别的错误
	 * @param isSuccess最终是成功还是失败
	 * @param msg
	 *            toast消息
	 */
	private void toastOrChangeView(final boolean isResult, final boolean isSuccess, final String msg) {
		if (handler != null)
			handler.post(new Runnable() {
				public void run() {
					if (isSuccess) {
						// TODO 搜索有结果并且成功推至音箱
						if (needJump) {
							startToActvitiy(PlayingMusicActivity.class);
						}
						finish();
						return;
					}
					ToastUtils.show(context, msg);
					clickTv.setVisibility(View.VISIBLE);
					circleWaveView.setVisibility(View.GONE);
					clickTv.setText("点击任意位置说话");
					clickArea.setOnClickListener(speechListener);
					recommendTv.setText(recommend[new Random().nextInt(recommend.length)]);
					clickArea.setOnLongClickListener(longClickListener);
					if (!isResult) {
					}
				}
			});
	}

	protected void dismissDialog() {
		new Handler(Looper.getMainLooper()).post(new Runnable() {

			@Override
			public void run() {
				if (myProgressDialog != null && !isFinishing())
					myProgressDialog.dismiss();
			}
		});
	}

	public void setParam() {
		// 清空参数
		speechRecognizer.setParameter(SpeechConstant.PARAMS, null);

		// 设置听写引擎
		speechRecognizer.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_MIX);
		// 设置返回结果格式
		speechRecognizer.setParameter(SpeechConstant.RESULT_TYPE, "json");

		// String lag = preferences.getPrefString("iat_language_preference",
		// "mandarin");
		// if (lag.equals("en_us")) {
		// // 设置语言
		// speechRecognizer.setParameter(SpeechConstant.LANGUAGE, "en_us");
		// } else {
		// 设置语言
		speechRecognizer.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
		// 设置语言区域
		speechRecognizer.setParameter(SpeechConstant.ACCENT, "mandarin");
		// }
		// 设置语音前端点
		speechRecognizer.setParameter(SpeechConstant.VAD_BOS, "4000");
		// 设置语音后端点
		speechRecognizer.setParameter(SpeechConstant.VAD_EOS, "1000");
		// 设置标点符号
		speechRecognizer.setParameter(SpeechConstant.ASR_PTT, "1");
	}

	protected void showDialog(String msg) {
		if (myProgressDialog == null) {
			myProgressDialog = new MyProgressDialog(context);
		}
		myProgressDialog.setMessage(msg);
		myProgressDialog.show();

	}

	private Handler handler = new Handler();

	protected void onDestroy() {
		ToastUtils.cancel();
		handler = null;
		try {
			speechRecognizer.cancel();
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			speechRecognizer.destroy();
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (myProgressDialog != null && !this.isFinishing())
			myProgressDialog.dismiss();
		myProgressDialog = null;
		super.onDestroy();
	}

	private void playOrPause() {
		if (handler != null) {
			handler.post(new Runnable() {
				public void run() {
					mWifiCRUDForMusic.playOrPause(new ResultForMusicListener() {
						public void onResult(String errorCode, List<WifiMusicInfo> infos) {
							handlerResult(errorCode);
						}
					});
				}
			});
		}
	}

	private void sendToBox(final WifiMusicInfos infos) {
		if (handler != null) {
			handler.post(new Runnable() {
				public void run() {
					LogUtil.i("voide--发送list大小：" + infos.getNetMusicInfos().size());
					mWifiCRUDForMusic.playNetResource(infos, WifiCRUDForMusic.NOT_SET_ID, new ResultForMusicListener() {
						public void onResult(String errorCode, List<WifiMusicInfo> infos) {
							handlerResult(errorCode);
						}
					});
				}
			});
		}
	};
}
