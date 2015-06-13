package com.base.app;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;


import com.base.util.KeyManager;
import com.iii360.sup.common.utl.LogManager;

public class AppsManager {

    public static boolean isUserApp(Context context, String packageName) {
//        BaseContext baseContext = new BaseContext(context);
//
//        @SuppressWarnings("unchecked")
//        ArrayList<AppInfo> appUserList = (ArrayList<AppInfo>) baseContext.getGlobalObject(KeyList.GKEY_OBJ_USER_APPS);
//        if(appUserList != null) {
//            for(AppInfo info : appUserList) {
//                if(info._package.equals(packageName)) {
//                    return true;
//                }
//            }
//        }


        return true;

    }

    public static String getMetaStringData(Context context, String packageName, String key) {
        String data = null;

        if(packageName == null || packageName.equals("")) {
            return data;
        }

        try {
            ApplicationInfo appInfo = context.getPackageManager()
                    .getApplicationInfo(packageName, PackageManager.GET_META_DATA);
            data = appInfo.metaData.getString(key);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            LogManager.printStackTrace(e);
        }
        return data;
    }


    public static String getMetaStringData(Context context, String key) {

        return getMetaStringData(context, context.getPackageName(), key);
    }

    public static String getAppId(Context context) {
        return "com.voice.assistant.hezi";
    }

    public static String getRobotId(Context context, String packageName) {

        return getMetaStringData(context, packageName, KeyManager.MEKEY_STR_ROBOTID);
    }

    public static void init(Context context) {


    }

    public static String getRobotId(Context context) {
        // TODO Auto-generated method stub
        return getRobotId(context, context.getPackageName());
    }
    
    public static String getServerAddress(Context context) {
        return getMetaStringData(context, context.getPackageName(), KeyManager.MEKEY_STR_SERVER_ADDRESS);
    }

}
