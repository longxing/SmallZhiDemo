package com.base.util;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;

import com.iii360.base.common.utl.BaseContext;
import com.iii360.sup.common.utl.HomeConstants;
import com.iii360.sup.common.utl.LogManager;
import com.iii360.sup.common.utl.SystemUtil;
import com.iii360.sup.common.utl.net.HttpRequest;
import com.iii360.sup.common.utl.net.HttpRequest.IHttpResultListener;

/**
 * 搜索abox
 * 
 * @author river
 * 
 * @date 2014-11-17
 */
public class AmazingBoxThread extends Thread {
	private boolean flag = true;
	private HttpRequest mHttpRequest;
	public static String ABOX_FIND_URL = "http://a-box.com.cn/abox/index.php/business/api/discovery/";

	private BaseContext mBaseContext = null;

	public AmazingBoxThread(final Context context) {
		// TODO Auto-generated constructor stub
		mBaseContext = new BaseContext(context);
		mHttpRequest = new HttpRequest();
		mHttpRequest.setHttpListener(new IHttpResultListener() {
			@Override
			public void onResult(String res) {
				// TODO Auto-generated method stub
				LogManager.e("find abox=" + res);

				if (res != null) {
					try {
						parserJson(res);

						mBaseContext.setPrefString(HomeConstants.ABOX_SN_KEY, sn);
						mBaseContext.setPrefBoolean(HomeConstants.ABOX_CONNECT, true);

						if (mBaseContext.getPrefBoolean(HomeConstants.ABOX_CONNECT_TTS, true)) {
							Intent intent = new Intent(HomeConstants.AKEY_TTS_PLAY);
							intent.putExtra(HomeConstants.TTS_PLAY_CONTENT, "连接amazing box成功");
							context.sendBroadcast(intent);
							mBaseContext.setPrefBoolean(HomeConstants.ABOX_CONNECT_TTS, false);
						}

					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						mBaseContext.setPrefBoolean(HomeConstants.ABOX_CONNECT, false);
					}
				} else {
					mBaseContext.setPrefBoolean(HomeConstants.ABOX_CONNECT, false);
				}
			}
		});

		String mac = null;
		try {
			mac = SystemUtil.getLocalMacAddress(context);
			if (mac != null) {
				mac = mac.replace(":", "");
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ABOX_FIND_URL += mac;
	}

	String sn;

	private void parserJson(String json) throws JSONException {
		JSONObject obj = new JSONObject(json);
		JSONObject obj2 = obj.getJSONObject("data");
		JSONObject obj3 = obj2.getJSONObject("host");
		String brand = obj3.getString("brand");
		sn = obj3.getString("sn");

		LogManager.i("brand=" + brand + "||sn=" + sn);
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		super.run();
		LogManager.e("start abox server ....");

		while (flag) {

			LogManager.i("abox find 1 mintues/once=" + ABOX_FIND_URL);
			mHttpRequest.doGet(ABOX_FIND_URL);
			try {
				Thread.sleep(1000 * 60);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	public void stopScanner() {
		flag = false;
	}
}
