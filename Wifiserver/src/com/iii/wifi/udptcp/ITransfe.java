package com.iii.wifi.udptcp;

/**
 * 发送数据
 * @author river
 *
 * @date 2014-11-18
 */
public interface ITransfe {
    
    public boolean send(String message);

	public boolean send(byte[] message);

}
