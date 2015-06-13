package com.voice.recognise;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;

import com.iii360.sup.common.utl.LogManager;
import com.voice.base.BaseVoiceContext;


public abstract class VoiceRecogniseAbstract extends BaseVoiceContext implements VoiceRecognise {
    private OnRecognizerEndListener mOnRecognizerEndListener;
    private Handler mUIHandler;
    
    VoiceRecogniseAbstract(Context context, Handler handler) {
        super(context);
        mUIHandler = handler;
    }

    public Handler getHandler() {
        return mUIHandler;
        
    }
    
    @Override
    public void startRecognise() {
        
    }
    

    @Override
    public void handText(Intent data) {

    }

    public void sendNewRecognizeMode(int mode, RecogniseObject recObject) {

        //VoiceCommand.sendNewRecognizeMode(mode, mUIHandler, recObject);
    }
    
    
    @Override
    public void setOnRecognizerEndListener(OnRecognizerEndListener l) {
        mOnRecognizerEndListener = l;
    }
    
    
    protected int getRecognizerMode() {
        return getGlobalInteger(KeyList.GKEY_INT_MODE);
    }
    
    
    protected String delInvalidText(String text) {
        String ret = "";
        Pattern pattern = Pattern
                .compile("(锟斤拷锟斤拷谁)(锟斤拷?|锟斤拷?)(\\(a[\\d]{8}\\))(锟斤拷?|锟斤拷?)");

        if (text != null) {
            ret = text.trim();
        }

        if (!ret.equals("")) {
            Matcher matcher = pattern.matcher(text);
            if (matcher.matches()) {
                ret = "锟斤拷锟斤拷谁";
            }
        }

        return ret;
    }
    
    private void handlerResult(String text) {
        
        
        LogManager.d("VoiceRecognise", "handlerResult");

    }
    
    
    @Override
    public void handText(String text) {
        boolean isEnd = false;
        LogManager.e("VoiceRecognise", "onEnd", "result:" + text);

        text = delInvalidText(text);

        if (!text.equals("")) {
            addSessionId();
            if (mOnRecognizerEndListener != null) {
                if (getRecognizerMode() == RECOGNIZE_MODE_WIDGET
                        || getRecognizerMode() == RECOGNIZE_MODE_EXCAT) {
                    LogManager.i("VoiceRecognise", "onEnd",
                            "call OnRecognizerEndListener");
                    isEnd = mOnRecognizerEndListener.onRecognizerEnd(text);
                } else {
                    LogManager.w("VoiceRecognise", "onEnd", "common mode");
                }

            }

            if (!isEnd) {
                handlerResult(text);
            }
        }
    }

    @Override
    public boolean isCanEnterCommandMode() {

        return false;
    }

    @Override
    public void setRecogniseDlg(IRecogniseDlg dlg) {
        LogManager.w("VoiceRecogniseAbstract", "setRecogniseDlg", "stub, no implement");
    }

}
