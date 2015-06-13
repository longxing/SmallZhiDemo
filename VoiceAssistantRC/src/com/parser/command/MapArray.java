package com.parser.command;

import java.util.Collection;
import java.util.HashMap;

class MapArray {
    private HashMap<String, String> mMap = new HashMap<String, String>();
    
    public String getValue(String key) {
        return mMap.get(key);
    }
    
    public void addKeys(Collection<String> keys, String val) {
        for(String item : keys) {
            mMap.put(item, val);
        }
    }
    
    public void addKeys(String[] keys, String val) {
        for(String item : keys) {
            mMap.put(item, val);
        }
    }
}
