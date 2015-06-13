package com.iii360.box.base;

import java.util.List;

import android.app.Activity;
import android.os.Bundle;

import com.iii360.box.common.BasePreferences;
import com.iii360.box.common.IPreferences;

/**
 * 保存SharedPreferences数值
 * 
 * @author hefeng
 * 
 */
public class PreferenceActivity extends Activity implements IPreferences {
    protected Activity context;
    private BasePreferences mBasePreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        context = PreferenceActivity.this;

        mBasePreferences = new BasePreferences(this);
    }

    @Override
    public void setPrefBoolean(String key, boolean value) {
        // TODO Auto-generated method stub
        this.mBasePreferences.setPrefBoolean(key, value);
    }

    @Override
    public boolean getPrefBoolean(String key) {
        // TODO Auto-generated method stub
        return this.mBasePreferences.getPrefBoolean(key);
    }

    @Override
    public boolean getPrefBoolean(String key, boolean defVal) {
        // TODO Auto-generated method stub
        return this.mBasePreferences.getPrefBoolean(key, defVal);
    }

    @Override
    public void setPrefString(String key, String value) {
        // TODO Auto-generated method stub
        this.mBasePreferences.setPrefString(key, value);
    }

    @Override
    public String getPrefString(String key) {
        // TODO Auto-generated method stub
        return this.mBasePreferences.getPrefString(key);
    }

    @Override
    public String getPrefString(String key, String defVal) {
        // TODO Auto-generated method stub
        return this.mBasePreferences.getPrefString(key, defVal);
    }

    @Override
    public void setPrefInteger(String key, int value) {
        // TODO Auto-generated method stub
        this.mBasePreferences.setPrefInteger(key, value);
    }

    @Override
    public int getPrefInteger(String key) {
        // TODO Auto-generated method stub
        return this.mBasePreferences.getPrefInteger(key);
    }

    @Override
    public int getPrefInteger(String key, int defVal) {
        // TODO Auto-generated method stub
        return this.mBasePreferences.getPrefInteger(key, defVal);
    }

    @Override
    public void setPrefLong(String key, long value) {
        // TODO Auto-generated method stub
        this.mBasePreferences.setPrefLong(key, value);
    }

    @Override
    public long getPrefLong(String key) {
        // TODO Auto-generated method stub
        return this.mBasePreferences.getPrefLong(key);
    }

    @Override
    public long getPrefLong(String key, long defVal) {
        // TODO Auto-generated method stub
        return this.mBasePreferences.getPrefLong(key, defVal);
    }

    @Override
    public void setPrefFloat(String key, float value) {
        // TODO Auto-generated method stub
        this.mBasePreferences.setPrefFloat(key, value);
    }

    @Override
    public float getPrefFloat(String key) {
        // TODO Auto-generated method stub
        return this.mBasePreferences.getPrefFloat(key);
    }

    @Override
    public float getPrefFloat(String key, float defVal) {
        // TODO Auto-generated method stub
        return this.mBasePreferences.getPrefFloat(key, defVal);
    }


    @Override
    public List<String> getPrefStringList(String key) {
        // TODO Auto-generated method stub
        return this.mBasePreferences.getPrefStringList(key);
    }

    @Override
    public List<String> getPrefStringList(String key, List<String> defValues) {
        // TODO Auto-generated method stub
        return this.mBasePreferences.getPrefStringList(key, defValues);
    }

    @Override
    public void setPrefStringList(String key, List<String> values) {
        // TODO Auto-generated method stub
        this.mBasePreferences.setPrefStringList(key, values);
    }
}
