package com.voice.common.util.time;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.R.integer;
import android.graphics.AvoidXfermode;
import android.provider.ContactsContract.Contacts.Data;

import com.iii360.base.common.utl.LogManager;
import com.iii360.sup.common.utl.ITimeUnit;
import com.iii360.sup.common.utl.TimerTicker;

/**
 * 
 * @author xpqiu
 * 
 */
public class TimeUnit implements Serializable, ITimeUnit {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/*
	 * 新版本中将根据上下文相关信息动态获得timeBase,故取消参数Time_Initial 及相关构造方法，添加参数normalizer。
	 * 
	 * modified by 曹零
	 */
	public String Time_Expression = null;
	private Date time;
	public boolean repeatFlag = false;
	private int repeatDistance = 1;
	private int repeatType = 0;
	private int timePointfield = 0;

	private boolean avalibeFlag = false;
	private int avalibeFrom = 0;
	private int avalibeTo = 0;
	private Calendar cale;
	private Long BaseTime;

	/*
	 * public String Time_Initial=null; modified by 曹零
	 */
	private TimeNormalizer normalizer = null;
	private TimePoint _tp = new TimePoint();
	private TimePoint _tp_origin = new TimePoint();

	final String[] hanzi = { "年", "月", "日", "点", "分", "秒" };

	/**
	 * 时间表达式单元规范化的内部类
	 * 
	 * 时间表达式单元规范化对应的内部类, 对应时间表达式规范化的每个字段， 六个字段分别是：年-月-日-时-分-秒， 每个字段初始化为-1
	 * 
	 * @author 邬桐 072021156
	 * @version 1.0 2009-02-12
	 * 
	 */
	public class TimePoint implements Serializable {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		int[] tunit = { -1, -1, -1, -1, -1, -1 };
	}

	/**
	 * 时间表达式单元构造方法 该方法作为时间表达式单元的入口，将时间表达式字符串传入
	 * 
	 * @param exp_time
	 *            时间表达式字符串
	 * @param n
	 */
	public TimeUnit(String exp_time, TimeNormalizer n) {
		Time_Expression = exp_time;
		LogManager.e(exp_time);
		normalizer = n;
		/*
		 * modified by 曹零 SimpleDateFormat tempDate = new
		 * SimpleDateFormat("yyyy-MM-dd-hh-mm-ss"); Time_Initial =
		 * tempDate.format(new java.util.Date());
		 */
		Time_Normalization();

	}

	/**
	 * return the accurate time object
	 */
	public Date getTime() {
		return time;
	}

	/**
	 * 年-规范化方法
	 * 
	 * 该方法识别时间表达式单元的年字段
	 * 
	 */
	public void norm_setyear() {
		String rule = "[0-9]{2}(?=年)";
		Pattern pattern = Pattern.compile(rule);
		Matcher match = pattern.matcher(Time_Expression);
		if (match.find()) {
			_tp.tunit[0] = Integer.parseInt(match.group());
			if (_tp.tunit[0] >= 0 && _tp.tunit[0] < 100) {
				if (_tp.tunit[0] < 30)
					_tp.tunit[0] += 2000;
				else
					_tp.tunit[0] += 1900;
				_tp.tunit[1] = 0;
			}
		}
		/*
		 * 不仅局限于支持1XXX年和2XXX年的识别，可识别三位数和四位数表示的年份 modified by 曹零
		 */
		rule = "[0-9]?[0-9]{3}(?=年)";
		pattern = Pattern.compile(rule);
		match = pattern.matcher(Time_Expression);
		if (match.find()) {
			_tp.tunit[0] = Integer.parseInt(match.group());
		}
	}

	/**
	 * 月-规范化方法
	 * 
	 * 该方法识别时间表达式单元的月字段
	 * 
	 */
	public void norm_setmonth() {
		String rule = "((10)|(11)|(12)|([1-9]))(?=月)";
		Pattern pattern = Pattern.compile(rule);
		Matcher match = pattern.matcher(Time_Expression);
		if (match.find()) {
			_tp.tunit[1] = Integer.parseInt(match.group());
			_tp.tunit[2] = 0;
		}
	}

