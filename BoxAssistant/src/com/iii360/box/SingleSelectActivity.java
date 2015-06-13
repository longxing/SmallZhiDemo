package com.iii360.box;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.iii.wifi.dao.info.WifiControlInfo;
import com.iii.wifi.dao.info.WifiDeviceInfo;
import com.iii.wifi.dao.info.WifiRoomInfo;
import com.iii.wifi.dao.manager.WifiCRUDForControl;
import com.iii.wifi.dao.manager.WifiCRUDForRoom;
import com.iii.wifi.dao.manager.WifiCRUDForRoom.ResultListener;
import com.iii360.box.adpter.UserInfoAdapter;
import com.iii360.box.base.BaseActivity;
import com.iii360.box.config.AddDeviceActivity;
import com.iii360.box.config.AirStudyActivity;
import com.iii360.box.config.TvStudyActivity;
import com.iii360.box.config.WifiSingleAcivity;
import com.iii360.box.util.KeyList;
import com.iii360.box.util.LogManager;
import com.iii360.box.util.ToastUtils;
import com.iii360.box.util.WifiCRUDUtil;
import com.iii360.box.view.MyProgressDialog;

public class SingleSelectActivity extends BaseActivity {
	public static final int UNCONFIG_DEVICE_SELECT = 10;
	public static final int ROOM_SELECT = 11;
	private int type;
	private ListView listView;
	private UserInfoAdapter adapter;
	private Handler handler = new Handler();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_single_select);
		getIntentData();
		setupView();
		addListeners();
	}

	private MyProgressDialog myProgressDialog;
	private long startTime;
	private WifiCRUDForRoom mWifiCRUDForRoom;
	private WifiCRUDForControl mWifiCRUDForControl;
	protected List<WifiRoomInfo> roomInfos;
	protected List<WifiControlInfo> controlInfos;
	private HashMap<String, ArrayList<String>> roomdevices;
	private HashMap<String, ArrayList<String>> roomControls;
	private ArrayList<String> roomids;
	private ArrayList<String> roomNames;
	private WifiDeviceInfo mWifiDeviceInfo;
	private String mFitting;
	private String mMac;
	private String mDeviceModel;
	private String mRoomName;

	private void addListeners() {
		listView.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if (type == ROOM_SELECT) {
					mRoomName = adapter.getKeys()[position];
					showProgressDialog(getString(R.string.ba_setting_toast));
					WifiCRUDForRoom mWifiCRUDForRoom = new WifiCRUDForRoom(context, getBoxIp(), getBoxTcpPort());
					WifiRoomInfo mWifiRoomInfo = new WifiRoomInfo();
					mWifiRoomInfo.setRoomName(mRoomName);
					mWifiCRUDForRoom.add(mWifiRoomInfo, new ResultListener() {
						@Override
						public void onResult(String type, String errorCode, final List<WifiRoomInfo> info) {
							// TODO Auto-generated method stub
							dismissProgressDialog(false);
							if (WifiCRUDUtil.isSuccessAll(errorCode)) {
								handler.post(new Runnable() {
									public void run() {
										putData(info.get(0));
									}
								});

							} else {
								LogManager.e("创建房间失败！");
								ToastUtils.show(context, R.string.ba_config_box_info_error_toast);
							}
						}
					});
				} else if (type == UNCONFIG_DEVICE_SELECT) {
					ArrayList<WifiDeviceInfo> unconfigBeans = (ArrayList<WifiDeviceInfo>) getIntent().getSerializableExtra(KeyList.PKEY_UNCONFIG_LIST);
					WifiDeviceInfo unconfigBean = unconfigBeans.get(position);
					toSetting(unconfigBean);
				}
			}
		});
	}

	/**
	 * 设置数据
	 * 
	 * @param fitting
	 * @param deviceType
	 */
	private void toSetting(WifiDeviceInfo info) {

		if (TextUtils.isEmpty(info.getFitting())) {
			ToastUtils.show(context, "获取配件数据异常，请重新再试...");
			return;
		}

		// 去除已经配置的房间名称
		// roomNames = new ArrayList<String>();

		if (info.getDeviceType() == KeyList.WIFI_UNSINGLE_DEVICE) {

			Intent intent = new Intent(context, SingleSelectActivity.class);
			intent.putExtra(KeyList.PKEY_SINGLE_SELECT_TYPE, SingleSelectActivity.ROOM_SELECT);
			intent.putExtra(KeyList.IKEY_WIFIDEVICEINFO_ENTITY, info);
			intent.putExtra(KeyList.IKEY_EXISTS_ROOM_NAME, roomNames);
			intent.putExtra(KeyList.IKEY_EXISTS_ROOM_IDS, roomids);

			startActivity(intent);

		} else if (info.getDeviceType() == KeyList.WIFI_SINGLE_DEVICE) {
			Intent intent = new Intent(context, WifiSingleAcivity.class);
			intent.putExtra(KeyList.IKEY_WIFIDEVICEINFO_ENTITY, info);
			intent.putExtra(KeyList.IKEY_EXISTS_ROOM_NAME, roomNames);
			intent.putExtra(KeyList.IKEY_EXISTS_ROOM_IDS, roomids);
			startActivity(intent);

		} else {
			LogManager.e("既不是单品又不是机器狗");
		}
		finish();
	}

	private void setupView() {
		listView = (ListView) findViewById(R.id.single_select_listview);
		if (type == UNCONFIG_DEVICE_SELECT) {
			setViewHead("选择设备");
			ArrayList<String> list = getIntent().getStringArrayListExtra(KeyList.PKEY_UNCONFIG_FITTING_LIST);
			if (handler != null)
				handler.post(new showListTask(listToArray(list), null));
		} else if (type == ROOM_SELECT) {
			setViewHead("选择房间");
			showProgressDialog(getString(R.string.ba_setting_toast));
			getRoomInfoAndControlInfo();
			mWifiDeviceInfo = (WifiDeviceInfo) getIntent().getSerializableExtra(KeyList.IKEY_WIFIDEVICEINFO_ENTITY);
			mFitting = mWifiDeviceInfo.getFitting();
			mMac = mWifiDeviceInfo.getMacadd();
			mDeviceModel = mWifiDeviceInfo.getDeviceModel();
		}
	}

	private void getIntentData() {
		type = getIntent().getIntExtra(KeyList.PKEY_SINGLE_SELECT_TYPE, 0);
		if (type == 0)
			finish();
	}

	private void showProgressDialog(final String msg) {
		if (handler == null)
			return;
		handler.post(new Runnable() {

			public void run() {
				if (myProgressDialog == null) {
					myProgressDialog = new MyProgressDialog(context);
				}
				myProgressDialog.setMessage(msg);
				myProgressDialog.show();
			}
		});
	}

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
											SingleSelectActivity.this.roomInfos = roomInfos;
											SingleSelectActivity.this.controlInfos = controlInfos;
											parseRoomAndControl(roomInfos, controlInfos);
										}

									} else {
										ToastUtils.show(context, R.string.ba_get_data_error_toast);
										LogManager.i("get data error");
										dismissProgressDialog(false);
									}
								}

							});
						} else {
							ToastUtils.show(context, R.string.ba_get_data_error_toast);
							dismissProgressDialog(false);
						}
					}
				});
			}
		}).start();
	}

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
		roomControls = new HashMap<String, ArrayList<String>>();
		roomids = new ArrayList<String>();
		roomNames = new ArrayList<String>();
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
			ArrayList<String> controlList = roomControls.get(roomid);
			if (controlList == null) {
				controlList = new ArrayList<String>();
				controlList.add(info.getAction());
				roomControls.put(roomid, controlList);
			} else {
				controlList.add(info.getAction());
			}

			String roomName = getRoomName(roomid, roomInfos);
			if (!roomNames.contains(roomName))
				roomNames.add(roomName);
			if (!roomids.contains(roomid))
				roomids.add(roomid);
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
		dismissProgressDialog(false);
		List<String> roomsList = getOkRoomList();
		String[] roomsArray = listToArray(roomsList);
		if (handler != null)
			handler.post(new showListTask(roomsArray, null));
	}

	private String[] listToArray(List<String> roomsList) {
		if (roomsList == null)
			return null;
		String[] arr = new String[roomsList.size()];
		for (int i = 0; i < roomsList.size(); i++) {
			arr[i] = roomsList.get(i);
		}
		return arr;
	}

	private class showListTask implements Runnable {

		private String[] values;
		private String[] keys;

		public showListTask(String[] keys, String[] values) {
			this.keys = keys;
			this.values = values;
		}

		@Override
		public void run() {
			if (keys == null)
				return;
			adapter = new UserInfoAdapter(keys, values, context, listView);
			listView.setAdapter(adapter);
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

	private void dismissProgressDialog(final boolean isSetNull) {
		if (handler == null)
			return;
		handler.post(new Runnable() {
			public void run() {
				if (myProgressDialog != null && !isFinishing()) {
					myProgressDialog.dismiss();
				}
				if (isSetNull)
					myProgressDialog = null;
			}
		});
	}

	private void putData(WifiRoomInfo info) {
		if (roomdevices.containsKey(info.getRoomId())) {
			ArrayList<String> list = roomdevices.get(info.getRoomId());
			if (list.contains(KeyList.GKEY_DEVICE_ARRAY[0]) && list.contains(KeyList.GKEY_DEVICE_ARRAY[1])) {
				/***
				 * 空调和电视都被消耗了
				 */
				mWifiDeviceInfo.setRoomid(info.getRoomId());
				Intent intent = new Intent(context, AddDeviceActivity.class);
				intent.putExtra(KeyList.IKEY_DEVICEINFO_BEAN, mWifiDeviceInfo);
				intent.putExtra(KeyList.IKEY_DEVICE_CONTROL_LIST, roomControls.get(info.getRoomId()) == null ? new ArrayList<String>() : roomControls.get(info.getRoomId()));
				startActivity(intent);
				finish();
			} else if (list.contains(KeyList.GKEY_DEVICE_ARRAY[1]) || list.contains(KeyList.GKEY_DEVICE_ARRAY[0])) {
				if (list.contains(KeyList.GKEY_DEVICE_ARRAY[1])) {
					/***
					 * 空调被消耗了
					 */
					jumpToTv(info);
				} else {
					/**
					 * 电视被消耗了,跳到空调页面多传一个值用来标志已经电视被消耗了
					 */
					Intent intent = new Intent(context, AirStudyActivity.class);
					intent.putExtra(KeyList.IKEY_WIFIDEVICEINFO_ENTITY, mWifiDeviceInfo);
					intent.putExtra(KeyList.KEY_IS_TV_CONFIG, "yes");
					intent.putExtra(KeyList.IKEY_ROOM_ID, info.getRoomId());
					LogManager.i("创建房间成功！mFitting=" + mFitting + "||mRoomName=" + mRoomName + "||roomId=" + info.getRoomId());
					startActivity(intent);
					finish();
				}

			} else {
				/***
				 * 这种情况包含了空调没有被消耗，电视没有被消耗
				 */
				Intent intent = new Intent(context, AirStudyActivity.class);
				intent.putExtra(KeyList.IKEY_WIFIDEVICEINFO_ENTITY, mWifiDeviceInfo);
				intent.putExtra(KeyList.IKEY_ROOM_ID, info.getRoomId());
				LogManager.i("创建房间成功！mFitting=" + mFitting + "||mRoomName=" + mRoomName + "||roomId=" + info.getRoomId());
				startActivity(intent);
				finish();
			}
		} else {
			Intent intent = new Intent(context, AirStudyActivity.class);
			intent.putExtra(KeyList.IKEY_WIFIDEVICEINFO_ENTITY, mWifiDeviceInfo);
			intent.putExtra(KeyList.IKEY_ROOM_ID, info.getRoomId());
			LogManager.i("创建房间成功！mFitting=" + mFitting + "||mRoomName=" + mRoomName + "||roomId=" + info.getRoomId());
			startActivity(intent);
			finish();
		}

	}

	/***
	 * 跳入学习电视
	 * 
	 * @param roominfo
	 */
	private void jumpToTv(final WifiRoomInfo roominfo) {
		Intent intent = new Intent(context, TvStudyActivity.class);
		intent.putExtra(KeyList.IKEY_WIFIDEVICEINFO_ENTITY, mWifiDeviceInfo);
		intent.putExtra(KeyList.IKEY_ROOM_ID, roominfo.getRoomId());
		LogManager.i("创建房间成功！mFitting=" + mFitting + "||mRoomName=" + mRoomName + "||roomId=" + roominfo.getRoomId());
		startActivity(intent);
		finish();
	}

	@Override
	protected void onDestroy() {
		dismissProgressDialog(true);
		super.onDestroy();
		handler = null;
	}
}
