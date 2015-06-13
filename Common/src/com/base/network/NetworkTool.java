
package com.base.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
// ID20130827001 chenyuming begin
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;

// ID20130827001 chenyuming end
import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;

import com.iii360.base.common.utl.BaseContext;
import com.iii360.base.common.utl.KeyList;
import com.iii360.base.common.utl.LogManager;


public class NetworkTool {
    public static final String NET_UNAVAILABLE = "-1";
    public static final String NET_TYPE_WIFI = "1";
    public static final String TYPE_MOBILE = "2";
    private static String mMacAddress = "";

    
    public static String getNetworkAsString(String url) {
    	InputStreamReader inputStreamReader = null;
    	BufferedReader bufferedReader = null;
    	StringBuilder sBuilder = new StringBuilder();
    	String line = null;
    	try {
    		inputStreamReader = getNetworkInputStreamReader(url);
    		if (inputStreamReader == null) {
    			throw new RuntimeException("连接失败");
    		}
    		bufferedReader = new BufferedReader(inputStreamReader);
			while((line=bufferedReader.readLine()) != null ) {
				sBuilder.append(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (bufferedReader != null) {
				try {
					bufferedReader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (inputStreamReader != null) {
				try {
					inputStreamReader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
        return sBuilder.toString();
    }
    
    public static JSONObject getNetworkAsJson(String url) {
    	String jsonString = getNetworkAsString(url);
    	try {
			return new JSONObject(jsonString);
		} catch (JSONException e) {
			LogManager.e("[NetTools|getNetworkAsJson]Error, not json format, URL=" + url);
			return null;
		}
    }
    
    public static InputStreamReader getNetworkInputStreamReader(String srcUrl) {

        LogManager.d("URL:" + srcUrl.toString());
        URLConnection connection;
        InputStreamReader input = null;
        URL url;
        try {

            // ID20130827001 chenyuming begin
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
            httpResponse = new DefaultHttpClient().execute(httpPost);// here will throw RunTimeException ,then add Catch late.
            if (httpResponse.getStatusLine().getStatusCode() == 200) {
                input = new InputStreamReader(httpResponse.getEntity().getContent(), "UTF-8");
            }
            // ID20130827001 chenyuming end

        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            LogManager.printStackTrace(e);
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            LogManager.printStackTrace(e);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            LogManager.printStackTrace(e);
        } catch (RuntimeException e) {
            LogManager.printStackTrace(e);
        }

        return input;
    }

    /**
     * check net
     * 
     * @param context
     * @return
     */
    public static boolean checkNetworkAvalible(Context context) {
        if (context == null) {
            LogManager.e("[NetTools|checkNetworkAvalible]Error, context is null!");
            return false;
        }
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);

        if (wifiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLED) { // wifi
            return true;
        } else {
            ConnectivityManager cManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo info = cManager.getActiveNetworkInfo();

            if (info != null && info.isAvailable()) {
                return true;
            } else {
                return false;
            }
        }
    }

    /**
     * check wifi
     * 
     * @param context
     * @return
     */
    public static boolean checkWifiAvilible(Context context) {
        if (context == null) {
            LogManager.e("[NetTools|checkNetworkAvalible]Error, context is null!");
            return false;
        }
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);

        if (wifiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLED) { // wifi
            return true;
        }

        return false;
    }

    // ID20120420005 zhanglin end


    public static String getLocalMacAddress(Context context) {
        if (mMacAddress == null || mMacAddress.equals("")) {
            WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            WifiInfo info = null;

            if (wifi != null) {
                info = wifi.getConnectionInfo();
            }

            if (info != null) {
                mMacAddress = info.getMacAddress();
            }
        }

        LogManager.i("NetworkTool", "getLocalMacAddress", "mac:" + mMacAddress);
        return mMacAddress;
    }

    public static String getLocalIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        return inetAddress.getHostAddress().toString();
                    }
                }
            }
        } catch (SocketException ex) {
        }
        return null;
    }

    public static String getNetType(Context context) {
        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeInfo = connMgr.getActiveNetworkInfo();
        if (activeInfo == null || activeInfo.isAvailable()) {
            return NET_UNAVAILABLE;
        }
        if (ConnectivityManager.TYPE_WIFI == activeInfo.getType()) {
            return NET_TYPE_WIFI;
        } else {
            return TYPE_MOBILE;
        }

    }

    public static String getNetName(Context context, String netType) {
        if (NET_TYPE_WIFI.equals(netType)) {
            return getWifiNetName(context);
        } else if (TYPE_MOBILE.equals(netType)) {
            return getMobileNetName(context);
        } else {
            LogManager.e("[NetTools|getNetName]Error, netType invalid");
            return null;
        }
    }

