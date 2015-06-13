package com.iii.wifi.dao.manager;

import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.List;

import com.iii.wifi.dao.info.WifiJSONObjectInfo;
import com.iii.wifi.dao.info.WifiStringInfo;
import com.iii.wifi.dao.info.WifiStringInfos;
import com.iii.wifi.dao.newmanager.AbsWifiCRUDForObject;

public class WifiCRUDForString extends AbsWifiCRUDForObject {

    public WifiCRUDForString(String ip, int port) {
        super(ip, port);
        // TODO Auto-generated constructor stub
    }

    public interface ResultForStringListener {
        public void onResult(String type, String errorCode, List<WifiStringInfo> infos);
    }
    
    /**
     * @param msg
     *            数据
     * @param type
     *            操作类型
     * @param resultListener
     */
    public void sendString(final String msg, final String type, final ResultForStringListener resultListener) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Socket socket = new Socket();
                WifiJSONObjectInfo result;
                try {
                    if (!socket.isConnected()) {
                        socket.connect(new InetSocketAddress(ip, port), 5000);
                    }
                    WifiStringInfo info = new WifiStringInfo();
                    info.setMessage(msg);
                    info.setType(type);

                    String obj = WifiCreateAndParseSockObjectManager.createWifiStringInfos(DB_SELECT,
                            WifiCreateAndParseSockObjectManager.WIFI_INFO_DEFAULT, info)+ "\n";
                    OutputStream outputStream = socket.getOutputStream();
                    outputStream.write(obj.toString().getBytes());
                    outputStream.flush();
                    result = WifiCRUDForClient.findData(socket, null);
                    outputStream.close();
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    resultListener.onResult("", WifiCreateAndParseSockObjectManager.WIFI_INFO_ERROR, null);
                    return;
                }
                resultListener.onResult(result.getType(), result.getError(), ((WifiStringInfos) result.getObject()).getInfos());
            }
        }).start();
    }
}
