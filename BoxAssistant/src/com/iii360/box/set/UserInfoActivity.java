package com.iii360.box.set;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Random;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;

import com.google.gson.Gson;
import com.iii.wifi.dao.info.WifiUserData;
import com.iii.wifi.dao.manager.WifiCRUDForUserData;
import com.iii.wifi.dao.manager.WifiCRUDForUserData.ResultForUserDataListener;
import com.iii360.box.MainActivity;
import com.iii360.box.R;
import com.iii360.box.adpter.ListApdater;
import com.iii360.box.adpter.UserInfoAdapter;
import com.iii360.box.base.BaseActivity;
import com.iii360.box.util.KeyList;
import com.iii360.box.util.LogUtil;
import com.iii360.box.util.PhoneInfoUtils;
import com.iii360.box.util.ToastUtils;
import com.iii360.box.util.WifiCRUDUtil;
import com.iii360.box.view.ListDialog;
import com.iii360.box.view.MyProgressDialog;

public class UserInfoActivity extends BaseActivity {
	private ListView listView;
	private UserInfoAdapter adapter;
	private String modify;
	private Button nextBtn;
	private WifiCRUDForUserData mWifiCRUDForUserData;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_userinfo);
		getIntentData();
		setupView();
		addListeners();
		nextBtn.setText("下一步");
		if (modify != null && modify.equals("yes")) {
			getUserInfoData();
			nextBtn.setText("确定");
		}
	}

	/****
	 * 从音箱端获取数据
	 */
	private void getUserInfoData() {
		showProgressDialog(getString(R.string.ba_update_date));
		mWifiCRUDForUserData.getUserData(PhoneInfoUtils.getIMEI(context), new ResultForUserDataListener() {

			@Override
			public void onResult(String type, String errorCode, WifiUserData userData) {
				dismissProgressDialog(false);
				if (WifiCRUDUtil.isSuccess(errorCode)) {
					if (handler != null)
					handler.post(new UpdateViewTask(userData));
				} else {
					ToastUtils.show(context, R.string.ba_get_info_error_toast);
				}
			}
		});
	}

	// = { "性别", "年龄", "星座", "学历", "婚姻", "子女", "血型" };
	private class UpdateViewTask implements Runnable {
		private WifiUserData wifiUserData;

		public UpdateViewTask(WifiUserData wifiUserData) {
			this.wifiUserData = wifiUserData;
		}

		@Override
		public void run() {
			if (wifiUserData == null) {
				return;
			}
			String[] values = adapter.getValues();
			if (!isEmpty(wifiUserData.getSex())) {
				values[0] = wifiUserData.getSex();
			}
			if (!isEmpty(wifiUserData.getAge())) {
				values[1] = wifiUserData.getAge();
			} else {
				if (!isEmpty(wifiUserData.getBirth())) {
					try {
						values[1] = getAgeFromBirth(wifiUserData.getBirth());
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
			if (!isEmpty(wifiUserData.getConstellation())) {
				values[2] = wifiUserData.getConstellation();
			} else {
				if (!isEmpty(wifiUserData.getBirth())) {
					try {
						values[2] = getConstellationFromBirth(wifiUserData.getBirth());
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
			if (!isEmpty(wifiUserData.getEducation())) {
				values[3] = wifiUserData.getEducation();
			}
			if (!isEmpty(wifiUserData.getMarriage())) {
				values[4] = wifiUserData.getMarriage();
			}
			if (!isEmpty(wifiUserData.getChildren())) {
				values[5] = wifiUserData.getChildren();
			}
			if (!isEmpty(wifiUserData.getBoold())) {
				values[6] = wifiUserData.getBoold();
			}
			adapter.notifyDataSetChanged();

		}
	}

	public boolean isEmpty(String str) {
		if (str == null || "".equals(str.trim()) || "null".equals(str.trim()))
			return true;
		return false;
	}

	public String getConstellationFromBirth(String birth) throws Exception {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date date = sdf.parse(birth);
		Calendar c = new GregorianCalendar();
		c.setTime(date);
		int month = c.get(Calendar.MONTH) + 1;
		int day = c.get(Calendar.DAY_OF_MONTH);
		return star(month, day);
	}

	private void getIntentData() {
		modify = getIntent().getStringExtra("modify");
	}

	public String getAgeFromBirth(String birth) throws Exception {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
		int age = Integer.parseInt(sdf.format(new Date())) - Integer.parseInt(birth.split("\\-")[0]);
		return age + "";
	}

	private void setupView() {
		setViewHead("我的信息");
		listView = (ListView) findViewById(R.id.userinfo_listview);
		nextBtn = (Button) findViewById(R.id.user_info_next_btn);
		adapter = new UserInfoAdapter(keys, getDefaultValues(), this, listView);
		listView.setAdapter(adapter);
		adapter.setListViewHeightBasedOnChildren(listView);
		mWifiCRUDForUserData = new WifiCRUDForUserData(getBoxIp(), getBoxTcpPort());
	}

	/**
	 * 显示列表对话框
	 * 
	 * @param data
	 * @param key
	 */
	private void showListDialog(final int mainPosition, final String[] data) {
		final List<String> mListData = Arrays.asList(data);
		ListDialog dialog = new ListDialog(this);
		dialog.setAdpter(new ListApdater(context, mListData), new ListDialog.OnListItemClickListener() {
			@Override
			public void onListItemClick(int position) {
				String value = mListData.get(position);
				String[] values = adapter.getValues();
				values[mainPosition] = value;
				adapter.notifyDataSetChanged();
			}
		});
		dialog.show();
	}

	private Handler handler = new Handler();

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

	private MyProgressDialog myProgressDialog;

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

	@Override
	protected void onDestroy() {
		dismissProgressDialog(true);
		super.onDestroy();
		handler = null;
	}

	private String[] keys = { "性别", "年龄", "星座", "学历", "婚姻", "子女", "血型" };

	private String[] getDefaultValues() {
		return new String[] { KeyList.GKEY_USERINFO_SEX_ARRAY[0], "24", KeyList.GKEY_USERINFO_STAR_SIGN_ARRAY[0], KeyList.GKEY_USERINFO_EDU_ARRAY[0], KeyList.GKEY_USERINFO_MARRIAGE_ARRAY[0],
				KeyList.GKEY_USERINFO_CHILDREN_ARRAY[0], KeyList.GKEY_USERINFO_BOOLD_ARRAY[0] };
	};

	private String[] getAgeArrays() {
		String[] arr = new String[120];
		for (int i = 0; i < arr.length; i++) {
			arr[i] = i + 1 + "";
		}
		return arr;
	}

	private void showDialog(int position, String key) {
		switch (position) {
		case 0:
			showListDialog(position, KeyList.GKEY_USERINFO_SEX_ARRAY);
			break;
		case 1:
			showListDialog(position, getAgeArrays());
			break;
		case 2:
			showListDialog(position, KeyList.GKEY_USERINFO_STAR_SIGN_ARRAY);
			break;
		case 3:
			showListDialog(position, KeyList.GKEY_USERINFO_EDU_ARRAY);
			break;
		case 4:
			showListDialog(position, KeyList.GKEY_USERINFO_MARRIAGE_ARRAY);
			break;
		case 5:
			showListDialog(position, KeyList.GKEY_USERINFO_CHILDREN_ARRAY);
			break;
		case 6:
			showListDialog(position, KeyList.GKEY_USERINFO_BOOLD_ARRAY);
			break;
		}
	}

	private void addListeners() {
		nextBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				submit();
			}
		});
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				showDialog(position, keys[position]);
			}

		});
	}

	// = { "性别", "年龄", "星座", "学历", "婚姻", "子女", "血型" };
	protected void submit() {
		showProgressDialog(getString(R.string.ba_update_date));
		String[] values = adapter.getValues();
		WifiUserData userData = new WifiUserData();
		userData.setSex(values[0]);
		userData.setAge(values[1]);
		userData.setConstellation(values[2]);
		userData.setEducation(values[3]);
		userData.setMarriage(values[4].equals(KeyList.GKEY_USERINFO_MARRIAGE_ARRAY[0]) ? "N" : values[4]);
		userData.setChildren(values[5]);
		userData.setBoold(values[6].equals(KeyList.GKEY_USERINFO_BOOLD_ARRAY[0]) ? "N" : values[6]);
		userData.setImei(PhoneInfoUtils.getIMEI(context));
		userData.setBrand(PhoneInfoUtils.getBrandModel());
		userData.setBirth(getBirth(values[1], values[2]));
		LogUtil.d("send user data : " + new Gson().toJson(userData));
		mWifiCRUDForUserData.setUserData(userData, new WifiCRUDForUserData.ResultForUserDataListener() {
			@Override
			public void onResult(String type, String errorCode, WifiUserData userData) {
				dismissProgressDialog(false);
				// TODO Auto-generated method stub
				if (WifiCRUDUtil.isSuccess(errorCode)) {
					LogUtil.d("send user data errorCode:" + errorCode);
					if (handler != null)
						handler.post(new Runnable() {
							public void run() {
								if (modify != null && modify.equals("yes")) {
									finish();
								} else {
									setPrefBoolean(KeyList.PKEY_IS_COMMIT_USER_INFO, true);
									startToActvitiy(MainActivity.class);
								}
							}
						});
				} else {
					LogUtil.d("send user data errorCode:" + errorCode);
					ToastUtils.show(context, R.string.ba_config_box_info_error_toast);
				}
			}
		});

	}

	public static String star(int month, int day) {
		String star = "白羊座";
		if (month == 1 && day >= 20 || month == 2 && day <= 18) {
			star = "水瓶座";
		}
		if (month == 2 && day >= 19 || month == 3 && day <= 20) {
			star = "双鱼座";
		}
		if (month == 3 && day >= 21 || month == 4 && day <= 19) {
			star = "白羊座";
		}
		if (month == 4 && day >= 20 || month == 5 && day <= 20) {
			star = "金牛座";
		}
		if (month == 5 && day >= 21 || month == 6 && day <= 21) {
			star = "双子座";
		}
		if (month == 6 && day >= 22 || month == 7 && day <= 22) {
			star = "巨蟹座";
		}
		if (month == 7 && day >= 23 || month == 8 && day <= 22) {
			star = "狮子座";
		}
		if (month == 8 && day >= 23 || month == 9 && day <= 22) {
			star = "处女座";
		}
		if (month == 9 && day >= 23 || month == 10 && day <= 22) {
			star = "天秤座";
		}
		if (month == 10 && day >= 23 || month == 11 && day <= 21) {
			star = "天蝎座";
		}
		if (month == 11 && day >= 22 || month == 12 && day <= 21) {
			star = "射手座";
		}
		if (month == 12 && day >= 22 || month == 1 && day <= 19) {
			star = "摩羯座";
		}
		return star;
	}

	private int getMonth(String star) {
		int month = 0;
		if ("白羊座".equals(star)) {
			month = 3;
		} else if ("金牛座".equals(star)) {
			month = 4;
		} else if ("双子座".equals(star)) {
			month = 5;
		} else if ("巨蟹座".equals(star)) {
			month = 6;
		} else if ("狮子座".equals(star)) {
			month = 7;
		} else if ("处女座".equals(star)) {
			month = 8;
		} else if ("天秤座".equals(star)) {
			month = 9;
		} else if ("天蝎座".equals(star)) {
			month = 10;
		} else if ("射手座".equals(star)) {
			month = 11;
		} else if ("摩羯座".equals(star)) {
			month = 0;
		} else if ("水瓶座".equals(star)) {
			month = 1;
		} else if ("双鱼座".equals(star)) {
			month = 2;
		}
		return month;
	}

	private String getBirth(String ages, String constellation) {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			int age = Integer.parseInt(ages);
			Calendar cNow = new GregorianCalendar();
			Calendar c = new GregorianCalendar();
			c.set(Calendar.YEAR, cNow.get(Calendar.YEAR) - age);
			c.set(Calendar.MONTH, getMonth(constellation));
			c.set(Calendar.DAY_OF_MONTH, getRandomDayFromMonthAndYear(c.get(Calendar.YEAR), c.get(Calendar.MONTH) + 1));
			return sdf.format(c.getTime());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "1985-01-01";
	}

	private int getRandomDayFromMonthAndYear(int year, int month) {
		int days;
		if (month == 1 || month == 3 || month == 5 || month == 7 || month == 8 || month == 10 || month == 12) {
			days = 31;
		} else if (month == 4 || month == 6 || month == 9 || month == 11) {
			days = 30;
		} else {
			if ((year % 4 == 0 && year % 100 != 0) || (year % 400 == 0)) {
				days = 29;
			} else {
				days = 28;
			}
		}
		return new Random().nextInt(days) + 1;
	}
}
