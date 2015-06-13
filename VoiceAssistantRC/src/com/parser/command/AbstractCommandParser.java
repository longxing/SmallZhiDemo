package com.parser.command;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.base.data.CommandInfo;
import com.iii360.base.inf.ICommandParser;
import com.iii360.sup.common.utl.LogManager;
import com.parser.iengine.crf.CRFUtil;

public abstract class AbstractCommandParser implements ICommandParser {
	public static final String COMMAND_HEAD = "com.voice.assistant.command.";

	// event id
	public static final String EVENT_ID_COMMAND_FROM_FLOAT_BUTTON = "EventCommandFromFloatButton";
	public static final String EVENT_ID_CHAT_INFO = "EventChatInfo";
	public static final String EVENT_ID_ROLE = "EventRole";
	public static final String EVENT_ID_TEACH_MODE = "EventTeachMode";
	public static final String EVENT_ID_COMMIT_ANSWER = "EventCommitAnswer";
	public static final String EVENT_ID_SEARCH_BY_WEB = "EventSearchByWeb";
	public static final String EVENT_ID_SET_SKIN = "EventSetSkin";
	public static final String EVENT_ID_SMS_DIALOG = "EventSmsDialog";
	public static final String EVENT_ID_PLAYER_TYPE = "EventPlayerType";
	// command id
	public static final String COMMAND_NAME_EXD_EXIT = "CommandExtendExit";
	public static final String COMMAND_NAME_BASE = "VoiceCommand";
	public static final String COMMAND_NAME_CHAT = "CommandChat";
	public static final String COMMAND_NAME_MEDIA_CONTROL = "CommandMediaControl";

	public static final String COMMAND_NAME_HELP = "CommandHelp";
	public static final String COMMAND_NAME_PLAY_MEDIA = "CommandPlayMedia";
	public static final String COMMAND_NAME_WEATHER = "CommandQueryWeather";
	public static final String COMMAND_NAME_REMIND = "CommandRemind";
	public static final String COMMAND_NAME_SEPCIAL = "CommandSepcial";
	public static final String COMMAND_NAME_TRANSLATION = "CommandTranslation";
	public static final String COMMAND_NAME_STUDY = "CommandStudy";
	public static final String COMMAND_NAME_LOCAL = "CommandLocal";
	public static final String COMMAND_NAME_CLOSE_APP = "CommandCloseApp";
	public static final String COMMAND_NAME_CHAT_MODE = "CommandChatMode";

	public static final String COMMAND_NAME_MULTI = "CommandMultiParser";
	// ID20120818001 zhanglin begin
	public static final String COMMAND_NAME_NOVEL = "CommandNovel";
	public static final String COMMAND_NAME_BUY = "CommandBuy";
	public static final String COMMAND_NAME_MOVIE_TICKET = "CommandMovieTicket";
	public static final String COMMAND_NAME_DATE = "CommandDate";
	public static final String COMMAND_PLAYMEDIA_HEZI_NULL = "CommandPlayMediaHeziNull";

	// ID20121101001 hujinrong end
	// ID20130226002 hujinrong begin
	public static final String COMMAND_NAME_SYSTEM_VOLUME = "CommandSystemVolume";
	public static final String COMMAND_SYSTEM_INFO = "CommandSystemInfo";
	// ID20130226002 hujinrong end
	public static final int COMMAND_BASE = 0;
	public static final int COMMAND_ADD_VIEW = COMMAND_BASE;
	public static final int COMMAND_ADD_SESSION = COMMAND_BASE + 1;
	public final static int COMMAND_SET_MODE = COMMAND_BASE + 2;
	public final static int COMMAND_CLOSE = COMMAND_BASE + 3;
	public final static int COMMAND_EXCAT_RECOGNISE = COMMAND_BASE + 4;
	public final static int COMMAND_SCROLL_DOWN = COMMAND_BASE + 5;
	public final static int COMMAND_ALTER_MEDIA_VOLUME = COMMAND_BASE + 6;
	public final static int COMMAND_SET_START_BUTTON_VISIBLE = COMMAND_BASE + 7;
	public final static int COMMAND_DESTORY_WIDGET = COMMAND_BASE + 8;
	public final static int COMMAND_ADD_MEDIAWIDGET = COMMAND_BASE + 9;

	public static final int DEF_SESSION_ID = -1;

	private Matcher mMatcher;
	private String mContent = "";
	private String mCommandName = COMMAND_NAME_BASE;
	private HashMap<String, String> mParamMap;
	public String matchedString = "";

	protected String getContentString() {
		return mContent;
	}

	protected <T> Matcher genMatcher(T arg) {
		return null;
	}

	public AbstractCommandParser(String name, String arg) {
		this(name, (Matcher) null, false);
		mMatcher = genMatcher(arg);
		mContent = arg;
	}

	public AbstractCommandParser(String name, Matcher matcher) {
		this(name, matcher, false);
	}

	public AbstractCommandParser(String name, Matcher matcher, boolean isUseCRF) {
		// super(context);
		mMatcher = matcher;
		mCommandName = name;
		if (matcher != null) {
			mContent = matcher.group();
			// ID20121113002 zhanglin begin
			if (isUseCRF) {
				// ID20121113002 zhanglin end
				extractParams();
			} else {
				extractParamsByExpression(matcher);
			}
		}
		// setGlobalObject(KeyManager.GKEY_OBJ_LAST_PASER, this);
	}

	public <T> AbstractCommandParser makeCommandParser(Class<? extends AbstractCommandParser> commandCls, T arg) {

		return CommandParserFactory.createCommandParser(commandCls, arg);

	}

