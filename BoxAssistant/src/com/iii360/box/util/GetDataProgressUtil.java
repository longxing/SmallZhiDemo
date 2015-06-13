package com.iii360.box.util;

import android.content.Context;
import android.os.Handler;

import com.iii360.box.R;
import com.iii360.box.view.MyProgressDialog;

public class GetDataProgressUtil {
    private static MyProgressDialog mProgressDialog;

    /**
     * 刷新数据进度对话框
     * @param context
     */
    public static void showGettingProgress(final Context context) {
        new Handler(context.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                mProgressDialog = new MyProgressDialog(context);
                mProgressDialog.setMessage(context.getResources().getString(R.string.ba_update_date));
                mProgressDialog.show();
            }
        });
    }
    
    /**
     * 设置数据对话框
     * @param context
     */
    public static void showSettingProgress(final Context context) {
        new Handler(context.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                mProgressDialog = new MyProgressDialog(context);
                mProgressDialog.setMessage(context.getResources().getString(R.string.ba_setting_toast));
                mProgressDialog.show();
            }
        });
    }

    /**
     * 隐藏对话框
     * @param context
     */
    public static void dismissProgress(Context context) {
        new Handler(context.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                if (mProgressDialog != null) {
                    mProgressDialog.dismiss();
                }
            }
        });
    }
}
