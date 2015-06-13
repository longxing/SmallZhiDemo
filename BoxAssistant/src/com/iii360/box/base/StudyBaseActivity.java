package com.iii360.box.base;

import java.util.List;

import android.text.TextUtils;

import com.iii.wifi.dao.info.WifiControlInfo;
import com.iii.wifi.dao.info.WifiDeviceInfo;
import com.iii.wifi.dao.manager.WifiCRUDForControl;
import com.iii.wifi.dao.manager.WifiCRUDForDevice;
import com.iii.wifi.dao.manager.WifiCRUDForWeatherTime.ResultForWeatherTimeListener;
import com.iii.wifi.dao.manager.WifiForCommonOprite;
import com.iii360.box.R;
import com.iii360.box.config.StudyHandler;
import com.iii360.box.util.BoxManagerUtils;
import com.iii360.box.util.LogManager;
import com.iii360.box.util.ToastUtils;
import com.iii360.box.util.WifiCRUDUtil;

/**
 * 暂时没有用
 * 
 * @author hefeng
 * 
 */
public abstract class StudyBaseActivity extends BaseActivity {
    private StudyHandler mHandler;

    public void setHandler(StudyHandler handler) {
        this.mHandler = handler;
    }

    public abstract void getIntentData();

    public abstract void putIntentData();

    public void study(String mDeviceId) {
        LogManager.e("开始采集指令。。。。");
        WifiForCommonOprite wco = new WifiForCommonOprite(getBoxTcpPort(), getBoxIp());
        wco.learnHF(mDeviceId, new ResultForWeatherTimeListener() {
            @Override
            public void onResult(String type, String errorCode, String result) {
                // TODO Auto-generated method stub
                LogManager.i("result learnHF " + result + "||errorCode=" + errorCode);

                if (WifiCRUDUtil.isSuccessAll(errorCode) && !TextUtils.isEmpty(result)) {
                    LogManager.e("采集指令成功 result : " + result);

                    studySuccess(result);
                } else {
                    playTTS(getString(R.string.ba_study_error_toast));
                    ToastUtils.show(context, R.string.ba_study_error_toast);
                    mHandler.sendEmptyMessage(StudyHandler.HANDLER_START_STUDY);
                    studyError();
                }
            }
        });
    }

    public abstract void studySuccess(String result);
    public abstract void studyError();

    public void addControl(final WifiControlInfo controlInfo) {
        final WifiCRUDForControl mControl = new WifiCRUDForControl(context, getBoxIp(), getBoxTcpPort());
        mControl.add(controlInfo, new WifiCRUDForControl.ResultListener() {
            @Override
            public void onResult(String type, String errorCode, List<WifiControlInfo> info) {
                // TODO Auto-generated method stub
                if (WifiCRUDUtil.isSuccess(errorCode)) {
                    LogManager.i("正在保存采集的数据.....保存成功！ ");
                    addControlSuccess(info);
                    
                } else if (WifiCRUDUtil.isExist(errorCode)) {
                    LogManager.i("正在保存采集的数据.....数据存在，更新数据 ");
                    controlInfo.setId(info.get(0).getId()) ;
                    
                    mControl.updata(controlInfo, new WifiCRUDForControl.ResultListener() {
                        @Override
                        public void onResult(String type, String errorCode, List<WifiControlInfo> info) {
                            // TODO Auto-generated method stub
                            if (WifiCRUDUtil.isSuccessAll(errorCode)) {
                                LogManager.i("正在保存采集的数据.....保存成功！ ");

                                addControlSuccess(info);

                            } else {
                                LogManager.i("正在保存采集的数据.....保存错误！ ");
                                mHandler.sendEmptyMessage(StudyHandler.HANDLER_START_STUDY);
                                playTTS(getString(R.string.ba_study_error_toast));
                            }
                        }
                    });
                    addControlError();
                } else {
                    addControlError();
                    LogManager.i("正在保存采集的数据.....保存错误！ ");
                    mHandler.sendEmptyMessage(StudyHandler.HANDLER_START_STUDY);
                    playTTS(getString(R.string.ba_study_error_toast));
                }

            }
        });
    }
    
