package com.iii360.box.protocol;

public interface Client {
    /**
     * 连接
     */
    public void connect();

    /**
     * @return 是否连接
     */
    public boolean isConnect();

    /**
     * 发送数据
     * @param data
     * @return
     */
    public boolean send(byte[] data);
    

    /**
     * 关闭
     */
    public void close();
}
