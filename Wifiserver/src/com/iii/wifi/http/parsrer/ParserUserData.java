package com.iii.wifi.http.parsrer;

import org.json.JSONException;
import org.json.JSONObject;

import android.text.TextUtils;

import com.iii.wifi.dao.info.WifiUserData;
import com.iii360.sup.common.utl.LogManager;

public class ParserUserData implements ParserData {
	public final static String[] MARRIAGE_ARRAY = { "保密", "已婚", "未婚" };

	// {
	// "boxId": "null",
	// "birth": "1986-03-03",
	// "imei": "867936010109999",
	// "mac": "00118d00890f",
	// "label": null,
	// "marriage": "否",
	// "version": "1",
	// "city": "上海市",
	// "child": "否",
	// "bloodType": "O",
	// "eduBackGround": "博士",
	// "brand": "Coolgen Coolgen E71 HD",
	// "gender": "男"
	// }
	@Override
	public WifiUserData getParserData(String json) throws JSONException {
		// TODO Auto-generated method stub
		LogManager.e(json);
//		json = "{\"age\":\"\",\"constellation\":\"null\",\"boxId\":\"TERRY0000010\",\"birth\":\"1990-01-01\",\"imei\":\"TERRY0000010\",\"mac\":\"000822beaffb\",\"label\":null,\"marriage\":\"N\",\"version\":\"1\",\"city\":\"上海市\",\"child\":\"无\",\"bloodType\":\"N\",\"eduBackGround\":\"大专\",\"brand\":\"Huawei H30-T00\",\"gender\":\"男\"}";
		WifiUserData user = new WifiUserData();
		JSONObject obj = new JSONObject(json);
		try {
			String birth = obj.get("birth") + "";
			if (!isEmpty(birth))
				user.setBirth(birth);
		} catch (Exception e) {
			e.printStackTrace();
		}
		String marriage = obj.getString("marriage");

		if (null != marriage) {

			if (marriage.equals("N")) {
				marriage = MARRIAGE_ARRAY[0];

			} else if (marriage.equals("是")) {
				marriage = MARRIAGE_ARRAY[1];

			} else if (marriage.equals("否")) {
				marriage = MARRIAGE_ARRAY[2];

			}

		}
		String child = obj.getString("child");
		String booldType = obj.getString("bloodType");

		if (booldType != null && booldType.equals("N")) {
			booldType = "未知";
		}

		String eduBackGround = obj.getString("eduBackGround");
		String gender = obj.getString("gender");

		user.setSex(gender);
		user.setEducation(eduBackGround);
		user.setMarriage(marriage);
		user.setChildren(child);
		user.setBoold(booldType);
		try {
			String age = obj.get("age") + "";
			if (!isEmpty(age))
				user.setAge(age);
		} catch (Exception e) {
		}
		try {
			String constellation = obj.get("constellation") + "";
			if (!isEmpty(constellation))
				user.setConstellation(constellation);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return user;
	}

	public boolean isEmpty(String str) {
		if (str == null || "".equals(str.trim()) || "null".equals(str.trim()))
			return true;
		return false;
	}
}
