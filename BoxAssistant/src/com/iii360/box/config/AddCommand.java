package com.iii360.box.config;

import java.util.List;

import android.content.Context;
import android.text.TextUtils;

import com.iii.wifi.dao.info.WifiControlInfo;
import com.iii.wifi.dao.info.WifiDeviceInfo;
import com.iii.wifi.dao.manager.WifiCRUDForControl;
import com.iii.wifi.dao.manager.WifiCRUDForDevice;
import com.iii.wifi.dao.manager.WifiCRUDForDevice.ResultListener;
import com.iii.wifi.dao.manager.WifiCRUDForWeatherTime.ResultForWeatherTimeListener;
import com.iii.wifi.dao.manager.WifiForCommonOprite;
import com.iii360.box.R;
import com.iii360.box.util.DataUtil;
import com.iii360.box.util.KeyList;
import com.iii360.box.util.LogManager;
import com.iii360.box.util.ToastUtils;
import com.iii360.box.util.WifiCRUDUtil;

public class AddCommand {
    private Context context;
    private String ip;
    private int port;
    private String mDeviceId;
    private int mControlId = -1;

    /**
     * 直接点击跳过，不学习指令
     */
    private boolean isNext = false;
    /**
     * 是否在学习指令
     */
    private boolean isStudy = false;

    public boolean isStudy() {
        return isStudy;
    }

    public void setStudy(boolean isStudy) {
        this.isStudy = isStudy;
    }

    public boolean isNext() {
        return isNext;
    }

    public void setNext(boolean isNext) {
        this.isNext = isNext;
    }

    /**
     * 1.先添加到设备中；2.学习指令；3.添加控制数据,保存指令
     * 
     * @param context
     * @param ip
     * @param port
     */
    public AddCommand(Context context, String ip, int port) {
        // TODO Auto-generated constructor stub
        this.context = context;
        this.ip = ip;
        this.port = port;
    }

    private WifiDeviceInfo mWifiDeviceInfo;
    private StudyHandler mHandler;

    public void excute(StudyHandler mStudyHandler, WifiDeviceInfo info) {
        this.mHandler = mStudyHandler;
        this.mWifiDeviceInfo = info;
        addDevice();
    }

    public void addDevice() {
        setStudy(true);
        if (TextUtils.isEmpty(mDeviceId)) {

            WifiCRUDForDevice mWifiCRUDForDevice = new WifiCRUDForDevice(context, ip, port);
            mWifiCRUDForDevice.add(mWifiDeviceInfo, new ResultListener() {
                @Override
                public void onResult(String type, String errorCode, List<WifiDeviceInfo> info) {
                    // TODO Auto-generated method stub
                    LogManager.i("add device air errorCode=" + errorCode);
                    if (WifiCRUDUtil.isSuccessAll(errorCode)) {
                        mDeviceId = info.get(0).getDeviceid();
                        mWifiDeviceInfo.setDeviceid(mDeviceId);
                        LogManager.i("create air device success mDeviceId=" + mDeviceId);
                        addStudy(mDeviceId, !isNext);
                    } else {
                        LogManager.i("create air device error");
                        ToastUtils.show(context, R.string.ba_add_device_error_toast);
                        mHandler.sendEmptyMessage(StudyHandler.HANDLER_START_STUDY);
                    }
                }
            });

        } else {

            addStudy(mDeviceId, !isNext);
        }
    }

