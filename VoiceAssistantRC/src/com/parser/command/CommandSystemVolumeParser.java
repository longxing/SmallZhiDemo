package com.parser.command;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.base.data.CommandInfo;

public class CommandSystemVolumeParser extends AbstractCommandParser {

	private static final String INCREASE_VOLUME = "1";
	private static final String DECREASE_VOLUME = "0";

	private static final Pattern[] increaseVolume = new Pattern[] {
			Pattern.compile("(.*音量)((升高)|(调高)|(调大)|(加大)|(条大)|(条高)|(调到最大)|(调到最高))(.*)"),
			Pattern.compile("(.*)((升高)|(调高)|(调大)|(加大)|(条大)|(条高)|(调到最大)|(调到最高))(音量.*)"),
			Pattern.compile("(.*)(音量大)(.*)"), Pattern.compile("(.*)(大声点)(.*)"), };
	private static final Pattern[] decreaseVolume = new Pattern[] {
			Pattern.compile("(.*音量)((降低)|(调小)|(调低)|(条小)|(条低)|(调到最低)|(调到最小))(.*)"),
			Pattern.compile("(.*)((降低)|(调小)|(调低)|(条小)|(条低)|(调到最低)|(调到最小))(音量.*)"),
			Pattern.compile("(.*)(音量小)(.*)"), Pattern.compile("(.*)(你太吵)(.*)"),
			Pattern.compile("(.*)(太吵了)(.*)"), Pattern.compile("(.*)(小声点)(.*)"), };
	private static Pattern[][] patterns = new Pattern[][] { increaseVolume,
			decreaseVolume };
	private static String[] pattersMapping = new String[] { INCREASE_VOLUME,
			DECREASE_VOLUME };
	private String mRecContent;
	private Pattern[] exceptionPattern = new Pattern[] { Pattern
			.compile("(.*)(不想|不需要|除非|不要|放屁|乱说|听|播放|唱)(.*)?"), };

	@Override
	protected Pattern[] getExceptPatterns() {
		// TODO Auto-generated method stub
		return exceptionPattern;
	}

	public CommandSystemVolumeParser(Matcher matcher) {
		super(COMMAND_NAME_SYSTEM_VOLUME, matcher);
		// TODO Auto-generated constructor stub
		mRecContent = matcher.group();
	}

	@Override
	public CommandInfo parser() {
		// TODO Auto-generated method stub
		if (!isItselfCommand()) {
			return null;
		}
		CommandInfo info = null;
		for (int i = 0; i < patterns.length; i++) {
			Pattern[] eachPatterns = patterns[i];
			for (int j = 0; j < eachPatterns.length; j++) {
				Pattern pattern = eachPatterns[j];
				Matcher matcher = pattern.matcher(mRecContent);
				if (matcher.matches()) {
					info = new CommandInfo();
					info._commandName = COMMAND_NAME_SYSTEM_VOLUME;
					info.addArg(pattersMapping[i]);
					if (mRecContent.contains("最") || mRecContent.contains("关闭")) {
						info.addArg("1");
					}
					return info;
				}
			}
		}
		return info;
	}

}
