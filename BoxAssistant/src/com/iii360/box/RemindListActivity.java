package com.iii360.box;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.iii.wifi.dao.info.WifiRemindInfos;
import com.iii.wifi.dao.manager.WifiCRUDForRemind;
import com.iii.wifi.dao.manager.WifiCRUDForRemind.ResultForRemindListener;
import com.iii360.box.adpter.RemindListAdapter;
import com.iii360.box.base.BaseActivity;
import com.iii360.box.base.ConfirmButtonListener;
import com.iii360.box.remind.ListExpiredRemind;
import com.iii360.box.remind.RemindComparator;
import com.iii360.box.set.SendSetBoxData;
import com.iii360.box.util.BoxManagerUtils;
import com.iii360.box.util.LogManager;
import com.iii360.box.util.TimeUtil;
import com.iii360.box.util.ToastUtils;
import com.iii360.box.util.WifiCRUDUtil;
import com.iii360.box.view.MyExitDialog;
import com.iii360.box.view.MyProgressDialog;
import com.iii360.swipemenulistview.SwipeMenu;
import com.iii360.swipemenulistview.SwipeMenuCreator;
import com.iii360.swipemenulistview.SwipeMenuItem;
import com.iii360.swipemenulistview.SwipeMenuListView;
import com.iii360.swipemenulistview.SwipeMenuListView.OnMenuItemClickListener;
import com.voice.common.util.Remind;

