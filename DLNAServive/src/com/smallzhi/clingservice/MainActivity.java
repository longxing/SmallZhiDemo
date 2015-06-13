package com.smallzhi.clingservice;

import org.fourthline.cling.UpnpService;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.example.clingservice.R;
import com.iii360.sup.common.utl.LogManager;
import com.smallzhi.clingservice.media.IPlayService;

public class MainActivity extends Activity {
    protected UpnpService mUpnpService;
    UpnpServerProxy mUpnpServerProxy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mUpnpServerProxy = new UpnpServerProxy(this);
        mUpnpServerProxy.bindService();

        findViewById(R.id.button1).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
//                mUpnpServerProxy.bindService() ;
                
                IPlayService mIPlayService = MyApplication.getInstance().getPlayService();
                if (mIPlayService != null) {
                    mIPlayService.IPause();
                    LogManager.i("IGetPlayerState=" + mIPlayService.IGetPlayerState());
                    LogManager.i("IGetCurrentPosition=" + mIPlayService.IGetCurrentPosition());
                    LogManager.i("IGetDuration=" + mIPlayService.IGetDuration());

                } else {
                    LogManager.e("getPlayService is null");
                }
            }
        });
        
        findViewById(R.id.button2).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
//                mUpnpServerProxy.bindService() ;
                
                IPlayService mIPlayService = MyApplication.getInstance().getPlayService();
                if (mIPlayService != null) {
                    mIPlayService.IPlay();
                } else {
                    LogManager.e("getPlayService is null");
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mUpnpServerProxy.unBindService();
    }

}
