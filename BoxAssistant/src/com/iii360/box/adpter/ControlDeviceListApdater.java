package com.iii360.box.adpter;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.iii.wifi.dao.info.WifiControlInfo;
import com.iii.wifi.dao.info.WifiDeviceInfo;
import com.iii.wifi.dao.manager.WifiCRUDForControl;
import com.iii.wifi.dao.manager.WifiCRUDForDevice;
import com.iii360.box.R;
import com.iii360.box.config.AddDeviceStudyActivity;
import com.iii360.box.util.BoxManagerUtils;
import com.iii360.box.util.DataUtil;
import com.iii360.box.util.KeyList;
import com.iii360.box.util.LogManager;
import com.iii360.box.util.ToastUtils;
import com.iii360.box.util.WifiCRUDUtil;
import com.iii360.box.view.MyExitDialog;

/**
 * 
 * @author hefeng
 * 
 */
public class ControlDeviceListApdater extends BaseAdapter {
	private List<WifiControlInfo> list;
	private LayoutInflater mInflater;
	private ViewHolder mViewHolder;
	private Context context;
	private MyExitDialog mDialog;
	private WifiControlInfo mWifiControlInfo;
	private boolean mIsStudy;

	public ControlDeviceListApdater(Context context, List<WifiControlInfo> list) {
		// TODO Auto-generated constructor stub
		mInflater = LayoutInflater.from(context);
		this.list = list;
		this.context = context;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return this.list.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		if (convertView == null) {

			mViewHolder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.view_control_device_listview_item, null);

			mViewHolder.tv1 = (TextView) (convertView.findViewById(R.id.control_device_name_tv));
			mViewHolder.tv2 = (TextView) (convertView.findViewById(R.id.control_switch_tv));
			mViewHolder.layout1 = (RelativeLayout) (convertView.findViewById(R.id.control_layout));
			mViewHolder.dividerView = convertView.findViewById(R.id.divider_view);
			convertView.setTag(mViewHolder);

		} else {
			mViewHolder = (ViewHolder) convertView.getTag();
		}

		mWifiControlInfo = list.get(position);

		mIsStudy = !TextUtils.isEmpty(mWifiControlInfo.getDorder());

		mViewHolder.tv1.setText(DataUtil.getDeviceName(mWifiControlInfo.getAction()));
		mViewHolder.tv2.setText(DataUtil.getAction(mWifiControlInfo.getAction()));

		RelativeLayout controlLayout = mViewHolder.layout1;

		if (!mIsStudy) {
			// controlLayout.setBackgroundColor(context.getResources().getColor(R.color.yellow_fffac7));
			mViewHolder.tv1.append("(未学习)");
		} else {
			// controlLayout.setBackgroundResource(R.drawable.ba_main_set_item_selector);
		}
		if (position == list.size() - 1) {
			mViewHolder.dividerView.setVisibility(View.GONE);
		} else {
			mViewHolder.dividerView.setVisibility(View.VISIBLE);
		}
		controlLayout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mWifiControlInfo = list.get(position);

