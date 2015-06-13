package com.smallzhi.homeappliances;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		final LinearLayout layout = (LinearLayout) LayoutInflater.from(getApplicationContext()).inflate(
				R.layout.activity_main, null);
		setContentView(layout);
	}
}