	public AbstractCommandParser makeCommandParser(Class<? extends AbstractCommandParser> commandCls) {

		return CommandParserFactory.createCommandParser(commandCls, mMatcher);
	}

	public AbstractCommandParser makeCommandParser(CommandInfo info) {
		AbstractCommandParser command = null;

		if (info == null || info._commandName == null || info._commandName.equals("")) {

			return null;
		}

		if (info._commandName.equals(COMMAND_NAME_STUDY)) {
			info.addArg(getMatcher().group());
		}

		try {
			@SuppressWarnings("unchecked")
			Class<? extends AbstractCommandParser> cls = (Class<? extends AbstractCommandParser>) Class
					.forName(COMMAND_HEAD + info._commandName);
			command = makeCommandParser(cls, info);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			LogManager.printStackTrace(e, "VoiceCommand", "makeCommand");
		}

		return command;
	}

	protected void setContent(String text) {
		mContent = text;
	}

	public String getCommandName() {
		return mCommandName;
	}

	public Matcher getMatcher() {
		return mMatcher;
	}

	/**
	 * @return MessageInfo
	 */
	public CommandInfo parser() {

		return null;
	}

	protected void extractParams() {
		mParamMap = CRFUtil.extractParams(mMatcher.group(), mCommandName);
	}

	protected boolean isNoParams() {
		return mParamMap == null || mParamMap.isEmpty();
	}

	protected String getParams(int index) {
		String name = "arg" + index;
		return getParams(name);
	}

	protected String getParams(String name) {
		if (mParamMap != null) {
			return mParamMap.get(name);
		}
		return null;
	}

	protected void setParams(int index, String value) {
		if (mParamMap != null) {
			String name = "arg" + index;
			mParamMap.put(name, value);
		}
	}

	protected void extractParamsByExpression(Matcher matcher) {
		mParamMap = new HashMap<String, String>();
	}

	/**
	 * @param index
	 *            参数序号
	 * @return 如果需要对提取出的参数做排除判断，请返回相应的map，否则返回null
	 */
	protected HashMap<String, Integer> getStopWords(int index) {
		return null;

	}

	/**
	 * @param index
	 *            参数序号
	 * @return 如果需要对提取出的参数做包含判断，请返回相应的map，否则返回null
	 */
	protected HashMap<String, Integer> getValidWords(int index) {
		return null;

	}

	private int getParamsCount() {
		if (mParamMap != null) {
			return mParamMap.size();
		}
		return 0;
	}

	private boolean checkWord(int index, HashMap<String, Integer> map) {

		// null,no need check.
		// if(map == null) {
		// return true;
		// }

		String val = getParams(index);
		Integer flg = map.get(val);

		return flg != null && ((flg & (0x01 << index)) > 0);
	}

	private boolean checkWords() {

		int cnt = getParamsCount();
		for (int i = 0; i < cnt; i++) {
			HashMap<String, Integer> mapStop = getStopWords(i);
			HashMap<String, Integer> mapValid = getValidWords(i);

			if ((mapStop != null && checkWord(i, mapStop)) || (mapValid != null && !checkWord(i, mapValid))) {
				return false;
			}
		}
		return true;

	}

	/**
	 * @param index
	 *            参数序号第一个参数传入0，第二个传入1...
	 * @param map
	 *            待初始化的map
	 * @param words
	 *            待放入的关键词列表
	 * 
	 *            请在static模块中调用本函数
	 * 
	 */
	protected static void initCheckWords(int index, HashMap<String, Integer> map, String[] words) {
		if (index < 0 || words == null || map == null) {
			return;
		}

		for (int i = 0; i < words.length; i++) {
			String key = words[i];
			Integer flg = map.get(key);
			if (flg == null) {
				flg = 0;
			}

			flg |= 0x01 << index;
			map.put(key, flg);
		}

	}

	protected boolean isItselfCommand() {

		boolean isItselfCommand = (mParamMap != null && !mParamMap.isEmpty());
		Pattern[] matchPatterns = getMatchPatterns();
		Pattern[] exceptPatterns = getExceptPatterns();

		if (isItselfCommand) {

			boolean isMatchPattern = matchPatterns(matchPatterns) != null;
			boolean isExceptPattern = matchPatterns(exceptPatterns) != null;

			if (matchPatterns != null) {
				isItselfCommand = isMatchPattern && !isExceptPattern && checkWords();
			} else {
				isItselfCommand = !isExceptPattern && checkWords();
			}
		} else {
			isItselfCommand = matchPatterns(exceptPatterns) == null;
		}

		return isItselfCommand;

	}

	protected Matcher matchPatterns(Pattern[] patternList) {

		return matchPatterns(patternList, mContent);
	}

	protected Matcher matchPatterns(Pattern[] patternList, String content) {

		if (patternList != null && content != null) {
			for (Pattern pattern : patternList) {
				Matcher m = pattern.matcher(content);
				if (m != null && m.matches()) {
					return m;
				}
			}
		}
		return null;
	}

	protected Pattern[] getMatchPatterns() {
		return null;

	}

	protected Pattern[] getExceptPatterns() {
		return null;

	}

	public CommandInfo getNextConversation(String text, String id) {
		return null;

	}

	protected void setParams(HashMap<String, String> paramMap, int count) {
		if (paramMap != null && !paramMap.isEmpty()) {
			for (int i = 0; i < count; i++) {

				String val = paramMap.get("arg" + i);
				if (val != null && !val.equals("")) {
					setParams(i, val);
					LogManager.e("set Param " + "arg" + i + ":" + val);
				}
			}
		}

	}
}
