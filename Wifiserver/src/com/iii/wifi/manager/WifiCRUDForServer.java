package com.iii.wifi.manager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.json.JSONException;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.iii.wifi.dao.imf.WifiConfigOperite;
import com.iii.wifi.dao.imf.WifiControlDao;
import com.iii.wifi.dao.imf.WifiDeviceDao;
import com.iii.wifi.dao.imf.WifiHuanTengDao;
import com.iii.wifi.dao.imf.WifiLedStateAndTimeDao;
import com.iii.wifi.dao.imf.WifiLedStatusDao;
import com.iii.wifi.dao.imf.WifiLedTimeDao;
import com.iii.wifi.dao.imf.WifiModeDao;
import com.iii.wifi.dao.imf.WifiPositionDao;
import com.iii.wifi.dao.imf.WifiRoomDao;
import com.iii.wifi.dao.imf.WifiTTSDao;
import com.iii.wifi.dao.imf.WifiUserDao;
import com.iii.wifi.dao.imf.WifiVolumeCotroller;
import com.iii.wifi.dao.imf.WifiWeatherStateAndTimeDao;
import com.iii.wifi.dao.imf.WifiWeatherStatusDao;
import com.iii.wifi.dao.imf.WifiWeatherTimeDao;
import com.iii.wifi.dao.info.HuanTengAccount;
import com.iii.wifi.dao.info.WifiBoxModeInfo;
import com.iii.wifi.dao.info.WifiBoxModeInfos;
import com.iii.wifi.dao.info.WifiConstants;
import com.iii.wifi.dao.info.WifiControlInfo;
import com.iii.wifi.dao.info.WifiControlInfos;
import com.iii.wifi.dao.info.WifiDeviceInfo;
import com.iii.wifi.dao.info.WifiDeviceInfos;
import com.iii.wifi.dao.info.WifiJSONObjectForLearnHF;
import com.iii.wifi.dao.info.WifiJSONObjectForLearnHFs;
import com.iii.wifi.dao.info.WifiJSONObjectInfo;
import com.iii.wifi.dao.info.WifiLedStatusInfo;
import com.iii.wifi.dao.info.WifiLedStatusInfos;
import com.iii.wifi.dao.info.WifiLedTimeInfo;
import com.iii.wifi.dao.info.WifiLedTimeInfos;
import com.iii.wifi.dao.info.WifiMyTag;
import com.iii.wifi.dao.info.WifiPositionInfo;
import com.iii.wifi.dao.info.WifiPositionInfos;
import com.iii.wifi.dao.info.WifiRemindInfos;
import com.iii.wifi.dao.info.WifiRoomInfo;
import com.iii.wifi.dao.info.WifiRoomInfos;
import com.iii.wifi.dao.info.WifiStringInfo;
import com.iii.wifi.dao.info.WifiStringInfos;
import com.iii.wifi.dao.info.WifiTTSVocalizationTypeInfo;
import com.iii.wifi.dao.info.WifiTTSVocalizationTypeInfos;
import com.iii.wifi.dao.info.WifiUpdateInfo;
import com.iii.wifi.dao.info.WifiUpdateInfos;
import com.iii.wifi.dao.info.WifiUserData;
import com.iii.wifi.dao.info.WifiUserInfo;
import com.iii.wifi.dao.info.WifiUserInfos;
import com.iii.wifi.dao.info.WifiVolume;
import com.iii.wifi.dao.info.WifiWeatherStatusInfo;
import com.iii.wifi.dao.info.WifiWeatherStatusInfos;
import com.iii.wifi.dao.info.WifiWeatherTimeInfo;
import com.iii.wifi.dao.info.WifiWeatherTimeInfos;
import com.iii.wifi.dao.inter.IWifiControlDao;
import com.iii.wifi.dao.inter.IWifiDeviceDao;
import com.iii.wifi.dao.inter.IWifiHuanTengDao;
import com.iii.wifi.dao.inter.IWifiLedStatusDao;
import com.iii.wifi.dao.inter.IWifiLedTimeDao;
import com.iii.wifi.dao.inter.IWifiPositionDao;
import com.iii.wifi.dao.inter.IWifiRoomDao;
import com.iii.wifi.dao.inter.IWifiTTSDao;
import com.iii.wifi.dao.inter.IWifiUserDao;
import com.iii.wifi.dao.inter.IWifiWeatherStatusDao;
import com.iii.wifi.dao.inter.IWifiWeatherTimeDao;
import com.iii.wifi.dao.manager.WifiCreateAndParseSockObjectManager;
import com.iii.wifi.dao.newmanager.AbsWifiCRUDForObject;
import com.iii.wifi.http.ParserMyTag;
import com.iii.wifi.http.WifiSetMyTag;
import com.iii.wifi.http.WifiSetUserData;
import com.iii.wifi.http.parsrer.ParserUserData;
import com.iii.wifi.manager.impl.AbsWifiResponse;
import com.iii.wifi.manager.impl.WifiResponseFactory;
import com.iii.wifi.util.AudioRecorder;
import com.iii.wifi.util.BasePreferences;
import com.iii.wifi.util.BoxModeEnumUtils;
import com.iii.wifi.util.KeyList;
import com.iii.wifi.util.SerialNumberUitls;
import com.iii.wifiserver.WeakupControl;
import com.iii360.sup.common.utl.LogManager;
import com.iii360.sup.common.utl.net.SupWifiConfig;
import com.voice.common.util.Remind;

/**
 * 助手端与盒子交互的关键类，相当于服务端：查询数据，控制盒子的入口 操作类型以后一律在AbsWifiCRUDForObject中添加
 * 
 * @author Peter
 * @data 2015年4月9日下午5:11:32
 */
public class WifiCRUDForServer {
	private static final String TAG = "WifiCRUDForServer";
	private Context mContext;
	private Socket mSocket;
	private Gson mGson;
	private boolean mRun = true;

	private WifiResponseFactory mWifiResponseFactory;
	private AbsWifiResponse mAbsWifiResponse;
	private BasePreferences mBasePreferences;

	public WifiCRUDForServer(Context context, Socket socket) {
		mContext = context;
		mSocket = socket;
		mGson = new Gson();
		mWifiResponseFactory = new WifiResponseFactory();
		mBasePreferences = new BasePreferences(context);
	}

