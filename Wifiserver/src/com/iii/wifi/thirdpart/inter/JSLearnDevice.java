package com.iii.wifi.thirdpart.inter;

import java.io.UnsupportedEncodingException;
import java.util.List;

import android.content.Context;
import android.text.TextUtils;

import com.iii.wifi.dao.imf.WifiControlDao;
import com.iii.wifi.dao.imf.WifiDeviceDao;
import com.iii.wifi.dao.info.WifiControlInfo;
import com.iii.wifi.dao.info.WifiDeviceInfo;
import com.iii.wifi.thirdpart.broadlink.BroadlinkDevice;
import com.iii.wifi.thirdpart.orvibo.OrviboDao;
import com.iii.wifi.thirdpart.orvibo.OrviboDevice;
import com.iii.wifi.thirdpart.yaokongbao.YaoKongBao;
import com.iii.wifi.thirdpart.yaokongbao.YaoKongBaoDevice;
import com.iii.wifi.util.HardwareUtils;
import com.iii.wifi.util.WaitUtil;
import com.iii360.sup.common.utl.LogManager;
import com.orvibo.lib.wiwo.bo.Device;

/**
 * 学习指令
 * 
 * @author river
 * 
 */
public class JSLearnDevice {

	private static final String TAG = "JSLearnDevice";

	private Context context;
	private String hfResult;
	private WifiDeviceDao mWifiDeviceDao;
	private WifiControlDao mWifiControlDao;
	private boolean mStudying = false;
	private OrviboDao mOrviboDao;
	private YaoKongBaoDevice ykbDevice = null;

	public JSLearnDevice(Context context) {
		// TODO Auto-generated constructor stub
		this.context = context;
		this.mWifiDeviceDao = new WifiDeviceDao(context);
		this.mWifiControlDao = new WifiControlDao(context);
		this.mOrviboDao = new OrviboDao(context);
		ykbDevice = YaoKongBaoDevice.getInstance();
	}

	public String startLearnHF(String deviceId) {
		List<WifiDeviceInfo> infos = mWifiDeviceDao.selectByDeviceId(deviceId);
		if (infos.size() > 0) {
			WifiDeviceInfo info = infos.get(0);
			String macAdd = info.getMacadd();
			String deviceModel = info.getDeviceModel();
			long startTime = System.currentTimeMillis();

			// ============Broadlink==================
			if (HardwareUtils.DEVICE_MODEL_BL_RM2.equals(deviceModel)) {
				BroadlinkDevice mBroadlinkDevice = new BroadlinkDevice(context);

				if (!mBroadlinkDevice.sendLearnHF(macAdd))
					return null;

				WaitUtil.sleep(1000);
				do {
					hfResult = mBroadlinkDevice.getHF(macAdd);
					try {
						Thread.sleep(800);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} while (TextUtils.isEmpty(hfResult) && (System.currentTimeMillis() - startTime) < 5000);

				LogManager.d(TAG, "broadlink returend  " + hfResult);
				return hfResult;

				// ============Orvibo==================
			} else if (HardwareUtils.DEVICE_MODEL_OB_ALLONE.equals(deviceModel)) {
				mStudying = false;
				startTime = System.currentTimeMillis();
				OrviboDevice orviboDevice = OrviboDevice.getInstance(context);
				List<Device> list = mOrviboDao.selectByUid(macAdd);
				int deviceIndex = -1;
				if (list != null && !list.isEmpty()) {
					deviceIndex = list.get(0).getDeviceIndex();
				}

				if (deviceIndex == -1) {
					LogManager.e("orvibo device not online");
					return null;
				}

				List<WifiControlInfo> controls = mWifiControlDao.selectByDeviceId(info.getDeviceid());

				LogManager.d(TAG, "orvibo study uid=" + macAdd + "||deviceIndex=" + deviceIndex);
				// uid-->mac || model--->fitting
				orviboDevice.learnHF(macAdd, deviceIndex, new HFResultListener() {
					@Override
					public void onResult(boolean status, String command) {
						// TODO Auto-generated method stub
						if (status) {
							hfResult = command;
						}
						mStudying = true;
					}
				});

				while (!mStudying && (System.currentTimeMillis() - startTime) < 15000) {
					WaitUtil.sleep(1000);
				}

				return hfResult;
				/************************* 遥控宝 **************************/
			} else if (HardwareUtils.DEVICE_MODEL_YKB_3S.equals(deviceModel)) {
				if (ykbDevice.ykbHandleStatue() != 0 && macAdd != null) {
					try {
						ykbDevice.startLearnCommand(macAdd.getBytes("utf-8"), YaoKongBao.YKB_TypeInfrared);
					} catch (UnsupportedEncodingException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					do {
						hfResult = ykbDevice.getLearnResult();
						try {
							Thread.sleep(500);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					} while (TextUtils.isEmpty(hfResult) && (System.currentTimeMillis() - startTime) < 15 * 1000);
					LogManager.d(TAG, "yaokongbao returend  " + hfResult);
					return hfResult;
				}
			}
		}
		return null;
	}

}
