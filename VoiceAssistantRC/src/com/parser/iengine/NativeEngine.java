package com.parser.iengine;

import android.content.Context;

import com.base.data.CommandInfo;
import com.base.platform.Platform;
import com.base.util.KeyManager;
import com.iii360.base.common.utl.KeyList;
import com.iii360.base.inf.ITTSController;
import com.iii360.sup.common.utl.LogManager;
import com.parser.command.AbstractCommandParser;
import com.parser.command.CommandParserFactory;

/**
 * 本地语义解析，主要实现方法是：正则表达式匹配
 * 
 * @author Peter
 * @data 2015年4月10日下午2:52:26
 */
public class NativeEngine extends AbstractEngine {
	public NativeEngine(Context context) {
		super(context);
	}
	@Override
	protected CommandInfo parse(String text, RequestParams params) {
		LogManager.i("NativeEngine", "CommandInfo --- parse:" + text);
		String sessionType = params.getParam(RequestParams.PARAM_SESSION_TYPE);
		if (sessionType != null && sessionType.equals(Platform.SESSION_TYPE_REMOTE)) {
			return null;
		}
		AbstractCommandParser parser = CommandParserFactory.makeParser(text);
		setGlobalObject(KeyManager.GKEY_OBJ_LAST_PASER, parser);
		if (parser != null) {
			CommandInfo info = parser.parser();

			if (info != null) {
				info._appId = params.getParam(RequestParams.PARAM_APP_ID);
				String temp = params.getParam(RequestParams.PARAM_SESSION_ID);
				if (temp != null && !temp.equals("")) {
					info._sessionId = Integer.parseInt(temp);
				}
				info._isFromNet = false;
			}
			// 离线线识别
			ITTSController mTTSController = (ITTSController) this.getGlobalObject(KeyList.GKEY_TTS_CONTORLLER);
			if (KeyList.IS_TTS_DEBUG) {
				mTTSController.syncPlay("正在使用离线语义识别");
			}
			return info;
		}
		return null;
	}


}