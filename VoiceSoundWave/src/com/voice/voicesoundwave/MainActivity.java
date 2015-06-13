package com.voice.voicesoundwave;

import com.voice.voicesoundwave.SoundWaveControl.StreamDecoderInterface;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends Activity {
	private EditText etSendContent;
	private Button btSend;
	private Button btLisent;
	private TextView tvResult;		
    private Button btStopSend;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		etSendContent = (EditText) findViewById(R.id.etSendContent);
		btSend = (Button) findViewById(R.id.btSend);
		btLisent = (Button) findViewById(R.id.btLisent);
		tvResult = (TextView) findViewById(R.id.tvResult);
		btStopSend = (Button) findViewById(R.id.btStopSend);
		btStopSend.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				SoundWaveControl.getInstance(MainActivity.this).stopSendData();
				tvResult.setText("test");
			}
			
		});
        btSend.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				tvResult.setText("test");
				SoundWaveControl.getInstance(MainActivity.this).sendData(etSendContent.getText().toString());
			}
        	
        });
        btLisent.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				tvResult.setText("test");
				SoundWaveControl.getInstance(MainActivity.this).listent(new StreamDecoderInterface() {

					@Override
					public void onResult(String result) {
						// TODO Auto-generated method stub
						if (result != null) {
							tvResult.setText(result);
						}
					}
					
				});
			}
        	
        });
	}
}
