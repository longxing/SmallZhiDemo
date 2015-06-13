package com.parser.iengine;

import com.base.platform.OnDataReceivedListener;


public interface IEngine {
    
	public void setAdditionalParams(String params);
	
    /**
     * @param text
     * @param params
     *    p1=v1&p2=v2...
     *    support:appId,robotId,session_type
     *    
     */
    public void input(String text, String params);
    
    /**
     * @param l
     */
    public void setOnDataReceivedListener(OnDataReceivedListener l);
    
    /**
     * @return
     */
    public OnDataReceivedListener getOnDataReceivedListener();
}