	/**
	 * 日-规范化方法
	 * 
	 * 该方法识别时间表达式单元的日字段
	 * 
	 */
	public void norm_setday() {
		String rule = "((?<!\\d))([0-3][0-9]|[1-9])(?=(日|号))";
		Pattern pattern = Pattern.compile(rule);
		Matcher match = pattern.matcher(Time_Expression);
		if (match.find()) {
			_tp.tunit[2] = Integer.parseInt(match.group());
		}
	}

	/**
	 * 时-规范化方法
	 * 
	 * 该方法识别时间表达式单元的时字段
	 * 
	 */
	public void norm_sethour() {
		/*
		 * 清除只能识别11-99时的bug modified by 曹零
		 */
		String rule = "(?<!(周|星期))([0-2]?[0-9])(?=(点|时))";

		Pattern pattern = Pattern.compile(rule);
		Matcher match = pattern.matcher(Time_Expression);
		if (match.find()) {
			_tp.tunit[3] = Integer.parseInt(match.group());
			_tp.tunit[4] = 0;
		}

		/*
		 * 对关键字：中午,午间,下午,午后,晚上,傍晚,晚间,晚,pm,PM的正确时间计算 规约： 1.中午/午间0-10点视为12-22点
		 * 2.下午/午后0-11点视为12-23点 3.晚上/傍晚/晚间/晚1-11点视为13-23点，12点视为0点
		 * 4.0-11点pm/PM视为12-23点
		 * 
		 * add by 曹零
		 */
		rule = "(中午)|(午间)";
		pattern = Pattern.compile(rule);
		match = pattern.matcher(Time_Expression);
		if (match.find()) {
			if (_tp.tunit[3] >= 0 && _tp.tunit[3] <= 10)
				_tp.tunit[3] += 12;
		}

		rule = "(下午)|(午后)|(pm)|(PM)";
		pattern = Pattern.compile(rule);
		match = pattern.matcher(Time_Expression);
		if (match.find()) {
			if (_tp.tunit[3] >= 0 && _tp.tunit[3] <= 11)
				_tp.tunit[3] += 12;
		}

		rule = "晚";
		pattern = Pattern.compile(rule);
		match = pattern.matcher(Time_Expression);
		if (match.find()) {
			if (_tp.tunit[3] >= 1 && _tp.tunit[3] <= 11)
				_tp.tunit[3] += 12;
			else if (_tp.tunit[3] == 12)
				_tp.tunit[3] = 0;
		}
	}

	/**
	 * 分-规范化方法
	 * 
	 * 该方法识别时间表达式单元的分字段
	 * 
	 */
	public void norm_setminute() {
		/*
		 * 添加了省略“分”说法的时间 如17点15 modified by 曹零
		 */
		String rule = "([0-5]?[0-9](?=分(?!钟)))|((?<=((?<!小)[点时]))[0-5]?[0-9](?!刻))";

		Pattern pattern = Pattern.compile(rule);
		Matcher match = pattern.matcher(Time_Expression);
		if (match.find()) {
			if (match.group().equals("")) {

			} else
				_tp.tunit[4] = Integer.parseInt(match.group());
		}
		/*
		 * 添加对一刻，半，3刻的正确识别（1刻为15分，半为30分，3刻为45分）
		 * 
		 * add by 曹零
		 */
		rule = "(?<=[点时])[1一]刻(?!钟)";
		pattern = Pattern.compile(rule);
		match = pattern.matcher(Time_Expression);
		if (match.find()) {
			_tp.tunit[4] = 15;
		}

		rule = "(?<=[点时])半";
		pattern = Pattern.compile(rule);
		match = pattern.matcher(Time_Expression);
		if (match.find()) {
			_tp.tunit[4] = 30;
		}

		rule = "(?<=[点时])[3三]刻(?!钟)";
		pattern = Pattern.compile(rule);
		match = pattern.matcher(Time_Expression);
		if (match.find()) {
			_tp.tunit[4] = 45;
		}

	}

	/**
	 * 秒-规范化方法
	 * 
	 * 该方法识别时间表达式单元的秒字段
	 * 
	 */
	public void norm_setsecond() {
		/*
		 * 添加了省略“分”说法的时间 如17点15分32 modified by 曹零
		 */
		String rule = "([0-5]?[0-9](?=秒))|((?<=分)[0-5]?[0-9])";

		Pattern pattern = Pattern.compile(rule);
		Matcher match = pattern.matcher(Time_Expression);
		if (match.find()) {
			_tp.tunit[5] = Integer.parseInt(match.group());
		}
	}

