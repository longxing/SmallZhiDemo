package com.iii.wifi.util;

import java.util.List;
import java.util.Set;

public interface IPreferences {
    
    public void setPrefBoolean(String key, boolean value);

    public boolean getPrefBoolean(String key);

    public boolean getPrefBoolean(String key, boolean defVal);

    public void setPrefString(String key, String value);

    public String getPrefString(String key);

    public String getPrefString(String key, String defVal);

    public void setPrefInteger(String key, int value);

    public int getPrefInteger(String key);

    public int getPrefInteger(String key, int defVal);

    public void setPrefLong(String key, long value);

    public long getPrefLong(String key);

    public long getPrefLong(String key, long defVal);

    public void setPrefFloat(String key, float value);

    public float getPrefFloat(String key);

    public float getPrefFloat(String key, float defVal);
    
    
//    public void setPrefStringSet(String key, Set<String> values);
//    public Set<String> getPrefStringSet(String key, Set<String> defValues);
//    public Set<String> getPrefStringSet(String key);
    
    public List<String> getPrefStringList(String key);
    public List<String> getPrefStringList(String key, List<String> defValues);
    public void setPrefStringList(String key, List<String> values);
}
