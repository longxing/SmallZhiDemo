package com.iii360.sup.common.utl;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 字符串预处理模块，为分析器TimeNormalizer提供相应的字符串预处理服务
 * 
 * @author 曹零07300720158
 * 
 */
public class stringPreHandlingModule {

	/**
	 * 该方法删除一字符串中所有匹配某一规则字串 可用于清理一个字符串中的空白符和语气助词
	 * 
	 * @param target
	 *            待处理字符串
	 * @param rules
	 *            删除规则
	 * @return 清理工作完成后的字符串
	 */
	public static String delKeyword(String target, String rules) {
		Pattern p = Pattern.compile(rules);
		Matcher m = p.matcher(target);
		StringBuffer sb = new StringBuffer();
		boolean result = m.find();
		while (result) {
			m.appendReplacement(sb, "");
			result = m.find();
		}
		m.appendTail(sb);
		String s = sb.toString();
		// System.out.println("字符串："+target+" 的处理后字符串为：" +sb);
		return s;
	}

	/**
	 * 该方法可以将字符串中所有的用汉字表示的数字转化为用阿拉伯数字表示的数字 如"这里有一千两百个人，六百零五个来自中国"可以转化为
	 * "这里有1200个人，605个来自中国" 此外添加支持了部分不规则表达方法 如两万零六百五可转化为20650
	 * 两百一十四和两百十四都可以转化为214 一六零加一五八可以转化为160+158 该方法目前支持的正确转化范围是0-99999999
	 * 该功能模块具有良好的复用性
	 * 
	 * @param target
	 *            待转化的字符串
	 * @return 转化完毕后的字符串
	 */
	public static String numberTranslator(String target) {
		Pattern p = Pattern.compile("[一二两三四五六七八九123456789]万[一二两三四五六七八九123456789](?!(千|百|十))");
		Matcher m = p.matcher(target);
		StringBuffer sb = new StringBuffer();
		boolean result = m.find();
		while (result) {
			String group = m.group();
			String[] s = group.split("万");
			int num = 0;
			if (s.length == 2) {
				num += wordToNumber(s[0]) * 10000 + wordToNumber(s[1]) * 1000;
			}
			m.appendReplacement(sb, Integer.toString(num));
			result = m.find();
		}
		m.appendTail(sb);
		target = sb.toString();

		p = Pattern.compile("[一二两三四五六七八九123456789]千[一二两三四五六七八九123456789](?!(百|十))");
		m = p.matcher(target);
		sb = new StringBuffer();
		result = m.find();
		while (result) {
			String group = m.group();
			String[] s = group.split("千");
			int num = 0;
			if (s.length == 2) {
				num += wordToNumber(s[0]) * 1000 + wordToNumber(s[1]) * 100;
			}
			m.appendReplacement(sb, Integer.toString(num));
			result = m.find();
		}
		m.appendTail(sb);
		target = sb.toString();

		p = Pattern.compile("[一二两三四五六七八九123456789]百[一二两三四五六七八九123456789](?!十)");
		m = p.matcher(target);
		sb = new StringBuffer();
		result = m.find();
		while (result) {
			String group = m.group();
			String[] s = group.split("百");
			int num = 0;
			if (s.length == 2) {
				num += wordToNumber(s[0]) * 100 + wordToNumber(s[1]) * 10;
			}
			m.appendReplacement(sb, Integer.toString(num));
			result = m.find();
		}
		m.appendTail(sb);
		target = sb.toString();

		p = Pattern.compile("[零一二两三四五六七八九]");
		m = p.matcher(target);
		sb = new StringBuffer();
		result = m.find();
		while (result) {
			m.appendReplacement(sb, Integer.toString(wordToNumber(m.group())));
			result = m.find();
		}
		m.appendTail(sb);
		target = sb.toString();

		p = Pattern.compile("(?<=(周|星期))[末天日]");
		m = p.matcher(target);
		sb = new StringBuffer();
		result = m.find();
		while (result) {
			m.appendReplacement(sb, Integer.toString(wordToNumber(m.group())));
			result = m.find();
		}
		m.appendTail(sb);
		target = sb.toString();

		p = Pattern.compile("(?<!(周|星期))0?[0-9]?十[0-9]?");
		m = p.matcher(target);
		sb = new StringBuffer();
		result = m.find();
		while (result) {
			String group = m.group();
			String[] s = group.split("十");
			int num = 0;
			if (s.length == 0) {
				num += 10;
			} else if (s.length == 1) {
				int ten = Integer.parseInt(s[0]);
				if (ten == 0)
					num += 10;
				else
					num += ten * 10;
			} else if (s.length == 2) {
				if (s[0].equals(""))
					num += 10;
				else {
					int ten = Integer.parseInt(s[0]);
					if (ten == 0)
						num += 10;
					else
						num += ten * 10;
				}
				num += Integer.parseInt(s[1]);
			}
			m.appendReplacement(sb, Integer.toString(num));
			result = m.find();
		}
		m.appendTail(sb);
		target = sb.toString();

		p = Pattern.compile("0?[1-9]百[0-9]?[0-9]?");
		m = p.matcher(target);
		sb = new StringBuffer();
		result = m.find();
		while (result) {
			String group = m.group();
			String[] s = group.split("百");
			int num = 0;
			if (s.length == 1) {
				int hundred = Integer.parseInt(s[0]);
				num += hundred * 100;
			} else if (s.length == 2) {
				int hundred = Integer.parseInt(s[0]);
				num += hundred * 100;
				num += Integer.parseInt(s[1]);
			}
			m.appendReplacement(sb, Integer.toString(num));
			result = m.find();
		}
		m.appendTail(sb);
		target = sb.toString();

		p = Pattern.compile("0?[1-9]千[0-9]?[0-9]?[0-9]?");
		m = p.matcher(target);
		sb = new StringBuffer();
		result = m.find();
		while (result) {
			String group = m.group();
			String[] s = group.split("千");
			int num = 0;
			if (s.length == 1) {
				int thousand = Integer.parseInt(s[0]);
				num += thousand * 1000;
			} else if (s.length == 2) {
				int thousand = Integer.parseInt(s[0]);
				num += thousand * 1000;
				num += Integer.parseInt(s[1]);
			}
			m.appendReplacement(sb, Integer.toString(num));
			result = m.find();
		}
		m.appendTail(sb);
		target = sb.toString();

		p = Pattern.compile("[0-9]+万[0-9]?[0-9]?[0-9]?[0-9]?");
		m = p.matcher(target);
		sb = new StringBuffer();
		result = m.find();
		while (result) {
			String group = m.group();
			String[] s = group.split("万");
			int num = 0;
			if (s.length == 1) {
				int tenthousand = Integer.parseInt(s[0]);
				num += tenthousand * 10000;
			} else if (s.length == 2) {
				int tenthousand = Integer.parseInt(s[0]);
				num += tenthousand * 10000;
				num += Integer.parseInt(s[1]);
			}
			m.appendReplacement(sb, Integer.toString(num));
			result = m.find();
		}
		m.appendTail(sb);
		target = sb.toString();

		return target;
	}

