package com.smallzhi.TTS.Main;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.smallzhi.TTS.Auxiliary.PacageNotFoundException;
import com.smallzhi.TTS.Engine.ITTSStatusListen;
import com.smallzhi.TTS.Engine.TTSPlayerFactory;
import com.voice.assistant.main.tts.R;

public class MainActivity extends Activity {
    TTSSameple sameple;
    private Button mButton1;
    private Button mButton2;
    private TextView mTextView;
    private Spinner mSpinner;
    private EditText mEditText;
    private static final String[] m = {"讯飞本地", "讯飞联网"};

    private ITTSStatusListen mListen = new ITTSStatusListen() {

        @Override
        public void onError() {
            // TODO Auto-generated method stub
            runOnUiThread(new Runnable() {
                public void run() {
                    mTextView.setText("onError");
                }
            });
        }

        @Override
        public void onEnd() {
            // TODO Auto-generated method stub
            runOnUiThread(new Runnable() {
                public void run() {
                    mTextView.setText("onEnd");
                }
            });
        }

        @Override
        public void onBegin() {
            // TODO Auto-generated method stub
            runOnUiThread(new Runnable() {
                public void run() {
                    mTextView.setText("onBegin");
                }
            });
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mButton1 = (Button) findViewById(R.id.button1);
        mButton2 = (Button) findViewById(R.id.button2);
        mTextView = (TextView) findViewById(R.id.textView1);
        mSpinner = (Spinner) findViewById(R.id.spinner1);
        mEditText = (EditText) findViewById(R.id.editText1);
        // 将可选内容与ArrayAdapter连接起来
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, m);

        // 设置下拉列表的风格
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // 将adapter 添加到spinner中
        mSpinner.setAdapter(adapter);

        mSpinner.setSelection(0);
        try {
            TTSSameple.initContext(this);
            sameple = new TTSSameple();
            sameple.setListen(mListen);

        } catch (PacageNotFoundException e) {
            // TODO Auto-generated catch block
            mTextView.setText("PacageNotFoundException " + e.toString());
        }

        // 添加事件Spinner事件监听
        mSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int enginetype = 0;
                switch (position) {
                case 3:
                    enginetype = TTSPlayerFactory.TYPE_XUNFEI;
                    break;
                case 4:
                    enginetype = TTSPlayerFactory.TYPE_XUNFEINET;
                    break;
                default:
                    break;
                }

                try {
                    if (sameple != null)
                        sameple.stop();
                    sameple = new TTSSameple(enginetype);
                    sameple.setListen(mListen);
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub

            }
        });

        // 设置默认值
        mSpinner.setVisibility(View.VISIBLE);

        mButton1.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                sameple.play(mEditText.getText().toString());
            }
        });

        mButton2.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                sameple.stop();
            }
        });

    }

    /*
     * (non-Javadoc)
     * 
     * @see android.app.Activity#onStop()
     */
    @Override
    protected void onStop() {
        // TODO Auto-generated method stub
        super.onStop();
        sameple.stop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

}