	/**
	 * 特殊形式的规范化方法
	 * 
	 * 该方法识别特殊形式的时间表达式单元的各个字段
	 * 
	 */
	public void norm_setTotal() {
		String rule;
		Pattern pattern;
		Matcher match;
		String[] tmp_parser;
		String tmp_target;

		/*
		 * 修改了函数中所有的匹配规则使之更为严格 modified by 曹零
		 */
		rule = "(?<!(周|星期))([0-2]?[0-9]):[0-5]?[0-9]:[0-5]?[0-9]";
		pattern = Pattern.compile(rule);
		match = pattern.matcher(Time_Expression);
		if (match.find()) {
			tmp_parser = new String[3];
			tmp_target = match.group();
			tmp_parser = tmp_target.split(":");
			_tp.tunit[3] = Integer.parseInt(tmp_parser[0]);
			_tp.tunit[4] = Integer.parseInt(tmp_parser[1]);
			_tp.tunit[5] = Integer.parseInt(tmp_parser[2]);
		}
		/*
		 * 添加了省略秒的:固定形式的时间规则匹配 add by 曹零
		 */
		else {
			rule = "(?<!(周|星期))([0-2]?[0-9]):[0-5]?[0-9]";
			pattern = Pattern.compile(rule);
			match = pattern.matcher(Time_Expression);
			if (match.find()) {
				tmp_parser = new String[2];
				tmp_target = match.group();
				tmp_parser = tmp_target.split(":");
				_tp.tunit[3] = Integer.parseInt(tmp_parser[0]);
				_tp.tunit[4] = Integer.parseInt(tmp_parser[1]);
			}
		}
		/*
		 * 增加了:固定形式时间表达式的 中午,午间,下午,午后,晚上,傍晚,晚间,晚,pm,PM 的正确时间计算，规约同上 add by 曹零
		 */
		rule = "(中午)|(午间)";
		pattern = Pattern.compile(rule);
		match = pattern.matcher(Time_Expression);
		if (match.find()) {
			if (_tp.tunit[3] >= 0 && _tp.tunit[3] <= 10)
				_tp.tunit[3] += 12;
			else if (_tp.tunit[3] == -1)
				_tp.tunit[3] = 12;
		}

		rule = "(下午)|(午后)|(pm)|(PM)";
		pattern = Pattern.compile(rule);
		match = pattern.matcher(Time_Expression);
		if (match.find()) {
			if (_tp.tunit[3] >= 0 && _tp.tunit[3] <= 11)
				_tp.tunit[3] += 12;
			else if (_tp.tunit[3] == -1)
				_tp.tunit[3] = 15;
		}

		rule = "晚";
		pattern = Pattern.compile(rule);
		match = pattern.matcher(Time_Expression);
		if (match.find()) {
			if (_tp.tunit[3] >= 1 && _tp.tunit[3] <= 11)
				_tp.tunit[3] += 12;
			else if (_tp.tunit[3] == 12)
				_tp.tunit[3] = 0;
			else if (_tp.tunit[3] == -1)
				_tp.tunit[3] = 20;
		}
		rule = "早";
		pattern = Pattern.compile(rule);
		match = pattern.matcher(Time_Expression);
		if (match.find()) {
			if (_tp.tunit[3] == -1)
				_tp.tunit[3] = 7;
		}

		rule = "[0-9]?[0-9]?[0-9]{2}-((10)|(11)|(12)|([1-9]))-((?<!\\d))([0-3][0-9]|[1-9])";
		pattern = Pattern.compile(rule);
		match = pattern.matcher(Time_Expression);
		if (match.find()) {
			tmp_parser = new String[3];
			tmp_target = match.group();
			tmp_parser = tmp_target.split("-");
			_tp.tunit[0] = Integer.parseInt(tmp_parser[0]);
			_tp.tunit[1] = Integer.parseInt(tmp_parser[1]);
			_tp.tunit[2] = Integer.parseInt(tmp_parser[2]);
		}

		rule = "((10)|(11)|(12)|([1-9]))/((?<!\\d))([0-3][0-9]|[1-9])/[0-9]?[0-9]?[0-9]{2}";
		pattern = Pattern.compile(rule);
		match = pattern.matcher(Time_Expression);
		if (match.find()) {
			tmp_parser = new String[3];
			tmp_target = match.group();
			tmp_parser = tmp_target.split("/");
			_tp.tunit[1] = Integer.parseInt(tmp_parser[0]);
			_tp.tunit[2] = Integer.parseInt(tmp_parser[1]);
			_tp.tunit[0] = Integer.parseInt(tmp_parser[2]);
		}

		/*
		 * 增加了:固定形式时间表达式 年.月.日 的正确识别 add by 曹零
		 */
		rule = "[0-9]?[0-9]?[0-9]{2}\\.((10)|(11)|(12)|([1-9]))\\.((?<!\\d))([0-3][0-9]|[1-9])";
		pattern = Pattern.compile(rule);
		match = pattern.matcher(Time_Expression);
		if (match.find()) {
			tmp_parser = new String[3];
			tmp_target = match.group();
			tmp_parser = tmp_target.split("\\.");
			_tp.tunit[0] = Integer.parseInt(tmp_parser[0]);
			_tp.tunit[1] = Integer.parseInt(tmp_parser[1]);
			_tp.tunit[2] = Integer.parseInt(tmp_parser[2]);
		}
	}

