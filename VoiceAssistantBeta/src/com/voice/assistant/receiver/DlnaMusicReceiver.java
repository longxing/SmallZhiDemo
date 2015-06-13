package com.voice.assistant.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.iii360.base.common.utl.BaseContext;
import com.iii360.sup.common.utl.LogManager;
import com.voice.assistant.main.KeyList;
import com.voice.assistant.main.MyApplication;
import com.voice.assistant.main.music.MediaPlayerService;
import com.voice.assistant.main.music.MyMusicHandler;

/**
 * 从dlna发送广播来控制音乐和相关逻辑
 * 
 * @author hank
 * 
 */

public class DlnaMusicReceiver extends BroadcastReceiver {
    public static final String DLAN_MUSIC_TO_MAIN_PLAY_ACTION = "com.voice.assistant.DlnaMusicReceiver.play";
    public static final String DLAN_MUSIC_TO_MAIN_PAUSE_ACTION = "com.voice.assistant.DlnaMusicReceiver.pause";
    public static final String DLAN_MUSIC_TO_MAIN_STOP_ACTION = "com.voice.assistant.DlnaMusicReceiver.stop";
    public static final String DLAN_CONTROLLER = "com.voice.assistant.DlnaMusicReceiver.dlna";

    private BaseContext mBaseContext;
    private MyApplication application;

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub
        String action = intent.getAction();
        application = (MyApplication) context.getApplicationContext();
        mBaseContext = new BaseContext(context);

        boolean dlna = intent.getBooleanExtra(DLAN_CONTROLLER, false);
        LogManager.e("dlan action =" +dlna);

        if (DLAN_MUSIC_TO_MAIN_PLAY_ACTION.equals(action) && dlna) {
            MediaPlayerService.messageQueue().post(new Runnable() {
                
                @Override
                public void run() {
                    setPlayStatus(true, true, true);
                    stopWakeup();
                }
            });
            
        } else if (DLAN_MUSIC_TO_MAIN_PAUSE_ACTION.equals(action) && dlna) {
            MediaPlayerService.messageQueue().post(new Runnable() {
                
                @Override
                public void run() {
                    setPlayStatus(true, false, true);
                    startWakeup();
                }
            });
            
        } else if (DLAN_MUSIC_TO_MAIN_STOP_ACTION.equals(action) && dlna) {
            MediaPlayerService.messageQueue().post(new Runnable() {
                
                @Override
                public void run() {
                    setPlayStatus(false, false, false);
                    startWakeup();
                }
            });
        }
    }

    private void startWakeup() {
        application.getUnion().getRecogniseSystem().startWakeup();
    }

    private void stopWakeup() {
        application.getUnion().getRecogniseSystem().stopWakeup();
    }

    private void setPlayStatus(boolean playing, boolean inPlay, boolean isDlan) {
        application.getUnion().getBaseContext().setGlobalBoolean(KeyList.GKEY_IS_MUSIC_PLAYING, playing);
        application.getUnion().getBaseContext().setGlobalBoolean(KeyList.GKEY_IS_MUSIC_IN_PLAYING, inPlay);
        mBaseContext.setPrefBoolean(KeyList.PKEY_CUREENT_MUSIC_IS_DLAN, isDlan);
    }

}
