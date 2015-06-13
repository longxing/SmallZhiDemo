package com.parser.command;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.iii360.base.common.utl.LogManager;

/**
 * 语音结果预解析
 * 
 * @author Peter
 * @data 2015年4月10日下午5:35:07
 */
public class CommandPreParserFactory {

	private static final Pattern[] match_remind_pattern = new Pattern[] { Pattern.compile("(提醒我|叫我|通知我|叫醒我|喊我|喊醒我)(.*)?"), Pattern.compile("(闹钟|闹铃|备忘|提醒).*") };

	private static final Pattern[] match_play_pattern = new Pattern[] { Pattern.compile("(.*)([放看]视频)(.*)?"), Pattern.compile("((?!帮我看看).*)([放播]个?电影)(.*)?"),
			Pattern.compile("((?!帮我看看).*)([放播]部电影)(.*)?"), Pattern.compile("((?!帮我看看).*)(放一部电影)(.*)?"), Pattern.compile("([听放]音频)(.*)?"), Pattern.compile("([听放]音乐)(.*)?"),
			Pattern.compile("([唱听放]歌)(.*)?"), Pattern.compile("([唱听放来]个(歌|音乐))(.*)?"), Pattern.compile("([唱听放来]首(歌|音乐))(.*)?"), Pattern.compile("([唱听放来]一首)(.*)?"), Pattern.compile("(我要听)(.*)?"),
			Pattern.compile("([放唱])((?!上一首|下一首|换一首|前一|后一|上首|下首|上一个|下一个|假|完假|个故事|故事).*)?"), };

	private static final Pattern[] match_media_ctrl_pattern = new Pattern[] { Pattern.compile("(下|上|前|后|换)(一首歌|首歌|一[个首曲]|首音乐|个歌)(.*)?"), Pattern.compile("()(开始|启动|继续播放)()"),
			Pattern.compile("(|给我|我要|你给我|)(开始|启动|继续)(音乐|视频|歌曲|歌|声音|一首歌)(.*)?"), Pattern.compile(".{0,4}(音乐|视频|歌曲|歌|声音)(开始|继续|启动)(.*)?"), Pattern.compile("()(关闭|退出)(音乐|视频|播放器)(.*)?"),
			Pattern.compile(".{0,4}(音乐|视频|歌曲|歌|声音)(关闭|退出)(.*)?"), Pattern.compile("()(停)(止|掉)(音乐|视频|歌曲|歌|声音|这首|播放)(.*)?"), Pattern.compile("(.*)(停)(止|掉)()"), Pattern.compile("()(暂停)()()"),
			Pattern.compile(".{0,3}(音乐|视频|歌曲|歌|声音|这首|播放)(停)(.*)?"), Pattern.compile("()(停)(止|掉)(音乐|视频|歌曲|歌|声音|这首|播放)(.*)?"), Pattern.compile("()(换)(另外|别|)(一首歌|首歌|一个|个歌|的歌)(.*)?"),
			Pattern.compile("(单曲)?(循环|循环)(这首歌)?(.*)?"), Pattern.compile("(?!(播放).*)(好听|难听)(.*)?"), Pattern.compile("(?!(播放).*)(喜欢|讨厌)(听|这歌|这首歌)?(.*)?") };

	private static final Pattern[] match_system_volumn_pattern = new Pattern[] { Pattern.compile("(.*音量)((升高)|(调高)|(调大)|(加大)|(条大)|(条高)|(调到最大)|(调到最高))(.*)"),
			Pattern.compile("(.*)((升高)|(调高)|(调大)|(加大)|(条大)|(条高)|(调到最大)|(调到最高))(音量.*)"), Pattern.compile("(.*)(音量大)(.*)"), Pattern.compile("(.*)(大声点)(.*)"),
			Pattern.compile("(.*音量)((降低)|(调小)|(调低)|(条小)|(条低)|(调到最低)|(调到最小))(.*)"), Pattern.compile("(.*)((降低)|(调小)|(调低)|(条小)|(条低)|(调到最低)|(调到最小))(音量.*)"), Pattern.compile("(.*)(音量小)(.*)"),
			Pattern.compile("(.*)(你太吵)(.*)"), Pattern.compile("(.*)(太吵了)(.*)"), Pattern.compile("(.*)(小声点)(.*)"), };

	private static final Pattern[] match_chatmode_pattern = new Pattern[] { Pattern.compile("(.*)(聊天模式)(.*)"), };

	private static final Pattern[] match_systeminfo_pattern = new Pattern[] { Pattern.compile("(.*)(当前网络|IP地址|MAC地址)(.*)"), };

	private static ArrayList<Pattern[]> mCommandParse = new ArrayList<Pattern[]>();

	static {

		mCommandParse.add(match_system_volumn_pattern);

		mCommandParse.add(match_remind_pattern);
		mCommandParse.add(match_media_ctrl_pattern);
		mCommandParse.add(match_play_pattern);

		mCommandParse.add(match_chatmode_pattern);
		mCommandParse.add(match_systeminfo_pattern);
	}

	public static String makeParser(String result) {

		String parser = null;

		if (result != null && !result.trim().equals("")) {
			parser = parseResult(result);
		}
		return parser;
	}

	private static String parseResult(String result) {

		for (Pattern[] item : mCommandParse) {
			Pattern[] patterns = item;

			for (Pattern pattern : patterns) {
				Matcher m = pattern.matcher(result);
				if (m.find()) {
					LogManager.i("CommandPreParserFactory", pattern.toString() + " : " + result + " : " + m.group());
					return m.group();
				}
			}
		}
		LogManager.e("none patter");
		return null;

	}

}
