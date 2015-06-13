package com.base.receiver;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.base.data.CommandInfo;
import com.base.platform.OnDataReceivedListener;
import com.base.platform.Platform;
import com.base.secure.Author;
import com.base.util.KeyManager;
import com.iii360.sup.common.utl.LogManager;

public class ParserResultReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        
        byte[] data = intent.getByteArrayExtra(KeyManager.EKEY_COMMAND_PARAM);
        //Intent intentService = new Intent(context, TrainActivity.class);
        
        String decodeData = Author.decode(data);
        LogManager.d("Received parsed result:" + decodeData);
        
//        intentService.putExtra(KeyList.EKEY_COMMAND_PARAM, decodeData);
//        intentService.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        
        OnDataReceivedListener listener = Platform.getPlatformInstance(context, null).getOnDataReceivedListener();
        
        if(listener != null) {
            listener.onDataReceived(new CommandInfo(decodeData));
        }
        
        //        context.startService(intentService);
        //context.startActivity(intentService);
        


    }

}
