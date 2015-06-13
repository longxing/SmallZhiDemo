package com.voice.common.util;

import com.iii360.base.inf.BasicServiceUnion;
import com.iii360.base.inf.IMainThreadUtil;
import com.iii360.base.inf.IVoiceWidget;
import com.iii360.sup.common.utl.LogManager;

public class MainThreadUtil implements IMainThreadUtil {

	BasicServiceUnion mUnion;
	public static final String ROLE = "role";
	public static final String MSG = "msg";

	@Override
	public void setCurrentUnion(BasicServiceUnion union) {
		// TODO Auto-generated method stub
		if (union == null) {
			LogManager.e("union == null");
		}
		mUnion = union;
	}

	@Override
	public void sendNormalWidget(final String content) {
		// TODO Auto-generated method stub
		LogManager.e(content);
		mUnion.getTTSController().play(content);
	}

	@Override
	public void sendQuestionWidget(final String content) {
		// TODO Auto-generated method stub
	}

	@Override
	public void pushNewWidget(final IVoiceWidget wiget) {
		// TODO Auto-generated method stub
		mUnion.getHandler().post(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub

				if (mUnion.getViewContainer() != null) {
					mUnion.getViewContainer().pushNewWidget(wiget);
				}
			}
		});
	}

}
