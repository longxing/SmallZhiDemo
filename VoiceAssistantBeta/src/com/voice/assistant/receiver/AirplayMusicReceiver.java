package com.voice.assistant.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.iii360.base.common.utl.BaseContext;
import com.iii360.base.common.utl.KeyList;
import com.voice.assistant.main.MyApplication;

public class AirplayMusicReceiver extends BroadcastReceiver {
    private BaseContext mBaseContext;
    private MyApplication application;
    
    public static final String AIRPLAY_TO_MAIN_PLAY_ACTION = "com.voice.assistant.receiver.AirplayMusicReceiver.play";
    public static final String AIRPLAY_TO_MAIN_PAUSE_ACTION = "com.voice.assistant.receiver.AirplayMusicReceiver.pause";
    public static final String AIRPLAY_TO_MAIN_STOP_ACTION = "com.voice.assistant.receiver.AirplayMusicReceiver.stop";

    @Override
    public void onReceive(Context context, Intent arg1) {
        // TODO Auto-generated method stub
        String action = arg1.getAction();
        application = (MyApplication) context.getApplicationContext();
        mBaseContext = new BaseContext(context);
        

        if (AIRPLAY_TO_MAIN_PLAY_ACTION.equals(action)) {
            application.getUnion().getMediaInterface().pause();
            setPlayStatus(true, true, true);
            stopWakeup();

        } else if (AIRPLAY_TO_MAIN_PAUSE_ACTION.equals(action)) {
            setPlayStatus(true, false, true);
            startWakeup();

        } else if (AIRPLAY_TO_MAIN_STOP_ACTION.equals(action)) {
            setPlayStatus(false, false, false);
            startWakeup();
        }

        
    }

    private void startWakeup() {
        application.getUnion().getRecogniseSystem().startWakeup();
    }

    private void stopWakeup() {
        application.getUnion().getRecogniseSystem().stopWakeup();
    }

    private void setPlayStatus(boolean playing, boolean inPlay, boolean isAirplay) {
        application.getUnion().getBaseContext().setGlobalBoolean(KeyList.GKEY_IS_MUSIC_PLAYING, playing);
        application.getUnion().getBaseContext().setGlobalBoolean(KeyList.GKEY_IS_MUSIC_IN_PLAYING, inPlay);
  //      mBaseContext.setPrefBoolean(Constants.PKEY_CUREENT_MUSIC_IS_AIRPLAY, isAirplay);
    }

}
