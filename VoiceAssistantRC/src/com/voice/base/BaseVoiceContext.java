package com.voice.base;

import java.io.File;
import java.util.Iterator;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.speech.RecognizerIntent;
import android.widget.Toast;

import com.base.file.FileUtil;
import com.iii360.sup.common.utl.LogManager;
import com.iii360.sup.common.utl.SuperBaseContext;
import com.voice.assistant.recognizer.R;
import com.voice.recognise.KeyList;

public class BaseVoiceContext extends SuperBaseContext {
	   
    private int mSessionId = 0;
    //private static String mVersion;
    //private static boolean isEnableUmeng = true;

    public int getSessionId() {

        
        return mSessionId;
    }

    public void resetSessionId() {
     
        mSessionId = 0;
    }

    public void addSessionId() {
        mSessionId++;
        LogManager.d("cur Id:" + mSessionId);
    }
    
    
	public BaseVoiceContext(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}



}
