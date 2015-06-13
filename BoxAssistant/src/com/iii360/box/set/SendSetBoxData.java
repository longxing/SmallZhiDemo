package com.iii360.box.set;

import android.content.Context;

import com.iii.wifi.dao.manager.WifiCRUDForLedStatus;
import com.iii.wifi.dao.manager.WifiCRUDForLedStatus.ResultForLedListener;
import com.iii.wifi.dao.manager.WifiCRUDForLedTime;
import com.iii.wifi.dao.manager.WifiCRUDForLedTime.ResultForLedTimeListener;
import com.iii.wifi.dao.manager.WifiCRUDForTTS;
import com.iii.wifi.dao.manager.WifiCRUDForTTS.ResultForTTSListener;
import com.iii.wifi.dao.manager.WifiCRUDForWeatherStatus;
import com.iii.wifi.dao.manager.WifiCRUDForWeatherStatus.ResultForWeatherListener;
import com.iii.wifi.dao.manager.WifiCRUDForWeatherTime;
import com.iii.wifi.dao.manager.WifiCRUDForWeatherTime.ResultForWeatherTimeListener;
import com.iii360.box.util.BoxManagerUtils;
import com.iii360.box.util.LogManager;
import com.iii360.box.util.WifiCRUDUtil;

public class SendSetBoxData extends AbsBoxData {

    public SendSetBoxData(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
    }

    @Override
    public void sendTtsPeople(final String voiceMan) {
        // TODO Auto-generated method stub
        LogManager.d("SET_VOICE_TTS_START------voiceMan=" + voiceMan);

        final WifiCRUDForTTS ttsMan = new WifiCRUDForTTS(context, BoxManagerUtils.getBoxIP(context), BoxManagerUtils.getBoxTcpPort(context));
        ttsMan.add(voiceMan, new ResultForTTSListener() {
            @Override
            public void onResult(String type, String errorCode, String ttsName) {
                // TODO Auto-generated method stub
                if (WifiCRUDUtil.isSuccess(errorCode)) {
                    //发送发音人成功
                    LogManager.i("SET_VOICE_TTS_SUCCESS");

                } else if (WifiCRUDUtil.isExist(errorCode)) {
                    LogManager.i("SET_VOICE_TTS_EXIST");

                    ttsMan.updata(voiceMan, new ResultForTTSListener() {
                        @Override
                        public void onResult(String type, String errorCode, String ttsName) {
                            // TODO Auto-generated method stub
                            if (WifiCRUDUtil.isSuccessAll(errorCode)) {
                                LogManager.i("SET_VOICE_TTS_SUCCESS");
                                //setLedSwitch();

                            } else {
                                LogManager.i("SET_VOICE_TTS_ERROR");
                            }
                        }
                    });
                } else {
                    LogManager.i("SET_VOICE_TTS_ERROR");
                }
            }
        });
    }
     /**
      * @deprecated
      */
    @Override
    public void sendLedSwitch(final String ledSwtich) {
        // TODO Auto-generated method stub
        LogManager.d("SET_LED_SWITCH------ledSwtich=" + ledSwtich);
        final WifiCRUDForLedStatus ledStatus = new WifiCRUDForLedStatus(context, BoxManagerUtils.getBoxIP(context), BoxManagerUtils.getBoxTcpPort(context));
        ledStatus.add(ledSwtich, new ResultForLedListener() {
            @Override
            public void onResult(String type, String errorCode, String status) {
                // TODO Auto-generated method stub
                if (WifiCRUDUtil.isSuccess(errorCode)) {
                    LogManager.i("SET_LED_SWITCH_SUCCESS");

                } else if (WifiCRUDUtil.isExist(errorCode)) {
                    LogManager.i("SET_LED_SWITCH_EXIST");

                    ledStatus.updata(ledSwtich, new ResultForLedListener() {
                        @Override
                        public void onResult(String type, String errorCode, String ledStatus) {
                            // TODO Auto-generated method stub
                            if (WifiCRUDUtil.isSuccessAll(errorCode)) {
                                LogManager.i("SET_LED_SWITCH_SUCCESS");

                            } else {
                                LogManager.i("SET_VLED_SWITCH_ERROR");
                            }
                        }
                    });

                } else {
                    LogManager.i("SET_LED_SWITCH_ERROR");
                }
            }
        });
    }