	/**
	 * 设置以上文时间为基准的时间偏移计算
	 * 
	 * add by 曹零
	 */
	public void norm_setBaseRelated() {
		String[] time_grid = new String[6];
		time_grid = normalizer.getTimeBase().split("-");
		int[] ini = new int[6];
		for (int i = 0; i < 6; i++)
			ini[i] = Integer.parseInt(time_grid[i]);

		Calendar calendar = Calendar.getInstance();
		calendar.setFirstDayOfWeek(Calendar.MONDAY);
		calendar.set(ini[0], ini[1] - 1, ini[2], ini[3], ini[4], ini[5]);
		calendar.getTime();

		boolean[] flag = { false, false, false, false, false, false };// 观察时间表达式是否因当前相关时间表达式而改变时间

		String rule = "\\d+(?=天[以之]?前)";
		Pattern pattern = Pattern.compile(rule);
		Matcher match = pattern.matcher(Time_Expression);
		if (match.find()) {
			flag[2] = true;
			int day = Integer.parseInt(match.group());
			calendar.add(Calendar.DATE, -day);
		}

		rule = "\\d+(?=天[以之]?后)";
		pattern = Pattern.compile(rule);
		match = pattern.matcher(Time_Expression);
		if (match.find()) {
			flag[2] = true;
			int day = Integer.parseInt(match.group());
			calendar.add(Calendar.DATE, day);
		}

		rule = "\\d+(?=(个)?月[以之]?前)";
		pattern = Pattern.compile(rule);
		match = pattern.matcher(Time_Expression);
		if (match.find()) {
			flag[1] = true;
			int month = Integer.parseInt(match.group());
			calendar.add(Calendar.MONTH, -month);
		}

		rule = "\\d+(?=(个)?月[以之]?后)";
		pattern = Pattern.compile(rule);
		match = pattern.matcher(Time_Expression);
		if (match.find()) {
			flag[1] = true;
			int month = Integer.parseInt(match.group());
			calendar.add(Calendar.MONTH, month);
		}

		rule = "\\d+(?=年[以之]?前)";
		pattern = Pattern.compile(rule);
		match = pattern.matcher(Time_Expression);
		if (match.find()) {
			flag[0] = true;
			int year = Integer.parseInt(match.group());
			calendar.add(Calendar.YEAR, -year);
		}

		rule = "\\d+(?=年[以之]?后)";
		pattern = Pattern.compile(rule);
		match = pattern.matcher(Time_Expression);
		if (match.find()) {
			flag[0] = true;
			int year = Integer.parseInt(match.group());
			calendar.add(Calendar.YEAR, year);
		}

		/*
		 * 对两个小时-分钟-秒-前，后 这样的语句做出识别
		 */
		rule = "\\d+(?=个?小时[以之]?后)";
		pattern = Pattern.compile(rule);
		match = pattern.matcher(Time_Expression);
		if (match.find()) {
			flag[3] = true;
			int hour = Integer.parseInt(match.group());
			calendar.add(Calendar.HOUR, hour);
		}

		rule = "\\d+(?=个?小时[以之]?前)";
		pattern = Pattern.compile(rule);
		match = pattern.matcher(Time_Expression);
		if (match.find()) {
			flag[3] = true;
			int hour = Integer.parseInt(match.group());
			calendar.add(Calendar.HOUR, -hour);
		}

		rule = "\\d+(?=分钟[以之]?前)";
		pattern = Pattern.compile(rule);
		match = pattern.matcher(Time_Expression);
		if (match.find()) {
			flag[4] = true;
			int min = Integer.parseInt(match.group());
			calendar.add(Calendar.MINUTE, -min);
		}
		rule = "\\d+(?=分钟[以之]?后)";
		pattern = Pattern.compile(rule);
		match = pattern.matcher(Time_Expression);
		if (match.find()) {
			flag[4] = true;
			int min = Integer.parseInt(match.group());
			calendar.add(Calendar.MINUTE, min);

		}

		rule = "\\d+(?=秒钟[以之]?前)";
		pattern = Pattern.compile(rule);
		match = pattern.matcher(Time_Expression);
		if (match.find()) {
			flag[5] = true;
			int min = Integer.parseInt(match.group());
			calendar.add(Calendar.SECOND, -min);
		}
		rule = "\\d+(?=秒钟[以之]?后)";
		pattern = Pattern.compile(rule);
		match = pattern.matcher(Time_Expression);
		if (match.find()) {
			flag[5] = true;
			int min = Integer.parseInt(match.group());
			calendar.add(Calendar.SECOND, min);

		}

		String s = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(calendar.getTime());
		String[] time_fin = s.split("-");
		if (flag[0] || flag[1]) {
			_tp.tunit[0] = Integer.parseInt(time_fin[0]);
			_tp.tunit[1] = Integer.parseInt(time_fin[1]);
		}
		if (flag[1] || flag[2]) {

			_tp.tunit[1] = Integer.parseInt(time_fin[1]);
			_tp.tunit[2] = Integer.parseInt(time_fin[2]);
		}

		if (flag[2] || flag[3]) {
			_tp.tunit[2] = Integer.parseInt(time_fin[2]);
			_tp.tunit[3] = Integer.parseInt(time_fin[3]);
		}

		if (flag[3] || flag[4]) {
			_tp.tunit[3] = Integer.parseInt(time_fin[3]);
			_tp.tunit[4] = Integer.parseInt(time_fin[4]);
		}

		if (flag[4] || flag[5]) {
			_tp.tunit[4] = Integer.parseInt(time_fin[4]);
			_tp.tunit[5] = Integer.parseInt(time_fin[5]);
		}

		if (flag[5]) {
			_tp.tunit[5] = Integer.parseInt(time_fin[5]);
		}

	}

