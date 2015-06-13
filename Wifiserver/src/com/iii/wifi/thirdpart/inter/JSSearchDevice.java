package com.iii.wifi.thirdpart.inter;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import android.content.Context;

import com.iii.wifi.dao.imf.WifiDeviceDao;
import com.iii.wifi.dao.info.WifiDeviceInfo;
import com.iii.wifi.thirdpart.broadlink.BroadlinkDevice;
import com.iii.wifi.thirdpart.broadlink.DeviceInfo;
import com.iii.wifi.thirdpart.huanteng.HuanTengDevice;
import com.iii.wifi.thirdpart.huanteng.HuanTengInfo;
import com.iii.wifi.thirdpart.orvibo.OrviboDevice;
import com.iii.wifi.thirdpart.wowo.WoWoDevice;
import com.iii.wifi.thirdpart.wowo.WoWoInfo;
import com.iii.wifi.thirdpart.yaokongbao.YaoKongBao;
import com.iii.wifi.thirdpart.yaokongbao.YaoKongBaoDevice;
import com.iii.wifi.thirdpart.yaokongbao.YaoKongBaoDeviceInfo;
import com.iii.wifi.util.BasePreferences;
import com.iii.wifi.util.HardwareUtils;
import com.iii.wifi.util.KeyList;
import com.iii360.sup.common.utl.LogManager;
import com.orvibo.lib.wiwo.bo.Device;
import com.orvibo.lib.wiwo.constant.ProductType;

/**
 * 搜索硬件设备
 * 
 * @author river
 * 
 */
public class JSSearchDevice {

	private static final String TAG = "WifiServer  JSSearchDevice";
	private ArrayList<WifiDeviceInfo> deviceInfos = new ArrayList<WifiDeviceInfo>();
	private WifiDeviceDao mWifiDeviceDao;
	private Context context;
	private OrviboDevice mOrviboDevice = null;
	private BroadlinkDevice mBroadlinkDevice = null;
	private HuanTengDevice mHuanTengDevice = null;
	private WoWoDevice mWoWoDevice = null;
	private YaoKongBaoDevice ykbDevice = null;

	private BasePreferences mPreferences;

	private int lastBroadlinkNum = 0;
	private int currBroadlinkNum = 0;

	private int lastWoWoNum = 0;
	private int currWoWoNum = 0;

	private int lastOrviboNum = 0;
	private int currOrviboNum = 0;

	private int lastHtNum = 0;
	private int currHtNum = 0;

	private boolean isDebug = true;

	public static long lastSearchYKBDeviceTime = 0; // 上一次搜索遥控宝设备的时间


	public JSSearchDevice(Context context) {
		// TODO Auto-generated constructor stub
		this.context = context;
		mPreferences = new BasePreferences(context);

		mWifiDeviceDao = new WifiDeviceDao(context);

		if (HardwareUtils.SW_ORVIBO) {
			mOrviboDevice = OrviboDevice.getInstance(context);
		}

		if (HardwareUtils.SW_BROADLINK) {
			mBroadlinkDevice = new BroadlinkDevice(context);
		}

		if (HardwareUtils.SW_HUANTENG) {
			mHuanTengDevice = HuanTengDevice.getHuanTengDeviceInstance(context);
		}
		if (HardwareUtils.SW_WOWO) {
			mWoWoDevice = WoWoDevice.getInstance();
		}

		if (HardwareUtils.SW_YKB_SWITCH) {
			ykbDevice = YaoKongBaoDevice.getInstance();
			ykbDevice.InitializeYKB();
		}
	}

