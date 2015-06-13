package com.iii360.box.set;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.iii.wifi.dao.info.WifiVolume;
import com.iii.wifi.dao.manager.WifiCRUDForVolume;
import com.iii.wifi.dao.manager.WifiCRUDForVolume.ResultForVolumeListener;
import com.iii360.box.R;
import com.iii360.box.base.BaseActivity;
import com.iii360.box.util.ToastUtils;
import com.iii360.box.util.WifiCRUDUtil;
import com.iii360.box.view.MyProgressDialog;

public class TTSVolumnActivity extends BaseActivity {
	private SeekBar ttsVolumnSeekBar;
	WifiCRUDForVolume crudForVolume;
	protected int current;
	private int max;
	private TextView ttsVolumnTv;
	private MyProgressDialog dialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tts_volumn);
		setupView();
		addListener();
		getTtsVolumn();
	}

	private Handler handler = new Handler();

	private void getTtsVolumn() {
		dialog.show();
		crudForVolume.getTTSVolumeInfo(new ResultForVolumeListener() {

			@Override
			public void onResult(String type, String errorCode, WifiVolume wifiVolume) {
				handler.post(dismissDialog);
				if (WifiCRUDUtil.isSuccess(errorCode)) {
					current = wifiVolume.getCurrentVolume();
					max = wifiVolume.getMaxVolume();
					handler.post(updateViewTask);
				} else {
					ToastUtils.show(context, R.string.ba_get_info_error_toast);
				}
			}
		});
	}

	private Runnable dismissDialog = new Runnable() {
		public void run() {
			if (dialog != null && !isFinishing())
				dialog.dismiss();
		}
	};
	private Runnable updateViewTask = new Runnable() {
		public void run() {
			if (dialog != null && !isFinishing())
				dialog.dismiss();
			if (current == 0 || max == 0) {
				findViewById(R.id.tts_volumn_percent_tv).setVisibility(View.GONE);
				return;
			}
			ttsVolumnTv.setText("" + current * 100 / max);
			ttsVolumnSeekBar.setMax(max - 1);
			findViewById(R.id.tts_volumn_percent_tv).setVisibility(View.VISIBLE);
			ttsVolumnSeekBar.setProgress(current - 1);
		}
	};

	private void addListener() {
		ttsVolumnSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(final SeekBar seekBar) {
				dialog.show();
				crudForVolume.setTTSVolumeInfo(seekBar.getProgress() + 1, new ResultForVolumeListener() {

					@Override
					public void onResult(String type, String errorCode, WifiVolume wifiVolume) {
						if (WifiCRUDUtil.isSuccess(errorCode)) {
							current = seekBar.getProgress() + 1;
						} else {
							ToastUtils.show(context, R.string.ba_config_box_info_error_toast);
						}
						handler.post(updateViewTask);
					}
				});
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {

			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

			}
		});
	}

	private void setupView() {
		this.setViewHead("音箱播报音量");
		crudForVolume = new WifiCRUDForVolume(getBoxIp(), getBoxTcpPort());
		ttsVolumnSeekBar = (SeekBar) findViewById(R.id.tts_volumn_seekbar);
		ttsVolumnTv = (TextView) findViewById(R.id.tts_volumn_value_tv);
		dialog = new MyProgressDialog(context);
		dialog.setMessage(getString(R.string.ba_update_date));
		dialog.setCanceledOnTouchOutside(false);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (dialog != null && !isFinishing())
			dialog.dismiss();
		dialog = null;
	}
}
