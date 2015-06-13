package com.iii.wifi.dao.manager;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.URLDecoder;

import android.content.Context;

import com.iii.wifi.dao.info.WifiJSONObjectInfo;
import com.iii360.sup.common.utl.LogManager;

/**
 * 
 * @author jsl 客户端回调 返回WifiJSONObjectInfo对象
 */
public class WifiCRUDForClient {

	public static WifiJSONObjectInfo findData(Socket socket) throws IOException {
		return findData(socket, null);
	}

	/**
	 * 请使用findData(Socket socket)方法
	 * 
	 * @param socket
	 * @param context
	 * @return
	 * @throws IOException
	 */
	@Deprecated
	public static WifiJSONObjectInfo findData(Socket socket, Context context) throws IOException {
		InputStream in;
		StringBuffer buffer = new StringBuffer();
		in = socket.getInputStream();
		// int result = in.available();
		// while (result == 0) {
		// result = in.available();
		// }
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte[] data = new byte[1024];
		int count = 0;
		while ((count = in.read(data)) != -1) {
			baos.write(data, 0, count);
			// buffer.append(new String(data, 0, count,""));
		}
		baos.flush();
		LogManager.d("" + new String(baos.toByteArray(),"utf-8"));
		WifiJSONObjectInfo info = WifiCreateAndParseSockObjectManager.ParseWifiUserInfos(URLDecoder.decode(new String(baos.toByteArray()), "utf-8"));
		in.close();
		socket.close();
		return info;
	}

}
