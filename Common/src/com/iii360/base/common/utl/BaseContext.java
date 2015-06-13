package com.iii360.base.common.utl;

import android.content.Context;

import com.iii360.base.umeng.UmengUtil;
import com.iii360.sup.common.utl.SuperBaseContext;

/**
 * 
 * 使用本类终端额方法有一个前提。 Application类要实现IGloableHeap;
 * 
 */
public class BaseContext extends SuperBaseContext{


	public BaseContext(Context context) {
		super(context);
	}

	/**
	 * 
	 * @param eventId Umeng事件ID。需要事先到Umeng注册该ID才能统计得到。
	 * @param content 事件内容。
	 */
	public void sendUmengEvent(String eventId, String content) {
		UmengUtil.onEvent(mContext, eventId, content);
	}
}
