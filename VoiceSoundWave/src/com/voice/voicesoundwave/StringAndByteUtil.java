package com.voice.voicesoundwave;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;

public class StringAndByteUtil {
	private Map<String, String> mKeyToValue;
	private Map<String, String> mValueToKey;

	public StringAndByteUtil(Context context) {
		mKeyToValue = new HashMap<String,String>();
		mValueToKey = new HashMap<String,String>();
		mapForFile(context);
	}

	public boolean isContainChinese(String str) {
		Pattern p = Pattern.compile("[\u4e00-\u9fa5]");
		Matcher m = p.matcher(str);
		if (m.find()) {
			return false;
		}
		return false;
	}

	public String bytesToString(byte[] result) {
		if ((byte) (result[result.length - 1] & 0x01) == 1) {
			byte[] dest = new byte[result.length - 1];
			System.arraycopy(result, 0, dest, 0, result.length - 1);
			try {
				return new String(dest, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			String res = "";
			for (int i = 0; i < result.length; i++) {
				res += byteToString(result[i]);
			}
			String s = "";
			for (int i = 0; i + 7 <= res.length() - 8 - ((result[result.length - 1] >> 5) & 0x07); i += 7) {
				s += mValueToKey.get(res.subSequence(i, i + 7));
			}
			return s;
		}
		return null;
	}

	public byte[] ascToBytes(String str) {
		TrieTree trie = new TrieTree(1);
		for (String key : mKeyToValue.keySet()) {
			trie.addWord(key);
		}
		List<String> list = trie.getMaxMatchWord(str);
		int length = 0;
		String reslut = "";
		for (String res : list) {
			reslut += mKeyToValue.get(res);
			length += res.length();
		}
		if (length != str.length()) {
			return otherToBytes(str);
		}
		byte[] newByte = new byte[reslut.length() / 8 + 1];
		for (int i = 0; i + 8 <= reslut.length(); i += 8) {
			for (int j = 0; j < 8; j++) {
				if (reslut.charAt(i + j) == '1') {
					newByte[i / 8] <<= 1;
					newByte[i / 8] |= 0x01;
				} else {
					newByte[i / 8] <<= 1;
					newByte[i / 8] &= 0xfe;
				}
			}
		}
		if (reslut.length() % 8 == 0) {
			newByte[reslut.length() / 8] = 0x00;
		} else {
			int bt = reslut.length() % 8;
			for (int i = 0; i < bt; i++) {
				if (reslut.charAt((reslut.length() / 8) * 8 + i) == '1') {
					newByte[reslut.length() / 8] <<= 1;
					newByte[reslut.length() / 8] |= 0x01;
				} else {
					newByte[reslut.length() / 8] <<= 1;
					newByte[reslut.length() / 8] &= 0xfe;
				}
			}
			newByte[reslut.length() / 8] <<= 8 - bt;
			byte[] newResult = new byte[newByte.length + 1];
			for (int i = 0 ; i < newByte.length;i++) {
				newResult[i] = newByte[i];
			}
			newResult[newByte.length] = (byte) ((8 - bt) << 5);
			return newResult;
		}
		return newByte;
	}

	public String byteToString(byte result) {
		String res = "";
		for (int i = 7; i >= 0; i--) {
			res += (result >> i) & 0x01;
		}
		return res;
	}

	public byte[] otherToBytes(String str) {
		try {
			byte[] t = str.getBytes("UTF-8");
			byte[] res = new byte[t.length + 1];
			for (int i = 0; i < t.length; i++) {
				res[i] = t[i];
			}
			res[t.length] = (byte) 0x01;
			return res;
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public void mapForFile(Context context) {
		try {
//			FileReader reader = new FileReader(
//					"/Users/jiangshenglan/Downloads/codes1");
			InputStreamReader reader = new InputStreamReader(context.getAssets().open("codes"));
			BufferedReader br = new BufferedReader(reader);
			String line = null;
			while ((line = br.readLine()) != null) {
				String[] lines = line.split(" ");
				if (lines.length > 2) {
					mKeyToValue.put(" ", lines[2]);
					mValueToKey.put(lines[2], " ");
				} else {
					mKeyToValue.put(lines[0], lines[1]);
					mValueToKey.put(lines[1], lines[0]);
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
