package com.base.network;

import java.io.IOException;
import java.io.InputStreamReader;

import org.xmlpull.v1.XmlPullParser;

import android.util.Xml;

import com.base.data.CommandInfo;
import com.iii360.base.common.utl.LogManager;
import com.iii360.sup.common.utl.NetWorkUtil;

public class XmlParserNew {

	public static interface OnReceivedListener {
		public void onReceived(CommandInfo info);
	};

	private CommandInfo mInfo = null;

	private XmlPullParser mXmlPullParser;

	private final static int PRASE_STATE_SUMMARY = 0;
	private final static int PRASE_STATE_URL = 1;
	private final static int PRASE_STATE_ARG = 2;
	private int mPraseState = PRASE_STATE_SUMMARY;

	private final static String TAG_CONTENT = "content";
	private final static String TAG_COMMAND = "command";
	private final static String TAG_ARG = "arg";
	private final static String REQUEST_ID = "request-id";
	private final static String ATTR_UK = "app-id";
	private final static String ATTR_PACKAGE = "app-id";

	private final static String ATTR_COMMAND_ID = "id";

	public XmlParserNew() {
		mXmlPullParser = Xml.newPullParser();
	}

	private void handleStartTag(String tag) {

		if (tag == null) {
			return;
		}
		if (tag.equalsIgnoreCase(TAG_CONTENT)) {

			String tempId = mXmlPullParser.getAttributeValue(null, REQUEST_ID);
			if (tempId != null && !tempId.trim().equals("") && !tempId.equals("null")) {
				mInfo._sessionId = Integer.parseInt(tempId);
			}

			String userKey = mXmlPullParser.getAttributeValue(null, ATTR_UK);
			if (userKey != null && !userKey.trim().equals("")) {
				mInfo._appId = userKey;
				mInfo._packageName = userKey;
			}

			String packageName = mXmlPullParser.getAttributeValue(null, ATTR_PACKAGE);
			if (packageName != null && !packageName.trim().equals("")) {
				mInfo._packageName = packageName;
			}

		} else if (tag.equalsIgnoreCase(TAG_COMMAND)) {
			mInfo._commandName = mXmlPullParser.getAttributeValue(null, ATTR_COMMAND_ID);
		} else if (tag.equalsIgnoreCase(TAG_ARG)) {
			mPraseState = PRASE_STATE_ARG;
		}
	}

	private void handText(String text) {

		if (text.equals("\n") || text.equals("\n\t")) {
			return;
		}
		switch (mPraseState) {
		case PRASE_STATE_SUMMARY:
			mInfo._answer = text;
			break;
		case PRASE_STATE_ARG:
			mInfo.addArg(text);
			break;
		}
	}

	private void process(String url) {
		url = url.replace("\n", "");
		LogManager.e("语音请求连接：" + url);
		InputStreamReader input = NetWorkUtil.getNetworkInputStreamReader(url);
		if (input != null) {
			try {
				mInfo = new CommandInfo();
				mXmlPullParser.setInput(input);
				int evtType = mXmlPullParser.getEventType();
				while (evtType != XmlPullParser.END_DOCUMENT) {
					switch (evtType) {

					case XmlPullParser.START_TAG:

						handleStartTag(mXmlPullParser.getName());
						break;
					case XmlPullParser.TEXT:

						handText(mXmlPullParser.getText());
						break;
					case XmlPullParser.END_TAG:
						// handleEndTag(mXmlPullParser.getName());
						break;
					}
					evtType = mXmlPullParser.next();
				}
			} catch (Exception e) {
				mInfo = null;
				LogManager.e("XmlParserNew 解析xml异常：" + e.toString());
			} finally {
				try {
					input.close();
				} catch (IOException e) {
					LogManager.printStackTrace(e);
				} catch (Exception e) {
					LogManager.printStackTrace(e);
				}
			}
		} else {
			mInfo = null;
			LogManager.e("XmlParserNew", "获取信息失败，返回对象：" + mInfo);
		}
	}

	public void parse(final String url) {
		process(url);
	}

	public CommandInfo getInfo() {
		if (mInfo != null) {
			mInfo.standardizing();
		}
		return mInfo;
	}
}
