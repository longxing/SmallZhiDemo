package com.voice.recognise.service;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;

import com.base.util.KeyManager;
import com.iii360.sup.common.utl.LogManager;
import com.iii360.sup.common.utl.SuperBaseContext;
import com.parser.iengine.EngineGroup;
import com.parser.iengine.IEngine;
import com.voice.recognise.KeyList;
import com.voice.recognise.VoiceRecognise;
import com.voice.recognise.VoiceRecogniseFactory;

public class RecogniseService extends Service {

    private VoiceRecognise mVoiceRecognise;
    private IEngine mIEngine;
   
    
    @Override
    public IBinder onBind(Intent arg0) {

        return null;
    }

    
    
    @Override
    public void onDestroy() {

        mVoiceRecognise.destory();
        super.onDestroy();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }



    @Override
    public void onCreate() {
        //LogManager.initLogManager(true, false);
        super.onCreate();
        mVoiceRecognise = VoiceRecogniseFactory
                .createNewVoiceRecognise(this, null, 
                        VoiceRecogniseFactory.VOICE_RECOGNISE_TYPE_OUTSIDE);
        mIEngine = new EngineGroup(this);
        init();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int ret = super.onStartCommand(intent, flags, startId);

		Bundle bundle = null;
		if (intent != null) {
			bundle = intent.getExtras();
		}

        int serviceId = -1;
        
        if(bundle != null) {
            serviceId = bundle.getInt(KeyManager.EKEY_SERVICE_ID, KeyManager.SERVICE_ID_SET_RECOGNIZER);
        }
        
        LogManager.w("serviceid:" + serviceId);
        
        switch(serviceId) {
        case KeyManager.SERVICE_ID_SET_RECOGNIZER:
            int type = bundle.getInt(KeyList.EKEY_RECOGNISE_ENGINE_TYPE);
            mVoiceRecognise.setRecognizer(type);
            break;
        case KeyManager.SERVICE_ID_SHOW_FLOAT_BUTTON:
            break;
        case KeyManager.SERVICE_ID_CLOSE_FLOAT_BUTTON:
            //stopSelf();
            break;
        case KeyManager.SERVICE_ID_START_PARER:
            String id = bundle.getString(KeyManager.EKEY_APP_ID);
            String text = bundle.getString(KeyManager.EKEY_RECOGNISE_RESULT);
            mIEngine.input(text, id);
            break;
        }

        return ret;
    }



    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
    }



    private void sendBroadcast(String action, Bundle bundle) {

        Intent intent = new Intent();
        intent.setAction(action);
        intent.putExtras(bundle);
        sendBroadcast(intent);
    }
    
    private void init() {
        SuperBaseContext context = new SuperBaseContext(this);
        
    }


    
}