    @Override
    public void sendLedTime(final String ledTime,final boolean isOpen) {
        // TODO Auto-generated method stub
        LogManager.d("SET_LED_TIME_START------ledTime=" + ledTime);
        final WifiCRUDForLedTime wLedTime = new WifiCRUDForLedTime(context, BoxManagerUtils.getBoxIP(context), BoxManagerUtils.getBoxTcpPort(context));
        wLedTime.add(ledTime,isOpen,new ResultForLedTimeListener() {
            @Override
            public void onResult(String type, String errorCode, String ledName) {
                // TODO Auto-generated method stub
                if (WifiCRUDUtil.isSuccess(errorCode)) {
                    //发送led时间状态成功
                    LogManager.i("SET_LED_TIME_SUCCESS");

                } else if (WifiCRUDUtil.isExist(errorCode)) {
                    LogManager.i("SET_LED_TIME_EXIST");
                    wLedTime.updata(ledTime,isOpen,new ResultForLedTimeListener() {
                        @Override
                        public void onResult(String type, String errorCode, String ledName) {
                            // TODO Auto-generated method stub
                            if (WifiCRUDUtil.isSuccessAll(errorCode)) {
                                LogManager.i("SET_LED_TIME_SUCCESS");

                            } else {
                                LogManager.i("SET_LED_TIME_ERROR");
                            }
                        }
                    });

                } else {
                    LogManager.i("SET_LED_TIME_ERROR");
                }
            }
        });
    }
    /**
     * @deprecated
     */
    @Override
    public void sendWeatherSwitch(final String weatherSwitch) {
        // TODO Auto-generated method stub
        LogManager.d("SET_WEATHER_SWITCH_START------weatherSwitch=" + weatherSwitch);
        final WifiCRUDForWeatherStatus weatherStatus = new WifiCRUDForWeatherStatus(context, BoxManagerUtils.getBoxIP(context),
                BoxManagerUtils.getBoxTcpPort(context));
        weatherStatus.add(weatherSwitch, new ResultForWeatherListener() {
            @Override
            public void onResult(String type, String errorCode, String weatherStatuss) {
                // TODO Auto-generated method stub
                if (WifiCRUDUtil.isSuccess(errorCode)) {
                    //发送天气播报时间开关成功
                    LogManager.i("SET_WEATHER_SWITCH_SUCCESS");

                } else if (WifiCRUDUtil.isExist(errorCode)) {
                    LogManager.d("SET_WEATHER_SWITCH_EXIST");
                    weatherStatus.updata(weatherSwitch, new ResultForWeatherListener() {
                        @Override
                        public void onResult(String type, String errorCode, String weatherStatus) {
                            // TODO Auto-generated method stub
                            if (WifiCRUDUtil.isSuccessAll(errorCode)) {
                                //发送天气播报时间开关成功
                                LogManager.i("SET_WEATHER_SWITCH_SUCCESS");

                            } else {
                                LogManager.i("SET_WEATHER_SWITCH_ERROR");
                            }
                        }
                    });

                } else {
                    LogManager.i("SET_WEATHER_SWITCH_ERROR");
                }
            }
        });

    }

    @Override
    public void sendWeatherTime(final String weatherTime,final boolean isOpen) {
        // TODO Auto-generated method stub
        LogManager.d("SET_WEATHER_TIME_START------weatherTime=" + weatherTime);
        final WifiCRUDForWeatherTime wTime = new WifiCRUDForWeatherTime(context, BoxManagerUtils.getBoxIP(context), BoxManagerUtils.getBoxTcpPort(context));
        wTime.add(weatherTime,isOpen, new ResultForWeatherTimeListener() {
            @Override
            public void onResult(String type, String errorCode, String weatherTime) {
                // TODO Auto-generated method stub
                if (WifiCRUDUtil.isSuccess(errorCode)) {
                    //发送天气播报时间状态成功
                    LogManager.i("SET_WEATHER_TIME_SUCCESS");

                } else if (WifiCRUDUtil.isExist(errorCode)) {
                    LogManager.d("SET_WEATHER_TIME_EXIST");
                    wTime.updata(weatherTime,isOpen, new ResultForWeatherTimeListener() {
                        @Override
                        public void onResult(String type, String errorCode, String weatherTime) {
                            // TODO Auto-generated method stub
                            if (WifiCRUDUtil.isSuccessAll(errorCode)) {
                                //发送天气播报时间状态成功
                                LogManager.i("SET_WEATHER_TIME_SUCCESS");

                            } else {
                                LogManager.i("SET_WEATHER_TIME_ERROR");
                            }
                        }
                    });
                } else {
                    LogManager.i("SET_WEATHER_TIME_ERROR");
                }
            }
        });
    }

}
