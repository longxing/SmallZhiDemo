package com.iii360.sup.common.utl;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

import com.iii360.sup.inf.IGlobalValueOperation;
import com.iii360.sup.inf.IPrefrenceOperation;

/**
 * 
 * 使用本类终端额方法有一个前提。 Application类要实现IGloableHeap;
 * 
 * 同Context相关联的工具类。 1.存取Prefrence值。 2.存取全局对象 3.发送Umeng统计事件。 3.获取string中的相关属性值 </pre>
 * 
 */
public class SuperBaseContext implements IPrefrenceOperation, IGlobalValueOperation {

	/**************************** Member Variables *************************************/

	private static final String TAG = "Global Monitor";
	protected Context mContext = null;
	private static SharedPreferences mPrefs = null; // 偏好文件
	private static Editor mEditor = null; // 偏好文件编辑器
	private final String path = "/mnt/sdcard/com.voice.assistant.main/properties/main_preferences.properties";
	private Map<String, Boolean> mGlobalBooleanMap = null;
	private Map<String, Integer> mGlobalIntMap = null;
	private Map<String, String> mGlobalStringMap = null;
	private Map<String, Long> mGlobalLongMap = null;
	private Map<String, Object> mGlobalObjectMap = null;
	private Map<String, Float> mGlobalFloatMap = null;
	private Properties properties = null;

	public SuperBaseContext(Context context) {
		mContext = context;
		LogManager.d(TAG, "create SuperBaseContext Object");
		getSharePerence(context);
		getGloableMapObject(context);
	}

	/**
	 * 获取偏好文件编辑器
	 * 
	 * @param context
	 */
	private void getSharePerence(Context context) {
		if (mEditor == null) {
			mPrefs = PreferenceManager.getDefaultSharedPreferences(context);
			mEditor = mPrefs.edit();
		}
	}

	/**
	 * 获取全局存取对象集合
	 * 
	 * @param context
	 */
	private void getGloableMapObject(Context context) {
		try {
			final IGloableHeap gloableHeapMap = (IGloableHeap) context.getApplicationContext();
			mGlobalBooleanMap = gloableHeapMap.getGlobalBooleanMap();
			mGlobalIntMap = gloableHeapMap.getGlobalIntegerMap();
			mGlobalStringMap = gloableHeapMap.getGloabalString();
			mGlobalLongMap = gloableHeapMap.getGlobalLongMap();
			mGlobalObjectMap = gloableHeapMap.getGlobalObjectMap();
			mGlobalFloatMap = gloableHeapMap.getGlobalFloatMap();
		} catch (Exception e) {
			throw new RuntimeException("Application need implements IGloableHeap!");
		}
	}

