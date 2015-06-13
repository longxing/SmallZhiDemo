package com.iii.client;

import java.net.Socket;
import java.util.List;
import java.util.logging.LogManager;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.iii.client.R;
import com.iii.wifi.dao.info.WifiControlInfo;
import com.iii.wifi.dao.info.WifiControlInfos;
import com.iii.wifi.dao.info.WifiJSONObjectInfo;
import com.iii.wifi.dao.info.WifiLedStatusInfos;
import com.iii.wifi.dao.info.WifiRemindInfos;
import com.iii.wifi.dao.manager.WifiCRUDForClient;
import com.iii.wifi.dao.manager.WifiCRUDForControl;
import com.iii.wifi.dao.manager.WifiCRUDForRemind;
import com.iii.wifi.dao.manager.WifiCreateAndParseSockObjectManager;
import com.iii.wifi.dao.manager.WifiCRUDForControl.ResultListener;
import com.iii.wifi.dao.manager.WifiCRUDForRemind.ResultForRemindListener;

public class MainActivity extends Activity {
	Handler mHandle = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			Log.i("jiangshenglan", msg.getData().getString("info"));
			Toast.makeText(MainActivity.this, msg.getData().getString("info"), Toast.LENGTH_LONG);
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		WifiCRUDForRemind remind = new WifiCRUDForRemind(getApplicationContext(), "192.168.20.75", 15678);
		remind.selectRemind(new ResultForRemindListener() {

			@Override
			public void onResult(String type, String errorCode, WifiRemindInfos infos) {
				// TODO Auto-generated method stub
				Log.e("123", " infos " + infos.getType() + "  " + infos.getRemindInfos().size());
			}
		});

		// WifiForCommonOprite wco = new WifiForCommonOprite(5678,
		// "192.168.20.48");
		// wco.playTTS("这是在测试tts~~~~这是在测试tts", new
		// ResultForWeatherTimeListener() {
		//
		// @Override
		// public void onResult(String type, String errorCode, String result) {
		// // TODO Auto-generated method stub
		//
		// }
		// });
		// WifiCRUDForUser wu = new WifiCRUDForUser(getApplicationContext(),
		// "192.168.20.170", 5678);
		// WifiUserInfo info = new WifiUserInfo();
		// info.setName("jushang_net2");
		// info.setPassWord("360iii---");
		// wu.updata(info, new ResultForUserListener() {
		//
		// @Override
		// public void onResult(String type, String errorCode, String userName,
		// String userPassWord) {
		// // TODO Auto-generated method stub
		// Log.e("123", errorCode + " " + userName + " " + userPassWord);
		// }
		// });
		//
		// wco.learnHF("123", new ResultForWeatherTimeListener() {
		//
		// @Override
		// public void onResult(String type, String errorCode, String result) {
		// // TODO Auto-generated method stub
		// Log.e("123", "result learnHF " + result + "  " + errorCode);
		// Log.e("123", "result learnHF " + result + "  " + errorCode);
		// Log.e("123", "result learnHF " + result + "  " + errorCode);
		// Log.e("123", "result learnHF " + result + "  " + errorCode);
		// Log.e("123", "result learnHF " + result + "  " + errorCode);
		// }
		// });
		// wco.getUnConfigedDevice(new ResultListener() {
		//
		// @Override
		// public void onResult(String type, String errorCode,
		// List<WifiDeviceInfo> info) {
		// // TODO Auto-generated method stub
		//
		// Log.e("123", "result getUnConfigedDevice " + info);
		// }
		// });

