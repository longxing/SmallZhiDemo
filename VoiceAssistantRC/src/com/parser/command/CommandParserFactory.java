package com.parser.command;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.iii360.sup.common.utl.LogManager;

/**
 * 主要用于离线语义解析
 * 
 * @author Peter
 * @data 2015年4月10日下午2:48:44
 */
public class CommandParserFactory {


	private static final Pattern[] match_weather_pattern = new Pattern[] { Pattern.compile("(.*)(查询|查看|显示|搜索|告诉我)(.*)(大后天)(的?天气)(.*)?"),
			Pattern.compile("(.*)(查询|查看|显示|搜索|告诉我)(.*)(今天|明天|后天)(的?天气)(.*)?"), Pattern.compile("(.*)(查询|查看|显示|搜索|告诉我)(.*)(的天气)(.*)?"), Pattern.compile("(.*)(查询|查看|显示|搜索|告诉我)(.*)(天气)(.*)?"),
			Pattern.compile("(.*)(查询|查看|显示|搜索|告诉我)(.*)(什么天气)(.*)?"), Pattern.compile("(.*)(查询天气|查看天气|显示天气|搜索天气|告诉我天气)(.*)?"), Pattern.compile("(.*)(大后天外边)(的?)(天|温度|气温)(.*)?"),
			Pattern.compile("(.*)(白天外边|中午外边|下午外边|今晚外边|今天外边|明天外边|后天外边)(的?)(天|温度|气温)(.*)?"), Pattern.compile("(.*)(大后天)(什么天气|什么气温|什么温度)(.*)?"), Pattern.compile("(.*)(大后天)(的?天气|的?气温|的?温度)(.*)?"),
			Pattern.compile("(.*)(今天|今晚|明天|后天|大后天)?(中午|下午|早.|晚上|夜晚|凌晨|白天|外边)?(什么天气|什么气温|什么温度)(.*)?"), Pattern.compile("(.*)(今天|今晚|明天|后天|大后天)?(中午|下午|早.|晚上|夜晚|凌晨|白天|外边)?(的?天气|的?气温|的?温度)(.*)?"),
			Pattern.compile("(.*)(的天气)(.*)?"), Pattern.compile("(.*)(什么天气)(.*)?"), Pattern.compile("(.*)(天气)(.*)?"),
			Pattern.compile("(.*)(大后天)(会冷么|会冷吧|会冷不|会热么|会热吧|会热不|会冷吗|会热吗|天会?如何|天会?好吗|天会?怎样)(.*)?"), Pattern.compile("(.*)(大后天)(冷么|冷吧|冷不|热么|热吧|热不|冷吗|热吗|天会?如何|天会?好吗|天会?怎样)(.*)?"),
			Pattern.compile("(.*)(今天|今晚|晚上|白天|中午|下午|明天|后天|外边)(冷吗|热吗|冷不|热不)(.*)?"), Pattern.compile("(.*)(今天|今晚|晚上|白天|中午|下午|明天|后天)(会?冷么|会?热么|会?冷吗|会?热吗|天如何|天好吗|天怎样)(.*)?"),
			Pattern.compile("(.*)(大后天)(的?天气?)(会不会冷|会不会热)(.*)?"), Pattern.compile("(.*)(大后天)(的?天气?)(会?冷|会?热|会?如何|会?好吗|会?怎样)(.*)?"),
			Pattern.compile("(.*)(今天|今晚|晚上|白天|中午|下午|明天|后天|外边)(的?天气?)(会?冷|会?热|会?如何|会?好吗|会?怎样)(.*)?"), Pattern.compile("(.*)(的天)(会?冷|会?热|会?如何|会?好吗|会?怎样)(.*)?"),
			Pattern.compile("(.*)(天会?冷|天会?热|天会?如何|天会?好吗|天会?怎样)(.*)?"), Pattern.compile("(.*)(的气温)(会?如何|会?好吗|会?怎样)(.*)?"), Pattern.compile("(.*)(气温)(会?如何|会?好吗|会?怎样)(.*)?"),
			Pattern.compile("(.*)(的气温)(.*)?"), Pattern.compile("(.*)(气温)(.*)?"), Pattern.compile("(.*)(大后天会不会)(有雨|下雨|刮风|下雪|有雪|打雷|冷|热)(.*)?"),
			Pattern.compile("(.*)(大后天会?)(有雨|下雨|刮风|下雪|有雪|打雷|冷|热)(.*)?"), Pattern.compile("(.*)(今天会不会|今晚会不会|晚上会不会|白天会不会|中午会不会|下午会不会|明天会不会|后天会不会)(有雨|下雨|刮风|下雪|有雪|打雷|冷|热)(.*)?"),
			Pattern.compile("(.*)(今天会?|今晚会?|晚上会?|白天会?|中午会?|下午会?|明天会?|后天会?)(有雨|下雨|刮风|下雪|有雪|打雷|冷|热)(.*)?"), Pattern.compile("(.*)(会不会有雨|会不会下雨|会不会刮风|会不会下雪|会不会有雪|会不会打雷|会不会冷|会不会热)(.*)?"),
			Pattern.compile("(.*)(会有雨|会下雨|会刮风|会下雪|会有雪|会打雷|天气?冷|天气?热)(.*)?"), Pattern.compile("(.*)(有雨|下雨|刮风|下雪|有雪|打雷|天气?冷|天气?热)(.*)?"), Pattern.compile("(.*)(冷吗|热吗|冷不|热不)(.*)?"),
			Pattern.compile("(.*)(会冷吗|会热吗|冷不|热不)(.*)?"),

			Pattern.compile("(查询|查看|显示|搜索|告诉我)?(.*)(天)(怎样|好不好|好吗|怎样呢)(.*)?"), Pattern.compile("(查询|查看|显示|搜索|告诉我)?(.*)(天气)(.*)(怎样|好不好|吗|呢)(.*)?"),
			Pattern.compile("(查询|查看|显示|搜索|告诉我)?(.*)(下雪|下雾|有霜|有雾|下雨|雹子|冷|热|打雷|下霜|冰雹|浮尘|刮风|扬沙|雨|雪|闪电|出太阳|凉快|凉爽)(.*)(吗|呢|吧|不)(.*)?"),
			Pattern.compile("(.*)(会不会)(下雪|下雾|有霜|有雾|有雷阵雨|有台风|刮台风|下雨|雹子|冷|热|打雷|下霜|冰雹|浮尘|刮风|扬沙|雨|雪|闪电|出太阳|凉快|凉爽)(.*)?"),
			Pattern.compile("(.*)(会)(下雪|下雾|有霜|有雾|下雨|雹子|冷|热|打雷|下霜|冰雹|浮尘|刮风|扬沙|雨|雪|闪电|出太阳|凉快|凉爽)(.*)(吗|吧)(.*)?"), Pattern.compile("(.*)(天气|气温)(.*)?"), };

