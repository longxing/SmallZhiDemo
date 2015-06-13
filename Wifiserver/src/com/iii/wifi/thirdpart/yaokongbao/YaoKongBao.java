package com.iii.wifi.thirdpart.yaokongbao;

import android.util.Log;

public class YaoKongBao {

	private static final String TAG = "YaoKongBao";
	//
	// type
	//
	public static final int YKB_TypeInfrared = 1; // infrared study
	public static final int YKB_TypeRadioFreq315M = 2; // radio freq 315M study
	public static final int YKB_TypeRadioFreq433M = 3; // radio freq 433 study

	//
	// error
	//
	public static final int YKB_ErrorTimeout = 1; // time out
	public static final int YKB_ErrorStudyFail = 2; // study fail
	public static final int YKB_ErrorDeviceBusy = 3; // devcie busy

	/**
	 * @brief device initialize
	 * @return handle
	 * @retval 0-->failure
	 */
	public native static long YKB_Initialize();

	/**
	 * @brief device uninitialize
	 * @param handle:
	 */
	public native static boolean YKB_UnInitialize(long handle);

	/**
	 * @brief scan device
	 */
	public native static boolean YKB_ScanStart(long handle);

	/**
	 * @brief stop scan device
	 */
	public native static boolean YKB_ScanStop(long handle);

	/**
	 * @brief start study
	 * @param deviceId: md5
	 * @param studyType:
	 */
	public native static boolean YKB_StudyStart(long handle, byte[] deviceId, int type);

	/**
	 * @brief study cancel
	 * @param deviceId: md5
	 */
	public native static boolean YKB_StudyCancel(long handle, byte[] deviceId);

	/**
	 * @brief send command
	 * @param deviceId: device id
	 * @param type:
	 * @param passwd: default {0, 0}
	 * @param data: send data
	 */
	public native static boolean YKB_SendCommand(long handle, byte[] deviceId, int type, byte[] passwd, byte[] data);

	// ************************************************************************************************
	// callback
	// @note run on non main thread
	// need realization
	// ************************************************************************************************

	/**
	 * @brief device status change callback
	 * @param deviceId: found device id
	 * @param isOnline: online status
	 */
	public static void YKB_DeviceStatusChange(byte[] deviceId, boolean isOnline) {

		Log.i(TAG, "*** YKB_DeviceStatusChange *** " + isOnline);
		String str = "";
		for (int index = 0; index < deviceId.length; ++index) {
			str += Integer.toHexString(deviceId[index]);
			str += " ";
		}
		Log.i(TAG, "*** ==>id: " + str);
		YaoKongBaoDevice.getInstance().recvFoundDevice(deviceId, isOnline);
	}

	/**
	 * @brief send infrared data reply callback
	 * @param deviceId: device id
	 * @param isSendOk: send result
	 */
	public static void YKB_InfraredDataSendReply(byte[] deviceId, boolean isSendOk) {
		Log.i(TAG, "==>YKB_InfraredDataSendReply   currentID:" + new String(deviceId) + "==>>isSendOk" + isSendOk);
		YaoKongBaoDevice.getInstance().recvSendInfraredDataAck(deviceId, isSendOk);
	}

	/**
	 * @brief send radio freq data reply callback
	 * @param deviceId: device id
	 * @param isSendOk: send result
	 */
	public static void YKB_RadioFreqDataSendReply(byte[] deviceId, boolean isSendOk) {
		Log.i(TAG, "==>YKB_RadioFreqDataSendReply   currentID:" + new String(deviceId) + "==>>isSendOk" + isSendOk);
		YaoKongBaoDevice.getInstance().recvSendRadioFreqDataAck(deviceId, isSendOk);
	}

	/**
	 * @brief study ok callback
	 * @param deviceId:
	 * @param type:
	 * @param studyData:
	 */
	public static void YKB_StudyOk(byte[] deviceId, int type, byte[] studyData) {
		YaoKongBaoDevice.getInstance().recvStudyOkAck(deviceId, type, studyData);
	}

	/**
	 * @brief study failure callback
	 * @param deviceId:
	 * @param type:
	 * @param error:
	 */
	public static void YKB_StudyFailure(byte[] deviceId, int type, int error) {
		Log.i(TAG, "==>YKB_StudyFailure   currentID:" + new String(deviceId) + "==>>error" + error);
		YaoKongBaoDevice.getInstance().recvStudyFailureAck(deviceId, type, error);
	}

	/*
	 * this is used to load the 'YaoKongBao' library on application startup.
	 */
	static {
		System.loadLibrary("YaoKongBao");
	}

};