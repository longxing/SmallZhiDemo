package com.voice.assistant.upgrade;

import android.content.Context;

import com.iii360.base.inf.BasicServiceUnion;
import com.voice.assistant.main.MyApplication;


public class UpgradeSupport extends com.base.upgrade.UpgradeSupport {

	@Override
	public BasicServiceUnion getBasicServiceUnion(Context context) {
		return ((MyApplication)context).getUnion();
	}

}
