package com.smallzhi.push.util;

import android.content.Context;
/**
 * 写入app配置文件中
 *
 */
public class DataConfig {

	
	public static final String HARDWARE_VERSION = "hardware";

	public static final String SOFTWARE_VERSION = "software";

	public static final String CONFIG_INFO  = "CONFIG_INFO";//配置信息
	
	public static final String SERVIER_DOMAIN = "SERVIER_DOMAIN";//host

	public static final String SERVIER_PORT = "SERVIER_PORT";//port

	public static final String SESSION_KEY = "SESSION_KEY";
	
	
	public static void putString(Context context,String key,String value)
	{
		context.getSharedPreferences(CONFIG_INFO, Context.MODE_PRIVATE).edit().putString(key, value).commit();
	}
	
	public static String getString(Context context,String key)
	{
		return context.getSharedPreferences(CONFIG_INFO, Context.MODE_PRIVATE).getString(key,null);
	}
	
	public static void putBoolean(Context context,String key,boolean value)
	{
		context.getSharedPreferences(CONFIG_INFO, Context.MODE_PRIVATE).edit().putBoolean(key, value).commit();
	}
	
	public static boolean getBoolean(Context context,String key)
	{
		return context.getSharedPreferences(CONFIG_INFO, Context.MODE_PRIVATE).getBoolean(key,false);
	}
	
	
	public static void putInt(Context context,String key,int value)
	{
		context.getSharedPreferences(CONFIG_INFO, Context.MODE_PRIVATE).edit().putInt(key, value).commit();
	}
	
	public static int getInt(Context context,String key)
	{
		return context.getSharedPreferences(CONFIG_INFO, Context.MODE_PRIVATE).getInt(key,0);
	}
}
