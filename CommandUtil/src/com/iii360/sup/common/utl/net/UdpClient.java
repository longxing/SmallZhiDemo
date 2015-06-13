package com.iii360.sup.common.utl.net;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.util.Set;

import android.content.Context;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;

import com.iii360.sup.common.utl.LogManager;
import com.iii360.sup.common.utl.SupKeyList;
import com.iii360.sup.common.utl.SuperBaseContext;

public class UdpClient {
	private static int sendPort = SupWifiConfig.BOX_SEND_PORT;
	private udpOngetData mGetData;
	private UdpPortListener udpListener;
	private boolean runing = false;
	private SuperBaseContext mBaseContext;
	private DatagramSocket serverSocket = null;
	private final String mutiCastIp = SupWifiConfig.PHONE_SET_MUTICAST_IP;
	private MulticastSocket muticastSocket = null;
	private static int SingleCastSendDelayTime = 3000;

	private DatagramSocket sender = null;

	/**
	 * 收到的数据回调接口
	 * 
	 * @author Peter
	 */
	public interface udpOngetData {
		public void ongetdata(DatagramPacket receivePacket);
	}

	public interface UdpPortListener {
		public void onPortListenter(DatagramPacket receivePacket);
	}

	/**
	 * 设置回调接口
	 * 
	 * @param data
	 */
	public void setonGetData(udpOngetData data) {
		mGetData = data;
	}

	public void setUdpPortListener(UdpPortListener udpPortListener) {
		udpListener = udpPortListener;
	}

	/**
	 * 通过上下文获取udp实例
	 * 
	 * @param mBaseContext
	 * @return
	 */
	public static UdpClient getInstance(SuperBaseContext mBaseContext,
			boolean isOldport) {
		if (isOldport) {
			return getInstance(SupWifiConfig.UDP_DEFAULT_PORT, mBaseContext);
		} else {
			return getInstance(SupWifiConfig.BOX_SEND_PORT,
					SupWifiConfig.PHONE_SEND_PORT, mBaseContext);
		}

	}

	/**
	 * old get udp instatnce method
	 * 
	 * @param sendPort
	 * @param mBaseContext
	 * @return
	 */
	public static UdpClient getInstance(int sendPort,
			SuperBaseContext mBaseContext) {
		UdpClient udpClient = (UdpClient) mBaseContext.getGlobalObject("UDP"
				+ sendPort);
		if (udpClient == null) {
			udpClient = new UdpClient(sendPort, mBaseContext);
			mBaseContext.setGlobalObject("UDP" + sendPort, udpClient);
		}
		return udpClient;
	}

	/**
	 * new get udp instatnce method
	 * 
	 * @param sendport
	 * @param receivePort
	 * @param mBaseContext
	 * @return
	 */
	private static UdpClient getInstance(int sendport, int receivePort,
			SuperBaseContext mBaseContext) {
		UdpClient udpClient = (UdpClient) mBaseContext.getGlobalObject("UDP"
				+ sendport + ":" + receivePort);
		if (udpClient == null) {
			udpClient = new UdpClient(sendport, receivePort, mBaseContext);
			mBaseContext.setGlobalObject("UDP" + sendport + ":" + receivePort,
					udpClient);
		}
		return udpClient;
	}

	private UdpClient(final int port, SuperBaseContext baseContext) {
		this(port);
		this.mBaseContext = baseContext;
	}

	public UdpClient(final int port) {
		runing = true;
		Thread mReciverThread = new Thread(new Runnable() {
			@Override
			public void run() {
				while (runing) {
					try {
						byte[] receiveData = new byte[1024];
						while (serverSocket == null) {
							try {
								serverSocket = new DatagramSocket(null);
								serverSocket.setReuseAddress(true);
								serverSocket.bind(new InetSocketAddress(port));
							} catch (SocketException e) {
								try {
									Thread.sleep(5000);
								} catch (InterruptedException e1) {
									// TODO Auto-generated catch block
									LogManager.printStackTrace(e1);
								}
							}
						}
						DatagramPacket receivePacket = new DatagramPacket(
								receiveData, receiveData.length);
						serverSocket.receive(receivePacket);
						if (mGetData != null) {
							mGetData.ongetdata(receivePacket);
						}
						try {
							Thread.sleep(50);
						} catch (InterruptedException e1) {
							LogManager.printStackTrace(e1);
						}
					} catch (Exception e) {
						// TODO Auto-generated catch block
						LogManager.printStackTrace(e);
					}
				}
			}
		});
		mReciverThread.start();
	}

