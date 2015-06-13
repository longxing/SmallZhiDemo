package com.parser.iengine;

import android.content.Context;

import com.base.data.CommandInfo;
import com.base.network.XmlParserNew;
import com.iii360.base.common.utl.IGloableHeap;
import com.iii360.base.inf.ITTSController;
import com.iii360.base.inf.recognise.ILightController;
import com.iii360.sup.common.utl.Animate;
import com.parser.command.AbstractCommandParser;
import com.voice.recognise.KeyList;

public class NewRemoteEngine extends AbstractEngine {
	// ID20121122001 zhanglin begin
	// move to abstractClass
	// ID20121122001 zhanglin begin

	// private XmlParserNew mXmlParser;

	public NewRemoteEngine(Context context) {
		super(context);
	}

	@Override
	protected CommandInfo parse(String text, RequestParams params) {
		// 灯动画
		Animate.post(new Runnable() {

			@Override
			public void run() {
				IGloableHeap gloableHeap = ((IGloableHeap) mContext.getApplicationContext());
				ILightController wakeupLightControl = (ILightController) gloableHeap.getGlobalObjectMap().get(KeyList.GKEY_WAKEUP_LIGHT_CONTROL);
				wakeupLightControl.reconiseStartAnimation();
			}

		});
		String sessionId = params.getParam(RequestParams.PARAM_SESSION_ID);
		String url = makeUrl(text);
		if (sessionId != null) {
			url += "&request_id=" + sessionId;
		}

		XmlParserNew xmlParser = new XmlParserNew();


		xmlParser.parse(url);
		
		
		
		CommandInfo info = xmlParser.getInfo();
		if (info != null) {
			info._question = text;
		} else {
			info = new CommandInfo();
			info._question = text;
			info._answer = "对不起，我没听懂。";
			info._appId = params.getParam(RequestParams.PARAM_APP_ID);
			info._commandName = AbstractCommandParser.COMMAND_NAME_HANDLE_ERR;
			if (sessionId != null && !sessionId.equals("")) {
				info._sessionId = Integer.valueOf(sessionId);
			}
		}
		info._isFromNet = true;

		
		// 在线识别
		if (KeyList.IS_TTS_DEBUG) {
			KeyList.SEMANTEME_RECOGNIZER_FINISH = System.currentTimeMillis();
			IGloableHeap gloableHeap = ((IGloableHeap) mContext.getApplicationContext());
			ITTSController mITTSController = (ITTSController) gloableHeap.getGlobalObjectMap().get(KeyList.GKEY_TTS_CONTORLLER);
			mITTSController.syncPlay("正在使用在线语义识别");
			mITTSController.syncPlay("耗时为" + (KeyList.SEMANTEME_RECOGNIZER_FINISH - KeyList.SEMANTEME_RECOGNIZER_BEGIN) + "毫秒");
		}
		// 灯动画结束
		Animate.post(new Runnable() {
			@Override
			public void run() {
				IGloableHeap gloableHeap = ((IGloableHeap) mContext.getApplicationContext());
				ILightController wakeupLightControl = (ILightController) gloableHeap.getGlobalObjectMap().get(KeyList.GKEY_WAKEUP_LIGHT_CONTROL);
				wakeupLightControl.reconiseStopAnimation();
			}
		});
		return info;
	}

}
