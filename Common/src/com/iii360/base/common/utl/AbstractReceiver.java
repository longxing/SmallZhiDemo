package com.iii360.base.common.utl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

/**
 * BroadCastRecevier基类。建议需要手动注册的BroadcastReceiver实现它。可以简化相关代码。 实现及使用方法：
 * 
 * <pre>
 * 1.继承AbstractReceiver.调用addActionMapping(String action,Runnable runnable)来注册相关的action.
 * 2.实例化对象.
 * 3.调用register(Context context)方法注册。
 * 
 * </pre>
 * 
 * @author Jerome.Hu
 */
public abstract class AbstractReceiver extends com.iii360.sup.common.utl.AbstractReceiver {

}
