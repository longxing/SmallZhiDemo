package com.iii360.sup.common.utl.net;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.HashSet;
import java.util.Set;

import com.iii360.sup.common.utl.LogManager;

public class UdpRunnable implements Runnable {

	public final static int Muticast_Type = 0;
	public final static int Singlecast_Type = 1;
	public final static int Response_Singlecast_Type = 2;
	public final static int OnceSinglecast_Type = 4;
	public final static int BroadCast_Type = 5;
	private int CastType = -1; // 播报类型
	private byte[] data = new byte[1024];
	private String ip = "127.0.0.1";
	private int port = 80;
	private static Set<String> ipSet = null;

	public UdpRunnable(int castType, byte[] data, String ip, int port) {
		super();
		CastType = castType;
		this.data = data;
		this.ip = ip;
		this.port = port;
	}

	public UdpRunnable(int castType, byte[] data, Set<String> ipSet, int port) {
		super();
		CastType = castType;
		this.data = data;
		this.port = port;
	}

	public static Set<String> getIpSet() {
		if (ipSet == null) {
			ipSet = new HashSet<String>();
		}
		return ipSet;
	}

	public static void setIpSet(Set<String> ipSet) {
		UdpRunnable.ipSet = ipSet;
	}

	/**
	 * 动态添加助手端的IP
	 * 
	 * @param ip
	 */
	public static void addIpToList(String ip) {
		UdpRunnable.ipSet.add(ip);
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		switch (CastType) {
		case Muticast_Type:
			sendMulticast(data, ip, port);
			break;
		case Singlecast_Type:
			sendSinglecast(data, port);
			break;
		case Response_Singlecast_Type:
			sendResposeSinglecast(data, ip, port);
			break;
		case OnceSinglecast_Type:
			sendSinglecast(data, port);
		case BroadCast_Type:
			sendBroadCastToPhone(data, ip, port);
			break;
		default:
			break;
		}
	}

	/**
	 * 发送组播
	 * 
	 * @param ipAddress
	 * @param port
	 * @param msg
	 * @throws Exception
	 */
	private void sendMulticast(byte[] data, String ip, int port) {
		InetAddress group = null;
		MulticastSocket ms = null;
		try {
			group = InetAddress.getByName(ip);
			ms = new MulticastSocket(port);
			ms.joinGroup(group);

			DatagramPacket dp = new DatagramPacket(data, data.length, group, port);
			for (int i = 0; i < 1000; i++) {
				ms.send(dp);
				LogManager.d("sendMulticast --- ip:" + ip + "--- port:" + port + "---data:" + new String(data)+"count:"+i);
				Thread.sleep(1000);
			}
		} catch (Exception e) {
			LogManager.printStackTrace(e);
		} finally {
			try {
				if (ms != null && group != null) {
					ms.leaveGroup(group);
					ms.close();
				}
			} catch (IOException e) {
				LogManager.printStackTrace(e);
			}
		}

	}
	
	
	
	/**
	 * 发送组播
	 * 
	 * @param ipAddress
	 * @param port
	 * @param msg
	 * @throws Exception
	 */
	private void sendBroadCastToPhone(byte[] data, String ip, int port) {
		DatagramSocket sender = null;
		try {
			sender = new DatagramSocket();
			sender.setBroadcast(true);
			InetAddress broadAddress = InetAddress.getByName(ip);
			DatagramPacket packet = new DatagramPacket(data, data.length, broadAddress, port);
			for (int i = 0; i < 1000; i++) {
				sender.send(packet);
				LogManager.d("sendBroadCastToPhone --- ip:" + ip + "--- port:" + port + "---data:" + new String(data)+"count:"+i);
				Thread.sleep(1000);
			}
		} catch (Exception e) {
			LogManager.printStackTrace(e);
		} finally {
			try {
				if (sender != null) {
					sender.close();
				}
			} catch (Exception e) {
				LogManager.printStackTrace(e);
			}
		}

	}

	/**
	 * 发送单播
	 * 
	 * @param data
	 * @param ip
	 * @param port
	 */
	private void sendSinglecast(byte[] data, int port) {
		DatagramSocket sender = null;
		try {
			sender = new DatagramSocket();
			for (String ip : ipSet) {
				LogManager.d("sendSinglecast --- ip:" + ip + "--- port:" + port + "---data:" + new String(data) + "---set:" + ipSet);
				InetAddress broadAddress = InetAddress.getByName(ip);
				DatagramPacket packet = new DatagramPacket(data, data.length, broadAddress, port);
				sender.send(packet);
			}
		} catch (Exception e) {
			LogManager.printStackTrace(e);
		} finally {
			if (sender != null) {
				sender.close();
			}
		}
	}

	/**
	 * 发送单播
	 * 
	 * @param data
	 * @param ip
	 * @param port
	 */
	private void sendResposeSinglecast(byte[] data, String ip, int port) {
		DatagramSocket sender = null;
		try {
			sender = new DatagramSocket();
			LogManager.d("sendResposeSinglecast --- ip:" + ip + "--- port:" + port + "---data:" + new String(data));
			InetAddress broadAddress = InetAddress.getByName(ip);
			DatagramPacket packet = new DatagramPacket(data, data.length, broadAddress, port);
			sender.send(packet);
		} catch (Exception e) {
			LogManager.printStackTrace(e);
		} finally {
			if (sender != null) {
				sender.close();
			}
		}
	}

	// 开始执行
	public void startRunnable() {
		new Thread(this).start();
	}
}
