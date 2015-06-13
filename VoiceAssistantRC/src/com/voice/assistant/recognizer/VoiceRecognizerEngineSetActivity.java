package com.voice.assistant.recognizer;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;

import com.base.util.KeyManager;
import com.iii360.sup.common.utl.SuperBaseContext;
import com.voice.recognise.KeyList;
import com.voice.recognise.service.RecogniseService;

public class VoiceRecognizerEngineSetActivity extends PreferenceActivity {
    private Preference mPrefOpenRecognizerService;
    private Preference mPrefChangeRecognizer;
    private SuperBaseContext mBaseContext;
    private Handler mHandler = new Handler();
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
//        LogManager.initLogManager(true, false);
//        SystemUtil.init(this);
        
        addPreferencesFromResource(R.xml.voice_recognizer_set);
        mBaseContext = new SuperBaseContext(this);
        mPrefOpenRecognizerService = findPreference(KeyManager.PKEY_OPEN_RECOGNISE_SERVICE);
        
        mPrefOpenRecognizerService.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {

            @Override
            public boolean onPreferenceChange(Preference preference,
                    Object newValue) {
                boolean isOpen = (Boolean) newValue;
                if(isOpen) {
                    mBaseContext.setPrefBoolean(KeyList.PKEY_OPEN_FLOAT_BUTTON, true);
                    startRecogniseService(KeyManager.SERVICE_ID_SHOW_FLOAT_BUTTON, null);
                } else {
                    mBaseContext.setPrefBoolean(KeyList.PKEY_OPEN_FLOAT_BUTTON, false);
                    startRecogniseService(KeyManager.SERVICE_ID_CLOSE_FLOAT_BUTTON, null);
                }
                
                return true;
            }
            
        });
        
        mPrefChangeRecognizer = findPreference(KeyList.PKEY_RECOGNISE_ENGINE);
        
        mPrefChangeRecognizer.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {

            @Override
            public boolean onPreferenceChange(Preference preference,
                    Object newValue) {
                int type = Integer.parseInt((String) newValue);
   
                Bundle bundle = new Bundle();
                bundle.putInt(KeyList.EKEY_RECOGNISE_ENGINE_TYPE, type);
                startRecogniseService(KeyManager.SERVICE_ID_SET_RECOGNIZER, bundle);
                return true;
            }
            
        });
        
        if(mBaseContext.getPrefBoolean(KeyManager.PKEY_OPEN_RECOGNISE_SERVICE)) {
            startRecogniseService(KeyManager.SERVICE_ID_SHOW_FLOAT_BUTTON, null);
        }
        
//        AutoTest.initTest(null);
//        AutoTest.runTestCRF("CommandQueryWeather", new DefaultResultChecker(), false, false);

    }
    
    
    private void startRecogniseService(int commandId, Bundle extras) {
        Bundle bundle = extras;
        
        if(bundle == null) {
            bundle = new Bundle();
        }
        
        
        bundle.putInt(KeyManager.EKEY_SERVICE_ID, commandId);
        Intent intent = new Intent(this, RecogniseService.class);
        intent.putExtras(bundle);
        //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startService(intent);
    }
}