	private static final Pattern[] match_remind_pattern = new Pattern[] {

	Pattern.compile("(不可延时的)?(.{3,})?(提醒我|叫我|通知我|叫醒我|喊我|喊醒我)(.*)?"), Pattern.compile("(不可延时的)?.*?(设置|设定|调|挑|定|订)(.*)?(闹钟|闹铃|备忘|提醒).*") };


	private static final Pattern[] match_play_pattern = new Pattern[] { Pattern.compile("(.*)?([听放]音乐)"), Pattern.compile("(播放)"), Pattern.compile("(.*)?([唱听放](歌|歌曲))"),
			Pattern.compile("(.*)?([唱听放来]个(歌|歌曲|音乐))"), Pattern.compile("(.*)?([唱听放来]首(歌|歌曲|音乐))"), Pattern.compile("(.*)?([唱听放来]一?首歌)"), };
	private static final Pattern[] match_translation_pattern = new Pattern[] { Pattern.compile(".*(把)(.*)(翻译[成为])(.*)?"), Pattern.compile(".*(把)(.*)(发艺成|反义成)(.*)?"),
			Pattern.compile(".*(把)(.*)(用)(.*)?"), Pattern.compile(".*(把)(.*)([翻译转]成为)(.*)?"), Pattern.compile(".*(把)(.*)([翻译转][成为])(.*)?"), Pattern.compile(".*(翻译)(.*)(成为)(.*)?"),
			Pattern.compile(".*(翻译)(.*)([成为])(.*)?"), Pattern.compile("(.*)([翻译转])(.*)([成为])(.*)?"), Pattern.compile("(.*)([翻译转][为成])(.*)?"), Pattern.compile("(.*)(翻译[成为])(.*)?"),
			Pattern.compile("(.*)(翻译)(.*)()(.*)?"), Pattern.compile("(.*)[用|的]([英法德韩])(语|话|国语|国话|国的语言|国的话)(怎么|是什么)(.*)?"), Pattern.compile("(.*)[用|的](繁体)(怎么|是什么)(.*)?"),
			Pattern.compile("(.*)[用|的](日本话|日本语|朝鲜话|朝鲜语|朝鲜的话|意大利语|意大利话|西班牙语|西班牙话)(怎么|是什么)(.*)?"), };