	/**
	 * 加载配置文件
	 * 
	 * @param propertiesPath
	 * @return
	 */
	private Properties getProperties(String propertiesPath) {
		InputStream inputStream = null;
		if (properties == null) {
			properties = new Properties();
		}
		try {
			File file = new File(propertiesPath);
			LogManager.d(TAG, "load properties  file is exist:" + file.exists() + "==>> file size:" + file.length());
			inputStream = new BufferedInputStream(new FileInputStream(file));
			properties.load(inputStream);
			LogManager.d(TAG, "load properties  from:" + propertiesPath);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return properties;
	}

	/**
	 * 读取属性文件 属性的读写会在多线程中操作，读写操作为同步方法
	 * 
	 * @param key
	 * @param value
	 */
	private void write(String key, String value) {
		synchronized (SuperBaseContext.class) {
			FileOutputStream fos = null;
			try {
				File file = new File(path);
				if (!file.getParentFile().exists()) {
					file.getParentFile().mkdirs();
				}
				if (!file.exists()) {
					file.createNewFile();
				}
				properties = getProperties(path);
				fos = new FileOutputStream(path);
				if (value == null) {
					value = "";
				}
				LogManager.d(TAG, "Write key = " + key + " ==>>value = " + value);
				properties.setProperty(key, value);
				properties.store(fos, null);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					if (fos != null) {
						fos.close();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 读取属性文件 属性的读写会在多线程中操作，读写操作为同步方法
	 * 
	 * @param key
	 * @param value
	 */
	private String read(String key) {
		synchronized (SuperBaseContext.class) {
			properties = getProperties(path);
			String value = properties.getProperty(key);
			LogManager.d(TAG, "Read key = " + key + " ==>>value = " + value);
			return value;
		}

	}

	/**
	 * 
	 * @param key Preference key值
	 * @return key对应的value值
	 */
	public boolean getPrefBoolean(String key) {
		// return mPrefs.getBoolean(key, false);
		String value = read(key);
		if (value == null) {
			return false;
		}
		return Boolean.valueOf(value);
	}

	/**
	 * 
	 * @param key Preference key值
	 * @param defVal 默认的value值
	 * @return key对应的value值
	 */
	public boolean getPrefBoolean(String key, boolean defVal) {
		String value = read(key);
		if (value == null) {
			return defVal;
		}
		return Boolean.valueOf(value);

	}

	/**
	 * 
	 * @param key Preference key值
	 * @param value 设置的value值
	 */
	public void setPrefBoolean(String key, boolean value) {
		write(key, String.valueOf(value));
	}

	/**
	 * 
	 * @param key Preference key值
	 * @return key对应的value值
	 */
	public String getPrefString(String key) {
		String value = read(key);
		if (value == null) {
			return null;
		}
		return value;
	}

	/**
	 * 
	 * @param key Preference key值
	 * @param defVal 默认的value值
	 * @return key对应的value值
	 */
	public String getPrefString(String key, String defVal) {
		String value = read(key);
		if (value == null) {
			return defVal;
		}
		return value;
	}

	/**
	 * 
	 * @param key Preference key值
	 * @param value 设置的value值
	 */
	public void setPrefInteger(String key, int value) {
		write(key, String.valueOf(value));

	}

	/**
	 * 
	 * @param key Preference key值
	 * @return key对应的value值
	 */
	public int getPrefInteger(String key) {
		String value = read(key);
		if (value == null) {
			return 0;
		}
		return Integer.valueOf(value);
	}

	/**
	 * 
	 * @param key Preference key值
	 * @param defVal 默认的value值
	 * @return key对应的value值
	 */
	public int getPrefInteger(String key, int defVal) {
		String value = read(key);
		;
		if (value == null) {
			return defVal;
		}
		return Integer.valueOf(value);
	}

	/**
	 * 
	 * @param key Preference key值
	 * @param value 设置的value值
	 */
	public void setPrefString(String key, String value) {
		write(key, value);
	}

	public void setPrefStringNew(String key, String value) {
		synchronized (mEditor) {
			mEditor.putString(key, value);
			mEditor.commit();
		}

		write(key, value);
	}

	public String getPrefStringNew(String key, String defVal) {
		String s = mPrefs.getString(key, defVal);
		if (s == null) {
			String value = read(key);
			if (value == null) {
				return null;
			}
			return value;
		} else {
			return s;
		}
	}

	/**
	 * 
	 * @param key Preference key值
	 * @return key对应的value值
	 */
	public long getPrefLong(String key) {
		String value = read(key);
		if (value == null) {
			return 0L;
		}
		return Long.valueOf(value);
	}

	/**
	 * 
	 * @param key Preference key值
	 * @param defVal 默认的value值
	 * @return key对应的value值
	 */
	public long getPrefLong(String key, long defVal) {
		String value = read(key);
		if (value == null) {
			return defVal;
		}
		return Long.valueOf(value);
	}

	/**
	 * 
	 * @param key Preference key值
	 * @param value 设置的value值
	 */
	public void setPrefLong(String key, long value) {
		write(key, String.valueOf(value));
	}

	/**
	 * 
	 * @param key Preference key值
	 * @return key对应的value值
	 */
	public float getPrefFloat(String key) {
		String value = read(key);
		if (value == null) {
			return 0.0F;
		}
		return Float.valueOf(value);
	}

	/**
	 * 
	 * @param key Preference key值
	 * @param defVal 默认的value值
	 * @return key对应的value值
	 */
	public float getPrefFloat(String key, float defVal) {
		String value = read(key);
		if (value == null) {
			return defVal;
		}
		return Float.valueOf(value);
	}

	/**
	 * 
	 * @param key Preference key值
	 * @param value 设置的value值
	 */
	public void setPrefFloat(String key, float value) {
		write(key, String.valueOf(value));
	}

	/**
	 * 
	 * @param key 全局对象的key值
	 * @param value 全局对象key对应的value值
	 */
	public void setGlobalBoolean(String key, boolean value) {
		LogManager.d(TAG, "SetGlobal Boolean key = " + key + " ==>> value = " + value);
		mGlobalBooleanMap.put(key, value);
	}

	/**
	 * 
	 * @param key 全局对象对应的key值
	 * @param value 全局对象对应的value值
	 */
	public void setGlobalString(String key, String value) {
		LogManager.d(TAG, "SetGlobal String key = " + key + " ==>> value = " + value);
		mGlobalStringMap.put(key, value);
	}

	/**
	 * 
	 * @param key 全局对象的key值
	 * @param value 全局对象key对应的value值
	 */
	public void setGlobalInteger(String key, Integer value) {
		LogManager.d(TAG, "SetGlobal Integer key = " + key + " ==>> value = " + value);
		mGlobalIntMap.put(key, value);
	}

	/**
	 * 
	 * @param key 全局对象的key值
	 * @param value 全局对象key对应的value值
	 */
	public void setGlobalLong(String key, Long value) {
		LogManager.d(TAG, "SetGlobal Long key = " + key + " ==>> value = " + value);
		mGlobalLongMap.put(key, value);
	}

	/**
	 * 
	 * @param key 全局对象的key值
	 * @param value 全局对象key对应的value值
	 */
	public void setGlobalObject(String key, Object value) {
		LogManager.d(TAG, "SetGlobal Object key = " + key + " ==>> value = " + value);
		mGlobalObjectMap.put(key, value);
	}

	/**
	 * 
	 * @param key 全局对象的key值
	 * @param defVal 全局对象key对应的默认value值
	 * @return 对应的value值
	 */
	public boolean getGlobalBoolean(String key, boolean defVal) {
		Boolean booleanValue = mGlobalBooleanMap.get(key);

		LogManager.d(TAG, "getGlobal Boolean key = " + key + " ==>> value = " + booleanValue);
		if (booleanValue == null) {
			booleanValue = defVal;
		}
		return booleanValue;
	}

	/**
	 * 
	 * @param key 全局对象的key值
	 * @param defVal 全局对象key对应的默认value值
	 * @return 对应的value值
	 */
	public String getGlobalString(String key, String defVal) {
		String gloableStringValue = mGlobalStringMap.get(key);

		LogManager.d(TAG, "getGlobal String key = " + key + " ==>> value = " + gloableStringValue);
		if (gloableStringValue == null) {
			gloableStringValue = defVal;
		}
		return gloableStringValue;
	}

	/**
	 * 
	 * @param key 全局对象的key值
	 * @param defVal 全局对象key对应的默认value值
	 * @return 对应的value值
	 */
	public int getGlobalInteger(String key, Integer defVal) {
		Integer integerValue = mGlobalIntMap.get(key);

		LogManager.d(TAG, "getGlobal Integer key = " + key + " ==>> value =" + integerValue);
		if (integerValue == null) {
			integerValue = defVal;
		}
		return integerValue;
	}

	/**
	 * 
	 * @param key 全局对象的key值
	 * @param defVal 全局对象key对应的默认value值
	 * @return 对应的value值
	 */
	public long getGlobalLong(String key, Long defVal) {
		Long value = mGlobalLongMap.get(key);
		LogManager.d(TAG, "getGlobal Long key = " + key + " ==>> value = " + value);
		if (value == null) {
			value = defVal;
		}
		return value;
	}

	/**
	 * 
	 * @param key 全局对象的key值
	 * @param defVal 全局对象key对应的默认value值
	 * @return 对应的value值
	 */
	public Object getGlobalObject(String key, Object defVal) {
		Object value = mGlobalObjectMap.get(key);

		LogManager.d(TAG, "getGlobal Object key =  " + key + " ==>>value = " + value);
		if (value == null) {
			value = defVal;
		}
		return value;
	}

	/**
	 * 
	 * @param key 全局对象的key值
	 * @return key对应的value值
	 */
	public boolean getGlobalBoolean(String key) {
		final Boolean value = mGlobalBooleanMap.get(key);

		LogManager.d(TAG, "getGlobal Boolean key = " + key + " ==>> value = " + value);
		if (value == null) {
			return false;
		}
		return value;
	}

	/**
	 * 
	 * @param key 全局对象的key值
	 * @return key对应的value值
	 */
	public float getGlobalFloat(String key) {
		final Float value = mGlobalFloatMap.get(key);

		LogManager.d(TAG, "getGlobal Float key = " + key + " ==>> value = " + value);
		if (value == null) {
			return 0f;
		}
		return value;
	}

	/**
	 * 
	 * @param key 全局对象的key值
	 * @return key对应的value值
	 */
	public String getGlobalString(String key) {
		String value = mGlobalStringMap.get(key);

		LogManager.d(TAG, "getGlobal String key = " + key + " ==>> value  = " + value);
		return value;
	}

	/**
	 * 
	 * @param key 全局对象的key值
	 * @return key对应的value值
	 */
	public int getGlobalInteger(String key) {
		if (mGlobalIntMap.containsKey(key)) {

			int value = mGlobalIntMap.get(key);
			LogManager.d(TAG, "getGlobal Integer key = " + key + " ==>> value = " + value);
			return value;
		}
		return 0;

	}

	/**
	 * 
	 * @param key 全局对象的key值
	 * @return key对应的value值
	 */
	public long getGlobalLong(String key) {
		if (mGlobalLongMap.containsKey(key)) {
			long value = mGlobalLongMap.get(key);
			LogManager.d(TAG, "getGlobal Long key = " + key + " ==>> value = " + value);
			return value;
		} else {
			return 0L;
		}

	}

	/**
	 * 
	 * @param key 全局对象的key值
	 * @return key对应的value值
	 */
	public Object getGlobalObject(String key) {
		Object value = mGlobalObjectMap.get(key);
		LogManager.d(TAG, "getGlobal Object key = " + key + " ==>> value = " + value);
		return value;
	}

	/**
	 * 
	 * @param resId 资源id
	 * @return 资源对应的String值
	 */
	public String getStri(int resId) {
		String value = mContext.getString(resId);
		LogManager.d(TAG, "getGlobal Res  resId = " + resId + " ==>> value = " + value);
		return value;
	}

	/**
	 * @param resId 资源id
	 * @return 资源对应的String[]值
	 */
	public String[] getStringArray(int resId) {
		LogManager.d(TAG, "getGlobal Res  resId[]");
		return getStringArray(resId, mContext);
	}

	private static String[] getStringArray(int resId, Context context) {
		return context.getResources().getStringArray(resId);
	}

	/**
	 * 
	 * @return Context对象。
	 */
	public Context getContext() {
		return mContext;
	}
}
