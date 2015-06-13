package com.iii360.box;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.ui.RecognizerDialog;
import com.iflytek.sunflower.FlowerCollector;
import com.iii.wifi.dao.info.WifiMusicInfo;
import com.iii.wifi.dao.info.WifiMusicInfos;
import com.iii.wifi.dao.newmanager.WifiCRUDForMusic;
import com.iii.wifi.dao.newmanager.WifiCRUDForMusic.ResultForMusicListener;
import com.iii360.box.base.BaseActivity;
import com.iii360.box.common.BasePreferences;
import com.iii360.box.music.MusicSearchBean;
import com.iii360.box.util.JsonParser;
import com.iii360.box.util.LogUtil;
import com.iii360.box.util.ToastUtils;
import com.iii360.box.util.WifiCRUDUtil;
import com.iii360.sup.common.utl.LogManager;
import com.voice.assistant.main.newmusic.NetResourceMusicInfo;

public class TestVoice extends BaseActivity implements OnClickListener {
	private Button start;
	private Button stop;
	private Button cancel;
	private SpeechRecognizer speechRecognizer;
	private BasePreferences preferences;
	private InitListener mInitListener = new InitListener() {

		@Override
		public void onInit(int code) {
			Log.d("info", "SpeechRecognizer init() code = " + code);
			if (code != ErrorCode.SUCCESS) {
				ToastUtils.show(context, "初始化失败,错误码：" + code);
			}
		}
	};
	RecognizerDialog iatDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.testvoice);
		setupView();
		addListener();
		initSpeech();
	}

	protected void search(String key) {

		final String url = "http://hezi.360iii.net:48080/webapi/queryMusic_queryMusicApp.action?key=" + key + "&page=" + 1 + "&per_page=" + 20;
		new Thread() {
			public void run() {
				HttpClient client = new DefaultHttpClient();

				try {
					HttpResponse res = client.execute(new HttpGet(url));
					if (res.getStatusLine().getStatusCode() != 200) {
						throw new Exception("errorcode:" + res.getStatusLine().getStatusCode());
					}
					HttpEntity entity = res.getEntity();
					InputStream in = entity.getContent();
					BufferedReader reader = new BufferedReader(new InputStreamReader(in, "utf-8"));
					parseStream(reader);
				} catch (Exception e) {
					ToastUtils.show(getApplicationContext(), "获取数据失败");
					e.printStackTrace();
				}
			};
		}.start();
	}

	protected void parseStream(BufferedReader reader) throws Exception {
		ArrayList<MusicSearchBean> beans = new ArrayList<MusicSearchBean>();
		String line;
		while ((line = reader.readLine()) != null) {
			line = line.trim();
			// 心肝宝贝(首届全国[爱肝日]主题曲)[刘德华^心肝宝贝(首届全国[爱肝日]主题曲)][http://nie.dfe.yymommy.com/mp3_128_1/03/96/0378de5e046e6a4b40a9a4def21f4496.mp3?k=d80872c3f056c337&t=1420880987]
			String deleteLastChar = line.substring(0, line.lastIndexOf("]"));
			MusicSearchBean bean = new MusicSearchBean(deleteLastChar.substring(deleteLastChar.indexOf("[") + 1, deleteLastChar.lastIndexOf("]")).trim(), line.substring(line.indexOf("http://"),
					line.lastIndexOf("]")), System.currentTimeMillis());
			// bean.setPage(page);
			beans.add(bean);
		}
		WifiMusicInfos infos = new WifiMusicInfos();
		int count = 0;
		for (int i = 0; count < 20 && i < beans.size(); i++) {
			MusicSearchBean audio = beans.get(i);
			String[] arr = audio.getMessage().split("\\^");
			String singer = arr[0];
			NetResourceMusicInfo Info = new NetResourceMusicInfo(arr[1], singer, "-1", audio.getUrl());
			infos.setNetMusicInfos(Info);
			count++;
		}
		LogUtil.i("发送list大小：" + infos.getNetMusicInfos().size());
		WifiCRUDForMusic mWifiCRUDForMusic = new WifiCRUDForMusic(getBoxIp(), getBoxTcpPort());

		mWifiCRUDForMusic.playNetResource(infos, WifiCRUDForMusic.NOT_SET_ID, new ResultForMusicListener() {

			@Override
			public void onResult(String errorCode, List<WifiMusicInfo> infos) {
				// TODO Auto-generated method stub
				if (!WifiCRUDUtil.isSuccessAll(errorCode)) {
					ToastUtils.show(context, R.string.ba_config_box_info_error_toast);
					LogManager.e("发送网络播放资源失败");
				}
			}
		});
	}

	private void initSpeech() {
		speechRecognizer = SpeechRecognizer.createRecognizer(this, mInitListener);
		iatDialog = new RecognizerDialog(this, mInitListener);
	}

	private void setupView() {
		preferences = new BasePreferences(this);
		start = (Button) findViewById(R.id.voice_start);
		stop = (Button) findViewById(R.id.voice_stop);
		cancel = (Button) findViewById(R.id.voice_cancel);
	}

	private void addListener() {
		cancel.setOnClickListener(this);
		stop.setOnClickListener(this);
		start.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.voice_cancel:
			cancel();
			break;
		case R.id.voice_stop:
			stop();
			break;
		case R.id.voice_start:
			start();
			break;
		}
	}

	/**
	 * 参数设置
	 * 
	 * @param param
	 * @return
	 */
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
		speechRecognizer.setParameter(SpeechConstant.VAD_BOS, preferences.getPrefString("iat_vadbos_preference", "4000"));
		// 设置语音后端点
		speechRecognizer.setParameter(SpeechConstant.VAD_EOS, preferences.getPrefString("iat_vadeos_preference", "1000"));
		// 设置标点符号
		speechRecognizer.setParameter(SpeechConstant.ASR_PTT, preferences.getPrefString("iat_punc_preference", "1"));
		// 设置音频保存路径