	private static final Pattern[] match_media_ctrl_pattern = new Pattern[] { Pattern.compile("(.*)(下|上|前|后|换)(一首歌|首歌|一[个首曲]|首音乐|个歌)(.*)?"), Pattern.compile("(.*)(开始|启动|继续|继续播放)()"),
			Pattern.compile("(|给我|我要|你给我|)(开始|启动|继续)(音乐|视频|歌曲|歌|声音|一首歌)(.*)?"), Pattern.compile(".{0,4}(音乐|视频|歌曲|歌|声音)(开始|继续|启动)(.*)?"), Pattern.compile("(.*)(关闭|退出)(音乐|视频|播放器)(.*)?"),
			Pattern.compile(".{0,4}(音乐|视频|歌曲|歌|声音)(关闭|退出)(.*)?"), Pattern.compile("(.*)(停)(止|掉)(音乐|视频|歌曲|歌|声音|这首|播放)(.*)?"), Pattern.compile("(.*)(停)(止|掉)()"), Pattern.compile("(.*)(暂停)()()"),
			Pattern.compile(".{0,3}(音乐|视频|歌曲|歌|声音|这首|播放)(停)(.*)?"), Pattern.compile("(.*)(停)(音乐|视频|歌曲|歌|声音|这首|播放)(.*)?"), Pattern.compile("(.*)(换)(另外|别|)(一首歌|首歌|一个|个歌|的歌)(.*)?"),
			Pattern.compile("(单曲)?(循环|循环)(这首歌)?(.*)?"), Pattern.compile("(?!(播放|来首).*)真?很?(不好听|好听|难听)(.*)?"), Pattern.compile("(?!(播放).*)我?(喜欢|讨厌)(听|这歌|这首歌)(.*)?"),
			Pattern.compile("(?!(播放).*)我?(喜欢|讨厌)") };
	private static final Pattern[] match_system_volumn_pattern = new Pattern[] { Pattern.compile("(.*音量)((升高)|(调高)|(调大)|(加大)|(条大)|(条高)|(调到最大)|(调到最高))(.*)"),
			Pattern.compile("(.*)((升高)|(调高)|(调大)|(加大)|(条大)|(条高)|(调到最大)|(调到最高))(音量.*)"), Pattern.compile("(.*)(音量大)(.*)"), Pattern.compile("(.*)(大声点)(.*)"),
			Pattern.compile("(.*音量)((降低)|(调小)|(调低)|(条小)|(条低)|(调到最低)|(调到最小))(.*)"), Pattern.compile("(.*)((降低)|(调小)|(调低)|(条小)|(条低)|(调到最低)|(调到最小))(音量.*)"), Pattern.compile("(.*)(音量小)(.*)"),
			Pattern.compile("(.*)(你太吵)(.*)"), Pattern.compile("(.*)(太吵了)(.*)"), Pattern.compile("(.*)(小声点)(.*)"), };


	public static final Pattern[] match_chatmode_pattern = new Pattern[] { Pattern.compile("(.*)(聊天模式)(.*)"), };