	/**
	 * 方法numberTranslator的辅助方法，可将[零-九]正确翻译为[0-9]
	 * 
	 * @param s
	 *            大写数字
	 * @return 对应的整形数，如果不是大写数字返回-1
	 */
	private static int wordToNumber(String s) {
		if (s.equals("零") || s.equals("0"))
			return 0;
		else if (s.equals("一") || s.equals("1"))
			return 1;
		else if (s.equals("二") || s.equals("两") || s.equals("2"))
			return 2;
		else if (s.equals("三") || s.equals("3"))
			return 3;
		else if (s.equals("四") || s.equals("4"))
			return 4;
		else if (s.equals("五") || s.equals("5"))
			return 5;
		else if (s.equals("六") || s.equals("6"))
			return 6;
		else if (s.equals("七") || s.equals("天") || s.equals("日") || s.equals("7") || s.equals("末"))
			return 7;
		else if (s.equals("八") || s.equals("8"))
			return 8;
		else if (s.equals("九") || s.equals("9"))
			return 9;
		else
			return -1;
	}

	/**
	 * 可以把阿拉伯数字 转化为 汉字的数字，可以到亿这个级别
	 * 
	 * @param i
	 * @return
	 */
	public static String intsToString(int i) {
		int j = i;
		int tag = 0;
		String[] Number = { "零", "一", "二", "三", "四", "五", "六", "七", "八", "九", };
		String[] Grade = { "", "十", "百", "千", "万", "十万", "百万", "千万", "亿" };
		String result = "";
		boolean isNagetive = false;
		if (i < 0) {
			isNagetive = true;
		}
		j = Math.abs(j);
		result = Number[j % 10] + Grade[tag] + result;
		// System.out.println(Number[j % 10] + Grade[tag]);

		while (j > 9) {
			tag++;
			j = j / 10;
			int value = j % 10;
			result = Number[value] + Grade[tag] + result;
			// System.out.println(Number[value] + Grade[tag]);
		}

		if (result.startsWith("一十")) {
			result = result.replace("一十", "十");
		}
		// remove more 0
		if (result.contains(Number[0]) && result.length() > 1) {
			String results[] = result.split(Number[0]);
			result = "";
			for (String s : results) {

				if (s.length() > 1) {

					for (String startTag : Grade) {
						if (s.startsWith(startTag) && startTag.length() > 0) {
							s = s.replace(startTag, Number[0]);
						}
					}
					result += s;
				}
			}
		}
		// remove more wan
		if (result.contains(Grade[4])) {
			String results[] = result.split(Grade[4]);
			result = "";
			for (int k = 0; k < results.length; k++) {
				if (k != results.length - 1) {
					result += results[k];
				} else {
					result += Grade[4] + results[k];
				}
			}
		}
		if (isNagetive) {
			result = "负" + result;
		}
		return result;
	}

