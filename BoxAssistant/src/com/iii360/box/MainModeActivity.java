package com.iii360.box;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.iii.wifi.dao.info.WifiBoxModeInfo;
import com.iii.wifi.dao.info.WifiControlInfo;
import com.iii.wifi.dao.info.WifiRoomInfo;
import com.iii.wifi.dao.manager.WifiCRUDForBoxMode;
import com.iii.wifi.dao.manager.WifiCRUDForBoxMode.ResultForBoxModeListener;
import com.iii.wifi.dao.manager.WifiCRUDForControl;
import com.iii.wifi.dao.manager.WifiCRUDForControl.ResultListener;
import com.iii.wifi.dao.manager.WifiCRUDForRoom;
import com.iii360.box.adpter.ModeListApdater;
import com.iii360.box.base.BaseActivity;
import com.iii360.box.data.ModeData;
import com.iii360.box.util.KeyList;
import com.iii360.box.util.LogManager;
import com.iii360.box.util.ToastUtils;
import com.iii360.box.util.WifiCRUDUtil;
import com.iii360.box.view.IView;
import com.iii360.box.view.MyProgressDialog;
import com.iii360.box.view.TipDialog;

/**
 * 场景模式
 * 
 * @author Administrator
 * 
 */
public class MainModeActivity extends BaseActivity implements IView {
	public final static int HANDLER_MES_CANCEL_DIALIG = 0;
	public final static int HANDLER_MES_LOAD_LIST = 1;
	public final static int HANDLER_MES_UPDATE_LIST = 2;

	private ListView mModeList;
	private ModeListApdater mModeListApdater;
	/**
	 * 已设置/未设置 数据
	 */
	private List<String> mSetList;
	/**
	 * [{1=开/关客厅空调}, {2=开/关客厅电视机}] id为控制的ID
	 */
	private ArrayList<Map<Integer, String>> mDataList;
	private long startTime;

