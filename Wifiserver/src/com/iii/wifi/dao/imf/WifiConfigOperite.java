package com.iii.wifi.dao.imf;

import java.util.List;

import android.content.Context;

import com.iii.wifi.dao.info.WifiDeviceInfo;
import com.iii.wifi.dao.inter.IOtherControl;
import com.iii.wifi.thirdpart.inter.JSLearnDevice;
import com.iii.wifi.thirdpart.inter.JSSearchDevice;
import com.iii.wifi.util.KeyList;
import com.iii360.sup.common.utl.SuperBaseContext;

public class WifiConfigOperite implements IOtherControl {
    public static final String LEARN_HF = "learnHF";
    public static final String GET_UNCONFIGED_DEVICE = "unconfigedDevice";
    public static final String PLAY_TTS = "play_tts";

    private SuperBaseContext mBaseContext;

    private JSSearchDevice mJSSearchDevice;
    private JSLearnDevice mJSLearnDevice;

    public WifiConfigOperite(Context context) {
        mBaseContext = new SuperBaseContext(context);
        mJSSearchDevice = new JSSearchDevice(context);
        mJSLearnDevice = new JSLearnDevice(context);
    }

    @Override
    public String startLearnHF(String DeviceId) {
        return mJSLearnDevice.startLearnHF(DeviceId);
    }


    @Override
    public List<WifiDeviceInfo> getUnConfigedDevice() {
        if (!mBaseContext.getGlobalBoolean(KeyList.GKEY_WIFI_DOG_FOUNDED)) {
            try {
                Thread.sleep(KeyList.SHORT_DELAY_TIME);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return mJSSearchDevice.getUnConfigedDeviceList();
    }


}
