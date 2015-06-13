package com.iii.wifi.thirdpart.huanteng;

import java.util.regex.Pattern;

import org.json.JSONException;
import org.json.JSONObject;

import com.iii360.sup.common.utl.LogManager;
import com.smallzhi.autobahn.WebSocket;
import com.smallzhi.autobahn.WebSocketConnection;
import com.smallzhi.autobahn.WebSocketException;

/**
 * 接收幻腾设备状态改变接口
 * 
 * @author Peter
 * @data 2015年5月13日下午4:40:25
 */
public class HuanTengWebSocketReceiver implements WebSocket.ConnectionHandler {

	private static final String TAG = "HuanTengWebSocketReceiver";

	private WebSocketConnection webSocketConnection = null;
	private String token = null;
	private static HuanTengWebSocketReceiver receiver = null;

	public static int HuanTeng_Bulbs_Status_OffLine = 0; // 离线
	public static int HuanTeng_Bulbs_Status_OnLine = 1; // 在线
	public static int HuanTeng_Bulbs_Status_Unknow = 2; // 状态未知

	private static Object obj = new Object();

	/**
	 * 获取单一实例的handler
	 * 
	 * @return
	 */
	public static HuanTengWebSocketReceiver getWSReceiverInstance() {
		synchronized (obj) {
			if (receiver == null) {
				receiver = new HuanTengWebSocketReceiver();
			}
			return receiver;
		}
	}

	/**
	 * 设置deprecatedToken,用于发送给Huanteng 服务验证ws连接
	 * 
	 * @param token
	 */
	public void setDeprecatedToken(String token) {
		this.token = token;
	}

	/**
	 * 创建链接，第一次链接失败，尝试重新连接
	 * 
	 * @param wsUrl
	 */
	public void createWSConnection(String wsUrl) {
		if (webSocketConnection == null) {
			webSocketConnection = new WebSocketConnection();
		}
		if (!webSocketConnection.isConnected()) {  
			try {
				webSocketConnection.connect(wsUrl, HuanTengWebSocketReceiver.this);
			} catch (WebSocketException e) {
				LogManager.e(TAG, "connection wsUrl:" + wsUrl + "fail====>>>" + e.toString());
				disWSConnection();
				LogManager.e(TAG, " try connection again wsUrl :" + wsUrl);
				webSocketConnection.reconnect();
			}
		}
	}

	/***
	 * 断开连接
	 */
	public void disWSConnection() {
		if (webSocketConnection != null && webSocketConnection.isConnected()) {
			webSocketConnection.disconnect();
		}
	}

	/**
	 * 创建链接成功
	 */

	@Override
	public void onOpen() {
		// TODO Auto-generated method stub
		String payload = String.format("{\"auth_token\":\"%s\"}", token);
		LogManager.d(TAG, "create connection success send auth_token to WebSocket===>>>payload:" + payload);
		webSocketConnection.sendTextMessage(payload);
	}

	@Override
	public void onClose(int code, String reason) {
		// TODO Auto-generated method stub
		LogManager.e(TAG, "onClose close connection code:" + code + "===>reason:" + reason);
	}

	/**
	 * 接收到推送消息
	 * 
	 * 上线消息：{"type":"DeviceConnectivity-v1-1410939854","content":{"device_identifier":"B3288","connectivity_string":"在线"}}
	 * 
	 * 离线消息：{"type":"DeviceConnectivity-v1-1410939854","content":{"device_identifier":"B3288","connectivity_string":"离线"}}
	 * 
	 * 
	 * 打开消息：{"type":"BulbsChanged-v2-1410939854","content":{"device_identifier":"B3288","bulb_id":3288,"wall_switch_id":null,
	 * "channel":null,"hue":0.62,"brightness":0.1,"turned_on":true,"script_end_time":null}}
	 *
	 * 关闭消息：{"type":"BulbsChanged-v2-1410939854","content":{"device_identifier":"B3288","bulb_id":3288,"wall_switch_id":null,
	 * "channel":null,"hue":0.62,"brightness":0.1,"turned_on":false,"script_end_time":null}}
	 * 
	 */
	@Override
	public void onTextMessage(String payload) {
		// TODO Auto-generated method stub
		LogManager.d(TAG, "onTextMessage  received msg:" + payload);
		synchronized (payload) {
			if (payload != null && !payload.equals("")) {

				try {
					JSONObject object = new JSONObject(payload);
					String type = object.getString("type");
					if (type != null && type.contains("DeviceConnectivity")) {
						String contentJson = object.getString("content");
						if (contentJson != null) {
							JSONObject objContent = new JSONObject(contentJson);
							Pattern p = Pattern.compile("[a-zA-Z]");
							String id = p.matcher(objContent.getString("device_identifier")).replaceAll("");
							String status = objContent.getString("connectivity_string");
							if (status.equals("在线")) {
								for (int i = 0; i < HuanTengDevice.AllBulbs.size(); i++) {
									if (HuanTengDevice.AllBulbs.get(i).getId().equals(id) && !HuanTengDevice.bulbsOnlines.contains(HuanTengDevice.AllBulbs.get(i))) {
										HuanTengDevice.bulbsOnlines.add(HuanTengDevice.AllBulbs.get(i));
									}
								}
							} else if (status.equals("离线")) {
								for (int i = 0; i < HuanTengDevice.bulbsOnlines.size(); i++) {
									if (HuanTengDevice.bulbsOnlines.get(i).getId().equals(id)) {
										HuanTengDevice.bulbsOnlines.remove(i);
									}
								}
							} else {
								for (int i = 0; i < HuanTengDevice.bulbsOnlines.size(); i++) {
									if (HuanTengDevice.bulbsOnlines.get(i).getId().equals(id)) {
										HuanTengDevice.bulbsOnlines.remove(i);
									}
								}
							}
						}
					}
				} catch (JSONException e) {
					LogManager.e(e.toString());
				}
			}

		}

	}

	@Override
	public void onRawTextMessage(byte[] payload) {
		// TODO Auto-generated method stub
		LogManager.d(TAG, "onRawTextMessage  received msg:" + new String(payload));
	}

	@Override
	public void onBinaryMessage(byte[] payload) {
		// TODO Auto-generated method stub
		LogManager.d(TAG, "onBinaryMessage  received msg:" + new String(payload));
	}

}