	/**
	 * 获取字符串中所有的数字
	 * 
	 * @param params
	 * @return
	 */
	public static int[] getInt(String[] params) {
		int[] result = new int[params.length];
		String patter = "-?[0-9]+";
		Pattern pattern = Pattern.compile(patter);
		for (int i = 0; i < params.length; i++) {

			Matcher matcher = pattern.matcher(numberTranslator(params[i]));
			if (matcher.find()) {
				String s1 = matcher.group();
				result[i] = Integer.valueOf(s1);
			} else {
				result[i] = -1;
			}
		}

		return result;

	}

	/**
	 * 获取字符串中所有的数字
	 * 
	 * @param params
	 * @return
	 */
	public static int getInt(String params) {
		String patter = "-?[0-9]+";
		Pattern pattern = Pattern.compile(patter);

		Matcher matcher = pattern.matcher(numberTranslator(params));
		if (matcher.find()) {
			String s1 = matcher.group();
			return Integer.valueOf(s1);
		}
		return -1;

	}

	/**
	 * 过滤开头和结尾无意义词
	 * 
	 * @param from
	 * @return
	 */
	public static String meanExtract(String from) {
		String chineseMatch = "(^[嗯啊呢吧]|[嗯啊呢吧]$)";
		String charMatch = "(^[,，。;；‘'~！]|[,，。;；‘'~！]$)";
		Pattern p = Pattern.compile(chineseMatch);
		Pattern p2 = Pattern.compile(charMatch);
		Matcher m = p.matcher(from);
		Matcher m2 = p2.matcher(from);
		int i = 0;
		while ((m.find() || m2.find()) && i < 10) {
			i++;
			m.reset();
			m2.reset();
			if (m.find()) {
				String s = m.group();
				from = from.replace(s, "");
			}

			if (m2.find()) {
				String s = m2.group();
				from = from.replace(s, "");
			}
			m = p.matcher(from);
			m2 = p2.matcher(from);
		}
		from = from.replaceAll("[,，。;；‘'~！ ]", "");
		return from;
	}

}
