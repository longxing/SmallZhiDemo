package com.parser.iengine;

import java.util.HashMap;

public class RequestParams {

	public final static String PARAM_QUESTION = "question";
	public final static String PARAM_APP_ID = "app_id";
	public final static String PARAM_ROBOT_ID = "robot_id";
	public final static String PARAM_SESSION_TYPE = "session_type";
	public final static String PARAM_SESSION_ID = "session_id";

	public HashMap<String, String> mParams = new HashMap<String, String>();

	RequestParams(String src) {
		if (src != null) {
			initValue(src);
		}
	}

	String getId() {
		return getParam(PARAM_APP_ID) + "," + getParam(PARAM_ROBOT_ID);
	}

	public String getParam(String key) {
		return mParams.get(key);
	}

	private void initValue(String src) {
		String[] params = src.split("\\&");

		for (String param : params) {
			String[] temp = param.split("\\=");
			if (temp.length > 1) {
				mParams.put(temp[0], temp[1]);
			}
		}
	}
}
