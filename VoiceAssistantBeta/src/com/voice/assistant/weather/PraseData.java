package com.voice.assistant.weather;

 // ID20130724001 chenyuming	begin

import org.json.JSONException;
import org.json.JSONObject;

import com.iii360.base.common.utl.LogManager;

public class PraseData {
    private final static String DEF_VALUE = "N/A";
    private final static String TEMP_FLG = "℃";
    private String mData;

    public PraseData(String data) {
        mData = data;
    }

    public void SetData(String data) {
        mData = data;
    }

    public String getCurrentTemperature() {
        LogManager.d("PraseData", "getCurrentTemperature", "mData:" + mData);
        String temp = DEF_VALUE;
        try {

            JSONObject jObject = new JSONObject(mData);
            if (jObject.has("weatherinfo")) {
                jObject = jObject.getJSONObject("weatherinfo");
                if (jObject.has("temp")) {
                    temp = jObject.getString("temp");
                    if (temp.equals("暂无实况")) {
                        temp = DEF_VALUE;
                    } else {
                        temp += TEMP_FLG;
                    }

                }
            } else {
                return temp;
            }
        } catch (JSONException e) {
            temp = DEF_VALUE;
            LogManager.printStackTrace(e, "PraseData", "getCurrentTemperature");
        } catch (NullPointerException e) {
            temp = DEF_VALUE;
            LogManager.e("PraseData", "getCurrentTemperature", "invaild data");
            LogManager.printStackTrace(e, "PraseData", "getCurrentTemperature");
        }

        return temp;
    }

    public WeatherInfo getWeatherInfo() {
        LogManager.d("PraseData", "getWeatherInfo", "mData:" + mData);

        WeatherInfo weatherInfo = new WeatherInfo();
        try {
            JSONObject jObject = new JSONObject(mData);

            if (jObject.has("weatherinfo")) {
                jObject = jObject.getJSONObject("weatherinfo");

            } else {
                return weatherInfo;
            }

            if (jObject.has("city")) {
                weatherInfo._city = jObject.getString("city");
            }

            if (jObject.has("date_y")) {
                weatherInfo._date = jObject.getString("date_y");
            }

            if (jObject.has("week")) {
                weatherInfo._week = jObject.getString("week");
            }

            if (jObject.has("index_d")) {
                weatherInfo._info = jObject.getString("index_d");
            }
            weatherInfo.ItemInfoList = new WeatherInfo.ItemInfo[6];

            for (int i = 1; i <= 6; i++) {
                String temp = "temp" + i;
                String weather = "weather" + i;
                String wind = "wind" + i;
                String imgType = "img_title" + i;
                weatherInfo.ItemInfoList[i - 1] = new WeatherInfo.ItemInfo();

                if (jObject.has(temp)) {
                    weatherInfo.ItemInfoList[i - 1]._temp = jObject.getString(temp);
                }

                if (jObject.has(weather)) {
                    weatherInfo.ItemInfoList[i - 1]._weatherName = jObject.getString(weather);
                }

                if (jObject.has(wind)) {
                    weatherInfo.ItemInfoList[i - 1]._wind = jObject.getString(wind);
                }

                if (jObject.has(imgType)) {
                    weatherInfo.ItemInfoList[i - 1]._imgType = jObject.getString(imgType);
                }
            }

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            LogManager.printStackTrace(e, "PraseData", "getWeatherInfo");
        } catch (NullPointerException e) {
            LogManager.e("PraseData", "getWeatherInfo", "invaild data");
            LogManager.printStackTrace(e, "PraseData", "getWeatherInfo");
        }

        return weatherInfo;
    }

}
// ID20130724001 chenyuming	end