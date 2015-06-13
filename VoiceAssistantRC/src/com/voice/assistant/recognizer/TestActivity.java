//ID20120723001 liuwen begin
package com.voice.assistant.recognizer;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.iii360.sup.common.utl.LogManager;
import com.parser.test.AutoTest;
import com.parser.test.AutoTest.OnTestCompletedListener;
import com.parser.test.CalculateResultChecker;
import com.parser.test.DefaultResultChecker;
import com.parser.test.RemindResultChecker;
import com.parser.test.ResultChecker;
import com.parser.test.SearchResultChecker;
import com.parser.test.TestResult;

public class TestActivity extends Activity {
	private Button mBtnConfirm;
	private Button mBtnConfirmAll;
	private TextView mTVparam;
	private Spinner mSpcategory;
	private ArrayAdapter<String> adapter;
	
	private TextView mTvTotalCnt;
	private TextView mTvMatchedCnt;
	private TextView mTvMatchedPer;
	private TextView mTvRunTime;
	private TextView mTvAvgTime;
	private CheckBox mCbReGenTestData;
	private CheckBox mCbNeedTrain;
	private CheckBox mCbReGenTestAllData;
    private CheckBox mCbNeedTrainAll;
	
    private FrameLayout mFrameLayout;
    private LinearLayout mUseLayout;
    private LinearLayout mPaticLayout;

	public final int ShowLoadint = 1;
    public final int ShowUse = 0;
	private Handler mHandler = new Handler(){
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case ShowUse:
                mUseLayout.setVisibility(View.VISIBLE);
                mFrameLayout.bringChildToFront(mUseLayout);
                mPaticLayout.setVisibility(View.GONE);
                break;
            case ShowLoadint:
                mPaticLayout.setVisibility(View.VISIBLE);
                mFrameLayout.bringChildToFront(mPaticLayout);
                mUseLayout.setVisibility(View.GONE);
                break;

            default:
                break;
            }
        };
	};
    private String[] category = { "打电话", "发短信", "播放", "天气", "打开网页或者应用", "读短信",
            "登录", "学习", "翻译", "关闭别的应用", "退出", "热点", "导航", "定位", "搜索", "备忘",
            "帮助", "下载应用", "删除应用", "播放控制", "计算" };


    private String[] mNames = { "CommandCall", "CommandSendSms",
            "CommandPlayMedia", "CommandQueryWeather", "CommandOpenAppAndWeb",
            "CommandReadSms", "CommandUser", "CommandStudy",
            "CommandTranslation", "CommandCloseApp", "CommandExtendExit",
            "CommandLocalHot", "CommandLocalNavi", "CommandLocal",
            "CommandSearch", "CommandRemind", "CommandHelp",
            "CommandDownloadApp", "CommandDeleteApp", "CommandMediaControl",
            "CommandCalculate" };
//ID20120721001 hujinrong end
	private void setTestResult(TestResult result) {
	    if(result != null) {
	        mTvTotalCnt.setText(String.valueOf(result._totalDataCnt));
	        mTvMatchedCnt.setText(String.valueOf(result._outputDataCnt));
	        mTvMatchedPer.setText(String.valueOf(result._matchPer));
	        mTvRunTime.setText(String.valueOf(result._runTime));
	        mTvAvgTime.setText(String.valueOf(result._averageTime));
	    }
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.test_activity);
		
		mFrameLayout = (FrameLayout) findViewById(R.id.FramLayout01);
        mUseLayout = (LinearLayout) findViewById(R.id.UseLayout);
        mPaticLayout = (LinearLayout) findViewById(R.id.PraticLayout);
        mFrameLayout.bringChildToFront(mUseLayout);
		
		mTvTotalCnt = (TextView) findViewById(R.id.tvTotalCnt);
		mTvMatchedCnt = (TextView) findViewById(R.id.tvMatchedCnt);
		mTvMatchedPer = (TextView) findViewById(R.id.tvMatchedPer);
		mTvRunTime = (TextView) findViewById(R.id.tvRunTime);
		mTvAvgTime = (TextView) findViewById(R.id.tvAvgTime);
		
		mBtnConfirm = (Button) findViewById(R.id.button1);
		mBtnConfirmAll = (Button) findViewById(R.id.button2);
		mTVparam = (TextView) findViewById(R.id.TextView03);
		mSpcategory = (Spinner) findViewById(R.id.Spainner01);
		
		mCbReGenTestData = (CheckBox)findViewById(R.id.checkBox1);
		mCbNeedTrain = (CheckBox)findViewById(R.id.checkBox2);
		mCbReGenTestAllData = (CheckBox)findViewById(R.id.checkBox3);
        mCbNeedTrainAll = (CheckBox)findViewById(R.id.checkBox4);
        
		adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, category);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		mSpcategory.setAdapter(adapter);
		mSpcategory.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                    int position, long id) {
                // TODO Auto-generated method stub
                mTVparam.setText(mNames[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub

            }
        });
		

        mBtnConfirm.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                String commandName = mTVparam.getText().toString();
                if(commandName != null && !commandName.equals("")) {
                    mBtnConfirm.setEnabled(false);
                    AutoTest.initTest(new OnTestCompletedListener() {

                        @Override
                        public void onTestCompleted(final TestResult result) {
                            if(result != null) {
                                LogManager.i(result.toString());
                                mHandler.post(new Runnable() {

                                    @Override
                                    public void run() {
                                        setTestResult(result);
                                        mBtnConfirm.setEnabled(true);
                                    }
                                    
                                });
                            }
                            
                            
                        }
                        
                    }, TestActivity.this);
// ID20120721001 hujinrong begin
                    ResultChecker checker = new DefaultResultChecker();
                    if (commandName.equals("CommandCalculate")) {
                        checker = new CalculateResultChecker();
                    } else if (commandName.equals("CommandSearch")) {
                        checker = new SearchResultChecker();
                    } else if (commandName.equals("CommandRemind")) {
                        checker = new RemindResultChecker();
                    }

                    AutoTest.runTestParser(TestActivity.this, commandName,
                            checker, mCbReGenTestData.isChecked(),
                            mCbNeedTrain.isChecked());
// ID20120721001 hujinrong end
                }
            }
        });
        

		
        mBtnConfirmAll.setOnClickListener(new OnClickListener(){

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                LogManager.d(""+mCbReGenTestAllData.isChecked()+""+mCbNeedTrainAll.isChecked());
                String commandName = mTVparam.getText().toString();
                if(commandName != null && !commandName.equals("")) {
                    mBtnConfirmAll.setEnabled(false);
                    AutoTest.initTest(new OnTestCompletedListener() {

                        @Override
                        public void onTestCompleted(final TestResult result) {
                            if(result != null) {
                                LogManager.i(result.toString());
                                mHandler.post(new Runnable() {

                                    @Override
                                    public void run() {
                                        setTestResult(result);
                                        mBtnConfirmAll.setEnabled(true);
                                    }
                                    
                                });
                            }
                            
                            
                        }
                        
                    },TestActivity.this);
                    
//                    AutoTest.runIntegrationTesting(infoList, new DefaultResultChecker(), mCbReGenTestAllData.isChecked(), mCbNeedTrainAll.isChecked());
                }
            }
            
        });

	}
}
//ID20120723001 liuwen end
