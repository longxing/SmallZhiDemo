//ID20120528006 liaoyixuan begin
package com.voice.recognise;

import android.speech.RecognitionListener;

public interface ISpeechRecognizer {
    public void cancel();
    public void start();
    public void stop();
    public void destroy();
    public void setRecognitionListener(RecognitionListener listener);
}
//ID20120528006 liaoyixuan end