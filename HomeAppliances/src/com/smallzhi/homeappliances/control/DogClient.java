package com.smallzhi.homeappliances.control;

import android.content.Context;

import com.iii360.sup.common.utl.LogManager;
import com.smallzhi.homeappliances.util.CoreUtil.OnGetDevAdd;
import com.voice.common.util.DogControlUtil;

public class DogClient implements IControlInterface {

	public static final String TYPE = "DOG_CLIENT";
	public DogControlUtil dogControlUtil;

	private OnGetDevAdd mOnGetDevAdd;

	public DogClient(Context context, final OnGetDevAdd onGetDevAdd) {
		dogControlUtil = DogControlUtil.getInstance(context);

		dogControlUtil.setOnConnect(new OnGetDevAdd() {

			@Override
			public void onGetAdd(IControlInterface control) {
				// TODO Auto-generated method stub
				if (dogControlUtil != null){
					dogControlUtil.buildDogCommand();
				}
				onGetDevAdd.onGetAdd(DogClient.this);
			}

			@Override
			public void onDisConnect() {
				// TODO Auto-generated method stub
				onGetDevAdd.onGetAdd(null);
			}
		});
	}

	@Override
	public void init() {

	}

	@Override
	public String sendMsg(String msg) {
		LogManager.e(msg);
		if (!dogControlUtil.sendCommand(msg)) {
			return "命令发送错误，请重试";
		}
		return null;
	}

	@Override
	public void setOnGetMsg(IgetMsg onGetMeg) {
		// TODO Auto-generated method stub
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "小狗";
	}

	@Override
	public void destory() {
		// TODO Auto-generated method stub

	}

}
