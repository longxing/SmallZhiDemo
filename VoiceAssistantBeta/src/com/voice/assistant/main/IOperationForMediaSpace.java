package com.voice.assistant.main;

import android.os.Message;

import com.iii360.base.inf.IVoiceWidget;


public interface IOperationForMediaSpace {
    public void addView(IVoiceWidget view);

    public void removeView();
    
    public void handMessage(Message msg);
    
    public String getName();
}
