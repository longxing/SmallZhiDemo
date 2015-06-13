package com.smallzhi.TTS.Engine;

/**
 * TTS状态监听
 * @author Peter
 * @data 2015年4月15日上午11:45:26
 */
public interface ITTSStatusListen {
    
    
    public void onBegin();

    public void onEnd();

    public void onError();
}
