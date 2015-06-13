package com.iii.wifi.thirdpart.yaokongbao;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import android.util.Log;

import com.iii360.sup.common.utl.LogManager;

/**
 * 遥控宝设备相关操作
 * 
 * 包括：设备初始化，设备注销、设备的搜索，设备状态改变通知、设备指令学习（红外学习、315M射频学习，433射频学习）
 * 
 * 获取设备列表
 * 
 * @author Peter
 * @data 2015年5月25日下午3:49:46
 */
public class YaoKongBaoDevice {

	/**********************************************************************/
	/**************************** Member Variables ****************************/
	/*********************************************************************/
	private static final String TAG = "YaoKongBaoDevice";
	private static YaoKongBaoDevice YkbDevicesInstance = null; // application instance
	private Set<YaoKongBaoDeviceInfo> m_deviceSet = new HashSet<YaoKongBaoDeviceInfo>(); // device set
	private long m_ykbHandleStatue = 0; // 遥控宝的初始化状态 0 表示未初始化

	private static int currentLearnMeoth = 0;

	private static String learnResult = null; // 学习结果

	private Object objLock = new Object(); // 设备列表锁

	private boolean commandSendState = false; // 命令发送状态

	/**
	 * get single instance
	 * 
	 * @return
	 */
	public static YaoKongBaoDevice getInstance() {
		LogManager.d(TAG, "YaoKongBaoDevice getInstance when instance:" + YkbDevicesInstance);
		if (YkbDevicesInstance == null) {
			YkbDevicesInstance = new YaoKongBaoDevice();
		}
		return YkbDevicesInstance;
	}

	/**
	 * 初始化遥控宝
	 */
	public void InitializeYKB() {
		LogManager.d(TAG, "YaoKongBaoDevice InitializeYKB  when m_ykbHandleStatue:" + m_ykbHandleStatue);
		if (m_ykbHandleStatue == 0) {
			m_ykbHandleStatue = YaoKongBao.YKB_Initialize();
		}
	}

	/**
	 * 注销遥控宝
	 */
	public void UnInitializeYKB() {
		if (0 != m_ykbHandleStatue) {
			YaoKongBao.YKB_UnInitialize(m_ykbHandleStatue);
		}
	}

	/**
	 * 设备发现回调接口，设备状态改变回到方法
	 * 
	 * @param deviceId
	 * @param isOnline
	 */
	public void recvFoundDevice(byte[] deviceId, boolean isOnline) {
		YaoKongBaoDeviceInfo device = new YaoKongBaoDeviceInfo();
		device.setmDeviceId(deviceId);
		device.setOnline(isOnline);
		synchronized (objLock) {
			if (!m_deviceSet.add(device)) {
				m_deviceSet.remove(device);
				m_deviceSet.add(device);
			}
		}
		Log.i(TAG, "====>> recvFoundDevice  current devices count:" + m_deviceSet.size() + "currentID:" + new String(deviceId) + "===>>isOnLine:" + isOnline);
	}

	/**
	 * 开始学习命令 （1）如果的设置过类型指定类型学习 （2）没有指定类型开启三种类型的学习
	 *
	 *
	 * @param deviceId
	 * @param type
	 */
	public void startLearnCommand(byte[] deviceId, int type) {
		Log.i(TAG, "====>> startLearnCommand  current devices id:" + new String(deviceId) + "==>>type:" + type + "===>>handleState:" + m_ykbHandleStatue);
		learnResult = null;
		if (type == YaoKongBao.YKB_TypeInfrared || type == YaoKongBao.YKB_TypeRadioFreq315M || type == YaoKongBao.YKB_TypeRadioFreq433M) {
			Log.i(TAG, "===>>startLearnCommand study start");
			boolean isStudyOk = YaoKongBao.YKB_StudyStart(m_ykbHandleStatue, deviceId, type);
			Log.i(TAG, "===>>startLearnCommand study result:" + isStudyOk);
		} else {// 同时开启三种学习方式
			YaoKongBao.YKB_StudyStart(m_ykbHandleStatue, deviceId, YaoKongBao.YKB_TypeInfrared);
			YaoKongBao.YKB_StudyStart(m_ykbHandleStatue, deviceId, YaoKongBao.YKB_TypeRadioFreq315M);
			YaoKongBao.YKB_StudyStart(m_ykbHandleStatue, deviceId, YaoKongBao.YKB_TypeRadioFreq433M);
		}

	}

	/**
	 * 发送控制命令
	 * 
	 * @param deviceId 设备ID
	 * @param isOpean 开关
	 * @return
	 */
	public boolean controlOnOff(final String deviceId, final boolean isOpean, final String commandData) {
		LogManager.d(TAG, "control yaokongbao device begin  state =" + isOpean + "==>>command:" + commandData);
		byte[] passWord = { 0, 0 };
		YaoKongBao.YKB_SendCommand(m_ykbHandleStatue, deviceId.getBytes(), YaoKongBao.YKB_TypeInfrared, passWord, hexStringToByte(commandData));
		LogManager.d(TAG, "control yaokongbao device end result =" + commandData);
		return commandSendState;
	}

