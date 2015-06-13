package com.iii360.box.adpter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.iii.wifi.dao.info.WifiDeviceInfo;
import com.iii360.box.R;
import com.iii360.box.config.ControlDeviceActivity;
import com.iii360.box.config.DetailRoomActivity;
import com.iii360.box.config.WifiSingleAcivity;
import com.iii360.box.util.DataUtil;
import com.iii360.box.util.KeyList;
import com.iii360.box.util.LogManager;
import com.iii360.box.util.ToastUtils;
import com.iii360.box.view.ListDialog;
import com.iii360.box.view.MyExitDialog;
import com.iii360.box.view.PopupWindowView;
import com.iii360.box.view.TipDialog;

/**
 * 
 * @author hefeng
 * 
 */
public class DetailRoomListApdater extends BaseAdapter {
	private List<WifiDeviceInfo> list;
	private LayoutInflater mInflater;
	private ViewHolder mViewHolder;
	private Context context;
	private PopupWindowView mPopupWindowView;
	private MyExitDialog mMyExitDialog;
	private ListDialog mListDialog;
	private WifiDeviceInfo mDeviceInfo;
	private String mRoomName;
	private List<String> mListData;
	private Map<Integer, List<String>> mToastMapList;
	private ArrayList<String> roomNames;

	private DetailRoomListApdater(Context context, List<WifiDeviceInfo> list, String roomName, Map<Integer, List<String>> mToastMapList) {
		// TODO Auto-generated constructor stub
		mInflater = LayoutInflater.from(context);
		mPopupWindowView = new PopupWindowView(context);
		this.list = list;
		this.context = context;
		this.mRoomName = roomName;
		this.mToastMapList = mToastMapList;
	}

	public DetailRoomListApdater(Context context, List<WifiDeviceInfo> list, String roomName, Map<Integer, List<String>> mToastMapList, ArrayList<String> roomNames) {
		this(context, list, roomName, mToastMapList);
		this.roomNames = roomNames;
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
			convertView = mInflater.inflate(R.layout.view_detail_room_listview_item, null);
			mViewHolder.tv1 = (TextView) (convertView.findViewById(R.id.fitting_name_tv));
			mViewHolder.tv2 = (TextView) (convertView.findViewById(R.id.device_name_tv));
			mViewHolder.Iv = (ImageView) (convertView.findViewById(R.id.detail_room_toast_iv));
			mViewHolder.layout1 = (LinearLayout) (convertView.findViewById(R.id.detail_room_layout));

			convertView.setTag(mViewHolder);

		} else {
			mViewHolder = (ViewHolder) convertView.getTag();
		}

		mDeviceInfo = list.get(position);
		mViewHolder.tv2.setText("智能设备：" + mDeviceInfo.getFitting());
		mViewHolder.tv1.setText(mDeviceInfo.getDeviceName());

		mViewHolder.Iv.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// String[] tips =
				// DataUtil.tipsVoice(list.get(position).getDeviceName());
				String[] tips;
				try {
					List<String> list = mToastMapList.get(position);
					tips = new String[list.size()];
					list.toArray(tips);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					tips = DataUtil.tipsVoice(mRoomName, list.get(position).getDeviceName());
				}

