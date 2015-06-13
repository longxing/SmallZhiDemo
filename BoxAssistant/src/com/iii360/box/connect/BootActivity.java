package com.iii360.box.connect;

import android.os.Bundle;

import com.iii360.box.R;
import com.iii360.box.base.BaseActivity;

/**
 * 盒子助手登陆界面
 * 
 * @author terry
 * 
 */
public class BootActivity extends BaseActivity {

	private OnLineBoxHandler onLineBoxHandler;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_boot);
		onLineBoxHandler = new OnLineBoxHandler(this);
		onLineBoxHandler.handle();
	}
	
	
	protected void onDestroy() {
		super.onDestroy();
		if (onLineBoxHandler == null)
			return;
		onLineBoxHandler.cleanMainThreadTask();
	}
}
