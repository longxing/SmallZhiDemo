package com.iii360.base.umeng;

import com.umeng.analytics.MobclickAgent;

import android.app.Activity;
import android.os.Bundle;

/**
 * UmengID:4eee98145270153fea00000d
 * 
 * <pre>
 *  manifest需要以下权限
 * <manifest……>
 * <application ……>
 * <activity ……/>
 * <meta-data android:value="YOUR_APP_KEY" android:name="UMENG_APPKEY"></meta-data>
 * <meta-data android:value="Channel ID" android:name="UMENG_CHANNEL"/>
 * </application>
 * <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"></uses-permission>
 * <uses-permission android:name="android.permission.INTERNET"></uses-permission>
 * <uses-permission android:name="android.permission.READ_PHONE_STATE"></uses-permission>
 * <uses-permission android:name="android.permission.READ_LOGS"></uses-permission>
 * </manifest>
 * </pre>
 * 
 * @author Jerome.Hu
 * 
 *
 */
public class UmengActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		MobclickAgent.updateOnlineConfig(this);
		MobclickAgent.onError(this);
//		MobclickAgent.setDebugMode(true);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		MobclickAgent.onResume(this);
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPause(this);
	}
}
