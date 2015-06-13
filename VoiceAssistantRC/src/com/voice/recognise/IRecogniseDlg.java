//ID20120528006 liaoyixuan begin
package com.voice.recognise;

import android.view.View.OnClickListener;

public interface IRecogniseDlg {
    
    public static interface OnShowListener {
        public void onShow();
    }
    
    public static interface OnDismssListener {
        public void onDismss();
    }
    
    public final static int RECOGNISE_STATE_NORMAL = -1;
    public final static int RECOGNISE_STATE_INIT = 0;
    public final static int RECOGNISE_STATE_SPEECHING = 1;
    public final static int RECOGNISE_STATE_REQUEST = 2;
    public final static int RECOGNISE_STATE_ERROR = 3;
    public final static int RECOGNISE_STATE_BEGIN_SPEECH = 4;
    public final static int RECOGNISE_STATE_END = 5;
    public final static int RECOGNISE_STATE_START_RECOGNISE = 6;
    
    public void update(int recogniseStateSpeeching, Object object);
    public void setOnStartClickListener(OnClickListener l);
    public void setOnCancelClickListener(OnClickListener l);
    public void setOnConfirmClickListener(OnClickListener l);
    public void setOnShowListener(OnShowListener l);
    
    public void setOnDismssListener(OnDismssListener l);
    
}
//ID20120528006 liaoyixuan end