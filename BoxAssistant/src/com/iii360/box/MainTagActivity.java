package com.iii360.box;

import java.util.List;
import java.util.Map;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.iii.wifi.dao.info.WifiMyTag;
import com.iii.wifi.dao.manager.WifiCRUDForMyTag;
import com.iii.wifi.dao.manager.WifiCRUDForMyTag.ResultForMyTagListener;
import com.iii360.box.base.BaseActivity;
import com.iii360.box.data.MyTagData;
import com.iii360.box.util.DensityUtil;
import com.iii360.box.util.KeyList;
import com.iii360.box.util.LogManager;
import com.iii360.box.util.PhoneInfoUtils;
import com.iii360.box.util.WifiCRUDUtil;
import com.iii360.box.view.IView;
import com.iii360.box.view.MyProgressDialog;
import com.iii360.box.view.NewViewHead;

/**
 * 我的标签
 * 
 * @author hefeng
 * 
 */
public class MainTagActivity extends BaseActivity implements IView, OnClickListener {
	private static LinearLayout.LayoutParams mParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
			LinearLayout.LayoutParams.WRAP_CONTENT, 1);
	private LinearLayout mMyTagLayout;
	private TextView mMyTagQuestionTv;
	private Button mNextBtn;
	private LinearLayout mMyTagGroupLayout;
	private int mCurrent = 0;
	private Button mTagBtn;
	private Map<String, List<String>> mGroupData;
	private List<String> mChildData;
	private MyTagListener mMyTagListener;
	private List<String> mSaveMyTag;
	private WifiCRUDForMyTag mWifiCRUDForMyTag;

	public static int TYPE_SELECTOR_TAG = 0;
	public static int TYPE_GROUP_TAG = 1;

	private MyProgressDialog mProgressDialog;
	private String mMyTag;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_tag);
		this.initViews();
		this.initDatas();
	}

	@Override
	public void initViews() {
		// TODO Auto-generated method stub
		mMyTagLayout = (LinearLayout) findViewById(R.id.mytag_selector_layout);
		mMyTagQuestionTv = (TextView) findViewById(R.id.mytag_question_tv);
		mNextBtn = (Button) findViewById(R.id.mytag_next_btn);
		mMyTagGroupLayout = (LinearLayout) findViewById(R.id.mytag_item_layout);

		mNextBtn.setOnClickListener(this);
	}

	@Override
	public void initDatas() {
		// TODO Auto-generated method stub
		NewViewHead.showLeft(context, "我的标签");
		mProgressDialog = new MyProgressDialog(context);
		mProgressDialog.setMessage(getString(R.string.ba_update_date));
		mProgressDialog.show();

		mSaveMyTag = getPrefStringList(KeyList.PKEY_SAVE_MYTAG_LIST);
		mMyTagListener = new MyTagListener();
		mWifiCRUDForMyTag = new WifiCRUDForMyTag(getBoxIp(), getBoxTcpPort());
		mWifiCRUDForMyTag.getMyTag(PhoneInfoUtils.getIMEI(context), new ResultForMyTagListener() {

			@Override
			public void onResult(String type, String errorCode, WifiMyTag myTag) {
				// TODO Auto-generated method stub
				mHandler.sendEmptyMessage(HANDLER_DISMISS);

				if (WifiCRUDUtil.isSuccessAll(errorCode) && myTag != null) {
					LogManager.d("get tag success");
					mMyTag = myTag.getTag();
					if (!mMyTag.equalsIgnoreCase("null") && !TextUtils.isEmpty(mMyTag)) {
						String[] tags = mMyTag.split("_");
						mSaveMyTag.clear();
						for (int i = 0; i < tags.length; i++) {
							mSaveMyTag.add(tags[i]);
						}
						setPrefStringList(KeyList.PKEY_SAVE_MYTAG_LIST, mSaveMyTag);
					}
				} else {
					LogManager.d("get tag error");
				}

				mHandler.sendEmptyMessage(HANDLER_LOADING);
			}
		});
	}

	private final int HANDLER_DISMISS = 0;
	private final int HANDLER_LOADING = 1;
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			int what = msg.what;
			switch (what) {

			case HANDLER_DISMISS:
				if (mProgressDialog != null && !MainTagActivity.this.isFinishing()) {
					mProgressDialog.dismiss();
				}

				break;
			case HANDLER_LOADING:
				loadMyTag();
				loadGroupTag();
				break;

			}
		}

	};

	/**
	 * 创建一个新标签
	 * 
	 * @param text
	 */
	private void createNewTag(String text, int type) {
		mParams.setMargins(DensityUtil.dip2px(context, 10), 0, 0, 0);
		mTagBtn = new Button(context);
		mTagBtn.setBackgroundResource(R.drawable.mytag_selector);
		mTagBtn.setPadding(DensityUtil.dip2px(context, 10), DensityUtil.dip2px(context, 8), DensityUtil.dip2px(context, 10),
				DensityUtil.dip2px(context, 8));
		mTagBtn.setTextSize(18);
		mTagBtn.setText(text);
		mTagBtn.setTag(type);
		mTagBtn.setLayoutParams(mParams);
		mTagBtn.setOnClickListener(mMyTagListener);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (v == mNextBtn) {
			mCurrent++;
			if (mCurrent == MyTagData.sTagQuestion.length) {
				mCurrent = 0;
			}
			loadGroupTag();
		}
	}

	/**
	 * 加载一组标签
	 */
	private void loadGroupTag() {
		mGroupData = MyTagData.getMapList();
		mMyTagQuestionTv.setText(MyTagData.sTagQuestion[mCurrent]);
		mChildData = mGroupData.get(MyTagData.sTagQuestion[mCurrent]);
		mMyTagGroupLayout.removeAllViews();

		for (String tag : mSaveMyTag) {
			if (mChildData.contains(tag)) {
				mChildData.remove(tag);
			}
		}

		for (String tag : mChildData) {
			createNewTag(tag, TYPE_GROUP_TAG);
			mMyTagGroupLayout.addView(mTagBtn);
		}
	}

	/**
	 * 加载已经选择的标签
	 */
	private void loadMyTag() {
		mMyTagLayout.removeAllViews();
		for (String tag : mSaveMyTag) {
			createNewTag(tag, TYPE_SELECTOR_TAG);
			mMyTagLayout.addView(mTagBtn);
		}
	}

	class MyTagListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if (v instanceof Button) {
				Button b = (Button) v;
				int type = (Integer) b.getTag();
				String text = b.getText().toString();

				if (type == TYPE_SELECTOR_TAG) {
					mSaveMyTag.remove(text);
					loadGroupTag();
					mMyTagLayout.removeView(v);

				} else if (type == TYPE_GROUP_TAG) {
					mSaveMyTag.add(text);
					loadMyTag();
					mMyTagGroupLayout.removeView(v);
				}

				setPrefStringList(KeyList.PKEY_SAVE_MYTAG_LIST, mSaveMyTag);
				sendTagData();
			}
		}
	}

	private StringBuffer mBuffer;

	private void sendTagData() {
		if (null == mSaveMyTag) {
			return;
		}

		mBuffer = new StringBuffer();
		if (!mSaveMyTag.isEmpty()) {
			for (String tag : mSaveMyTag) {
				mBuffer.append(tag);
				mBuffer.append("_");
			}
			mBuffer.deleteCharAt(mBuffer.length() - 1);
		}

		LogManager.d("send tag data : " + mBuffer.toString());

		mWifiCRUDForMyTag.setMyTag(mBuffer.toString(), PhoneInfoUtils.getIMEI(context), new WifiCRUDForMyTag.ResultForMyTagListener() {
			@Override
			public void onResult(String type, String errorCode, WifiMyTag myTag) {
				// TODO Auto-generated method stub
				if (WifiCRUDUtil.isSuccessAll(errorCode)) {
					LogManager.i("send tag data success");
				} else {
					LogManager.i("send tag data error");
				}
			}
		});
	}

	protected void onDestroy() {
		if (mProgressDialog != null && !this.isFinishing())
			mProgressDialog.dismiss();
		mProgressDialog = null;
		super.onDestroy();
	};
}
