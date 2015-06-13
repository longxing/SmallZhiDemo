package com.iii360.sup.common.utl;

import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.util.InetAddressUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.protocol.HTTP;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

/**
 * 
 * 网络连接管理。
 * 
 */
public class NetWorkUtil {
	private static final String RUNTIMEEXCEPTION_MSG = "Context is null.";

	/**
	 * 
	 * @param context
	 *            Context
	 * @return true:有连接 false:无网络连接
	 */

	public static boolean isNetworkConnected(Context context) {
		if (context != null) {
			final ConnectivityManager mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
			final NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
			if (mNetworkInfo != null) {
				return mNetworkInfo.isAvailable();
			}
		} else {
			throw new RuntimeException(RUNTIMEEXCEPTION_MSG);
		}
		return false;
	}

	/**
	 * 
	 * @param context
	 *            Context
	 * @return true:已连接到wifi false:不连接wifi。
	 */
	public static boolean isWifiConnected(Context context) {
		if (context != null) {
			final ConnectivityManager mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
			final NetworkInfo mWiFiNetworkInfo = mConnectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
			if (mWiFiNetworkInfo != null) {
				return mWiFiNetworkInfo.isAvailable();
			}
		} else {
			throw new RuntimeException(RUNTIMEEXCEPTION_MSG);
		}
		return false;
	}

	/**
	 * 
	 * @param context
	 *            Context
	 * @return true:有数据GPRS连接。 false :无数据GPRS连接
	 */
	public static boolean isMobileConnected(Context context) {
		if (context != null) {
			final ConnectivityManager mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
			final NetworkInfo mMobileNetworkInfo = mConnectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
			if (mMobileNetworkInfo != null) {
				return mMobileNetworkInfo.isAvailable();
			}
		} else {
			throw new RuntimeException(RUNTIMEEXCEPTION_MSG);
		}
		return false;
	}

	/**
	 * 
	 * @param context
	 *            Context
	 * @return <pre>
	 *  ConnectivityManager.TYPE_MOBILE  数据联系
	 *  ConnectivityManager.TYPE_WIFI    wifi连接
	 *  -1 No Connected
	 * </pre>
	 */
	public static int getConnectedType(Context context) {
		if (context != null) {
			final ConnectivityManager mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
			final NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
			if (mNetworkInfo != null && mNetworkInfo.isAvailable()) {
				return mNetworkInfo.getType();
			}
		} else {
			throw new RuntimeException(RUNTIMEEXCEPTION_MSG);
		}
		return -1;
	}

	/**
	 * 获得本地ip地址
	 * 
	 * @return
	 */
	public static String getLocalIpAddress() {
		try {
			String tempIp = null;
			for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
				NetworkInterface intf = en.nextElement();

				for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
					InetAddress inetAddress = enumIpAddr.nextElement();
					if (!inetAddress.isLoopbackAddress()) {
						String HostAdd = inetAddress.getHostAddress().toString();
						if (InetAddressUtils.isIPv4Address(HostAdd) && !HostAdd.equals("::1")) {
							if (HostAdd.startsWith("192")) {
								return HostAdd;
							} else {
								tempIp = HostAdd;
							}
						}
					}
				}
			}
			return tempIp;
		} catch (SocketException ex) {
			ex.printStackTrace();
			Log.e("WifiPreference IpAddress", ex.toString());
		}

		return null;
	}

	public static InputStreamReader getNetworkInputStreamReader(String srcUrl) {
		LogManager.d("getNetworkInputStreamReader URL:" + srcUrl.toString());
		InputStreamReader input = null;
		try {
			int index = srcUrl.indexOf("?");
			String urlHeader = srcUrl.substring(0, index);
			String srcUrls = srcUrl.substring(index + 1, srcUrl.length());
			HttpPost httpPost = new HttpPost(urlHeader);
			String[] param = srcUrls.split("&");
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			for (int i = 0; i < param.length; i++) {
				String[] p = param[i].split("=");
				if (p.length > 1) {
					params.add(new BasicNameValuePair(p[0], URLDecoder.decode(p[1])));
				} else {
					params.add(new BasicNameValuePair(p[0], null));
				}
			}
			HttpResponse httpResponse = null;
			httpPost.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
			BasicHttpParams httpParams = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(httpParams, 10 * 1000);
			HttpConnectionParams.setSoTimeout(httpParams, 5 * 1000);
			httpResponse = new DefaultHttpClient(httpParams).execute(httpPost);
			if (httpResponse.getStatusLine().getStatusCode() == 200) {
				input = new InputStreamReader(httpResponse.getEntity().getContent(), "UTF-8");
			}
		} catch (Exception e) {
			LogManager.e("NetWorkUtil", "getNetworkInputStreamReader----" + e.toString());
			return null;
		}
		return input;
	}

	public static String getLocalMacAddress(Context context) {
		return SystemUtil.getLocalMacAddress(context);
	}
}