	/**
	 * 设置当前时间相关的时间表达式
	 * 
	 * add by 曹零
	 */
	public void norm_setCurRelated() {
		String[] time_grid = new String[6];
		time_grid = normalizer.getOldTimeBase().split("-");
		int[] ini = new int[6];
		for (int i = 0; i < 6; i++)
			ini[i] = Integer.parseInt(time_grid[i]);

		Calendar calendar = Calendar.getInstance();
		calendar.setFirstDayOfWeek(Calendar.MONDAY);
		calendar.set(ini[0], ini[1] - 1, ini[2], ini[3], ini[4], ini[5]);
		calendar.getTime();

		boolean[] flag = { false, false, false };// 观察时间表达式是否因当前相关时间表达式而改变时间

		String rule = "前年";
		Pattern pattern = Pattern.compile(rule);
		Matcher match = pattern.matcher(Time_Expression);
		if (match.find()) {
			flag[0] = true;
			calendar.add(Calendar.YEAR, -2);
		}

		rule = "去年";
		pattern = Pattern.compile(rule);
		match = pattern.matcher(Time_Expression);
		if (match.find()) {
			flag[0] = true;
			calendar.add(Calendar.YEAR, -1);
		}

		rule = "今年";
		pattern = Pattern.compile(rule);
		match = pattern.matcher(Time_Expression);
		if (match.find()) {
			flag[0] = true;
			calendar.add(Calendar.YEAR, 0);
		}

		rule = "明年";
		pattern = Pattern.compile(rule);
		match = pattern.matcher(Time_Expression);
		if (match.find()) {
			flag[0] = true;
			calendar.add(Calendar.YEAR, 1);
		}

		rule = "后年";
		pattern = Pattern.compile(rule);
		match = pattern.matcher(Time_Expression);
		if (match.find()) {
			flag[0] = true;
			calendar.add(Calendar.YEAR, 2);
		}

		rule = "上(个)?月";
		pattern = Pattern.compile(rule);
		match = pattern.matcher(Time_Expression);
		if (match.find()) {
			flag[1] = true;
			calendar.add(Calendar.MONTH, -1);

		}

		rule = "(本|这个)月";
		pattern = Pattern.compile(rule);
		match = pattern.matcher(Time_Expression);
		if (match.find()) {
			flag[1] = true;
			calendar.add(Calendar.MONTH, 0);
		}

		rule = "下(个)?月";
		pattern = Pattern.compile(rule);
		match = pattern.matcher(Time_Expression);
		if (match.find()) {
			flag[1] = true;
			calendar.add(Calendar.MONTH, 1);
		}

		rule = "大前天";
		pattern = Pattern.compile(rule);
		match = pattern.matcher(Time_Expression);
		if (match.find()) {
			flag[2] = true;
			calendar.add(Calendar.DATE, -3);
		}

		rule = "(?<!大)前天";
		pattern = Pattern.compile(rule);
		match = pattern.matcher(Time_Expression);
		if (match.find()) {
			flag[2] = true;
			calendar.add(Calendar.DATE, -2);
		}

		rule = "昨";
		pattern = Pattern.compile(rule);
		match = pattern.matcher(Time_Expression);
		if (match.find()) {
			flag[2] = true;
			calendar.add(Calendar.DATE, -1);
		}

		rule = "今(?!年)";
		pattern = Pattern.compile(rule);
		match = pattern.matcher(Time_Expression);
		if (match.find()) {
			flag[2] = true;
			calendar.add(Calendar.DATE, 0);
		}

		rule = "明(?!年)";
		pattern = Pattern.compile(rule);
		match = pattern.matcher(Time_Expression);
		if (match.find()) {
			flag[2] = true;
			calendar.add(Calendar.DATE, 1);
		}

		rule = "(?<!大)后天";
		pattern = Pattern.compile(rule);
		match = pattern.matcher(Time_Expression);
		if (match.find()) {
			flag[2] = true;
			calendar.add(Calendar.DATE, 2);
		}

		rule = "大后天";
		pattern = Pattern.compile(rule);
		match = pattern.matcher(Time_Expression);
		if (match.find()) {
			flag[2] = true;
			calendar.add(Calendar.DATE, 3);
		}

		rule = "(?<=(上上(周|星期)))[1-7]";
		pattern = Pattern.compile(rule);
		match = pattern.matcher(Time_Expression);
		if (match.find()) {
			flag[2] = true;
			int week = Integer.parseInt(match.group());
			if (week == 7)
				week = 1;
			else
				week++;
			calendar.add(Calendar.WEEK_OF_MONTH, -2);
			calendar.set(Calendar.DAY_OF_WEEK, week);
		}

		rule = "(?<=((?<!上)上(周|星期)))[1-7]";
		pattern = Pattern.compile(rule);
		match = pattern.matcher(Time_Expression);
		if (match.find()) {
			flag[2] = true;
			int week = Integer.parseInt(match.group());
			if (week == 7)
				week = 1;
			else
				week++;
			calendar.add(Calendar.WEEK_OF_MONTH, -1);
			calendar.set(Calendar.DAY_OF_WEEK, week);
		}

		rule = "(?<=((?<!下)下(周|星期)))[1-7]";
		pattern = Pattern.compile(rule);
		match = pattern.matcher(Time_Expression);
		if (match.find()) {
			flag[2] = true;
			int week = Integer.parseInt(match.group());
			if (week == 7)
				week = 1;
			else
				week++;
			calendar.add(Calendar.WEEK_OF_MONTH, 1);
			calendar.set(Calendar.DAY_OF_WEEK, week);
		}

		rule = "(?<=(下下(周|星期)))[1-7]";
		pattern = Pattern.compile(rule);
		match = pattern.matcher(Time_Expression);
		if (match.find()) {
			flag[2] = true;
			int week = Integer.parseInt(match.group());
			if (week == 7)
				week = 1;
			else
				week++;
			calendar.add(Calendar.WEEK_OF_MONTH, 2);
			calendar.set(Calendar.DAY_OF_WEEK, week);
		}

		rule = "(?<=((?<!(上|下))(周|星期)))[1-7]";
		pattern = Pattern.compile(rule);
		match = pattern.matcher(Time_Expression);
		if (match.find()) {
			flag[2] = true;
			int week = Integer.parseInt(match.group());
			if (week == 7)
				week = 1;
			else
				week++;
			calendar.add(Calendar.WEEK_OF_MONTH, 0);
			calendar.set(Calendar.DAY_OF_WEEK, week);
		}

		String s = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(calendar.getTime());
		String[] time_fin = s.split("-");
		if (flag[0] || flag[1] || flag[2]) {
			_tp.tunit[0] = Integer.parseInt(time_fin[0]);
		}
		if (flag[1] || flag[2])
			_tp.tunit[1] = Integer.parseInt(time_fin[1]);
		if (flag[2])
			_tp.tunit[2] = Integer.parseInt(time_fin[2]);

	}

