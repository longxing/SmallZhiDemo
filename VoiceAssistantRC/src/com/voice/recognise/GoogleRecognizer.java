//ID20120528006 liaoyixuan begin
package com.voice.recognise;


import com.iii360.sup.common.utl.LogManager;

import android.content.Context;
import android.content.Intent;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;


public class GoogleRecognizer implements ISpeechRecognizer {
    private SpeechRecognizer mSpeechRecognizer;
    
    GoogleRecognizer(Context context) {
        
        mSpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(context);
    }
    
    @Override
    public void cancel() {
        //mSpeechRecognizer.stopListening();
        mSpeechRecognizer.cancel();

    }

    @Override
    public void start() {
        LogManager.d("start recognise.");
        Intent intent = new Intent("android.speech.action.ANALYZE_SPEECH");
        intent.putExtra("calling_package", "VoiceIME");
        intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3);
        mSpeechRecognizer.startListening(intent);

    }

    @Override
    public void stop() {
        mSpeechRecognizer.stopListening();

    }

    @Override
    public void setRecognitionListener(RecognitionListener listener) {
        mSpeechRecognizer.setRecognitionListener(listener);

    }

    @Override
    public void destroy() {
        mSpeechRecognizer.destroy();
        
    }

}
//ID20120528006 liaoyixuan end