    public void addStudyErrorControl(final WifiControlInfo controlInfo) {
        final WifiCRUDForControl mControl = new WifiCRUDForControl(context, getBoxIp(), getBoxTcpPort());
        mControl.add(controlInfo, new WifiCRUDForControl.ResultListener() {
            @Override
            public void onResult(String type, String errorCode, List<WifiControlInfo> info) {
                // TODO Auto-generated method stub
                addControlError();
                if (WifiCRUDUtil.isSuccess(errorCode)) {
                    LogManager.i("正在保存采集失败的数据.....保存成功！ ");
                    
                } else if (WifiCRUDUtil.isExist(errorCode)) {
                    LogManager.i("正在保存采集失败的数据.....数据已经存在！ ");
//                    LogManager.i("正在保存采集失败的数据.....数据存在，更新数据 ");
//                    mControl.updata(controlInfo, new WifiCRUDForControl.ResultListener() {
//                        @Override
//                        public void onResult(String type, String errorCode, List<WifiControlInfo> info) {
//                            // TODO Auto-generated method stub
//                            if (WifiCRUDUtil.isSuccessAll(errorCode)) {
//                                LogManager.i("正在保存采集失败的数据.....保存成功！ ");
//
//                            } else {
//                                LogManager.i("正在保存采集失败的数据.....保存错误！ ");
//                            }
//                        }
//                    });
                } else {
                    LogManager.i("正在保存采集失败的数据.....保存错误！ ");
                }
            }
        });
    }

    public abstract void addControlSuccess(List<WifiControlInfo> list);
    public void addControlError(){
        
    }

    public void selectDevice(final String deviceId) {
        WifiCRUDForDevice mWifiCRUDForDevice = new WifiCRUDForDevice(context, getBoxIp(), getBoxTcpPort());
        mWifiCRUDForDevice.seleteByDeviceId(deviceId, new WifiCRUDForDevice.ResultListener() {
            @Override
            public void onResult(String type, String errorCode, List<WifiDeviceInfo> info) {
                // TODO Auto-generated method stub

                LogManager.i("StudyBaseActivity selectDevice id=" + deviceId + "||errorCode=" + errorCode);
                if (WifiCRUDUtil.isSuccessAll(errorCode) && info != null && !info.isEmpty()) {
                    selectDeviceSuccess(info);
                } else {
                    ToastUtils.show(context, R.string.ba_config_box_info_error_toast);
                }
            }
        });
    }

    public abstract void selectDeviceSuccess(List<WifiDeviceInfo> list);

    public void updateControl(WifiControlInfo info) {
        WifiCRUDForControl mControl = new WifiCRUDForControl(context, getBoxIp(), getBoxTcpPort());
        mControl.updata(info, new WifiCRUDForControl.ResultListener() {
            @Override
            public void onResult(String type, String errorCode, List<WifiControlInfo> info) {
                // TODO Auto-generated method stub
                if (WifiCRUDUtil.isSuccessAll(errorCode)) {
                    LogManager.i("delete command success");

                    ToastUtils.show(context, R.string.ba_delete_success_toast);
                    mHandler.sendEmptyMessage(StudyHandler.HANDLER_START_STUDY);
                } else {
                    LogManager.i("delete command error");

                    ToastUtils.show(context, R.string.ba_delete_data_error_toast);
                }
            }
        });
    }

    public void playTTS(String content) {
        WifiForCommonOprite oprite = new WifiForCommonOprite(BoxManagerUtils.getBoxTcpPort(context), BoxManagerUtils.getBoxIP(context));
        oprite.playTTS(content, new ResultForWeatherTimeListener() {
            @Override
            public void onResult(String type, String errorCode, String result) {
                // TODO Auto-generated method stub

                if (!WifiCRUDUtil.isSuccessAll(errorCode)) {
                    LogManager.e("tts error");
                    ToastUtils.show(context, R.string.ba_tts_error);
                }
            }
        });
    }
}
