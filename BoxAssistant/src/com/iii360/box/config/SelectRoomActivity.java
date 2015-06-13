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
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.iii.wifi.dao.info.WifiControlInfo;
import com.iii.wifi.dao.info.WifiDeviceInfo;
import com.iii.wifi.dao.info.WifiRoomInfo;
import com.iii.wifi.dao.manager.WifiCRUDForControl;
import com.iii.wifi.dao.manager.WifiCRUDForRoom;
import com.iii.wifi.dao.manager.WifiCRUDForRoom.ResultListener;
import com.iii360.box.R;
import com.iii360.box.adpter.ListApdater;
import com.iii360.box.base.BaseActivity;
import com.iii360.box.util.KeyList;
import com.iii360.box.util.LogManager;
import com.iii360.box.util.ToastUtils;
import com.iii360.box.util.WifiCRUDUtil;
import com.iii360.box.view.ListDialog;
import com.iii360.box.view.MyProgressDialog;

/**
 * 选择机器狗所在的房间
 * 
 * @author hefeng
 * 
 */
public class SelectRoomActivity extends BaseActivity implements View.OnClickListener {
	private LinearLayout mSelectRoomLayout;
	private TextView mRoomNameTv;
	private Button mNextBtn;
	private List<String> mListData;
	private String mRoomName;
	private String mFitting;
	private String mMac;
	private String mDeviceModel;
	private TextView mSelectName;
	private MyProgressDialog mProgressDialog;
	private long startTime;
	private WifiCRUDForRoom mWifiCRUDForRoom;
	private WifiCRUDForControl mWifiCRUDForControl;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_select_room);

		this.setViewHead("选择房间");
		this.mSelectRoomLayout = (LinearLayout) findViewById(R.id.dog_room_layout);
		this.mRoomNameTv = (TextView) findViewById(R.id.dog_room_tv);
		this.mNextBtn = (Button) findViewById(R.id.dog_next_btn);
		this.mSelectName = (TextView) findViewById(R.id.select_name_tv);
		this.mSelectRoomLayout.setOnClickListener(this);
		this.mNextBtn.setOnClickListener(this);
		mProgressDialog = new MyProgressDialog(context);
		mProgressDialog.setMessage(getString(R.string.ba_setting_toast));
		mProgressDialog.show();
		getRoomInfoAndControlInfo();
		this.getData();
	}

	private List<WifiRoomInfo> roomInfos;
	private List<WifiControlInfo> controlInfos;

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
											SelectRoomActivity.this.roomInfos = roomInfos;
											SelectRoomActivity.this.controlInfos = controlInfos;
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

	private Map<String, ArrayList<String>> roomdevices;
	private Map<String, ArrayList<String>> roomControls;

	private static String getRoomName(String roomId, List<WifiRoomInfo> roomInfos) {
		for (int i = 0; i < roomInfos.size(); i++) {
			WifiRoomInfo roominfo = roomInfos.get(i);
			if (roominfo.getRoomId().equals(roomId)) {
				return roominfo.getRoomName();
			}
		}
		return "";
	}

	private ArrayList<String> roomids;
	private Handler handler = new Handler();
	private ArrayList<String> roomNames;

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
		handler.post(new Runnable() {
			public void run() {
				if (mProgressDialog != null && !isFinishing()) {
					mProgressDialog.dismiss();
				}
				List<String> roomds = getOkRoomList();
				if (roomds == null)
					return;
				mRoomNameTv.setText(roomds.get(0));
			}
		});
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

	@Override
	protected void onDestroy() {
		if (mProgressDialog != null && !this.isFinishing())
			mProgressDialog.dismiss();
		mProgressDialog = null;
		super.onDestroy();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (v == this.mSelectRoomLayout) {
			String name = mRoomNameTv.getText().toString().trim();
			if (TextUtils.isEmpty(name)) {
				mProgressDialog.setMessage(getString(R.string.ba_setting_toast));
				mProgressDialog.show();
				getRoomInfoAndControlInfo();
			} else {
				List<String> list = getOkRoomList();
				if (list == null)
					return;
				showListDialog(list, this.mRoomNameTv);
			}

		} else if (v == this.mNextBtn) {

			mRoomName = mRoomNameTv.getText().toString();
			if (TextUtils.isEmpty(mRoomName)) {
				ToastUtils.show(context, "请选择房间");
				return;
			}
			ToastUtils.show(context, R.string.ba_setting_toast);
			WifiCRUDForRoom mWifiCRUDForRoom = new WifiCRUDForRoom(context, getBoxIp(), getBoxTcpPort());
			WifiRoomInfo mWifiRoomInfo = new WifiRoomInfo();
			mWifiRoomInfo.setRoomName(mRoomName);
			mWifiCRUDForRoom.add(mWifiRoomInfo, new ResultListener() {
				@Override
				public void onResult(String type, String errorCode, final List<WifiRoomInfo> info) {
					// TODO Auto-generated method stub
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
		}
	}

	/**
	 * 显示列表对话框
	 * 
	 * @param data
	 * @param key
	 */
	private void showListDialog(List<String> data, final TextView textView) {
		mListData = data;
		ListDialog dialog = new ListDialog(this);
		dialog.setAdpter(new ListApdater(context, mListData), new ListDialog.OnListItemClickListener() {
			@Override
			public void onListItemClick(int position) {
				// TODO Auto-generated method stub
				mRoomName = mListData.get(position);
				textView.setText(mRoomName);
			}
		});
		dialog.show();
	}

	private WifiDeviceInfo mWifiDeviceInfo;

	public void getData() {
		mWifiDeviceInfo = (WifiDeviceInfo) getIntent().getSerializableExtra(KeyList.IKEY_WIFIDEVICEINFO_ENTITY);
		mFitting = mWifiDeviceInfo.getFitting();
		mMac = mWifiDeviceInfo.getMacadd();
		mDeviceModel = mWifiDeviceInfo.getDeviceModel();

		this.mSelectName.setText("选择" + mWifiDeviceInfo.getFitting() + "所处房间");
		if (!TextUtils.isEmpty(mRoomName)) {
			mRoomNameTv.setText(mRoomName);
		} else {
			mRoomNameTv.setText("");
		}

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
				intent.putExtra(KeyList.IKEY_DEVICE_CONTROL_LIST,
						roomControls.get(info.getRoomId()) == null ? new ArrayList<String>() : roomControls.get(info.getRoomId()));
				startActivity(intent);
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
			}
		} else {
			Intent intent = new Intent(context, AirStudyActivity.class);
			intent.putExtra(KeyList.IKEY_WIFIDEVICEINFO_ENTITY, mWifiDeviceInfo);
			intent.putExtra(KeyList.IKEY_ROOM_ID, info.getRoomId());
			LogManager.i("创建房间成功！mFitting=" + mFitting + "||mRoomName=" + mRoomName + "||roomId=" + info.getRoomId());
			startActivity(intent);
		}

	}

	/***
	 * 跳入学习电视
	 * 
	 * @param roominfo
	 */
	private void jumpToTv(final WifiRoomInfo roominfo) {
		// TODO Auto-generated method stub

		Intent intent = new Intent(context, TvStudyActivity.class);
		intent.putExtra(KeyList.IKEY_WIFIDEVICEINFO_ENTITY, mWifiDeviceInfo);
		intent.putExtra(KeyList.IKEY_ROOM_ID, roominfo.getRoomId());
		LogManager.i("创建房间成功！mFitting=" + mFitting + "||mRoomName=" + mRoomName + "||roomId=" + roominfo.getRoomId());
		startActivity(intent);

	}
}
