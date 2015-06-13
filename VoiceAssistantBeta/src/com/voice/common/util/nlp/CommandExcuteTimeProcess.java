package com.voice.common.util.nlp;

import com.iii360.base.common.utl.LogManager;
import com.iii360.base.inf.BasicServiceUnion;
import com.iii360.sup.common.utl.stringPreHandlingModule;
import com.parser.command.CommandPreParserFactory;
import com.voice.assistant.main.KeyList;
import com.voice.common.util.time.TimeProcess;
import com.voice.common.util.time.TimeUnit;

/**
 * 备忘设置命令
 * 
 * @author Peter
 * @data 2015年4月10日下午2:18:05
 */
public class CommandExcuteTimeProcess {

	private BasicServiceUnion mUnion;
	private TimeProcess tp;
	private static CommandExcuteTimeProcess TimeProcess;

	public static CommandExcuteTimeProcess getInstance(BasicServiceUnion union) {

		if (union.getCommandEngine() == null) {
			return null;
		}

		if (TimeProcess == null) {
			TimeProcess = new CommandExcuteTimeProcess(union);
		}
		return TimeProcess;
	}

	public static void destory() {
		TimeProcess = null;
	}

	public CommandExcuteTimeProcess(BasicServiceUnion union) {
		mUnion = union;
		tp = new TimeProcess(union.getBaseContext().getContext());
	}

	public boolean handText(final String text) {

		if (text.matches("(.*)(聊天模式)(.*)")) {
			return false;
		}

		String matchedString = CommandPreParserFactory.makeParser(text);
		if (matchedString != null && matchedString.length() > 0) {
			LogManager.e(matchedString);

			String TimeCheckText = text.replace(matchedString, "");
			final TimeUnit tu = tp.handText(TimeCheckText);
			if (tu != null) {
				// 支持TTS Debug播报
				if (KeyList.IS_TTS_DEBUG) {
					mUnion.getTTSController().syncPlay("正在使用离线语义识别");
					mUnion.getTTSController().syncPlay("识别结果为添加备忘录命令");
				}
				LogManager.e((tu.getRunTime() - System.currentTimeMillis()) + "");
				if (tu.getRunTime() < System.currentTimeMillis()) {
					mUnion.getMainThreadUtil().sendNormalWidget("备忘设置失败，提醒时间小于当前时间");
				} else {
					String commandString = stringPreHandlingModule.numberTranslator(text).replace(tu.Time_Expression, "");
					commandString = stringPreHandlingModule.meanExtract(commandString);
					LogManager.e(commandString);
					mUnion.getTaskSchedu().pushStackWithTask(mUnion, commandString, tu.getTimeTicker());
					String notic = "设置成功，提醒内容为：" + commandString + ";时间为：" + tp.getSaysTime(tu) + ";距离现在还有：" + tp.getDistanceTime(tu);
					mUnion.getMainThreadUtil().sendNormalWidget(notic);
				}

				return true;
			}
		}
		return false;
	}

	public TimeProcess getTp() {
		return tp;
	}
}
