//ID20120504003 liaoyixuan begin
package com.voice.recognise.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.voice.assistant.recognizer.R;
import com.voice.recognise.IRecogniseDlg;

public class SoundView extends ImageView {

    private static final float AUDIO_METER_DB_RANGE = 20.0F;
    private static final float AUDIO_METER_MIN_DB = 7.0F;
    private static final float DOWN_SMOOTHING_FACTOR = 0.4F;
    private static final long FRAME_DELAY = 50L;
    private static final float UP_SMOOTHING_FACTOR = 0.9F;
    
    private static final int BITMAP_ID_INIT = 0;
    private static final int BITMAP_ID_WORKING = 1;
    private static final int BITMAP_ID_NORMAL = 2;
    private static final int BITMAP_ID_ERROR = 3;
    
    private int mState = IRecogniseDlg.RECOGNISE_STATE_NORMAL;
    
    private Matrix mMatrix = new Matrix();
    private Paint mPaint = new Paint();
    private float mRotate;
    private float mSpeed = 3;
    private Bitmap[] mBitmap;
    
    private Canvas mBufferCanvas;
    private Paint mClearPaint;
    private Paint mMultPaint;
    private Runnable mDrawFrameRunnable;
    private Bitmap mDrawingBuffer;
    private Bitmap mEdgeBitmap;
    private int mEdgeBitmapOffset;
    private Drawable mFrontDrawable;
    private Handler mHandler = new Handler();
    private float mLevel = 0.0F;
    
    
    public SoundView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public SoundView(Context context) {
        super(context);
    }

    public SoundView(Context context, AttributeSet attrs) {
        super(context, attrs);
        
        mBitmap = new Bitmap[] {
                BitmapFactory.decodeResource(getResources(), R.drawable.rec_initing), 
                BitmapFactory.decodeResource(getResources(), R.drawable.rec_working),
                BitmapFactory.decodeResource(getResources(), R.drawable.rec_background),
                BitmapFactory.decodeResource(getResources(), R.drawable.rec_error_out),
        };
        
        mDrawFrameRunnable = new Runnable() {

            @Override
            public void run() {
                invalidate();
                mHandler.postDelayed(this, FRAME_DELAY);
            }
            
        };
        mPaint.setAntiAlias(true);
        mFrontDrawable = getDrawable();
        mEdgeBitmap = ((BitmapDrawable) context.getResources().getDrawable(R.drawable.rec_animation)).getBitmap();

        mEdgeBitmapOffset = mEdgeBitmap.getHeight() / 2;

        mDrawingBuffer = Bitmap.createBitmap(mFrontDrawable.getIntrinsicWidth(),
                                             mFrontDrawable.getIntrinsicHeight(), 
                                             Bitmap.Config.ARGB_8888);
        
        mBufferCanvas = new Canvas(mDrawingBuffer);
        mClearPaint = new Paint();
        mClearPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        mMultPaint = new Paint();
        mMultPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.MULTIPLY));
    }
    
    
    
    @Override
    protected void onDraw(Canvas canvas) {

        //LogManager.d("SoundView", "onDraw");
  
        switch(mState) {
        case IRecogniseDlg.RECOGNISE_STATE_NORMAL:
            drawBitMap(BITMAP_ID_NORMAL, canvas);
            break;
        case IRecogniseDlg.RECOGNISE_STATE_INIT:
            drawCycle(BITMAP_ID_INIT, canvas);
            break;
        case IRecogniseDlg.RECOGNISE_STATE_BEGIN_SPEECH:
            break;
        case IRecogniseDlg.RECOGNISE_STATE_SPEECHING:
            drawVoiceLevel(canvas);
            break;
        case IRecogniseDlg.RECOGNISE_STATE_REQUEST:
            drawCycle(BITMAP_ID_WORKING, canvas);
            break;
        case IRecogniseDlg.RECOGNISE_STATE_END:
            break;
        case IRecogniseDlg.RECOGNISE_STATE_ERROR:
            drawBitMap(BITMAP_ID_ERROR, canvas);
            break;
        
        }
    }
    
    private void drawVoiceLevel(Canvas canvas) {
        float width = getWidth();
        float hight = getHeight();
        
        mBufferCanvas.drawRect(0.0F, 0.0F, width, hight, mClearPaint);

        double d2 = 1.0D - mLevel;
        double d3 = mEdgeBitmapOffset + hight;
        
        int k = (int)(d2 * d3) - mEdgeBitmapOffset;

        Rect rect = new Rect(0, k, (int) width, (int) hight);
        mBufferCanvas.save();
        mBufferCanvas.clipRect(rect);

        mFrontDrawable.setBounds(new Rect(0, 0, (int) width, (int) hight));
        mFrontDrawable.draw(mBufferCanvas);
        mBufferCanvas.restore();

        mBufferCanvas.drawBitmap(mEdgeBitmap, 0.0F, rect.top, mMultPaint);

        canvas.drawBitmap(mDrawingBuffer, 0.0F, 0.0F, null);
    }
    
    private void drawBitMap(int bitmapId, Canvas canvas) {
        float width = getWidth();
        float hight = getHeight();
        
        canvas.drawRect(0.0F, 0.0F, width, hight, mClearPaint);
        canvas.drawBitmap(mBitmap[bitmapId], 0.0F, 0.0F, null);
        
    }
    
    private void drawCycle(int bitmapId, Canvas canvas) {
        int hight = getHeight();
        int width = getWidth();
        float xf = width / 2;
        float yf = hight / 2;

        mMatrix.setRotate(mRotate, xf, yf);
        
        
        mRotate += mSpeed;
        if (mRotate >= 360) {
            mRotate = 0;
        }
        
        canvas.drawBitmap(mBitmap[bitmapId], mMatrix, mPaint);

    }

    public void update(float soundDb, int state) {
        float f1 = (soundDb - AUDIO_METER_MIN_DB) / AUDIO_METER_DB_RANGE;
        float f2 = Math.min(Math.max(0.0F, f1), 1.0F);
        mState = state;
        
//        if(state == FloatButton.RECOGNISE_STATE_INIT)  {
//            start();
//        }
        
        if(mLevel < f2) {
            mLevel += (f2 - mLevel) * UP_SMOOTHING_FACTOR;
        } else {
            mLevel += (f2 - mLevel) * DOWN_SMOOTHING_FACTOR;
        }
        
        postInvalidate();
    }
    
    public void start() {
        mHandler.post(mDrawFrameRunnable);
    }
    
    public void stop() {
        //postInvalidateDelayed(0);
        
        mHandler.removeCallbacks(mDrawFrameRunnable);
    }
    

}
//ID20120504003 liaoyixuan end