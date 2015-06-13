package com.iii360.sup.common.utl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.iii360.sup.common.utl.HanziToPinyin.Token;

/**
 * 拼音转换的工具类
 * 
 * @author showlcw
 * 
 */
public class PinYinUtil {

	/**
	 * 把汉字转为拼音
	 * 
	 * @param input
	 *            汉字
	 * @return set
	 */
	public static List<String> getPinYinSet(String input) {

		List<Token> tokens;// 列表
		List<String> pinyinSet = new ArrayList<String>();// 存放拼音的集合

		tokens = HanziToPinyin.getInstance().get(input);
		if (tokens != null && tokens.size() > 0) {
			for (Token token : tokens) {
				if (Token.PINYIN == token.type) {
					pinyinSet.add(token.target.toLowerCase());
				} else {
					pinyinSet.add(token.source.toLowerCase());
				}
			}
		}
		return pinyinSet;
	}

	/**
	 * 把汉字转为拼音
	 * 
	 * @param input
	 *            汉字
	 * @return set
	 */
	public static String getPinYin(String input) {

		List<Token> tokens;// 列表
		StringBuilder sb;// 字符

		tokens = HanziToPinyin.getInstance().get(input);
		sb = new StringBuilder();

		if (tokens != null && tokens.size() > 0) {
			for (Token token : tokens) {
				if (Token.PINYIN == token.type) {
					sb.append(token.target);
				} else {
					sb.append(token.source);
				}
			}
		}

		return sb.toString().toLowerCase();
	}
}
