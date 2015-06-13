package com.iii.wifi.thirdpart.huanteng;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Base64;

import com.iii.wifi.util.KeyList;
import com.iii360.sup.common.utl.LogManager;
import com.iii360.sup.common.utl.SuperBaseContext;

/***
 * 幻腾家电控制
 * 
 * @author Peter
 * @data 2015年5月11日下午5:57:30
 */
public class HuanTengDevice {

	/*************************************************************************/
	/************************** Members Variables ***************************/
	/*************************************************************************/

	private static final String TAG = "wifiServer HuanTengDevice";
	private static final String app_id = "ea4d3c206dd0b2093d1991d6d8fecb4a";
	private static final String app_secret = "382f5246abd72c8b0b668ea719a7ae69";
	private static String loginUrl = "http://huantengsmart.com:80/api/user.json";
	private static final String getHuanTengTokenUrl = "http://huantengsmart.com:80/api/tokens.json";
	private static String searchDevicesUrl = "http://huantengsmart.com:80/api/bulbs.json";
	private static String updateOAuth2TokenUrl = "http://huantengsmart.com:80/api/tokens/refresh.json";

	private static String webSocketPushProtocolUrl = "ws://huantengsmart.com:6002";

	public static List<HuanTengInfo> bulbsOnlines = new ArrayList<HuanTengInfo>();
	public static List<HuanTengInfo> AllBulbs = new ArrayList<HuanTengInfo>();
	private String singleBasicToken = null;
	private List<HuanTengOAuth2Token> oauth2TokenList = new ArrayList<HuanTengOAuth2Token>();
	private Context mcontext = null;
	private SuperBaseContext mPreferenceUtil = null;
	private String result = null;
	private static HuanTengDevice huanTengDevice = null;

	private static HuanTengWebSocketReceiver wsReceiver = null;

	private boolean isRequestHuanTengDeviceStatus = false;

	/****
	 * get HuanTengDevice Instance
	 * 
	 * @param mcontent
	 * @return
	 */

	public static HuanTengDevice getHuanTengDeviceInstance(Context mcontent) {
		if (huanTengDevice == null) {
			huanTengDevice = new HuanTengDevice(mcontent);
		}
		return huanTengDevice;
	}

	/***
	 * Creates a new HuanTengDevice
	 * 
	 * @param context
	 */
	private HuanTengDevice(Context context) {
		mcontext = context;
		mPreferenceUtil = new SuperBaseContext(context);
		singleBasicToken = getBasicToken();
		getDeprecatedTokenJsonAndParser();
		registAccountChangeBroadCastReceiver();
		bulbsOnlines.clear();
		oauth2TokenList.clear();
		AllBulbs.clear();
	}

	private void createWSConnection(String authTokenJson) {
		wsReceiver = HuanTengWebSocketReceiver.getWSReceiverInstance();
		wsReceiver.setDeprecatedToken(authTokenJson);
		wsReceiver.createWSConnection(webSocketPushProtocolUrl);

	}

	/**
	 * 在线程中获取DeprecatedToken 用于WSConnection连接认证
	 * 
	 * “deprecated_token” 是登陆成功后，返回json信息的一个字段
	 * 
	 * @param startWSConenctionStatus
	 */
	private void getDeprecatedTokenJsonAndParser() {
		new Thread() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				super.run();
				if (getBasicToken() == null) {
					return;
				}
				String authTokenJson = getDeprecatedTokenOnLogin(getBasicToken());
				String token = null;
				if (authTokenJson != null) {
					try {
						JSONObject obj = new JSONObject(authTokenJson);
						token = obj.getString("deprecated_token");
						createWSConnection(token);
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			}
		}.start();
	}

	/**
	 * 注册助手端账号登陆广播
	 */
	private void registAccountChangeBroadCastReceiver() {
		IntentFilter filter = new IntentFilter(KeyList.PKEY_HUANTENG_ACCOUNT_CHANGE_ACTION);
		mcontext.registerReceiver(receiver, filter);
	}

	/**
	 * 注销助手端账号登陆广播 and stopWebSocketConnection
	 * 
	 */
	public void unRegistBroadCastReceiver() {
		LogManager.d("unRegistBroadCastReceiver:");
		mcontext.unregisterReceiver(receiver);
		if (wsReceiver != null) {
			wsReceiver.disWSConnection();
		}
		isRequestHuanTengDeviceStatus = false;
	}

