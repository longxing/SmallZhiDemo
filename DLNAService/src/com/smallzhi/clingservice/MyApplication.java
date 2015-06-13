package com.smallzhi.clingservice;

import org.fourthline.cling.UpnpService;
import org.fourthline.cling.model.meta.Device;

import com.smallzhi.clingservice.media.IPlayService;

import android.app.Application;

public class MyApplication extends com.example.common.MyApplication{
    private UpnpService upnpService;
    private Device mSelectedDevice;
    private IPlayService mPlayService;
    private static MyApplication mMyApplication;

    public static synchronized MyApplication getInstance() {
        if (mMyApplication == null) {
            mMyApplication = new MyApplication();
        }

        return mMyApplication;
    }

    public IPlayService getPlayService() {
        return mPlayService;
    }

    public void setPlayService(IPlayService playService) {
        if (playService != null) {
            this.mPlayService = playService;
        }
    }

    public UpnpService getUpnpService() {
        return upnpService;
    }

    public void setUpnpService(UpnpService upnpService) {
        this.upnpService = upnpService;
    }

    public Device getmSelectedDevice() {
        return mSelectedDevice;
    }

    public void setmSelectedDevice(Device mSelectedDevice) {
        this.mSelectedDevice = mSelectedDevice;
    }
}
