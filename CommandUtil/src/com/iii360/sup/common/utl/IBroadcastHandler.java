package com.iii360.sup.common.utl;

import android.content.Context;
/**
 * <pre>
 * 动态注册的BroadcastReceiver自己有自我注册能力可以实现该接口。
 * 建议继承：AbstractReceiver.{@link com.iii360.sup.common.utl.AbstractReceiver}
 * </pre>
 * @author Jerome.Hu
 *
 */
public interface IBroadcastHandler {
	/**
	 * 注册方法。
	 * @param context Context
	 */
	public void register(Context context);
	/**
	 * 解注册
	 * @param context Context
	 */
	public void unRegister(Context context);
}
