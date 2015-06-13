package com.base.resource;

import java.io.InputStream;

public class ResourceManagerLinux implements IResourceManager {

    private final static String HEAD = "/assets/";
    @Override
    public InputStream getInputStream(Object context, String path) {
        InputStream input = this.getClass().getResourceAsStream(HEAD + path);
        return input;
    }

    @Override
    public String[] list(Object context, String string) {
        //this.getClass().
        return new String[] {
                             "CommandLocalHot",
                             "CommandLocalNavi", 
                             "CommandPlayMedia", 
                             "CommandQueryWeather",
                             "CommandReadSms", 
                             "CommandSendSms", 
                             "CommandTranslation"
                             };
    }

    @Override
    public boolean checkStorageVaild() {
        // TODO Auto-generated method stub
        return true;
    }

}
