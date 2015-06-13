package com.iii.wifi.dao.manager;

import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.List;

import com.iii.wifi.dao.info.WifiBoxModeInfo;
import com.iii.wifi.dao.info.WifiBoxModeInfos;
import com.iii.wifi.dao.info.WifiJSONObjectInfo;
import com.iii.wifi.dao.newmanager.AbsWifiCRUDForObject;

/**
 * 场景模式数据传输
 * @author Administrator
 *
 */
public class WifiCRUDForBoxMode extends AbsWifiCRUDForObject {

    public WifiCRUDForBoxMode(String ip, int port) {
        super(ip, port);
        // TODO Auto-generated constructor stub
    }

    public interface ResultForBoxModeListener {
        public void onResult(String type, String errorCode, List<WifiBoxModeInfo> infos);
    }

    
    /**
     * @param modeName
     * @param controlIds 控制设备的ID，格式:ID+"||"+ID
     * @param resultListener
     */
    public void setBoxMode(final String modeName,final String controlIds, final ResultForBoxModeListener resultListener) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Socket socket = new Socket();
                WifiJSONObjectInfo result;
                try {
                    if (!socket.isConnected()) {
                        socket.connect(new InetSocketAddress(ip, port), 5000);
                    }
                    WifiBoxModeInfo info = new WifiBoxModeInfo();
                    info.setModeName(modeName);
                    info.setControlIDs(controlIds);
                    
                    String obj = WifiCreateAndParseSockObjectManager.createWifiBoxModeInfos(DB_ADD,
                            WifiCreateAndParseSockObjectManager.WIFI_INFO_DEFAULT, info)+"\n";
                    OutputStream outputStream;
                    outputStream = socket.getOutputStream();
                    outputStream.write(obj.toString().getBytes());
                    outputStream.flush();
                    result = WifiCRUDForClient.findData(socket, null);
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    resultListener.onResult(DB_ADD, WifiCreateAndParseSockObjectManager.WIFI_INFO_ERROR, null);
                    return;
                }
                resultListener.onResult(result.getType(), result.getError(), ((WifiBoxModeInfos) result.getObject()).getWifiInfos());
            }
        }).start();
    }
    
    public void getBoxModeList(final ResultForBoxModeListener resultListener) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Socket socket = new Socket();
                WifiJSONObjectInfo result;
                try {
                    if (!socket.isConnected()) {
                        socket.connect(new InetSocketAddress(ip, port), 5000);
                    }
                    String obj = WifiCreateAndParseSockObjectManager.createWifiBoxModeInfos(DB_SELECT,
                            WifiCreateAndParseSockObjectManager.WIFI_INFO_DEFAULT, new WifiBoxModeInfo())+"\n";
                    OutputStream outputStream;
                    outputStream = socket.getOutputStream();
                    outputStream.write(obj.toString().getBytes());
                    outputStream.flush();
                    result = WifiCRUDForClient.findData(socket, null);
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    resultListener.onResult(DB_SELECT, WifiCreateAndParseSockObjectManager.WIFI_INFO_ERROR, null);
                    return;
                }
                resultListener.onResult(result.getType(), result.getError(), ((WifiBoxModeInfos) result.getObject()).getWifiInfos());
            }
        }).start();
    }
    
    public void getBoxModeByName(final String modeName , final ResultForBoxModeListener resultListener) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Socket socket = new Socket();
                WifiJSONObjectInfo result;
                try {
                    if (!socket.isConnected()) {
                        socket.connect(new InetSocketAddress(ip, port), 5000);
                    }
                    WifiBoxModeInfo mWifiBoxModeInfo = new WifiBoxModeInfo() ;
                    mWifiBoxModeInfo.setModeName(modeName);
                    
                    String obj = WifiCreateAndParseSockObjectManager.createWifiBoxModeInfos(DB_SELECT_BY_NAME,
                            WifiCreateAndParseSockObjectManager.WIFI_INFO_DEFAULT, mWifiBoxModeInfo)+"\n";
                    OutputStream outputStream;
                    outputStream = socket.getOutputStream();
                    outputStream.write(obj.toString().getBytes());
                    
                    outputStream.flush();
                    result = WifiCRUDForClient.findData(socket, null);
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    resultListener.onResult(DB_SELECT_BY_NAME, WifiCreateAndParseSockObjectManager.WIFI_INFO_ERROR, null);
                    return;
                }
                resultListener.onResult(result.getType(), result.getError(), ((WifiBoxModeInfos) result.getObject()).getWifiInfos());
            }
        }).start();
    }
}
