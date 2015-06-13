package com.iii360.box.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.iii.wifi.dao.info.WifiControlInfo;
import com.iii.wifi.dao.info.WifiDeviceInfo;
import com.iii.wifi.dao.info.WifiRoomInfo;
import com.iii.wifi.dao.manager.WifiCRUDForControl;
import com.iii.wifi.dao.manager.WifiCRUDForDevice;
import com.iii.wifi.dao.manager.WifiCRUDForDevice.ResultListener;
import com.iii.wifi.dao.manager.WifiCRUDForRoom;
import com.iii360.box.R;
import com.iii360.box.adpter.ListApdater;
import com.iii360.box.base.BaseActivity;
import com.iii360.box.data.ModeData;
import com.iii360.box.util.BoxManagerUtils;
import com.iii360.box.util.DataUtil;
import com.iii360.box.util.KeyList;
import com.iii360.box.util.LogManager;
import com.iii360.box.util.ToastUtils;
import com.iii360.box.util.WifiCRUDUtil;
import com.iii360.box.view.IView;
import com.iii360.box.view.ListDialog;

/**
 * 添加设备
 * 
 * @author hefeng
 * 
 */
public class AddDeviceActivity extends BaseActivity implements IView, View.OnClickListener {
	private RelativeLayout mSelectDeviceLayout;
	private RelativeLayout mOperationLayout;
	private TextView mAddDeviceTv;
	private TextView mActionTv;
	private Button mAddNextBtn;
	private List<String> mListData;
	private WifiDeviceInfo mDeviceInfo;
	private ArrayList<String> controlsString;
	private ArrayList<String> coupleList;
	private ArrayList<String> onList;
	private ArrayList<String> offList;
	private long startTime;

