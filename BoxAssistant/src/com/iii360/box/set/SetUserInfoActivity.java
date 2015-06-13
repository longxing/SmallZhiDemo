package com.iii360.box.set;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.RadioButton;

import com.iii.wifi.dao.info.WifiUserData;
import com.iii.wifi.dao.manager.WifiCRUDForUserData;
import com.iii.wifi.dao.manager.WifiCRUDForUserData.ResultForUserDataListener;
import com.iii360.box.MainActivity;
import com.iii360.box.R;
import com.iii360.box.adpter.ListApdater;
import com.iii360.box.base.BaseActivity;
import com.iii360.box.util.KeyList;
import com.iii360.box.util.LogManager;
import com.iii360.box.util.PhoneInfoUtils;
import com.iii360.box.util.ToastUtils;
import com.iii360.box.util.WifiCRUDUtil;
import com.iii360.box.view.IView;
import com.iii360.box.view.ListDialog;
import com.iii360.box.view.MyProgressDialog;

/**
 * 信息填写
 * 
 * @author hefeng
 * 
 */
public class SetUserInfoActivity extends BaseActivity implements IView, OnClickListener {
	private RadioButton mMaleRadio;
	private RadioButton mWomanRadio;

	private Button mBirthBtn;
	private Button mEduBtn;
	private Button mMarriageBtn;
	private Button mChildrenBtn;
	private Button mBooldBtn;

	private Button mNextBtn;
	private List<String> mListData;

