package com.voice.recognise.view;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager.LayoutParams;

import com.voice.recognise.IRecogniseDlg;

public abstract class AbstractRecogniseDlg extends FloatView implements IRecogniseDlg {

    private OnShowListener mOnShowListener;
    private OnDismssListener mOnDismssListener;
    
    @Override
    protected void onDimss() {

        
        if(mOnDismssListener != null) {
            mOnDismssListener.onDismss();
        }
    }

    @Override
    protected void onShow(LayoutParams params, View holdView, DisplayMetrics dm) {
        
        if(mOnShowListener != null) {
            mOnShowListener.onShow();
        }
    }

    public AbstractRecogniseDlg(int layoutId, Context context) {
        super(layoutId, context);
        // TODO Auto-generated constructor stub
    }
//ID20120528001 liaoyixuan begin
    public AbstractRecogniseDlg(int layoutId, Context context,
                                String keyX, String keyY) {
        super(layoutId, context, keyX, keyY);
    }
//ID20120528001 liaoyixuan end  
    @Override
    public void update(int recogniseStateSpeeching, Object object) {
        // TODO Auto-generated method stub

    }


    @Override
    public void setOnShowListener(OnShowListener l) {
        mOnShowListener = l;

    }

    @Override
    public void setOnDismssListener(OnDismssListener l) {
        mOnDismssListener = l;

    }

}
