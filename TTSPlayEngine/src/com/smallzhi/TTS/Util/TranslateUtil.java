package com.smallzhi.TTS.Util;

public class TranslateUtil {
	static String ask = "1234567890.:";
	static String answer = "一二三四五六七八九零点点";
	/**
	 * 朗读原文
	 * @return
	 */
	public static String original(String text) {
		if (text == null) {
			return "";
		}
		StringBuffer stringBuffer = new StringBuffer();
		for (int i=0;i<text.length();i++) {
			char c = text.charAt(i);
			int offset = ask.indexOf(c);
			if (offset >= 0) {
				c = answer.charAt(offset);
			}
			stringBuffer.append(c);
		}
		return stringBuffer.toString();
	}
}
