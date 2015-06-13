package com.iii360.box.base;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.iii360.box.MainActivity;
import com.iii360.box.MyApplication;
import com.iii360.box.R;
import com.iii360.box.config.WifiConfigActivity;
import com.iii360.box.set.UserInfoActivity;
import com.iii360.box.util.BoxManagerUtils;
import com.iii360.box.util.KeyList;
import com.iii360.box.util.LogManager;
import com.iii360.box.view.NewViewHead;
import com.iii360.box.view.ViewHead;
import com.umeng.analytics.MobclickAgent;

/**
 * Activity基类
 * 
 * @author hefeng
 * 
 */
public class BaseActivity extends PreferenceActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        setTheme(R.style.ActivityNoAnimationTheme);
        super.onCreate(savedInstanceState);
        MyApplication.getInstance().addActivity(this);
        MobclickAgent.updateOnlineConfig(this);
    }

	protected void onResume() {
	    super.onResume();
	    MobclickAgent.onPageStart("BaseActivity"); //统计页面(仅有Activity的应用中SDK自动调用，不需要单独写)
	    MobclickAgent.onResume(this);          //统计时长
	}
	protected void onPause() {
	    super.onPause();
	    MobclickAgent.onPageEnd("BaseActivity"); // （仅有Activity的应用中SDK自动调用，不需要单独写）保证 onPageEnd 在onPause 之前调用,因为 onPause 中会保存信息 
	    MobclickAgent.onPause(this);
	}
    
    public void startToActvitiyNoFinish(Class<?> cls) {
        startActivity(new Intent(this, cls));
    }

    public void startToActvitiy(Class<?> cls) {
        startToActvitiyNoFinish(cls);
        this.finish();
    }

    /**
     * 进入主界面
     */
    public void startToMainActvitiy() {
        LogManager.i("is  show user info view : "+getPrefBoolean(KeyList.PKEY_IS_COMMIT_USER_INFO, false)) ;
        if (getPrefBoolean(KeyList.PKEY_IS_COMMIT_USER_INFO, false)) {
            
            startToActvitiy(MainActivity.class);
            
        } else {
            
            startToActvitiy(UserInfoActivity.class);
        }
    }
    

    /**
     * @return 获取设置的wifi的名称
     */
    public String getWifiName() {
        return this.getPrefString(KeyList.GKEY_WIFI_SSID);
    }

    /**
     * @param wifiName
     *            保存设置的wifi的名称
     */
    public void saveWifiName(String wifiName) {
        this.setPrefString(KeyList.GKEY_WIFI_SSID, wifiName);
    }

    /**
     * @return 获取设置的wifi的密码
     */
    public String getWifiPassword() {
        return getPrefString(KeyList.GKEY_WIFI_PASSWORD);
    }

	/**
	 * @param wifiPassword
	 *            保存设置的wifi的密码
	 */
	public void saveWifiPassword(String wifiPassword) {
		this.setPrefString(KeyList.GKEY_WIFI_PASSWORD, wifiPassword);
	}

	/**
	 * 是否设置过盒子wifi
	 * 
	 * @return true表示第一次进入，没有设置过
	 */
	public boolean isFirstSetBoxWifi() {

		return TextUtils.isEmpty(this.getWifiName());

	}

	/**
	 * @return 盒子ip地址
	 */
	public String getBoxIp() {
		return BoxManagerUtils.getBoxIP(context);
	}

	public int getBoxTcpPort() {
		return BoxManagerUtils.getBoxTcpPort(context);
	}

	/**
	 * 判断字符是否为空
	 * 
	 * @param str
	 * @return
	 */
	public boolean isEmpty(CharSequence str) {
		return TextUtils.isEmpty(str);
	}

	/**
	 * 显示配置盒子网络对话框
	 */
	public void showWifiConfig() {
		MyApplication.getInstance().exit();
		this.startToActvitiy(WifiConfigActivity.class);
		finish();
	}

	/**
	 * 设置标题头
	 * 
	 * @param title
	 */
	public void setViewHead(String title) {
		NewViewHead.showLeft(context, title);
	}

    /**
     * 设置标题头
     * 
     * @param title
     */
    public void setViewHead(int resid) {
        ViewHead.showLeft(this, getResources().getString(resid), R.drawable.ba_back_btn_selector);
    }
}
