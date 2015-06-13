package com.iii.wifi.util;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.text.TextUtils;

public class BasePreferences implements IPreferences {

    private SharedPreferences mPrefs;
    private Editor mEditor;

    public BasePreferences(Context context) {
        // TODO Auto-generated constructor stub
        mPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        mEditor = mPrefs.edit();
    }

    /**
     * 
     * @param key
     *            Preference key值
     * @return key对应的value值
     */
    public boolean getPrefBoolean(String key) {
        return mPrefs.getBoolean(key, false);
    }

    /**
     * 
     * @param key
     *            Preference key值
     * @param defVal
     *            默认的value值
     * @return key对应的value值
     */
    public boolean getPrefBoolean(String key, boolean defVal) {
        if (mPrefs != null) {
            return mPrefs.getBoolean(key, defVal);
        } else {
            return false;
        }

    }

    /**
     * 
     * @param key
     *            Preference key值
     * @param value
     *            设置的value值
     */
    public void setPrefBoolean(String key, boolean value) {
        mEditor.putBoolean(key, value);
        mEditor.commit();
    }

    /**
     * 
     * @param key
     *            Preference key值
     * @return key对应的value值
     */
    public String getPrefString(String key) {
        return mPrefs.getString(key, null);
    }

    /**
     * 
     * @param key
     *            Preference key值
     * @param defVal
     *            默认的value值
     * @return key对应的value值
     */
    public String getPrefString(String key, String defVal) {
        return mPrefs.getString(key, defVal);
    }


    public final static String regularEx = "#";
    public List<String> getPrefStringList(String key) {
        return getPrefStringList(key, null);
    }

    public List<String> getPrefStringList(String key, List<String> defValues) {
        String str = mPrefs.getString(key, "");
        if (!TextUtils.isEmpty(str)) {
            String[] values = str.split(regularEx);
            if (defValues == null) {
                defValues = new ArrayList<String>();
                for (String value : values) {
                    if (!TextUtils.isEmpty(value)) {
                        defValues.add(value);
                    }
                }
            }
        }else{
            defValues = new ArrayList<String>();
        }

        return defValues;
    }

    public void setPrefStringList(String key, List<String> values) {
        String str = "";
        if (values != null | !values.isEmpty()) {
            for (String data : values) {
                str += data;
                str += regularEx;
            }
            mEditor.putString(key, str);
            mEditor.commit();
        }
    }

    /**
     * 
     * @param key
     *            Preference key值
     * @param value
     *            设置的value值
     */
    public void setPrefInteger(String key, int value) {
        mEditor.putInt(key, value);
        mEditor.commit();
    }

    /**
     * 
     * @param key
     *            Preference key值
     * @return key对应的value值
     */
    public int getPrefInteger(String key) {
        return mPrefs.getInt(key, 0);
    }

    /**
     * 
     * @param key
     *            Preference key值
     * @param defVal
     *            默认的value值
     * @return key对应的value值
     */
    public int getPrefInteger(String key, int defVal) {
        return mPrefs.getInt(key, defVal);
    }

    /**
     * 
     * @param key
     *            Preference key值
     * @param value
     *            设置的value值
     */
    public void setPrefString(String key, String value) {
        mEditor.putString(key, value);
        mEditor.commit();
    }

    /**
     * 
     * @param key
     *            Preference key值
     * @return key对应的value值
     */
    public long getPrefLong(String key) {
        return mPrefs.getLong(key, 0L);
    }

    /**
     * 
     * @param key
     *            Preference key值
     * @param defVal
     *            默认的value值
     * @return key对应的value值
     */
    public long getPrefLong(String key, long defVal) {
        return mPrefs.getLong(key, defVal);
    }

    /**
     * 
     * @param key
     *            Preference key值
     * @param value
     *            设置的value值
     */
    public void setPrefLong(String key, long value) {
        mEditor.putLong(key, value);
        mEditor.commit();
    }

    /**
     * 
     * @param key
     *            Preference key值
     * @return key对应的value值
     */
    public float getPrefFloat(String key) {
        return mPrefs.getFloat(key, 0.0F);
    }

    /**
     * 
     * @param key
     *            Preference key值
     * @param defVal
     *            默认的value值
     * @return key对应的value值
     */
    public float getPrefFloat(String key, float defVal) {
        return mPrefs.getFloat(key, defVal);
    }

    /**
     * 
     * @param key
     *            Preference key值
     * @param value
     *            设置的value值
     */
    public void setPrefFloat(String key, float value) {
        mEditor.putFloat(key, value);
        mEditor.commit();
    }

}
