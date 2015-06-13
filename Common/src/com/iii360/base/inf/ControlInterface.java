package com.iii360.base.inf;

public interface ControlInterface {

	public interface IgetMsg {
		public void onGetMsg(String msg);
	}

	public void init();

	public String sendMsg(String msg, String target);

	public void setOnGetMsg(IgetMsg onGetMeg);

	public String getName();

	public void destory();
}