	/**
	 * 创建助手端账号登陆广播接收器
	 */
	private BroadcastReceiver receiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			singleBasicToken = makeBasicToken();
			if (singleBasicToken == null) { // 解除绑定
				if (bulbsOnlines != null) {
					bulbsOnlines.clear();
				}
				isRequestHuanTengDeviceStatus = false;
			} else {
				isRequestHuanTengDeviceStatus = false;
				getDeprecatedTokenJsonAndParser();
			}

		}

	};

	/**
	 * @return 获得所有灯
	 */
	public List<HuanTengInfo> getAllBulbs() {
		if (getBasicToken() == null) {
			return AllBulbs;
		}
		if (AllBulbs.size() <= 0) {
			String json = getAllBulbsDevices(getBasicToken());
			LogManager.d(TAG, "use huanteng callback json:" + json);
			if (json != null) {
				try {
					JSONArray array = new JSONArray(json);
					HuanTengInfo info;
					for (int i = 0; i < array.length(); i++) {
						JSONObject obj = array.getJSONObject(i);
						String id = obj.getString("id");
						info = new HuanTengInfo();
						info.setId(id);
						info.setTurnedOn(obj.getString("turned_on"));
						info.setOwnDevice(obj.getString("own_device?"));
						info.setName(obj.getString("name"));
						AllBulbs.add(info);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		LogManager.d(TAG, "get huanteng devices count:" + AllBulbs.size());
		return AllBulbs;
	}

	/**
	 * 获取所有在线设备
	 * 
	 * @param listDevices
	 * @return
	 */
	public List<HuanTengInfo> getALLOnLineBulbs() {
		List<HuanTengInfo> listDevices = getAllBulbs();
		LogManager.d(TAG, "get huanteng online devices have a try===>>:" + isRequestHuanTengDeviceStatus);
		if (!isRequestHuanTengDeviceStatus) {
			for (int i = 0; i < listDevices.size(); i++) {
				if (isLineOnID(listDevices.get(i).getId())) {
					bulbsOnlines.add(listDevices.get(i));
				}
				isRequestHuanTengDeviceStatus = true;
			}
		}
		return bulbsOnlines;
	}

	/**
	 * 根据ID判断当前设备是否在线
	 * 
	 * @param id
	 * @return
	 */
	private boolean isLineOnID(String id) {
		String res = sendAGetRequest("http://huantengsmart.com:80/api/bulbs/" + id + ".json");
		LogManager.d(TAG, "get huanteng onlineStatus callback json:" + res);
		try {
			if (res == null) {
				return false;
			}
			JSONObject obj = new JSONObject(res);
			String connectivity = obj.getString("connectivity");
			LogManager.d(TAG, "according to club id get connectivity=" + connectivity);
			if ("在线".equals(connectivity)) {
				return true;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 发起一个post请求
	 * 
	 * @param requestUrl
	 * @return
	 */
	private String sendAPostRequest(String requestUrl) {
		String basicToken = getBasicToken();
		LogManager.d(TAG, "==>HuanTengDevice::sendAPostRequest()::basicToken:" +basicToken);
		if (basicToken == null) {
			return null;
		}
		String oauth2Token = getEffectiveOAuth2Token(getBasicToken());
		if (oauth2Token == null) {
			return null;
		} else {
			Map<String, Object> headers = new HashMap<String, Object>();
			headers.put("Authorization", "token " + oauth2Token);
			headers.put("Accept", "application/vnd.huantengsmart-v1+json");
			return executeHttpConnection(requestUrl, "POST", headers, null);
		}
	}

	/**
	 * 发起一个get请求
	 * 
	 * @param requestUrl
	 * @return
	 */
	private String sendAGetRequest(String requestUrl) {
		String basicToken = getBasicToken();
		LogManager.d(TAG, "send get request:====>>" + requestUrl + "basicToken:===>" + basicToken);
		if (basicToken == null) {
			return null;
		}
		String oauth2Token = getEffectiveOAuth2Token(getBasicToken());
		if (oauth2Token == null) {
			return null;
		} else {
			Map<String, Object> headers = new HashMap<String, Object>();
			headers.put("Authorization", "token " + oauth2Token);
			headers.put("Accept", "application/vnd.huantengsmart-v1+json");
			return executeHttpConnection(requestUrl, "GET", headers, null);
		}
	}

	/**
	 * {"success":true}
	 * 
	 * @param uid 灯的id
	 * @param isOff true表示打开灯
	 */
	public String controlOnOff(final String uid, final boolean isOff) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				LogManager.d(TAG, "control huanteng device begin  state =" + isOff);
				if (isOff) {
					result = sendAPostRequest("http://huantengsmart.com:80/api/bulbs/" + uid + "/switch_on.json");
				} else {
					result = sendAPostRequest("http://huantengsmart.com:80/api/bulbs/" + uid + "/switch_off.json");
				}
				LogManager.w(TAG, "==HuanTengDevice::controlOnOff()::control huanteng device end result =" + result+"action:"+isOff);
			}
		}).start();
		return result;
	}

	private String getBasicToken() {
		if (singleBasicToken == null) {
			singleBasicToken = makeBasicToken();
		}
		return singleBasicToken;
	}

	/**
	 * get username and password base HTTP Basic Authorization
	 * 
	 * @return
	 */
	public String makeBasicToken() {
		String mUserName = mPreferenceUtil.getPrefString(KeyList.PKEY_HUANTENG_USERNAME, "");
		String mPassword = mPreferenceUtil.getPrefString(KeyList.PKEY_HUANTENG_PASSWORD, "");
		String token = mUserName + ":" + mPassword;
		if (mUserName.equals("") || mPassword.equals("")) {
			LogManager.d(TAG, "your current account or password is null =====>>mUserName:" + mUserName + "===>>mPassword:" + mPassword);
			return null;
		}
		try {
			byte[] encoder = token.replaceAll(" ", "+").getBytes("UTF-8");
			token = new String(Base64.encode(encoder, 0, encoder.length, Base64.DEFAULT));
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		LogManager.d(TAG, "use account and password grnerate token:" + token);
		return "Basic " + token;
	}

	/**
	 * 根据Basic 认证的Token 换取OAuth2认证的Token
	 * 
	 * @param basicToken
	 * @return
	 */
	private String getEffectiveOAuth2Token(String basicToken) {
		if (oauth2TokenList.size() > 0) { // 当前令牌有效
			HuanTengOAuth2Token oauth2Token = oauth2TokenList.get(0);
			String lastUpdateTime = oauth2Token.getTimestamp();
			String effectivesTime = oauth2Token.getExpires_in();
			if (lastUpdateTime != null && effectivesTime != null && ((System.currentTimeMillis() - Long.valueOf(lastUpdateTime)) < Long.valueOf(effectivesTime))) {
				String  oauth2token= oauth2Token.getAccess_token();
				LogManager.d(TAG, "HuanTengDevice::getEffectiveOAuth2Token()::oauth2Token.getAccess_token()::" + oauth2token);
				return oauth2token;
			} else {
				String oauth2TokenToParse = getOAuth2TokenToParse(basicToken);
				LogManager.d(TAG, "HuanTengDevice::getEffectiveOAuth2Token()::getOAuth2TokenToParsen()::" + oauth2TokenToParse);
				return oauth2TokenToParse;
			}
		} else {
			String oauth2TokenToParse = getOAuth2TokenToParse(basicToken);
			LogManager.d(TAG, "HuanTengDevice::getEffectiveOAuth2Token()::getOAuth2TokenToParsen() else::" + oauth2TokenToParse);
			return oauth2TokenToParse;
		}
	}

	/**
	 * 更新令牌
	 * 
	 * @deprecated
	 * @param basicToken
	 * @return
	 */
	public String getUpdateOAuth2Token(String oauth2Token) {
		Map<String, Object> headers = new HashMap<String, Object>();
		headers.put("Authorization", oauth2Token);
		headers.put("Accept", "application/vnd.huantengsmart-v1+json");
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("app_id", app_id));
		params.add(new BasicNameValuePair("app_secret", app_secret));
		return executeHttpConnection(updateOAuth2TokenUrl, "PUT", headers, params);
	}

	/**
	 * 获取当前账号下所有的灯
	 * 
	 * @param basicToken 基本认证的Token
	 * @return String 返回灯设备的json信息
	 */
	private String getAllBulbsDevices(String basicToken) {

		Map<String, Object> headers = new HashMap<String, Object>();
		String effectiveToken = getEffectiveOAuth2Token(basicToken);
		headers.put("Authorization", "token " + effectiveToken);
		headers.put("Accept", "application/vnd.huantengsmart-v1+json");
		return executeHttpConnection(searchDevicesUrl, "GET", headers, null);
	}

	/**
	 * 解析"OAuth2_Token" 并将OAuth2Token信息保存到Map中
	 * 
	 * @param basicToken
	 * @return String OAuth2Token
	 */
	private String getOAuth2TokenToParse(String basicToken) {
		String tokenJson = getOAuth2Token(basicToken);
		String oauth2Token = null;
		oauth2TokenList.clear();
		if (tokenJson != null) {
			try {

				/**
				 * json格式 ：{"access_token":"42df236e343df87a2a10211abe65e1c6","token_type":"bearer",
				 * "refresh_token":"f48768a76a248c2d8c6e949c74ccf9ab","expires_in": 2678399,"timestamp":1431425831}
				 */
				JSONObject obj = new JSONObject(tokenJson);
				HuanTengOAuth2Token token = new HuanTengOAuth2Token();
				oauth2Token = obj.getString("access_token");
				token.setAccess_token(oauth2Token);
				token.setToken_type(obj.getString("token_type"));
				token.setRefresh_token(obj.getString("refresh_token"));
				token.setExpires_in(obj.getString("expires_in"));
				token.setTimestamp(String.valueOf(System.currentTimeMillis()));
				if (oauth2TokenList != null) {
					oauth2TokenList.add(token);
				}
			} catch (Exception e) {
				e.printStackTrace();
				return oauth2Token;
			}
		}
		return oauth2Token;
	}

	/**
	 * 获取"OAuth2_Token" 的json信息
	 * 
	 * @param basicToken 基本的验证token
	 * @return
	 */
	private static String getOAuth2Token(String basicToken) {
		Map<String, Object> headers = new HashMap<String, Object>();
		headers.put("Authorization", basicToken);
		headers.put("Accept", "application/vnd.huantengsmart-v1+json");
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("app_id", app_id));
		params.add(new BasicNameValuePair("app_secret", app_secret));
		return executeHttpConnection(getHuanTengTokenUrl, "POST", headers, params);
	}

	/**
	 * 获取"deprecated_token"
	 * 
	 * @param basicToken 基本的验证token
	 * @return
	 */
	private static String getDeprecatedTokenOnLogin(String basicToken) {
		Map<String, Object> headers = new HashMap<String, Object>();
		headers.put("Authorization", basicToken);
		headers.put("Accept", "application/vnd.huantengsmart-v1+json");
		return executeHttpConnection(loginUrl, "GET", headers, null);
	}

	/**
	 * 执行http请求返回结果
	 * 
	 * @param url 请求连接
	 * @param requestMethod 请求方式
	 * @param headers 请求头文件
	 * @param params 请求参数
	 * @return
	 */
	private static String executeHttpConnection(String url, String requestMethod, Map<String, Object> headers, List<NameValuePair> params) {
		URL requestUrl = null;
		HttpURLConnection conn = null;
		String callBackResult = null;
		try {
			requestUrl = new URL(url);
			conn = (HttpURLConnection) requestUrl.openConnection();
			conn.setRequestMethod(requestMethod);
			conn.setReadTimeout(10 * 1000);
			conn.setConnectTimeout(15 * 1000);
			conn.setDoInput(true);
			if (requestMethod.equals("POST")) {
				conn.setDoOutput(true);
			}
			if (headers != null && headers.size() > 0) {
				for (Map.Entry<String, Object> entry : headers.entrySet()) {
					conn.setRequestProperty(entry.getKey(), entry.getValue().toString());
				}
			}
			if (params != null && params.size() > 0) {
				OutputStream os = conn.getOutputStream();
				BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
				writer.write(getQuery(params));
				writer.flush();
				writer.close();
				os.close();
			}
			conn.connect();
			InputStream in = conn.getInputStream();
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			int count;
			byte[] buff = new byte[1024];
			while ((count = in.read(buff)) != -1) {
				baos.write(buff, 0, count);
			}
			baos.flush();
			byte[] data = baos.toByteArray();
			callBackResult = new String(data, "UTF-8");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (conn != null) {
				conn.disconnect();
				requestUrl = null;
				conn = null;
			}
		}
		return callBackResult;
	}

	private static String getQuery(List<NameValuePair> params) throws UnsupportedEncodingException {
		StringBuilder result = new StringBuilder();
		boolean first = true;
		for (NameValuePair pair : params) {
			if (first) {
				first = false;
			} else {
				result.append("&");
			}
			result.append(URLEncoder.encode(pair.getName(), "UTF-8"));
			result.append("=");
			result.append(URLEncoder.encode(pair.getValue(), "UTF-8"));
		}
		return result.toString();
	}

}
