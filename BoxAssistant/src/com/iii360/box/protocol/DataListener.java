package com.iii360.box.protocol;

/**
 * 接收和发送数据接口
 * @author hefeng
 *
 */
public interface DataListener {

    /**
     * @param data  接收的数据
     */
    public void onReceiver(String data);
    
    /**
     * @param data 发送的数据
     * @param isSuccess 发送是否成功
     */
    public void onSend(String data,boolean isSuccess) ;
}
