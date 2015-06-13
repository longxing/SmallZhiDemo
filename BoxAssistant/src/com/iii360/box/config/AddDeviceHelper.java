package com.iii360.box.config;

import java.util.List;

import android.content.Context;

import com.iii.wifi.dao.info.WifiControlInfo;
import com.iii.wifi.dao.manager.WifiCRUDForControl;
import com.iii360.box.R;
import com.iii360.box.util.BoxManagerUtils;
import com.iii360.box.util.LogManager;
import com.iii360.box.util.ToastUtils;
import com.iii360.box.util.WifiCRUDUtil;

public class AddDeviceHelper {
    private Context context;

    private IResultListener resultListener;

    public void setResultListener(IResultListener resultListener) {
        this.resultListener = resultListener;
    }

    public interface IResultListener {
        public void onResult(WifiControlInfo info);
    }

    public AddDeviceHelper(Context context) {
        // TODO Auto-generated constructor stub
        this.context = context;
    }

    public void addControl(final WifiControlInfo controlInfo) {
        LogManager.d("roomId=" + controlInfo.getRoomId() + "||deviceId" + controlInfo.getDeviceid() + "||action=" + controlInfo.getAction()
                + "||command=" + controlInfo.getDorder());

        final WifiCRUDForControl wControl = new WifiCRUDForControl(context, BoxManagerUtils.getBoxIP(context), BoxManagerUtils.getBoxTcpPort(context));
        wControl.add(controlInfo, new WifiCRUDForControl.ResultListener() {
            @Override
            public void onResult(String type, String errorCode, List<WifiControlInfo> list) {
                // TODO Auto-generated method stub
                if (WifiCRUDUtil.isSuccess(errorCode)) {
                    LogManager.d("采集数据保存成功。。。");

                    if (resultListener != null && list != null && !list.isEmpty()) {
                        resultListener.onResult(list.get(0));
                    }

                } else if (WifiCRUDUtil.isExist(errorCode)) {
                    LogManager.d("采集数据control已经存在。。。");

                    wControl.updata(controlInfo, new WifiCRUDForControl.ResultListener() {
                        @Override
                        public void onResult(String type, String errorCode, List<WifiControlInfo> list2) {
                            // TODO Auto-generated method stub
                            if (WifiCRUDUtil.isSuccess(errorCode)) {

                                LogManager.d("采集数据保存成功。。。");

                                if (resultListener != null && list2 != null && !list2.isEmpty()) {
                                    resultListener.onResult(list2.get(0));
                                }
                            } else {
                                LogManager.d("采集数据保存失败。。。");
                            }
                        }
                    });

                } else {
                    LogManager.d("采集数据保存失败。。。");
                    ToastUtils.show(context, R.string.ba_config_box_info_error_toast);
                }
            }
        });
    }

    public void updateControl(WifiControlInfo controlInfo) {
        WifiCRUDForControl wControl = new WifiCRUDForControl(context, BoxManagerUtils.getBoxIP(context), BoxManagerUtils.getBoxTcpPort(context));
        wControl.updata(controlInfo, new WifiCRUDForControl.ResultListener() {
            @Override
            public void onResult(String type, String errorCode, List<WifiControlInfo> list2) {
                // TODO Auto-generated method stub
                if (WifiCRUDUtil.isSuccess(errorCode)) {

                    LogManager.d("删除数据保存成功。。。");
                    ToastUtils.show(context, R.string.ba_delete_success_toast);

                    if (resultListener != null && list2 != null && !list2.isEmpty()) {
                        resultListener.onResult(list2.get(0));
                    }
                } else {
                    LogManager.d("删除数据保存失败。。。");

                    ToastUtils.show(context, R.string.ba_delete_data_error_toast);
                }
            }
        });

    }
}
