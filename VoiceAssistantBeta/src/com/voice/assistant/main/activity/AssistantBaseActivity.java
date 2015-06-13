package com.voice.assistant.main.activity;

import android.content.ContentResolver;
import android.content.Context;
import android.os.Bundle;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import com.iii360.base.common.utl.BaseActivity;
import com.iii360.base.common.utl.BaseContext;
import com.iii360.base.common.utl.LogManager;
import com.iii360.base.inf.BasicServiceUnion;
import com.iii360.base.inf.parse.IVoiceCommand;
import com.iii360.base.inf.recognise.ILightController;
import com.voice.assistant.main.MyApplication;
import com.voice.assistant.main.R;

public class AssistantBaseActivity extends BaseActivity {

	/**
	 * 软键盘弹对应WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN
	 */
	protected static final int SOFT_INPUT_ADJUST_PAN = 1;
	/**
	 * 软键盘弹对应WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
	 */
	protected static final int SOFT_INPUT_ADJUST_RESIZE = 2;

	// protected ITextDisposer mTextDisposer;
	protected BasicServiceUnion mUnion;
	protected BaseContext mBaseContext;

	private long mLastTime;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		mBaseContext = new BaseContext(this);
	}

	protected void initBasicServiceUnion() {

		mLastTime = System.currentTimeMillis();
		final View engineUi = findViewById(R.id.recButton);

		MyApplication app = (MyApplication) getApplication();
		
		mUnion = app.getUnion();
		app.setIRecogniseDlg((ILightController) engineUi);
		//更新LED的状态
		app.updateLigthControl();
		engineUi.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mUnion.getRecogniseSystem().dispatchUserAction();
			}
		});

	}

	/**
	 * 隐藏输入框
	 * 
	 * @param view
	 */
	protected void hideTextInput(View view) {
		final InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
	}

	/**
	 * 显示输入框
	 * 
	 * @param view
	 */
	protected void showTextInput(View view) {
		if (view.requestFocus()) {
			final InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.showSoftInput(view, InputMethodManager.SHOW_FORCED);
		}
	}

	/**
	 * 
	 * @param mode
	 *            1:SOFT_INPUT_ADJUST_PAN 2:SOFT_INPUT_ADJUST_RESIZE
	 */
	protected void setInputMode(int mode) {
		if (mode == SOFT_INPUT_ADJUST_PAN) {
			getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
		} else if (mode == SOFT_INPUT_ADJUST_RESIZE) {
			getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
		}
	}

	/**
	 * 发送声音
	 * 
	 * @param text
	 * @param isShowText
	 *            是否显示文字
	 */
	protected void sendAnswerSession(String text, boolean isShowText) {
		if (mUnion.getTTSController() != null) {
			mUnion.getTTSController().play(text);
		}
	}

	/**
	 * 
	 * @param text
	 */
	protected void sendAnswerSessionNoTTs(String text) {
		LogManager.e("no tts " + text);
		// WidgetAnswer widgetAnswer = new WidgetAnswer(this, text);
		// widgetAnswer.setVoiceEnable(false);
		// if (mViewContainer != null) {
		// mViewContainer.pushNewWidget(widgetAnswer);
		// }
	}



	protected void executeCommand(IVoiceCommand voiceCommand) {
		voiceCommand.execute();
	}

	/**
	 * 设置亮度
	 */
	public void adjustScreenBrightness(int brightness) throws SettingNotFoundException {
		ContentResolver contentResolver = this.getContentResolver();
		int brightnessMode = Settings.System.getInt(contentResolver, Settings.System.SCREEN_BRIGHTNESS_MODE);
		if (brightnessMode == Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC) {
			Settings.System.putInt(contentResolver, Settings.System.SCREEN_BRIGHTNESS_MODE,
					Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
		}
		WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
		layoutParams.screenBrightness = (float) brightness / 255; //
		getWindow().setAttributes(layoutParams);
		android.provider.Settings.System.putInt(contentResolver, android.provider.Settings.System.SCREEN_BRIGHTNESS,
				brightness);
	}

	protected boolean isInputMethodActivie() {
		final InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
		return imm.isActive();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

}