//		speechRecognizer.setParameter(SpeechConstant.ASR_AUDIO_PATH, Environment.getExternalStorageDirectory() + "boxassistant/wavaudio.pcm");
	}

	private void cancel() {
		speechRecognizer.cancel();
	}

	private void stop() {
		speechRecognizer.stopListening();
	}

	private RecognizerListener recognizerListener = new RecognizerListener() {

		@Override
		public void onBeginOfSpeech() {
			Log.i("info", "onBeginOfSpeech");
			ToastUtils.show(context, "开始说话");
		}

		@Override
		public void onError(SpeechError error) {
			Log.i("info", "onError");
			ToastUtils.show(context, error.getPlainDescription(true));
		}

		@Override
		public void onEndOfSpeech() {
			Log.i("info", "onEndOfSpeech");
			ToastUtils.show(context, "结束说话");
		}

		String text = "";
		private long lastSearchTime;

		@Override
		public void onResult(RecognizerResult results, boolean isLast) {
			LogUtil.d(results.getResultString());
			String text = JsonParser.parseIatResult(results.getResultString());
			this.text = "" + text;
			Log.i("info", "onResult=" + text + ",::" + isLast);
			// mResultText.append(text);
			// mResultText.setSelection(mResultText.length());
			long currTime = System.currentTimeMillis();
			if (currTime - lastSearchTime > 2000) {
//				search(text);
				lastSearchTime = currTime;
				ToastUtils.show(context, "" + this.text + "," + text.length());
			}
//			Log.i("info", "" + this.text + "," + text.length());
			if (isLast) {
				// TODO 最后的结果
			}
		}

		@Override
		public void onVolumeChanged(int volume) {
			Log.i("info", "onVolumeChanged=" + volume);
//			ToastUtils.show(context, "当前正在说话，音量大小：" + volume);
		}

		@Override
		public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {
			Log.i("info", "onEvent");
		}
	};
	int ret = 0;// 函数调用返回值

	private void start() {

		setParam();
		ret = speechRecognizer.startListening(recognizerListener);
		if (ret != ErrorCode.SUCCESS) {
			ToastUtils.show(context, "听写失败,错误码：" + ret);
		} else {
			ToastUtils.show(context, "请开始说话");
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
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
	}

	@Override
	protected void onResume() {
		// 移动数据统计分析
		FlowerCollector.onResume(this);
		FlowerCollector.onPageStart("TestVoice");
		super.onResume();
	}

	@Override
	protected void onPause() {
		// 移动数据统计分析
		FlowerCollector.onPageEnd("TestVoice");
		FlowerCollector.onPause(this);
		super.onPause();
	}
}