	private TextView mBackIb;
	private Button mHelpBtn;
	private MyProgressDialog mProgressDialog;
	private WifiCRUDForControl mWifiCRUDForControl;
	private WifiCRUDForRoom mWifiCRUDForRoom;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_mode);
		initViews();
		initDatas();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		updateData();
	}

	@Override
	public void initViews() {
		// TODO Auto-generated method stub
		mBackIb = (TextView) findViewById(R.id.head_left_textview);
		mModeList = (ListView) findViewById(R.id.main_mode_list);
		mHelpBtn = (Button) findViewById(R.id.main_mode_help_btn);
	}

	@Override
	public void initDatas() {
		// TODO Auto-generated method stub
		mBackIb.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});

		mHelpBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String[] tips = { "1.支持句式\"小智,打开xx模式\"\n\"小智,关闭xx模式\"", "2.如说\"小智,打开回家模式\"\n就可以执行一系列此模式里面的命令" };
				// new
				// AlertDialog.Builder(context).setTitle("使用说明").setItems(tips,
				// null).setNegativeButton("明白了", null).show();
				TipDialog dialog = new TipDialog(context);
				dialog.setTitle("使用说明");
				dialog.setItems(tips);
				dialog.show();
			}
		});
		// updateData();
		setModeData();
		getData();

		mDataList = new ArrayList<Map<Integer, String>>();
		mHandler.sendEmptyMessage(HANDLER_MES_LOAD_LIST);
		mProgressDialog = new MyProgressDialog(context);
		mProgressDialog.setMessage(getString(R.string.ba_update_date));
		mProgressDialog.show();
	}

	private void getData() {
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

							mWifiCRUDForControl.seleteAll(new ResultListener() {
								@Override
								public void onResult(String type, String errorCode, List<WifiControlInfo> controlInfos) {
									// TODO Auto-generated method stub

									while (System.currentTimeMillis() - startTime < 800) {

									}
									mHandler.sendEmptyMessage(HANDLER_MES_CANCEL_DIALIG);

									if (WifiCRUDUtil.isSuccessAll(errorCode)) {
										LogManager.i("get data success");

										if (controlInfos != null) {
											mDataList = ModeData._getOpenModeGroupData(roomInfos, controlInfos);
										}

										mHandler.sendEmptyMessage(HANDLER_MES_LOAD_LIST);

									} else {
										ToastUtils.show(context, R.string.ba_get_data_error_toast);
										LogManager.i("get data error");
									}
								}
							});
						} else {
							mHandler.sendEmptyMessage(HANDLER_MES_CANCEL_DIALIG);
							ToastUtils.show(context, R.string.ba_get_data_error_toast);
						}
					}
				});
			}
		}).start();
	}

	Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			int what = msg.what;
			switch (what) {

			case HANDLER_MES_CANCEL_DIALIG:
				if (mProgressDialog != null && !MainModeActivity.this.isFinishing()) {
					mProgressDialog.dismiss();
				}
				break;

			case HANDLER_MES_LOAD_LIST:
				LogManager.e("列表数据数量1：" + mSetList.size() + "");
				mModeListApdater = new ModeListApdater(context, mSetList, mDataList);
				mModeList.setAdapter(mModeListApdater);

				break;

			case HANDLER_MES_UPDATE_LIST:
				LogManager.e("列表数据数量2：" + mSetList.size() + "");
				mModeListApdater.notifyDataSetChanged();

				break;

			default:
				break;
			}

		}
	};

	/**
	 * 获取判断的数据，设置模式是否设置了
	 */
	private synchronized void setModeData() {
		mSetList = new ArrayList<String>();
		// 存储模式的格式为：MODE_GO_HOME 1||2||3 模式名称+控制设备ID
		for (int i = 0; i < KeyList.GKEY_MODE_ARRAY.length; i++) {
			String mSelectStr = getPrefString(KeyList.GKEY_MODE_ARRAY[i].toString());

			if (!TextUtils.isEmpty(mSelectStr)) {
				mSetList.add(KeyList.GKEY_SET);
			} else {
				mSetList.add(KeyList.GKEY_UNSET);
			}
		}
	}

	/**
	 * 更新和获取设置的模式数据
	 */
	private synchronized void updateData() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				WifiCRUDForBoxMode mWifiCRUDForBoxMode = new WifiCRUDForBoxMode(getBoxIp(), getBoxTcpPort());
				mWifiCRUDForBoxMode.getBoxModeList(new ResultForBoxModeListener() {
					@Override
					public void onResult(String type, String errorCode, List<WifiBoxModeInfo> infos) {
						// TODO Auto-generated method stub
						if (WifiCRUDUtil.isSuccessAll(errorCode)) {
							LogManager.i("MainModeActivity getBoxModeList data ok");
							if (infos == null || infos.isEmpty()) {
								// 数据为空，说明没有配置数据,保存选中的数据
								for (int i = 0; i < KeyList.GKEY_MODE_ARRAY.length; i++) {
									// 以模式名称为key存储打开模式数据
									setPrefString(KeyList.GKEY_MODE_ARRAY[i].toString(), "");
								}
							} else {
								for (int i = 0; i < KeyList.GKEY_MODE_ARRAY.length; i++) {
									String ids = getModeControlId(KeyList.GKEY_MODE_ARRAY[i].getValue(), infos);
									setPrefString(KeyList.GKEY_MODE_ARRAY[i].toString(), ids);
								}
							}

							setModeData();
							mHandler.sendEmptyMessage(HANDLER_MES_LOAD_LIST);

						} else {
							LogManager.i("MainModeActivity getBoxModeList data error");
						}
					}
				});
			}
		}).start();
	}

	private String getModeControlId(String modeName, List<WifiBoxModeInfo> infos) {
		for (WifiBoxModeInfo info : infos) {
			if (modeName.equals(info.getModeName())) {
				return info.getControlIDs();
			}
		}
		return "";
	}

	protected void onDestroy() {
		if (mProgressDialog != null && !this.isFinishing())
			mProgressDialog.dismiss();
		mProgressDialog = null;
		super.onDestroy();
	};

}
