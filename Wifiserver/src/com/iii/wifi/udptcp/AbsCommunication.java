package com.iii.wifi.udptcp;

/**
 * 通信协议
 * 
 * @author river
 * 
 * @date 2014-11-18
 */
public abstract class AbsCommunication {
	protected IReceiverListener receiverListener;
	protected String ip;
	protected int port;

	public AbsCommunication(String ip, int port) {
		// TODO Auto-generated constructor stub
		this.ip = ip;
		this.port = port;
	}

	public void setReceiverListener(IReceiverListener receiverListener) {
		this.receiverListener = receiverListener;
	}

	/**
	 * 连接
	 * 
	 * @return
	 */
	public abstract boolean connect();

	/**
	 * 取消连接
	 */
	public abstract void close();
    
	/**
	 * 接收数据
	 */
	public abstract void receive();

}
