package com.iii360.box.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.iii.wifi.dao.info.WifiControlInfo;
import com.iii.wifi.dao.info.WifiDeviceInfo;
import com.iii.wifi.dao.info.WifiRoomInfo;
import com.iii.wifi.dao.manager.WifiCRUDForControl;
import com.iii.wifi.dao.manager.WifiCRUDForDevice;
import com.iii.wifi.dao.manager.WifiCRUDForRoom;
import com.iii360.box.R;
import com.iii360.box.base.StudyBaseActivity;
import com.iii360.box.util.DataUtil;
import com.iii360.box.util.KeyList;
import com.iii360.box.util.LogManager;
import com.iii360.box.util.ToastUtils;
import com.iii360.box.util.WifiCRUDUtil;
import com.iii360.box.view.IView;

/****
 * 
 * @author AirStudyActivity，，跳入该页面以后，对WifiDeviceInfo加了deviceid再传入其它页面
 */
public class AirStudyActivity extends StudyBaseActivity implements IView, View.OnClickListener {
	private final static String mDeviceName = "空调";
	private Button mStudyBtn;
	private Button mStudyCloseBtn;
	private Button mDeleteStudyBtn;
	private Button mDeleteCloseStudyBtn;
	private StudyHandler mHandler;
	private StudyHandler mHandler2;
	// private String mRoomName;
	private String mRoomId;
	private String mDeviceId;
	private String mCommand;
	private String mFitting;
	private String mMac;
	private String mDeviceModel;
	private boolean mIsStudy = false;
	private boolean mStudyStatus = false;
	private WifiDeviceInfo mWifiDeviceInfo;
	private int mDeviceType;
	private String mIsTvConfig;
	private TextView airCompletedTv;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_air_study);
		this.initViews();
		this.initDatas();
	}

	@Override
	public void initViews() {
		// TODO Auto-generated method stub
		mStudyBtn = (Button) findViewById(R.id.air_study_btn);
		mDeleteStudyBtn = (Button) findViewById(R.id.air_delete_btn);
		mStudyCloseBtn = (Button) findViewById(R.id.air_close_study_btn);
		mDeleteCloseStudyBtn = (Button) findViewById(R.id.air_close_delete_btn);
		airCompletedTv = (TextView) findViewById(R.id.air_complete_study_tv);
		mStudyBtn.setOnClickListener(this);
		mDeleteStudyBtn.setOnClickListener(this);
		mStudyCloseBtn.setOnClickListener(this);
		mDeleteCloseStudyBtn.setOnClickListener(this);
		airCompletedTv.setOnClickListener(this);

	}

	@Override
	public void initDatas() {
		// TODO Auto-generated method stub
		this.setViewHead("空调");
		this.mHandler = new StudyHandler(mStudyBtn, mDeleteStudyBtn);
		this.mHandler2 = new StudyHandler(mStudyCloseBtn, mDeleteCloseStudyBtn);
		this.setHandler(mHandler);
		this.getIntentData();
		mAddCommand = new AddCommand(context, getBoxIp(), getBoxTcpPort());
	}

	@Override
	public void getIntentData() {
		mWifiDeviceInfo = (WifiDeviceInfo) getIntent().getSerializableExtra(KeyList.IKEY_WIFIDEVICEINFO_ENTITY);
		mFitting = mWifiDeviceInfo.getFitting();
		mMac = mWifiDeviceInfo.getMacadd();
		mDeviceModel = mWifiDeviceInfo.getDeviceModel();
		mRoomId = getIntent().getStringExtra(KeyList.IKEY_ROOM_ID);
		mDeviceType = mWifiDeviceInfo.getDeviceType();
		mIsTvConfig = getIntent().getExtras().getString(KeyList.KEY_IS_TV_CONFIG, "");
		if (mIsTvConfig.equals("yes")) {
			airCompletedTv.setText("完成学习");
		} else {
			airCompletedTv.setText("下一步");
		}
		// Bundle bundle = getIntent().getExtras();
		// if (bundle != null) {
		// mFitting = bundle.getString(KeyList.IKEY_NEW_DEVICE_FITTING);
		// mMac = bundle.getString(KeyList.IKEY_NEW_DEVICE_MAC);
		// // mRoomName = bundle.getString(KeyList.IKEY_ROOM_NAME);
		// mRoomId = bundle.getString(KeyList.IKEY_ROOM_ID);
		// mDeviceModel = bundle.getString(KeyList.IKEY_DEVICE_MODEL);
		// }
	}

	private AddCommand mAddCommand;
	private long startTime;
	private WifiCRUDForRoom mWifiCRUDForRoom;
	private WifiCRUDForControl mWifiCRUDForControl;

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (v == mStudyBtn) {
			if (mAddCommand.isStudy()) {
				ToastUtils.show(context, R.string.ba_not_study_toast);
				return;
			}
			// 1.先添加到设备中；2.学习指令；3.添加控制数据,保存指令
			mIsStudy = true;
			mStudyStatus = true;
			LogManager.i("study ...");
			mHandler.sendEmptyMessage(StudyHandler.HANDLER_STUDYING);

			if (TextUtils.isEmpty(mDeviceId)) {
				addDevice(true);
				LogManager.i("study ...addDevice");
			} else {
				study(mDeviceId);
				LogManager.i("studing...");
			}

		} else if (v == mDeleteStudyBtn) {

			this.mCommand = "";
			this.updateControl(getWifiControlInfo());
			LogManager.i("deleting command .....");

		}  else if (v == mStudyCloseBtn) {
			// 1.先添加到设备中；2.学习指令；3.添加控制数据,保存指令
			if (mStudyStatus) {
				ToastUtils.show(context, R.string.ba_not_study_toast);
				return;
			}
			LogManager.i("study ...");
			mHandler2.sendEmptyMessage(StudyHandler.HANDLER_STUDYING);
			WifiDeviceInfo info = new WifiDeviceInfo();
			info.setRoomid(mRoomId);
			info.setMacadd(mMac);
			info.setDeviceName(mDeviceName);
			info.setFitting(mFitting);
			info.setDeviceType(mDeviceType);
			info.setDeviceModel(mDeviceModel);
			mAddCommand.excute(mHandler2, info);

		} else if (v == mDeleteCloseStudyBtn) {
			// mAddCommand.setNext(true);
			mAddCommand.updateControl(null);
			mAddCommand.setStudy(false);
		} else if (v == airCompletedTv) {
			// context.startActivity(new Intent(context,
			// PartsManagerActivity.class));
			if (mIsTvConfig.equals("yes")) {
				startToRoomListActivity();
			} else {
				this.putIntentData();
			}
		}
	}

	private void startToRoomListActivity() {
		ToastUtils.show(context, R.string.ba_setting_toast);
		if (!mIsStudy) {
			WifiCRUDForDevice mWifiCRUDForDevice = new WifiCRUDForDevice(context, getBoxIp(), getBoxTcpPort());
			WifiDeviceInfo mWifiDeviceInfo = new WifiDeviceInfo();
			mWifiDeviceInfo.setRoomid(mRoomId);
			mWifiDeviceInfo.setMacadd(mMac);
			mWifiDeviceInfo.setDeviceName(mDeviceName);
			mWifiDeviceInfo.setFitting(mFitting);
			mWifiDeviceInfo.setDeviceType(mDeviceType);
			mWifiDeviceInfo.setDeviceModel(mDeviceModel);

			mWifiCRUDForDevice.add(mWifiDeviceInfo, new WifiCRUDForDevice.ResultListener() {
				@Override
				public void onResult(String type, String errorCode, List<WifiDeviceInfo> info) {
					// TODO Auto-generated method stub
					LogManager.i("add device air errorCode=" + errorCode);

					if (WifiCRUDUtil.isSuccessAll(errorCode)) {
						mDeviceId = info.get(0).getDeviceid();

						LogManager.i("create air device success mDeviceId=" + mDeviceId);
						WifiCRUDForControl mControl = new WifiCRUDForControl(context, getBoxIp(), getBoxTcpPort());
						mControl.add(getWifiControlInfo(), new WifiCRUDForControl.ResultListener() {
							@Override
							public void onResult(String type, String errorCode, List<WifiControlInfo> info) {
								// TODO Auto-generated method stub
								LogManager.i("addNullControl...success errorCode=" + errorCode);
								if (WifiCRUDUtil.isSuccessAll(errorCode)) {
									mControlID = info.get(0).getId();
									context.startActivity(new Intent(context, PartsManagerActivity.class));
									LogManager.i("addNullControl...success");
								} else {
									ToastUtils.show(context, R.string.ba_add_device_error_toast);
								}

							}
						});

					} else {
						LogManager.i("create air device error");
						ToastUtils.show(context, R.string.ba_add_device_error_toast);
					}
				}
			});
		} else {
			context.startActivity(new Intent(context, PartsManagerActivity.class));
		}
	}

	@Override
	public void putIntentData() {
		// TODO Auto-generated method stub
		ToastUtils.show(context, R.string.ba_setting_toast);
		if (!mIsStudy) {
			addDevice(false);
		} else {
			startToOther();
		}

	}

	private void addNullControl() {
		LogManager.i("addNullControl...");
		WifiCRUDForControl mControl = new WifiCRUDForControl(context, getBoxIp(), getBoxTcpPort());
		mControl.add(getWifiControlInfo(), new WifiCRUDForControl.ResultListener() {
			@Override
			public void onResult(String type, String errorCode, List<WifiControlInfo> info) {
				// TODO Auto-generated method stub
				LogManager.i("addNullControl...success errorCode=" + errorCode);
				if (WifiCRUDUtil.isSuccessAll(errorCode)) {
					mControlID = info.get(0).getId();
					startToOther();
					LogManager.i("addNullControl...success");
				} else {
					ToastUtils.show(context, R.string.ba_add_device_error_toast);
				}

			}
		});
	}

	private Handler handler = new Handler();
	private HashMap<String, ArrayList<String>> roomControls;

	private boolean infoRoomInRoomList(List<WifiRoomInfo> roomInfos, WifiControlInfo info) {
		for (int i = 0; i < roomInfos.size(); i++) {
			WifiRoomInfo roominfo = roomInfos.get(i);
			if (roominfo.getRoomId().equals(info.getRoomId())) {
				return true;
			}
		}
		return false;
	}

	private void parseRoomAndControl(List<WifiRoomInfo> roomInfos, List<WifiControlInfo> controlInfos) {
		roomControls = new HashMap<String, ArrayList<String>>();
		for (int i = controlInfos.size() - 1; i >= 0; i--) {
			WifiControlInfo info = controlInfos.get(i);
			if (!infoRoomInRoomList(roomInfos, info)) {
				controlInfos.remove(i);
			}
		}
		for (int i = 0; i < controlInfos.size(); i++) {
			WifiControlInfo info = controlInfos.get(i);
			String roomid = info.getRoomId();
			ArrayList<String> controlList = roomControls.get(roomid);
			if (controlList == null) {
				controlList = new ArrayList<String>();
				controlList.add(info.getAction());
				roomControls.put(roomid, controlList);
			} else {
				controlList.add(info.getAction());
			}

		}
		handler.post(new Runnable() {
			public void run() {
				startToAddDeviceActivity();
			}
		});
	}

	/***
	 * 
	 * 跳入这个页面 WifiDeviceInfo需要两个roomId，和deviceId
	 */
	protected void startToAddDeviceActivity() {
		mWifiDeviceInfo.setRoomid(mRoomId);
		Intent intent = new Intent(context, AddDeviceActivity.class);
		intent.putExtra(KeyList.IKEY_DEVICEINFO_BEAN, mWifiDeviceInfo);
		intent.putExtra(KeyList.IKEY_DEVICE_CONTROL_LIST, roomControls.get(mRoomId) == null ? new ArrayList<String>() : roomControls.get(mRoomId));
		startActivity(intent);
	}

	/****
	 * 获取已经的配置的设备和操作，然后跳入AddDeviceActivity
	 */
	private void getRoomInfoAndControlInfo() {
		startTime = System.currentTimeMillis();

		mWifiCRUDForRoom = new WifiCRUDForRoom(context, getBoxIp(), getBoxTcpPort());
		mWifiCRUDForControl = new WifiCRUDForControl(context, getBoxIp(), getBoxTcpPort());
		new Thread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				mWifiCRUDForRoom.seleteAll(new WifiCRUDForRoom.ResultListener() {
					@Override
					public void onResult(String type, String errorCode, final List<WifiRoomInfo> roomInfos) {
						// TODO Auto-generated method stub
						if (WifiCRUDUtil.isSuccessAll(errorCode)) {

							mWifiCRUDForControl.seleteAll(new WifiCRUDForControl.ResultListener() {
								@Override
								public void onResult(String type, String errorCode, List<WifiControlInfo> controlInfos) {
									// TODO Auto-generated method stub

									while (System.currentTimeMillis() - startTime < 800) {

									}

									if (WifiCRUDUtil.isSuccessAll(errorCode)) {
										LogManager.i("get data success");

										if (controlInfos != null) {
											parseRoomAndControl(roomInfos, controlInfos);
										}

									} else {
										ToastUtils.show(context, R.string.ba_get_data_error_toast);
										LogManager.i("get data error");
									}
								}

							});
						} else {
							ToastUtils.show(context, R.string.ba_get_data_error_toast);
						}
					}
				});
			}
		}).start();
	}

	private void startToOther() {
		mWifiDeviceInfo.setDeviceid(mDeviceId);
		Intent intent = null;
		if (mIsTvConfig.equals("yes")) {
			getRoomInfoAndControlInfo();
		} else {
			intent = new Intent(context, TvStudyActivity.class);
			intent.putExtra(KeyList.IKEY_WIFIDEVICEINFO_ENTITY, mWifiDeviceInfo);
			intent.putExtra(KeyList.IKEY_ROOM_ID, mRoomId);
			startActivity(intent);
			finish();
		}
		// Bundle bundle = new Bundle();
		// // bundle.putString(KeyList.IKEY_NEW_DEVICE_FITTING, mFitting);
		// // bundle.putString(KeyList.IKEY_NEW_DEVICE_MAC, mMac);
		// bundle.putString(KeyList.IKEY_ROOM_ID, mRoomId);
		// // bundle.putString(KeyList.IKEY_ROOM_NAME, mRoomName);
		// bundle.putString(KeyList.IKEY_DEVICE_ID, mDeviceId);
		// bundle.putString(KeyList.IKEY_DEVICE_MODEL, mDeviceModel);
		// intent.putExtras(bundle);

	}

	@Override
	public void studySuccess(String result) {
		// TODO Auto-generated method stub
		mCommand = result;
		LogManager.i("正在保存采集的数据.....");
		this.addControl(getWifiControlInfo());
		mStudyStatus = false;
	}

	@Override
	public void studyError() {
		// TODO Auto-generated method stub
		LogManager.i("正在保存采集失败的数据.....");
		this.addStudyErrorControl(getWifiControlInfo());
		mStudyStatus = false;
	}

	public WifiControlInfo getWifiControlInfo() {
		WifiControlInfo wInfo = new WifiControlInfo();
		wInfo.setId(mControlID);
		wInfo.setDorder(mCommand);
		wInfo.setRoomId(mRoomId);
		wInfo.setDeviceid(mDeviceId);
		wInfo.setDeviceModel(mDeviceModel);
		wInfo.setAction(DataUtil.formatAction(KeyList.GKEY_OPERATION_DEVICE_ARRAY[0], mDeviceName));

		LogManager.d("air========mControlID=" + mControlID + "||mRoomId=" + mRoomId + "||mDeviceId=" + mDeviceId + "||mDeviceModel=" + mDeviceModel + "||Action="
				+ DataUtil.formatAction(KeyList.GKEY_OPERATION_DEVICE_ARRAY[2], mDeviceName) + "||mCommand=" + mCommand);

		return wInfo;
	}

	private int mControlID;

	@Override
	public void addControlSuccess(List<WifiControlInfo> list) {
		// TODO Auto-generated method stub
		LogManager.i("addControlSuccess callback mIsStudy=" + mIsStudy);
		mStudyStatus = false;
		try {
			mControlID = list.get(0).getId();
		} catch (Exception e) {
			// TODO: handle exception
			LogManager.e("addControlSuccess callback null data..");
		}

		if (mIsStudy) {
			mHandler.sendEmptyMessage(StudyHandler.HANDLER_STUDY_SUCCESS_UNCLICK);
			this.playTTS(getString(R.string.ba_study_success_tts));
		}
	}

	@Override
	public void addControlError() {
		// TODO Auto-generated method stub
		super.addControlError();
		mStudyStatus = false;
	}

	public void addDevice(final boolean goToStudy) {
		LogManager.i("add device...........mRoomId=" + mRoomId + "||mMac=" + mMac + "||mDeviceName=" + mDeviceName + "||mFitting=" + mFitting);

		WifiCRUDForDevice mWifiCRUDForDevice = new WifiCRUDForDevice(context, getBoxIp(), getBoxTcpPort());
		WifiDeviceInfo mWifiDeviceInfo = new WifiDeviceInfo();
		mWifiDeviceInfo.setRoomid(mRoomId);
		mWifiDeviceInfo.setMacadd(mMac);
		mWifiDeviceInfo.setDeviceName(mDeviceName);
		mWifiDeviceInfo.setFitting(mFitting);
		mWifiDeviceInfo.setDeviceType(mDeviceType);
		mWifiDeviceInfo.setDeviceModel(mDeviceModel);

		mWifiCRUDForDevice.add(mWifiDeviceInfo, new WifiCRUDForDevice.ResultListener() {
			@Override
			public void onResult(String type, String errorCode, List<WifiDeviceInfo> info) {
				// TODO Auto-generated method stub
				LogManager.i("add device air errorCode=" + errorCode);

				if (WifiCRUDUtil.isSuccessAll(errorCode)) {
					mDeviceId = info.get(0).getDeviceid();

					LogManager.i("create air device success mDeviceId=" + mDeviceId);

					if (goToStudy) {
						study(mDeviceId);
					} else {
						addNullControl();
					}

				} else {
					LogManager.i("create air device error");
					ToastUtils.show(context, R.string.ba_add_device_error_toast);
				}
			}
		});
	}

	@Override
	public void selectDeviceSuccess(List<WifiDeviceInfo> list) {
		// TODO Auto-generated method stub

	}

}
