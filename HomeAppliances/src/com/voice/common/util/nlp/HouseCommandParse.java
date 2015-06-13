package com.voice.common.util.nlp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.iii360.base.common.utl.LogManager;

/***
 * 家电本地语义解析
 * 
 * @author Peter
 * @data 2015年4月29日上午10:31:54
 */
public class HouseCommandParse {

	private static final String TAG = "HouseCommandParse";

	private static final String HOTTEXT_PATH = "/sdcard/VoiceAssistant/models/HotText2";
	private static final String SENTENCE_PATH = "/sdcard/VoiceAssistant/models/Sentence";
	private static WordManage wm;
	private static Sentence sentence;
	private static HouseCommandParse commandParse;
	// 当前配置的家电与情景
	private HashMap<String, SentenceObject> mHash = new HashMap<String, SentenceObject>();

	public static HouseCommandParse getInstanse() {
		if (commandParse == null) {
			commandParse = new HouseCommandParse();
		}

		return commandParse;
	}

	private HouseCommandParse() {
		wm = new WordManage();
		wm.readFile(HOTTEXT_PATH);
		sentence = new Sentence();
		sentence.read(SENTENCE_PATH);
	}

	public ParseResultList HandText(String text) {

		ArrayList<SentenceObject> resultList = new ArrayList<SentenceObject>();
		ArrayList<WordObj> mSegList = wm.getSegList(text);
		int resultscore = Integer.MAX_VALUE;

		if (mSegList.size() == 1) {
			if (mSegList.get(0).getType().equals("父动作")) {
				return null;
			}
		}

		for (SentenceObject s : mHash.values()) {
			// 数值越小，表示越准确
			int tempReuslt = wm.comPair(mSegList, s.getTextObjects()) + s.getResultDistance();
			// 取得最接近的家电
			if (tempReuslt < resultscore) {
				resultscore = tempReuslt;
				resultList.clear();
				resultList.add(s);
			} else if (tempReuslt == resultscore) {
				// 累计得分相同项
				resultList.add(s);
			}
		}
		LogManager.e("result score " + resultscore);
		ParseResultList result = new ParseResultList(resultList, resultscore);
		isHouseCommand(text);
		return result;
	}

	public void addContent(String corder, String dorder, String target) {
		LogManager.e(corder);
		SentenceObject s = new SentenceObject(wm.getSegList(corder), corder);
		s.setOrder(dorder);
		s.setTarget(target);
		LogManager.e(s.toString());
		mHash.put(corder, s);
	}

	public void removeContent(String corder) {
		mHash.remove(corder);
	}

	private static String head = "^小智?(给|帮)?我?想?";
	private static Pattern pattern = Pattern.compile(head);

	public String isHouseCommand(String text) {
		Matcher m = pattern.matcher(text);
		if (m.find()) {
			text = text.replace(m.group(), "");
		}
		LogManager.e(text);
		ArrayList<WordObj> mSegList = wm.getSegList(text);
		int i = 0;
		if (sentence.isFullOrder(mSegList)) {
			for (WordObj w : mSegList) {
				if (!w.getType().equals("其他")) {
					if (w.getType().equals("模式") && w.getWord().equals("回家")) {
						text = w.getWord() + w.getType();
						i += text.length();
					} else {
						i += w.getWord().length();
					}
				}
			}

			if (i > (text.length() * 0.8)) {
				return text;
			}
		}
		return null;
	}

}
