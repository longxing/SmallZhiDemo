package com.voice.assistant.utl;

import android.content.Context;

//import com.example.airplay.util.Constants;
import com.iii360.base.common.utl.BaseContext;
import com.voice.assistant.main.KeyList;
import com.voice.assistant.main.MyApplication;
import com.voice.assistant.main.music.MediaPlayerService;

public class AirPlayMusicController {
    private Context context;
    private BaseContext mBaseContext;
    private MyApplication application;

    public AirPlayMusicController(Context context) {
        // TODO Auto-generated constructor stub
        application = (MyApplication) context.getApplicationContext();
        this.context = context;
        mBaseContext = new BaseContext(context);
    }

    public boolean isAirplay() {
    	return false;
//    return mBaseContext.getPrefBoolean(Constants.PKEY_CUREENT_MUSIC_IS_AIRPLAY);
    }

    public void setAirplay(boolean airplay) {
     	return ;
        //mBaseContext.setPrefBoolean(Constants.PKEY_CUREENT_MUSIC_IS_AIRPLAY, airplay);
    }

    public void play() {
    //    context.sendBroadcast(new Intent(Constants.IKEY_MEDIA_PLAY));
        MediaPlayerService.messageQueue().post(new Runnable() {

            @Override
            public void run() {
                stopWakeup();
                mBaseContext.setGlobalBoolean(KeyList.GKEY_IS_MUSIC_PLAYING, true);
                mBaseContext.setGlobalBoolean(KeyList.GKEY_IS_MUSIC_IN_PLAYING, true);
            }
        });
    }

    public void pause() {
       // context.sendBroadcast(new Intent(Constants.IKEY_MEDIA_PAUSE));
        MediaPlayerService.messageQueue().post(new Runnable() {

            @Override
            public void run() {
                mBaseContext.setGlobalBoolean(KeyList.GKEY_IS_MUSIC_PLAYING, true);
                mBaseContext.setGlobalBoolean(KeyList.GKEY_IS_MUSIC_IN_PLAYING, false);
                startWakeup();
            }
        });

    }

    public void stop() {
        setAirplay(false);
        MediaPlayerService.messageQueue().post(new Runnable() {

            @Override
            public void run() {
   //             context.sendBroadcast(new Intent(Constants.IKEY_MEDIA_STOP));

                mBaseContext.setGlobalBoolean(KeyList.GKEY_IS_MUSIC_PLAYING, true);
//                mBaseContext.setGlobalBoolean(KeyList.GKEY_IS_MUSIC_IN_PLAYING, false);
                startWakeup();
            }
        });    

    }

    public void inCreaseVolume() {
 //       context.sendBroadcast(new Intent(Constants.IKEY_INCREASE_VOLUME));
    }

    public void deCreaseVolume() {
//        context.sendBroadcast(new Intent(Constants.IKEY_DECREASE_VOLUME));
    }

    private void startWakeup() {
        application.getUnion().getRecogniseSystem().startWakeup();
    }

    private void stopWakeup() {
        application.getUnion().getRecogniseSystem().stopWakeup();
    }

}
