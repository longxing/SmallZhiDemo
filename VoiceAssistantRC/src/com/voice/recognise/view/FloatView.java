//ID20120528001 liaoyixuan begin
package com.voice.recognise.view;

import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;

import com.iii360.sup.common.utl.LogManager;
import com.iii360.sup.common.utl.SuperBaseContext;

public class FloatView {


    private GestureDetector mGestureDetector;
    private SimpleOnGestureListener mSimpleOnGestureListener;
    
    private SuperBaseContext mBaseContext;
    private float mTouchStartX;
    private float mTouchStartY;
    private boolean mIsShowing;
    private View mHoldView;
    private WindowManager.LayoutParams mParams = new WindowManager.LayoutParams();
    private WindowManager mWindowManager;
    private DisplayMetrics mDisplayMetrics;
    //private Window mWindow;
    private Handler mHandler;
    
    private String mKeyX;
    private String mKeyY;
    
    public FloatView(View view) {
        mWindowManager = (WindowManager) view.getContext().getApplicationContext()
                .getSystemService(Context.WINDOW_SERVICE);
        mHoldView = view;
        
        init();
    }
    
    public FloatView(int layoutId, Context context) {
        mWindowManager = (WindowManager) context.getApplicationContext()
                .getSystemService(Context.WINDOW_SERVICE);
        mHoldView = LayoutInflater.from(context).inflate(layoutId, null);

        init();
    }
    
    public FloatView(int layoutId, Context context, String keyX, String keyY) {
        mWindowManager = (WindowManager) context.getApplicationContext()
                .getSystemService(Context.WINDOW_SERVICE);
        mHoldView = LayoutInflater.from(context).inflate(layoutId, null);
        
        setXYCoordinatePref(keyX, keyY);
        init();
    }
    
    protected Context getContext() {
        
        Context context = null;
        if(mHoldView != null) {
            context = mHoldView.getContext();
        }
        
        return context;
    }
    
    protected SuperBaseContext getBaseContext() {
        return mBaseContext;
    }
    
    protected Handler getHandler() {
        return mHandler;
    }
   
    protected void postRunnable(Runnable run) {
        if(mHandler != null) {
            mHandler.post(run);
        }
    }
    
    protected void removeCallbacks(Runnable run) {
        if(mHandler != null) {
            mHandler.removeCallbacks(run);
        }
    }
    
    protected void postDelayed(Runnable run, long delayMillis) {
        if(mHandler != null) {
            mHandler.postDelayed(run, delayMillis);
        }
    }
    
    protected View findViewById(int id) {
        return mHoldView.findViewById(id);
    }
    
    public void show() {
        //LogManager.d("FloatView", "show");
        
        if(!mIsShowing) {
            LogManager.i("show success");
            onShow(mParams, mHoldView, mDisplayMetrics);
            mWindowManager.addView(mHoldView, mParams);
            mIsShowing = true;
        }

    }
    
    
    public boolean isShown() {
        return mIsShowing;
    }
    
    public void updateState(int state, Object obj) {
//        switch() {
//        
//        }
    }
    
    public void dimss() {
        LogManager.d("FloatView", "dimss");
        if(mIsShowing) {
            LogManager.i("dimss success");
            onDimss();
            mWindowManager.removeViewImmediate(mHoldView);
            mIsShowing = false;
        }
    }
    
    protected void onDimss() {

    }
    
    protected void onShow(WindowManager.LayoutParams params, View holdView, DisplayMetrics dm) {

    }
    
    public void setBackgroundResource(int resId) {
        if(mHoldView != null) {
            mHoldView.setBackgroundResource(resId);
        }
    }
    
    public void setOnGestureListener(SimpleOnGestureListener l) {
        mSimpleOnGestureListener = l;
        mGestureDetector =  new GestureDetector(mHoldView.getContext(), l);
    }
    
