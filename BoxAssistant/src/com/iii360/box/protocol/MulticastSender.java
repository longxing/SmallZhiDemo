package com.iii360.box.protocol;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;

import android.content.Context;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Looper;

import com.iii360.box.util.LogUtil;

public class MulticastSender {
	private static final String MULTICAST_IP = "239.255.255.252";
	private static final int sendPort = 9091;

	/**
	 * 
	 * @param msg
	 * @param timeout连续发多少秒
	 */
	public static void sendLongTime(final Context context, final String msg, final long timeout) {
		new Thread() {
			public void run() {
				String ip = "";
				try {
					ip = getBroadcastAddress(context).getHostAddress();
				} catch (Exception e) {
					// TODO: handle exception
				}
				long start = System.currentTimeMillis();
				LogUtil.e("起动发送组播start");
				while (true) {
					 sendMulticast(msg);
//					sendBroadcast(ip, msg);
					try {
						Thread.sleep(1000);
					} catch (Exception e) {
						e.printStackTrace();
					}
					long currTime = System.currentTimeMillis();
					if (currTime - start > timeout) {
						break;
					}
				}
				// handler.post(new Runnable() {
				// public void run() {
				// SinglecastClient.sendLooper(WifiConfig.PHONE_SEND_PORT,WifiConfig.SINGLECAST_MSG
				// );
				// }
				// });
			};
		};
	}

	private static Handler handler = new Handler(Looper.getMainLooper());

	private static void sendMulticast(final String msg) {
		LogUtil.e("发送组播--" + msg);
		MulticastSocket socket = null;
		byte[] buf = msg.getBytes();
		try {
			if (socket == null) {
				socket = new MulticastSocket(sendPort);
				socket.setTimeToLive(1);
				socket.joinGroup(InetAddress.getByName(MULTICAST_IP));
			}
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		try {
			DatagramPacket packet = new DatagramPacket(buf, buf.length, InetAddress.getByName(MULTICAST_IP), sendPort);
			socket.send(packet);
			socket.send(packet);
		} catch (Exception e) {
			e.printStackTrace();
			LogUtil.e(e.toString());
		} finally {
			try {
				socket.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private static void sendBroadcast(final String ip, final String msg) {
		LogUtil.e("发送广播--ip--" + msg);
		DatagramSocket socket = null;
		byte[] buf = msg.getBytes();
		try {
			if (socket == null) {
				socket = new DatagramSocket(sendPort);
				socket.setReuseAddress(true);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		try {
			DatagramPacket packet = new DatagramPacket(buf, buf.length, InetAddress.getByName(ip), sendPort);
			LogUtil.e("广播地址" + ip);
			socket.send(packet);
			socket.send(packet);
		} catch (Exception e) {
			e.printStackTrace();
			LogUtil.e(e.toString());
		} finally {
			try {
				socket.close();
			} catch (Exception e) {
			}
		}
	}

	public static InetAddress getBroadcastAddress(Context baseContext) throws IOException {
		WifiManager wifi = (WifiManager) baseContext.getSystemService(Context.WIFI_SERVICE);
		DhcpInfo dhcp = wifi.getDhcpInfo();
		int broadcast = (dhcp.ipAddress & dhcp.netmask) | ~dhcp.netmask;
		byte[] quads = new byte[4];
		for (int k = 0; k < 4; k++)
			quads[k] = (byte) ((broadcast >> k * 8) & 0xFF);
		return InetAddress.getByAddress(quads);
	}

}
