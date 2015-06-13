package com.smallzhi.TTS.Main;



import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.iii360.base.common.utl.BaseContext;
import com.iii360.base.common.utl.LogManager;


public class VoiceStateReceiver extends BroadcastReceiver {

    private BaseContext mBaseContext;

    private OnStateChange mOnStateChange;

    public interface OnStateChange {
        public void onChange(int type);
    }

    public void setOnStateChange(OnStateChange onStateChange) {
        mOnStateChange = onStateChange;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        mBaseContext = new BaseContext(context);
        if (action.equals(BluetoothDevice.ACTION_ACL_CONNECTED)) {
            LogManager.e("ACTION_ACL_CONNECTED");
            // 0 means phone type 3 means music type
            mBaseContext.setPrefInteger("OUT_PUT_SPEECH_TYPE", 0);
            if (mOnStateChange != null) {
                mOnStateChange.onChange(0);
            }

        } else if (action.equals(BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED)) {
            LogManager.e("ACTION_ACL_DISCONNECT_REQUESTED");
            mBaseContext.setPrefInteger("OUT_PUT_SPEECH_TYPE", 3);
            if (mOnStateChange != null) {
                mOnStateChange.onChange(3);
            }

        } else if (action.equals(BluetoothDevice.ACTION_ACL_DISCONNECTED)) {
            LogManager.e("ACTION_ACL_DISCONNECTED");
            mBaseContext.setPrefInteger("OUT_PUT_SPEECH_TYPE", 3);
            if (mOnStateChange != null) {
                mOnStateChange.onChange(3);
            }

        }
    }
}