		//
		// WifiCreateAndParseSockObjectManager.ParseWifiUserInfos("{\"error\":\"0\",\"obj\":{\"type\":\"6\",\"wifiInfos\":[{\"roomid\":\"roomid2\"}]},\"type\":\"8\"}");
		// WifiCRUDForRoom room = new WifiCRUDForRoom(MainActivity.this,
		// "192.168.20.173", 5678);
		// WifiRoomInfo info = new WifiRoomInfo();
		// info.setRoomId("23");
		// info.setRoomName("test");
		// room.add(info,new ResultListener() {
		//
		// @Override
		// public void onResult(String type, String errorCode,
		// List<WifiRoomInfo> info) {
		// // TODO Auto-generated method stub
		// if (errorCode.equals("1")) {
		// Log.i("jiangshenglan", "add=======================begin");
		// for (WifiRoomInfo in : info) {
		// Log.i("jiangshenglan", in.toString());
		// }
		// Log.i("jiangshenglan", "add=======================end");
		// }
		// }
		//
		// });
		// room.seleteByRoomId("23",new ResultListener() {
		//
		// @Override
		// public void onResult(String type, String errorCode,
		// List<WifiRoomInfo> info) {
		// // TODO Auto-generated method stub
		// if (errorCode.equals("1")) {
		// Log.i("jiangshenglan", "add=======================begin");
		// for (WifiRoomInfo in : info) {
		// Log.i("jiangshenglan", in.toString());
		// }
		// Log.i("jiangshenglan", "add=======================end");
		// }
		// }
		//
		// });
		// WifiCRUDForUser user = new
		// WifiCRUDForUser(MainActivity.this,"192.168.20.173",5678);
		// WifiUserInfo info = new WifiUserInfo();
		// info.setName("jiangsehnglan1");
		// info.setPassWord("2323232321");
		// user.select(new ResultForUserListener() {
		//
		// @Override
		// public void onResult(String type, String errorCode,
		// String userName, String userPassWord) {
		// // TODO Auto-generated method stub
		//
		// }
		//
		// });
		// WifiCRUDForWeatherTime weatherTime = new
		// WifiCRUDForWeatherTime(MainActivity.this,"192.168.20.157",5678);
		// weatherTime.select(new ResultForWeatherTimeListener() {
		//
		// @Override
		// public void onResult(String type, String errorCode,
		// String weatherTime) {
		// // TODO Auto-generated method stub
		//
		// }
		//
		// });
		// WifiCRUDForWeatherStatus weather = new
		// WifiCRUDForWeatherStatus(MainActivity.this,"192.168.20.173",5678);
		// weather.select(new ResultForWeatherListener() {
		//
		// @Override
		// public void onResult(String type, String errorCode, String ledName) {
		// // TODO Auto-generated method stub
		//
		// }
		//
		// });
		// WifiCRUDForTTS tts = new
		// WifiCRUDForTTS(MainActivity.this,"192.168.20.173",5678);
		// tts.select(new ResultForTTSListener() {
		//
		// @Override
		// public void onResult(String type, String errorCode, String ttsName) {
		// // TODO Auto-generated method stub
		// if (errorCode.equals("1")) {
		// Log.i("jiangshenglan", "add=======================begin");
		// Log.i("jiangshenglan", ttsName);
		// Log.i("jiangshenglan", "add=======================end");
		// }
		// }
		//
		// });
		// WifiCRUDForPosition position = new
		// WifiCRUDForPosition(MainActivity.this,"192.168.20.173",5678);
		// position.select(new ResultForPositionListener() {
		//
		// @Override
		// public void onResult(String type, String errorCode, String name) {
		// // TODO Auto-generated method stub
		// if (errorCode.equals("1")) {
		// Log.i("jiangshenglan", "add=======================begin");
		// Log.i("jiangshenglan", name);
		// Log.i("jiangshenglan", "add=======================end");
		// }
		// }
		//
		// });
		// WifiCRUDForLedTime ledTime = new
		// WifiCRUDForLedTime(MainActivity.this,"192.168.20.173",5678);
		// ledTime.selete(new ResultForLedTimeListener() {
		//
		// @Override
		// public void onResult(String type, String errorCode, String ledName) {
		// // TODO Auto-generated method stub
		// if (errorCode.equals("1")) {
		// Log.i("jiangshenglan", "add=======================begin");
		// Log.i("jiangshenglan", ledName);
		// Log.i("jiangshenglan", "add=======================end");
		// }
		// }
		//
		// });
		// WifiCRUDForLedStatus led = new
		// WifiCRUDForLedStatus(MainActivity.this,"192.168.20.173",5678);
		// led.select(new ResultForLedListener() {
		//
		// @Override
		// public void onResult(String type, String errorCode, String ledName) {
		// // TODO Auto-generated method stub
		// if (errorCode.equals("1")) {
		// Log.i("jiangshenglan", "add=======================begin");
		// Log.i("jiangshenglan", ledName);
		// Log.i("jiangshenglan", "add=======================end");
		// }
		// }
		//
		// });
		// WifiCRUDForRoom room = new
		// WifiCRUDForRoom(MainActivity.this,"192.168.20.173",5678);
		// WifiRoomInfo info = new WifiRoomInfo();
		// info.setId(2);
		// info.setRoomId("roomid4");
		// info.setRoomName("roomname4");
		// room.add(info, new ResultListener() {
		//
		// @Override
		// public void onResult(String type, String errorCode,
		// List<WifiRoomInfo> info) {
		// // TODO Auto-generated method stub
		// if (errorCode.equals("1")) {
		// Log.i("jiangshenglan", "add=======================begin");
		// for (WifiRoomInfo in : info) {
		// Log.i("jiangshenglan", in.toString());
		// }
		// Log.i("jiangshenglan", "add=======================end");
		// }
		// }
		//
		// });
		// room.seleteAll(new ResultListener() {
		//
		// @Override
		// public void onResult(String type, String errorCode,
		// List<WifiRoomInfo> info) {
		// // TODO Auto-generated method stub
		// if (errorCode.equals("1")) {
		// Log.i("jiangshenglan", "add=======================begin");
		// for (WifiRoomInfo in : info) {
		// Log.i("jiangshenglan", in.toString());
		// }
		// Log.i("jiangshenglan", "add=======================end");
		// }
		// }
		//
		// });
		// WifiCRUDForDevice device = new
		// WifiCRUDForDevice(MainActivity.this,"192.168.20.177",5678);
		// WifiDeviceInfo info = new WifiDeviceInfo();
		// info.setId(3);
		// info.setMacadd("macadd2");
		// info.setDeviceid("deviceid2");
		// info.setRoomid("roomid2");
		// info.setDeviceName("DeviceName2");
		// info.setFitting("fitting2");
		// device.add(info, new ResultListener() {
		//
		// @Override
		// public void onResult(String type, String errorCode,
		// List<WifiDeviceInfo> info) {
		// // TODO Auto-generated method stub
		// if (errorCode.equals("1")) {
		// Log.i("jiangshenglan", "add=======================begin");
		// for (WifiDeviceInfo in : info) {
		// Log.i("jiangshenglan", in.toString());
		// }
		// Log.i("jiangshenglan", "add=======================end");
		// }
		// }
		//
		// });
		// device.seleteByRoomId("roomid2",new ResultListener() {
		//
		// @Override
		// public void onResult(String type, String errorCode,
		// List<WifiDeviceInfo> info) {
		// // TODO Auto-generated method stub
		// if (errorCode.equals("1")) {
		// Log.i("jiangshenglan", "add=======================begin");
		// for (WifiDeviceInfo in : info) {
		// Log.i("jiangshenglan", in.toString());
		// }
		// Log.i("jiangshenglan", "add=======================end");
		// }
		// }
		//
		// });
		final WifiCRUDForControl control = new WifiCRUDForControl(MainActivity.this, "192.168.20.95", 5678);
		WifiControlInfo info = new WifiControlInfo();
		info.setId(2);
		info.setCorder("corder6");
		info.setDeviceid("deviceid6");
		info.setDorder("dorder6");
		info.setFrequency(100);
		info.setRoomId("roomname6");
		info.setAction("jiangshenglan5");
		control.add(info, new ResultListener() {

			@Override
			public void onResult(String type, String errorCode, List<WifiControlInfo> info) {
				// TODO Auto-generated method stub
				if (errorCode.equals("1")) {
					Log.i("jiangshenglan", "add=======================begin");
					for (WifiControlInfo in : info) {
						Log.i("jiangshenglan", in.toString());
					}
					Log.i("jiangshenglan", "add=======================end");
				}

			}

		});
		// control.seleteAll(new ResultListener() {
		//
		// @Override
		// public void onResult(String type, String errorCode,
		// List<WifiControlInfo> info) {
		// // TODO Auto-generated method stub
		// if (errorCode.equals("1")) {
		// Log.i("jiangshenglan", "add=======================begin");
		// for (WifiControlInfo in : info) {
		// Log.i("jiangshenglan", in.toString());
		// }
		// Log.i("jiangshenglan", "add=======================end");
		// }
		//
		// }
		//
		// });
		// control.seleteByDeviceId("deviceid6", new ResultListener() {
		//
		// @Override
		// public void onResult(String type, String errorCode,
		// List<WifiControlInfo> info) {
		// // TODO Auto-generated method stub
		// if (errorCode.equals("1")) {
		// Log.i("jiangshenglan", "add=======================begin");
		// for (WifiControlInfo in : info) {
		// Log.i("jiangshenglan", in.toString());
		// }
		// Log.i("jiangshenglan", "add=======================end");
		// }
		//
		// }
		//
		// });
		// WifiControlInfo info2 = new WifiControlInfo();
		// info.setCorder("corder2");
		// info.setDeviceid("deviceid2");
		// info.setDorder("dorder2");
		// info.setFrequency(5);
		// info.setRoomId("roomname2");
		// info.setAction("jiangshenglan2");
		// control.add(info2, new ResultListener() {
		//
		// @Override
		// public void onResult(String type, String errorCode,
		// List<WifiControlInfo> info) {
		// // TODO Auto-generated method stub
		// if (errorCode.equals("1")) {
		// Log.i("jiangshenglan", "add=======================begin");
		// for (WifiControlInfo in : info) {
		// Log.i("jiangshenglan", info.toString());
		// }
		// Log.i("jiangshenglan", "add=======================end");
		// }
		//
		// }
		//
		// });
		// control.seleteByRoomId("roomname7",new ResultListener() {
		//
		// @Override
		// public void onResult(String type, String errorCode,
		// List<WifiControlInfo> info) {
		// // TODO Auto-generated method stub
		// if (errorCode.equals("1")) {
		// Log.i("jiangshenglan", "selectByRoomId=======================begin");
		// for (WifiControlInfo in : info) {
		// Log.i("jiangshenglan", info.toString());
		// }
		// Log.i("jiangshenglan", "selectByRoomId=======================end");
		// }
		//
		// }
		//
		// });
		// control.seleteByDeviceId("deviceid",new ResultListener() {
		//
		// @Override
		// public void onResult(String type, String errorCode,
		// List<WifiControlInfo> info) {
		// // TODO Auto-generated method stub
		// if (errorCode.equals("1")) {
		// Log.i("jiangshenglan",
		// "seleteByDeviceId=======================begin");
		// for (WifiControlInfo in : info) {
		// Log.i("jiangshenglan", info.toString());
		// }
		// Log.i("jiangshenglan", "seleteByDeviceId=======================end");
		// }
		//
		// }
		//
		// });
		// control.seleteAll(new ResultListener() {
		//
		// @Override
		// public void onResult(String type, String errorCode,
		// List<WifiControlInfo> info) {
		// // TODO Auto-generated method stub
		// if (errorCode.equals("1")) {
		// Log.i("jiangshenglan", "seleteAll=======================begin");
		// for (WifiControlInfo in : info) {
		// Log.i("jiangshenglan", in.toString());
		// }
		// Log.i("jiangshenglan", "seleteAll=======================end");
		// }
		//
		// }
		//
		// });
		// info.setId(2);
		// info.setAction("jiangshenglan3");
		// control.updata(info, new ResultListener() {
		//
		// @Override
		// public void onResult(String type, String errorCode,
		// List<WifiControlInfo> info) {
		// // TODO Auto-generated method stub
		// if (errorCode.equals("1")) {
		// Log.i("jiangshenglan", "updata=======================begin");
		// for (WifiControlInfo in : info) {
		// Log.i("jiangshenglan", info.toString());
		// }
		// Log.i("jiangshenglan", "updata=======================end");
		// }
		//
		// }
		//
		// });
		// control.seleteAll(new ResultListener() {
		//
		// @Override
		// public void onResult(String type, String errorCode,
		// List<WifiControlInfo> info) {
		// // TODO Auto-generated method stub
		// if (errorCode.equals("1")) {
		// Log.i("jiangshenglan", "seleteAll=======================begin");
		// for (WifiControlInfo in : info) {
		// Log.i("jiangshenglan", info.toString());
		// }
		// Log.i("jiangshenglan", "seleteAll=======================end");
		// control.delete(info.get(0).getId(), new ResultListener() {
		//
		// @Override
		// public void onResult(String type, String errorCode,
		// List<WifiControlInfo> info) {
		// // TODO Auto-generated method stub
		// if (errorCode.equals("1")) {
		// Log.i("jiangshenglan", "delete=======================begin");
		// for (WifiControlInfo in : info) {
		// Log.i("jiangshenglan", info.toString());
		// }
		// Log.i("jiangshenglan", "delete=======================end");
		// }
		//
		// }
		//
		// });
		// }
		//
		// }
		//
		// });
		// control.seleteAll(new ResultListener() {
		//
		// @Override
		// public void onResult(String type, String errorCode,
		// List<WifiControlInfo> info) {
		// // TODO Auto-generated method stub
		// if (errorCode.equals("1")) {
		// Log.i("jiangshenglan", "seleteAll=======================begin");
		// for (WifiControlInfo in : info) {
		// Log.i("jiangshenglan", info.toString());
		// }
		// Log.i("jiangshenglan", "seleteAll=======================end");
		// }
		//
		// }
		//
		// });
		// new Thread(new Runnable() {
		//
		// @Override
		// public void run() {
		// // TODO Auto-generated method stub
		// while (true) {
		// try {
		// Socket socket = new Socket();
		// if (!socket.isConnected()) {
		// // socket.
		// socket.connect(new InetSocketAddress(
		// "192.168.20.161", 5678), 5000);
		// }
		// // if (!socket.isOutputShutdown()) {
		// OutputStream out = socket.getOutputStream();
		// // new WifiCRUDForLed(out).add("jiangshenglan");
		// // new WifiCRUDForLed(out).updata("jiangsheng");
		// // new WifiCRUDForLed(out).selete();
		// // WifiControlInfo info = new WifiControlInfo();
		// // info.setInfoId("123456");
		// // info.setCorder("corder");
		// // info.setDeviceid("deviceid");
		// // info.setDorder("dorder");
		// // info.setFrequency("3");
		// // info.setMacadd("12:51:21:54");
		// // info.setRoomname("roomname");
		// // new WifiCRUDForControl(out).add(info);
		// // new WifiCRUDForControl(MainActivity.this,socket,new
		// com.iii.wifi.dao.manager.WifiCRUDForControl.ResultListener() {
		// //
		// // @Override
		// // public void onResult(String type, String errorCode,
		// // List<WifiControlInfo> info) {
		// // // TODO Auto-generated method stub
		// // Log.i("jiangshenglan", "type = " + type + ";errorCode = " +
		// errorCode);
		// // if (errorCode.equals("1")) {
		// // Log.i("jiangshenglan", info.get(0).toString());
		// // }
		// // }
		// //
		// // }).seleteByInfoId("123456");
		// new WifiCRUDForLed(MainActivity.this,socket,new
		// ResultForLedListener() {
		//
		// @Override
		// public void onResult(String type, String errorCode,
		// String ledName) {
		// // TODO Auto-generated method stub
		// Log.i("jiangshenglan", "type = " + type + ";errorCode = " + errorCode
		// + "ledName = " + ledName);
		// }
		//
		// }).selete();
		// // out.write("jiangshenglanclient".getBytes("ISO-8859-1"));
		// // out.flush();
		// // }
		// // new ClientThread(socket).start();
		// // socket.close();
		// return;
		// } catch (Exception e) {
		// e.printStackTrace();
		// } finally {
		// try {
		// Thread.sleep(3000);
		// } catch (InterruptedException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		// }
		// }
		// }
		//
		// }).start();
	}

	class ClientThread extends Thread {
		Socket socket;

		public ClientThread(Socket socket) {
			this.socket = socket;
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			// while (true) {
			try {
				WifiJSONObjectInfo info = WifiCRUDForClient.findData(socket, MainActivity.this);
				String type = info.getType();
				if (type.equals(WifiCreateAndParseSockObjectManager.WIFI_INFO_TYPE_LED)) {
					Log.i("jiangshenglan", ((WifiLedStatusInfos) info.getObject()).getWifiInfo().get(0).getLedName());
				} else if (type.equals(WifiCreateAndParseSockObjectManager.WIFI_INFO_TYPE_CONTROL)) {
					Log.i("jiangshenglan", ((WifiControlInfos) info.getObject()).getWifiInfo().get(0).toString());
				}
				Log.i("jiangshenglan", info.getType() + " " + info.getError() + " " + info.getObject());
				// if (!socket.isConnected()) {
				// socket.connect(new InetSocketAddress("192.168.20.31",
				// 5678), 5000);
				// }
				// if (in == null) {
				// return;
				// }
				// if (!socket.isInputShutdown()) {
				// Log.i("jiangshenglan", "++++++++++");
				// int result = in.available();
				// while(result==0){
				// result = in.available();
				// }
				// Log.i("jiangshenglan", "------in.available() = " +
				// in.available());
				// byte[] data = new byte[result];
				// in.read(data);
				// String dataString = new String(data,"ISO-8859-1");
				// Bundle bundle = new Bundle();
				// bundle.putString("info", dataString);
				// Message msg = new Message();
				// msg.setData(bundle);
				// mHandle.sendMessage(msg);
				// // in.close();
				// // socket.close();
				// return;
				// }
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	// }

}
