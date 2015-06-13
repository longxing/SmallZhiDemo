package com.iii360.base.umeng;

import java.util.Map;

import com.iii360.base.common.utl.LogManager;
import com.umeng.analytics.MobclickAgent;

import android.content.Context;

public class OnlineConfigurationUtil {
	private Context mContext ;
	private Map<String,String> mConfigurationMap ;
	public OnlineConfigurationUtil(Context context) {
		mContext = context ;
		try {
			IUmengConfigurationContainer container = (IUmengConfigurationContainer) mContext.getApplicationContext();
			mConfigurationMap = container.getConfiguration();
		} catch(Exception e) {
			throw new RuntimeException("Application need implements the interface com.iii360.base.inf.IUmengConfigurationContainer");
		}
	}
	/**
	 * 
	 * @param key Umeng key
	 * @param defaultValue Umeng 默认值
	 */
	public void loadOnLineConfig(String key,String defaultValue) {
		String onlineParam = MobclickAgent.getConfigParams(mContext ,key);
		mConfigurationMap.put(key, defaultValue);
		LogManager.i("getOnLine Params key is "+key + " value is "+onlineParam);
		if( onlineParam != null && !"".equals(onlineParam)) {
			mConfigurationMap.put(key, onlineParam);
		}
	}
	/**
	 * 
	 * @param key
	 * @return 网络参数值 或者之前存入的defaultValue.必须先loadOnLineConfig，
	 * 否则取到的值可能为空。
	 */
	public String getOnLineParam(String key) {
		return mConfigurationMap.get(key);
	}
}
