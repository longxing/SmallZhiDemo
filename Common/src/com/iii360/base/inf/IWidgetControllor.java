package com.iii360.base.inf;

import java.util.Map;

import android.content.Context;
/**
 * 控制Widget的显示。
 * @author Jerome.Hu.
 *
 */
public interface IWidgetControllor extends IWidgetBuilder{
    public interface WidgetMessageReceiver {
        public void receivedWidgetMessage(String widgetName);
    }
    
    public void dispatchWidget(Context context, String action, Map<String, Object> data, String commandName);
    
    public void dispatchWidgetMessage(String widgetName);
    
    public void registerWidgetMessageObserver(WidgetMessageReceiver voiceWidget);

    public void unRegisterWidgetMessageObserver(WidgetMessageReceiver voiceWidget);
    
    public void init();
    
    public void destroy();
    
    public void setIViewContainer(IViewContainer container);
}