	private String mSex = KeyList.GKEY_USERINFO_SEX_ARRAY[0];
	private String mBirth = KeyList.GKEY_USERINFO_BIRTH;
	private String mEducation = KeyList.GKEY_USERINFO_EDU_ARRAY[0];
	private String mMarriage = KeyList.GKEY_USERINFO_MARRIAGE_ARRAY[0];
	private String mChildren = KeyList.GKEY_USERINFO_CHILDREN_ARRAY[0];
	private String mBoold = KeyList.GKEY_USERINFO_BOOLD_ARRAY[0];
	private String modify;
	private static final int HANDLE_GET_DATA_SUCCESS = 1;
	private static final int HANDLE_GET_DATA_FAIL = 2;
	protected static final Object userData = null;
	private MyProgressDialog mProgressDialog;
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			if (mProgressDialog != null && !SetUserInfoActivity.this.isFinishing())
				mProgressDialog.dismiss();
			switch (msg.what) {
			case HANDLE_GET_DATA_SUCCESS:
				WifiUserData userdata = (WifiUserData) msg.obj;
				updateView(userdata);
				break;
			case HANDLE_GET_DATA_FAIL:
				ToastUtils.show(context, R.string.ba_get_info_error_toast);
				break;
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_set_user_info);
		modify = getIntent().getStringExtra("modify");
		this.initViews();
		this.initDatas();
		if (modify != null && modify.equals("yes")) {
			getUserInfoData();
			mNextBtn.setText("确定");
		}

	}

	protected void updateView(WifiUserData userdata) {
		if (userdata == null) {
			return;
		}
		if (userdata.getBirth() != null) {
			mBirthBtn.setText(userdata.getBirth());
		}
		if (userdata.getSex() != null) {
			String sex = userdata.getSex();
			if (KeyList.GKEY_USERINFO_SEX_ARRAY[0].equals(sex)) {
				mMaleRadio.setChecked(true);
			} else if (KeyList.GKEY_USERINFO_SEX_ARRAY[1].equals(sex)) {
				mWomanRadio.setChecked(true);
			}
		}
		if (userdata.getEducation() != null) {
			mEduBtn.setText(userdata.getEducation());
		}
		if (userdata.getMarriage() != null) {
			mMarriageBtn.setText(userdata.getMarriage());
		}
		if (userdata.getChildren() != null) {
			mChildrenBtn.setText(userdata.getChildren());
		}
		if (userdata.getBoold() != null) {
			mBooldBtn.setText(userdata.getBoold());
		}
	}

	private void getUserInfoData() {
		mProgressDialog = new MyProgressDialog(context);
		mProgressDialog.setMessage(getString(R.string.ba_update_date));
		// mProgressDialog.setCanceledOnTouchOutside(false);
		mProgressDialog.show();
		mWifiCRUDForUserData.getUserData(PhoneInfoUtils.getIMEI(context), new ResultForUserDataListener() {

			@Override
			public void onResult(String type, String errorCode, WifiUserData userData) {
				if (WifiCRUDUtil.isSuccess(errorCode)) {
					Message msg = new Message();
					msg.obj = userData;
					msg.what = HANDLE_GET_DATA_SUCCESS;
					handler.sendMessage(msg);
				} else {
					handler.sendEmptyMessage(HANDLE_GET_DATA_FAIL);
				}
			}
		});
	}

	@Override
	public void initViews() {
		// TODO Auto-generated method stub
		mMaleRadio = (RadioButton) findViewById(R.id.userinfo_radio_male);
		mWomanRadio = (RadioButton) findViewById(R.id.userinfo_radio_woman);

		mBirthBtn = (Button) findViewById(R.id.userinfo_birth_btn);
		mEduBtn = (Button) findViewById(R.id.userinfo_edu_btn);
		mMarriageBtn = (Button) findViewById(R.id.userinfo_marriage_btn);
		mChildrenBtn = (Button) findViewById(R.id.userinfo_children_btn);
		mBooldBtn = (Button) findViewById(R.id.userinfo_boold_btn);
		mNextBtn = (Button) findViewById(R.id.userinfo_next_btn);

		mBirthBtn.setOnClickListener(this);
		mEduBtn.setOnClickListener(this);
		mChildrenBtn.setOnClickListener(this);
		mBooldBtn.setOnClickListener(this);
		mMarriageBtn.setOnClickListener(this);
		mMaleRadio.setOnClickListener(this);
		mWomanRadio.setOnClickListener(this);
		mNextBtn.setOnClickListener(this);
	}

	@Override
	public void initDatas() {
		setViewHead("我的信息");
		// TODO Auto-generated method stub
		mWifiCRUDForUserData = new WifiCRUDForUserData(getBoxIp(), getBoxTcpPort());
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (v == mMaleRadio) {
			mSex = KeyList.GKEY_USERINFO_SEX_ARRAY[0];

		} else if (v == mWomanRadio) {
			mSex = KeyList.GKEY_USERINFO_SEX_ARRAY[1];

		} else if (v == mBirthBtn) {
			showDateDialog();

		} else if (v == mEduBtn) {
			showListDialog(KeyList.GKEY_USERINFO_EDU_ARRAY, mEduBtn);

		} else if (v == mMarriageBtn) {

			showListDialog(KeyList.GKEY_USERINFO_MARRIAGE_ARRAY, mMarriageBtn);

		} else if (v == mChildrenBtn) {
			showListDialog(KeyList.GKEY_USERINFO_CHILDREN_ARRAY, mChildrenBtn);

		} else if (v == mBooldBtn) {
			showListDialog(KeyList.GKEY_USERINFO_BOOLD_ARRAY, mBooldBtn);

		} else if (v == mNextBtn) {
			setPrefBoolean(KeyList.PKEY_IS_COMMIT_USER_INFO, true);
			sendUserData();
			if (modify != null && modify.equals("yes")) {
				finish();
			} else {
				startToActvitiy(MainActivity.class);
			}
		}
	}

	private WifiCRUDForUserData mWifiCRUDForUserData;

	private void sendUserData() {
		if (mMaleRadio.isChecked()) {
			mSex = KeyList.GKEY_USERINFO_SEX_ARRAY[0];
		} else {
			mSex = KeyList.GKEY_USERINFO_SEX_ARRAY[1];
		}
		mBirth = mBirthBtn.getText().toString();
		mEducation = mEduBtn.getText().toString();
		mMarriage = mMarriageBtn.getText().toString();
		mChildren = mChildrenBtn.getText().toString();
		mBoold = mBooldBtn.getText().toString();

		if (mMarriage.equals(KeyList.GKEY_USERINFO_MARRIAGE_ARRAY[0])) {
			mMarriage = "N";
		}

		if (mBoold.equals(KeyList.GKEY_USERINFO_BOOLD_ARRAY[0])) {
			mBoold = "N";
		}

		WifiUserData userData = new WifiUserData();
		userData.setSex(mSex);
		userData.setBirth(mBirth);
		userData.setEducation(mEducation);
		userData.setMarriage(mMarriage);
		userData.setChildren(mChildren);
		userData.setBoold(mBoold);
		userData.setImei(PhoneInfoUtils.getIMEI(context));
		userData.setBrand(PhoneInfoUtils.getBrandModel());

		LogManager.d("send user data : " + mSex + "||" + mBirth + "||" + mEducation + "||" + mMarriage + "||" + mChildren + "||" + mBoold);
		mWifiCRUDForUserData.setUserData(userData, new WifiCRUDForUserData.ResultForUserDataListener() {
			@Override
			public void onResult(String type, String errorCode, WifiUserData userData) {
				// TODO Auto-generated method stub
				if (WifiCRUDUtil.isSuccess(errorCode)) {
					LogManager.i("send user data success");
				} else {
					LogManager.i("send user data error");
				}
			}
		});
	}

	/**
	 * 显示列表对话框
	 * 
	 * @param data
	 * @param key
	 */
	private void showListDialog(final String[] data, final Button button) {
		mListData = Arrays.asList(data);
		ListDialog dialog = new ListDialog(this);
		dialog.setAdpter(new ListApdater(context, mListData), new ListDialog.OnListItemClickListener() {
			@Override
			public void onListItemClick(int position) {
				// TODO Auto-generated method stub
				LogManager.i(mListData.get(position));
				button.setText(mListData.get(position));
			}
		});
		dialog.show();
	}

	private void showDateDialog() {
		String date = mBirthBtn.getText().toString();
		String dates[] = date.split("-");
		int year = Integer.parseInt(dates[0]);
		int month = Integer.parseInt(dates[1]);
		int day = Integer.parseInt(dates[2]);

		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.YEAR, year);
		calendar.set(Calendar.MONTH, month - 1);
		calendar.set(Calendar.DAY_OF_MONTH, day);

		DatePickerDialog dialog = new DatePickerDialog(this, dateListener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
				calendar.get(Calendar.DAY_OF_MONTH));
		dialog.show();
	}

	DatePickerDialog.OnDateSetListener dateListener = new DatePickerDialog.OnDateSetListener() {
		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
			// TODO Auto-generated method stub

			int month = monthOfYear + 1;
			String monthStr = "" + month;
			if (month < 10) {
				monthStr = "0" + month;
			}
			String day = "" + dayOfMonth;
			if (dayOfMonth < 10) {
				day = "0" + dayOfMonth;
			}
			mBirthBtn.setText(year + "-" + monthStr + "-" + day);
		}
	};

	protected void onDestroy() {
		if (mProgressDialog != null && !SetUserInfoActivity.this.isFinishing()) {
			mProgressDialog.dismiss();
		}
		mProgressDialog = null;
		super.onDestroy();
	}

}
