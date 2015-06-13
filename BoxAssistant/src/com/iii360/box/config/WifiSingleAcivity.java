package com.iii360.box.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.iii.wifi.dao.info.WifiControlInfo;
import com.iii.wifi.dao.info.WifiDeviceInfo;
import com.iii.wifi.dao.info.WifiDeviceInfos;
import com.iii.wifi.dao.info.WifiRoomInfo;
import com.iii.wifi.dao.info.WifiUpdateInfo;
import com.iii.wifi.dao.manager.WifiCRUDForControl;
import com.iii.wifi.dao.manager.WifiCRUDForDevice;
import com.iii.wifi.dao.manager.WifiCRUDForRoom;
import com.iii.wifi.dao.manager.WifiCRUDForRoom.ResultListener;
import com.iii.wifi.dao.manager.WifiCRUDForUpdate;
import com.iii.wifi.dao.manager.WifiCreateAndParseSockObjectManager;
import com.iii360.box.R;
import com.iii360.box.adpter.ListApdater;
import com.iii360.box.base.BaseActivity;
import com.iii360.box.common.Invoke;
import com.iii360.box.util.DataUtil;
import com.iii360.box.util.KeyList;
import com.iii360.box.util.LogManager;
import com.iii360.box.util.ToastUtils;
import com.iii360.box.util.WaitUtils;
import com.iii360.box.util.WifiCRUDUtil;
import com.iii360.box.view.IView;
import com.iii360.box.view.ListDialog;
import com.iii360.box.view.MyProgressDialog;

/**
 * wifi单品配置
 * 
 * @author hefeng
 * 
 */
public class WifiSingleAcivity extends BaseActivity implements IView, Invoke, View.OnClickListener {
	private RelativeLayout mRoomLayout;
	private RelativeLayout mDeviceLayout;
	private TextView mRoomTv;
	private TextView mDeviceTv;
	private List<String> mListData;

	private WifiRoomInfo mWifiRoomInfo;
	private WifiDeviceInfo mWifiDeviceInfo;
	private WifiControlInfo mWifiControlInfo;

	private String mRoomName;
	private String mDeviceName;
	private String mFittingName;
	private String mMacAddress;
	private String mDeviceModel;
	private int mDeviceType;