	/**
	 * 该方法用于更新timeBase使之具有上下文关联性
	 */
	public void modifyTimeBase() {
		String[] time_grid = new String[6];
		time_grid = normalizer.getTimeBase().split("-");

		String s = "";
		if (_tp.tunit[0] != -1)
			s += Integer.toString(_tp.tunit[0]);
		else
			s += time_grid[0];
		for (int i = 1; i < 6; i++) {
			s += "-";
			if (_tp.tunit[i] != -1)
				s += Integer.toString(_tp.tunit[i]);
			else
				s += time_grid[i];
		}
		normalizer.setTimeBase(s);
	}

	public void parseRepeat() {

		String rule = "^每隔?(\\d+|半)?(分|周|月|天|日|小时|年|秒|时)";
		Pattern pattern = Pattern.compile(rule);
		Matcher match = pattern.matcher(Time_Expression);
		repeatDistance = 1;
		if (match.find()) {
			repeatFlag = true;
			String distance = match.group(1);

			String repeatleave = match.group(2);
			if (repeatleave.equals("年")) {
				repeatType = Calendar.YEAR;
				timePointfield = 0;
			} else if (repeatleave.equals("月")) {
				repeatType = Calendar.MONTH;
				timePointfield = 1;
			} else if (repeatleave.equals("日") || repeatleave.equals("天")) {
				repeatType = Calendar.DAY_OF_MONTH;
				timePointfield = 2;
			} else if (repeatleave.contains("时")) {
				repeatType = Calendar.HOUR_OF_DAY;
				timePointfield = 3;
			} else if (repeatleave.equals("分")) {
				repeatType = Calendar.MINUTE;
				timePointfield = 4;
			} else if (repeatleave.equals("秒")) {
				repeatType = Calendar.SECOND;
				timePointfield = 5;
			} else if (repeatleave.equals("周")) {
				repeatType = Calendar.WEEK_OF_YEAR;
				timePointfield = 2;
			}
			if (distance != null && distance.equals("半")) {
				int[] types = { Calendar.YEAR, Calendar.MONTH, Calendar.WEEK_OF_YEAR, Calendar.DAY_OF_MONTH,
						Calendar.HOUR_OF_DAY, Calendar.MINUTE, Calendar.SECOND };
				int[] numbers = { 12, 4, 7, 24, 60, 60 };
				for (int i = 0; i < types.length; i++) {
					if (types[i] == repeatType) {
						if (i < numbers.length) {
							repeatType = types[i + 1];
							repeatDistance = numbers[i] / 2;
							break;
						}
					}
				}
			} else if (distance != null && distance.length() > 0) {
				repeatDistance = Integer.valueOf(distance);
			}

			LogManager.e(match.group(1) + match.group(2));
		}

		rule = "周([1-7])(到|至)周([1-7])";
		pattern = Pattern.compile(rule);
		match = pattern.matcher(Time_Expression);
		if (match.find()) {
			int from = Integer.parseInt(match.group(1));
			int to = Integer.parseInt(match.group(3));

			LogManager.e(from + "  " + to);

			if (to > from) {
				repeatType = Calendar.DAY_OF_MONTH;
				timePointfield = 2;
				repeatDistance = 1;
				repeatFlag = true;

				avalibeFlag = true;
				avalibeFrom = from;
				avalibeTo = to;
			}

		}

	}