	public void findData() {
		long start = System.currentTimeMillis();
		InputStream in;
		String gsonInfo = null;
		WifiJSONObjectInfo info = null;
		String result = null;
		try {
			mSocket.setSoLinger(true, 60);
			int port = mSocket.getLocalPort();
			in = mSocket.getInputStream();
			if (port == SupWifiConfig.TCP_DEFAULT_PORT) {
				int number = 0;
				while (number == 0) {
					number = in.available();
					Thread.sleep(50);
				}
				byte[] data = new byte[number];
				in.read(data);
				gsonInfo = URLDecoder.decode(new String(data), "utf-8");

			} else {
				BufferedReader br = new BufferedReader(new InputStreamReader(in));
				String dataString = br.readLine();
				gsonInfo = URLDecoder.decode(dataString, "utf-8");
			}
			LogManager.e("wifiserver接收的数据" + gsonInfo);
			info = WifiCreateAndParseSockObjectManager.ParseWifiUserInfos(gsonInfo);
			String type = info.getType();
			String operateTime = info.getOperateTime();

			mRun = true;

			// ========================old interface
			// start========================
			if (type.equals(WifiCreateAndParseSockObjectManager.WIFI_INFO_TYPE_CONTROL)) {
				result = getWifiControlData(info, mContext);
			} else if (type.equals(WifiCreateAndParseSockObjectManager.WIFI_INFO_TYPE_TTS)) {
				result = getWifiTTSData(info, mContext);
			} else if (type.equals(WifiCreateAndParseSockObjectManager.WIFI_INFO_TYPE_USERS)) { // 配置wifi账号、密码
				result = getWifiUserData(info, mContext);
			} else if (type.equals(WifiCreateAndParseSockObjectManager.WIFI_INFO_TYPE_LED)) {
				result = getWifiLedData(info, mContext);
			} else if (type.equals(WifiCreateAndParseSockObjectManager.WIFI_INFO_TYPE_LED_SLEEP_SET)) { // 灯休眠设置
				String lastOperateTime = mBasePreferences.getPrefString(KeyList.SEND_LED_SLEEPTIME_TIME, "0");
				if (Long.parseLong(operateTime) >= Long.parseLong(lastOperateTime)) {
					result = getWifiLedStateAndTimeData(info, mContext);
					LogManager.e("update set led sleep time: " + type + "--" + operateTime + "--" + result);
					mBasePreferences.setPrefString(KeyList.SEND_LED_SLEEPTIME_TIME, operateTime);
				} else {
					LogManager.e("忽略本次操作，操作时间：" + type + "--" + operateTime);
					return;
				}
			} else if (type.equals(WifiCreateAndParseSockObjectManager.WIFI_INFO_TYPE_WEATHER_REPORT)) { // 定时播报天气预报
				String lastOperateTime = mBasePreferences.getPrefString(KeyList.SEND_WEATHER_REPORT_TIME, "0");
				if (Long.parseLong(operateTime) >= Long.parseLong(lastOperateTime)) {
					result = getWifiWeatherStateAndTimeData(info, mContext);
					LogManager.e("update set timer weather report: " + type + "--" + operateTime + "--" + result);
					mBasePreferences.setPrefString(KeyList.SEND_WEATHER_REPORT_TIME, operateTime);
				} else {
					LogManager.e("忽略本次操作，操作时间：" + type + "--" + operateTime);
					return;
				}
			} else if (type.equals(WifiCreateAndParseSockObjectManager.WIFI_INFO_TYPE_LED_TIME)) { // 旧的LED设置接口
				result = getWifiLedTimeData(info, mContext);
			} else if (type.equals(WifiCreateAndParseSockObjectManager.WIFI_INFO_TYPE_POSITION)) {
				result = getWifiPositionData(info, mContext);
			} else if (type.equals(WifiCreateAndParseSockObjectManager.WIFI_INFO_TYPE_ROOM)) {
				result = getWifiRoomData(info, mContext);
			} else if (type.equals(WifiCreateAndParseSockObjectManager.WIFI_INFO_TYPE_DEVICE)) {
				result = getWifiDeviceData(info, mContext);
			} else if (type.equals(WifiCreateAndParseSockObjectManager.WIFI_INFO_TYPE_WEATHER)) {
				result = getWifiWeatherData(info, mContext);
			} else if (type.equals(WifiCreateAndParseSockObjectManager.WIFI_INFO_TYPE_WEATHER_TIME)) {
				result = getWifiWeatherTimeData(info, mContext);
			} else if (type.equals(WifiCreateAndParseSockObjectManager.WIFI_INFO_TYPE_COMMON_OPRITE)) {
				result = getCommonOperiteResponse(info, mContext);
			} else if (type.equals(WifiCreateAndParseSockObjectManager.WIFI_INFO_TYPE_REMIND)) {
				result = getRemindResponse(info, mContext);
			} else if (type.equals(WifiCreateAndParseSockObjectManager.WIFI_INFO_TYPE_HUANTENG)) {
				result = getHuanTengResponse(info, mContext, gsonInfo);
			} else if (type.equals(WifiCreateAndParseSockObjectManager.WIFI_INFO_TYPE_VOLUME)) {
				result = getVolumeResponse(info, mContext);
			} else if (type.equals(WifiCreateAndParseSockObjectManager.WIFI_INFO_TYPE_TTS_VOLUME)) {
				result = getTTSVolumeResponse(info, mContext);
			} else if (type.equals(WifiCreateAndParseSockObjectManager.WIFI_INFO_TYPE_MYTAG)) {
				mRun = false;
				result = getMyTagResponse(info, mContext, gsonInfo);

			} else if (type.equals(WifiCreateAndParseSockObjectManager.WIFI_INFO_TYPE_USER_DATA)) {
				mRun = false;
				result = getUserDataResponse(info, mContext, gsonInfo);

			} else if (type.equals(WifiCreateAndParseSockObjectManager.WIFI_INFO_TYPE_BOX_MODE)) {
				result = getBoxModeResponse(info, mContext);

				// } else if
				// (type.equals(WifiCreateAndParseSockObjectManager.WIFI_INFO_TYPE_MUSIC))
				// {
				// result = getMusicResponse(info, mContext);
			} else if (type.equals(WifiCreateAndParseSockObjectManager.WIFI_INFO_TYPE_UPDATE)) {
				result = getUpdateResponse(info, mContext);

			} else if (type.equals(WifiCreateAndParseSockObjectManager.WIFI_INFO_TYPE_STRING)) {
				result = getStringResponse(info, mContext);
				// ========================old interface
				// end========================

			} else if (!TextUtils.isEmpty(type)) {
				// ========================new interface
				// start========================
				// 最新接口标准,参考WifiResponseForBoxSystem
				LogManager.d(type + " receiver : " + mGson.toJson(info));

				// if
				// (type.equals(WifiCreateAndParseSockObjectManager.WIFI_INFO_TYPE_BOX_SYSTEM)
				// &&
				// !mBasePreferences.getPrefBoolean(KeyList.CURRENT_TCP_PORT_IS_NEW,
				// false)) {
				// // 提示助手升级
				// TTSUtil tts = new TTSUtil(mContext);
				// tts.playContent("您的小智助手版本过低，请重新扫描音箱底部二维码下载安装最新版本小智助手");
				// }

				mAbsWifiResponse = mWifiResponseFactory.createResponse(type);
				result = mAbsWifiResponse.getResponse(info, mContext);
				LogManager.d(type + " send : " + result);
			}
			// ========================new interface end========================

		} catch (Exception e) {
			// TODO Auto-generated catch block
			LogManager.e(Log.getStackTraceString(e));
		} finally {
			if (mRun) {
				responseToClient(result, info, gsonInfo);
			}
		}
		LogManager.e("time = " + (System.currentTimeMillis() - start));
	}

	private String getHuanTengResponse(WifiJSONObjectInfo obj, final Context mContext, final String jsoninfo) {
		IWifiHuanTengDao dao = new WifiHuanTengDao(mContext);
		HuanTengAccount info = (HuanTengAccount) obj.getObject();
		if (info.getType().equals(AbsWifiCRUDForObject.OPERATION_TYPE_SET)) {
			dao.set(info);
			return WifiCreateAndParseSockObjectManager.createWifiHuanTengInfos(WifiCreateAndParseSockObjectManager.WIFI_INFO_SUCCESS, info);
		} else if (info.getType().equals(AbsWifiCRUDForObject.OPERATION_TYPE_GET)) {
			HuanTengAccount info2 = dao.get();
			return WifiCreateAndParseSockObjectManager.createWifiHuanTengInfos(WifiCreateAndParseSockObjectManager.WIFI_INFO_SUCCESS, info2);
		} else if (info.getType().equals(AbsWifiCRUDForObject.OPERATION_TYPE_DELETE)) {
			dao.delete();
			return WifiCreateAndParseSockObjectManager.createWifiHuanTengInfos(WifiCreateAndParseSockObjectManager.WIFI_INFO_SUCCESS, info);
			// dao.delete(info.getId());
			// return
			// WifiCreateAndParseSockObjectManager.createWifiUserInfos(AbsWifiCRUDForObject.DB_SELECT,
			// WifiCreateAndParseSockObjectManager.WIFI_INFO_SUCCESS,dao.seleteAll());
		}
		return null;
	}

