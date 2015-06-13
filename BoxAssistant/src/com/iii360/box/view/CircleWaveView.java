package com.iii360.box.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class CircleWaveView extends View implements Runnable {

	private float centerX = -1; // 圆心X
	private float centerY = -1; // 圆心Y
	private float floatRadius; // 变化的半径
	private float maxRadius = -1; // 圆半径
	private float minRadius = -1;
	private volatile boolean started = false;
	private Paint mLinePaint;
	// private Paint mSolidPaint;
	private int waveColor = Color.argb(200, 0, 0, 0); // 颜色

	// private int waveInterval = 100; //圆环的宽度

	public CircleWaveView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public CircleWaveView(Context context) {
		this(context, null, 0);
	}

	public CircleWaveView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initView();
	}

	private void initView() {
		mLinePaint = new Paint();
		// mSolidPaint = new Paint();
		init();
	}

	public void init() {
		mLinePaint.setAntiAlias(true);
		mLinePaint.setStrokeWidth(1.5F);
		mLinePaint.setStyle(Paint.Style.STROKE);
		mLinePaint.setColor(waveColor);
		floatRadius = minRadius;
		start();
	}

	public void start() {
		if (!started) {
			started = true;
			new Thread(this).start();
		}
	}

	public void stop() {
		started = false;
	}

	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		stop();
	}

	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if (floatRadius == -1) {
			floatRadius = minRadius;
		}
		if (maxRadius <= 0.0F || centerX == -1f || centerY == -1f || minRadius == -1) {
			return;
		}
		int alpha = (int) (255.0F * (1.0F - floatRadius / maxRadius));
		mLinePaint.setAlpha(alpha);
		canvas.drawCircle(centerX, centerY, floatRadius, mLinePaint);
	}

	public void onWindowFocusChanged(boolean hasWindowFocus) {
		super.onWindowFocusChanged(hasWindowFocus);
		if (hasWindowFocus) {
			init();
		} else {
			stop();
		}
	}

	public void run() {
		while (started) {
			floatRadius = 1.0F + floatRadius;
			if (floatRadius > maxRadius) {
				floatRadius = minRadius;
			}
			postInvalidate();
			try {
				Thread.sleep(20L);
			} catch (InterruptedException localInterruptedException) {
				localInterruptedException.printStackTrace();
			}
		}
	}

	public void setMaxRadius(float maxRadius) {
		this.maxRadius = maxRadius;
	}

	public void setWaveColor(int waveColor) {
		this.waveColor = waveColor;
	}

	public void setMinRadius(float minRadius) {
		this.minRadius = minRadius;
	}

	public void setFloatRadius(float floatRadius) {
		this.floatRadius = floatRadius;
	}

	public void setCenter(float x, float y) {
		centerX = x;
		centerY = y;
	}
}