	private static final Pattern[] match_systeminfo_pattern = new Pattern[] { Pattern.compile("(.*)?(当前网络|IP地址|物理地址|调试环境|正式环境|当前版本|播放器调试)(.*)?"), };

	private static class SearchPair {
		public Class<? extends AbstractCommandParser> _class;
		public Pattern[] _partternSet;

		SearchPair(Class<? extends AbstractCommandParser> cls, Pattern[] partternSet) {
			_class = cls;
			_partternSet = partternSet;
		}
	}

	private static ArrayList<SearchPair> mCommandParse = new ArrayList<SearchPair>();

	static {

		mCommandParse.add(new SearchPair(CommandSystemVolumeParser.class, match_system_volumn_pattern));

		mCommandParse.add(new SearchPair(CommandQueryWeatherParser.class, match_weather_pattern));

		mCommandParse.add(new SearchPair(CommandTranslationParser.class, match_translation_pattern));

		mCommandParse.add(new SearchPair(CommandRemindParser.class, match_remind_pattern));

		mCommandParse.add(new SearchPair(CommandMediaControlParser.class, match_media_ctrl_pattern));

		mCommandParse.add(new SearchPair(CommandPlayMediaParser.class, match_play_pattern));




		mCommandParse.add(new SearchPair(CommandChatModeParser.class, match_chatmode_pattern));
		mCommandParse.add(new SearchPair(CommandSystemInfoParser.class, match_systeminfo_pattern));
	}

	private CommandParserFactory() {
	}

	public static AbstractCommandParser makeParser(String result) {

		AbstractCommandParser parser = null;

		if (result != null && !result.trim().equals("")) {

			parser = parseResult(result);

		}
		return parser;
	}

	@SuppressWarnings("unchecked")
	public static <T> AbstractCommandParser createCommandParser(String commandName, T arg) {
		AbstractCommandParser command = null;
		Class<? extends AbstractCommandParser> cls;
		try {
			cls = (Class<? extends AbstractCommandParser>) Class.forName(AbstractCommandParser.COMMAND_HEAD + commandName);
			command = createCommandParser(cls, arg);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			LogManager.printStackTrace(e);
		}
		return command;

	}

	public static AbstractCommandParser matchCommandParser(Class<? extends AbstractCommandParser> cls, String result) {
		AbstractCommandParser newCmd = null;

		find_cls: for (SearchPair item : mCommandParse) {
			if (item._class.equals(cls)) {
				Pattern[] patterns = item._partternSet;

				for (Pattern pattern : patterns) {
					Matcher m = pattern.matcher(result);
					if (m.matches()) {
						newCmd = createCommandParser(cls, m);

						break find_cls;

					}
				}

				break find_cls;
			}

		}
		return newCmd;
	}

	public static <T> AbstractCommandParser createCommandParser(Class<? extends AbstractCommandParser> cls, T arg) {

		AbstractCommandParser newCmd = null;
		Constructor<? extends AbstractCommandParser> constructor;
		try {
			constructor = cls.getConstructor(new Class[] { arg.getClass() });

			newCmd = constructor.newInstance(new Object[] { arg });
		} catch (SecurityException e) {
			LogManager.printStackTrace(e);
		} catch (NoSuchMethodException e) {
			LogManager.printStackTrace(e);
		} catch (IllegalArgumentException e) {
			LogManager.printStackTrace(e);
		} catch (InstantiationException e) {
			LogManager.printStackTrace(e);
		} catch (IllegalAccessException e) {
			LogManager.printStackTrace(e);
		} catch (InvocationTargetException e) {
			LogManager.printStackTrace(e);
		}
		return newCmd;
	}

	private static AbstractCommandParser parseResult(String result) {

		AbstractCommandParser newCmd = null;

		find_cls: for (SearchPair item : mCommandParse) {
			Class<? extends AbstractCommandParser> cls = item._class;
			Pattern[] patterns = item._partternSet;

			for (Pattern pattern : patterns) {
				Matcher m = pattern.matcher(result);

				if (m.matches()) {
					newCmd = createCommandParser(cls, m);
					break find_cls;
				}
			}
		}
		return newCmd;

	}
}
