package com.iii.wifi.dao.newmanager;

import java.net.Socket;

import com.iii.wifi.dao.info.WifiBoxSystemInfo;
import com.iii.wifi.dao.info.WifiBoxSystemInfos;
import com.iii.wifi.dao.info.WifiJSONObjectInfo;
import com.iii.wifi.dao.manager.WifiCreateAndParseSockObjectManager;

/**
 * 音箱系统信息
 * 
 * @author Administrator
 * 
 */
public class WifiCRUDForBoxSystem extends AbsWifiCRUDForObject {

    public WifiCRUDForBoxSystem(String ip, int port) {
        super(ip, port);
        // TODO Auto-generated constructor stub
    }

    public interface ResultListener {
        public void onResult(String code, WifiBoxSystemInfo info);
    }

    public void getSystemInfo(final ResultListener listener) {
        new Thread(new Runnable() {

            @Override
            public void run() {
                WifiJSONObjectInfo resultObj = null;

                try {

                    Socket socket = connect();
                    String obj = WifiCreateAndParseSockObjectManager.createWifiBoxSystemInfos(DB_SELECT,
                            WifiCreateAndParseSockObjectManager.WIFI_INFO_DEFAULT, null)+ "\n";
                    resultObj = getResult(socket, obj);

                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    listener.onResult(WifiCreateAndParseSockObjectManager.WIFI_INFO_ERROR, null);
                    return;
                }
                WifiBoxSystemInfos infos = (WifiBoxSystemInfos) (resultObj.getObject()) ;
                listener.onResult(resultObj.getError(), infos.getInfo());
            }
        }).start();
    }

}