	private void responseToClient(String result, WifiJSONObjectInfo info, String gsonInfo) {
		if (result == null && info != null) {
			info.setError(WifiCreateAndParseSockObjectManager.WIFI_INFO_ERROR);
			Gson gson = new Gson();
			result = gson.toJson(info);
		} else if (result == null && info == null) {
			info = new WifiJSONObjectInfo();
			info.setError(WifiCreateAndParseSockObjectManager.WIFI_INFO_ERROR);
			Gson gson = new Gson();
			result = gson.toJson(info);
		}
		if (result != null) {
			LogManager.e("received request from phone before response:", result);
			OutputStream out;
			try {
				out = mSocket.getOutputStream();
				out.write(result.getBytes());
				out.flush();
				out.close();
				LogManager.e("received request from phone after response:" + gsonInfo + "result:" + result);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		try {
			mSocket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * @deprecated
	 */
	private String getWifiWeatherTimeData(WifiJSONObjectInfo obj, Context context) {
		// TODO Auto-generated method stub
		IWifiWeatherTimeDao dao = new WifiWeatherTimeDao(context);
		WifiWeatherTimeInfos infos = (WifiWeatherTimeInfos) obj.getObject();
		WifiWeatherTimeInfo info = infos.getWifiInfo().get(0);
		if (infos.getType().equals(AbsWifiCRUDForObject.DB_ADD)) {
			dao.add(info);
			return WifiCreateAndParseSockObjectManager.createWifiWeatherTimeInfos(AbsWifiCRUDForObject.DB_ADD, WifiCreateAndParseSockObjectManager.WIFI_INFO_SUCCESS, info);
		} else if (infos.getType().equals(AbsWifiCRUDForObject.DB_UPDATA)) {
			dao.updata(info);
			return WifiCreateAndParseSockObjectManager.createWifiWeatherTimeInfos(AbsWifiCRUDForObject.DB_UPDATA, WifiCreateAndParseSockObjectManager.WIFI_INFO_SUCCESS, info);
		} else if (infos.getType().equals(AbsWifiCRUDForObject.DB_DELETE)) {
			// dao.delete(info.getId());
			return null;
		} else if (infos.getType().equals(AbsWifiCRUDForObject.DB_SELECT)) {
			return WifiCreateAndParseSockObjectManager.createWifiWeatherTimeInfos(AbsWifiCRUDForObject.DB_SELECT, WifiCreateAndParseSockObjectManager.WIFI_INFO_SUCCESS, dao.select());
		}
		return null;
	}

	/**
	 * peter update get timer report weather method
	 * 
	 * @param obj
	 * @param context
	 * @return
	 */
	private String getWifiWeatherStateAndTimeData(WifiJSONObjectInfo obj, Context context) {
		// TODO Auto-generated method stub
		IWifiWeatherTimeDao dao = new WifiWeatherStateAndTimeDao(context);
		WifiWeatherTimeInfos infos = (WifiWeatherTimeInfos) obj.getObject();
		WifiWeatherTimeInfo info = infos.getWifiInfo().get(0);
		if (infos.getType().equals(AbsWifiCRUDForObject.DB_ADD)) {
			dao.add(info);
			return WifiCreateAndParseSockObjectManager.createWifiWeatherTimeInfos(AbsWifiCRUDForObject.DB_ADD, WifiCreateAndParseSockObjectManager.WIFI_INFO_SUCCESS, info);
		} else if (infos.getType().equals(AbsWifiCRUDForObject.DB_UPDATA)) {
			dao.updata(info);
			return WifiCreateAndParseSockObjectManager.createWifiWeatherTimeInfos(AbsWifiCRUDForObject.DB_UPDATA, WifiCreateAndParseSockObjectManager.WIFI_INFO_SUCCESS, info);
		} else if (infos.getType().equals(AbsWifiCRUDForObject.DB_DELETE)) {
			// dao.delete(info.getId());
			return null;
		} else if (infos.getType().equals(AbsWifiCRUDForObject.DB_SELECT)) {
			return WifiCreateAndParseSockObjectManager.createWifiWeatherTimeInfos(AbsWifiCRUDForObject.DB_SELECT, WifiCreateAndParseSockObjectManager.WIFI_INFO_SUCCESS, dao.select());
		} else if (infos.getType().equals(AbsWifiCRUDForObject.OPERATION_TYPE_SET)) {
			dao.SetWeatherReportCityName(info.getWeartherCityName());
			return null;
			// return
			// WifiCreateAndParseSockObjectManager.createWifiWeatherTimeInfos(AbsWifiCRUDForObject.OPERATION_TYPE_SET,
			// WifiCreateAndParseSockObjectManager.WIFI_INFO_SUCCESS, info);
		}
		return null;
	}

	private String getWifiPositionData(WifiJSONObjectInfo obj, Context context) {
		// TODO Auto-generated method stub
		IWifiPositionDao dao = new WifiPositionDao(context);
		WifiPositionInfos infos = (WifiPositionInfos) obj.getObject();
		WifiPositionInfo info = infos.getWifiInfo().get(0);
		if (infos.getType().equals(AbsWifiCRUDForObject.DB_ADD)) {
			dao.add(info);
			return WifiCreateAndParseSockObjectManager.createWifiPositionInfos(AbsWifiCRUDForObject.DB_ADD, WifiCreateAndParseSockObjectManager.WIFI_INFO_SUCCESS, info);
		} else if (infos.getType().equals(AbsWifiCRUDForObject.DB_UPDATA)) {
			dao.updata(info);
			return WifiCreateAndParseSockObjectManager.createWifiPositionInfos(AbsWifiCRUDForObject.DB_UPDATA, WifiCreateAndParseSockObjectManager.WIFI_INFO_SUCCESS, info);
		} else if (infos.getType().equals(AbsWifiCRUDForObject.DB_DELETE)) {
			// dao.delete(info.getId());
			return null;
		} else if (infos.getType().equals(AbsWifiCRUDForObject.DB_SELECT)) {
			return WifiCreateAndParseSockObjectManager.createWifiPositionInfos(AbsWifiCRUDForObject.DB_SELECT, WifiCreateAndParseSockObjectManager.WIFI_INFO_SUCCESS, dao.select());
		}
		return null;
	}

	private String getWifiWeatherData(WifiJSONObjectInfo obj, Context context) {
		// TODO Auto-generated method stub
		IWifiWeatherStatusDao dao = new WifiWeatherStatusDao(context);
		WifiWeatherStatusInfos infos = (WifiWeatherStatusInfos) obj.getObject();
		WifiWeatherStatusInfo info = infos.getWifiInfo().get(0);
		if (infos.getType().equals(AbsWifiCRUDForObject.DB_ADD)) {
			dao.add(info);
			return WifiCreateAndParseSockObjectManager.createWifiWeatherInfos(AbsWifiCRUDForObject.DB_ADD, WifiCreateAndParseSockObjectManager.WIFI_INFO_SUCCESS, info);
		} else if (infos.getType().equals(AbsWifiCRUDForObject.DB_UPDATA)) {
			dao.updata(info);
			return WifiCreateAndParseSockObjectManager.createWifiWeatherInfos(AbsWifiCRUDForObject.DB_UPDATA, WifiCreateAndParseSockObjectManager.WIFI_INFO_SUCCESS, info);
		} else if (infos.getType().equals(AbsWifiCRUDForObject.DB_DELETE)) {
			// dao.delete(info.getId());
			return null;
		} else if (infos.getType().equals(AbsWifiCRUDForObject.DB_SELECT)) {
			return WifiCreateAndParseSockObjectManager.createWifiWeatherInfos(AbsWifiCRUDForObject.DB_SELECT, WifiCreateAndParseSockObjectManager.WIFI_INFO_SUCCESS, dao.select());
		}
		return null;
	}

	/**
	 * @deprecated
	 */
	private String getWifiLedTimeData(WifiJSONObjectInfo obj, Context context) {
		// TODO Auto-generated method stub
		IWifiLedTimeDao dao = new WifiLedTimeDao(context);
		WifiLedTimeInfos infos = (WifiLedTimeInfos) obj.getObject();
		WifiLedTimeInfo info = infos.getWifiInfo().get(0);
		if (infos.getType().equals(AbsWifiCRUDForObject.DB_ADD)) {
			dao.add(info);
			return WifiCreateAndParseSockObjectManager.createWifiLedTimeInfos(AbsWifiCRUDForObject.DB_ADD, WifiCreateAndParseSockObjectManager.WIFI_INFO_SUCCESS, info);
		} else if (infos.getType().equals(AbsWifiCRUDForObject.DB_UPDATA)) {
			dao.updata(info);
			return WifiCreateAndParseSockObjectManager.createWifiLedTimeInfos(AbsWifiCRUDForObject.DB_UPDATA, WifiCreateAndParseSockObjectManager.WIFI_INFO_SUCCESS, info);
		} else if (infos.getType().equals(AbsWifiCRUDForObject.DB_DELETE)) {
			// dao.delete(info.getId());
			return null;
		} else if (infos.getType().equals(AbsWifiCRUDForObject.DB_SELECT)) {
			return WifiCreateAndParseSockObjectManager.createWifiLedTimeInfos(AbsWifiCRUDForObject.DB_SELECT, WifiCreateAndParseSockObjectManager.WIFI_INFO_SUCCESS, dao.select());
		}
		return null;
	}

	/**
	 * peter add update get ledSetting method
	 * 
	 * @param obj
	 * @param context
	 * @return
	 */
	private String getWifiLedStateAndTimeData(WifiJSONObjectInfo obj, Context context) {
		// TODO Auto-generated method stub
		IWifiLedTimeDao dao = new WifiLedStateAndTimeDao(context);
		WifiLedTimeInfos infos = (WifiLedTimeInfos) obj.getObject();
		WifiLedTimeInfo info = infos.getWifiInfo().get(0);
		if (infos.getType().equals(AbsWifiCRUDForObject.DB_ADD)) {
			dao.add(info);
			return WifiCreateAndParseSockObjectManager.createWifiLedTimeInfos(AbsWifiCRUDForObject.DB_ADD, WifiCreateAndParseSockObjectManager.WIFI_INFO_SUCCESS, info);
		} else if (infos.getType().equals(AbsWifiCRUDForObject.DB_UPDATA)) {
			dao.updata(info);
			return WifiCreateAndParseSockObjectManager.createWifiLedTimeInfos(AbsWifiCRUDForObject.DB_UPDATA, WifiCreateAndParseSockObjectManager.WIFI_INFO_SUCCESS, info);
		} else if (infos.getType().equals(AbsWifiCRUDForObject.DB_DELETE)) {
			// dao.delete(info.getId());
			return null;
		} else if (infos.getType().equals(AbsWifiCRUDForObject.DB_SELECT)) {
			return WifiCreateAndParseSockObjectManager.createWifiLedTimeInfos(AbsWifiCRUDForObject.DB_SELECT, WifiCreateAndParseSockObjectManager.WIFI_INFO_SUCCESS, dao.select());
		}
		return null;
	}

	private String getWifiLedData(WifiJSONObjectInfo obj, Context context) {
		// TODO Auto-generated method stub
		IWifiLedStatusDao dao = new WifiLedStatusDao(context);
		WifiLedStatusInfos infos = (WifiLedStatusInfos) obj.getObject();
		WifiLedStatusInfo info = infos.getWifiInfo().get(0);
		if (infos.getType().equals(AbsWifiCRUDForObject.DB_ADD)) {
			dao.add(info);
			return WifiCreateAndParseSockObjectManager.createWifiLedInfos(AbsWifiCRUDForObject.DB_ADD, WifiCreateAndParseSockObjectManager.WIFI_INFO_SUCCESS, info);
		} else if (infos.getType().equals(AbsWifiCRUDForObject.DB_UPDATA)) {
			dao.updata(info);
			return WifiCreateAndParseSockObjectManager.createWifiLedInfos(AbsWifiCRUDForObject.DB_UPDATA, WifiCreateAndParseSockObjectManager.WIFI_INFO_SUCCESS, info);
		} else if (infos.getType().equals(AbsWifiCRUDForObject.DB_DELETE)) {
			// dao.delete(info.getId());
			return null;
		} else if (infos.getType().equals(AbsWifiCRUDForObject.DB_SELECT)) {
			return WifiCreateAndParseSockObjectManager.createWifiLedInfos(AbsWifiCRUDForObject.DB_SELECT, WifiCreateAndParseSockObjectManager.WIFI_INFO_SUCCESS, dao.select());
		}
		return null;
	}

	public String getWifiControlData(WifiJSONObjectInfo obj, Context context) {
		IWifiControlDao dao = new WifiControlDao(context);
		WifiControlInfos infos = (WifiControlInfos) obj.getObject();
		WifiControlInfo info = infos.getWifiInfo().get(0);
		if (infos.getType().equals(AbsWifiCRUDForObject.DB_ADD)) {
			List<WifiControlInfo> list = dao.selectByRoomIdAndDeviceIdAndAction(info);
			if (list == null || list.isEmpty() || list.size() <= 0) {
				dao.add(info);
				list = dao.selectByRoomIdAndDeviceIdAndAction(info);

				return WifiCreateAndParseSockObjectManager.createWifiControlInfos(AbsWifiCRUDForObject.DB_ADD, WifiCreateAndParseSockObjectManager.WIFI_INFO_SUCCESS, list.get(0));
			}
			return WifiCreateAndParseSockObjectManager.createWifiControlInfos(AbsWifiCRUDForObject.DB_ADD, WifiCreateAndParseSockObjectManager.WIFI_INFO_REPEAT, list.get(0));
		} else if (infos.getType().equals(AbsWifiCRUDForObject.DB_UPDATA)) {
			dao.updata(info);
			return WifiCreateAndParseSockObjectManager.createWifiControlInfos(AbsWifiCRUDForObject.DB_UPDATA, WifiCreateAndParseSockObjectManager.WIFI_INFO_SUCCESS, info);
		} else if (infos.getType().equals(AbsWifiCRUDForObject.DB_DELETE)) {
			dao.delete(info.getId());
			context.sendBroadcast(new Intent(KeyList.IKEY_DELETE_CONTROL));

			return WifiCreateAndParseSockObjectManager.createWifiControlInfos(AbsWifiCRUDForObject.DB_DELETE, WifiCreateAndParseSockObjectManager.WIFI_INFO_SUCCESS, info);
		} else if (infos.getType().equals(AbsWifiCRUDForObject.DB_SELECT)) {
			String error = WifiCreateAndParseSockObjectManager.WIFI_INFO_SUCCESS;
			List<WifiControlInfo> list = dao.selectAll();
			// if (list == null || list.isEmpty()) {
			// error = WifiCreateAndParseSockObjectManager.WIFI_INFO_ERROR;
			// }
			return WifiCreateAndParseSockObjectManager.createWifiControlInfos(AbsWifiCRUDForObject.DB_SELECT, error, list);
		} else if (infos.getType().equals(AbsWifiCRUDForObject.DB_SELECT_BY_ID)) {
			String error = WifiCreateAndParseSockObjectManager.WIFI_INFO_SUCCESS;
			List<WifiControlInfo> list = dao.selectById(info.getId());
			// if (list == null || list.isEmpty()) {
			// error = WifiCreateAndParseSockObjectManager.WIFI_INFO_ERROR;
			// }
			return WifiCreateAndParseSockObjectManager.createWifiControlInfos(AbsWifiCRUDForObject.DB_SELECT_BY_ID, error, list);
		} else if (infos.getType().equals(AbsWifiCRUDForObject.DB_SELECT_BY_ROOM_ID)) {
			String error = WifiCreateAndParseSockObjectManager.WIFI_INFO_SUCCESS;
			List<WifiControlInfo> list = dao.selectByRoomId(info.getRoomId());
			// if (list == null || list.isEmpty()) {
			// error = WifiCreateAndParseSockObjectManager.WIFI_INFO_ERROR;
			// }
			return WifiCreateAndParseSockObjectManager.createWifiControlInfos(AbsWifiCRUDForObject.DB_SELECT_BY_ROOM_ID, error, list);
		} else if (infos.getType().equals(AbsWifiCRUDForObject.DB_SELECT_BY_DEVICE_ID)) {
			String error = WifiCreateAndParseSockObjectManager.WIFI_INFO_SUCCESS;
			List<WifiControlInfo> list = dao.selectByDeviceId(info.getDeviceid());
			// if (list == null || list.isEmpty()) {
			// error = WifiCreateAndParseSockObjectManager.WIFI_INFO_ERROR;
			// }
			return WifiCreateAndParseSockObjectManager.createWifiControlInfos(AbsWifiCRUDForObject.DB_SELECT_BY_DEVICE_ID, error, list);
		}
		return null;
	}

	public String getWifiRoomData(WifiJSONObjectInfo obj, Context context) {
		IWifiRoomDao dao = new WifiRoomDao(context);
		WifiRoomInfos infos = (WifiRoomInfos) obj.getObject();
		WifiRoomInfo info = infos.getWifiInfo().get(0);
		if (infos.getType().equals(AbsWifiCRUDForObject.DB_ADD)) {
			List<WifiRoomInfo> list = dao.selectByRoomName(info.getRoomName());
			if (list == null || list.isEmpty() || list.size() <= 0) {
				dao.add(info);
				list = dao.selectByRoomName(info.getRoomName());
				list.get(0).setRoomId(list.get(0).getId() + "");
				dao.updata(list.get(0));
				return WifiCreateAndParseSockObjectManager.createWifiWifiRoomInfos(AbsWifiCRUDForObject.DB_ADD, WifiCreateAndParseSockObjectManager.WIFI_INFO_SUCCESS, list.get(0));
			}
			return WifiCreateAndParseSockObjectManager.createWifiWifiRoomInfos(AbsWifiCRUDForObject.DB_ADD, WifiCreateAndParseSockObjectManager.WIFI_INFO_REPEAT, list.get(0));
		} else if (infos.getType().equals(AbsWifiCRUDForObject.DB_UPDATA)) {
			dao.updata(info);
			return WifiCreateAndParseSockObjectManager.createWifiWifiRoomInfos(AbsWifiCRUDForObject.DB_UPDATA, WifiCreateAndParseSockObjectManager.WIFI_INFO_SUCCESS, info);
		} else if (infos.getType().equals(AbsWifiCRUDForObject.DB_DELETE)) {
			dao.deleteByRoomId(info.getRoomId());
			return WifiCreateAndParseSockObjectManager.createWifiWifiRoomInfos(AbsWifiCRUDForObject.DB_DELETE, WifiCreateAndParseSockObjectManager.WIFI_INFO_SUCCESS, info);
		} else if (infos.getType().equals(AbsWifiCRUDForObject.DB_SELECT)) {
			String error = WifiCreateAndParseSockObjectManager.WIFI_INFO_SUCCESS;
			List<WifiRoomInfo> list = dao.selectByAll();
			// if (list == null || list.isEmpty()) {
			// error = WifiCreateAndParseSockObjectManager.WIFI_INFO_ERROR;
			// }
			return WifiCreateAndParseSockObjectManager.createWifiWifiRoomInfos(AbsWifiCRUDForObject.DB_SELECT, error, list);
		} else if (infos.getType().equals(AbsWifiCRUDForObject.DB_SELECT_BY_ROOM_ID)) {
			String error = WifiCreateAndParseSockObjectManager.WIFI_INFO_SUCCESS;
			List<WifiRoomInfo> list = dao.selectByRoomId(info.getRoomId());
			// if (list == null || list.isEmpty()) {
			// error = WifiCreateAndParseSockObjectManager.WIFI_INFO_ERROR;
			// }
			return WifiCreateAndParseSockObjectManager.createWifiWifiRoomInfos(AbsWifiCRUDForObject.DB_SELECT_BY_ROOM_ID, error, list);
		}
		return null;
	}

	public String getWifiDeviceData(WifiJSONObjectInfo obj, Context context) {
		IWifiDeviceDao dao = new WifiDeviceDao(context);
		WifiDeviceInfos infos = (WifiDeviceInfos) obj.getObject();
		WifiDeviceInfo info = infos.getWifiInfo().get(0);
		if (infos.getType().equals(AbsWifiCRUDForObject.DB_ADD)) {
			List<WifiDeviceInfo> list = dao.selectByDeviceName(info.getMacadd(), info.getRoomid());
			if (list == null || list.isEmpty() || list.size() <= 0) {
				dao.add(info);
				list = dao.selectByDeviceName(info.getMacadd(), info.getRoomid());
				list.get(0).setDeviceid(list.get(0).getId() + "");
				dao.updata(list.get(0));
				return WifiCreateAndParseSockObjectManager.createWifiDeviceInfos(AbsWifiCRUDForObject.DB_ADD, WifiCreateAndParseSockObjectManager.WIFI_INFO_SUCCESS, list.get(0));
			}
			return WifiCreateAndParseSockObjectManager.createWifiDeviceInfos(AbsWifiCRUDForObject.DB_ADD, WifiCreateAndParseSockObjectManager.WIFI_INFO_REPEAT, list.get(0));
		} else if (infos.getType().equals(AbsWifiCRUDForObject.DB_UPDATA)) {
			dao.updata(info);
			return WifiCreateAndParseSockObjectManager.createWifiDeviceInfos(AbsWifiCRUDForObject.DB_UPDATA, WifiCreateAndParseSockObjectManager.WIFI_INFO_SUCCESS, info);
		} else if (infos.getType().equals(AbsWifiCRUDForObject.DB_DELETE)) {
			dao.deleteByDeviceId(info.getDeviceid());
			context.sendBroadcast(new Intent(KeyList.IKEY_DELETE_DEVICE));

			return WifiCreateAndParseSockObjectManager.createWifiDeviceInfos(AbsWifiCRUDForObject.DB_DELETE, WifiCreateAndParseSockObjectManager.WIFI_INFO_SUCCESS, info);
		} else if (infos.getType().equals(AbsWifiCRUDForObject.DB_SELECT)) {
			String error = WifiCreateAndParseSockObjectManager.WIFI_INFO_SUCCESS;
			List<WifiDeviceInfo> list = dao.selectByAll();
			// if (list == null || list.isEmpty()) {
			// error = WifiCreateAndParseSockObjectManager.WIFI_INFO_ERROR;
			// }
			return WifiCreateAndParseSockObjectManager.createWifiDeviceInfos(AbsWifiCRUDForObject.DB_SELECT, error, list);
		} else if (infos.getType().equals(AbsWifiCRUDForObject.DB_SELECT_BY_DEVICE_ID)) {
			String error = WifiCreateAndParseSockObjectManager.WIFI_INFO_SUCCESS;
			List<WifiDeviceInfo> list = dao.selectByDeviceId(info.getDeviceid());
			// if (list == null || list.isEmpty()) {
			// error = WifiCreateAndParseSockObjectManager.WIFI_INFO_ERROR;
			// }
			return WifiCreateAndParseSockObjectManager.createWifiDeviceInfos(AbsWifiCRUDForObject.DB_SELECT_BY_DEVICE_ID, error, list);
		} else if (infos.getType().equals(AbsWifiCRUDForObject.DB_SELECT_BY_ROOM_ID)) {
			String error = WifiCreateAndParseSockObjectManager.WIFI_INFO_SUCCESS;
			List<WifiDeviceInfo> list = dao.selectByRoomId(info.getRoomid());
			// if (list == null || list.isEmpty()) {
			// error = WifiCreateAndParseSockObjectManager.WIFI_INFO_ERROR;
			// }
			return WifiCreateAndParseSockObjectManager.createWifiDeviceInfos(AbsWifiCRUDForObject.DB_SELECT_BY_ROOM_ID, error, list);
		}
		return null;
	}

	public String getWifiTTSData(WifiJSONObjectInfo obj, Context context) {
		IWifiTTSDao dao = new WifiTTSDao(context);
		WifiTTSVocalizationTypeInfos infos = (WifiTTSVocalizationTypeInfos) obj.getObject();
		WifiTTSVocalizationTypeInfo info = infos.getWifiInfo().get(0);
		if (infos.getType().equals(AbsWifiCRUDForObject.DB_ADD)) {
			dao.add(info);
			return WifiCreateAndParseSockObjectManager.createWifiTTSVocalizationTypeInfos(AbsWifiCRUDForObject.DB_ADD, WifiCreateAndParseSockObjectManager.WIFI_INFO_SUCCESS, info);
		} else if (infos.getType().equals(AbsWifiCRUDForObject.DB_UPDATA)) {
			dao.updata(info);
			return WifiCreateAndParseSockObjectManager.createWifiTTSVocalizationTypeInfos(AbsWifiCRUDForObject.DB_UPDATA, WifiCreateAndParseSockObjectManager.WIFI_INFO_SUCCESS, info);
		} else if (infos.getType().equals(AbsWifiCRUDForObject.DB_DELETE)) {
			// dao.delete(info.getId());
			return null;
		} else if (infos.getType().equals(AbsWifiCRUDForObject.DB_SELECT)) {
			return WifiCreateAndParseSockObjectManager.createWifiTTSVocalizationTypeInfos(AbsWifiCRUDForObject.DB_SELECT, WifiCreateAndParseSockObjectManager.WIFI_INFO_SUCCESS, dao.select());
		}
		return null;
	}

	public String getWifiUserData(WifiJSONObjectInfo obj, Context context) {
		IWifiUserDao dao = new WifiUserDao(context);
		WifiUserInfos infos = (WifiUserInfos) obj.getObject();
		WifiUserInfo info = infos.getWifiInfo().get(0);
		if (infos.getType().equals(AbsWifiCRUDForObject.DB_ADD)) {
			dao.add(info);
			return WifiCreateAndParseSockObjectManager.createWifiUserInfos(AbsWifiCRUDForObject.DB_ADD, WifiCreateAndParseSockObjectManager.WIFI_INFO_SUCCESS, info);
		} else if (infos.getType().equals(AbsWifiCRUDForObject.DB_UPDATA)) {
			dao.updata(info);
			return WifiCreateAndParseSockObjectManager.createWifiUserInfos(AbsWifiCRUDForObject.DB_UPDATA, WifiCreateAndParseSockObjectManager.WIFI_INFO_SUCCESS, info);
		} else if (infos.getType().equals(AbsWifiCRUDForObject.DB_DELETE)) {
			// dao.delete(info.getId());
			// return
			// WifiCreateAndParseSockObjectManager.createWifiUserInfos(AbsWifiCRUDForObject.DB_SELECT,
			// WifiCreateAndParseSockObjectManager.WIFI_INFO_SUCCESS,dao.seleteAll());
		} else if (infos.getType().equals(AbsWifiCRUDForObject.DB_SELECT)) {
			return WifiCreateAndParseSockObjectManager.createWifiUserInfos(AbsWifiCRUDForObject.DB_SELECT, WifiCreateAndParseSockObjectManager.WIFI_INFO_SUCCESS, dao.selectAll());
		}
		return null;
	}

	public String getCommonOperiteResponse(WifiJSONObjectInfo obj, Context context) {
		WifiConfigOperite configOperite = new WifiConfigOperite(mContext);
		Gson gson = new Gson();
		WifiJSONObjectInfo newobj = new WifiJSONObjectInfo();
		LogManager.e(new Gson().toJson(obj.getObject()));

		WifiJSONObjectForLearnHFs learnHFs = gson.fromJson(new Gson().toJson(obj.getObject()), WifiJSONObjectForLearnHFs.class);
		WifiJSONObjectForLearnHF learnHF = learnHFs.getWifiInfoFirst();
		newobj.setError(WifiCreateAndParseSockObjectManager.WIFI_INFO_SUCCESS);

		if (learnHF.getType().equals(WifiConfigOperite.GET_UNCONFIGED_DEVICE)) {
			List<WifiDeviceInfo> unconfigDevices = configOperite.getUnConfigedDevice();
			learnHF.setDeviceInfos(unconfigDevices);

		} else if (learnHF.getType().equals(WifiConfigOperite.LEARN_HF)) {

			String deviceID = learnHF.getDeviceID();
			String result = configOperite.startLearnHF(deviceID);
			if (result != null) {
				learnHF.setHFContent(result);
			} else {
				newobj.setError(WifiCreateAndParseSockObjectManager.WIFI_INFO_ERROR);
			}
			LogManager.e(result);

		} else if (learnHF.getType().equals(WifiConfigOperite.PLAY_TTS)) {
			String content = learnHF.getDeviceID();
			if (KeyList.TTSUtil != null) {
				KeyList.TTSUtil.playContent(content);
			} else {
				newobj.setError(WifiCreateAndParseSockObjectManager.WIFI_INFO_ERROR);
			}
		}
		WifiJSONObjectForLearnHFs learnHfs = new WifiJSONObjectForLearnHFs();
		learnHfs.setWifiInfo(learnHF);

		newobj.setObject(learnHfs);
		newobj.setType(WifiCreateAndParseSockObjectManager.WIFI_INFO_TYPE_COMMON_OPRITE);
		String result = gson.toJson(newobj);
		LogManager.e(result);
		return result;
	}

	public String getRemindResponse(WifiJSONObjectInfo obj, Context context) {
		Gson gson = new Gson();
		LogManager.e(new Gson().toJson(obj));
		WifiRemindInfos infos = (WifiRemindInfos) obj.getObject();
		obj.setError(WifiCreateAndParseSockObjectManager.WIFI_INFO_SUCCESS);
		if (infos.getType().equals(AbsWifiCRUDForObject.DB_ADD)) {
			Remind r = infos.getRemindInfos().get(0);
			KeyList.REMIND_UTIL.addRemind(r);
		} else if (infos.getType().equals(AbsWifiCRUDForObject.DB_DELETE)) {
			Remind r = infos.getRemindInfos().get(0);
			KeyList.REMIND_UTIL.deleteRemind(r.id);
		} else if (infos.getType().equals(AbsWifiCRUDForObject.DB_UPDATA)) {
			Remind r = infos.getRemindInfos().get(0);
			KeyList.REMIND_UTIL.deleteRemind(r.id);
			KeyList.REMIND_UTIL.addRemind(r);
		} else if (infos.getType().equals(AbsWifiCRUDForObject.DB_SELECT)) {
			infos.setRemindInfos(KeyList.REMIND_UTIL.getReMindList());
		}
		infos.setCurrentTime(System.currentTimeMillis());
		obj.setObject(infos);

		String result = gson.toJson(obj);
		return result;
	}

	public String getVolumeResponse(WifiJSONObjectInfo obj, Context context) {
		WifiVolumeCotroller controller = new WifiVolumeCotroller(context, AudioManager.STREAM_MUSIC);
		Gson gson = new Gson();
		LogManager.e(gson.toJson(obj));

		obj.setError(WifiCreateAndParseSockObjectManager.WIFI_INFO_SUCCESS);
		WifiVolume info = (WifiVolume) obj.getObject();

		if (info.getType().equals(WifiVolumeCotroller.GET_BOX_VOLUME)) {
			info.setCurrentVolume(controller.getCurrentVolume());
			info.setMaxVolume(controller.getMaxVolume());
			return WifiCreateAndParseSockObjectManager.createWifiVolumeInfos(WifiCreateAndParseSockObjectManager.WIFI_INFO_SUCCESS, info);

		} else if (info.getType().equals(WifiVolumeCotroller.SET_BOX_VOLUME)) {
			controller.setVolume(info.getVolume());
			return WifiCreateAndParseSockObjectManager.createWifiVolumeInfos(WifiCreateAndParseSockObjectManager.WIFI_INFO_SUCCESS, info);
		}
		String result = gson.toJson(obj);
		return result;
	}

	public String getTTSVolumeResponse(WifiJSONObjectInfo obj, Context context) {
		WifiVolumeCotroller controller = new WifiVolumeCotroller(context, AudioManager.STREAM_ALARM);
		Gson gson = new Gson();
		LogManager.e(gson.toJson(obj));

		obj.setError(WifiCreateAndParseSockObjectManager.WIFI_INFO_SUCCESS);
		WifiVolume info = (WifiVolume) obj.getObject();

		if (info.getType().equals(WifiVolumeCotroller.GET_TTS_VOLUME)) {
			info.setCurrentVolume(controller.getCurrentVolume());
			info.setMaxVolume(controller.getMaxVolume());
			return WifiCreateAndParseSockObjectManager.createWifiVolumeInfos(WifiCreateAndParseSockObjectManager.WIFI_INFO_SUCCESS, info);

		} else if (info.getType().equals(WifiVolumeCotroller.SET_TTS_VOLUME)) {
			controller.setVolume(info.getVolume());
			return WifiCreateAndParseSockObjectManager.createWifiVolumeInfos(WifiCreateAndParseSockObjectManager.WIFI_INFO_SUCCESS, info);
		}
		String result = gson.toJson(obj);
		return result;
	}

	public String getMyTagResponse(final WifiJSONObjectInfo obj, Context context, final String gsonInfo) {
		Gson gson = new Gson();
		LogManager.e(gson.toJson(obj));
		obj.setError(WifiCreateAndParseSockObjectManager.WIFI_INFO_SUCCESS);
		final WifiMyTag info = (WifiMyTag) obj.getObject();

		if (info.getType().equals(AbsWifiCRUDForObject.OPERATION_TYPE_SET)) {
			// TODO 获取到的设置数据
			WifiSetMyTag mWifiSetMyTag = new WifiSetMyTag(context);
			mWifiSetMyTag.setTagInfo(info.getTag(), info.getImei());
			mWifiSetMyTag.setRequestListener(new WifiSetMyTag.HttpRequestListener() {
				@Override
				public void onRequestResult(boolean isSuccess, String result) {
					// TODO Auto-generated method stub
					String create = "";
					if (isSuccess) {
						create = WifiCreateAndParseSockObjectManager.createWifiMyTagInfos(WifiCreateAndParseSockObjectManager.WIFI_INFO_SUCCESS, info);
					} else {
						create = WifiCreateAndParseSockObjectManager.createWifiMyTagInfos(WifiCreateAndParseSockObjectManager.WIFI_INFO_ERROR, info);
					}
					responseToClient(create, obj, gsonInfo);
				}
			});

			return WifiCreateAndParseSockObjectManager.createWifiMyTagInfos(WifiCreateAndParseSockObjectManager.WIFI_INFO_SUCCESS, info);

		} else if (info.getType().equals(AbsWifiCRUDForObject.OPERATION_TYPE_GET)) {
			// TODO 传入查询数据
			WifiSetMyTag mWifiSetMyTag = new WifiSetMyTag(context);
			mWifiSetMyTag.getTagInfo(info.getImei());
			mWifiSetMyTag.setRequestListener(new WifiSetMyTag.HttpRequestListener() {
				@Override
				public void onRequestResult(boolean isSuccess, String result) {
					// TODO Auto-generated method stub
					String create = "";
					if (isSuccess) {
						String tag = ParserMyTag.getTag(result);
						info.setTag(tag);
						create = WifiCreateAndParseSockObjectManager.createWifiMyTagInfos(WifiCreateAndParseSockObjectManager.WIFI_INFO_SUCCESS, info);
					} else {
						create = WifiCreateAndParseSockObjectManager.createWifiMyTagInfos(WifiCreateAndParseSockObjectManager.WIFI_INFO_ERROR, info);
					}
					responseToClient(create, obj, gsonInfo);
				}
			});

			return WifiCreateAndParseSockObjectManager.createWifiMyTagInfos(WifiCreateAndParseSockObjectManager.WIFI_INFO_SUCCESS, info);
		}
		String result = gson.toJson(obj);
		return result;
	}

	public String getUserDataResponse(final WifiJSONObjectInfo obj, Context context, final String gsonInfo) {
		Gson gson = new Gson();
		LogManager.e(gson.toJson(obj));
		obj.setError(WifiCreateAndParseSockObjectManager.WIFI_INFO_SUCCESS);
		final WifiUserData info = (WifiUserData) obj.getObject();
		WifiSetUserData setData = new WifiSetUserData(info, context);
		if (info.getType().equals(AbsWifiCRUDForObject.OPERATION_TYPE_SET)) {
			// TODO 获取到的设置数据
			setData.setUserData();
			setData.setRequestListener(new WifiSetUserData.HttpRequestListener() {
				@Override
				public void onRequestResult(boolean isSuccess, String result) {
					// TODO Auto-generated method stub
					String create = "";
					if (isSuccess) {
						create = WifiCreateAndParseSockObjectManager.createWifiUserDataInfos(WifiCreateAndParseSockObjectManager.WIFI_INFO_SUCCESS, info);

					} else {
						create = WifiCreateAndParseSockObjectManager.createWifiUserDataInfos(WifiCreateAndParseSockObjectManager.WIFI_INFO_ERROR, info);
					}
					responseToClient(create, obj, gsonInfo);
				}
			});

			return WifiCreateAndParseSockObjectManager.createWifiUserDataInfos(WifiCreateAndParseSockObjectManager.WIFI_INFO_SUCCESS, info);

		} else if (info.getType().equals(AbsWifiCRUDForObject.OPERATION_TYPE_GET)) {
			// TODO 传入查询数据
			setData.getUserData(info.getImei());
			setData.setRequestListener(new WifiSetUserData.HttpRequestListener() {
				@Override
				public void onRequestResult(boolean isSuccess, String result) {
					// TODO Auto-generated method stub
					String create = "";
					if (isSuccess) {
						ParserUserData data = new ParserUserData();
						WifiUserData uInfo = info;
						if (result != null && result.length() > 2) {
							try {
								uInfo = data.getParserData(result);
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
						create = WifiCreateAndParseSockObjectManager.createWifiUserDataInfos(WifiCreateAndParseSockObjectManager.WIFI_INFO_SUCCESS, uInfo);

					} else {
						create = WifiCreateAndParseSockObjectManager.createWifiUserDataInfos(WifiCreateAndParseSockObjectManager.WIFI_INFO_ERROR, info);
					}
					responseToClient(create, obj, gsonInfo);
				}
			});
			return WifiCreateAndParseSockObjectManager.createWifiUserDataInfos(WifiCreateAndParseSockObjectManager.WIFI_INFO_SUCCESS, info);
		}
		String result = gson.toJson(obj);
		return result;
	}

	public String getBoxModeResponse(final WifiJSONObjectInfo obj, Context context) {
		Gson gson = new Gson();
		LogManager.e(gson.toJson(obj));

		obj.setError(WifiCreateAndParseSockObjectManager.WIFI_INFO_SUCCESS);
		WifiBoxModeInfos infos = (WifiBoxModeInfos) obj.getObject();
		WifiModeDao mWifiModeDao = new WifiModeDao(context);
		String type = infos.getType();

		if (type.equals(AbsWifiCRUDForObject.DB_ADD)) {
			WifiBoxModeInfo mWifiBoxModeInfo = new WifiBoxModeInfo();
			WifiBoxModeInfo boxInfo = infos.getWifiInfos().get(0);
			String name = boxInfo.getModeName();
			mWifiBoxModeInfo.setModeName(name);
			mWifiBoxModeInfo.setControlIDs(boxInfo.getControlIDs());
			// mWifiBoxModeInfo.setAction("开/关");
			mWifiBoxModeInfo.setAction(KeyList.OPER_DEVICE_ARRAY[0]);

			// 设置模式ID
			String action = boxInfo.getAction();
			mWifiBoxModeInfo.setId(BoxModeEnumUtils.getModeId(name));
			android.util.Log.e("hefeng", "id=" + BoxModeEnumUtils.getModeId(name));

			// 通过模式名称和action查询
			List<WifiBoxModeInfo> modes = mWifiModeDao.selectByNameAndAction(name, KeyList.OPER_DEVICE_ARRAY[0]);

			if (!modes.isEmpty()) {
				mWifiBoxModeInfo.setId(modes.get(0).getId());
				mWifiModeDao.update(mWifiBoxModeInfo);
				android.util.Log.e("hefeng", "update");
			} else {
				android.util.Log.e("hefeng", "add");
				mWifiModeDao.add(mWifiBoxModeInfo);
			}

			addDeleteModeData(infos.getWifiInfos().get(0), mWifiModeDao);

			android.util.Log.e("hefeng", "WIFI_INFO_TYPE_BOX_MODE:" + infos.getWifiInfos().get(0).getModeName() + "||" + infos.getWifiInfos().get(0).getControlIDs());
			return WifiCreateAndParseSockObjectManager.createWifiBoxModeInfos(AbsWifiCRUDForObject.DB_ADD, WifiCreateAndParseSockObjectManager.WIFI_INFO_SUCCESS, mWifiBoxModeInfo);

		} else if (type.equals(AbsWifiCRUDForObject.DB_SELECT)) {
			android.util.Log.e("hefeng", "AbsWifiCRUDForObject.DB_SELECT");
			List<WifiBoxModeInfo> list = mWifiModeDao.selectOpenModeData();
			// if (list == null) {
			// list = new ArrayList<WifiBoxModeInfo>();
			// }
			return WifiCreateAndParseSockObjectManager.createWifiBoxModeInfos(AbsWifiCRUDForObject.DB_SELECT, WifiCreateAndParseSockObjectManager.WIFI_INFO_SUCCESS, list);

		} else if (type.equals(AbsWifiCRUDForObject.DB_SELECT_BY_NAME)) {
			android.util.Log.e("hefeng", "DB_SELECT_BY_NAME");
			return WifiCreateAndParseSockObjectManager.createWifiBoxModeInfos(AbsWifiCRUDForObject.DB_SELECT_BY_NAME, WifiCreateAndParseSockObjectManager.WIFI_INFO_SUCCESS,
					mWifiModeDao.selectByName(infos.getWifiInfos().get(0).getModeName()));
		}

		String result = gson.toJson(obj);
		return result;
	}

	/**
	 * 添加删除模式数据
	 * 
	 * @param modeInfo
	 * @param modeDao
	 */
	private void addDeleteModeData(final WifiBoxModeInfo modeInfo, final WifiModeDao modeDao) {
		StringBuffer deleteModeIds = new StringBuffer();
		IWifiControlDao dao = new WifiControlDao(mContext);
		WifiControlInfo mWifiControlInfo = null;

		if (TextUtils.isEmpty(modeInfo.getControlIDs())) {
			// 不存在数据，则全部删除
			for (int i = 0; i < BoxModeEnumUtils.deleteModeArrayId.length; i++) {
				try {
					Thread.sleep(50);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				modeDao.delete(BoxModeEnumUtils.deleteModeArrayId[i]);
			}
		} else {
			String newAction;
			List<WifiControlInfo> selectList;
			// 通过Id查找control表中的数据，如果包含开/关，那么指令是同一条
			// 否则，再通过control表中deviceid查找记录判断是否有其他指令
			String[] id = modeInfo.getControlIDs().split(KeyList.SEPARATOR_ACTION_SUBLIT);

			for (int i = 0; i < id.length; i++) {
				try {
					Thread.sleep(50);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				List<WifiControlInfo> control = dao.selectById(Integer.parseInt(id[i]));
				WifiControlInfo info = control.get(0);

				if (control != null && !control.isEmpty()) {
					mWifiControlInfo = null;

					String action = info.getAction();
					String device = action.split(KeyList.SEPARATOR_ACTION_SUBLIT)[1];

					if (action.startsWith(KeyList.OPER_DEVICE_ARRAY[0])) { // 打开
						mWifiControlInfo = new WifiControlInfo();
						newAction = KeyList.OPER_DEVICE_ARRAY[1] + KeyList.SEPARATOR_ACTION + device;
						mWifiControlInfo.setAction(newAction);

					} else if (action.startsWith(KeyList.OPER_DEVICE_ARRAY[1])) {// 关闭
						mWifiControlInfo = new WifiControlInfo();
						newAction = KeyList.OPER_DEVICE_ARRAY[0] + KeyList.SEPARATOR_ACTION + device;
						mWifiControlInfo.setAction(newAction);

					} else if (action.startsWith(KeyList.OPER_DEVICE_ARRAY[2])) {// 开/关
						deleteModeIds.append(info.getId());
						deleteModeIds.append(KeyList.SEPARATOR_ACTION);
					}

					if (mWifiControlInfo != null) {
						mWifiControlInfo.setRoomId(info.getRoomId());
						mWifiControlInfo.setDeviceid(info.getDeviceid());

						selectList = dao.selectByRoomIdAndDeviceIdAndAction(mWifiControlInfo);
						if (selectList != null && !selectList.isEmpty()) {
							deleteModeIds.append(selectList.get(0).getId());
							deleteModeIds.append(KeyList.SEPARATOR_ACTION);
						}
					}
				}
			}

			String endIds = deleteModeIds.toString();
			if (endIds.endsWith(KeyList.SEPARATOR_ACTION)) {
				endIds = endIds.substring(0, endIds.length() - KeyList.SEPARATOR_ACTION.length());
			}

			android.util.Log.e("hefeng", "delete mode db controls id=" + endIds);

			modeInfo.setId(BoxModeEnumUtils.getDeleteModeId(modeInfo.getModeName()));
			modeInfo.setControlIDs(endIds);
			modeInfo.setAction(KeyList.OPER_DEVICE_ARRAY[1]);

			// 通过模式名称和action查询
			List<WifiBoxModeInfo> modes = modeDao.selectByNameAndAction(modeInfo.getModeName(), KeyList.OPER_DEVICE_ARRAY[1]);
			if (!modes.isEmpty()) {
				modeInfo.setId(modes.get(0).getId());

				modeDao.update(modeInfo);
				android.util.Log.e("hefeng", "delete mode db update");
			} else {
				android.util.Log.e("hefeng", "delete mode db add");
				modeDao.add(modeInfo);
			}
		}
	}

	public String getUpdateResponse(final WifiJSONObjectInfo obj, Context context) {
		Gson gson = new Gson();
		LogManager.e(gson.toJson(obj));

		obj.setError(WifiCreateAndParseSockObjectManager.WIFI_INFO_SUCCESS);
		WifiUpdateInfos infos = (WifiUpdateInfos) obj.getObject();
		String type = infos.getType();

		WifiUpdateInfo info = infos.getWifiInfos().get(0);

		IWifiRoomDao roomDao = new WifiRoomDao(context);
		WifiDeviceDao deviceDao = new WifiDeviceDao(context);
		IWifiControlDao controlDao = new WifiControlDao(context);

		if (type.equals(AbsWifiCRUDForObject.DB_UPDATA)) {
			LogManager.i("hefeng", "AbsWifiCRUDForObject.DB_SELECT ");

			String roomName = info.getRoomName();
			String deviceName = info.getDeviceName();
			String roomId = "";
			String deviceId = "";
			LogManager.i("hefeng", "roomName=" + roomName + "||deviceName=" + deviceName);

			List<WifiRoomInfo> rooms = roomDao.selectByRoomName(roomName);
			List<WifiDeviceInfo> devices = deviceDao.selectByAll();

			if (rooms == null || rooms.isEmpty()) {
				LogManager.i("hefeng", "房间没有创建，创建房间");
				// 房间没有创建
				WifiRoomInfo room = new WifiRoomInfo();
				room.setRoomName(roomName);
				roomDao.add(room);

				List<WifiRoomInfo> list = roomDao.selectByRoomName(roomName);
				list.get(0).setRoomId(list.get(0).getId() + "");
				roomDao.updata(list.get(0));
			}

			rooms = roomDao.selectByRoomName(roomName);
			// 设置的RoomId
			roomId = rooms.get(0).getRoomId();

			OUT: for (int i = 0; i < devices.size(); i++) {
				String name = devices.get(i).getDeviceName();
				if (name.equals(deviceName)) {
					// 有设备则更新设备名称
					String[] names = name.split(KeyList.SEPARATOR);
					WifiDeviceInfo device2 = devices.get(i);

					// 说明不止一个设备
					if (names.length > 1) {
						// 更新设备名称
						for (int j = 0; j < names.length; j++) {
							if (!names[j].equals(deviceName)) {
								name += names[i];
								name += KeyList.SEPARATOR;
							}
						}

						device2.setDeviceName(name);
						deviceDao.updata(device2);

						// 单独创建一个设备数据
						device2 = info.getWifiDeviceInfo();
						device2.setDeviceName(deviceName);
						deviceDao.add(device2);

					} else {
						// 只有一个设备，则更新设备ID
						LogManager.i("有设备则更新设备房间ID=" + roomId);
						device2.setRoomid(roomId);
						deviceDao.updata(device2);
					}

					break OUT;
				}
				if (i == devices.size() - 1) {
					// 设备没有创建
					LogManager.i("设备没有创建");

					WifiDeviceInfo device = info.getWifiDeviceInfo();
					device.setDeviceName(deviceName);
					device.setRoomid(roomId);
					deviceDao.add(device);

					List<WifiDeviceInfo> list = deviceDao.selectByRoomId(roomId);
					WifiDeviceInfo dinfo = list.get(0);
					dinfo.setDeviceid(dinfo.getId() + "");
					deviceDao.updata(dinfo);
					if (!deviceId.equals(info.getWifiDeviceInfo().getDeviceid())) {
						deviceDao.deleteByDeviceId2(info.getWifiDeviceInfo().getDeviceid());
					}
				}
			}

			List<WifiDeviceInfo> deviceInfos = deviceDao.selectByRoomId(roomId);
			deviceId = deviceDao.selectByMacAdd(info.getWifiDeviceInfo().getMacadd()).get(0).getDeviceid();

			List<WifiControlInfo> controls = controlDao.selectByDeviceId(info.getWifiDeviceInfo().getDeviceid());
			// 控制列表不存在创建
			if (controls == null || controls.isEmpty()) {
				LogManager.e("控制列表不存在");
			} else {
				LogManager.i("控制列表存在,则更新");
				for (WifiControlInfo c : controls) {
					String action = c.getAction().split(KeyList.SEPARATOR_ACTION_SUBLIT)[0] + KeyList.SEPARATOR_ACTION + info.getWifiDeviceInfo().getDeviceName();
					c.setRoomId(roomId);
					c.setDeviceid(deviceId);
					c.setAction(action);
					c.setCorder(action);
					controlDao.updata(c);
				}
			}

			context.sendBroadcast(new Intent(KeyList.AKEY_COMMAND_CHANGE));
		}
		String result = gson.toJson(obj);
		return result;
	}

	public String getStringResponse(final WifiJSONObjectInfo obj, Context context) {
		Gson gson = new Gson();
		LogManager.e(gson.toJson(obj));
		obj.setError(WifiCreateAndParseSockObjectManager.WIFI_INFO_SUCCESS);
		WifiStringInfos infos = (WifiStringInfos) obj.getObject();
		WifiStringInfo info = infos.getInfos().get(0);

		String type = info.getType();
		// 设置序列号
		if (type.equals(WifiConstants.STR_TYPE_SCNNER_TWO_CODE)) {
			LogManager.i("WifiStringInfo msg:" + info.getMessage() + "||type:" + info.getType());

			try {
				// ShellUtils.execRootCmdSilent("mount -o remount rw /system");
				// ShellUtils.execRootCmdSilent("chmod 777 /system/usr");
				String set = SerialNumberUitls.writeSerialNumber(mContext, info.getMessage()).trim();
				info.setMessage(set);

				return WifiCreateAndParseSockObjectManager.createWifiStringInfos(AbsWifiCRUDForObject.DB_SELECT, WifiCreateAndParseSockObjectManager.WIFI_INFO_SUCCESS, info);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return WifiCreateAndParseSockObjectManager.createWifiStringInfos(AbsWifiCRUDForObject.DB_SELECT, WifiCreateAndParseSockObjectManager.WIFI_INFO_ERROR, info);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} else if (type.equals(WifiConstants.STR_TYPE_START_RECORD)) {
			WeakupControl control = new WeakupControl(context);
			control.bindControlService();
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			control.closeWeakup();
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			// 开始录音
			AudioRecorder audio = AudioRecorder.getAudioRecorder();
			if (audio == null) {
				try {
					audio = new AudioRecorder("box-record-" + new SimpleDateFormat("yyyyMMdd-HHmmss").format(new Date()));
					audio.start();
					return WifiCreateAndParseSockObjectManager.createWifiStringInfos(AbsWifiCRUDForObject.DB_SELECT, WifiCreateAndParseSockObjectManager.WIFI_INFO_SUCCESS, info);
				} catch (Exception e) {
					AudioRecorder.setAudioRecorder(null);
					e.printStackTrace();
					return WifiCreateAndParseSockObjectManager.createWifiStringInfos(AbsWifiCRUDForObject.DB_SELECT, WifiCreateAndParseSockObjectManager.WIFI_INFO_ERROR, info);
				}

			}
			return WifiCreateAndParseSockObjectManager.createWifiStringInfos(AbsWifiCRUDForObject.DB_SELECT, WifiCreateAndParseSockObjectManager.WIFI_INFO_ERROR, info);

		} else if (type.equals(WifiConstants.STR_TYPE_STOP_RECORD)) {

			WeakupControl control = new WeakupControl(context);
			control.bindControlService();
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			control.openWeakup();
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			// 停止录音
			AudioRecorder audio = AudioRecorder.getAudioRecorder();
			if (audio != null) {
				try {
					audio.stop();
					AudioRecorder.setAudioRecorder(null);
					return WifiCreateAndParseSockObjectManager.createWifiStringInfos(AbsWifiCRUDForObject.DB_SELECT, WifiCreateAndParseSockObjectManager.WIFI_INFO_SUCCESS, info);
				} catch (IOException e) {
					e.printStackTrace();
					AudioRecorder.setAudioRecorder(null);
					return WifiCreateAndParseSockObjectManager.createWifiStringInfos(AbsWifiCRUDForObject.DB_SELECT, WifiCreateAndParseSockObjectManager.WIFI_INFO_ERROR, info);
				}
			}
			return WifiCreateAndParseSockObjectManager.createWifiStringInfos(AbsWifiCRUDForObject.DB_SELECT, WifiCreateAndParseSockObjectManager.WIFI_INFO_ERROR, info);
		}
		return gson.toJson(obj);
	}

}
