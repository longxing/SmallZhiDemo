package com.parser.command;

import java.util.Date;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.base.data.CommandInfo;
import com.iii360.sup.common.utl.LogManager;
import com.parser.extractor.date.DateInfo;
import com.parser.iengine.crf.CRFUtil;
import com.parser.operator.OperatorServer;

public class CommandQueryWeatherParser extends AbstractCommandParser {

	// public static final String QUERY_URL = "http://m.weather.com.cn/data/";
	public static final String QUERY_URL = "weather/getWeather?city=";
	public static final String QUERY_REALTIME_URL = "http://www.weather.com.cn/data/sk/";
	public static final String IWEATHER_ADDRESS = "http://www.google.com/ig/api?hl=zh-cn&weather=,,,";
	public static final String WEATHER_TEXT_FORMAT = "(city)(date)(weather)，(wind)，气温：(degree)。(note)";

	private static final long mDayTime = 86400000L;
	public static final String DEF_CITY_CODE = "101010100";

	// private static final int DATA_INDEX_FIRST_DAY = 0;
	// private static final int DATA_INDEX_SECOND_DAY = 1;
	// private static final int DATA_INDEX_THIRD = 2;
	// private static final int DATA_INDEX_FOURTH = 3;

	private String mCity;
	private String mDateText;

	private final static String SPECIAL_CITY_ARRAY[] = { "呼市郊区", "尖草坪区", "小店区", "淮阴区", "黄山区", "黄山风景区", "赫山区", "通化县",
			"本溪县", "辽阳县", "建平县", "承德县", "大同县", "五台县", "伊宁县", "芜湖县", "南昌县", "上饶县", "吉安县", "衡阳县", "邵阳县", "遵义县", "宜宾县",
			"黄山市" };

	private static final Pattern[] match_next_pattern = new Pattern[] {
			// Pattern.compile("(.*)(那请问|那么请问|那么|那好|那|请问)(下午|今晚|中午|上午|傍晚|早上|早晨)(呢)"),
			// Pattern.compile("(.*)(那|请问|那么)(下午|今晚|中午|上午|傍晚|早上|早晨)(呢)"),
			Pattern.compile("(.*)(那请问|那么请问|那么|那好|那|请问)(.*)(呢|怎样|好吗|如何|咋样|好不|好不好)"),
			Pattern.compile("(.*)()(.*)(呢|怎样|好吗|如何|咋样|好不|好不好)"),
	// Pattern.compile("(.*)(那|请问|那么)(.*)(呢)"),
	};

	private static final Pattern[] match_pattern = new Pattern[] { Pattern.compile("天气"),
			Pattern.compile("(.*)(天气|天)(咋样|怎样|好吗|好不|如何)(.*)?"), Pattern.compile("(.*)(查询|显示|搜索|告诉我|查下|查看)(天气)(.*)?"),
			Pattern.compile("(.*)(外边|外面|下午|今晚|中午|傍晚|早上|早晨|上午)(气温|温度)(咋样|怎样|如何|高|低)(.*)?"),
			Pattern.compile("(.*)(外边|外面|下午|今晚|中午|上午|傍晚|早上|早晨)(会?出太阳|会?有雨|会?下雨|会?刮风|会?下雪|会?有雪|会?打雷|会?冷|会?热)(.*)?"),
			Pattern.compile("(.*)(外边|外面|下午|今晚|中午|上午|傍晚|早上|早晨)(会不会)(出太阳|有雨|下雨|刮风|下雪|有雪|打雷|冷|热)(.*)?"),

	};

	private static final Pattern[] except_pattern = new Pattern[] { Pattern
			.compile("(.*)(真好|不想|不需要|除非|不要|放屁|乱说|听|播放|唱)(.*)?"),
	// Pattern.compile("(.*)(好)(，|。|！)(.*)?"),
	};

	private static final String[] thesaurus_place = { "外边", "外面", "屋外", "门外", "窗外", "车外",

	};

	private static final String[] thesaurus_date = { "上午", "中午", "早上", "晚上", "白天", "夜间", "凌晨", "下午", "傍晚"

	};

	public CommandQueryWeatherParser(String arg) {
		super(COMMAND_NAME_WEATHER, arg);

	}

	public CommandQueryWeatherParser(Matcher matcher) {
		super(COMMAND_NAME_WEATHER, matcher, true);
		LogManager.d("CommandQueryWeather", "Construct");

	}

	private String getDataText(String text) {
		String date = "";

		return text;
	}

	@Override
	public CommandInfo parser() {
		CommandInfo info = null;
		if (isItselfCommand()) {
			info = makeMessageInfo();
		}
		return info;
	}

	private String formatName(String src) {
		String ret = src;
		boolean isSpecialName = false;

		for (String name : SPECIAL_CITY_ARRAY) {
			if (ret.equals(name)) {
				isSpecialName = true;
				break;
			}
		}
		if (!isSpecialName && ret.length() > 2) {
			// if(ret.endsWith("县") || ret.endsWith("区") || ret.endsWith("市")) {
			//
			// }
			ret = ret.replaceAll("(县|区|市)", "");
		}
		LogManager.i("name:" + ret);
		return ret;
	}

	@Override
	protected void extractParams() {

		super.extractParams();
		String content = getContentString();
		LogManager.e(content);
		
		Date date = (Date) OperatorServer.getData(OperatorServer.EXTRACTOR_DATE, new DateInfo(getParams(1)));
		int day = getDayIndex(date);
		LogManager.e("day "+day);
		setParams(1, String.valueOf(day));
//		date = (Date) OperatorServer.getData(OperatorServer.EXTRACTOR_DATE, new DateInfo(getParams(0)));
//		if (date != null) {
//			setParams(0, "");
//		}
//		setParams(1, String.valueOf(day));
		
		

	}

	private int getDayIndex(Date date) {

		int day = 0;

		if (date != null) {
			long intereval = date.getTime() - System.currentTimeMillis();
			if (intereval < 0) {
				intereval -= 500000;
			} else {
				intereval += 500000;
			}
			day = (int) ((intereval) / mDayTime);
		}
		return day;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.parser.command.AbstractCommandParser#getMatchPatterns()
	 */
	@Override
	protected Pattern[] getMatchPatterns() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected Pattern[] getExceptPatterns() {
		// TODO Auto-generated method stub
		return except_pattern;
	}

	private CommandInfo makeMessageInfo() {
		CommandInfo info = null;

		String city = getParams(0);
		if (city != null) {
			city = city.replaceAll("播报|播放", "");
			city = formatName(city);
		}
		LogManager.e("arg0:" + city);
		// if(mDateText == null) {
		mDateText = getParams(1);
		// }
		LogManager.e("arg1:" + mDateText);
		String time = getParams(2);

		LogManager.e("arg2:" + time);

		info = new CommandInfo();
		info._commandName = COMMAND_NAME_WEATHER;
		info.addArg(city);
		info.addArg(mDateText);
		info.addArg(time);

		return info;
	}

	@Override
	public CommandInfo getNextConversation(String text, String id) {
		CommandInfo info = null;
		setContent(text);
		Matcher m = matchPatterns(match_next_pattern);
		if (m != null) {
			HashMap<String, String> paramMap = CRFUtil.extractParams(text, getCommandName());

			setParams(paramMap, 3);
			info = makeMessageInfo();
		}

		return info;
	}

}
