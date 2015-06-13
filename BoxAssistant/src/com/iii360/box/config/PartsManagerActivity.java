package com.iii360.box.config;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.iii.wifi.dao.info.WifiDeviceInfo;
import com.iii.wifi.dao.info.WifiRoomInfo;
import com.iii.wifi.dao.manager.WifiCRUDForDevice;
import com.iii.wifi.dao.manager.WifiCRUDForRoom;
import com.iii.wifi.dao.manager.WifiCRUDForRoom.ResultListener;
import com.iii.wifi.dao.manager.WifiCreateAndParseSockObjectManager;
import com.iii360.box.R;
import com.iii360.box.SingleSelectActivity;
import com.iii360.box.adpter.MainListApdater;
import com.iii360.box.base.BaseActivity;
import com.iii360.box.common.BasePreferences;
import com.iii360.box.common.Invoke;
import com.iii360.box.util.KeyList;
import com.iii360.box.util.LogManager;
import com.iii360.box.util.ToastUtils;
import com.iii360.box.util.WifiCRUDUtil;
import com.iii360.box.view.IView;
import com.iii360.box.view.MyProgressDialog;
import com.iii360.box.view.NewViewHead;

/**
 * 配件管理
 * 
 * @author hefeng
 * 
 */
public class PartsManagerActivity extends BaseActivity implements IView, Invoke {
	private static final int HANDLER_UPDATE_NUMBER = 0;
	private static final int HANDLER_UPDATE_NUMBER_ERROR = 1;
	private static final int HANDLER_DISSMIS_DIALOG = 2;
	private static final int HANDLER_GET_NEW_DEVICE = 3;

