package com.base.platform;

import com.base.data.CommandInfo;

/**
 * @author rtygbwwwerr
 * @description:数据接收监听
 */
public interface OnDataReceivedListener {
    
    /**
     * @param cmdInfo 从语义解析器返回的命令信息
     * @description:将语义解析结果返回给实现对象
     */
    public void onDataReceived(CommandInfo cmdInfo);
    
    /**
     * @param errorCode 从语义解析器返回的错误代码
     */
    public void onError(int errorCode);
}
