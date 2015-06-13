package com.iii360.box.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.ListView;

import com.iii.wifi.dao.info.WifiControlInfo;
import com.iii.wifi.dao.info.WifiDeviceInfo;
import com.iii.wifi.dao.info.WifiRoomInfo;
import com.iii.wifi.dao.manager.WifiCRUDForControl;
import com.iii.wifi.dao.manager.WifiCRUDForDevice;
import com.iii.wifi.dao.manager.WifiCRUDForDevice.ResultListener;
import com.iii.wifi.dao.manager.WifiCRUDForRoom;
import com.iii360.box.R;
import com.iii360.box.adpter.DetailRoomListApdater;
import com.iii360.box.base.BaseActivity;
import com.iii360.box.util.BoxManagerUtils;
import com.iii360.box.util.DataUtil;
import com.iii360.box.util.KeyList;
import com.iii360.box.util.LogManager;
import com.iii360.box.util.ToastUtils;
import com.iii360.box.util.WifiCRUDUtil;
import com.iii360.box.view.IView;

/**
 * 房间设备详细信息
 * 
 * @author hefeng
 * 
 */
public class DetailRoomActivity extends BaseActivity implements IView {
	private ListView mDeatilLv;
	private String mRoomName;
	private String mRoomId;
	private List<WifiDeviceInfo> mDeviceInfoList;
	private long startTime;
	private HashMap<String, ArrayList<String>> roomdevices;
	protected List<WifiControlInfo> controlInfos;
	protected List<WifiRoomInfo> roomInfos;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_detail_room);
		this.initViews();
		this.initDatas();
	}

	@Override
	public void initViews() {
		// TODO Auto-generated method stub
		mDeatilLv = (ListView) findViewById(R.id.detail_room_lv);
		mDeatilLv.setSelector(new ColorDrawable(Color.TRANSPARENT));

	}

	public void getRoomInfoAndControlInfo() {
		startTime = System.currentTimeMillis();

		final WifiCRUDForRoom mWifiCRUDForRoom = new WifiCRUDForRoom(context, getBoxIp(), getBoxTcpPort());
		final WifiCRUDForControl mWifiCRUDForControl = new WifiCRUDForControl(context, getBoxIp(), getBoxTcpPort());
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
											DetailRoomActivity.this.roomInfos = roomInfos;
											DetailRoomActivity.this.controlInfos = controlInfos;
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
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		count = 0;
		getDeviceInfo();
		getRoomInfoAndControlInfo();
	}

	@Override
	public void initDatas() {
		// TODO Auto-generated method stub
		Intent intent = getIntent();
		mRoomId = intent.getStringExtra(KeyList.IKEY_ROOM_ID);
		mRoomName = intent.getStringExtra(KeyList.IKEY_ROOM_NAME);
		this.setViewHead(mRoomName);
		ToastUtils.show(context, R.string.ba_update_date);
	}

	private void getDeviceInfo() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				WifiCRUDForDevice wDevice = new WifiCRUDForDevice(context, getBoxIp(), BoxManagerUtils.getBoxTcpPort(context));
				// 通过roomId获取设备信息
				wDevice.seleteByRoomId(mRoomId, new ResultListener() {
					@Override
					public void onResult(String type, String errorCode, final List<WifiDeviceInfo> list) {
						// TODO Auto-generated method stub
						if (WifiCRUDUtil.isSuccessAll(errorCode)) {
							mDeviceInfoList = list;
							mToastMapList = new HashMap<Integer, List<String>>();
							getToastList();
						} else {
							ToastUtils.show(context, R.string.ba_get_info_error_toast);

						}
					}
				});
			}
		}).start();
	}

	private int count = 0;
	private Map<Integer, List<String>> mToastMapList;
	private List<String> mToastList;

	private void getToastList() {
		if (mDeviceInfoList == null || mDeviceInfoList.isEmpty()) {
			return;
		}
		if (count == mDeviceInfoList.size() - 1) {
			mHandler.sendEmptyMessage(0);
		}
		if (count < mDeviceInfoList.size()) {
			mToastList = new ArrayList<String>();
			String deviceId = mDeviceInfoList.get(count).getDeviceid();
			count++;
			WifiCRUDForControl mControl = new WifiCRUDForControl(context, getBoxIp(), getBoxTcpPort());
			mControl.seleteByDeviceId(deviceId, new WifiCRUDForControl.ResultListener() {
				@Override
				public void onResult(String type, String errorCode, List<WifiControlInfo> info) {
					// TODO Auto-generated method stub
					if (WifiCRUDUtil.isSuccessAll(errorCode)) {

						for (WifiControlInfo infos : info) {
							// 主要是分开“开/关”是2条指令
							if (infos.getAction().contains(KeyList.GKEY_OPERATION_DEVICE_ARRAY[2])) {
								mToastList.add("小智,打开" + mRoomName + DataUtil.getDeviceName(infos.getAction()));
								mToastList.add("小智,关闭" + mRoomName + DataUtil.getDeviceName(infos.getAction()));
							} else {
								try {
									String[] temp = infos.getAction().split(KeyList.SEPARATOR_ACTION_SUBLIT);
									if (temp.length > 1) {
										mToastList.add("小智," + temp[0] + mRoomName + temp[1]);
									}
								} catch (Exception e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
									mToastList.add("小智," + infos.getAction());
								}

							}
						}

					} else {
						ToastUtils.show(context, R.string.ba_get_info_error_toast);
						LogManager.i("get data error");

					}
					mToastMapList.put(count - 1, mToastList);
					getToastList();
				}
			});
		}
	}

	public ArrayList<String> getOkRoomList(WifiDeviceInfo deviceInfo) {
		if (roomdevices == null)
			return null;
		String[] devices = deviceInfo.getDeviceName().split(KeyList.SEPARATOR);
		ArrayList<String> resource = new ArrayList<String>(Arrays.asList(KeyList.GKEY_ROOM_NAME_ARRAY));
		resource.remove(getRoomName(deviceInfo.getRoomid(), roomInfos));
		Iterator<Entry<String, ArrayList<String>>> it = roomdevices.entrySet().iterator();
		while (it.hasNext()) {
			Entry<String, ArrayList<String>> entry = it.next();
			ArrayList<String> temp = entry.getValue();
			inner: for (int i = 0; i < devices.length; i++) {
				if (temp.contains(devices[i])) {
					resource.remove(getRoomName(entry.getKey(), roomInfos));
					break inner;
				}
			}
		}
		return resource;
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

	Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			try {
				mDeatilLv.setAdapter(new DetailRoomListApdater(DetailRoomActivity.this, mDeviceInfoList, mRoomName, mToastMapList,
						new ArrayList<String>()));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			ToastUtils.cancel();
		}

	};
}
