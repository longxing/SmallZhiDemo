package com.smallzhi.homeappliances.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * 网络客户端，用户访问服务器
 * 
 * 
 */
public class Client {

	private Socket socket;
	private OutputStream out;
	private InputStream in;
	private String ip;
	private int port;

	private static Client client;

	public void setIp$Port(String ip, int port) {
		this.ip = ip;
		this.port = port;
	}

	/**
	 * 心跳包
	 * 
	 * @return true 连接状态 false 不能连接状态
	 */
	public boolean getSocketState() {
		boolean lCon = false;
		try {
			if (socket != null) {
				socket.sendUrgentData(0xFF);
				lCon = true;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			lCon = false;
		}
		return lCon;
	}

	public InputStream getInputThread() {
		return in;
	}

	public OutputStream getOutputStream() {
		return out;
	}

	/**
	 * 连接服务器
	 * 
	 * @return 是否连接成功
	 */
	public synchronized boolean start() {
		try {
			if (socket != null) {
				closeClient();
			}

			socket = new Socket();
			socket.connect(new InetSocketAddress(ip, port), 1000);
			if (socket.isConnected()) {
				out = socket.getOutputStream();
				in = socket.getInputStream();
			}
		} catch (IOException e) {
			e.printStackTrace();
			closeClient();
			return false;
		}
		return true;
	}

	/**
	 * 关闭Client
	 */
	public synchronized void closeClient() {

		// LoginActivity.logger("关闭client");
		if (socket == null) {
			return;
		}

		try {
			socket.close();
			socket = null;

			if (out != null) {
				out.close();
				out = null;
			}
			if (in != null) {
				in.close();
				in = null;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