				mIsStudy = !TextUtils.isEmpty(mWifiControlInfo.getDorder());
				// 学习指令
				mWifiControlInfo = list.get(position);
				Intent intent = new Intent(context, AddDeviceStudyActivity.class);
				intent.putExtra(KeyList.IKEY_CONTROLINFO_BEAN, mWifiControlInfo);
				intent.putExtra(KeyList.IKEY_DEVICE_IS_STUDY, mIsStudy);
				context.startActivity(intent);

			}
		});
		controlLayout.setOnLongClickListener(new View.OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				// TODO Auto-generated method stub

				try {
					mWifiControlInfo = list.get(position);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return false;
				}

				mDialog = new MyExitDialog(context, "确定要删除设备?");
				mDialog.setConfirmListener(new com.iii360.box.base.ConfirmButtonListener() {
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						mDialog.dismiss();

						// 删除设备
						WifiCRUDForControl mControl = new WifiCRUDForControl(context, BoxManagerUtils.getBoxIP(context), BoxManagerUtils.getBoxTcpPort(context));
						LogManager.i("delete ..ing  id = " + mWifiControlInfo.getId());
						mControl.delete(mWifiControlInfo.getId(), new WifiCRUDForControl.ResultListener() {
							@Override
							public void onResult(String type, String errorCode, List<WifiControlInfo> info) {
								// TODO Auto-generated method stub
								if (WifiCRUDUtil.isSuccessAll(errorCode)) {
									ToastUtils.show(context, R.string.ba_delete_success_toast);
									mWifiControlInfo = list.get(position);
									LogManager.i("delete ok id = " + mWifiControlInfo.getId());
									list.remove(position);
									mHandler.sendEmptyMessage(0);
									deleteDevice(mWifiControlInfo.getDeviceid(), DataUtil.getDeviceName(mWifiControlInfo.getAction()), position);
									if (listener != null) {
										listener.onRefresh();
									}
								} else {
									LogManager.i("delete error id = " + mWifiControlInfo.getId());
									ToastUtils.show(context, R.string.ba_config_box_info_error_toast);
								}
							}
						});
					}
				});
				mDialog.show();
				return false;
			}
		});
		return convertView;
	}

	Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);

			notifyDataSetChanged();
		}

	};

	public void deleteDevice(String deviceId, final String deleteDev, int deletePos) {
		LogManager.i("deviceId=" + deviceId + "||deleteDev=" + deleteDev);

		for (int i = 0; i < list.size(); i++) {
			String devName = DataUtil.getDeviceName(list.get(i).getAction());
			LogManager.i("devName=" + devName + "||deleteDev=" + deleteDev);

			if (deleteDev.equals(devName)) {
				return;
			}
		}
		final WifiCRUDForDevice wDevice = new WifiCRUDForDevice(context, BoxManagerUtils.getBoxIP(context), BoxManagerUtils.getBoxTcpPort(context));
		wDevice.seleteByDeviceId(deviceId, new WifiCRUDForDevice.ResultListener() {
			@Override
			public void onResult(String type, String errorCode, List<WifiDeviceInfo> info) {
				// TODO Auto-generated method stub
				if (WifiCRUDUtil.isSuccessAll(errorCode) && info != null && !info.isEmpty()) {
					WifiDeviceInfo wInfo = info.get(0);

					LogManager.i("new deviceName=" + newDeviceName(wInfo.getDeviceName(), deleteDev));

					wInfo.setDeviceName(newDeviceName(wInfo.getDeviceName(), deleteDev));
					wDevice.updata(wInfo, new WifiCRUDForDevice.ResultListener() {
						@Override
						public void onResult(String type, String errorCode, List<WifiDeviceInfo> info) {
							// TODO Auto-generated method stub
							if (WifiCRUDUtil.isSuccessAll(errorCode)) {
								LogManager.i("update ok");
							} else {
								LogManager.i("update error");
							}
						}
					});
				}
			}
		});

	}

	private String newDeviceName(String deviceNames, String removeDev) {
		if (deviceNames.equals(removeDev)) {
			return "";
		}

		StringBuffer buffer = new StringBuffer();
		String[] dev = deviceNames.split(KeyList.SEPARATOR);
		for (int i = 0; i < dev.length; i++) {
			if (!removeDev.equals(dev[i])) {
				buffer.append(dev[i]);
				buffer.append(KeyList.SEPARATOR);
			}
		}
		try {
			return buffer.toString().substring(0, buffer.length() - 1);
		} catch (Exception e) {
			// TODO: handle exception
		}
		return deviceNames;
	}

	private RefreshListener listener;

	public void setListener(RefreshListener listener) {
		this.listener = listener;
	}

	public interface RefreshListener {
		public void onRefresh();
	}

	public void destory() {
		if (mDialog != null) {
			mDialog.dismiss();
		}
	}

	private class ViewHolder {
		TextView tv1;
		TextView tv2;
		RelativeLayout layout1;
		View dividerView;
	}
}
