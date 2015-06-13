package com.smallzhi.homeappliances.util;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;


//import com.example.leadondemo.LoginActivity;

/**
 * 数据类型转换
 * 
 * @author zsy
 * 
 */
public class NetDataTypeTransform {

	/**
	 * 将int转为低字节在前，高字节在后的byte数组
	 */
	public static byte[] IntToByteArray(int n) {
		byte[] b = new byte[4];
		b[0] = (byte) (n & 0xff);
		b[1] = (byte) (n >> 8 & 0xff);
		b[2] = (byte) (n >> 16 & 0xff);
		b[3] = (byte) (n >> 24 & 0xff);
		return b;
	}

	/**
	 * byte数组转化为int 将低字节在前转为int，高字节在后的byte数组
	 */
	public static int ByteArrayToInt(byte[] res) {

		int targets = (res[0] & 0xff) | ((res[1] << 8) & 0xff00) | ((res[2] << 24) >>> 8) | (res[3] << 24);
		return targets;
	}

	public static int bytesToInt(byte b1, byte b2) {
		int targets = (b1 & 0xff) | ((b2 << 8) & 0xff00);
		return targets;
	}

	/**
	 * 将byte数组转化成String
	 */
	public static String ByteArraytoString(byte[] valArr) {
		String result = null;
		int index = 0;
		while (index < valArr.length) {
			if (valArr[index] == 0) {
				break;
			}
			index++;
		}
		byte[] temp = new byte[index];
		System.arraycopy(valArr, 0, temp, 0, index);
		try {
			result = new String(temp, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 将String转化成byte数组
	 * 
	 * @param str
	 * @return
	 */
	public static byte[] StringToByteArray(String str) {
		byte[] temp = null;
		try {
			if (str == null) {
				temp = new byte[] { 0 };
			} else {
				temp = str.getBytes("UTF-8");
			}
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return temp;
	}

	/**
	 * byte转换为char
	 * 
	 * @param b
	 * @return
	 */
	public static char byteToChar(byte[] b) {
		char c = (char) (((b[0] & 0xFF) << 8) | (b[1] & 0xFF));
		return c;
	}

	/**
	 * char 转换为byte
	 * 
	 * @param c
	 * @return
	 */
	public static byte[] charToByte(char c) {
		byte[] b = new byte[2];
		b[0] = (byte) (c & 0xFF);
		b[1] = (byte) ((c & 0xFF00) >> 8);
		return b;
	}

	/**
	 * @方法功能 InputStream 转为 byte
	 * @param inStream
	 * @return 读入的字节数组
	 * @throws Exception
	 */
	public static byte[] inputStreamToByte(InputStream inStream) throws Exception {
		int count = 0;
		while (count == 0) {
			if (null != inStream) {
				count = inStream.available();
			}
		}
		byte[] b = null;
		if (count > 2048) {
			b = new byte[2048];
		} else {
			if (count != -1) {
				b = new byte[count];
			}
		}
		if (null != b) {
			inStream.read(b);
		}
		// LoginActivity.logger("receive data len = "+b.length);
		return b;
	}

	public static byte[] intStringToByte(String s) {

		byte[] resulte = new byte[(s.length() + 1) / 2];

		for (int i = 0, j = 0; i < s.length() - 1; i += 2, j++) {
			String info = s.substring(i, i + 2);
			resulte[j] = (byte) Integer.parseInt(info, 16);
//			LogManager.e((byte) Integer.parseInt(info, 16) + "");
		}

		return resulte;
	}

	public static String BytesToIntString(byte[] values) {
		StringBuilder s = new StringBuilder();
		for (byte b : values) {
			int i = 0xff & b;
			String temps =Integer.toHexString(i);
			if(temps.length() ==1){
				temps= '0'+temps;
			}
			s.append(temps);
		}
		return s.toString();
	}

	public static void main(String[] args) {
		byte[] b = { 62, -7, -97, -29, 1, -111, 0, 0, 0, 0, 0, 0, -116, 0, 0, 0, 1, 112, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
				4, 48, 120, 102, 97, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
				0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 5, 48,
				120, 49, 50, 51, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
				0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };

	}
}
