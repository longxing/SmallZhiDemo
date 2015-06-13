/**
 * 
 */
package com.voice.assistant.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

import com.base.data.CommandInfo;
import com.base.util.KeyManager;

/**
 * @author rtygbwwwerr
 *
 */
public class AssistantService extends Service {

    /* (non-Javadoc)
     * @see android.app.Service#onBind(android.content.Intent)
     */
    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
    }

    @Override
    public void onStart(Intent intent, int startId) {
        // TODO Auto-generated method stub
        super.onStart(intent, startId);
        startTrain(intent);

    }
    
    private void startTrain(Intent intent) {
        if(intent != null) {
            String data = intent.getStringExtra(KeyManager.EKEY_COMMAND_PARAM);
            CommandInfo info = new CommandInfo(data);
            Toast.makeText(this, info.toString(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // TODO Auto-generated method stub
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
    }

    
}
