package com.base.util;

import org.json.JSONObject;

import android.content.Context;

import com.base.data.CommandInfo;
import com.iii360.base.common.utl.BaseContext;
import com.iii360.base.common.utl.IGloableHeap;
import com.iii360.base.common.utl.KeyList;
import com.iii360.base.inf.ITTSController;
import com.iii360.sup.common.utl.HomeConstants;
import com.iii360.sup.common.utl.LogManager;
import com.iii360.sup.common.utl.SystemUtil;
import com.iii360.sup.common.utl.net.HttpRequest;
import com.iii360.sup.common.utl.net.HttpRequest.IHttpResultListener;
import com.parser.command.AbstractCommandParser;
import com.parser.iengine.RequestParams;

public class AmazingBoxExcute {
	// 这是语音控制接口，第一个参数是sn，第二个参数是mac地址，第三个参数是语音识别结果
	// private String
	// ABOX_URL="http://a-box.com.cn/abox/index.php/business/api/smartControlByVoice/4011000900000550/40f02fdb5f7d/dddddddd";
	private String ABOX_URL = "http://a-box.com.cn/abox/index.php/business/api/smartControlByVoice/";
	private HttpRequest mHttpRequest;
	private Context mContext;
	private BaseContext mBaseContext;

	public AmazingBoxExcute(Context context) {
		mContext = context;
		mHttpRequest = new HttpRequest();
		mBaseContext = new BaseContext(context);
	}

	private boolean isExcute = false;
	private boolean isNeedWait = true;

	public CommandInfo request(String text, RequestParams params) {
		LogManager.i("amazing text=" + text);
		isExcute = false;
		isNeedWait = true;

		

		ABOX_URL = "http://a-box.com.cn/abox/index.php/business/api/smartControlByVoice/";

		CommandInfo info = new CommandInfo();

		mHttpRequest.setHttpListener(new IHttpResultListener() {
			@Override
			public void onResult(String res) {
				// TODO Auto-generated method stub
				LogManager.i("res=" + res);

				if (res != null) {
					try {
						JSONObject obj = new JSONObject(res);
						String data = obj.getString("data");
						// {"data":"OK"}
						if (data.equalsIgnoreCase("OK")) {
							isExcute = true;
						} else {
							isExcute = false;
						}

						isNeedWait = false;
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						isExcute = false;
						isNeedWait = false;
					}

				} else {
					isExcute = false;
					isNeedWait = false;
				}
			}
		});

		String sn = mBaseContext.getPrefString(HomeConstants.ABOX_SN_KEY);
		String mac = SystemUtil.getLocalMacAddress(mContext);
		mac = mac.replaceAll(":", "");
		ABOX_URL = ABOX_URL + sn + "/" + mac + "/" + text;
		mHttpRequest.doGet(ABOX_URL);

		long start = System.currentTimeMillis();

		// 等待执行结果
		while (((System.currentTimeMillis() - start) < 2000) && !isExcute && isNeedWait) {
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		if (isExcute) {
			info._question = text;
			info._answer = "amazing box执行成功";
			info._appId = params.getParam(RequestParams.PARAM_APP_ID);
			info._commandName = AbstractCommandParser.COMMAND_NAME_HANDLE_ERR;
			if (params.getParam(RequestParams.PARAM_SESSION_ID) != null && !params.getParam(RequestParams.PARAM_SESSION_ID).equals("")) {
				info._sessionId = Integer.valueOf(params.getParam(RequestParams.PARAM_SESSION_ID));
			}
			info._isFromNet = true;
		} else {
			info = null;
		}

		// 在线识别
		if (KeyList.IS_TTS_DEBUG) {
			KeyList.SEMANTEME_RECOGNIZER_BEGIN = System.currentTimeMillis();
			IGloableHeap gloableHeap = ((IGloableHeap) mContext.getApplicationContext());
			ITTSController mITTSController = (ITTSController) gloableHeap.getGlobalObjectMap().get(KeyList.GKEY_TTS_CONTORLLER);
			KeyList.SEMANTEME_RECOGNIZER_FINISH = System.currentTimeMillis();
			mITTSController.syncPlay("正在使用在线语义识别");
			mITTSController.syncPlay("耗时为" + (KeyList.SEMANTEME_RECOGNIZER_FINISH - KeyList.SEMANTEME_RECOGNIZER_BEGIN) + "毫秒");
		}
		return info;

	}

}
