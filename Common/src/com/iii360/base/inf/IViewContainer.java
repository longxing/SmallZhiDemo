package com.iii360.base.inf;

import android.content.Context;
import android.content.Intent;



/**
 * 主容器类
 * @author Administrator
 * 
 */
public interface IViewContainer  {
    public void pushNewWidget(IVoiceWidget voiceWidget);

    public void destory();

    public void removeWidget(IVoiceWidget voiceWidget);

    public boolean isEmpty();
    /**
     * 父Activity在onResume的时候调用
     */
    public void onShowAll();

    /**
     * Activity onActivityResult时候调用。
     * @param requestCode
     * @param resultCode
     * @param result
     */
    public void onActivityResult(int requestCode, int resultCode, Intent result);
    
    public IWidgetControllor getWidgetController();

    public Context getContext();
    
    public BasicServiceUnion getUnion();
    
}
