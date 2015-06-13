package com.base.upgrade;

import java.util.HashSet;
import java.util.Set;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.iii360.base.common.utl.LogManager;
import com.iii360.base.inf.BasicServiceUnion;

public abstract class UpgradeSupport extends BroadcastReceiver {
	private static boolean needUpgrade = false;
	private static Set<String> taskList = new HashSet();

	public static boolean isNeedUpgrade() {
		return needUpgrade;
	}

	/**
	 * 注册任务
	 * 
	 * @param task
	 */
	public static void registerTask(Context context, String task) {
		taskList.add(task);
		LogManager.d("taskList +" + task);
	}

	/**
	 * 移除任务
	 * 
	 * @param task
	 */
	public static void removeTask(Context context, String task) {
		if(taskList.contains(task)){
			taskList.remove(task);
		}
		LogManager.d("taskList -" + task);
		disptach(context);

	}

	public abstract BasicServiceUnion getBasicServiceUnion(Context context);

	private static String serverBroadcast = "com.voice.upgrade.AGREE_UPGRADE";

	public static void agreeUpgrade(Context context) {
		Intent intent = new Intent();
		intent.setAction(serverBroadcast);
		String clientBroadcast = context.getPackageName();
		String endsWith = ".main";
		if (clientBroadcast.endsWith(endsWith)) {
			clientBroadcast = clientBroadcast.substring(0,
					clientBroadcast.length() - endsWith.length());
		}
		clientBroadcast += ".upgrade.READY_UPGRADE";
		intent.putExtra("clientBroadcast", clientBroadcast);
		context.sendBroadcast(intent);
		needUpgrade = false;
		LogManager.d("AGREE_UPGRADE Broadcast >> key=" + intent.getAction()
				+ "[" + clientBroadcast + "]");
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		LogManager.d("READY_UPGRADE onReceive << key=" + intent.getAction());
		if(mUpdateReceiverListener !=null){
			mUpdateReceiverListener.receiver();
		}
		this.disptach(context);
		this.abortBroadcast();// 终止广播
	}

	/**
	 * 调度任务
	 */
	public static void disptach(Context context) {
		LogManager.d("disptach size:" + taskList.size());
		if (taskList.size() > 0) {
			// 等待空先后通知
			needUpgrade = true;
		} else {
			// 当前空闲立即同意
			agreeUpgrade(context);
			needUpgrade = false;
		}
	}
	
	private static UpdateReceiverListener mUpdateReceiverListener;
	
	public interface UpdateReceiverListener{
		void  receiver();
	}
	
	public static void setOnUpdateReceiverListener(UpdateReceiverListener listener){
		mUpdateReceiverListener=listener;
	}
}
