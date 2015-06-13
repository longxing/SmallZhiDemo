package com.base.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import android.content.Context;

import com.base.data.CommandInfo;
import com.iii360.base.common.utl.BaseContext;
import com.iii360.base.common.utl.IGloableHeap;
import com.iii360.base.common.utl.KeyList;
import com.iii360.base.inf.ITTSController;
import com.iii360.sup.common.utl.LogManager;
import com.iii360.sup.common.utl.SystemUtil;
import com.parser.command.AbstractCommandParser;
import com.parser.iengine.RequestParams;

public class HttpRequestInfo {
	private Context mContext;

	public HttpRequestInfo(Context context) {
		mContext = context;
	}

	public String mControl4Url = "http://voice.insona.cc/CommandTest.jsp";

	public CommandInfo request(String text, RequestParams params) {
		
		CommandInfo info = new CommandInfo();
		InputStream in = null;
		try {
			Map<String, String> postData = new HashMap<String, String>();
			postData.put("mac", SystemUtil.getLocalMacAddress(mContext));
			postData.put("text", text);
			URL url = new URL(mControl4Url);
			in = submitPostData(postData, "utf-8", url);
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(in);
			NodeList links = doc.getElementsByTagName("flag");
			Element linkLine = (Element) links.item(0);
			String flag = linkLine.getAttributes().getNamedItem("name").getNodeValue();
			String data = linkLine.getElementsByTagName("data").item(0).getAttributes().getNamedItem("name").getNodeValue();
			if ("0".equals(flag) || "1".equals(flag)) {
				info._question = text;
				info._answer = data;
				info._appId = params.getParam(RequestParams.PARAM_APP_ID);
				info._commandName = AbstractCommandParser.COMMAND_NAME_HANDLE_ERR;
				if (params.getParam(RequestParams.PARAM_SESSION_ID) != null && !params.getParam(RequestParams.PARAM_SESSION_ID).equals("")) {
					info._sessionId = Integer.valueOf(params.getParam(RequestParams.PARAM_SESSION_ID));
				}
				info._isFromNet = true;
			} else {
				return null;
			}

		} catch (Exception e) {
			// TODO: handle exception
			LogManager.printStackTrace(e);
			return null;
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
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

	public static InputStream submitPostData(Map<String, String> params, String encode, URL url) {
		byte[] data = getRequestData(params, encode).toString().getBytes();
		try {
			HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
			httpURLConnection.setConnectTimeout(3000);
			httpURLConnection.setDoInput(true);
			httpURLConnection.setDoOutput(true);
			httpURLConnection.setRequestMethod("POST");
			httpURLConnection.setUseCaches(false);
			httpURLConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			httpURLConnection.setRequestProperty("Content-Length", String.valueOf(data.length));
			OutputStream outputStream = httpURLConnection.getOutputStream();
			outputStream.write(data);
			int response = httpURLConnection.getResponseCode();
			if (response == HttpURLConnection.HTTP_OK) {
				InputStream inptStream = httpURLConnection.getInputStream();
				return inptStream;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static StringBuffer getRequestData(Map<String, String> params, String encode) {
		StringBuffer stringBuffer = new StringBuffer();
		try {
			for (Map.Entry<String, String> entry : params.entrySet()) {
				stringBuffer.append(entry.getKey()).append("=").append(URLEncoder.encode(entry.getValue(), encode)).append("&");
			}
			stringBuffer.deleteCharAt(stringBuffer.length() - 1);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return stringBuffer;
	}

}
