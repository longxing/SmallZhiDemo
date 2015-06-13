package com.iii360.box.util;

import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;

import com.iii360.box.MyService;

public class AppUtils {
    /**
     * 判断当前应用程序处于前台还是后台
     * 
     * @param context
     * 
     * @return
     */
    public static boolean isAppToBackground(final Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningTaskInfo> tasks = am.getRunningTasks(1);
        if (!tasks.isEmpty()) {
            ComponentName topActivity = tasks.get(0).topActivity;
            if (!topActivity.getPackageName().equals(context.getPackageName())) {
                return true;
            }
        }
        return false;

    }

    /**
     * 用来判断服务是否运行.
     * 
     * @param context
     * @param className
     *            判断的服务名字
     * @return true 在运行 false 不在运行
     */
    public static boolean isServiceRunning(Context mContext, String className) {
        boolean isRunning = false;
        ActivityManager activityManager = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> serviceList = activityManager.getRunningServices(30);
        if (!(serviceList.size() > 0)) {
            return false;
        }
        for (int i = 0; i < serviceList.size(); i++) {
            if (serviceList.get(i).service.getClassName().equals(className) == true) {
                isRunning = true;
                break;
            }
        }
        return isRunning;
    }
    
    public static void repeat(Context context,String action) {
        // 启动完成  
        Intent intent = new Intent(context,MyService.class);
//        intent.setAction(action);
        PendingIntent sender = PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        long firstime = SystemClock.elapsedRealtime();
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        // 10秒一个周期，不停的发送广播  
        am.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, firstime,  1000, sender);
    }
}