				// new
				// AlertDialog.Builder(context).setTitle("您可以试试对着音箱说如下内容：").setItems(tips,
				// null).setNegativeButton("明白了", null).show();
				TipDialog dialog = new TipDialog(context);
				dialog.setTitle("您可以试试对着音箱说如下内容");
				dialog.setItems(tips);
				dialog.show();
			}
		});

		LinearLayout mLayout = mViewHolder.layout1;
		mLayout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// 点击编辑 如果是机器狗，进入开关，否则进入编辑界面
				// String fittingName = list.get(position).getFitting();

				mDeviceInfo = list.get(position);

				if (mDeviceInfo.getDeviceType() == KeyList.WIFI_UNSINGLE_DEVICE) {
					// 多个能设备，如机器狗
					Intent intent = new Intent(context, ControlDeviceActivity.class);
					intent.putExtra(KeyList.IKEY_DEVICEINFO_BEAN, mDeviceInfo);
					context.startActivity(intent);

				} else if (mDeviceInfo.getDeviceType() == KeyList.WIFI_SINGLE_DEVICE) {
					// wifi单品，进入编辑界面
					Intent intent = new Intent(context, WifiSingleAcivity.class);
					intent.putExtra(KeyList.IKEY_EXISTS_ROOM_NAME, roomNames);
					intent.putExtra(KeyList.IKEY_WIFIDEVICEINFO_ENTITY, mDeviceInfo);
					intent.putExtra(KeyList.IKEY_DEVICE_UPDATE, true);
					intent.putExtra(KeyList.PKEY_ROOM_NAME, mRoomName);
					context.startActivity(intent);

				} else {
					ToastUtils.show(context, "这不是机器狗也不是wifi单品哦");
				}
			}
		});

		mLayout.setOnLongClickListener(new View.OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				// TODO Auto-generated method stub
				LogManager.e("=============" + v.hashCode());
				WifiDeviceInfo deviceInfo = list.get(position);
				if (KeyList.WIFI_UNSINGLE_DEVICE == deviceInfo.getDeviceType()) {
					mPopupWindowView.showMoveBtn();
				} else {
					mPopupWindowView.dismissMoveBtn();
				}
				mPopupWindowView.show(v);

				mPopupWindowView.setPopupListener(new PopupWindowView.PopupWindowListener() {
					@Override
					public void onRemoveClick(View v) {
						WifiDeviceInfo deviceInfo = list.get(position);
						mListData = ((DetailRoomActivity) context).getOkRoomList(deviceInfo);
						// TODO Auto-generated method stub
						if (mListData == null) {
							((DetailRoomActivity) context).getRoomInfoAndControlInfo();
							ToastUtils.show(context, "系统繁忙，请稍候再试");
							return;
						}
						// 由于Arrays.asList() 返回java.util.Arrays$ArrayList，
						// 而不是ArrayList
						// mListData = new ArrayList<String>(mListData);
						// mListData.remove(mRoomName);
						// mListData = getOkRoomList(null);
						mListDialog = new ListDialog(context);
						mListDialog.setAdpter(new ListApdater(context, mListData), new ListDialog.OnListItemClickListener() {
							@Override
							public void onListItemClick(final int pos) {
								// TODO Auto-generated method stub
								LogManager.i("移动到:" + mListData.get(pos));
								ToastUtils.show(context, "正在移动，请稍后...", Toast.LENGTH_LONG);
								mDeviceInfo = list.get(position);
								DetailRoomListHelper.move(context, mListData.get(pos), mDeviceInfo);
							}
						});
						mListDialog.show();
					}

					@Override
					public void onDeleteClick(View v) {
						// TODO Auto-generated method stub
						LogManager.i("删除");
						mMyExitDialog = new MyExitDialog(context, "确定要删除配件?");
						mMyExitDialog.setConfirmListener(new com.iii360.box.base.ConfirmButtonListener() {
							@Override
							public void onClick(View v) {
								// TODO Auto-generated method stub
								mMyExitDialog.dismiss();
								ToastUtils.show(context, "正在删除，请稍后...");
								mDeviceInfo = list.get(position);
								// 通过deviceId删除配件
								DetailRoomListHelper.deleteDevice(context, mDeviceInfo.getDeviceid());
							}
						});
						mMyExitDialog.show();
					}
				});

				return false;
			}
		});
		return convertView;
	}

	public void destory() {
		if (mMyExitDialog != null) {
			mMyExitDialog.dismiss();
		}

		if (mPopupWindowView != null) {
			mPopupWindowView.dismiss();
		}

		if (mListDialog != null) {
			mListDialog.dismiss();
		}
	}
}

class ViewHolder {
	TextView tv1;
	TextView tv2;
	ImageView Iv;
	LinearLayout layout1;
}
