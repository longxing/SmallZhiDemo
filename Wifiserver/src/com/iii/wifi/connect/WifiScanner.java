package com.iii.wifi.connect;

import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;

import com.iii.wifi.manager.impl.IBindBroadcastReceiver;
import com.iii.wifi.util.WaitUtil;
import com.iii360.sup.common.utl.LogManager;

public class WifiScanner extends AbsWifiManager implements IBindBroadcastReceiver {
    private long startTime;
    private boolean mScannerResult = false;

    public WifiScanner(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
    }

    public void scanner() {
        mScannerResult = false;
        startTime = System.currentTimeMillis();
        mWifiManager.startScan();
    }

    public boolean checkScannerWifiResult() {
        registerReceiver();
        
        long time = System.currentTimeMillis();

        while (!mScannerResult ) {
            if(System.currentTimeMillis() - time > 15000){
                return false;
            }
            WaitUtil.sleep(1000);
        }
        unregisterReceiver() ;
        return true;
    }

    public List<ScanResult> getScanResults() {
        return mWifiManager.getScanResults();
    }

    // check ap is now available
    Boolean checkBSAvailable(final String ssid){
    		List<ScanResult> result = mWifiManager.getScanResults();
    		for(ScanResult item : result){
    			if(item.BSSID == ssid){
    				return true;
    			}
    		}
    		return false;
    }
    
    /**
     * 获取wifi加密类型
     * 
     * @param ssid
     * @return
     */
    public WifiCipherType getCipherType(String ssid) {     
        return  WifiUtils.getCipherType(context, ssid);
    }

    @Override
    public void registerReceiver() {
        // TODO Auto-generated method stub
        context.registerReceiver(receiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
    }

    @Override
    public void unregisterReceiver() {
        // TODO Auto-generated method stub
        try {
            context.unregisterReceiver(receiver);
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            if (intent.getAction().equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)) {
                mScannerResult = true;
                LogManager.e(WIFI_HEAD + "scanner time=" + (System.currentTimeMillis() - startTime));
            }
        }
    };

    @Override
    public void destroy() {
        // TODO Auto-generated method stub
        unregisterReceiver();
    }

}
