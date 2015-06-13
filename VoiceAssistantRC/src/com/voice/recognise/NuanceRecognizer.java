////ID20120528006 liaoyixuan begin
//package com.voice.recognise;
//
//
//import java.util.ArrayList;
//
//import android.content.Context;
//import android.os.Bundle;
//import android.os.Handler;
//import android.speech.RecognitionListener;
//import android.speech.SpeechRecognizer;
//
//import com.nuance.nmdp.speechkit.Recognition;
//import com.nuance.nmdp.speechkit.Recognizer;
//import com.nuance.nmdp.speechkit.SpeechError;
//import com.nuance.nmdp.speechkit.SpeechKit;
//
//public class NuanceRecognizer implements ISpeechRecognizer {
//    
//    /**
//     * The login parameters should be specified in the following manner:
//     * 
//     * public static final String SpeechKitServer = "ndev.server.name";
//     * 
//     * public static final int SpeechKitPort = 1000;
//     * 
//     * public static final String SpeechKitAppId = "ExampleSpeechKitSampleID";
//     * 
//     * public static final byte[] SpeechKitApplicationKey =
//     * {
//     *     (byte)0x38, (byte)0x32, (byte)0x0e, (byte)0x46, (byte)0x4e, (byte)0x46, (byte)0x12, (byte)0x5c, (byte)0x50, (byte)0x1d,
//     *     (byte)0x4a, (byte)0x39, (byte)0x4f, (byte)0x12, (byte)0x48, (byte)0x53, (byte)0x3e, (byte)0x5b, (byte)0x31, (byte)0x22,
//     *     (byte)0x5d, (byte)0x4b, (byte)0x22, (byte)0x09, (byte)0x13, (byte)0x46, (byte)0x61, (byte)0x19, (byte)0x1f, (byte)0x2d,
//     *     (byte)0x13, (byte)0x47, (byte)0x3d, (byte)0x58, (byte)0x30, (byte)0x29, (byte)0x56, (byte)0x04, (byte)0x20, (byte)0x33,
//     *     (byte)0x27, (byte)0x0f, (byte)0x57, (byte)0x45, (byte)0x61, (byte)0x5f, (byte)0x25, (byte)0x0d, (byte)0x48, (byte)0x21,
//     *     (byte)0x2a, (byte)0x62, (byte)0x46, (byte)0x64, (byte)0x54, (byte)0x4a, (byte)0x10, (byte)0x36, (byte)0x4f, (byte)0x64
//     * };
//     * 
//     * Please note that all the specified values are non-functional
//     * and are provided solely as an illustrative example.
//     * 
//     */
//
//    /* Please contact Nuance to receive the necessary connection and login parameters */
//    private static final String SPEECH_KIT_SERVER = "sandbox.nmdp.nuancemobility.net"/* Enter your server here */;
//
//    private static final int SPEECH_KIT_PORT = 443/* Enter your port here */;
//    
//    private static final boolean SPEECH_KIT_SSL = false;
//
//    private static final String SPEECH_KIT_APP_ID = "NMDPTRIAL_fengbs60022020120507052119"/* Enter your ID here */;
//
//    private static final byte[] SPEECH_KIT_APP_KEY = {
//        (byte)0xb4, (byte)0x84, (byte)0x57, (byte)0xe7, (byte)0xc5, (byte)0xcf, (byte)0xcb, (byte)0x9b, (byte)0xc4, (byte)0x14, (byte)0x76, 
//        (byte)0xb6, (byte)0x5c, (byte)0xac, (byte)0x20, (byte)0x6e, (byte)0xbe, (byte)0xb0, (byte)0xbf, (byte)0xe3, (byte)0x23, (byte)0x64,
//        (byte)0x5b, (byte)0x65, (byte)0x79, (byte)0x06, (byte)0xc9, (byte)0x90, (byte)0xe0, (byte)0x77, (byte)0xb2, (byte)0x94, (byte)0xfc,
//        (byte)0x09, (byte)0x7b, (byte)0xc1, (byte)0xd9, (byte)0x46, (byte)0x5c, (byte)0x9d, (byte)0x5a, (byte)0xd2, (byte)0x53, (byte)0xb6,
//        (byte)0x7e, (byte)0x74, (byte)0x24, (byte)0x3d, (byte)0x24, (byte)0xe3, (byte)0x20, (byte)0x47, (byte)0x48, (byte)0x7b, (byte)0xac,
//        (byte)0xc1, (byte)0x81, (byte)0x76, (byte)0x51, (byte)0x61, (byte)0xed, (byte)0x03, (byte)0xf5, (byte)0xe9};
//    
//    private static final String LANUAGE_TYPE = "cn_ma";
//    
//    //private boolean mIsRecording;
//    private SpeechKit mSpeechKit;
//    private Recognizer mSpeechRecognizer;
//    private Handler mHandler = new Handler();
//    private RecognitionListener mRecognitionListener;
//    
//    private Runnable mRunnable = new Runnable() {
//
//        @Override
//        public void run() {
//            mRecognitionListener.onRmsChanged(getAudioLevel());
//            mHandler.postDelayed(this, 100);
//            
//        }
//        
//    };
//    
//    private Recognizer.Listener mListener = new Recognizer.Listener() {
//
//        @Override
//        public void onError(Recognizer recognizer, SpeechError error) {
//            mHandler.removeCallbacks(mRunnable);
//            if(mRecognitionListener != null) {
//                mRecognitionListener.onError(getErrorCode(error));
//            }
//           // mSpeechRecognizer = null;
//            
//        }
//
//        @Override
//        public void onRecordingBegin(Recognizer recognizer) {
//            //mIsRecording = true;
//            if(mRecognitionListener != null) {
//                mRecognitionListener.onReadyForSpeech(null);
//                
//                mHandler.post(mRunnable);
//            }
//            
//        }
//
//        @Override
//        public void onRecordingDone(Recognizer recognizer) {
//           // mIsRecording = false;
//            mHandler.removeCallbacks(mRunnable);
//            if(mRecognitionListener != null) {
//                mRecognitionListener.onEndOfSpeech();
//            }
//            
//        }
//
//        @Override
//        public void onResults(Recognizer recognizer, Recognition ret) {
//            if(mRecognitionListener != null) {
//                Bundle results = new Bundle();
//                ArrayList<String> data = new ArrayList<String>();
//                data.add(ret.getResult(0).getText());
//                results.putStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION, data);
//                mRecognitionListener.onResults(results);
//            }
//            //mSpeechRecognizer = null;
//            
//        }
//        
//    };
//    
//    
//    NuanceRecognizer(Context context) {
//        mSpeechKit = SpeechKit.initialize(context, SPEECH_KIT_APP_ID, SPEECH_KIT_SERVER, 
//                                          SPEECH_KIT_PORT, SPEECH_KIT_SSL, SPEECH_KIT_APP_KEY);
//        
//
//    }
//    
//    private int getErrorCode(SpeechError error) {
//        return error.getErrorCode();
//    }
//    
//    private float getAudioLevel() {
//        return (mSpeechRecognizer.getAudioLevel() - 60) * 2;
//    }
//    
//    @Override
//    public void cancel() {
//        if(mSpeechRecognizer != null) {
//            mSpeechRecognizer.cancel();
//        }
//        
//
//    }
//
//    @Override
//    public void start() {
//        mSpeechRecognizer = mSpeechKit.createRecognizer(Recognizer.RecognizerType.Search, 
//                Recognizer.EndOfSpeechDetection.Short, 
//                LANUAGE_TYPE, mListener, mHandler);
//        mSpeechRecognizer.setListener(mListener);
//        
//        mSpeechRecognizer.start();
//        
//    }
//
//    @Override
//    public void stop() {
//        mHandler.removeCallbacks(mRunnable);
//        mSpeechRecognizer.stopRecording();
//
//    }
//
//    @Override
//    public void setRecognitionListener(RecognitionListener listener) {
//        mRecognitionListener = listener;
//        //mSpeechRecognizer.setListener(mListener);
//    }
//
//    @Override
//    public void destroy() {
//        if(mSpeechRecognizer != null) {
//            mSpeechRecognizer.cancel();
//            mSpeechRecognizer = null;
//        }
//
//    }
//}
////ID20120528006 liaoyixuan end