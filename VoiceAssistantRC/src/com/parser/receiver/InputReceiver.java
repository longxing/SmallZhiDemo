package com.parser.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.base.data.CommandInfo;
import com.base.util.KeyManager;
import com.iii360.sup.common.utl.LogManager;
import com.iii360.sup.common.utl.SystemUtil;
import com.voice.base.BaseVoiceContext;

public class InputReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        LogManager.i("received!");
        
        if(intent.getAction().equals(KeyManager.AKEY_RESPONE)) {
            
            CommandInfo info = new CommandInfo();// = intent.getParcelableExtra(KeyManager.EKEY_REPONSE_RESULT);
            BaseVoiceContext baseContext = new BaseVoiceContext(context);
            if(info != null && info._packageName != null && !info._packageName.equals("")) {
                baseContext.setPrefString(KeyManager.PKEY_CUR_PACKAGE, info._packageName);
            } else {
                String name = SystemUtil.getTopActivity(context).getPackageName();
                baseContext.setPrefString(KeyManager.PKEY_CUR_PACKAGE, name);
            }
            
            abortBroadcast();
        } else if(intent.getAction().equals(KeyManager.AKEY_PARSE_INPUT)) {
            Bundle bundleSrc = null;
            String text = null;
            String appId = null;
            
            if (intent != null) {
                bundleSrc = intent.getExtras();
            }
            
            if(bundleSrc != null) {
                text = bundleSrc.getString(KeyManager.EKEY_RECOGNISE_RESULT);
                appId = bundleSrc.getString(KeyManager.EKEY_APP_ID);
            }
            
            if(appId == null || appId.trim().equals("")) {
                appId = SystemUtil.getTopActivity(context).getPackageName();
            }
            
            //Platform.startParser(context, appId, text);
            
            abortBroadcast();
        }
        //abortBroadcast();
        
    }
    
}
