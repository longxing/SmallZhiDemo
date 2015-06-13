package com.iii360.box.config;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;

import com.iii.wifi.dao.info.WifiControlInfo;
import com.iii.wifi.dao.manager.WifiCRUDForWeatherTime.ResultForWeatherTimeListener;
import com.iii.wifi.dao.manager.WifiForCommonOprite;
import com.iii360.box.R;
import com.iii360.box.base.BaseActivity;
import com.iii360.box.util.DataUtil;
import com.iii360.box.util.KeyList;
import com.iii360.box.util.LogManager;
import com.iii360.box.util.ToastUtils;
import com.iii360.box.util.WifiCRUDUtil;
import com.iii360.box.view.IView;

/**
 * 添加设备学习指令
 * 
 * @author hefeng
 * 
 */
public class AddDeviceStudyActivity extends BaseActivity implements IView, View.OnClickListener {
	private Button mAddStudyBtn;
	private Button mAddDeleteStudyBtn;
	private StudyHandler mHandler;
	private String mCommand;
	private boolean isStudy;
	// private int mControlId;
	private AddDeviceHelper mAddDeviceHelper;
	private WifiControlInfo mControlInfo;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_device_study);
		this.initViews();
		this.initDatas();
	}

	@Override
	public void initViews() {
		// TODO Auto-generated method stub
		mAddStudyBtn = (Button) findViewById(R.id.add_study_btn);
		mAddDeleteStudyBtn = (Button) findViewById(R.id.add_delete_study_btn);

		mAddStudyBtn.setOnClickListener(this);
		mAddDeleteStudyBtn.setOnClickListener(this);
	}

	@Override
	public void initDatas() {
		// TODO Auto-generated method stub
		this.mControlInfo = (WifiControlInfo) getIntent().getSerializableExtra(KeyList.IKEY_CONTROLINFO_BEAN);
		this.isStudy = getIntent().getBooleanExtra(KeyList.IKEY_DEVICE_IS_STUDY, false);

		mAddDeviceHelper = new AddDeviceHelper(context);
		mHandler = new StudyHandler(mAddStudyBtn, mAddDeleteStudyBtn);
		mAddStudyBtn.setTag(0);

		this.setViewHead(DataUtil.getDeviceName(mControlInfo.getAction()));
		if (isStudy) {
			mHandler.sendEmptyMessage(StudyHandler.HANDLER_STUDY_AFRESH);
		}

		mAddDeviceHelper.setResultListener(new AddDeviceHelper.IResultListener() {
			@Override
			public void onResult(WifiControlInfo info) {
				// TODO Auto-generated method stub
				mControlInfo = info;

				if (TextUtils.isEmpty(info.getDorder())) {
					mHandler.sendEmptyMessage(StudyHandler.HANDLER_START_STUDY);
				} else {
					mHandler.sendEmptyMessage(StudyHandler.HANDLER_STUDY_SUCCESS_CLICK);
				}
			}
		});
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (v == mAddStudyBtn) {
			int tag = (Integer) mAddStudyBtn.getTag();
			if (tag == StudyHandler.TAG_STUDY_SUCCESS) {
				context.startActivity(new Intent(context, PartsManagerActivity.class));
			} else {
				startStudy();
			}

		} else if (v == mAddDeleteStudyBtn) {
			mAddDeviceHelper.updateControl(mControlInfo);
		}
	}

	public void startStudy() {
		LogManager.e("开始采集指令。。。。");

		mHandler.sendEmptyMessage(StudyHandler.HANDLER_STUDYING);
		WifiForCommonOprite wco = new WifiForCommonOprite(getBoxTcpPort(), getBoxIp());
		wco.learnHF(mControlInfo.getDeviceid(), new ResultForWeatherTimeListener() {
			@Override
			public void onResult(String type, String errorCode, String result) {
				// TODO Auto-generated method stub
				LogManager.i("result learnHF " + result + "||errorCode=" + errorCode);

				if (WifiCRUDUtil.isSuccessAll(errorCode)) {
					mCommand = result;

					LogManager.e("采集指令成功 result : " + result);
					if (TextUtils.isEmpty(result)) {

						WifiCRUDUtil.playTTS(context, getResources().getString(R.string.ba_study_error_toast));
						mHandler.sendEmptyMessage(StudyHandler.HANDLER_START_STUDY);

					} else {

						WifiCRUDUtil.playTTS(context, getResources().getString(R.string.ba_study_success_tts));
						mHandler.sendEmptyMessage(StudyHandler.HANDLER_STUDY_SUCCESS_CLICK);
						// 有指令则更新，没有则添加 WifiControlInfo
						mControlInfo.setDorder(mCommand);
						mAddDeviceHelper.addControl(mControlInfo);

					}

				} else {

					WifiCRUDUtil.playTTS(context, getResources().getString(R.string.ba_study_error_toast));
					ToastUtils.show(context, R.string.ba_study_error_toast);
					mHandler.sendEmptyMessage(StudyHandler.HANDLER_START_STUDY);

				}
			}
		});
	}
}