	private boolean mIsUpdate;
	private MyProgressDialog mProgressDialog;
	private GetNewDevice mGetNewDevice;
	private long startTime;
	private TextView backBtn;
	private ImageButton confirmBtn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_wifi_single);
		this.initViews();
		this.getIntentData();
		this.initDatas();
	}

	@Override
	public void initViews() {
		backBtn = (TextView) findViewById(R.id.head_left_textview);
		backBtn.setText("返回");
		backBtn.setOnClickListener(this);
		confirmBtn = (ImageButton) findViewById(R.id.head_right_btn);
		confirmBtn.setVisibility(View.VISIBLE);
		confirmBtn.setImageResource(R.drawable.wifi_single_confirm_selector);
		confirmBtn.setOnClickListener(this);
		// TODO Auto-generated method stub
		mRoomLayout = (RelativeLayout) findViewById(R.id.room_layout);
		mDeviceLayout = (RelativeLayout) findViewById(R.id.device_layout);
		mRoomTv = (TextView) findViewById(R.id.room_tv);
		mDeviceTv = (TextView) findViewById(R.id.device_tv);

		mRoomLayout.setOnClickListener(this);
		mDeviceLayout.setOnClickListener(this);
	}

	private WifiCRUDForControl mWifiCRUDForControl;
	protected List<WifiRoomInfo> roomInfos;
	protected List<WifiControlInfo> controlInfos;

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
											WifiSingleAcivity.this.roomInfos = roomInfos;
											WifiSingleAcivity.this.controlInfos = controlInfos;
											parseRoomAndControl(roomInfos, controlInfos);
										} else
											dismissDialog();

									} else {
										ToastUtils.show(context, R.string.ba_get_data_error_toast);
										LogManager.i("get data error");
										dismissDialog();
									}
								}

							});
						} else {
							ToastUtils.show(context, R.string.ba_get_data_error_toast);
							dismissDialog();
						}
					}
				});
			}
		}).start();
	}

	private void dismissDialog() {
		handler.post(new Runnable() {
			public void run() {
				if (mProgressDialog != null && !isFinishing())
					mProgressDialog.dismiss();
			}
		});
	}

	private Map<String, ArrayList<String>> roomdevices;

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
		roomdevices = new HashMap<String, ArrayList<String>>();
		for (int i = controlInfos.size() - 1; i >= 0; i--) {
			WifiControlInfo info = controlInfos.get(i);
			if (!infoRoomInRoomList(roomInfos, info)) {
				controlInfos.remove(i);
			}
		}
		for (int i = 0; i < controlInfos.size(); i++) {
			WifiControlInfo info = controlInfos.get(i);
			String[] arr = info.getAction().split(KeyList.SEPARATOR_ACTION_SUBLIT);
			String roomid = info.getRoomId();
			ArrayList<String> list = roomdevices.get(roomid);
			if (list == null) {
				list = new ArrayList<String>();
				list.add(arr[1]);
				roomdevices.put(roomid, list);
			} else {
				if (!list.contains(arr[1])) {
					list.add(arr[1]);
				}
			}
		}
		handler.post(new Runnable() {

			@Override
			public void run() {
				if (mProgressDialog != null && !isFinishing())
					mProgressDialog.dismiss();
				inputData();
			}
		});
	}

	protected void inputData() {
		if (TextUtils.isEmpty(mRoomTv.getText().toString().trim())) {
			mRoomTv.setText(getOkRoomList().get(0));
			mDeviceTv.setText(getOkDeviceList(getOkRoomList().get(0)).get(0));
		}
	}

	private Handler handler = new Handler();

	@Override
	public void initDatas() {
		// TODO Auto-generated method stub
		mGetNewDevice = new GetNewDevice(context);
		mProgressDialog = new MyProgressDialog(context);
		if (!TextUtils.isEmpty(mOldRoomName)) {
			mRoomTv.setText(mOldRoomName);
		}
		if (!TextUtils.isEmpty(mWifiDeviceInfo.getDeviceName())) {
			mDeviceTv.setText(mWifiDeviceInfo.getDeviceName());
		}
		((TextView) findViewById(R.id.head_title_tv)).setText(mFittingName);
		// ViewHead.showAll(this, mFittingName,
		// R.drawable.ba_check_btn_selector).setOnClickListener(new
		// View.OnClickListener() {
		// @Override
		// public void onClick(View v) {
		// // TODO Auto-generated method stub
		// // ToastUtils.show(context, R.string.ba_setting_toast);
		// mRoomName = mRoomTv.getText().toString();
		// mDeviceName = mDeviceTv.getText().toString();
		// if (TextUtils.isEmpty(mRoomName)) {
		// ToastUtils.show(WifiSingleAcivity.this, "请选择房间");
		// return;
		// }
		// if (TextUtils.isEmpty(mDeviceName)) {
		// ToastUtils.show(WifiSingleAcivity.this, "请选择设备");
		// return;
		// }
		// if (!mIsUpdate) {
		// requestBox();
		// } else {
		// update();
		// }
		// }
		// });
		mProgressDialog.setMessage(getString(R.string.ba_setting_toast));
		mProgressDialog.show();
		getRoomInfoAndControlInfo();
	}

	private String mOldRoomName;

	public void getIntentData() {
		Intent intent = getIntent();
		mIsUpdate = intent.getBooleanExtra(KeyList.IKEY_DEVICE_UPDATE, false);
		mWifiDeviceInfo = (WifiDeviceInfo) intent.getSerializableExtra(KeyList.IKEY_WIFIDEVICEINFO_ENTITY);
		mOldRoomName = intent.getStringExtra(KeyList.PKEY_ROOM_NAME);

		mFittingName = mWifiDeviceInfo.getFitting();
		mMacAddress = mWifiDeviceInfo.getMacadd();
		mDeviceModel = mWifiDeviceInfo.getDeviceModel();
		mDeviceType = mWifiDeviceInfo.getDeviceType();

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (v == mRoomLayout) {
			List<String> arr = getOkRoomList();
			if (arr == null) {
				getRoomInfoAndControlInfo();
			} else
				showListDialog(arr, mRoomTv, false);

		} else if (v == mDeviceLayout) {
			if (TextUtils.isEmpty(mRoomTv.getText().toString().trim())) {
				ToastUtils.show(context, "请先选择房间");
				return;
			}
			List<String> arr = getOkDeviceList(mRoomTv.getText().toString().trim());
			if (arr == null) {
				getRoomInfoAndControlInfo();
			} else
				showListDialog(arr, mDeviceTv, true);

		} else if (backBtn == v) {
			finish();
		} else if (v == confirmBtn) {

			// TODO Auto-generated method stub
			// ToastUtils.show(context, R.string.ba_setting_toast);
			mRoomName = mRoomTv.getText().toString();
			mDeviceName = mDeviceTv.getText().toString();
			if (TextUtils.isEmpty(mRoomName)) {
				ToastUtils.show(WifiSingleAcivity.this, "请选择房间");
				return;
			}
			if (TextUtils.isEmpty(mDeviceName)) {
				ToastUtils.show(WifiSingleAcivity.this, "请选择设备");
				return;
			}
			if (!mIsUpdate) {
				requestBox();
			} else {
				update();
			}

		}
	}

	private static String getRoomName(String roomId, List<WifiRoomInfo> roomInfos) {
		for (int i = 0; i < roomInfos.size(); i++) {
			WifiRoomInfo roominfo = roomInfos.get(i);
			if (roominfo.getRoomId().equals(roomId)) {
				return roominfo.getRoomName();
			}
		}
		return "";
	}

	private List<String> getOkDeviceList(String roomName) {
		if (roomdevices == null)
			return null;
		Iterator<Entry<String, ArrayList<String>>> it = roomdevices.entrySet().iterator();
		ArrayList<String> devices = new ArrayList<String>(Arrays.asList(KeyList.GKEY_DEVICE_ARRAY));
		while (it.hasNext()) {
			Entry<String, ArrayList<String>> entry = it.next();
			String name = getRoomName(entry.getKey(), roomInfos);
			if (roomName.equals(name)) {
				if (roomName.equals(mOldRoomName)) {
					entry.getValue().remove(mWifiDeviceInfo.getDeviceName());
				}
				devices.removeAll(entry.getValue());
				return devices;
			}
		}
		return devices;
	}

	private List<String> getOkRoomList() {
		if (roomdevices == null || roomInfos == null)
			return null;
		if (roomdevices.isEmpty()) {
			return Arrays.asList(KeyList.GKEY_ROOM_NAME_ARRAY);
		}
		Iterator<Entry<String, ArrayList<String>>> it = roomdevices.entrySet().iterator();
		ArrayList<String> tempRoom = new ArrayList<String>();
		while (it.hasNext()) {
			Entry<String, ArrayList<String>> entry = it.next();
			if (entry.getValue().size() >= KeyList.GKEY_DEVICE_ARRAY.length) {
				String name = getRoomName(entry.getKey(), roomInfos);
				tempRoom.add(name);
			}
		}
		ArrayList<String> rooms = new ArrayList<String>(Arrays.asList(KeyList.GKEY_ROOM_NAME_ARRAY));
		rooms.removeAll(tempRoom);
		return rooms;
	}

	/**
	 * 显示列表对话框(参数不同)
	 * 
	 * @param data
	 *            String[]
	 * @param key
	 */
	private void showListDialog(String[] data, final TextView textView, final boolean isDevice) {
		mListData = Arrays.asList(data);
		ListDialog dialog = new ListDialog(this);
		dialog.setAdpter(new ListApdater(context, mListData), new ListDialog.OnListItemClickListener() {
			@Override
			public void onListItemClick(int position) {
				// TODO Auto-generated method stub
				textView.setText(mListData.get(position));
			}
		});
		dialog.show();
	}

	/**
	 * 显示列表对话框(参数不同)
	 * 
	 * @param data
	 *            List<String>
	 * @param key
	 */
	private void showListDialog(List<String> data, final TextView textView, final boolean isDevice) {
		mListData = data;
		ListDialog dialog = new ListDialog(this);
		dialog.setAdpter(new ListApdater(context, mListData), new ListDialog.OnListItemClickListener() {
			@Override
			public void onListItemClick(int position) {
				// TODO Auto-generated method stub
				textView.setText(mListData.get(position));
				if (mRoomTv == textView) {
					List<String> arr = getOkDeviceList(mListData.get(position));
					if (arr != null) {
						mDeviceTv.setText(arr.get(0));
					}
				}
			}
		});
		dialog.show();
	}

	private void update() {
		ToastUtils.show(this, R.string.ba_setting_toast);
		WifiCRUDForUpdate update = new WifiCRUDForUpdate(getBoxIp(), getBoxTcpPort());
		WifiUpdateInfo info = new WifiUpdateInfo();
		info.setRoomName(mRoomName);
		info.setDeviceName(mDeviceName);
		// info.setOldRoomName(mOldRoomName);
		// info.setOldDeviceName(mWifiDeviceInfo.getDeviceName());
		info.setWifiDeviceInfo(mWifiDeviceInfo);

		update.update(info, new WifiCRUDForUpdate.ResultForUpdateListener() {
			@Override
			public void onResult(String type, String errorCode, List<WifiUpdateInfo> infos) {
				// TODO Auto-generated method stub
				LogManager.e("errorCode=" + errorCode);
				ToastUtils.cancel();
				if (WifiCRUDUtil.isSuccessAll(errorCode)) {
					LogManager.i("WifiCRUDForUpdate success");
					startToActvitiy(PartsManagerActivity.class);
				} else {
					LogManager.i("WifiCRUDForUpdate error");
					ToastUtils.show(context, R.string.ba_config_box_info_error_toast);
				}

			}
		});
	}

	private WifiCRUDForRoom mWifiCRUDForRoom;
	private WifiCRUDForDevice mWifiCRUDForDevice;

	@Override
	public void requestBox() {
		// TODO Auto-generated method stub
		mProgressDialog.setMessage(getString(R.string.ba_setting_toast));
		mProgressDialog.show();

		new Thread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub

				// add room
				mWifiCRUDForRoom = new WifiCRUDForRoom(context, getBoxIp(), getBoxTcpPort());
				mWifiRoomInfo = new WifiRoomInfo();
				mWifiRoomInfo.setRoomName(mRoomName);
				mWifiCRUDForRoom.add(mWifiRoomInfo, new ResultListener() {
					@Override
					public void onResult(String type, String errorCode, List<WifiRoomInfo> info) {
						// TODO Auto-generated method stub
						if (WifiCRUDUtil.isSuccessAll(errorCode)) {
							LogManager.i("add room ok");
							final String roomId = info.get(0).getRoomId();
							// add device
							mWifiCRUDForDevice = new WifiCRUDForDevice(context, getBoxIp(), getBoxTcpPort());
							mWifiDeviceInfo = new WifiDeviceInfo();

							mWifiDeviceInfo.setRoomid(info.get(0).getRoomId());
							mWifiDeviceInfo.setDeviceName(mDeviceName);
							mWifiDeviceInfo.setFitting(mFittingName);
							mWifiDeviceInfo.setMacadd(mMacAddress);
							mWifiDeviceInfo.setDeviceType(mDeviceType);
							mWifiDeviceInfo.setDeviceModel(mDeviceModel);

							mWifiCRUDForDevice.add(mWifiDeviceInfo, new WifiCRUDForDevice.ResultListener() {
								@Override
								public void onResult(String type, String errorCode, List<WifiDeviceInfo> info) {
									// TODO Auto-generated method stub
									if (WifiCRUDUtil.isSuccess(errorCode)) {

										LogManager.i("add device ok");
										deleteConfigDevice();
										addTwoCommand(roomId, info.get(0).getDeviceid());
										WaitUtils.sleep(500);
										runOnUiThread(new Runnable() {

											@Override
											public void run() {
												// TODO Auto-generated method
												// stub
												if (mProgressDialog != null && !WifiSingleAcivity.this.isFinishing())
													mProgressDialog.dismiss();
											}
										});
										startToActvitiy(PartsManagerActivity.class);

									} else if (errorCode.equals(WifiCreateAndParseSockObjectManager.WIFI_INFO_REPEAT)) {
										dismissDialog();
										ToastUtils.show(context, R.string.ba_box_have_info_toast);

									} else {
										LogManager.e("add device error");
										dismissDialog();
										ToastUtils.show(context, R.string.ba_config_box_info_error_toast);

									}
								}

							});

						} else {
							LogManager.e("add room error");
							dismissDialog();
							ToastUtils.show(context, R.string.ba_config_box_info_error_toast);
						}
					}
				});
			}
		}).start();
	}

	private void deleteConfigDevice() {
		ArrayList<WifiDeviceInfo> deviceList = mGetNewDevice.getNewDeviceByUdp();
		for (int i = deviceList.size() - 1; i >= 0; i--) {
			WifiDeviceInfo dinfo = deviceList.get(i);
			if (dinfo.getMacadd().equals(mMacAddress)) {
				deviceList.remove(i);
			}
		}
		String json = "";
		if (deviceList.isEmpty()) {
			json = "";
		} else {
			try {
				WifiDeviceInfos infos = new WifiDeviceInfos();
				infos.setWifiInfo(deviceList);
				json = new Gson().toJson(infos);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		Intent intent = new Intent(KeyList.AKEY_NEW_DEVICE_LIST_ACTION);
		intent.putExtra(KeyList.KEY_DEVICE_IP, getBoxIp());
		intent.putExtra(KeyList.IKEY_NEW_DEVICE_LIST, json);
		sendBroadcast(intent);
	}

	private void addTwoCommand(String roomId, String deviceId) {
		final WifiCRUDForControl mControl = new WifiCRUDForControl(context, getBoxIp(), getBoxTcpPort());
		mWifiControlInfo = new WifiControlInfo();
		mWifiControlInfo.setRoomId(roomId);
		mWifiControlInfo.setDeviceid(deviceId);
		mWifiControlInfo.setDeviceModel(mDeviceModel);
		mWifiControlInfo.setAction(DataUtil.formatAction(KeyList.GKEY_OPERATION_DEVICE_ARRAY[0], mDeviceName));
		mWifiControlInfo.setDorder("1");

		mControl.add(mWifiControlInfo, new WifiCRUDForControl.ResultListener() {
			@Override
			public void onResult(String type, String errorCode, List<WifiControlInfo> info) {
				// TODO Auto-generated method stub
				if (WifiCRUDUtil.isSuccessAll(errorCode)) {
					LogManager.i("add open control ok");

					mWifiControlInfo.setAction(DataUtil.formatAction(KeyList.GKEY_OPERATION_DEVICE_ARRAY[1], mDeviceName));
					mWifiControlInfo.setDorder("0");

					mControl.add(mWifiControlInfo, new WifiCRUDForControl.ResultListener() {
						@Override
						public void onResult(String type, String errorCode, List<WifiControlInfo> info) {
							// TODO Auto-generated method stub
							if (WifiCRUDUtil.isSuccessAll(errorCode)) {
								LogManager.i("add close control ok");

							} else {
								LogManager.i("add close control error");
								ToastUtils.show(context, R.string.ba_add_device_error_toast);
							}

						}
					});
				} else {
					LogManager.i("add open control error");
					ToastUtils.show(context, R.string.ba_add_device_error_toast);
				}

			}
		});
	}

	@Override
	protected void onDestroy() {
		if (mProgressDialog != null && !this.isFinishing())
			mProgressDialog.dismiss();
		mProgressDialog = null;
		super.onDestroy();
	}
}
