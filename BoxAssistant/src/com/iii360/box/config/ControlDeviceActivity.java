package com.iii360.box.config;

import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.iii.wifi.dao.info.WifiControlInfo;
import com.iii.wifi.dao.info.WifiDeviceInfo;
import com.iii.wifi.dao.manager.WifiCRUDForControl;
import com.iii360.box.R;
import com.iii360.box.adpter.ControlDeviceListApdater;
import com.iii360.box.adpter.ControlDeviceListApdater.RefreshListener;
import com.iii360.box.base.BaseActivity;
import com.iii360.box.util.KeyList;
import com.iii360.box.util.LogManager;
import com.iii360.box.util.ToastUtils;
import com.iii360.box.util.WifiCRUDUtil;
import com.iii360.box.view.IView;

/**
 * 机器狗控制的设备
 * 
 * @author hefeng
 * 
 */
public class ControlDeviceActivity extends BaseActivity implements IView {
	private ListView mDeviceLv;
	private TextView mAddDeviceTv;
	private ControlDeviceListApdater mAdapter;
	private List<WifiControlInfo> mControlList;
	private static final int HANDLER_UPDATE_LIST = 0;
	private static final int HANDLER_UPDATE_LIST_VIEW = 1;
	private WifiDeviceInfo mDeviceInfo;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_control_device);
		this.initViews();
		this.initDatas();
	}
	@Override
	protected void onRestart() {
		super.onRestart();
		showList();
	}
	@Override
	public void initViews() {
		// TODO Auto-generated method stub
		mDeviceLv = (ListView) findViewById(R.id.control_device_lv);
		mAddDeviceTv = (TextView) findViewById(R.id.control_add_device_tv);
		mAddDeviceTv.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				Intent intent = new Intent(context, AddDeviceActivity.class);
				intent.putExtra(KeyList.IKEY_DEVICEINFO_BEAN, mDeviceInfo);
				startActivity(intent);

			}
		});

	}

	@Override
	public void initDatas() {
		// TODO Auto-generated method stub
		mDeviceInfo = (WifiDeviceInfo) getIntent().getSerializableExtra(KeyList.IKEY_DEVICEINFO_BEAN);
		String name = mDeviceInfo.getFitting();
		if (name.contains("-")) {
			name = name.substring(0, name.indexOf("-"));
		}
		this.setViewHead(name + "控制的设备");
		showList();
	}

	private void showList() {
		ToastUtils.show(context, R.string.ba_update_date);
		WifiCRUDForControl mControl = new WifiCRUDForControl(context, getBoxIp(), getBoxTcpPort());
		mControl.seleteByDeviceId(mDeviceInfo.getDeviceid(), new WifiCRUDForControl.ResultListener() {
			@Override
			public void onResult(String type, String errorCode, List<WifiControlInfo> info) {
				// TODO Auto-generated method stub
				if (WifiCRUDUtil.isSuccessAll(errorCode)) {

					// 没有control
					mControlList = info;
					mHandler.sendEmptyMessage(HANDLER_UPDATE_LIST);
				} else {

					ToastUtils.show(context, R.string.ba_get_data_error_toast);
					LogManager.i("get data error");

				}
			}
		});
	}

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			int what = msg.what;

			switch (what) {

			case HANDLER_UPDATE_LIST:
				mAdapter = new ControlDeviceListApdater(context, mControlList);
				mDeviceLv.setAdapter(mAdapter);
				mAdapter.setListener(new RefreshListener() {
					@Override
					public void onRefresh() {
						// TODO Auto-generated method stub
						mHandler.sendEmptyMessage(HANDLER_UPDATE_LIST_VIEW);
					}
				});
				ToastUtils.cancel();

				break;

			case HANDLER_UPDATE_LIST_VIEW:
				mAdapter = new ControlDeviceListApdater(context, mControlList);
				mDeviceLv.setAdapter(mAdapter);

				break;

			default:
				break;
			}
		}

	};

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if (mAdapter != null) {
			mAdapter.destory();
		}
	}
}
