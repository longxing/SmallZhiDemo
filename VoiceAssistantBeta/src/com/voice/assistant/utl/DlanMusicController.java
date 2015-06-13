package com.voice.assistant.utl;

import android.content.Context;

import com.iii360.base.common.utl.BaseContext;
import com.iii360.base.common.utl.LogManager;
import com.iii360.base.inf.recognise.ILightController;
import com.smallzhi.clingservice.media.IPlayService;
import com.smallzhi.clingservice.media.MyMediaPlayerService;
import com.voice.assistant.main.KeyList;
import com.voice.assistant.main.MyApplication;
import com.voice.recognise.IRecogniseDlg;

/**
 * DLAN音乐控制
 * 
 * @author Administrator
 * 
 */
public class DlanMusicController {
    private BaseContext mBaseContext;
    private Context context;
    private MyApplication application;
    private ILightController mIRecogniseDlg;
    private IPlayService mPlayService;

    public DlanMusicController(Context context) {
        // TODO Auto-generated constructor stub
        application = (MyApplication) context.getApplicationContext();
        mBaseContext = new BaseContext(context);
        this.context = context;
        mIRecogniseDlg = (ILightController) mBaseContext.getGlobalObject(KeyList.GKEY_WAKEUP_LIGHT_CONTROL);
        mPlayService=MyMediaPlayerService.getInstance(context);
    }

    public boolean isDlan() {
        return mBaseContext.getPrefBoolean(KeyList.PKEY_CUREENT_MUSIC_IS_DLAN);
    }

    public void setDlan(boolean dlan) {
        LogManager.i("setDlan=" + dlan);
        mBaseContext.setPrefBoolean(KeyList.PKEY_CUREENT_MUSIC_IS_DLAN, dlan);
    }

    /**
     * 发送至dlan，播放歌曲
     */
    public void paly() {
//        android.util.Log.i("hefeng", "发送至dlan，播放歌曲");
//        MediaPlayerService.messageQueue().post(new Runnable() {
//
//            @Override
//            public void run() {
                mBaseContext.setGlobalBoolean(KeyList.GKEY_IS_MUSIC_PLAYING, true);
                mBaseContext.setGlobalBoolean(KeyList.GKEY_IS_MUSIC_IN_PLAYING, true);
                stopWakeup();
                sendToDlnaPlay();
//            }
//        });
        
        mIRecogniseDlg.updateStateOnRunnable(ILightController.RECOGNISE_STATE_CLOSE, 0);
    }

    /**
     * 发送至dlan，暂停歌曲
     */
    public void pause() {
//        android.util.Log.i("hefeng", "发送至dlan，暂停歌曲");
//        MediaPlayerService.messageQueue().post(new Runnable() {
//
//            @Override
//            public void run() {
                sendToDlanPause();
                mBaseContext.setGlobalBoolean(KeyList.GKEY_IS_MUSIC_PLAYING, true);
                mBaseContext.setGlobalBoolean(KeyList.GKEY_IS_MUSIC_IN_PLAYING, false);
                startWakeup();
//            }
//        });
    }

    /**
     * 发送至dlan，停止播放歌曲
     */
    public void stop() {
//        android.util.Log.i("hefeng", "发送至dlan，停止播放歌曲");
//        MediaPlayerService.messageQueue().post(new Runnable() {
//
//            @Override
//            public void run() {
    	        stop(false);
//            }
//        });
    }
    public void stop(boolean flag){
    	if(flag){
    		sendToDlnasimpleStop();
    	}else{
    		sendToDlanStop();
    	}
         mBaseContext.setGlobalBoolean(KeyList.GKEY_IS_MUSIC_PLAYING, true);
         mBaseContext.setGlobalBoolean(KeyList.GKEY_IS_MUSIC_IN_PLAYING, false);
         setDlan(false);
         startWakeup();
    	
    }
    
    public void sendToDlnaPlay() {
    	mPlayService.IPlay();
       // context.sendBroadcast(new Intent(KeyList.AKEY_TO_DLAN_MUSIC_START));
    }

    public void sendToDlanPause() {
    	mPlayService.IPause();
       // context.sendBroadcast(new Intent(KeyList.AKEY_TO_DLAN_MUSIC_PAUSE));
    }
    
    public void sendToDlnasimpleStop(){
    	mPlayService.ISimpleStop();
    }
    public void sendToDlanStop() {
    	mPlayService.IStop();
    	//context.sendBroadcast(new Intent(KeyList.AKEY_TO_DLAN_MUSIC_STOP));
    }

    private void startWakeup() {
        application.getUnion().getRecogniseSystem().startWakeup();
    }

    private void stopWakeup() {
        application.getUnion().getRecogniseSystem().stopWakeup();
    }
}