	public UdpClient(int sendPort, final int receivePort,
			SuperBaseContext baseContext) {
		this.mBaseContext = baseContext;
		new Thread(new Runnable() {
			@Override
			public void run() {
				InetAddress group = null;
				while (true) {
					try {
						byte[] receiveData = new byte[1024];
						muticastSocket = new MulticastSocket(
								SupWifiConfig.PHONE_SEND_PORT);
						group = InetAddress.getByName(mutiCastIp);// 设定多播IP
						muticastSocket.joinGroup(group);
						DatagramPacket receivePacket = new DatagramPacket(
								receiveData, receiveData.length);
						muticastSocket.receive(receivePacket);
						if (udpListener != null) {
							udpListener.onPortListenter(receivePacket);
						}
					} catch (Exception e) {
						LogManager.printStackTrace(e);
					} finally {
						if (muticastSocket != null && group != null) {
							try {
								muticastSocket.leaveGroup(group);
								muticastSocket.close();
							} catch (IOException e) {
								LogManager.printStackTrace(e);
							}
						}

					}
				}
			}
		}).start();
	}

	public void disConnect() {
		LogManager.e("disConnect");
		runing = false;
		if (serverSocket != null) {
			serverSocket.disconnect();
			serverSocket.close();
		}
		if (mBaseContext != null) {
			mBaseContext.setGlobalObject("UDP" + sendPort, null);
		}

	}

	/**
	 * @deprecated
	 * @param send
	 * @param ip
	 * @param port
	 */
	public void send(byte[] send, String ip, int port) {
		try {
			if (sender == null) {
				sender = new DatagramSocket();
			}
			sender.setBroadcast(true);
			InetAddress broadAddress = InetAddress.getByName(ip);
			DatagramPacket packet = new DatagramPacket(send, send.length,
					broadAddress, port);
			sender.send(packet);
			LogManager.d("UdpClient send:" + new String(send) + "-----ip:" + ip
					+ "----" + port);
		} catch (Exception e) {
			LogManager.printStackTrace(e);
		}
	}

	/**
	 * @param send
	 */
	public void sendBroadcast(byte[] send) {
		try {
			String ip = getBroadcastAddress(mBaseContext).getHostAddress();
			send(send, ip, SupWifiConfig.UDP_DEFAULT_PORT);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void sendSingleCastOrBroadCast(byte[] send, Set<String> ipSet) {
		boolean isSendSingleCast = mBaseContext
				.getPrefBoolean(SupKeyList.CURRENT_TCP_PORT_IS_NEW);
		if (ipSet.size() > 0) {
			sendCastToGroupForFindDevices(send, ipSet);
		}
		if (!isSendSingleCast) {
			sendBroadcast(send);
		}
	}

	/**
	 * 发送上线组播
	 */
	public void sendMulticastSocket() {
		sendMulticastSocket(SupWifiConfig.MUTICAST_MSG.getBytes(),
				SupWifiConfig.BOX_SET_MUTICAST_IP);
	}

	/**
	 * 发送组播
	 * 
	 * @param data
	 * @param ip
	 * @param port
	 */
	public void sendMulticastSocket(byte[] data, String ip) {
		new UdpRunnable(UdpRunnable.Muticast_Type, data, ip, sendPort)
				.startRunnable();
	}

	/**
	 * 发送心跳单播
	 * 
	 * @param send
	 * @param ipList
	 * @param port
	 */
	public void sendCastToGroup(byte[] send, Set<String> ipSet) {
		while (true) {
			try {
				new UdpRunnable(UdpRunnable.Singlecast_Type, send, ipSet,
						sendPort).startRunnable();
				Thread.sleep(SingleCastSendDelayTime);
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
	}

	/**
	 * 发送广播
	 * 
	 * @param data
	 * @param ip
	 * @param port
	 */
	public void sendBroadCastToPhone(String ip) {
		new UdpRunnable(UdpRunnable.BroadCast_Type,
				SupWifiConfig.MUTICAST_MSG.getBytes(), ip,
				SupWifiConfig.BOX_SEND_PORT).startRunnable();
	}

	/**
	 * 发送单播搜索家电
	 * 
	 * @param send
	 * @param ipList
	 * @param port
	 */
	public void sendCastToGroupForFindDevices(byte[] send, Set<String> ipSet) {
		LogManager.d("find device content:" + new String(send));
		new UdpRunnable(UdpRunnable.OnceSinglecast_Type, send, ipSet,
				SupWifiConfig.BOX_SEND_PORT).startRunnable();
	}

	/**
	 * 发送响应单播
	 * 
	 * @param send
	 * @param ipList
	 */
	public void sendResponseSiingleCast(byte[] send, String ip) {
		new UdpRunnable(UdpRunnable.Response_Singlecast_Type, send, ip,
				sendPort).startRunnable();
	}

	public InetAddress getBroadcastAddress(SuperBaseContext baseContext)
			throws IOException {
		WifiManager wifi = (WifiManager) baseContext.getContext()
				.getSystemService(Context.WIFI_SERVICE);
		DhcpInfo dhcp = wifi.getDhcpInfo();
		int broadcast = (dhcp.ipAddress & dhcp.netmask) | ~dhcp.netmask;
		byte[] quads = new byte[4];
		for (int k = 0; k < 4; k++)
			quads[k] = (byte) ((broadcast >> k * 8) & 0xFF);
		return InetAddress.getByAddress(quads);
	}

}
