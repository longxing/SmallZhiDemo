package com.iii360.box.protocol;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Map;
import java.util.Set;

import com.iii360.box.MyApplication;
import com.iii360.box.util.LogUtil;

/***
 * 单播
 * 
 * @author terry
 *
 */
public class SinglecastClient {
	public static void send(final String ip, final int port, final String msg) {
		LogUtil.e(ip + "," + port + "," + msg);
//		ToastUtils.show(MyApplication.instance, ""+ip + "," + port + "," + msg);
		new Thread() {
			public void run() {
				DatagramSocket socket = null;
				try {
					socket = new DatagramSocket();
					byte[] data = msg.getBytes("utf-8");
					DatagramPacket packet = new DatagramPacket(data,
							data.length, InetAddress.getByName(ip), port);
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
			};
		}.start();
	}
	
	public static void sendLooper(final int port, final String msg) {
		Map<String, Long>map = MyApplication.getBoxAdds();
		final Set<String> set = map.keySet();
		
//		ToastUtils.show(MyApplication.instance, ""+ip + "," + port + "," + msg);
		new Thread() {
			public void run() {
				DatagramSocket socket = null;
				try {
					socket = new DatagramSocket();
					byte[] data = msg.getBytes("utf-8");
					for (String ip : set) {
						LogUtil.e("单播"+ip + "," + port + "," + msg);
						DatagramPacket packet = new DatagramPacket(data,
								data.length, InetAddress.getByName(ip), port);
						socket.send(packet);
						socket.send(packet);
					}
				} catch (Exception e) {
					e.printStackTrace();
					LogUtil.e(e.toString());
				} finally {
					try {
						socket.close();
					} catch (Exception e) {
					}
				}
			};
		}.start();
	}
}
