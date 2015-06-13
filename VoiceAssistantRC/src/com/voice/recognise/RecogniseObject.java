package com.voice.recognise;

import com.voice.recognise.VoiceRecognise.OnRecognizerEndListener;

public class RecogniseObject {
    public OnRecognizerEndListener _onRecognizerEndListener;
    public String _type;
    public String _keys;
    public String _contentId;
    
    public RecogniseObject(String type, String keys, String contentId, OnRecognizerEndListener listener) {
        _type = type;
        _keys = keys;
        _contentId = contentId;
        _onRecognizerEndListener = listener;
    }
    public RecogniseObject(OnRecognizerEndListener listener) {
        this(VoiceRecognise.ENGINE_TYPE_RECOGNISE_TEXT, null, null, listener);
    }
    
    public RecogniseObject() {
        _type = VoiceRecognise.ENGINE_TYPE_RECOGNISE_TEXT;
        _keys = null;
        _contentId = null;
        _onRecognizerEndListener = null;
    }
}
