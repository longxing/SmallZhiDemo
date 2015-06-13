/******************************************************************************************
 * @file SystemEvent.java
 *
 * @brief system event module to get the event ,such as keydown and other input event
 *
 * Code History:
 *      [2015-05-27] Hank, initial version
 *
 * Code Review:
 *
 *********************************************************************************************/

package com.smallzhi.systemrestore;

public class SystemEvent {
	//important this value is custom by device (smallzhi hardware version 1.0)
	public static final int KEY_RESET 			= 87;
	public static final int KEY_LOG				= 88;
	public static final int KEY_VOLUMEDOWN		= 114;
	public static final int KEY_VOLUMEUP			= 115;
	
	
	/**
	 * @brief  event monitor start
	 * @return false means failure
	 * @return true means success
	 */
	public native static boolean   MonitorStart();
	
	/**
	 * @brief  event monitor start
	 * @return false means failure
	 * @return true means success
	 */
	public native static boolean   MonitorStop();
	
	
	/**
	 * @brief system event report callback
	 * @param eventType is the value of system event
	 */
	
	public static void EventReportCallback(int eventType){
		
	}
	
	static{
		System.loadLibrary("SystemEvent");
	}
	
}
