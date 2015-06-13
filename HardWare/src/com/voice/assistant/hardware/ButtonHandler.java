package com.voice.assistant.hardware;

import com.iii360.base.common.utl.LogManager;

public abstract class ButtonHandler {

	public enum TouchStatus {
		TOUCH_BEGIN, TOUCH_END, TOUCH_MOVE, TOUCH_ED;
	}

	protected long touchBeginTime;
	protected boolean isNewTouch;
	protected boolean isCalledClickInTouch = true;// 长按住不放
	protected boolean isCalledLongClickInTouch = true;// 非常久按住不放

	private static final String TAG = "HardWare ButtonHandler";
	private static final int LONG_CLICK_TIME = 1000;
	private static final int LONG_LONG_CLICK_TIME = 5000;

	/**
	 * 请在替换前调用，防止之前的长按键重复
	 */
	public void prepare() {
		isCalledClickInTouch = true;
		isCalledLongClickInTouch = true;
	}

	public void onStatusClick(TouchStatus status) {
		switch (status) {
		case TOUCH_BEGIN:
			isNewTouch = true;
			touchBeginTime = System.currentTimeMillis();
			isCalledClickInTouch = false;
			isCalledLongClickInTouch = false;
			this.onTouchBegin();
			break;
		case TOUCH_ED:
			if ((System.currentTimeMillis() - touchBeginTime) > LONG_CLICK_TIME && !isCalledClickInTouch) {
				isCalledClickInTouch = true;
				LogManager.d(TAG, "onClickInTouch...");
				onClickInTouch();
			} else if ((System.currentTimeMillis() - touchBeginTime) > LONG_LONG_CLICK_TIME && !isCalledLongClickInTouch) {
				isCalledLongClickInTouch = true;
				LogManager.d(TAG, "onLongClickInTouch...");
				onLongClickInTouch();
			}
			break;
		case TOUCH_END: // 开始按键与结束按键值不一样
			this.onTouchEnd();
			if (System.currentTimeMillis() - touchBeginTime < LONG_CLICK_TIME) {
				onShortClick();
			} else if ((System.currentTimeMillis() - touchBeginTime) > LONG_CLICK_TIME && (System.currentTimeMillis() - touchBeginTime) < LONG_LONG_CLICK_TIME) {
				LogManager.d(TAG, "onLongClick...");
				onLongClick();
			} else {
				LogManager.d(TAG, "onLongLongClick...");
				onLongLongClick();
			}

			break;

		default:
			break;
		}
	}

	public void onTouchBegin() {

	}

	public void onTouchEnd() {

	}

	public void onTouch() {

	}

	public abstract void onShortClick();

	public abstract void onLongClick();

	public abstract void onLongLongClick();

	public abstract void onClickInTouch();

	public abstract void onLongClickInTouch();
}
