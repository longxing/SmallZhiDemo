package com.iii360.box.about;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.iii360.box.R;
import com.iii360.box.base.BaseActivity;
import com.iii360.box.view.NewViewHead;

public class MainContactUsActivity extends BaseActivity implements OnClickListener {
	private TextView softVersionTv;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_contact_us);
		NewViewHead.showLeft(context, "关于我们");
		setupView();
		addListeners();
	}

	private void addListeners() {
		findViewById(R.id.main_contact_us_mobile_btn).setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				String str = getString(R.string.ba_main_contactus_service_phone_value);
				str = str.replaceAll("\\-", "");
				Uri uri = Uri.parse("tel:"+str);
				Intent intent = new Intent(Intent.ACTION_DIAL, uri);
				startActivity(intent);
			}
		});
	}

	private String getVersionName() {
		// 获取packagemanager的实例
		PackageManager packageManager = getPackageManager();
		// getPackageName()是你当前类的包名，0代表是获取版本信息
		try {
			PackageInfo packInfo = packageManager.getPackageInfo(getPackageName(), 0);
			String version = packInfo.versionName;
			return version;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return "1.0.0";
	}

	private void setupView() {
		softVersionTv = (TextView) findViewById(R.id.about_us_soft_version_tv);
		softVersionTv.setText("版本号 " + getVersionName());
	}

	@Override
	public void onClick(View v) {
	}
}