public class RemindListActivity extends BaseActivity {
	private int menuWidth;
	private SwipeMenuListView remindListView;
	private RemindListAdapter remindListAdapter;
	private WifiCRUDForRemind mWifiCRUDForRemind;
	private MyProgressDialog mProgressDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_remind_list);
		setupView();
		initList();
		addListeners();
		initData();
	}

	private void addListeners() {
		remindListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				remindListView.smoothOpenMenu(position);
				// Toast.makeText(getApplicationContext(), "" +
				// remindListAdapter.getItem(position).needHand,
				// Toast.LENGTH_SHORT).show();
			}
		});
	}

	private void initData() {
		mProgressDialog = new MyProgressDialog(context);
		this.mWifiCRUDForRemind = new WifiCRUDForRemind(context, getBoxIp(), getBoxTcpPort());
		this.getRemindDatas();

	}

	private Handler handler = new Handler();
	private Runnable dismissDialogTask = new Runnable() {

		@Override
		public void run() {
			if (mProgressDialog != null && !isFinishing())
				mProgressDialog.dismiss();
		}
	};
	private ListExpiredRemind mListExpiredRemind;
	private Runnable showTask = new Runnable() {

		@Override
		public void run() {
			if (mRemindList == null) {
				mRemindList = new ArrayList<Remind>();
			}
			Collections.sort(mRemindList, new RemindComparator(mBoxTime));

			for (int i = 0; i < mRemindList.size(); i++) {
				LogManager.e(TimeUtil.getTime(mRemindList.get(i).getRunTime(mBoxTime)));
			}

			mListExpiredRemind = new ListExpiredRemind(mRemindList, mBoxTime);

			LogManager.i("获取到备忘管理数目：" + mRemindList.size());

			mRemindList = mListExpiredRemind.getNotExpiredList();

			LogManager.i("获取到备忘管理有效的数目：" + mRemindList.size());
			mListExpiredRemind.deleteWeekExpired(mWifiCRUDForRemind);
			remindListAdapter.changeData(mRemindList, mBoxTime);
		}
	};
	private List<Remind> mRemindList;
	private long mBoxTime;

	private void getRemindDatas() {
		// ToastUtils.show(context, R.string.ba_getting_data);
		mProgressDialog.setMessage(getString(R.string.ba_update_date));
		mProgressDialog.show();

		new Thread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				mWifiCRUDForRemind.selectRemind(new ResultForRemindListener() {
					@Override
					public void onResult(String type, String errorCode, WifiRemindInfos infos) {
						// TODO Auto-generated method stub
						if (WifiCRUDUtil.isSuccessAll(errorCode)) {
							handler.postDelayed(dismissDialogTask, 200);
							ToastUtils.cancel();
							if (infos.getRemindInfos() != null) {
								mRemindList = infos.getRemindInfos();
								mBoxTime = infos.getCurrentTime();
								// mHandler.sendEmptyMessage(HANDLER_SHOW_LIST);
								handler.post(showTask);
							} else {
								LogManager.i("获取到备忘管理数目：null");
							}

						} else {
							LogManager.i("获取到备忘管理数目：失败");
							handler.postDelayed(dismissDialogTask, 500);
							ToastUtils.show(context, R.string.ba_get_info_error_toast);
						}
					}
				});
			}
		}).start();
	}

	private void initList() {
		remindListAdapter = new RemindListAdapter(context, null, 0);
		remindListView.setAdapter(remindListAdapter);
		// step 1. create a MenuCreator
		SwipeMenuCreator creator = new SwipeMenuCreator() {

			@Override
			public void create(SwipeMenu menu) {
				// Create different menus depending on the view type
				switch (menu.getViewType()) {
				case 0:
					createMenu1(menu);
					break;
				case 1:
					// createMenu2(menu);
					break;
				case 2:
					// createMenu3(menu);
					break;
				}
			}

			private void createMenu1(SwipeMenu menu) {
				SwipeMenuItem item1 = new SwipeMenuItem(getApplicationContext());
				item1.setBackground(new ColorDrawable(Color.parseColor("#e44121")));
				item1.setWidth(menuWidth);
				item1.setIcon(R.drawable.remind_delete_btn_selector);
				menu.addMenuItem(item1);
				// SwipeMenuItem item2 = new
				// SwipeMenuItem(getApplicationContext());
				// item2.setBackground(new ColorDrawable(Color.rgb(0xC9, 0xC9,
				// 0xCE)));
				// item2.setWidth(dp2px(90));
				// item2.setIcon(R.drawable.ic_action_good);
				// menu.addMenuItem(item2);
			}

			// private void createMenu2(SwipeMenu menu) {
			// SwipeMenuItem item1 = new SwipeMenuItem(getApplicationContext());
			// item1.setBackground(new ColorDrawable(Color.rgb(0xE5, 0xE0,
			// 0x3F)));
			// item1.setWidth(dp2px(90));
			// item1.setIcon(R.drawable.ic_action_important);
			// menu.addMenuItem(item1);
			// SwipeMenuItem item2 = new SwipeMenuItem(getApplicationContext());
			// item2.setBackground(new ColorDrawable(Color.rgb(0xF9, 0x3F,
			// 0x25)));
			// item2.setWidth(dp2px(90));
			// item2.setIcon(R.drawable.ic_action_discard);
			// menu.addMenuItem(item2);
			// }
			//
			// private void createMenu3(SwipeMenu menu) {
			// SwipeMenuItem item1 = new SwipeMenuItem(getApplicationContext());
			// item1.setBackground(new ColorDrawable(Color.rgb(0x30, 0xB1,
			// 0xF5)));
			// item1.setWidth(dp2px(90));
			// item1.setIcon(R.drawable.ic_action_about);
			// menu.addMenuItem(item1);
			// SwipeMenuItem item2 = new SwipeMenuItem(getApplicationContext());
			// item2.setBackground(new ColorDrawable(Color.rgb(0xC9, 0xC9,
			// 0xCE)));
			// item2.setWidth(dp2px(90));
			// item2.setIcon(R.drawable.ic_action_share);
			// menu.addMenuItem(item2);
			// }
		};
		// set creator
		remindListView.setMenuCreator(creator);

		// step 2. listener item click event
		remindListView.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
				switch (index) {
				case 0:
					// open
					// mAppList.remove(position);
					// mAdapter.notifyDataSetChanged();
					showDeleteDialog(position);
					break;
				case 1:
					// delete
					// delete(item);
					// mAppList.remove(position);
					// mAdapter.notifyDataSetChanged();
					break;
				}
				return false;
			}
		});
	}

	/**
	 * 通过ID删除盒子备忘
	 * 
	 * @param remindId
	 */
	private void deleteRemind(final int position) {

		WifiCRUDForRemind wifiCRUDForRemind = new WifiCRUDForRemind(context, BoxManagerUtils.getBoxIP(context), BoxManagerUtils.getBoxTcpPort(context));
		wifiCRUDForRemind.deleteRemind(mRemindList.get(position).id, new ResultForRemindListener() {
			@Override
			public void onResult(String type, String errorCode, WifiRemindInfos infos) {
				// TODO Auto-generated method stub
				if (WifiCRUDUtil.isSuccessAll(errorCode)) {

					closeSetWeather(mRemindList.get(position));

					mRemindList.remove(position);
					new Handler(context.getMainLooper()).post(new Runnable() {
						@Override
						public void run() {
							// TODO Auto-generated method stub
							remindListAdapter.changeData(new ArrayList<Remind>(mRemindList), mBoxTime);
							if (mProgressDialog != null && !isFinishing()) {
								mProgressDialog.dismiss();
							}
						}
					});
					LogManager.i("删除备忘数据 成功");
					ToastUtils.cancel();
					ToastUtils.show(context, R.string.ba_delete_success_toast);

				} else {
					handler.post(dismissDialogTask);
					LogManager.i("删除备忘数据 失败");
					ToastUtils.show(context, R.string.ba_delete_error_toast);
				}
			}
		});
	}

	private void closeSetWeather(Remind remind) {
		if (remind.needHand.equals("播报天气")) {
			SendSetBoxData data = new SendSetBoxData(context);
			data.sendWeatherSwitch("false");
		}
	}

	private void showDeleteDialog(final int position) {
		final MyExitDialog mMyExitDialog = new MyExitDialog(context, "确定要删除吗？");
		mMyExitDialog.setConfirmListener(new ConfirmButtonListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mMyExitDialog.dismiss();
				mProgressDialog.setMessage("正在删除备忘...");
				mProgressDialog.show();
				deleteRemind(position);
			}
		});
		mMyExitDialog.show();
	}

	private void setupView() {
		this.setViewHead("备忘列表");
		menuWidth = (int) getResources().getDimension(R.dimen.swipe_menu_width);
		remindListView = (SwipeMenuListView) findViewById(R.id.remind_listview);
	}

}
