package com.voice.assistant.recognizer;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.base.data.CommandInfo;
import com.base.platform.OnDataReceivedListener;
import com.base.platform.Platform;
import com.base.util.KeyManager;
import com.iii360.sup.common.utl.LogManager;
import com.parser.iengine.RequestParams;
import com.parser.iengine.crf.CRFUtil;

public class TrainActivity extends Activity {
    private Platform mPlatform;
	private Button mBtnconfirm;
	private Button mBtncancel;
	private TextView mTVparam;
	private EditText mEdcontent;
	private Spinner mSpcategory;
	private ArrayAdapter<String> adapter;
	private int mDef = 0;
	private CommandInfo mInfo;
	private String mSummary;
	private String mArgs = "";
	private EditText mEdreconize;
	private Button mBtnReconize;
	private Button mBtnPractisce;

	private FrameLayout mFrameLayout;
	private LinearLayout mUseLayout;
	private LinearLayout mPaticLayout;

	private Button mBtnAddleft;
	private Button mBtnAddRight;
	private Spinner mSpAddArgs;
	private ArrayAdapter<String> mArgsadapter;

	private Button mBtnMoveLeft;
	private Button mBtnMoveRight;
	private Button mBtnDelete;

	private int mArgPosition = 0;

	private String[] category = { "打电话", "发短信", "播放", "天气", "打开网页或者应用", "读短信",
			"登录", "学习", "翻译", "关闭别的应用", "退出", "热点", "导航", "定位", "搜索", "备忘",
			"帮助", "下载应用", "删除应用","播放控制" };

	private String[] mNames = { "CommandCall", "CommandSendSms",
			"CommandPlayMedia", "CommandQueryWeather", "CommandOpenAppAndWeb",
			"CommandReadSms", "CommandUser", "CommandStudy",
			"CommandTranslation", "CommandCloseApp", "CommandExtendExit",
			"CommandLocalHot", "CommandLocalNavi", "CommandLocal",
			"CommandSearch", "CommandRemind", "CommandHelp",
			"CommandDownloadApp", "CommandDeleteApp","CommandMediaControl" };

	private String[] numberArgs = { "0", "1", "2", "3", "4", "5", "6" };

	public final int ShowLoadint = 1;
	public final int ShowUse = 0;

	Handler mHandler = new Handler() {
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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.train_activity);
		mBtnconfirm = (Button) findViewById(R.id.button1);
		mBtncancel = (Button) findViewById(R.id.button2);
		mTVparam = (TextView) findViewById(R.id.TextView03);
		mEdcontent = (EditText) findViewById(R.id.EditText02);
		mSpcategory = (Spinner) findViewById(R.id.Spainner01);

		Intent mReciver = getIntent();
		if (mReciver != null) {
			String param = mReciver.getStringExtra(KeyManager.EKEY_COMMAND_PARAM);
			mInfo = new CommandInfo(param);
			Toast.makeText(getApplicationContext(), mInfo.toString(),
					Toast.LENGTH_LONG).show();

		}

		adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, category);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		mSpcategory.setAdapter(adapter);

		mArgsadapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, numberArgs);
		mArgsadapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		mEdreconize = (EditText) findViewById(R.id.EditText04);
		mBtnReconize = (Button) findViewById(R.id.button4);
		mBtnPractisce = (Button) findViewById(R.id.button3);

		mFrameLayout = (FrameLayout) findViewById(R.id.FramLayout01);
		mUseLayout = (LinearLayout) findViewById(R.id.UseLayout);
		mPaticLayout = (LinearLayout) findViewById(R.id.PraticLayout);
		mFrameLayout.bringChildToFront(mUseLayout);

		mBtnAddleft = (Button) findViewById(R.id.button7);
		mBtnAddRight = (Button) findViewById(R.id.button8);
		mSpAddArgs = (Spinner) findViewById(R.id.Spainner02);
		mSpAddArgs.setAdapter(mArgsadapter);
		mSpAddArgs.setSelection(0, true);
		mBtnMoveLeft = (Button) findViewById(R.id.button10);
		mBtnMoveRight = (Button) findViewById(R.id.button11);
		mBtnDelete = (Button) findViewById(R.id.button12);

		
		mPlatform = Platform.getPlatformInstance(this, new OnDataReceivedListener() {

            @Override
            public void onDataReceived(CommandInfo cmdInfo) {
                handCommand(cmdInfo);
                
            }

            @Override
            public void onError(int errorCode) {
                // TODO Auto-generated method stub
                
            }
		    
		});
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		set();
	}
	
	private void handCommand(CommandInfo command) {
	    mInfo = command;
        if (command != null && command._question != null && !command._question.equals("null")) {
            Toast.makeText(getApplicationContext(), command.toString(),
                    Toast.LENGTH_LONG).show();
        }
        set();
	}

	@Override
	protected void onNewIntent(Intent intent) {
		// TODO Auto-generated method stub
		super.onNewIntent(intent);
		CommandInfo command = null;
		if (intent != null) {
			String param = intent.getStringExtra(KeyManager.EKEY_COMMAND_PARAM);
			command = new CommandInfo(param);

		}
		handCommand(command);
		
	}

	public void set() {
		mDef = get(mInfo._commandName);
		if (mDef >= 0) {
			mSpcategory.setSelection(mDef, true);
		}
		mSummary = mInfo._question;
		mEdreconize.setText(mSummary);
		int i = 0;
		mArgs = "";
		while (mInfo.getArg(i) != null) {
			if (!mInfo.getArg(i).equals("")) {
				mSummary = mSummary.replace(mInfo.getArg(i),
						"[" + mInfo.getArg(i) + ",arg" + String.valueOf(i)
								+ "]");
				mArgs = mArgs + " [arg" + String.valueOf(i) + " "
						+ mInfo.getArg(i) + "] ";
			}

			i++;
		}
		LogManager.i("after" + mSummary);
		mEdcontent.setText(mSummary);
		mTVparam.setText(mArgs);

		mSpcategory.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				mDef = position;
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub

			}
		});

		// mEdcontent.setText(mInfo._summary);

		mBtncancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();

			}
		});

		mBtnconfirm.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				boolean isconfirm = true;
				String st = mEdcontent.getText().toString();
				for (int i = 0; i < st.length(); i++) {
					if ((int) st.charAt(i) == (int) '[') {
						if (isconfirm) {
							isconfirm = false;
						} else {
							return;
						}
					} else if ((int) st.charAt(i) == (int) ']') {
						if (!isconfirm) {
							isconfirm = true;
						} else {
							return;
						}
					}
				}
				if (isconfirm) {
					showAddDialog(st);
				} else {
					Toast.makeText(getApplicationContext(), "保存失败，请检查参数",
							Toast.LENGTH_LONG).show();
				}

			}
		});

		mBtnReconize.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String recContent = mEdreconize.getText().toString();
				if (recContent.equals("")) {
					Toast.makeText(getApplicationContext(), "输入为空，请检查",
							Toast.LENGTH_LONG).show();
				} else {
				    
//					Intent intent = new Intent();
//					intent.setAction(KeyList.AKEY_PARSE_INPUT);
//					intent.putExtra(KeyList.EKEY_RECOGNISE_RESULT, recContent);
//					sendOrderedBroadcast(intent, null);
				    
				    mPlatform.sendSession(recContent, RequestParams.PARAM_ROBOT_ID + "=r00001");
				}
			}
		});

		mBtnPractisce.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				new Thread(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						mHandler.sendEmptyMessage(ShowLoadint);
						CRFUtil.training(mNames[mDef]);
						mHandler.sendEmptyMessage(ShowUse);
					}
				}).start();

			}
		});

		mBtnAddleft.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				int index = mEdcontent.getSelectionStart();
				String st = mEdcontent.getText().toString();
				if (index < 0 || index >= st.length()) {

				} else {
					mEdcontent.getText().insert(index, "[");
				}
			}
		});

		mBtnAddRight.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				int index = mEdcontent.getSelectionStart();
				String st = mEdcontent.getText().toString();
				if (index < 0 || index >= st.length()) {
					mEdcontent.append(",arg" + numberArgs[mArgPosition] + "]");
				} else {
					mEdcontent.getText().insert(index,
							",arg" + numberArgs[mArgPosition] + "]");
				}
			}
		});

		mSpAddArgs.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				mArgPosition = position;
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub
				LogManager.e("onNothingSelected");
			}
		});

		mBtnMoveLeft.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				int index = mEdcontent.getSelectionStart();
				if (index > 0) {
					index--;
				}
				mEdcontent.setSelection(index);
			}
		});
		mBtnMoveLeft.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				// TODO Auto-generated method stub
				int index = mEdcontent.getSelectionStart();
				if (index > 0) {
					index = 0;
				}
				mEdcontent.setSelection(index);
				return false;
			}
		});

		mBtnMoveRight.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				int index = mEdcontent.getSelectionStart();
				if (index < mEdcontent.getText().toString().length()) {
					index++;
				}
				mEdcontent.setSelection(index);
			}
		});

		mBtnMoveRight.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				// TODO Auto-generated method stub
				int index = mEdcontent.getSelectionStart();
				if (index < mEdcontent.getText().toString().length()) {
					index = mEdcontent.getText().toString().length();
				}
				mEdcontent.setSelection(index);
				return false;
			}
		});

		mBtnDelete.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				int index = mEdcontent.getSelectionStart();

				if (index > 0) {
					mEdcontent.getText().delete(index - 1, index);
				}
			}
		});
		mBtnDelete.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				// TODO Auto-generated method stub
				int index = mEdcontent.getSelectionStart();

				if (index > 0) {
					mEdcontent.getText().delete(0, index);
				}
				return false;
			}
		});

	}

	public int get(String commandName) {
		int i = 0;
		for (String name : mNames) {

			if (name.equals(commandName)) {
				return i;
			}
			i++;
		}

		return 0;
	}

	public void showAddDialog(final String st) {
		AlertDialog.Builder builder = new Builder(TrainActivity.this);
		builder.setMessage(st);
		builder.setTitle("确认添加吗");
		builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				CRFUtil.addTrainData(mNames[mDef], st);
				Toast.makeText(getApplicationContext(), "保存成功 ",
						Toast.LENGTH_LONG).show();
				dialog.dismiss();

			}
		});
		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				dialog.dismiss();
			}
		});
		builder.create().show();
	}
}
