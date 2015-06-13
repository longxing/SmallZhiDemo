package com.iii360.box;

import java.util.List;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.iii.wifi.dao.info.WifiBoxSystemInfo;
import com.iii.wifi.dao.info.WifiMusicInfo;
import com.iii.wifi.dao.newmanager.WifiCRUDForBoxSystem;
import com.iii.wifi.dao.newmanager.WifiCRUDForMusic;
import com.iii.wifi.dao.newmanager.WifiCRUDForMusic.ResultForMusicListener;
import com.iii360.box.base.BaseActivity;
import com.iii360.box.util.BoxManagerUtils;

public class TestActivity extends BaseActivity implements OnClickListener {
    private Button btn1;
    private Button btn2;
    private Button btn3;
    private Button btn4;
    private Button btn5;
    private Button btn6;
    private Button btn7;

    private WifiCRUDForMusic mWifiCRUDForMusic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test);

        btn1 = (Button) findViewById(R.id.button1);
        btn2 = (Button) findViewById(R.id.button2);
        btn3 = (Button) findViewById(R.id.button3);
        btn4 = (Button) findViewById(R.id.button4);
        btn5 = (Button) findViewById(R.id.button5);
        btn6 = (Button) findViewById(R.id.button6);

        btn1.setOnClickListener(this);
        btn2.setOnClickListener(this);
        btn3.setOnClickListener(this);
        btn4.setOnClickListener(this);
        btn5.setOnClickListener(this);
        btn6.setOnClickListener(this);

        mWifiCRUDForMusic = new WifiCRUDForMusic(BoxManagerUtils.getBoxIP(context), BoxManagerUtils.getBoxTcpPort(context));
    }

    private void testSystem() {
        WifiCRUDForBoxSystem sys = new WifiCRUDForBoxSystem(BoxManagerUtils.getBoxIP(context), BoxManagerUtils.getBoxTcpPort(context));
        sys.getSystemInfo(new WifiCRUDForBoxSystem.ResultListener() {
            @Override
            public void onResult(String code, WifiBoxSystemInfo info) {
                // TODO Auto-generated method stub
                Log.e("hefeng", "code=" + code);
                if (info != null) {
                    Log.e("hefeng", info.getAvailableRomSize() + "/" + info.getRomTotalSize());
                    Log.e("hefeng", info.getAvailableRamSize() + "/" + info.getRamTotalSize());

                    Log.e("hefeng", "battery:" + info.getBattery());
                    Log.e("hefeng", "ip&mac=" + info.getIp() + "||" + info.getMac());

                    Log.e("hefeng", "serial=" + info.getSerial());
                    Log.e("hefeng", "version=" + info.getVersionCode());
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        if (v == btn1) {
            mWifiCRUDForMusic.play("409", new ResultForMusicListener() {

                @Override
                public void onResult(String errorCode, List<WifiMusicInfo> infos) {
                    // TODO Auto-generated method stub
                    Log.e("hefeng", "code=" + errorCode);
                }
            });

        } else if (v == btn2) {
//            mWifiCRUDForMusic.playResume(new ResultForMusicListener() {
//
//                @Override
//                public void onResult(String errorCode, List<WifiMusicInfo> infos) {
//                    // TODO Auto-generated method stub
//                    Log.e("hefeng", "code=" + errorCode);
//                }
//            });
        } else if (v == btn3) {
//            mWifiCRUDForMusic.playPause(new ResultForMusicListener() {
//
//                @Override
//                public void onResult(String errorCode, List<WifiMusicInfo> infos) {
//                    // TODO Auto-generated method stub
//                    Log.e("hefeng", "code=" + errorCode);
//                }
//            });
        } else if (v == btn4) {
            mWifiCRUDForMusic.playNext(new ResultForMusicListener() {

                @Override
                public void onResult(String errorCode, List<WifiMusicInfo> infos) {
                    // TODO Auto-generated method stub
                    Log.e("hefeng", "code=" + errorCode);
                }
            });
        } else if (v == btn5) {
            mWifiCRUDForMusic.playPre(new ResultForMusicListener() {

                @Override
                public void onResult(String errorCode, List<WifiMusicInfo> infos) {
                    // TODO Auto-generated method stub
                    Log.e("hefeng", "code=" + errorCode);
                }
            });
        } else if (v == btn6) {
            mWifiCRUDForMusic.playState(new ResultForMusicListener() {
                
                @Override
                public void onResult(String errorCode, List<WifiMusicInfo> infos) {
                    // TODO Auto-generated method stub
                    Log.e("hefeng", "code=" + errorCode);
                }
            });
        }
    }
}
