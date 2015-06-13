 
package com.smallzhi.push.manager;


import com.smallzhi.push.entity.Message;

import android.net.NetworkInfo;


/**
 *CIM 主要事件接口
 * 类名称：OnCIMMessageListener
 * 类描述：
 * 修改时间： 2014-4-28 下午5:07:47
 * 修改备注：
 * @version 1.0.0
 *
 */
public interface MessageListener
{
    /**
     * 当收到服务端推送过来的消息时调用
     * @param message
     */
    public void onMessageReceived(Message message);

    /**
     * 当调用CIMPushManager.sendRequest()向服务端发送请求，获得相应时调用
     * @param replybody
     */
    public void onReplyReceived(Message message);

    /**
     * 当手机网络发生变化时调用
     * @param networkinfo
     */
    public void onNetworkChanged(NetworkInfo networkinfo);
    
    /**
     * 获取到是否连接到服务端
     * 通过调用CIMPushManager.detectIsConnected()来异步获取
     * 
     */
    public void onConnectionStatus(boolean  isConnected);
    
    /**
     * 连接服务端成功
     */
	public  void onConnectionSucceed();
	/**
	 * 发送信息成功
	 * @author tart
	 * @date 2014-11-24 下午3:01:45
	 * @param 
	 * @return void
	 * @throws
	 */
	public  void onSentSucceed(Message msg);
	/**
     * 连接断开
     */
	public  void onConnectionClosed();
}

