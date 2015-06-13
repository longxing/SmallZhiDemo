package com.iii360.voiceassistant.common;

import android.content.Context;

import com.base.platform.OnDataReceivedListener;
import com.base.platform.Platform;
import com.iii360.base.inf.BasicServiceUnion;
import com.iii360.base.inf.parse.ICommandEngine;
import com.iii360.voiceassistant.semanteme.common.Params;

public class CommandEngine implements ICommandEngine {

	OnDataReceivedListener mDataReceivedListener;
	private Context mContext;
	Platform mPlatform;

	public CommandEngine(Context context) {
	
		mContext = context;
		mPlatform = Platform.getPlatformInstance(mContext, null);
	}

	public BasicServiceUnion getUnion() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public void handleText(String text) {
		// TODO Auto-generated method stub
		handleText(text, true, false);
	}

	@Override
	public void handleText(String text, boolean isOnlyToServe, boolean isNeedShowWidget) {
		// TODO Auto-generated method stub
		final Params params = new Params(mContext);
		mPlatform.setAdditionalParams(params.getCommonParams());
		if (!isOnlyToServe) {
			mPlatform.sendSession(text);
		} else {
			mPlatform.sendRemoteSession(text);
		}
	}


	public void setOnDataReceivedListener(OnDataReceivedListener onDataReceivedListener) {
		mDataReceivedListener = onDataReceivedListener;
		if (mPlatform != null) {
			mPlatform.setOnDataReceivedListener(mDataReceivedListener);
		}
	}

}
