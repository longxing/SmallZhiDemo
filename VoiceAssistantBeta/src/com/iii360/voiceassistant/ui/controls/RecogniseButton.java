package com.iii360.voiceassistant.ui.controls;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BlurMaskFilter;
import android.graphics.BlurMaskFilter.Blur;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.iii360.base.common.utl.LogManager;
import com.iii360.base.inf.recognise.ILightController;
import com.voice.assistant.main.R;
import com.voice.recognise.IRecogniseDlg;

public class RecogniseButton extends ImageView implements ILightController {

	public static final int LENGTH = 270;
	private static final float FIRST_POINT_RADIUS = 4;
	private static final int STEP = 12;

	private int mNowLength;
	private int mAngel = 90;
	private Bitmap mHaloTemp;
	private Bitmap mHaloArcCcw;

	private Paint mPaint;
	private float mScale;
	private int mState = RECOGNISE_STATE_NORMAL;
	private int mVoiceLevel;
	private Bitmap mBackBitmap;

	private boolean mIsNoNeedUpdate;
	private float mRaduisArray[];
	
	
	
	public RecogniseButton(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		init();
	}

	public RecogniseButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		init();
	}

	public RecogniseButton(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
		init();
	}

	private void init() {
		mBackBitmap = getBitmap(R.drawable.recbtn_normal);
		mScale = mBackBitmap.getWidth() / 109.0f;
		mRaduisArray = radius(LENGTH);
		mPaint = new Paint();
		mPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
		mPaint.setAntiAlias(true);
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				// initRefs();
				initBuffer();
			}
		}).start();
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// TODO Auto-generated method stub
		setMeasuredDimension(mBackBitmap.getWidth(), mBackBitmap.getHeight());
	}

	private void initBuffer() {
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);
		long time = System.currentTimeMillis();
		if (mIsNoNeedUpdate) {
			drawMoveCircle(canvas);
			return;
		}
		switch (mState) {
		case IRecogniseDlg.RECOGNISE_STATE_INIT:
			drawInit(canvas);
			mAngel = 90;
			mNowLength = 0;
			break;
		case ILightController.RECOGNISE_STATE_VOICE_LEVEL_CHANGE:
			drawVoiceLevel(canvas, mVoiceLevel);
			invalidate();
			break;
		case ILightController.RECOGNISE_STATE_RECONISING:
			long time1 = System.currentTimeMillis();
			drawMoveCircle(canvas);
			break;
		case ILightController.RECOGNISE_STATE_SUCCESS:
			drawInit(canvas);
			mState = RECOGNISE_STATE_NORMAL;
			postInvalidateDelayed(500);
			break;
		case ILightController.RECOGNISE_STATE_ERROR:
			drawInit(canvas);
			mState = RECOGNISE_STATE_NORMAL;
			postInvalidateDelayed(500);
			break;
		case IRecogniseDlg.RECOGNISE_STATE_NORMAL:
			drawNormal(canvas);
			break;
//		case IRecogniseDlg.BUTTON_STATE_PRESSED:
//			drawButtonPressed(canvas);
//			break;
		}

	}

	@Override
	public int getState() {
		// TODO Auto-generated method stub
		return 0;
	}

	private void drawButtonPressed(Canvas canvas) {
		canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
		canvas.drawBitmap(getBitmap(R.drawable.recbtn_pressed), new Matrix(), mPaint);
	}

	private void drawNormal(Canvas canvas) {
		canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
		Bitmap bitmap = getBitmap(R.drawable.recbtn_normal);
		canvas.drawBitmap(bitmap, new Matrix(), mPaint);
	}

	private void drawMoveCircle(Canvas canvas) {
		mIsNoNeedUpdate = true;
		if (mNowLength < LENGTH) {

			mAngel -= STEP;
			mNowLength += STEP;
			if (mNowLength > LENGTH)
				mNowLength = LENGTH;
			drawArcFromBig(canvas, mAngel, mNowLength, true);
		} else {
			drawArcUseBitmap(canvas, mAngel);
			mAngel -= STEP;

		}
		if (mNowLength >= (LENGTH / 2)) {
			mIsNoNeedUpdate = false;
			if (mState == RECOGNISE_STATE_SUCCESS || mState == RECOGNISE_STATE_ERROR
					|| mState == RECOGNISE_STATE_NORMAL) {
				mState = RECOGNISE_STATE_NORMAL;

			}
		}
		invalidate();
	}

	private void drawArcFromBig(Canvas canvas, int startAngel, int length, boolean cw) {
		if (length == STEP) {
			mHaloTemp = Bitmap.createBitmap(mBackBitmap.getWidth(), mBackBitmap.getHeight(), Config.ARGB_8888);
		}
		int where = length / STEP - 1;
		Canvas tempCanvas = new Canvas(mHaloTemp);
		for (int i = where * STEP; i < (where + 1) * STEP; i++) {
			float[] position = position(i);
			drawPoint(tempCanvas, position[0], position[1], mRaduisArray[i], i);
		}
		Matrix matrix = new Matrix();
		matrix.setRotate(-mAngel, 55 * mScale, 52 * mScale);
		canvas.drawBitmap(getBitmap(R.drawable.recbtn_normal), new Matrix(), new Paint());
		canvas.drawBitmap(mHaloTemp, matrix, new Paint());
	}

	private void drawArcUseBitmap(Canvas canvas, int mAngel2) {
		Matrix matrix = new Matrix();
		matrix.setRotate(-mAngel2, 55 * mScale, 52 * mScale);
		Bitmap halo = mHaloArcCcw;
		canvas.drawBitmap(getBitmap(R.drawable.recbtn_normal), new Matrix(), new Paint());
		canvas.drawBitmap(halo, matrix, new Paint());
	}

	private void drawVoiceLevel(Canvas canvas, int mVoiceLevel2) {
		try {
			// canvas.setDrawFilter(new PaintFlagsDrawFilter(0,
			// Paint.ANTI_ALIAS_FLAG
			// | Paint.FILTER_BITMAP_FLAG));
			// canvas.drawBitmap(mVoiceLevelSotfRef[mVoiceLevel2].get(), new
			// Matrix(),
			// mPaint);
		} catch (Exception e) {
			LogManager.printStackTrace(e);
		}
	}

	private void drawInit(Canvas canvas) {
		canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
		Bitmap bitmap = getBitmap(R.drawable.recbtn_init);
		canvas.drawBitmap(bitmap, new Matrix(), mPaint);
	}

	private void drawPoint(Canvas canvas, float x, float y, float radius, int k) {
		Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
		p.setAntiAlias(true);
		p.setColor(Color.parseColor("#FFFFFF"));
		k = Math.abs(k);
		if (k == 0 || k == LENGTH || k == LENGTH - 1 || k % 4 == 0) {

			p.setMaskFilter(new BlurMaskFilter(radius, Blur.SOLID));

			canvas.drawCircle(x, y, radius, p);
			p.setColor(Color.parseColor("#00BAFF"));
			p.setMaskFilter(new BlurMaskFilter(radius * 2.8f, Blur.OUTER));
			canvas.drawCircle(x, y, radius, p);
		} else {
			canvas.drawCircle(x, y, radius, p);
		}

	}

	private float[] position(int angel) {
		float x = (float) (mScale * (55 + 30 * Math.cos(angel * Math.PI / 180)));
		float y = (float) (mScale * (52 - 30 * Math.sin(angel * Math.PI / 180)));
		return new float[] { x, y };
	}

	private float[] radius(int length) {
		float[] radiusArray = new float[length];
		float firstSize = FIRST_POINT_RADIUS;
		float step = firstSize / length;
		radiusArray[0] = firstSize;
		for (int i = 1; i < length; i++) {
			radiusArray[i] = firstSize - i * step;
		}
		return radiusArray;
	}

	@Override
	public void updateState(int state, Object params) {
		// TODO Auto-generated method stub
		mState = state;
		if (mState == RECOGNISE_STATE_VOICE_LEVEL_CHANGE) {
			mVoiceLevel = (Integer) params;
		}
		invalidate();
	}

	private Bitmap getBitmap(int res) {
		// for (int i = 0; otherResSotfRef != null && i <
		// otherResSotfRef.length; i++) {
		// if ( otherRes[i] == res ) {
		// if (null == otherResSotfRef[i]) {
		// return null;
		// } else {
		// return otherResSotfRef[i].get();
		// }
		// }
		// }
		return BitmapFactory.decodeResource(getResources(), res);
	}

	@Override
	public void updateMode(int mode) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getMode() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void reconiseStartAnimation() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void reconiseStopAnimation() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void updateStateOnRunnable(int state, Object params) {
		// TODO Auto-generated method stub
		
	}

}
