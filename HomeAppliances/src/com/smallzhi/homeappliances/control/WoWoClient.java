package com.smallzhi.homeappliances.control;

import java.io.IOException;

import com.iii360.base.common.utl.LogManager;
import com.smallzhi.homeappliances.util.Client;

public class WoWoClient implements IControlInterface {

    private Client mClient;
    private IgetMsg mIgetMsg;

    public WoWoClient(Client c) {
        mClient = c;
    }

    @Override
    public void init() {
        // TODO Auto-generated method stub

    }

    @Override
    public String sendMsg(String msg) {
        String conTent = msg;
        LogManager.e(conTent);
        byte[] b = conTent.getBytes();
        try {
            mClient.getOutputStream().write(b);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            LogManager.printStackTrace(e);
            return "消息发送失败";
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
        return "窝窝家庭主机";
    }

    @Override
    public void destory() {
        // TODO Auto-generated method stub

    }

}
