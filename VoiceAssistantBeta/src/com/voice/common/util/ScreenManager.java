package com.voice.common.util;

import java.util.Stack;

import android.app.Activity;

public class ScreenManager {

    private static Stack<Activity> activityStack;

    private static ScreenManager instance = new ScreenManager();;

    private ScreenManager() {
        activityStack = new Stack<Activity>();
    }

    public static ScreenManager getScreenManager() {
        return instance;

    }

    // 退出栈顶Activity

    public void popActivity(Activity activity) {

        // activity.finish();

        activityStack.remove(activity);

        // activity=null;

    }

    // 获得当前栈顶Activity
    public Activity currentActivity() {
        Activity activity = null;
        if (!activityStack.empty()) {
            activity = (Activity) activityStack.lastElement();
        }
        return activity;

    }

    // 将当前Activity推入栈中
    public void pushActivity(Activity activity) {
        activityStack.add(activity);
    }

    // 退出栈中所有Activity

    public void popAllActivity() {

        while (!activityStack.empty()) {

            Activity activity = currentActivity();
            activity.finish();
            popActivity(activity);

        }

    }

    public boolean isEmpty() {
        return activityStack.empty();
    }

}
