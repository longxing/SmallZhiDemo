package com.smallzhi.homeappliances.util;

import java.io.IOException;
import java.net.InetAddress;

import android.content.Context;

import com.iii360.base.common.utl.LogManager;
import com.smallzhi.homeappliances.control.IControlInterface;
import com.smallzhi.homeappliances.control.WoWoClient;

public class CoreUtil {

	private String mIp = "";
	private int mPort = 6000;
	private static CoreUtil coreUtil;
	private OnGetDevAdd mOnGetDevAdd;
	private Context context;

	private CoreUtil() {

	}

	public synchronized static CoreUtil getInstance(Context c) {
		if (coreUtil == null) {
			coreUtil = new CoreUtil();
		}
		if (c != null) {
			coreUtil.context = c;
		}
		return coreUtil;
	}

	public interface OnGetDevAdd {
		/**
		 * 当连接到主机的时候，
		 * 
		 * @param control
		 */
		public void onGetAdd(IControlInterface control);

		/**
		 * 当和家庭主机断开连接的时候
		 * 
		 * 
		 */
		public void onDisConnect();
	}

	public void setIP(String ip) {
		mIp = ip;
	}

	public void setPort(String port) {
		if (port != null && !port.equals("") && !port.equals("null"))
			mPort = Integer.valueOf(port);
	}

	public void setOnGetDevAdd(OnGetDevAdd onGetDevAdd) {
		mOnGetDevAdd = onGetDevAdd;
	}

	/**
	 * 
	 * @return
	 */
	public void findDevice() {

		new Thread(new Runnable() {

			@Override
			public void run() {
				findWOWODevice(mOnGetDevAdd);
			}
		}).start();
	}

	public void findWOWODevice(final OnGetDevAdd onGetDevAdd) {
		LogManager.e("ip&port: " + mIp + ":" + mPort);
		if (mIp != null && mIp.length() > 6) {
			Client client = connectLeadon(mIp, mPort);
			if (client != null) {
				WoWoClient wowoClient = new WoWoClient(client);
				onGetDevAdd.onGetAdd(wowoClient);
			} else {
				onGetDevAdd.onGetAdd(null);
			}
		} else {
			onGetDevAdd.onGetAdd(null);
		}
	}

	private Client connectLeadon(String ip, int port) {
		Client client = null;
		try {
			LogManager.e(ip);
			InetAddress address = InetAddress.getByName(ip);
			client = new Client();
			client.setIp$Port(address.getHostAddress(), port);
		} catch (Exception e) {
			LogManager.printStackTrace(e);
			return null;
		}

		if (client.start()) {
			// TODO leadon 登陆
			byte[] b = { 62, -7, -97, -29, 1, -111, 0, 0, 0, 0, 0, 0, -116, 0, 0, 0, 1, 112, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 4, 48, 120, 102, 97, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
					0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 5, 48, 120, 49, 50, 51, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
					0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
			try {
				client.getOutputStream().write(b);
				return client;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		return null;
	}

}