    private static String getWifiNetName(Context context) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        return wifiManager.getConnectionInfo().getSSID();
    }

    public static String getImei(Context context) {
        BaseContext rc = new BaseContext(context);
        String imei = rc.getPrefString(KeyList.PKEY_IMEI);
        if (imei == null || imei.equals("")) {
            imei = ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();

            rc.setPrefString(KeyList.PKEY_IMEI, imei);
        }

        return imei;
    }

    private static String getMobileNetName(Context context) {
        String strNetName = null;

        Uri PREFERAPN_URI = android.net.Uri.parse("content://telephony/carriers/preferapn");
        Cursor cursor = ((Activity) context).managedQuery(PREFERAPN_URI, null, null, null, null);
        if (cursor != null && cursor.getCount() != 0) {
            cursor.moveToFirst();
            int index = cursor.getColumnIndex("user");
            strNetName = cursor.getString(index);
        }
        if (cursor != null) {
            cursor.close();
        }

        // The phone which support double-SIMCard need to check it again
        Uri PREFERAPN_URI_2 = android.net.Uri.parse("content://telephony/carriers/preferapn2");
        cursor = ((Activity) context).managedQuery(PREFERAPN_URI_2, null, null, null, null);
        if (cursor != null && cursor.getCount() != 0) {
            cursor.moveToFirst();
            int index = cursor.getColumnIndex("user");
            strNetName = cursor.getString(index);
        }
        if (cursor != null) {
            cursor.close();
        }

        return strNetName;
    }

    public static boolean is3GAndWifiNetworkAvailable(Context context) {

        // Context context = mService.getApplicationContext();
        ConnectivityManager con = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        TelephonyManager conMan = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

        if (conMan == null) {
            return false;
        } else {

            TelephonyManager mWifiManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            int info = mWifiManager.getNetworkType();

            // State mobile =
            // conMan.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState();

            // wifi
            State wifi = con.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();

            /*
             * //如果3G网络和wifi网络都未连接，且不是处于正在连接状态 则进入Network Setting界面 由用户配置网络连接
             * if(mobile==State.CONNECTED) { return true; }
             */
            if (wifi == State.CONNECTED) {

                return true;
            }

            if (info == TelephonyManager.NETWORK_TYPE_EVDO_0 || info == TelephonyManager.NETWORK_TYPE_EVDO_A || info == TelephonyManager.NETWORK_TYPE_UMTS
                    || info == TelephonyManager.NETWORK_TYPE_HSDPA || info == TelephonyManager.NETWORK_TYPE_HSPA || info == TelephonyManager.NETWORK_TYPE_HSUPA) {
                return true;
            }

        }

        return false;
    }

    public static boolean is3GNetworkAvailable(Context context) {

        /*
         * ConnectivityManager conMan = (ConnectivityManager)
         * context.getSystemService(Context.CONNECTIVITY_SERVICE);
         * 
         * //Context context = mService.getApplicationContext(); if (conMan ==
         * null) { return false; } else {
         * 
         * State mobile =
         * conMan.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState();
         * 
         * 
         * //如果3G网络和wifi网络都未连接，且不是处于正在连接状态 则进入Network Setting界面 由用户配置网络连接
         * if(mobile==State.CONNECTED) { return true; }
         * 
         * 
         * 
         * }
         */

        TelephonyManager conMan = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

        if (conMan == null) {
            return false;
        } else {

            TelephonyManager mWifiManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            int info = mWifiManager.getNetworkType();

            if (info == TelephonyManager.NETWORK_TYPE_EVDO_0 || info == TelephonyManager.NETWORK_TYPE_EVDO_A || info == TelephonyManager.NETWORK_TYPE_UMTS
                    || info == TelephonyManager.NETWORK_TYPE_HSDPA || info == TelephonyManager.NETWORK_TYPE_HSPA || info == TelephonyManager.NETWORK_TYPE_HSUPA) {
                return true;
            }

        }

        return false;
    }

    public static boolean isWifiNetworkAvailable(Context context) {
        ConnectivityManager conMan = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (conMan == null) {
            return false;
        } else {

            State wifi = conMan.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();

            if (wifi == State.CONNECTED) {

                return true;

            }
        }

        return false;
    }

    public static boolean wiffIsStrong(Context context) {

        WifiManager mWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo mWifiInfo = mWifiManager.getConnectionInfo();
        int wifiLength = mWifiInfo.getRssi();// 获取wifi信号强度//-67 ,-59更强些

        LogManager.i("wifiLength", wifiLength + "");
        if (wifiLength > -80) {
            return true;
        }

        return false;
    }

    public static boolean isPhoneNetworkAvailable(Context context) {
        boolean flag = false;
        ConnectivityManager cwjManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cwjManager.getActiveNetworkInfo() != null) {
            flag = cwjManager.getActiveNetworkInfo().isAvailable();

        }

        return flag;
    }

    public static boolean is2GNetworkAvailable(Context context) {

        TelephonyManager conMan = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (conMan == null) {
            return false;
        } else {
            boolean flag = isPhoneNetworkAvailable(context);
            ConnectivityManager cwjManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (cwjManager.getActiveNetworkInfo() != null) {
                flag = cwjManager.getActiveNetworkInfo().isAvailable();
            }

            TelephonyManager mManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            State wifi = cwjManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
            boolean wifiFlag = false;

            if (wifi == State.CONNECTED) {

                wifiFlag = true;

            }

            int info = mManager.getNetworkType();

            if ((info == TelephonyManager.NETWORK_TYPE_GPRS || info == TelephonyManager.NETWORK_TYPE_EDGE || info == TelephonyManager.NETWORK_TYPE_CDMA || info == TelephonyManager.NETWORK_TYPE_1xRTT)
                    && flag && !wifiFlag) {
                return true;
            }

        }

        return false;
    }

}