	public List<WifiDeviceInfo> getUnConfigedDeviceList() {
		deviceInfos.clear();
		// =========== huanteng ===========
		if (HardwareUtils.SW_HUANTENG) {
			List<HuanTengInfo> hts = mHuanTengDevice.getALLOnLineBulbs();
			currHtNum = hts.size();
			for (HuanTengInfo huanTengInfo : hts) {
				// 判读是否已经配置了
				List<WifiDeviceInfo> infos = mWifiDeviceDao.selectByMacAdd(huanTengInfo.getId());
				if (infos == null || infos.size() == 0) {
					WifiDeviceInfo info = new WifiDeviceInfo();
					info.setMacadd(huanTengInfo.getId());
					info.setFitting("幻腾-" + huanTengInfo.getName());
					info.setDeviceType(HardwareUtils.WIFI_SINGLE_DEVICE);
					info.setDeviceModel(HardwareUtils.DEVICE_MODEL_HT_BULBS);
					deviceInfos.add(info);
				}
			}

			lastHtNum = mPreferences.getPrefInteger(KeyList.IKEY_LAST_HUANTENG_NUMBER, 0);
			LogManager.d(TAG, "get devices  for huanteng: currHtNum = " + currHtNum + "====>lastHtNum: " + lastHtNum);
			if (currHtNum == 0) {
				mPreferences.setPrefInteger(KeyList.IKEY_LAST_HUANTENG_NUMBER, 0);
			} else if (currHtNum != lastHtNum) {
				JSTTSUtils.findDeviceTTS(context);
				mPreferences.setPrefInteger(KeyList.IKEY_LAST_HUANTENG_NUMBER, currHtNum);
			}
		}

		// =========== Broadlink ===========

		if (HardwareUtils.SW_BROADLINK) {
			List<DeviceInfo> blinkList = mBroadlinkDevice.probeList();
			int size = blinkList.size();
			currBroadlinkNum = 0;
			for (int i = 0; i < size; i++) {
				// 搜索数据库是否配置了
				List<WifiDeviceInfo> infos = mWifiDeviceDao.selectByMacAdd(blinkList.get(i).getMac().replaceAll(":", HardwareUtils.MAC_ADRESS_SEPERATER));

				if (infos == null || infos.size() == 0) {
					currBroadlinkNum++;

					WifiDeviceInfo info = new WifiDeviceInfo();
					String mac = blinkList.get(i).getMac();
					info.setIpAdd(blinkList.get(i).getKey());
					info.setMacadd(mac.replaceAll(":", HardwareUtils.MAC_ADRESS_SEPERATER));
					info.setFitting("博联-" + blinkList.get(i).getName());

					if ("RM2".equals(blinkList.get(i).getType())) {
						info.setDeviceType(1);
					} else {
						info.setDeviceType(0);
					}
					info.setDeviceModel("DEVICE_MODEL_BL_" + blinkList.get(i).getType());
					deviceInfos.add(info);
				}
			}

			lastBroadlinkNum = mPreferences.getPrefInteger(KeyList.IKEY_LAST_BROADLINK_NUMBER, 0);

			if (isDebug) {
				LogManager.i("now s : currBroadlinkNum = " + lastBroadlinkNum + " : " + lastBroadlinkNum);
			}

			if (currBroadlinkNum == 0) {
				mPreferences.setPrefInteger(KeyList.IKEY_LAST_BROADLINK_NUMBER, 0);
			} else if (currBroadlinkNum != lastBroadlinkNum) {
				JSTTSUtils.findDeviceTTS(context);
				mPreferences.setPrefInteger(KeyList.IKEY_LAST_BROADLINK_NUMBER, currBroadlinkNum);
			}
		}

		// =========== orvibo ===========
		if (HardwareUtils.SW_ORVIBO) {
			if (mOrviboDevice.isEnable()) {
				mOrviboDevice.search();
				List<Device> orvibo = mOrviboDevice.getDeviceList();
				print("orvibo", orvibo);

				for (Device d : orvibo) {
					List<WifiDeviceInfo> infos = mWifiDeviceDao.selectByMacAdd(d.getUid());
					currOrviboNum = 0;

					if (infos == null || infos.size() == 0) {
						currOrviboNum++;

						WifiDeviceInfo info = new WifiDeviceInfo();
						// uid-->mac || deviceIndex-->ip ||model--->fitting
						// info.setIpAdd(d.getDeviceIndex() + "");
						info.setMacadd(d.getUid());
						info.setFitting("欧瑞博-" + d.getModel());

						int deviceType = d.getDeviceType();
						if (deviceType == ProductType.WIFI_SOCKET) {
							info.setDeviceType(HardwareUtils.WIFI_SINGLE_DEVICE);
							info.setDeviceModel(HardwareUtils.DEVICE_MODEL_OB_S20);

						} else if (deviceType == ProductType.ALLONE) {
							info.setDeviceType(HardwareUtils.WIFI_UNSINGLE_DEVICE);
							info.setDeviceModel(HardwareUtils.DEVICE_MODEL_OB_ALLONE);

						}
						deviceInfos.add(info);
					}
				}

				lastOrviboNum = mPreferences.getPrefInteger(KeyList.IKEY_LAST_ORVIBO_NUMBER, 0);
				int s = currOrviboNum;
				if (isDebug) {
					LogManager.i("now s : lastOrviboNum = " + s + " : " + lastOrviboNum);
				}
				if (s == 0) {
					mPreferences.setPrefInteger(KeyList.IKEY_LAST_ORVIBO_NUMBER, 0);

				} else if (s != lastOrviboNum) {
					JSTTSUtils.findDeviceTTS(context);
					mPreferences.setPrefInteger(KeyList.IKEY_LAST_ORVIBO_NUMBER, s);
				}
			}
		}

		// =========== wowo ===========
		if (HardwareUtils.SW_WOWO) {
			mWoWoDevice.search();
			List<WoWoInfo> wwinfos = mWoWoDevice.getAllWoWoDevcie();

			for (WoWoInfo inf : wwinfos) {
				currWoWoNum = 0;
				List<WifiDeviceInfo> infos = mWifiDeviceDao.selectByMacAdd(inf.getMac());
				if (infos == null || infos.size() == 0) {
					currWoWoNum++;
					WifiDeviceInfo info = new WifiDeviceInfo();
					info.setMacadd(inf.getMac());
					info.setIpAdd(inf.getIp());
					info.setFitting("窝窝-" + inf.getBrand());
					info.setDeviceType(HardwareUtils.WIFI_SINGLE_DEVICE);
					info.setDeviceModel(HardwareUtils.DEVICE_MODEL_WW_TS);
					deviceInfos.add(info);
				}
			}
			mWoWoDevice.clear();
			lastWoWoNum = mPreferences.getPrefInteger(KeyList.IKEY_LAST_WOWO_NUMBER, 0);
			int s1 = currWoWoNum;
			if (isDebug) {
				LogManager.i("now s : lastWoWoNum = " + s1 + " : " + lastWoWoNum);
			}
			if (s1 == 0) {
				mPreferences.setPrefInteger(KeyList.IKEY_LAST_WOWO_NUMBER, 0);

			} else if (s1 != lastWoWoNum) {
				JSTTSUtils.findDeviceTTS(context);
				mPreferences.setPrefInteger(KeyList.IKEY_LAST_WOWO_NUMBER, s1);
			}
		}
		// =========== others ===========

		if (HardwareUtils.SW_YKB_SWITCH) {
			if (ykbDevice.ykbHandleStatue() != 0) {
				LogManager.d(TAG, "get devices  for yaokongbao  YKB_ScanStart: handleState = " + ykbDevice.ykbHandleStatue());
				if (lastSearchYKBDeviceTime == 0 || System.currentTimeMillis() - lastSearchYKBDeviceTime > 15 * 1000) {
					lastSearchYKBDeviceTime = System.currentTimeMillis();
					YaoKongBao.YKB_ScanStart(ykbDevice.ykbHandleStatue());
				}
				Set<YaoKongBaoDeviceInfo> ykbSet = ykbDevice.getDeviceSet();
				for (YaoKongBaoDeviceInfo ykbInfo : ykbSet) {
					// 判读是否已经配置了
					List<WifiDeviceInfo> infos = mWifiDeviceDao.selectByMacAdd(new String(ykbInfo.getmDeviceId()));
					if (infos == null || infos.size() == 0) {
						WifiDeviceInfo info = new WifiDeviceInfo();
						info.setMacadd(new String(ykbInfo.getmDeviceId()));
						info.setFitting("遥控宝-3s");
						info.setDeviceType(HardwareUtils.WIFI_UNSINGLE_DEVICE);
						info.setDeviceModel(HardwareUtils.DEVICE_MODEL_YKB_3S);
						deviceInfos.add(info);
					}
				}
				LogManager.d(TAG, "get devices  for yaokongbao: currHtNum = " + ykbSet.size());
			}
		}

		// =========== others ===========

		return deviceInfos;
	}

	public void print(String deviceName, List<?> list) {
		if (isDebug) {
			if (list == null || list.isEmpty()) {
				LogManager.i("search " + deviceName + " size=0");
			} else {
				LogManager.i("search " + deviceName + " size=" + list.size());
			}
		}
	}

	public void unRegistHuanTengReceiver() {
		if (mHuanTengDevice != null) {
			mHuanTengDevice.unRegistBroadCastReceiver();
		}
	}
}
