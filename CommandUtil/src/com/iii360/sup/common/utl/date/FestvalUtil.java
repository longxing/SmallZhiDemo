package com.iii360.sup.common.utl.date;

import java.util.Calendar;
import java.util.HashMap;

import com.iii360.sup.common.utl.LogManager;

public class FestvalUtil {
	final static String[] sFtv = new String[] { "0101 元旦", "0214 情人节", "0308 妇女节", "0312 植树节",
			// "0314 国际警察日",
			"0315 消费者权益日", "0323 世界气象日", "0401 愚人节", "0407 世界卫生日", "0501 劳动节", "0504 青年节",
			// "0508 红十字日", "0512 护士节",
			// "0515 国际家庭日", "0517 世界电信日", "0519 全国助残日", "0531 世界无烟日",
			"0601 儿童节",
			// "0605 世界环境日", "0606 全国爱眼日",
			// "0623 奥林匹克日",
			// "0625 全国土地日",
			// "0626 反毒品日", "0701 建党节", "0707 抗战纪念日", "0711 世界人口日",
			"0801 建军节",
			// "0908 国际扫盲日",
			// "0909 毛泽东逝世纪念",
			"0910 教师节",
			// "0917 国际和平日", "0920 国际爱牙日", "0922 国际聋人节", "0927 世界旅游日",
			// "0928 孔子诞辰",
			// "1001 国庆节", "1004 世界动物日", "1006 老人节", "1007 国际住房日", "1009 世界邮政日",
			// "1015 国际盲人节", "1016 世界粮食日", "1024 联合国日",

			"1031 万圣节",
			// "1108 中国记者日", "1109 消防宣传日", "1112 孙中山诞辰", "1114 世界糖尿病日",
			// "1117 国际大学生节", "1201 世界艾滋病日",
			// "1203 世界残疾人日", "1209 世界足球日",
			// "1220 澳门回归",
			"1225 圣诞节",
	// "1226 毛泽东诞辰"
	};

	final static String[] lFtv = { "0101 春节", "0115 元宵", "0505 端午", "0707 七夕", "0815 中秋", "0909 重阳", "1208 腊八",
			"1223 小年", "0100 除夕" };

	final static String[] wFtv = { "母亲节", "父亲节", "感恩节" };// 每年6月第3个星期日是父亲节,5月的第2个星期日是母亲节
	final static int[] otherDays = { 520, 630, 1144 };

	static HashMap<String, String> GregorianFestval = new HashMap<String, String>();
	static HashMap<String, String> ChineseFestval = new HashMap<String, String>();
	static {
		GregorianFestval.put("0101", "元旦");
		GregorianFestval.put("0214", "情人节");
		GregorianFestval.put("0308", "妇女节");
		GregorianFestval.put("0312", "植树节");
		GregorianFestval.put("0315", "消费者权益日");
		GregorianFestval.put("0401", "愚人节");
		GregorianFestval.put("0501", "劳动节");
		GregorianFestval.put("0504", "青年节");
		GregorianFestval.put("0601", "儿童节");
		GregorianFestval.put("0625", "全国土地日");
		GregorianFestval.put("0801", "建军节");
		GregorianFestval.put("0910", "教师节");
		GregorianFestval.put("1031", "万圣节");
		GregorianFestval.put("1225", "圣诞节");

		ChineseFestval.put("0101", "春节");
		ChineseFestval.put("0115", "元宵");
		ChineseFestval.put("0505", "端午");
		ChineseFestval.put("0707", "七夕");
		ChineseFestval.put("0815", "中秋");
		ChineseFestval.put("0909", "重阳");
		ChineseFestval.put("1208", "腊八");
		ChineseFestval.put("1223", "小年");
		ChineseFestval.put("0100", "除夕");

		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.MONTH, 6);
		calendar.set(Calendar.WEEK_OF_MONTH, 3);
		calendar.set(Calendar.DAY_OF_WEEK, 0);

		GregorianFestval.put("06" + calendar.get(Calendar.DAY_OF_MONTH), "父亲节");

		calendar.set(Calendar.MONTH, 5);
		calendar.set(Calendar.WEEK_OF_MONTH, 2);
		calendar.set(Calendar.DAY_OF_WEEK, 0);
		GregorianFestval.put("05" + calendar.get(Calendar.DAY_OF_MONTH), "父亲节");

		calendar.set(Calendar.MONTH, 11);
		calendar.set(Calendar.WEEK_OF_MONTH, 4);
		calendar.set(Calendar.DAY_OF_WEEK, 4);
		GregorianFestval.put("05" + calendar.get(Calendar.DAY_OF_MONTH), "感恩节");

	}

	public static String getCurrentFestval() {
		ChineseCalendarGB calendarGB = new ChineseCalendarGB();
		int y = 0, m = 0, d = 0;
		y = Calendar.getInstance().get(Calendar.YEAR);
		m = Calendar.getInstance().get(Calendar.MONTH) + 1;
		d = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
		LogManager.e(y + "  " + m + " " + d);
		calendarGB.setGregorian(y, m, d);
		calendarGB.computeChineseFields();
		calendarGB.computeSolarTerms();

		String chineseDate = String.format("%2s", calendarGB.chineseMonth).replace(" ", "0")
				+ String.format("%2s", calendarGB.chineseDate).replace(" ", "0");
		if (ChineseFestval.containsKey(chineseDate)) {
			return ChineseFestval.get(chineseDate);
		}
		String GregorianData = String.format("%2s", m).replace(" ", "0") + String.format("%2s", d).replace(" ", "0");
		if (GregorianFestval.containsKey(GregorianData)) {
			return GregorianFestval.get(GregorianData);
		}

		return null;
	}
}