    public void setXYCoordinatePref(String keyX, String keyY) {
        mKeyX = keyX;
        mKeyY = keyY;
    }
    
    
    private void init() {
        LogManager.d("FloatView", "init");
//        mHoldView.setOnKeyListener(new OnKeyListener() {
//
//            @Override
//            public boolean onKey(View v, int keyCode, KeyEvent event) {
//                //LogManager.d("FloatView", "onKey", msg)
//                return false;
//            }
//            
//        });
        mBaseContext = new SuperBaseContext(mHoldView.getContext());
        
        
        mHandler = mHoldView.getHandler();
        if(mHandler == null) {
            mHandler = new Handler();
        }
        
        mDisplayMetrics = new DisplayMetrics();
        mWindowManager.getDefaultDisplay().getMetrics(mDisplayMetrics);
        mParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_MEDIA;
        mParams.gravity = Gravity.LEFT | Gravity.TOP;
        mParams.format = PixelFormat.RGBA_8888;
        mParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        mParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        mParams.width = WindowManager.LayoutParams.WRAP_CONTENT;

        if(mKeyX != null && mKeyY != null) {
            mParams.x = mBaseContext.getPrefInteger(mKeyX);
            mParams.y = mBaseContext.getPrefInteger(mKeyY);
        }
        
        // mParams.flags=WindowManager.LayoutParams.FLAG_TOUCHABLE_WHEN_WAKING;
        if (mDisplayMetrics.heightPixels < mDisplayMetrics.widthPixels) {
            //mParams.screenOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
            //mParams.gravity = Gravity.CENTER;
//            mParams.x = 0;
//            mParams.y = 0;
//             mParams.y = (mDisplayMetrics.heightPixels - 400) / 2;
//            mParams.width = mDisplayMetrics.widthPixels;
 //           mParams.height = mDisplayMetrics.heightPixels - 20;
            //mParams.flags = WindowManager.LayoutParams.FLAG_BLUR_BEHIND;
        } else {
           // mParams.screenOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
           // mParams.gravity = Gravity.CENTER;
//            mParams.x = 0;
            //mParams.y = (mDisplayMetrics.heightPixels - 400) / 2;
//            mParams.width = (int) (mDisplayMetrics.widthPixels * 0.6);
 //           mParams.height = 400;
            //mParams.flags = WindowManager.LayoutParams.FLAG_BLUR_BEHIND;
        }

        
        
        mHoldView.setOnTouchListener(new OnTouchListener() {

            private boolean mIsProcessed = false;
            private float mX;
            private float mY;
            
            private boolean isNeedMove(MotionEvent event) {
              float x = event.getRawX();
              float y = event.getRawY();
//              float dis = (float) Math.sqrt(x * x + y * y);
              
              float dx = Math.abs(x - mX);
              float dy = Math.abs(y - mY);
              
              LogManager.d("OnTouchListener", "isNeedMove",  "dx:" + dx + ",dy:" + dy);

              if(dx < 38.1 && dy < 38.1) {
                  return false;
              }
              return true;
          }
            
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                //event.getX()
                float x = event.getRawX();
                float y = event.getRawY();

                LogManager.i("FloatView", "onTouch", "RawX:" + x + ",RawY:" + y);
                
                switch(event.getAction()) {
                case MotionEvent.ACTION_DOWN:

                    mTouchStartX = event.getX();
                    mTouchStartY = event.getY();
                    
                    mX = event.getRawX();
                    mY = event.getRawY();
                    
                    LogManager.i("FloatView", "onTouch", "ACTION_DOWN");
                    LogManager.i("FloatView", "onTouch", "mTouchStartX:" + mTouchStartX + ",mTouchStartY:" + mTouchStartY);
                    break;
                    
                case MotionEvent.ACTION_MOVE:
                    LogManager.w("ACTION_MOVE");
                    if(isNeedMove(event)) {
                        setPosition(x, y);
                    }
                    
                    break;
                case MotionEvent.ACTION_UP:
                
                    LogManager.w("ACTION_UP");
//                    mTouchStartX = event.getX();
//                    mTouchStartY = event.getY();
                    //setPosition(x, y);
                    mBaseContext.setPrefInteger(mKeyX, mParams.x);
                    mBaseContext.setPrefInteger(mKeyY, mParams.y);
                    
                    float dx = Math.abs(x - mX);
                    float dy = Math.abs(y - mY);
                    LogManager.w("dis:(" + dx + "," + dy + ")");
                    
                    if(dx < 1.0 && dy < 38.1) {
                        if(mSimpleOnGestureListener != null) {
                            mSimpleOnGestureListener.onSingleTapUp(event);
                        }
                        mIsProcessed = true;
                    }
                    
                    mTouchStartX = 0;
                    mTouchStartY = 0;
                    break;
                    
                }
                
                if(mGestureDetector != null && !mIsProcessed) {
                    return mGestureDetector.onTouchEvent(event);
                } else {
                    mIsProcessed = false;
                    return true;
                }
                
            }
            
        });
    }
    

    
    public void setPosition(float x, float y) {
        mParams.x = (int) (x - mTouchStartX);
        mParams.y = (int) (y - mTouchStartY);
        LogManager.i("FloatView", "setPosition", "mParamsX:" + mParams.x + ",ParamsY:" + mParams.y);
        mWindowManager.updateViewLayout(mHoldView, mParams);

    }
    
}
//ID20120528001 liaoyixuan end