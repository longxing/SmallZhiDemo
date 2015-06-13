package com.iii360.box.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Handler;
import android.util.AttributeSet;
import android.widget.ImageView;

public class RotateReleaseImageView extends ImageView implements Runnable {
	private boolean isRound;
	private int maxDegrees;
	private boolean isBack;

	public RotateReleaseImageView(Context context) {
		super(context);
	}

	public RotateReleaseImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public RotateReleaseImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		pauseRound();
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
		if (isRound && !isBack) {
			i += 1;
			if (i % 360 == 0)
				i = 0;
		} else {
			i -= 1;
		}
		if (bm != null) {
			canvas.drawBitmap(bm, 0, 0, null);
		}

	}

	public void setBitmap(Bitmap bm) {
		this.bm = bm;
	}

	public void startRound() {
		try {
			if (maxDegrees == 0) {
				throw new Exception("not degress");
			}
		} catch (Exception e) {
			// e.printStackTrace();
			return;
		}
		try {
			if (i >= maxDegrees) {
				throw new Exception("is max degress");
			}
		} catch (Exception e) {
			// e.printStackTrace();
			return;
		}
		if (!isRound) {
			isRound = true;
			new Thread(RotateReleaseImageView.this) {
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

	private Handler handler = new Handler();

	@Override
	public void run() {
		while (isRound) {
			try {
				Thread.sleep(25);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			if (isBack) {
				if (i <= 0) {
					isBack = false;
					isRound = false;
					break;

				}
			} else {
				if (i >= maxDegrees) {
					isRound = false;
					isBack = false;
					break;
				}
			}
			handler.post(new Runnable() {
				public void run() {
					invalidate();
				}
			});
		}
	}

	public void setMaxDegrees(int j) {
		maxDegrees = j;
	}

	public void release() {
		if (!isRound && !isBack) {
			isBack = true;
			isRound = true;
			new Thread(RotateReleaseImageView.this) {
			}.start();
		}
	}
}