	private ListView mPartsLv;
	private View mListViewHead;
	private TextView mLvHeadTitleTv;
	private List<WifiRoomInfo> mRoomInfoList;
	private int mCurrentPosition;
	private BasePreferences mBasePreferences;
	private TextView mPartsNumberTv;
	private ArrayList<WifiDeviceInfo> mDeviceInfoList;
	/**
	 * 配件名称
	 */
	private ArrayList<String> mFittingList;
	/**
	 * 配件名称+mac地址
	 */
	// private Map<String, String> mMap;
	private GetNewDevice mGetNewDevice;
	private MyProgressDialog mProgressDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_parts_manager);
		this.initViews();
		this.initDatas();
		this.requestBox();
	}

	@Override
	protected void onNewIntent(Intent intent) {
		// TODO Auto-generated method stub
		super.onNewIntent(intent);
		this.requestBox();

		// mDeviceInfoList = mGetNewDevice.getNewDeviceByUdp();
		// if (mDeviceInfoList != null && !mDeviceInfoList.isEmpty()) {
		// mHandler.sendEmptyMessage(HANDLER_UPDATE_NUMBER);
		//
		// } else {
		// mDeviceInfoList = new ArrayList<WifiDeviceInfo>();
		// mHandler.sendEmptyMessage(HANDLER_UPDATE_NUMBER_ERROR);
		// }

		LogManager.e("onNewIntent");
	}

	@Override
	protected void onRestart() {
		super.onRestart();
		this.requestBox();
	}

	@Override
	public void initViews() {
		// TODO Auto-generated method stub
		mPartsLv = (ListView) findViewById(R.id.manager_lv);
		mPartsNumberTv = (TextView) findViewById(R.id.ba_number_tv);
		mListViewHead = LayoutInflater.from(this).inflate(R.layout.view_main_listview_header, null);
		mLvHeadTitleTv = (TextView) mListViewHead.findViewById(R.id.main_big_tv);
	}

	// @SuppressWarnings("unchecked")
	@Override
	public void initDatas() {
		// TODO Auto-generated method stub
		// if (getIntent() != null) {
		// mDeviceInfoList = (ArrayList<WifiDeviceInfo>)
		// getIntent().getSerializableExtra(KeyList.IKEY_WIFI_DEVICES_INFO_LIST);
		// }
		//
		// if (mDeviceInfoList != null && !mDeviceInfoList.isEmpty()) {
		// mHandler.sendEmptyMessage(HANDLER_UPDATE_NUMBER);
		//
		// } else {
		// mDeviceInfoList = new ArrayList<WifiDeviceInfo>();
		// mHandler.sendEmptyMessage(HANDLER_UPDATE_NUMBER_ERROR);
		// }
		mHandler.sendEmptyMessage(HANDLER_UPDATE_NUMBER_ERROR);
		mHandler.sendEmptyMessage(HANDLER_GET_NEW_DEVICE);
		// mHandler.sendEmptyMessage(HANDLER_UPDATE_NUMBER);

		mProgressDialog = new MyProgressDialog(context);
		mBasePreferences = new BasePreferences(context);
		mGetNewDevice = new GetNewDevice(context);
		// mGetNewDevice.registerReceiver();
		// mGetNewDevice.getNewConfigDevice();
		// mGetNewDevice.setListener(new GetNewDevice.DeviceListener() {
		// @Override
		// public void onData(boolean isGet, List<WifiDeviceInfo> list) {
		// // TODO Auto-generated method stub
		// if (isGet) {
		// mDeviceInfoList = (ArrayList<WifiDeviceInfo>) list;
		// mHandler.sendEmptyMessage(HANDLER_UPDATE_NUMBER);
		// } else {
		//
		// mHandler.sendEmptyMessage(HANDLER_UPDATE_NUMBER_ERROR);
		// }
		// }
		// });
		NewViewHead.showAll(context, "智能设备", R.drawable.online_box_add_selector).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				// TODO Auto-generated method stub
				if (mDeviceInfoList == null || mDeviceInfoList.isEmpty()) {
					// // getNewConfigDevice();
					// mGetNewDevice.getNewConfigDevice();
					ToastUtils.show(context, "没有找到新的配件，请检查配件是否正常工作");
					return;
				}
				showListDialog(mDeviceInfoList);

			}
		});
	}

	// @Override
	// protected void onResume() {
	// // TODO Auto-generated method stub
	// super.onResume();
	// mGetNewDevice.getNewConfigDevice();
	// }
	private Gson gson = new Gson();
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			int what = msg.what;

			switch (what) {

			case HANDLER_UPDATE_NUMBER:
				mPartsNumberTv.setVisibility(View.VISIBLE);
				mPartsNumberTv.setText(mDeviceInfoList.size() + "");

				break;

			case HANDLER_UPDATE_NUMBER_ERROR:
				mPartsNumberTv.setVisibility(View.GONE);
				if (mDeviceInfoList != null) {
					mDeviceInfoList.clear();
				}

				break;

			case HANDLER_DISSMIS_DIALOG:
				if (mProgressDialog != null && !PartsManagerActivity.this.isFinishing())
					mProgressDialog.dismiss();

				break;
			case HANDLER_GET_NEW_DEVICE:
				// String unConfigDevice =
				// getPrefString(KeyList.IKEY_NEW_DEVICE_GSON);
				// WifiDeviceInfos infos = gson.fromJson(unConfigDevice,
				// WifiDeviceInfos.class);

				// if (infos == null) {
				// mHandler.sendEmptyMessage(HANDLER_UPDATE_NUMBER_ERROR);
				//
				// } else {
				mDeviceInfoList = mGetNewDevice.getNewDeviceByUdp();
				if (mDeviceInfoList == null || mDeviceInfoList.isEmpty()) {
					mHandler.sendEmptyMessage(HANDLER_UPDATE_NUMBER_ERROR);

				} else {
					mHandler.sendEmptyMessage(HANDLER_UPDATE_NUMBER);

				}
				// }
				mHandler.sendEmptyMessageDelayed(HANDLER_GET_NEW_DEVICE, 1000);
				break;

			default:
				break;
			}
		}

	};

	/**
	 * 显示列表对话框
	 * 
	 * @param data
	 * @param key
	 */
	private void showListDialog(final ArrayList<WifiDeviceInfo> list) {
		mFittingList = new ArrayList<String>();
		// mMap = new HashMap<String, String>();
		for (WifiDeviceInfo info : list) {
			mFittingList.add(info.getFitting());
			// mMap.put(info.getFitting(), info.getMacadd());
		}

		if (list.size() == 1) {
			toSetting(list.get(0));
			// toSetting(mFittingList.get(0),list.get(0).getDeviceType(),list.get(0).getMacadd());
			return;
		}

		// ListDialog dialog = new ListDialog(this);
		// dialog.setAdpter(new ListApdater(context, mFittingList), new
		// ListDialog.OnListItemClickListener() {
		// @Override
		// public void onListItemClick(int position) {
		// // TODO Auto-generated method stub
		// // String fitting = mFittingList.get(position);
		// toSetting(list.get(position), mRoomName);
		// }
		// });
		// dialog.show();
		Intent intent = new Intent(context, SingleSelectActivity.class);
		intent.putExtra(KeyList.PKEY_SINGLE_SELECT_TYPE, SingleSelectActivity.UNCONFIG_DEVICE_SELECT);
		intent.putExtra(KeyList.PKEY_UNCONFIG_FITTING_LIST, mFittingList);
		intent.putExtra(KeyList.PKEY_UNCONFIG_LIST, list);
		startActivity(intent);
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
	}

	@Override
	public void requestBox() {
		// TODO Auto-generated method stub
		mProgressDialog.setMessage(getString(R.string.ba_update_date));
		mProgressDialog.show();

		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				// ToastUtils.show(context, R.string.ba_update_date,
				// Toast.LENGTH_LONG);
				WifiCRUDForRoom wRoom = new WifiCRUDForRoom(context, getBoxIp(), getBoxTcpPort());
				wRoom.seleteAll(new ResultListener() {
					@Override
					public void onResult(String type, String errorCode, List<WifiRoomInfo> info) {
						// TODO Auto-generated method stub

						if (errorCode.equals(WifiCreateAndParseSockObjectManager.WIFI_INFO_SUCCESS)) {
							LogManager.i("partsManager size : " + info.size());

							mRoomInfoList = copyRoomInfoList(info);
							mCurrentPosition = 0;

							deleteNullRoom(0);
							loadData(info);
							mHandler.sendEmptyMessageDelayed(HANDLER_DISSMIS_DIALOG, 300);
						} else {
							mHandler.sendEmptyMessageDelayed(HANDLER_DISSMIS_DIALOG, 500);
							ToastUtils.show(context, R.string.ba_get_info_error_toast);
						}
					}
				});
			}
		}).start();
	}

	public void updateView() {
		WifiCRUDForRoom wRoom = new WifiCRUDForRoom(context, getBoxIp(), getBoxTcpPort());
		wRoom.seleteAll(new ResultListener() {
			@Override
			public void onResult(String type, String errorCode, List<WifiRoomInfo> info) {
				// TODO Auto-generated method stub
				if (errorCode.equals(WifiCreateAndParseSockObjectManager.WIFI_INFO_SUCCESS)) {
					loadData(info);

				} else {
					// 刷新失败
					ToastUtils.show(context, R.string.ba_get_info_error_toast);
				}
			}
		});
	}

	private List<WifiRoomInfo> copyRoomInfoList(List<WifiRoomInfo> list) {
		WifiRoomInfo mWifiRoomInfo = null;
		mRoomInfoList = new ArrayList<WifiRoomInfo>();
		for (WifiRoomInfo info : list) {
			mWifiRoomInfo = new WifiRoomInfo();
			mWifiRoomInfo.setId(info.getId());
			mWifiRoomInfo.setRoomId(info.getRoomId());
			mWifiRoomInfo.setRoomName(info.getRoomName());
			mRoomInfoList.add(mWifiRoomInfo);
		}
		return mRoomInfoList;
	}

	private void deleteNullRoom(int position) {
		LogManager.i("mRoomInfoList=" + mRoomInfoList.size() + "||position=" + position);

		if (mRoomInfoList == null || mRoomInfoList.size() <= 0) {
			return;
		}

		if (mRoomInfoList.size() <= position) {
			LogManager.e("更新数据");
			updateView();
			return;
		}

		final String roomId = mRoomInfoList.get(position).getRoomId();
		WifiCRUDForDevice mWifiCRUDForDevice = new WifiCRUDForDevice(context, getBoxIp(), getBoxTcpPort());
		mWifiCRUDForDevice.seleteByRoomId(roomId, new WifiCRUDForDevice.ResultListener() {
			@Override
			public void onResult(String type, String errorCode, List<WifiDeviceInfo> info) {
				// TODO Auto-generated method stub

				if (WifiCRUDUtil.isSuccessAll(errorCode)) {

					if (info.size() == 0) {
						WifiCRUDForRoom wRoom = new WifiCRUDForRoom(context, getBoxIp(), getBoxTcpPort());
						LogManager.i("删除数据，roomId=" + roomId);
						wRoom.deleteByRoomId(roomId, new ResultListener() {

							@Override
							public void onResult(String type, String errorCode, List<WifiRoomInfo> info) {
								// TODO Auto-generated method stub
								if (WifiCRUDUtil.isSuccessAll(errorCode)) {
									LogManager.i("删除数据，sucess");
									mCurrentPosition++;
									deleteNullRoom(mCurrentPosition);
								} else {
									LogManager.i("删除数据，error");
								}
							}
						});
					} else {
						mCurrentPosition++;
						deleteNullRoom(mCurrentPosition);
					}
				}
			}
		});
	}

	private MainListApdater mApdater;
	private String mRoomName;
	private ArrayList<String> roomNames;
	private ArrayList<String> roomids;

	private void loadData(final List<WifiRoomInfo> info) {
		new Handler(context.getMainLooper()).post(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				roomNames = new ArrayList<String>();
				roomids = new ArrayList<String>();
				for (int i = 0; i < info.size(); i++) {
					roomNames.add(info.get(i).getRoomName());
					roomids.add(info.get(i).getRoomId());
				}
				if (mApdater != null) {
					mPartsLv.removeHeaderView(mListViewHead);
				}

				if (null == info || info.size() < 1) {
					return;
				}
				mRoomName = info.get(0).getRoomName();
				mLvHeadTitleTv.setText(mRoomName);
				mLvHeadTitleTv.setTag(info.get(0).getRoomId());

				mLvHeadTitleTv.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						String roomId = mLvHeadTitleTv.getTag().toString();
						String roomName = mLvHeadTitleTv.getText().toString();
						mBasePreferences.setPrefString(KeyList.PKEY_ROOM_NAME, roomName);

						// 查询数据
						Intent intent = new Intent(context, DetailRoomActivity.class);
						intent.putExtra(KeyList.IKEY_ROOM_ID, roomId);
						intent.putExtra(KeyList.IKEY_ROOM_NAME, roomName);
						startActivity(intent);
					}
				});
				mPartsLv.setAdapter(null);
				mPartsLv.addHeaderView(mListViewHead);
				info.remove(0);
				mApdater = new MainListApdater(context, info);
				mPartsLv.setAdapter(mApdater);
				ToastUtils.cancel();
				LogManager.i("update ok");
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

	// @Override
	// protected void onDestroy() {
	// // TODO Auto-generated method stub
	// super.onDestroy();
	// mGetNewDevice.unregisterReceiver();
	// }
}
