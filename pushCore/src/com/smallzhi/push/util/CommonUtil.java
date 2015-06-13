package com.smallzhi.push.util;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.smallzhi.push.entity.Message;

import android.content.Context;
import android.telephony.TelephonyManager;

/**
 * @description : TODO
 * @author : Tart
 * @date : 2014-11-17 下午6:59:03
 * @version :  1.0
 */
public class CommonUtil {
	public static Gson gson = new Gson();
	public static String getIMEI(Context context)
	{
		return ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
		
	}

	/**
	 * @author tart
	 * @date 2014-11-17 下午7:01:27
	 * @param @return
	 * @return Message
	 * @throws
	 */
	public static Message createHeartBeat(Context ctx) {
		Message msg = new Message();
		msg.setType(PushConstant.TYPE_REQUEST);
		msg.setAction(PushConstant.ACTION_HEART_BEAT);
		msg.setSender(getIMEI(ctx));
		msg.setTimestamp(new Date());
		return msg;
	}

	public static Message createBind(Context ctx) {
		Message msg = new Message();
		msg.setType(PushConstant.TYPE_REQUEST);
		msg.setAction(PushConstant.ACTION_BIND);
		//获取sessionKey
		msg.setSender(DataConfig.getString(ctx, DataConfig.SESSION_KEY));
		msg.setTimestamp(new Date());
		Map<String,String> remarkMap = new HashMap<String,String>();
    	String hardware = DataConfig.getString(ctx, DataConfig.HARDWARE_VERSION);
    	String software = DataConfig.getString(ctx, DataConfig.SOFTWARE_VERSION);
		remarkMap.put(DataConfig.HARDWARE_VERSION, hardware);
		remarkMap.put(DataConfig.SOFTWARE_VERSION, software);
		msg.setRemarkMap(remarkMap);
		return msg;
	}
}
