package com.parser.iengine;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import android.content.Context;

import com.base.app.AppsManager;
import com.base.data.CommandInfo;
import com.iii360.sup.common.utl.LogManager;
import com.iii360.sup.common.utl.NetWorkUtil;
import com.iii360.sup.common.utl.SuperBaseContext;
import com.iii360.sup.common.utl.SystemUtil;
import com.voice.recognise.KeyList;

public abstract class AbstractEngine extends SuperBaseContext {

	private String mAdditionalParams = "";
	public static String REQ_HEAD = "http://hezi.360iii.net:48080/webapi/webservice/";

	AbstractEngine(Context context) {
		super(context);
		String head = AppsManager.getServerAddress(context);
		if (head != null && !head.trim().equals("")) {
			REQ_HEAD = head;
		}
	}

	void setAdditionalParams(String params) {
		mAdditionalParams = params;
	}

	private String getUrlHead() {
		return REQ_HEAD;
	}

	// ID20121122001 zhanglin end
	private String makeParams() {
		String params = "&option=phone";
		String imei = "&imei=" + SystemUtil.getDeviceId();
		String mac = "&macaddress=" + NetWorkUtil.getLocalMacAddress(getContext());
		params += imei;
		params += mac;
		params += "&app_id=" + AppsManager.getAppId(getContext());
		params += "&robot_id=" + AppsManager.getRobotId(getContext());
		params += mAdditionalParams;

		return params;
	}

	protected String makeUrl(String text) {
		/**
		 * add voice original text originaltalk = "原始语音结果"
		 */
		String utfString = "";
		try {
			String originalText = getGlobalString(KeyList.RECOR__VOICE_RECONGINISE_RESULT);
			utfString = "?talk=" + URLEncoder.encode(text, "utf-8");
			if (originalText != null && !originalText.equals("")) {
				utfString = utfString + "&originaltalk=" + URLEncoder.encode(originalText, "utf-8");
			}

		} catch (UnsupportedEncodingException e) {
			LogManager.printStackTrace(e);
		} catch (NullPointerException e) {
			LogManager.printStackTrace(e);
		}
		// ID20121106005 zhanglin end
		String url = getUrlHead() + utfString + makeParams();
		// LogManager.e(url);
		return url;
	}

	abstract CommandInfo parse(String text, RequestParams params);

}
