package com.iii.wifi.udptcp;

public interface IReceiverListener {

    /**
     * data和message数据相同，只是格式不同
     * 
     * @param data
     * @param message
     */
    public void onReceived(byte[] data, String message);

}
