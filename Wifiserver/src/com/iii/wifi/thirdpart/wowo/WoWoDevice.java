package com.iii.wifi.thirdpart.wowo;

import java.util.ArrayList;
import java.util.List;

import com.iii.wifi.udptcp.IReceiverListener;
import com.iii.wifi.udptcp.JSTcpClient;
import com.iii.wifi.udptcp.JSUdpClient;
import com.iii360.sup.common.utl.LogManager;

/**
 * 窝窝智能设备
 * 
 * @author liang
 * 
 */
public class WoWoDevice {

	/**
	 * 设备信息列表
	 */
	private ArrayList<WoWoInfo> infos = null;
	private String mRemoteIp = "255.255.255.255";
	private int mRemotePort = 7682;
	private int mTcpPort = 7681;
	private JSUdpClient udpclient = null;
	private JSTcpClient tcpclient = null;
	private boolean flag;
	private static String IP;
	private String Mac;
	private boolean Off;
	private static WoWoDevice device = null;
	/**
	 * 存储设备回复的mac号码
	 */
	private List<String> msgIp = new ArrayList<String>();
	private WoWoInfo info = null;
	private String result;

	private WoWoDevice() {
		// TODO Auto-generated constructor stub
		infos = new ArrayList<WoWoInfo>();
		udpclient = new JSUdpClient(mRemoteIp, mRemotePort);
		udpclient.connect();
		udpclient.receive();
		search();
		flag = true;

	}

	// 静态工厂方法
	public synchronized static WoWoDevice getInstance() {
		if (device == null) {
			device = new WoWoDevice();
		}
		return device;
	}

	public void clear() {
		msgIp.clear();
		infos.clear();
	}

	public List<WoWoInfo> getAllWoWoDevcie() {
		// TODO Auto-generated method stub

		return infos;
	}

	public void close() {
		flag = false;
		udpclient.close();
	}

	/**
	 * 开始所搜设备
	 */
	public void search() {
		new Thread() {
			public void run() {
				while (flag) {
					udpclient.send("ip");
					try {
						Thread.sleep(2000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

			};

		}.start();

		/**
		 * 回调方法接收信息
		 */
		udpclient.setReceiverListener(new IReceiverListener() {

			@Override
			public void onReceived(byte[] data, String message) {
				// TODO Auto-generated method stub
				/**
				 * 执行存储设备信息
				 */
				if (!message.equals("ip")) {
					operate(message);
				}

			}
		});
	}

	/**
	 * 打开或关闭开关
	 */
	public String controlOnOff(String mac, boolean isOff) {
		// TODO Auto-generated method stub

		Mac = mac;
		this.Off = isOff;
		new Thread() {
			public void run() {
				tcpclient = new JSTcpClient(IP, mTcpPort);
				tcpclient.connect();
				for (int i = 0; i < 2; i++) {
					if (Off) {
						tcpclient.send(Mac + "01");
					} else {
						tcpclient.send(Mac + "00");
					}
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			};
		}.start();
		return result;
	}

	/**
	 * @param message
	 */
	public void operate(String message) {
		// TODO Auto-generated method stub

		String[] res = message.split("\\+");
		if (res.length == 6) {
			if (res[5].equals("ww")) {
				boolean flag = true;
				for (int i = 0; i < msgIp.size(); i++) {
					if (msgIp.get(i).equals(res[2])) {
						flag = false;
					}
				}
				if (flag) {
					msgIp.add(res[2]);
					int num = Integer.parseInt(res[4]);
					for (int i = 0; i < num; i++) {
						info = new WoWoInfo();
						info.setIp(res[1]);
						int num1 = i + 1;
						info.setMac(res[3] + res[2] + "0" + num1);
						info.setBrand("开关" + num1);
						infos.add(info);
						IP = res[1];
					}
				}
			} else {
				LogManager.e("接受设备回应数据格式不准确");
			}
		}

	}
}
