package com.smallzhi.homeappliances.control;



public interface IControlInterface {

    public interface IgetMsg {
        public void onGetMsg(String msg);
    }

    public void init();

    public String sendMsg(String msg);

    public void setOnGetMsg(IgetMsg onGetMeg);

    public String getName();

    public void destory();
}
