package com.iii360.sup.common.utl;

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
public abstract class AbstractReceiver extends BroadcastReceiver implements IBroadcastHandler {
	/**
	 * 
	 * @author Jerome.Hu. 类似于Broadcast中的onReceiver(Context,Intent)方法。接收回调。
	 * 
	 */
	public interface OnReceiverListener {
		/**
		 * 
		 * @param context
		 *            Context
		 * @param intent
		 *            Intent
		 */
		public void onReceiver(Context context, Intent intent);
	};

	private List<String> mActionList;
	private Map<String, OnReceiverListener> mMapping;

	/**
	 * 
	 */
	public AbstractReceiver() {
		mActionList = new ArrayList<String>();
		mMapping = new HashMap<String, OnReceiverListener>();
	}

	@Override
	public void register(Context context) {
		// TODO Auto-generated method stub
		final IntentFilter intentFilter = new IntentFilter();
		for (int i = 0; i < mActionList.size(); i++) {
			intentFilter.addAction(mActionList.get(i));
		}
		context.registerReceiver(this, intentFilter);
	}

	@Override
	public void unRegister(Context context) {
		// TODO Auto-generated method stub
		try {
			context.unregisterReceiver(this);
		} catch (Exception e) {
			// TODO: handle exception
			LogManager.printStackTrace(e);
		}

	}

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		String action = null;
		if (intent == null) {
			action = null;
		} else {
			action = intent.getAction();
		}
		final OnReceiverListener runnable = getRunnable(action);
		if (runnable != null) {
			runnable.onReceiver(context, intent);
		}
	}

	protected OnReceiverListener getRunnable(String action) {
		return mMapping.get(action);
	}

	protected void addActionMapping(String action, OnReceiverListener runnable) {
		LogManager.d("action is " + action);
		if (action == null || "".equals(action) || runnable == null) {
			return;
		}
		mActionList.add(action);
		mMapping.put(action, runnable);
	}

}
