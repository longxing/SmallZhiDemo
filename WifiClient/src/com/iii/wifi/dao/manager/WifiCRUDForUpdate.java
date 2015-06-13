package com.iii.wifi.dao.manager;

import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.List;

import com.iii.wifi.dao.info.WifiJSONObjectInfo;
import com.iii.wifi.dao.info.WifiUpdateInfo;
import com.iii.wifi.dao.info.WifiUpdateInfos;
import com.iii.wifi.dao.newmanager.AbsWifiCRUDForObject;

public class WifiCRUDForUpdate extends AbsWifiCRUDForObject {

    public WifiCRUDForUpdate(String ip, int port) {
        super(ip, port);
        // TODO Auto-generated constructor stub
    }

    public interface ResultForUpdateListener {
        public void onResult(String type, String errorCode, List<WifiUpdateInfo> infos);
    }

    /**
     * 通过更新roomName和deviceName更新数据库
     * @param roomName
     * @param deviceName
     * @param resultListener
     */
    public void update(final WifiUpdateInfo info, final ResultForUpdateListener resultListener) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Socket socket = new Socket();
                WifiJSONObjectInfo result;
                try {
                    if (!socket.isConnected()) {
                        socket.connect(new InetSocketAddress(ip, port), 5000);
                    }
                    String obj = WifiCreateAndParseSockObjectManager.createWifiUpdateInfos(DB_UPDATA,
                            WifiCreateAndParseSockObjectManager.WIFI_INFO_DEFAULT, info)+ "\n";
                    OutputStream outputStream;
                    outputStream = socket.getOutputStream();
                    outputStream.write(obj.toString().getBytes());
                    outputStream.flush();
                    result = WifiCRUDForClient.findData(socket, null);
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    resultListener.onResult(MUSIC_SELECT, WifiCreateAndParseSockObjectManager.WIFI_INFO_ERROR, null);
                    return;
                }
                resultListener.onResult(result.getType(), result.getError(), ((WifiUpdateInfos) result.getObject()).getWifiInfos());
            }
        }).start();
    }
}
