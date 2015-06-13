package com.iii360.box.config;

import java.util.List;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.iii.wifi.dao.info.WifiControlInfo;
import com.iii.wifi.dao.info.WifiDeviceInfo;
import com.iii.wifi.dao.manager.WifiCRUDForDevice;
import com.iii360.box.R;
import com.iii360.box.base.StudyBaseActivity;
import com.iii360.box.util.DataUtil;
import com.iii360.box.util.KeyList;
import com.iii360.box.util.LogManager;
import com.iii360.box.util.ToastUtils;
import com.iii360.box.util.WifiCRUDUtil;
import com.iii360.box.view.IView;

/**
 * 电视指令学习
 * 
 * @author hefeng
 * 
 */
public class TvStudyActivity extends StudyBaseActivity implements IView, View.OnClickListener {
	private final static String mDeviceName = "电视机";

	private Button mTvStudyBtn;
	private Button mDeleteStudyBtn;
	private TextView mCompleteStudyTv;

	private String mCommand;
	private String mRoomId;
	private StudyHandler mHandler;

	// private String mFitting;
	// private String mMac;
	private String mDeviceId;
	// private String mRoomName;
	private int mControlID;

	private boolean isClickStudy = false;
	private String mNewDeviceNames;
	private String mDeviceModel;
	private int mDeviceType;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tv_study);
		this.initViews();
		this.initDatas();
	}

	@Override
	public void initViews() {
		// TODO Auto-generated method stub
		mTvStudyBtn = (Button) findViewById(R.id.tv_study_btn);
		mDeleteStudyBtn = (Button) findViewById(R.id.tv_delete_study_btn);
		mCompleteStudyTv = (TextView) findViewById(R.id.tv_complete_study_tv);

		mTvStudyBtn.setOnClickListener(this);
		mDeleteStudyBtn.setOnClickListener(this);
		mCompleteStudyTv.setOnClickListener(this);
	}

	@Override
	public void initDatas() {
		// TODO Auto-generated method stub
		this.setViewHead("电视机");
		this.mHandler = new StudyHandler(mTvStudyBtn, mDeleteStudyBtn);
		this.setHandler(mHandler);
		this.getIntentData();
	}

	private WifiDeviceInfo mWifiDeviceInfo;

	@Override
	public void getIntentData() {
		// TODO Auto-generated method stub
		mWifiDeviceInfo = (WifiDeviceInfo) getIntent().getSerializableExtra(KeyList.IKEY_WIFIDEVICEINFO_ENTITY);
		mDeviceId = mWifiDeviceInfo.getDeviceid();
		mDeviceModel = mWifiDeviceInfo.getDeviceModel();
		mRoomId = getIntent().getStringExtra(KeyList.IKEY_ROOM_ID);
		mDeviceType = mWifiDeviceInfo.getDeviceType();

		// Bundle bundle = getIntent().getExtras();
		// if (bundle != null) {
		// mDeviceId = bundle.getString(KeyList.IKEY_DEVICE_ID);
		// mRoomId = bundle.getString(KeyList.IKEY_ROOM_ID);
		// mDeviceModel = bundle.getString(KeyList.IKEY_DEVICE_MODEL);
		// // mFitting = bundle.getString(KeyList.IKEY_NEW_DEVICE_FITTING);
		// // mMac = bundle.getString(KeyList.IKEY_NEW_DEVICE_MAC);
		// // mRoomName = bundle.getString(KeyList.IKEY_ROOM_NAME);
		// }
	}

	/**
	 * 从SelectRoomActivity直接跳入的,还没有deviceid
	 */
	private void createDevice(final boolean ToStudy) {

		WifiCRUDForDevice mWifiCRUDForDevice = new WifiCRUDForDevice(context, getBoxIp(), getBoxTcpPort());
		WifiDeviceInfo deviceinfo = new WifiDeviceInfo();
		deviceinfo.setRoomid(mRoomId);
		deviceinfo.setMacadd(mWifiDeviceInfo.getMacadd());
		deviceinfo.setFitting(mWifiDeviceInfo.getFitting());
		deviceinfo.setDeviceType(mWifiDeviceInfo.getDeviceType());
		deviceinfo.setDeviceModel(mWifiDeviceInfo.getDeviceModel());
		deviceinfo.setDeviceName(mDeviceName);
		mWifiCRUDForDevice.add(deviceinfo, new WifiCRUDForDevice.ResultListener() {
			public void onResult(String type, String errorCode, List<WifiDeviceInfo> info) {
				if (WifiCRUDUtil.isSuccessAll(errorCode)) {
					mWifiDeviceInfo.setDeviceid(info.get(0).getDeviceid());
					mDeviceId = mWifiDeviceInfo.getDeviceid();
					if (ToStudy) {
						isClickStudy = true;
						mHandler.sendEmptyMessage(StudyHandler.HANDLER_STUDYING);
						// 1.先添加到设备中 2.学习指令 3.添加控制数据,保存指令
						selectDevice(mDeviceId);
					} else {
						createData();
					}

				} else {
					ToastUtils.show(context, R.string.ba_config_box_info_error_toast);
				}
			}
		});
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

		if (v == mTvStudyBtn) {
			if (mDeviceId == null) {
				createDevice(true);
			} else {
				isClickStudy = true;
				mHandler.sendEmptyMessage(StudyHandler.HANDLER_STUDYING);
				// 1.先添加到设备中 2.学习指令 3.添加控制数据,保存指令
				this.selectDevice(mDeviceId);
			}

		} else if (v == mDeleteStudyBtn) {
			this.mCommand = "";
			this.updateControl(getWifiControlInfo());

		} else if (v == mCompleteStudyTv) {
			LogManager.i("完成学习---1.搜索设备，存在则更新，否则添加 2.添加控制数据");

			ToastUtils.show(context, R.string.ba_setting_toast);
			isClickStudy = false;
			if (mDeviceId == null) {
				createDevice(false);
			} else
				createData();

		}
	}

	public void createData() {
		// 1.判断是否学习过;2.先添加到设备中 3.添加控制数据,保存指令
		WifiCRUDForDevice mWifiCRUDForDevice = new WifiCRUDForDevice(context, getBoxIp(), getBoxTcpPort());
		mWifiCRUDForDevice.seleteByDeviceId(mDeviceId, new WifiCRUDForDevice.ResultListener() {
			@Override
			public void onResult(String type, String errorCode, List<WifiDeviceInfo> info) {
				// TODO Auto-generated method stub

				if (WifiCRUDUtil.isSuccessAll(errorCode)) {
					if (info == null || info.isEmpty()) {
						LogManager.i("完成学习---搜索机器狗设备失败，mDeviceId=" + mDeviceId);
						ToastUtils.show(context, R.string.ba_add_device_error_toast);
						return;
					}

					WifiCRUDForDevice mWifiCRUDForDevice = new WifiCRUDForDevice(context, getBoxIp(), getBoxTcpPort());
					WifiDeviceInfo mWifiDeviceInfo = info.get(0);
					if (!DataUtil.isExistDevice(mWifiDeviceInfo.getDeviceName(), mDeviceName)) {
						mNewDeviceNames = mWifiDeviceInfo.getDeviceName() + KeyList.SEPARATOR + mDeviceName;
					} else {
						mNewDeviceNames = mWifiDeviceInfo.getDeviceName();
					}

					mWifiDeviceInfo.setDeviceName(mNewDeviceNames);
					mWifiDeviceInfo.setDeviceType(mDeviceType);
					mWifiCRUDForDevice.updata(mWifiDeviceInfo, new WifiCRUDForDevice.ResultListener() {
						@Override
						public void onResult(String type, String errorCode, List<WifiDeviceInfo> info) {
							// TODO Auto-generated method stub
							if (WifiCRUDUtil.isSuccessAll(errorCode)) {
								LogManager.i("完成学习---1.搜索机器狗设备，存在则更新控件成功");
								addControl(getWifiControlInfo());

							} else {
								LogManager.i("完成学习---1.搜索机器狗设备，存在则更新控件失败");
								ToastUtils.show(context, R.string.ba_add_device_error_toast);
							}
						}
					});

				} else {
					ToastUtils.show(context, R.string.ba_config_box_info_error_toast);
				}
			}
		});
	}

	@Override
	public void putIntentData() {
	}

	public WifiControlInfo getWifiControlInfo() {
		WifiControlInfo wInfo = new WifiControlInfo();
		wInfo.setId(mControlID);
		wInfo.setDorder(mCommand);
		wInfo.setRoomId(mRoomId);
		wInfo.setDeviceid(mDeviceId);
		wInfo.setDeviceModel(mDeviceModel);
		wInfo.setAction(DataUtil.formatAction(KeyList.GKEY_OPERATION_DEVICE_ARRAY[2], mDeviceName));

		LogManager.d("air========mControlID=" + mControlID + "||mRoomId=" + mRoomId + "||mDeviceId=" + mDeviceId + "||mDeviceModel=" + mDeviceModel + "||Action="
				+ DataUtil.formatAction(KeyList.GKEY_OPERATION_DEVICE_ARRAY[2], mDeviceName) + "||mCommand=" + mCommand);

		return wInfo;
	}

	@Override
	public void selectDeviceSuccess(List<WifiDeviceInfo> list) {
		// TODO Auto-generated method stub
		this.updateDevice(list);
	}

	@Override
	public void studySuccess(String result) {
		// TODO Auto-generated method stub
		mCommand = result;
		this.addControl(getWifiControlInfo());
	}

	@Override
	public void studyError() {
		// TODO Auto-generated method stub
		LogManager.i("正在保存采集失败的数据.....");
		this.addStudyErrorControl(getWifiControlInfo());
	}

	@Override
	public void addControlSuccess(List<WifiControlInfo> list) {
		// TODO Auto-generated method stub
		LogManager.i("2.添加控制数据成功 isStudy=" + isClickStudy);

		ToastUtils.cancel();

		try {
			mControlID = list.get(0).getId();
		} catch (Exception e) {
			// TODO: handle exception
			LogManager.e("addControlSuccess callback null data..");
		}

		if (isClickStudy) {

			mHandler.sendEmptyMessage(StudyHandler.HANDLER_STUDY_SUCCESS_UNCLICK);
			WifiCRUDUtil.playTTS(context, getResources().getString(R.string.ba_study_success_tts));

		} else {
			startToActvitiy(PartsManagerActivity.class);
		}
	}

	public void updateDevice(List<WifiDeviceInfo> list) {
		WifiCRUDForDevice mWifiCRUDForDevice = new WifiCRUDForDevice(context, getBoxIp(), getBoxTcpPort());
		WifiDeviceInfo mWifiDeviceInfo = list.get(0);
		String deviceNames = "";
		if (!DataUtil.isExistDevice(mWifiDeviceInfo.getDeviceName(), mDeviceName)) {
			deviceNames = mWifiDeviceInfo.getDeviceName() + KeyList.SEPARATOR + mDeviceName;
		} else {
			deviceNames = mWifiDeviceInfo.getDeviceName();
		}

		// String deviceNames = mWifiDeviceInfo.getDeviceName() +
		// KeyList.SEPARATOR + mDeviceName;
		mWifiDeviceInfo.setDeviceName(deviceNames);

		mWifiCRUDForDevice.updata(mWifiDeviceInfo, new WifiCRUDForDevice.ResultListener() {
			@Override
			public void onResult(String type, String errorCode, List<WifiDeviceInfo> info) {
				// TODO Auto-generated method stub
				if (WifiCRUDUtil.isSuccessAll(errorCode)) {

					LogManager.i("update tv device success");
					study(mDeviceId);

				} else {
					LogManager.i("update tv device error");
					ToastUtils.show(context, R.string.ba_add_device_error_toast);
				}
			}
		});
	}
}
