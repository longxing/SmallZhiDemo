package com.iii360.box.util;

import android.content.Context;
import android.telephony.TelephonyManager;

public class PhoneInfoUtils {
    /**
     * @param context
     * @return 手机品牌
     */
    public static String getBrand() {
        return android.os.Build.BRAND;
    }

    /**
     * @param context
     * @return 手机型号
     */
    public static String getModel() {
        return android.os.Build.MODEL;
    }

    public static String getIMEI(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

        return telephonyManager.getDeviceId();
    }

    public static String getBrandModel() {
        return getBrand() + " " + getModel();
    }

}