	// private boolean mStudy;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_device);
		this.initViews();
		this.initDatas();
	}

	@Override
	protected void onRestart() {
		super.onRestart();
		getRoomInfoAndControlInfo();
	}

	@Override
	public void initViews() {
		// TODO Auto-generated method stub
		mSelectDeviceLayout = (RelativeLayout) findViewById(R.id.select_device_layout);
		mOperationLayout = (RelativeLayout) findViewById(R.id.device_operation_layout);
		mAddDeviceTv = (TextView) findViewById(R.id.add_device_tv);
		mActionTv = (TextView) findViewById(R.id.operation_device_tv);
		mAddNextBtn = (Button) findViewById(R.id.add_device_next_btn);

		mSelectDeviceLayout.setOnClickListener(this);
		mOperationLayout.setOnClickListener(this);
		mAddNextBtn.setOnClickListener(this);
	}

	@Override
	public void initDatas() {
		// TODO Auto-generated method stub
		this.setViewHead("添加设备");
		
		this.mDeviceInfo = (WifiDeviceInfo) getIntent().getSerializableExtra(KeyList.IKEY_DEVICEINFO_BEAN);
		getRoomInfoAndControlInfo();
	}

	private Handler handler = new Handler();
	private Runnable inputDataTask = new Runnable() {
		public void run() {
			mAddDeviceTv.setText(getOkDeviceList().get(0));
			mActionTv.setText(getOkOperationList(getOkDeviceList().get(0)) == null ? "" : getOkOperationList(getOkDeviceList().get(0))[0]);
		}
	};

	private void getRoomInfoAndControlInfo() {
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
											HashMap<String, ArrayList<String>> roomControls = ModeData.parseRoomAndControl(roomInfos, controlInfos);
											controlsString = roomControls.get(mDeviceInfo.getRoomid()) == null ? new ArrayList<String>()
													: roomControls.get(mDeviceInfo.getRoomid());
											handler.post(inputDataTask);
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

	/**
	 * 该方法在使用前要前过去时 controlsString
	 * 
	 * @return
	 */
	public List<String> getOkDeviceList() {
		List<String> list = new ArrayList<String>(Arrays.asList(KeyList.GKEY_DEVICE_ARRAY));
		List<String> temp = new ArrayList<String>(controlsString);
		coupleList = new ArrayList<String>();
		onList = new ArrayList<String>();
		offList = new ArrayList<String>();
		for (int i = 0; i < temp.size(); i++) {
			String[] arr = temp.get(i).split(KeyList.SEPARATOR_ACTION_SUBLIT);
			if (KeyList.GKEY_OPERATION_DEVICE_ARRAY[2].equals(arr[0])) {
				coupleList.add(arr[1]);
			} else if (KeyList.GKEY_OPERATION_DEVICE_ARRAY[0].equals(arr[0])) {
				// 打开
				onList.add(arr[1]);
			} else if (KeyList.GKEY_OPERATION_DEVICE_ARRAY[1].equals(arr[0])) {
				// 关闭
				offList.add(arr[1]);
			}
		}
		list.removeAll(coupleList);
		for (int i = 0; i < onList.size(); i++) {
			String onItem = onList.get(i);
			if (offList.contains(onItem)) {
				list.remove(onItem);
			}
		}
		return list;
	}

	public String[] getOkOperationList(String deviceName) {
		if (onList.contains(deviceName) && offList.contains(deviceName)) {
			return null;
		}
		if (onList.contains(deviceName)) {
			return new String[] { KeyList.GKEY_OPERATION_DEVICE_ARRAY[1] };
		}
		if (offList.contains(deviceName)) {
			return new String[] { KeyList.GKEY_OPERATION_DEVICE_ARRAY[0] };
		}
		return KeyList.GKEY_OPERATION_DEVICE_ARRAY;
		// return null;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (v == mSelectDeviceLayout) {
			if (controlsString == null) {
				getRoomInfoAndControlInfo();
			} else
				showListDialog(getOkDeviceList(), mAddDeviceTv);
		} else if (v == mOperationLayout) {
			if (TextUtils.isEmpty(mAddDeviceTv.getText().toString().trim())) {
				ToastUtils.show(context, "请选择设备");
				return;
			}
			String[] arr = getOkOperationList(mAddDeviceTv.getText().toString().trim());
			if (arr == null) {
				getRoomInfoAndControlInfo();
				ToastUtils.show(context, "设备已经配置，请重新选择设备");
				return;
			}
			showListDialog(Arrays.asList(arr), mActionTv);

		} else if (v == mAddNextBtn) {
			if (TextUtils.isEmpty(mAddDeviceTv.getText().toString().trim())) {
				ToastUtils.show(context, "请选择设备");
				mAddDeviceTv.setText("");
				mActionTv.setText("");
				return;
			}
			if (TextUtils.isEmpty(mActionTv.getText().toString().trim())) {
				mActionTv.setText("");
				ToastUtils.show(context, "请选择操作");
				return;
			}
			ToastUtils.show(context, R.string.ba_setting_toast);

			if (TextUtils.isEmpty(mDeviceInfo.getDeviceid())) {

				createDevice();
			} else {
				setControlDevice();
			}
		}
	}

	/**
	 * 从SelectRoomActivity直接跳入的,还没有deviceid
	 */
	private void createDevice() {

		WifiCRUDForDevice mWifiCRUDForDevice = new WifiCRUDForDevice(context, getBoxIp(), getBoxTcpPort());
		WifiDeviceInfo deviceinfo = new WifiDeviceInfo();
		deviceinfo.setRoomid(mDeviceInfo.getRoomid());
		deviceinfo.setMacadd(mDeviceInfo.getMacadd());
		deviceinfo.setFitting(mDeviceInfo.getFitting());
		deviceinfo.setDeviceType(mDeviceInfo.getDeviceType());
		deviceinfo.setDeviceModel(mDeviceInfo.getDeviceModel());
		deviceinfo.setDeviceName(mAddDeviceTv.getText().toString().trim());
		mWifiCRUDForDevice.add(deviceinfo, new WifiCRUDForDevice.ResultListener() {
			public void onResult(String type, String errorCode, List<WifiDeviceInfo> info) {
				if (WifiCRUDUtil.isSuccessAll(errorCode)) {
					mDeviceInfo.setDeviceid(info.get(0).getDeviceid());
					setControlDevice();
				} else {
					ToastUtils.show(context, R.string.ba_config_box_info_error_toast);
				}
			}
		});
	}

	private void setControlDevice() {
		mDeviceName = mAddDeviceTv.getText().toString();
		mAction = mActionTv.getText().toString();
		mNewAction = DataUtil.formatAction(mAction, mDeviceName);

		WifiCRUDForControl mControl = new WifiCRUDForControl(context, getBoxIp(), getBoxTcpPort());
		WifiControlInfo controlInfo = new WifiControlInfo();
		controlInfo.setAction(mNewAction);
		controlInfo.setDeviceid(mDeviceInfo.getDeviceid());
		controlInfo.setRoomId(mDeviceInfo.getRoomid());
		controlInfo.setDeviceModel(mDeviceInfo.getDeviceModel());

		mControl.add(controlInfo, new WifiCRUDForControl.ResultListener() {
			@Override
			public void onResult(String type, String errorCode, List<WifiControlInfo> list) {
				// TODO Auto-generated method stub
				if (WifiCRUDUtil.isSuccessAll(errorCode) && list != null && !list.isEmpty()) {
					LogManager.d("数据保存成功。。。");
					updateDevice(list.get(0));
				} else {
					LogManager.d("数据保存失败。。。");
					ToastUtils.show(context, R.string.ba_config_box_info_error_toast);
				}
			}
		});

	}

	private String mDeviceName;
	private String mAction;
	private String mNewAction;

	private void updateDevice(final WifiControlInfo controlInfo) {
		final WifiCRUDForDevice wDevice = new WifiCRUDForDevice(context, getBoxIp(), BoxManagerUtils.getBoxTcpPort(context));
		wDevice.seleteByDeviceId(mDeviceInfo.getDeviceid(), new ResultListener() {
			@Override
			public void onResult(String type, String errorCode, List<WifiDeviceInfo> list) {
				// TODO Auto-generated method stub
				if (WifiCRUDUtil.isSuccessAll(errorCode) && list != null && !list.isEmpty()) {
					String devString = list.get(0).getDeviceName();

					if (DataUtil.isExistDevice(devString, mDeviceName)) {

						LogManager.i("设备名称已经存在，不需要添加 devString=" + devString + "||add : " + mDeviceName);
						mDeviceInfo.setDeviceName(devString);
						if (TextUtils.isEmpty(controlInfo.getDorder())) {
							putIntentData(controlInfo, false);
						} else {
							putIntentData(controlInfo, true);
						}

					} else {
						if (isEmpty(devString)) {
							mDeviceInfo.setDeviceName(mDeviceName);
						} else {
							mDeviceInfo.setDeviceName(devString + KeyList.SEPARATOR + mDeviceName);
						}
						LogManager.i("设备名称不存在，需要添加 all devString=" + (devString + KeyList.SEPARATOR + mDeviceName) + "||add : " + mDeviceName);
						wDevice.updata(mDeviceInfo, new ResultListener() {
							@Override
							public void onResult(String type, String errorCode, List<WifiDeviceInfo> list) {
								// TODO Auto-generated method stub
								LogManager.i("update deviceName errorCode=" + errorCode);
								if (WifiCRUDUtil.isSuccessAll(errorCode)) {
									putIntentData(controlInfo, false);

								} else {
									ToastUtils.show(context, R.string.ba_config_box_info_error_toast);
								}
							}
						});
					}

				} else {

					ToastUtils.show(context, R.string.ba_config_box_info_error_toast);
				}
			}
		});
	}

	private void putIntentData(WifiControlInfo controlInfo, boolean isStudy) {
		Intent intent = new Intent(context, AddDeviceStudyActivity.class);
		intent.putExtra(KeyList.IKEY_DEVICE_IS_STUDY, isStudy);
		intent.putExtra(KeyList.IKEY_CONTROLINFO_BEAN, controlInfo);
		startActivity(intent);
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
				textView.setText(mListData.get(position));
				if (mAddDeviceTv == textView) {
					String[] arr = getOkOperationList(mAddDeviceTv.getText().toString().trim());
					if (arr == null) {
						getRoomInfoAndControlInfo();
						mActionTv.setText("");
					} else {
						mActionTv.setText(arr[0]);
					}

				}

			}
		});
		dialog.show();
	}
}
