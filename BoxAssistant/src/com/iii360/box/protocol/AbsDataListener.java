package com.iii360.box.protocol;

/**
 * 设置接收和发送数据回调接口
 * @author hefeng
 *
 */
public abstract class AbsDataListener {
    
    protected DataListener dataListener;

    public DataListener getDataListener() {
        return dataListener;
    }

    public void setDataListener(DataListener dataListener) {
        this.dataListener = dataListener;
    }

    /**
     * 设置接收数据回调接口
     * @param data  接收的数据
     */
    public void setReceiver(String data) {
        if (this.dataListener != null) {
            this.dataListener.onReceiver(data);
        }
    }
    /**
     * 设置发送数据回调接口
     * @param data 发送的数据
     * @param isSuccess 发送是否成功
     */
    public void setSend(String data,boolean isSuccess) {
        if (this.dataListener != null) {
           this.dataListener.onSend(data,isSuccess);
        }
    }
}
