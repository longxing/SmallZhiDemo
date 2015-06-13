package com.base.secure;

public class Author {
    
    public static byte[] encryption(String text) {
        byte[] data = null;
        if(text != null) {
            data = text.getBytes();
            for(int i = 0;i < data.length;i++) {
                data[i] ^= i;
            }
             
        }
        return data;
    }
    
    public static String decode(byte[] data) {
        String result = null;
        if(data != null) {
            for(int i = 0;i < data.length;i++) {
                data[i] ^= i;
            }
            result = new String(data);
        }
        return result;
        
    }
    
    public static boolean checkId(String appId) {
        return true;
        
    }
    
    
}
