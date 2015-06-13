package com.iii360.box.set.third;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.util.Base64;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.iii.wifi.dao.info.HuanTengAccount;
import com.iii.wifi.dao.manager.WifiCRUDForHuanteng;
import com.iii.wifi.dao.manager.WifiCRUDForHuanteng.ResultForHuantengListener;
import com.iii360.box.R;
import com.iii360.box.base.BaseActivity;
import com.iii360.box.util.KeyList;
import com.iii360.box.util.ToastUtils;
import com.iii360.box.util.WifiCRUDUtil;
import com.iii360.box.view.MyProgressDialog;

public class ThirdAddHuanTengActivity extends BaseActivity implements OnClickListener {
	private EditText accountEt, PwdEt;
	private Button addBtn;
	private String mUserName;
	private String mPassword;
	private MyProgressDialog progressDialog;
	private String url;
	private WifiCRUDForHuanteng wifiCRUDForHuanteng;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_third_add_huanteng);
		wifiCRUDForHuanteng = new WifiCRUDForHuanteng(getBoxIp(), getBoxTcpPort());
		setupView();
		addListeners();
//		testMode();
		// Basic MzE4OTgxMjM0QHFxLmNvbTpzaGVuZ3hpYW4wMzIx
		// Basic MzE4OTgxMjM0QHFxLmNvbTpzaGVuZ3hpYW4wMzIx
		url = "http://huantengsmart.com/api/user.json";
	}

	void testMode() {
		accountEt.setText("318981234@qq.com");
		PwdEt.setText("shengxian0321");
		PwdEt.setInputType(InputType.TYPE_CLASS_TEXT);
	}

	private Handler handler = new Handler();
	private Runnable dismissDialog = new Runnable() {

		@Override
		public void run() {
			if (progressDialog != null && !isFinishing()) {
				progressDialog.dismiss();
			}
		}
	};

	protected void onDestroy() {
		super.onDestroy();
		if (progressDialog != null && !isFinishing()) {
			progressDialog.dismiss();
		}
		progressDialog = null;
	};

	private interface ResultListener {
		/****
		 * 账号和密码验证正确
		 */
		void onSuccess();

		void onFail(Exception e);
	}

	private void sendGet(String urlStr, ResultListener listener) {
		try {
			URL url = new URL(urlStr);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Authorization", makeBaseAuth(mUserName, mPassword));
			conn.setRequestProperty("Accept", "application/vnd.huantengsmart-v1+json");
			conn.connect();
			int code = conn.getResponseCode();
			if (code != 200) {
				throw new Exception("errorCode=" + code);
			}
			InputStream in = conn.getInputStream();
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			int count;
			byte[] buff = new byte[1024];
			while ((count = in.read(buff)) != -1) {
				baos.write(buff, 0, count);
			}
			baos.flush();
			byte[] data = baos.toByteArray();
			String content = new String(data, "utf-8");
			JSONObject obj = new JSONObject(content);
			String email = obj.getString("email");
			if (!mUserName.equals(email)) {
				throw new Exception("email not equals :" + mUserName + "---" + email);
			}
			listener.onSuccess();
		} catch (Exception e) {
			e.printStackTrace();
			listener.onFail(e);
		}
	}

	private String makeBaseAuth(String username, String password) {
		String json = username + ":" + password;
		// "Basic " + Base64.encodeBase64String(token.getBytes());
		try {
			return "Basic " + Base64.encodeToString(json.getBytes("UTF-8"), Base64.DEFAULT);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}

	private void addListeners() {
		addBtn.setOnClickListener(this);
		findViewById(R.id.huanteng_main).setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				if (imm.isActive()) {
					imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
				}
			}
		});
	}

	private void setupView() {
		this.setViewHead("添加幻腾");
		accountEt = (EditText) findViewById(R.id.huanteng_account_et);
		PwdEt = (EditText) findViewById(R.id.huanteng_pwd_et);
		addBtn = (Button) findViewById(R.id.huanteng_add_btn);
		progressDialog = new MyProgressDialog(context);
		progressDialog.setMessage(getString(R.string.ba_update_date));
		progressDialog.setCanceledOnTouchOutside(false);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.huanteng_add_btn:
			validateAndSend();
			break;
		}

	}

	private void validateAndSend() {
		String account = accountEt.getText().toString().trim();
		String pwd = PwdEt.getText().toString();
		if (isEmpty(account) || isEmpty(pwd)) {
			ToastUtils.show(getApplicationContext(), "账号或密码不能为空");
			return;
		}
		mUserName = account;
		mPassword = pwd;
		progressDialog.show();
		new Thread() {
			public void run() {
				sendGet(url, new ResultListener() {

					@Override
					public void onSuccess() {
						// ToastUtils.show(getApplicationContext(), "账号和密码均正确");
						handler.post(new Runnable() {

							@Override
							public void run() {
								send();
							}
						});

					}

					@Override
					public void onFail(Exception e) {
						handler.post(dismissDialog);
						// Log.i("info", "" + e);
						ToastUtils.show(getApplicationContext(), "账号或密码错误");
					}
				});
			};
		}.start();
	}

	private void send() {
		final HuanTengAccount info = new HuanTengAccount();
		info.setPassword(mPassword);
		info.setUsername(mUserName);
		info.setAddTime(System.currentTimeMillis());
		wifiCRUDForHuanteng.setHuantengData(info, new ResultForHuantengListener() {

			@Override
			public void onResult(String type, String errorCode, HuanTengAccount userData) {
				handler.post(dismissDialog);
				// ToastUtils.show(getApplicationContext(), "" + errorCode);
				// Log.i("info", "" + errorCode);
				if (WifiCRUDUtil.isSuccessAll(errorCode)) {
					ToastUtils.show(context, "添加成功");
					handler.post(new Runnable() {

						@Override
						public void run() {
							Intent intent = new Intent(context,ThirdSeeHuanTengActivity.class);
							intent.putExtra(KeyList.KEY_HUANTENG_BEAN_KEY_EXTRA, info);
							startActivity(intent);
							finish();
						}
					});
				} else {
					ToastUtils.show(context, R.string.ba_get_data_error_toast);
				}
			}
		});
	}
}
