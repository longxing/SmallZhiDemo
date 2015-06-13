package com.iii.wifi.dao.manager;

import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

import com.iii.wifi.dao.info.WifiJSONObjectInfo;
import com.iii.wifi.dao.info.WifiVolume;

public class WifiCRUDForVolume {
    private String mIp;
    private int mPort;
    public static final String GET_BOX_VOLUME = "GET_BOX_VOLUME";
    public static final String SET_BOX_VOLUME = "SET_BOX_VOLUME";
    public static final String GET_TTS_VOLUME = "GET_TTS_VOLUME";
    public static final String SET_TTS_VOLUME = "SET_TTS_VOLUME";

    public WifiCRUDForVolume(String ip, int port) {
        mIp = ip;
        mPort = port;
    }

    public interface ResultForVolumeListener {
        public void onResult(String type, String errorCode, WifiVolume wifiVolume);
    }

    public void getVolumeInfo(final ResultForVolumeListener resultListener) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Socket socket = new Socket();
                WifiJSONObjectInfo result;
                try {
                    if (!socket.isConnected()) {
                        socket.connect(new InetSocketAddress(mIp, mPort), 5000);
                    }
                    WifiVolume wifiVolume = new WifiVolume();
                    wifiVolume.setType(GET_BOX_VOLUME);
                    String obj = WifiCreateAndParseSockObjectManager.createWifiVolumeInfos(WifiCreateAndParseSockObjectManager.WIFI_INFO_DEFAULT,
                            wifiVolume)+ "\n";
                    OutputStream outputStream;
                    outputStream = socket.getOutputStream();
                    outputStream.write(obj.toString().getBytes());
                    outputStream.flush();
                    result = WifiCRUDForClient.findData(socket, null);
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    resultListener.onResult("", WifiCreateAndParseSockObjectManager.WIFI_INFO_ERROR, null);
                    return;
                }
                WifiVolume infos = (WifiVolume) result.getObject();
                resultListener.onResult(result.getType(), result.getError(), infos);
            }
        }).start();
    }

    public void setVolumeInfo(final int volume, final ResultForVolumeListener resultListener) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Socket socket = new Socket();
                WifiJSONObjectInfo result;
                try {
                    if (!socket.isConnected()) {
                        socket.connect(new InetSocketAddress(mIp, mPort), 5000);
                    }
                    WifiVolume wifiVolume = new WifiVolume();
                    wifiVolume.setVolume(volume);
                    wifiVolume.setType(SET_BOX_VOLUME);
                    String obj = WifiCreateAndParseSockObjectManager.createWifiVolumeInfos(WifiCreateAndParseSockObjectManager.WIFI_INFO_DEFAULT,
                            wifiVolume)+ "\n";
                    OutputStream outputStream;
                    outputStream = socket.getOutputStream();
                    outputStream.write(obj.toString().getBytes());
                    outputStream.flush();
                    result = WifiCRUDForClient.findData(socket, null);
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    resultListener.onResult("", WifiCreateAndParseSockObjectManager.WIFI_INFO_ERROR, null);
                    return;
                }
                WifiVolume infos = (WifiVolume) result.getObject();
                resultListener.onResult(result.getType(), result.getError(), infos);
            }
        }).start();
    }
    
    
    public void getTTSVolumeInfo(final ResultForVolumeListener resultListener) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Socket socket = new Socket();
                WifiJSONObjectInfo result;
                try {
                    if (!socket.isConnected()) {
                        socket.connect(new InetSocketAddress(mIp, mPort), 5000);
                    }
                    WifiVolume wifiVolume = new WifiVolume();
                    wifiVolume.setType(GET_TTS_VOLUME);
                    String obj = WifiCreateAndParseSockObjectManager.createTTSWifiVolumeInfos(WifiCreateAndParseSockObjectManager.WIFI_INFO_DEFAULT,
                            wifiVolume)+ "\n";
                    OutputStream outputStream;
                    outputStream = socket.getOutputStream();
                    outputStream.write(obj.toString().getBytes());
                    outputStream.flush();
                    result = WifiCRUDForClient.findData(socket, null);
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    resultListener.onResult("", WifiCreateAndParseSockObjectManager.WIFI_INFO_ERROR, null);
                    return;
                }
                WifiVolume infos = (WifiVolume) result.getObject();
                resultListener.onResult(result.getType(), result.getError(), infos);
            }
        }).start();
    }

    public void setTTSVolumeInfo(final int volume, final ResultForVolumeListener resultListener) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Socket socket = new Socket();
                WifiJSONObjectInfo result;
                try {
                    if (!socket.isConnected()) {
                        socket.connect(new InetSocketAddress(mIp, mPort), 5000);
                    }
                    WifiVolume wifiVolume = new WifiVolume();
                    wifiVolume.setVolume(volume);
                    wifiVolume.setType(SET_TTS_VOLUME);
                    String obj = WifiCreateAndParseSockObjectManager.createTTSWifiVolumeInfos(WifiCreateAndParseSockObjectManager.WIFI_INFO_DEFAULT,
                            wifiVolume)+ "\n";
                    OutputStream outputStream;
                    outputStream = socket.getOutputStream();
                    outputStream.write(obj.toString().getBytes());
                    outputStream.flush();
                    result = WifiCRUDForClient.findData(socket, null);
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    resultListener.onResult("", WifiCreateAndParseSockObjectManager.WIFI_INFO_ERROR, null);
                    return;
                }
                WifiVolume infos = (WifiVolume) result.getObject();
                resultListener.onResult(result.getType(), result.getError(), infos);
            }
        }).start();
    }
}