	/**
	 * 时间表达式规范化的入口
	 * 
	 * 时间表达式识别后，通过此入口进入规范化阶段， 具体识别每个字段的值
	 * 
	 */
	private void Time_Normalization() {
		parseRepeat();
		norm_setyear();
		norm_setmonth();
		norm_setday();
		norm_sethour();
		norm_setminute();
		norm_setsecond();
		norm_setTotal();
		norm_setBaseRelated();
		norm_setCurRelated();
		modifyTimeBase();

		_tp_origin.tunit = _tp.tunit.clone();

		String[] time_grid = new String[6];
		time_grid = normalizer.getTimeBase().split("-");

		int tunitpointer = 5;
		while (tunitpointer >= 0 && _tp.tunit[tunitpointer] < 0) {
			tunitpointer--;
		}
		for (int i = 0; i < tunitpointer; i++) {
			if (_tp.tunit[i] < 0)
				_tp.tunit[i] = Integer.parseInt(time_grid[i]);
		}
		String[] _result_tmp = new String[6];
		_result_tmp[0] = String.valueOf(_tp.tunit[0]);
		if (_tp.tunit[0] >= 10 && _tp.tunit[0] < 100) {
			_result_tmp[0] = "19" + String.valueOf(_tp.tunit[0]);
		}
		if (_tp.tunit[0] > 0 && _tp.tunit[0] < 10) {
			_result_tmp[0] = "200" + String.valueOf(_tp.tunit[0]);
		}

		for (int i = 1; i < 6; i++) {
			_result_tmp[i] = String.valueOf(_tp.tunit[i]);
		}

		cale = Calendar.getInstance(); // leverage a calendar object to
										// figure out the final time

		if (Integer.parseInt(_result_tmp[0]) != -1) {
			cale.set(Calendar.YEAR, Integer.valueOf(_result_tmp[0]));
			if (Integer.parseInt(_result_tmp[1]) != -1) {
				cale.set(Calendar.MONTH, Integer.valueOf(_result_tmp[1]) - 1);
				if (Integer.parseInt(_result_tmp[2]) != -1) {
					cale.set(Calendar.DAY_OF_MONTH, Integer.valueOf(_result_tmp[2]));
					if (Integer.parseInt(_result_tmp[3]) != -1) {
						cale.set(Calendar.HOUR_OF_DAY, Integer.valueOf(_result_tmp[3]));
						if (Integer.parseInt(_result_tmp[4]) != -1) {
							cale.set(Calendar.MINUTE, Integer.valueOf(_result_tmp[4]));
							if (Integer.parseInt(_result_tmp[5]) != -1) {
								cale.set(Calendar.SECOND, Integer.valueOf(_result_tmp[5]));
							}
						}
					}
				}
			}
		}

		BaseTime = cale.getTime().getTime();

	}

	public long getRunTime() {
		Calendar changeCale = Calendar.getInstance();
		changeCale.setTimeInMillis(BaseTime);
		time = changeCale.getTime();
		if (repeatFlag) {
			while ((time.getTime() - System.currentTimeMillis()) <= 2) {

				changeCale.add(repeatType, repeatDistance);
				if (avalibeFlag) {
					int weekday = changeCale.get(Calendar.DAY_OF_WEEK);
					weekday--;
					if (weekday <= 0) {
						weekday = 7;
					}
					if (weekday < avalibeFrom || weekday > avalibeTo) {
						LogManager.e("continue");
						continue;
					}
				}

				time = changeCale.getTime();
			}
			return changeCale.getTime().getTime();
		}

		return BaseTime;
	}

	@Override
	public boolean getReapteFlag() {
		// TODO Auto-generated method stub
		return repeatFlag;
	}

	public TimerTicker getTimeTicker() {
		TimerTicker ticker = new TimerTicker(BaseTime, repeatFlag, repeatDistance, repeatType);
		ticker.setAvalibe(avalibeFlag, avalibeFrom, avalibeTo);
		return ticker;
	}

}
