package com.voice.assistant.main.music.db;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.util.Log;

import com.example.common.MyApplication;
import com.iii360.base.common.utl.BaseContext;
import com.iii360.base.common.utl.LogManager;
import com.voice.assistant.main.music.KeyList;
import com.voice.assistant.main.music.MediaInfo;

public class MusicInfoUtils {

	private static final String TAG = "Music MusicInfoUtils";
	
    /**
     * 本地200首歌曲添加到数据库
     * 
     * @param context
     */
    public synchronized static void addLocalMusicToDB(final Context context) {
        final BaseContext baseContext = new BaseContext(context);
        boolean isAdd = baseContext.getPrefBoolean(KeyList.PKEY_IS_ADD_LOCALMUSIC_TO_DB, false);

        if (isAdd) {
            return;
        }
        final MusicDBHelper musicDBHelper = new MusicDBHelper(context);
        LogManager.d(TAG, "addLocalMusicToDB +Thread start"+MyApplication.SystemDoingCurrentTime+new SimpleDateFormat(MyApplication.Date_Fomort).format(new Date()));
        
        new Thread(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                try {
                	
                    InputStream is = context.getAssets().open("localmusic");
                    BufferedReader bufReader = new BufferedReader(new InputStreamReader(is));
                    String line = "";

                    while ((line = bufReader.readLine()) != null) {
                        LogManager.d(TAG, "line=" + line);

                        String[] info = line.split("\\|\\|");

                        if (info.length >= 3) {
                            String id = info[0];
                            id = id.trim() ;
                            
                            String songName = info[1];
                            songName = songName.trim() ;
                            
                            String singerName = info[2];
                            singerName = singerName.trim();

                            MediaInfo media = new MediaInfo(null, null);
                            media._Id = id;
                            media._name = songName;
                            media._singerName = singerName;
                            media._isFromNet = false;
                            media._path = "sdcard/VoiceAssistant/localMusic/" + id;
                            media._updateTime=System.currentTimeMillis();
                            musicDBHelper.add(media);
                        }

                    }

                    baseContext.setPrefBoolean(KeyList.PKEY_IS_ADD_LOCALMUSIC_TO_DB, true);
                    
                    LogManager.d(TAG, "addLocalMusicToDB +Thread end"+MyApplication.SystemDoingCurrentTime+new SimpleDateFormat(MyApplication.Date_Fomort).format(new Date()));

                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    LogManager.e(TAG, "MusicInfoUtils error =" + Log.getStackTraceString(e));
                }
            }
        }).start();
    }

    public static boolean isAddLocalToDB(Context context) {
        BaseContext baseContext = new BaseContext(context);
        return baseContext.getPrefBoolean(KeyList.PKEY_IS_ADD_LOCALMUSIC_TO_DB, false);
    }
}
