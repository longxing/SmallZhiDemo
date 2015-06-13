package com.iii.wifi.dao.manager;

import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

import com.iii.wifi.dao.info.WifiJSONObjectInfo;
import com.iii.wifi.dao.info.WifiUserData;
import com.iii.wifi.dao.newmanager.AbsWifiCRUDForObject;

public class WifiCRUDForUserData extends AbsWifiCRUDForObject {
    public WifiCRUDForUserData(String ip, int port) {
        super(ip, port);
        // TODO Auto-generated constructor stub
    }

    public interface ResultForUserDataListener {
        public void onResult(String type, String errorCode, WifiUserData userData);
    }

    public void setUserData(final WifiUserData userData, final ResultForUserDataListener resultListener) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Socket socket = new Socket();
                WifiJSONObjectInfo result;
                try {
                    if (!socket.isConnected()) {
                        socket.connect(new InetSocketAddress(ip, port), 5000);
                    }

                    userData.setType(OPERATION_TYPE_SET);

                    String obj = WifiCreateAndParseSockObjectManager.createWifiUserDataInfos(WifiCreateAndParseSockObjectManager.WIFI_INFO_DEFAULT,
                            userData)+ "\n";
                    OutputStream outputStream;
                    outputStream = socket.getOutputStream();
                    outputStream.write(obj.toString().getBytes());
                    outputStream.flush();
                    result = WifiCRUDForClient.findData(socket, null);
                    outputStream.close();
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    resultListener.onResult("", WifiCreateAndParseSockObjectManager.WIFI_INFO_ERROR, null);
                    return;
                }
                WifiUserData infos = (WifiUserData) result.getObject();
                resultListener.onResult(result.getType(), result.getError(), infos);
            }
        }).start();
    }
    public void getUserData(final String imei,final ResultForUserDataListener resultListener) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Socket socket = new Socket();
                WifiJSONObjectInfo result;
                try {
                    if (!socket.isConnected()) {
                        socket.connect(new InetSocketAddress(ip, port), 5000);
                    }
                    WifiUserData userData = new WifiUserData() ;
                    userData.setType(OPERATION_TYPE_GET);
                    userData.setImei(imei);
                    
                    String obj = WifiCreateAndParseSockObjectManager.createWifiUserDataInfos(WifiCreateAndParseSockObjectManager.WIFI_INFO_DEFAULT,
                            userData)+ "\n";
                    OutputStream outputStream;
                    outputStream = socket.getOutputStream();
                    outputStream.write(obj.toString().getBytes());
                    outputStream.flush();
                    result = WifiCRUDForClient.findData(socket, null);
                    outputStream.close();
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    resultListener.onResult("", WifiCreateAndParseSockObjectManager.WIFI_INFO_ERROR, null);
                    return;
                }
                WifiUserData infos = (WifiUserData) result.getObject();
                resultListener.onResult(result.getType(), result.getError(), infos);
            }
        }).start();
    }
    
//    public void getMyTag(final ResultForVolumeListener resultListener) {
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                Socket socket = new Socket();
//                WifiJSONObjectInfo result;
//                try {
//                    if (!socket.isConnected()) {
//                        socket.connect(new InetSocketAddress(ip, port), 5000);
//                    }
//
//                    WifiMyTag myTag = new WifiMyTag();
//                    myTag.setType(OPERATION_TYPE_GET);
//
//                    String obj = WifiCreateAndParseSockObjectManager.createWifiMyTagInfos(WifiCreateAndParseSockObjectManager.WIFI_INFO_DEFAULT,
//                            myTag);
//                    OutputStream outputStream;
//                    outputStream = socket.getOutputStream();
//                    outputStream.write(obj.toString().getBytes());
//                    outputStream.flush();
//                    result = WifiCRUDForClient.findData(socket, null);
//                } catch (Exception e) {
//                    // TODO Auto-generated catch block
//                    resultListener.onResult("", WifiCreateAndParseSockObjectManager.WIFI_INFO_ERROR, null);
//                    return;
//                }
//                WifiMyTag infos = (WifiMyTag) result.getObject();
//                resultListener.onResult(result.getType(), result.getError(), infos);
//            }
//        }).start();
//    }
}