    public void addControl(String command) {
        final WifiControlInfo controlInfo = new WifiControlInfo();
        controlInfo.setDorder(command);
        controlInfo.setRoomId(mWifiDeviceInfo.getRoomid());
        controlInfo.setDeviceid(mDeviceId);
        controlInfo.setDeviceModel(mWifiDeviceInfo.getDeviceModel());
        controlInfo.setAction(DataUtil.formatAction(KeyList.GKEY_OPERATION_DEVICE_ARRAY[1], mWifiDeviceInfo.getDeviceName()));

        final WifiCRUDForControl mControl = new WifiCRUDForControl(context, ip, port);

        mControl.add(controlInfo, new WifiCRUDForControl.ResultListener() {
            @Override
            public void onResult(String type, String errorCode, List<WifiControlInfo> info) {
                // TODO Auto-generated method stub
                setStudy(false);
                if (WifiCRUDUtil.isSuccess(errorCode)) {
                    LogManager.i("正在保存采集的数据.....保存成功！ ");
                    mControlId = info.get(0).getId();
                    if (!isNext) {
                        mHandler.sendEmptyMessage(StudyHandler.HANDLER_STUDY_SUCCESS_UNCLICK);
                        WifiCRUDUtil.playTTS(context, context.getResources().getString(R.string.ba_study_success_tts));
                    }

                } else if (WifiCRUDUtil.isExist(errorCode)) {
                    LogManager.i("正在保存采集的数据.....数据存在，更新数据 ");
                    mControlId = info.get(0).getId();
                    controlInfo.setId(mControlId);

                    mControl.updata(controlInfo, new WifiCRUDForControl.ResultListener() {
                        @Override
                        public void onResult(String type, String errorCode, List<WifiControlInfo> info) {
                            // TODO Auto-generated method stub
                            if (WifiCRUDUtil.isSuccessAll(errorCode)) {
                                LogManager.i("正在保存采集的数据.....保存成功！ ");

                                mControlId = info.get(0).getId();
                                if (!isNext) {
                                    mHandler.sendEmptyMessage(StudyHandler.HANDLER_STUDY_SUCCESS_UNCLICK);
                                    WifiCRUDUtil.playTTS(context, context.getResources().getString(R.string.ba_study_success_tts));
                                }

                            } else {
                                LogManager.i("正在保存采集的数据.....保存错误！ ");
                                LogManager.i("正在保存采集的数据.....保存错误！ ");

                                if (!isNext) {
                                    mHandler.sendEmptyMessage(StudyHandler.HANDLER_START_STUDY);
                                    WifiCRUDUtil.playTTS(context, context.getResources().getString(R.string.ba_study_error_toast));
                                }
                            }
                        }
                    });

                } else {
                    LogManager.i("正在保存采集的数据.....保存错误！ ");

                    if (!isNext) {
                        mHandler.sendEmptyMessage(StudyHandler.HANDLER_START_STUDY);
                        WifiCRUDUtil.playTTS(context, context.getResources().getString(R.string.ba_study_error_toast));
                    }
                }

            }
        });
    }

    public void updateControl(String command) {
        WifiControlInfo controlInfo = new WifiControlInfo();
        controlInfo.setId(mControlId);
        controlInfo.setDorder(command);
        controlInfo.setRoomId(mWifiDeviceInfo.getRoomid());
        controlInfo.setDeviceid(mDeviceId);
        controlInfo.setDeviceModel(mWifiDeviceInfo.getDeviceModel());
        controlInfo.setAction(DataUtil.formatAction(KeyList.GKEY_OPERATION_DEVICE_ARRAY[1], mWifiDeviceInfo.getDeviceName()));

        WifiCRUDForControl mControl = new WifiCRUDForControl(context, ip, port);
        mControl.updata(controlInfo, new WifiCRUDForControl.ResultListener() {
            @Override
            public void onResult(String type, String errorCode, List<WifiControlInfo> info) {
                // TODO Auto-generated method stub
                if (WifiCRUDUtil.isSuccessAll(errorCode)) {
                    LogManager.i("正在保存采集的数据.....保存成功！ ");
                    ToastUtils.show(context, R.string.ba_delete_success_toast);
                    mHandler.sendEmptyMessage(StudyHandler.HANDLER_START_STUDY);

                } else {
                    LogManager.i("正在保存采集的数据.....保存错误！ ");
                    ToastUtils.show(context, R.string.ba_delete_data_error_toast);
                }

            }
        });
    }

    public void addStudy(String mDeviceId, boolean isStudy) {
        if (isStudy) {
            setStudy(true);
            LogManager.e("开始采集指令。。。。");
            WifiForCommonOprite wco = new WifiForCommonOprite(port, ip);
            wco.learnHF(mDeviceId, new ResultForWeatherTimeListener() {
                @Override
                public void onResult(String type, String errorCode, String result) {
                    // TODO Auto-generated method stub
                    setStudy(false);
                    LogManager.i("result learnHF " + result + "||errorCode=" + errorCode);
                    if (WifiCRUDUtil.isSuccessAll(errorCode) && !TextUtils.isEmpty(result)) {
                        LogManager.e("采集指令成功 result : " + result);
                        addControl(result);
                    } else {
                        WifiCRUDUtil.playTTS(context, context.getResources().getString(R.string.ba_study_error_toast));
                        ToastUtils.show(context, R.string.ba_study_error_toast);
                        mHandler.sendEmptyMessage(StudyHandler.HANDLER_START_STUDY);
                    }
                }
            });
        } else {
            addControl(null);
        }
    }
}
