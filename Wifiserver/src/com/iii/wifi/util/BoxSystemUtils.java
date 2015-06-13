package com.iii.wifi.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.Properties;

import org.apache.http.conn.util.InetAddressUtils;

import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.os.StatFs;
import android.text.format.Formatter;
import android.util.Log;

import com.iii360.sup.common.utl.SuperBaseContext;

/**
 * @author Administrator
 *
 */
public class BoxSystemUtils {

    /**
     * 获得SD卡总大小
     * 
     * @return
     */
    public static String getSDTotalSize(Context context) {
        File path = Environment.getExternalStorageDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long totalBlocks = stat.getBlockCount();
        return Formatter.formatFileSize(context, blockSize * totalBlocks);
    }

    /**
     * 获得sd卡剩余容量，即可用大小
     * 
     * @return
     */
    public static String getSDAvailableSize(Context context) {
        File path = Environment.getExternalStorageDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getAvailableBlocks();
        return Formatter.formatFileSize(context, blockSize * availableBlocks);
    }

    /**
     * 获得机身运行内存总大小
     * 
     * @return
     */
    public static String getRamTotalSize(Context context) {
        return getTotalMemory(context);
    }

    /**
     * 获得机身运行可用内存
     * 
     * @return
     */
    public static String getRamAvailableSize(Context context) {
        return getAvailMemory(context);
    }

    /**
     * 获取可用运行内存
     * @return
     */
	private static String getAvailMemory(Context context) {// 获取android当前可用内存大小

		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		MemoryInfo mi = new MemoryInfo();
		am.getMemoryInfo(mi);// mi.availMem; 当前系统的可用内存
		return Formatter.formatFileSize(context, mi.availMem);// 将获取的内存大小规格化

	}
  /**
   * 获取全部运行内存
   * @return
   */
	private static String getTotalMemory(Context context) {

		String str1 = "/proc/meminfo";// 系统内存信息文件
		String str2;
		String[] arrayOfString;
		long initial_memory = 0;
		try {

			FileReader localFileReader = new FileReader(str1);

			BufferedReader localBufferedReader = new BufferedReader(
					localFileReader, 8192);
			str2 = localBufferedReader.readLine();// 读取meminfo第一行，系统总内存大小
			arrayOfString = str2.split("\\s+");
			for (String num : arrayOfString) {
				Log.i(str2, num + "\t");
			}
			initial_memory = Integer.valueOf(arrayOfString[1]).intValue() * 1024;// 获得系统总内存，单位是KB，乘以1024转换为Byte

			localBufferedReader.close();
		} catch (IOException e) {
		}

		return Formatter.formatFileSize(context, initial_memory);// Byte转换为KB或者MB，内存大小规格化
	}
    
    
    /**
     * 获得mac地址
     * 
     * @param context
     * @return
     */
    public static String getLocalMacAddress(Context context) {
        WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wifi.getConnectionInfo();
        return info.getMacAddress();
    }

    /**
     * 获取IP地址
     * 
     * @return
     */
    public static String getLocalIpAddress() {

        try {
            Enumeration<NetworkInterface> networks;
            Enumeration<InetAddress> inets;
            NetworkInterface network;
            InetAddress inetAddress;

            for (networks = NetworkInterface.getNetworkInterfaces(); networks.hasMoreElements();) {
                network = networks.nextElement();

                for (inets = network.getInetAddresses(); inets.hasMoreElements();) {
                    inetAddress = inets.nextElement();

                    if (!inetAddress.isLoopbackAddress() && InetAddressUtils.isIPv4Address(inetAddress.getHostAddress())) {

                        return inetAddress.getHostAddress().toString();

                    }
                }

            }
        } catch (SocketException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 获取IP地址
     * 
     * @param context
     * @return
     */
    public static String getWifiIp(Context context) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        int ipAddress = wifiInfo.getIpAddress();
        return Formatter.formatIpAddress(ipAddress);
    }

    /**
     * 系统固件版本号
     * 
     * @return
     */
    public static String getFirmwareVersion() {

        String anser = "1.0.0.0";
        // 
        File file = new File(Environment.getExternalStorageDirectory().getPath() + "/upgrade.properties");
        FileInputStream in = null;
        if (file.exists()) {
            try {
                in = new FileInputStream(file);
                Properties properties = new Properties();
                properties.load(in);
                anser = properties.getProperty("version");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
        return anser;
    }

    /**
     * 音箱序列号
     * @return
     */
    public static String getSerialNumber() {
        try {
            return SerialNumberUitls.readSerialNumber();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return "000000000000";
    }

    public static int SIZE_UNIT = 1000;

    public static String formatSize(long size) {
        String suffix = null;
        float fSize = 0;

        if (size >= SIZE_UNIT) {
            suffix = "KB";
            fSize = size / SIZE_UNIT;
            if (fSize >= SIZE_UNIT) {
                suffix = "MB";
                fSize /= SIZE_UNIT;
            }
            if (fSize >= SIZE_UNIT) {
                suffix = "GB";
                fSize /= SIZE_UNIT;
            }
        } else {
            fSize = size;
        }
        java.text.DecimalFormat df = new java.text.DecimalFormat("#0.00");
        StringBuilder resultBuffer = new StringBuilder(df.format(fSize));
        if (suffix != null)
            resultBuffer.append(suffix);
        return resultBuffer.toString();
    }
    public static String getBattery(Context context){
    	SuperBaseContext baseContext = new SuperBaseContext(context);
    	String battery = baseContext.getPrefString(KeyList.PKEY_BATTERY_LEVEL);
    	if(battery==null)return "100";
    	return battery;
    }
}
