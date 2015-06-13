package com.iii360.box.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Handler;
import android.util.AttributeSet;
import android.widget.ImageView;

public class RotateImageView extends ImageView implements Runnable {
	private boolean isRound;

	public RotateImageView(Context context) {
		super(context);
	}

	public RotateImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public RotateImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	private int i;
	private double radiuX;
	private double radiuY;
	Bitmap bm;

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		int height = getMeasuredHeight();
		int width = getMeasuredWidth();
		canvas.rotate(i, (float) (width * radiuX), (float) (height * radiuY));
		if (isRound) {
			i += 1;
			if (i % 360 == 0)
				i = 0;
		}
		if (bm != null) {
			canvas.drawBitmap(bm, 0, 0, null);
		}

	}

	public void setBitmap(Bitmap bm) {
		this.bm = bm;
	}

	private boolean isDestory;

	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		isDestory = true;
	}

	public void startRound() {
		if (!isRound) {
			isRound = true;
			new Thread(RotateImageView.this) {
			}.start();
		}
	}

	public void setRoundRadiuX(double radiuX) {
		this.radiuX = radiuX;
	}

	public void setRoundRadiuY(double radiuY) {
		this.radiuY = radiuY;
	}

	public void roundCenter() {
		this.radiuY = this.radiuX = 0.5;
	}

	public void pauseRound() {
		isRound = false;
	}

	public boolean isRound() {
		return isRound;
	}

	public boolean inStartPosition() {
		if (i == 0)
			return true;
		return false;
	}

	private Handler handler = new Handler();

	@Override
	public void run() {
		while (!isDestory && isRound) {
			try {
				Thread.sleep(25);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			handler.post(new Runnable() {
				public void run() {
					invalidate();
				}
			});
		}
	}
}