	/**
	 * 红外学习回调方法
	 * 
	 * @param deviceId
	 * @param isSendOk
	 */

	public void recvSendInfraredDataAck(byte[] deviceId, boolean isSendOk) {
		Log.i(TAG, "====>> recvSendInfraredDataAck  current id:" + new String(deviceId));
		setCommandSendState(isSendOk);
	}

	/**
	 * 射频学习回调方法
	 * 
	 * @param deviceId
	 * @param isSendOk
	 */
	public void recvSendRadioFreqDataAck(byte[] deviceId, boolean isSendOk) {
		Log.i(TAG, "====>> recvSendRadioFreqDataAck  current id:" + new String(deviceId) + "==>isSendOk:" + isSendOk);
		setCommandSendState(isSendOk);
	}

	/**
	 * 学习成功回到方法
	 * 
	 * @param deviceId
	 * @param type
	 * @param studyData
	 */
	public void recvStudyOkAck(byte[] deviceId, int type, byte[] studyData) {
		learnResult = bytesToHexString(studyData);
		Log.i(TAG, "====>> recvStudyOkAck  current id:" + new String(deviceId) + "==>type:" + type + "==>>command:" + learnResult);
		// 记录当前设备学习成功的方式
		setCurrentLearnMeoth(type);

	}

	/**
	 * 学习失败回调方法
	 * 
	 * @param deviceId
	 * @param type
	 * @param error
	 */
	public void recvStudyFailureAck(byte[] deviceId, int type, int error) {
		Log.i(TAG, "====>> recvStudyFailureAck  current id:" + new String(deviceId) + "==>type:" + type + "==>>error" + error);
		learnResult = null;
	}

	/**
	 * 获取遥控宝设备列表
	 * 
	 * @return
	 */
	public Set<YaoKongBaoDeviceInfo> getDeviceSet() {
		if (m_deviceSet != null) {
			for (Iterator<YaoKongBaoDeviceInfo> iterator = m_deviceSet.iterator(); iterator.hasNext();) {
				YaoKongBaoDeviceInfo yaoKongBaoDeviceInfo = (YaoKongBaoDeviceInfo) iterator.next();
				if (!yaoKongBaoDeviceInfo.isOnline()) {
					synchronized (objLock) {
						m_deviceSet.remove(yaoKongBaoDeviceInfo);
					}
				}
			}
		}
		return m_deviceSet;
	}

	/**
	 * 获取遥控宝的初始化状态
	 * 
	 * @return
	 */
	public long ykbHandleStatue() {
		// synchronized (objLock) {
		return m_ykbHandleStatue;
		// }
	}

	/**
	 * 获取学习结果
	 * 
	 * @return
	 */
	public String getLearnResult() {
		return learnResult;
	}

	/**
	 * 设置当前学习方式 红外或者射频
	 * 
	 * @return
	 */
	public static synchronized int getCurrentLearnMeoth() {
		return currentLearnMeoth;
	}

	public static synchronized void setCurrentLearnMeoth(int currentLearnMeoth) {
		YaoKongBaoDevice.currentLearnMeoth = currentLearnMeoth;
	}

	/**
	 * 设置命令发送状态
	 * 
	 * @return
	 */

	public synchronized boolean isCommandSendState() {
		return commandSendState;
	}

	public synchronized void setCommandSendState(boolean commandSendState) {
		this.commandSendState = commandSendState;
	}

	/**
	 * 字节数组转换成十六进制字符串
	 * 
	 * @param b
	 * @return
	 */
	public static String bytes2HexString(byte[] b) {
		String ret = "";
		for (int i = 0; i < b.length; i++) {
			String hex = Integer.toHexString(b[i] & 0xFF);
			if (hex.length() == 1) {
				hex = '0' + hex;
			}
			ret += hex;
		}
		return ret.toUpperCase();
	}

	/**
	 * 十六进制字符串转换成字节数组
	 * 
	 * @param b
	 * @return
	 */

	public static final String bytesToHexString(byte[] bArray) {
		StringBuffer sb = new StringBuffer(bArray.length);
		String sTemp;
		for (int i = 0; i < bArray.length; i++) {
			sTemp = Integer.toHexString(0xFF & bArray[i]);
			if (sTemp.length() < 2)
				sb.append(0);
			sb.append(sTemp.toUpperCase());
		}
		return sb.toString();
	}

	public static byte[] hexStringToByte(String hex) {
		int len = (hex.length() / 2);
		byte[] result = new byte[len];
		char[] achar = hex.toCharArray();
		for (int i = 0; i < len; i++) {
			int pos = i * 2;
			result[i] = (byte) (toByte(achar[pos]) << 4 | toByte(achar[pos + 1]));
		}
		return result;
	}

	private static byte toByte(char c) {
		byte b = (byte) "0123456789ABCDEF".indexOf(c);
		return b;
	}

}
