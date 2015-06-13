/******************************************************************************************
* @file HandleWakeup.java
*
* @brief implement the recognize interface, and also contain the wake up logic process
*
* Code History:
*      [2015-04-01] xiaohua lu, initial version.
*
* Code Review:
*
*********************************************************************************************/

package com.iii360.external.wakeup;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import com.iii360.base.common.utl.BaseContext;
import com.iii360.base.common.utl.KeyList;
import com.iii360.base.common.utl.LogManager;
import com.iii360.base.inf.recognise.ILightController;
import com.iii360.base.inf.recognise.IRecogniseSystem;
import com.iii360.external.wakeup.WakeupService.MyBinder;



/**
 * @brief 实现语音识别接口, 同时又包含了唤醒灯控制的应用逻辑
 * 
 */
public class HandleWakeup implements IRecogniseSystem 
{
	/////////////////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////// Member Variables /////////////////////////////////////
	/////////////////////////////////////////////////////////////////////////////////////////////	
	public static final String Tag = "[WAKEUP]";	
	private Context context = null;
	private static ILightController mDlg = null;
	private static BaseContext mBaseContext = null;


	private static ServiceConnection mServiceConnection = new ServiceConnection() {	// 服务连接器

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) 
		{
			WakeupService.MyBinder binder = (MyBinder) service;
			binder.bindRecogniseButton(mDlg);
		}

		@Override
		public void onServiceDisconnected(ComponentName name) 
		{
		}
	};
	
	
	/////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////// Public or Override Functions /////////////////////////////////
	/////////////////////////////////////////////////////////////////////////////////////////////	
	
	public HandleWakeup(Context context, ILightController dlg) 
	{
		this.context = context;
		mBaseContext = new BaseContext(context);
		this.mDlg = dlg;
	}
	

	@Override
	public void startCaptureVoice() 
	{
		startWakeup(context);
	}

	@Override
	public void stopCaptureVoice() 
	{
		stopWakeup(context);
	}

	@Override
	public void cancelRecognising() 
	{
		stopWakeup(context);
	}

	@Override
	public void destroy() 
	{
		stopWakeup(context);
	}

	@Override
	public void setOnResultListener(IOnResultListener onResultListener) 
	{
	}

	@Override
	public void dispatchUserAction() 
	{
	}

	@Override
	public void bindRecogniseButton(ILightController recogniseButton) 
	{
		mDlg = recogniseButton;
	}

	@Override
	public void startWakeup() 
	{
	}

	@Override
	public void stopWakeup() 
	{
	}
	
	
	
	
	
	
	
	/////////////////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////// Static Functions /////////////////////////////////////
	/////////////////////////////////////////////////////////////////////////////////////////////		


	public static void setWakeupNeedBaseDir(String string) 
	{
	}

	public static void setFileName(String netFile, String binFile) 
	{
	}

	/**
	 * 关闭WakeupService.停止服务后台运行。
	 * 
	 * @param context
	 */
	public static void stopWakeupService(Context context) 
	{
		Intent intent = new Intent(context, WakeupService.class);
		context.stopService(intent);
	}

	/**
	 * 开启唤醒。 开启麦克风监听。
	 */
	public static void startWakeup(Context context) 
	{
		LogManager.i(Tag, "==>HandleWakeup::startWakeup(): Enter ");								
		boolean isVoiceSound = mBaseContext.getPrefBoolean(KeyList.PKEY_IS_VOICE_SOUND_WAVE, false);

		// 声呐配置,停止唤醒
		if (isVoiceSound) 
		{
			LogManager.e(Tag, "   HandleWakeup::startWakeup(): [ERROR]  voice sound start can not wakeup");			
			return;
		}

		BaseContext baseContext = new BaseContext(context);
		ILightController wakeUpLightControl = (ILightController) baseContext.getGlobalObject(KeyList.GKEY_WAKEUP_LIGHT_CONTROL);
		if (baseContext.getGlobalBoolean(KeyList.GKEY_IS_MUSIC_IN_PLAYING)) 
		{				
			if (KeyList.IS_WAKEUP_IN_PLAYING_DEBUG)   // 调试模式
			{
				LogManager.i(Tag, "   HandleWakeup::startWakeup(): music is playing and debug mode");					
				Intent intent = new Intent(context, WakeupService.class);
				intent.putExtra(KeyList.EKEY_WAKE_UP_SERVECE_ID, WakeupService.SERVICE_ID_START_BACK_WAKE_UP_LISTEN);
				context.bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
				context.startService(intent);
				// 开启唤醒灯
				wakeUpLightControl.updateStateOnRunnable(ILightController.RECOGNISE_STATE_OPEN, 0);
			} 
			else 
			{
				// 关闭唤醒灯
				LogManager.i(Tag, "   HandleWakeup::startWakeup(): music is playing...");								
				wakeUpLightControl.updateStateOnRunnable(ILightController.RECOGNISE_STATE_CLOSE, 0);
			}

		} 
		else if (!baseContext.getGlobalBoolean(KeyList.GKEY_BOOL_IS_CONNECT_WIFIGATE))  // 无线网络断开
		{
			// 关闭唤醒灯
			LogManager.i(Tag, "   HandleWakeup::startWakeup(): WIFI is disconnected");				
			wakeUpLightControl.updateStateOnRunnable(ILightController.RECOGNISE_STATE_CLOSE, 0);
		} 
		else if (baseContext.getGlobalBoolean(KeyList.GKEY_DEVICE_CASE))  // 测试模式
		{
			// 关闭唤醒灯
			LogManager.i(Tag, "   HandleWakeup::startWakeup(): device case testing");				
			wakeUpLightControl.updateStateOnRunnable(ILightController.RECOGNISE_STATE_CLOSE, 0);
		}
		else if (mBaseContext.getGlobalBoolean(KeyList.GKEY_IS_WAKEUP_TO_RECOGNISE, false))  // 唤醒正在到识别模式
		{
			// 唤醒灯保持不变
			LogManager.i(Tag, "   HandleWakeup::startWakeup(): wakeup change to recognise can not wakeup");				
		} 
		else if (mBaseContext.getGlobalBoolean(KeyList.GKEY_FORCE_WAKEUP_UNSTARTABLE, false)) // 强制不能唤醒
		{
			// 唤醒灯保持不变
			LogManager.i(Tag, "   HandleWakeup::startWakeup(): force wakeup unstartable");				
		} 
		else if (baseContext.getGlobalBoolean(KeyList.GKEY_IS_NOW_RECOGNING, false))  // 正在识别处理中
		{
			// 关闭唤醒灯
			LogManager.i(Tag, "   HandleWakeup::startWakeup(): now is recognizing...");				
			wakeUpLightControl.updateStateOnRunnable(ILightController.RECOGNISE_STATE_CLOSE, 0);
		}
		else if (baseContext.getGlobalBoolean(KeyList.GKEY_IS_NOW_BUFF_RECOGNING, false))  // 正在buffer识别中
		{
			// 关闭唤醒灯
			LogManager.i(Tag, "   HandleWakeup::startWakeup(): now is buffer recognizing...");				
			wakeUpLightControl.updateStateOnRunnable(ILightController.RECOGNISE_STATE_CLOSE, 0);
		}
		else if (baseContext.getGlobalBoolean(KeyList.GKEY_IS_NOW_WAKEUP, false))   // 正在唤醒中
		{
			// 唤醒灯保持不变
			LogManager.i(Tag, "   HandleWakeup::startWakeup(): Now is waking up...");		
		}
		else 
		{
			LogManager.i(Tag, "   HandleWakeup::startWakeup(): test buffer startWakeup");				
			Intent intent = new Intent(context, WakeupService.class);
			intent.putExtra(KeyList.EKEY_WAKE_UP_SERVECE_ID, WakeupService.SERVICE_ID_START_BACK_WAKE_UP_LISTEN);
			context.bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
			context.startService(intent);
			
			// 开启唤醒灯
//			wakeUpLightControl.updateStateOnRunnable(ILightController.RECOGNISE_STATE_OPEN, 0);
		}
		
		LogManager.i(Tag, "   HandleWakeup::startWakeup(): Exit");					
	}

	/**
	 * 
	 * 关闭唤醒，但是服务还在后台运行 关闭麦克风监听。
	 * 
	 */
	public static void stopWakeup(Context context) 
	{
		stopWakeup(context, true);
	}

	private static void stopWakeup(Context context, boolean light) 
	{
		LogManager.i(Tag, "==>HandleWakeup::stopWakeup(): Enter, light = " + light);	
		
		if (mBaseContext.getGlobalBoolean(KeyList.GKEY_FORCE_WAKEUP_UNSTOPPABLE, false)) 
		{
			// 强制不可停止唤醒
			LogManager.i(Tag, "   HandleWakeup::stopWakeup(): GKEY_FORCE_WAKEUP_UNSTOPPABLE, force cannot stop wakeup");
		}
		else 
		{
			if (KeyList.IS_WAKEUP_IN_PLAYING_DEBUG) 
			{
				LogManager.i(Tag, "   HandleWakeup::stopWakeup(): now is playing debug");			
				mBaseContext.setPrefBoolean(KeyList.PKEY_IS_WAKE_UP_ACTIVE, false);
				mBaseContext.setGlobalBoolean(KeyList.GKEY_IS_NOW_WAKEUP, false);
			} 
			else 
			{
				Intent intent = new Intent(context, WakeupService.class);
				intent.putExtra(KeyList.EKEY_WAKE_UP_SERVECE_ID, WakeupService.SERVICE_ID_STOP_BACK_WAKE_UP_LISTEN);
				context.startService(intent);
				if (light) 
				{
					ILightController wakeUpLightControl = (ILightController) mBaseContext.getGlobalObject(KeyList.GKEY_WAKEUP_LIGHT_CONTROL);
					
					// 关闭唤醒灯
					wakeUpLightControl.updateStateOnRunnable(ILightController.RECOGNISE_STATE_CLOSE, 0);
				}
			}
		}
		
		LogManager.i(Tag, "   HandleWakeup::stopWakeup(): Exit");			
	}

	/**
	 * 重启唤醒，为了优化内存
	 * 
	 * @param context
	 */
	public static void restartWakeup(Context context) 
	{
		LogManager.i(Tag, "==>HandleWakeup::restartWakeup(): Enter");
		stopWakeup(context, false);
		startWakeup(context);
		LogManager.i(Tag, "   HandleWakeup::restartWakeup(): Exit");		
	}

	/**
	 * 程序开启的时候需要进行一次判断。check。
	 * 
	 * @param context
	 *            Context
	 * @return 返回是否启动标志
	 */
	public static boolean check(Context context) 
	{
		// if (WakeupUtil.isNeedOpenWakeup(context)) {
		startWakeup(context);
		return true;
		// } else {
		// stopWakeupService(context);
		// return false;
		// }
	}


	/**
	 * 
	 * 是否打开了Wifi并且屏幕关闭下的Wakeup
	 * 
	 * @param context
	 * @return true.打开 “wifi开启和屏幕关闭下开启唤醒”设置 false.未打开“wifi开启和屏幕关闭下开启唤醒”设置
	 * 
	 */
	public static boolean isNeedWifiAndScreenOff(Context context) 
	{
		return true;
		// return WakeupUtil.isNeedOpenWakeup(context);
	}


}
