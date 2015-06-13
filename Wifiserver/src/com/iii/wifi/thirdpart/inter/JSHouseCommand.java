package com.iii.wifi.thirdpart.inter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.iii.wifi.dao.imf.WifiControlDao;
import com.iii.wifi.dao.imf.WifiDeviceDao;
import com.iii.wifi.dao.imf.WifiModeDao;
import com.iii.wifi.dao.imf.WifiRoomDao;
import com.iii.wifi.dao.info.WifiBoxModeInfo;
import com.iii.wifi.dao.info.WifiControlInfo;
import com.iii.wifi.dao.info.WifiDeviceInfo;
import com.iii.wifi.dao.info.WifiRoomInfo;
import com.iii.wifi.thirdpart.broadlink.BroadlinkDevice;
import com.iii.wifi.thirdpart.huanteng.HuanTengDevice;
import com.iii.wifi.thirdpart.orvibo.OrviboDao;
import com.iii.wifi.thirdpart.orvibo.OrviboDevice;
import com.iii.wifi.thirdpart.wowo.WoWoDevice;
import com.iii.wifi.thirdpart.yaokongbao.YaoKongBaoDevice;
import com.iii.wifi.util.HardwareUtils;
import com.iii.wifi.util.WaitUtil;
import com.iii360.sup.common.utl.LogManager;
import com.orvibo.lib.wiwo.i.AlloneControlResult;
import com.voice.common.util.CommandInfo;

/**
 * 家电命令
 * 
 * @author Administrator
 * 
 */
public class JSHouseCommand {

	private WifiControlDao mWifiControlDao;
	private WifiDeviceDao mWifiDeviceDao;
	private WifiModeDao mWifiModeDao;
	private WifiRoomDao mWifiRoomDao;
	private OrviboDao mOrviboDao;
	private CommandInfo mCommandInfo;
	private Context context;

	public JSHouseCommand(Context context) {
		// TODO Auto-generated constructor stub
		this.context = context;

		mWifiControlDao = new WifiControlDao(context);
		mWifiDeviceDao = new WifiDeviceDao(context);
		mWifiRoomDao = new WifiRoomDao(context);
		mWifiModeDao = new WifiModeDao(context);
		mOrviboDao = new OrviboDao(context);
	}

	/**
	 * @return 获取所有家电命令列表
	 */
	public List<CommandInfo> getCommand() {
		LogManager.i("getCommand");

		try {
			List<WifiControlInfo> infos = mWifiControlDao.selectAll();
			List<WifiBoxModeInfo> modes = mWifiModeDao.selectAll();

			List<CommandInfo> resultInfos = new ArrayList<CommandInfo>();

			for (WifiControlInfo info : infos) {
				if (TextUtils.isEmpty(info.getDorder())) {
					continue;
				}
				String Corder = info.getAction();
				String[] contents = Corder.split("\\|\\|");
				ArrayList<String> commandinfos = new ArrayList<String>();
				List<WifiRoomInfo> roomInfos = mWifiRoomDao.selectByRoomId(info.getRoomId());

				if (roomInfos.size() > 0) {
					commandinfos.add(roomInfos.get(0).getRoomName());
				} else {
					LogManager.e("get room info error " + info.getRoomId());
					commandinfos.add("");
				}

				for (String content : contents) {
					ArrayList<String> arrayList = new ArrayList<String>();
					for (String commandInfo : commandinfos) {
						if (content.contains("/")) {
							String[] newInfos = content.split("/");
							for (String newInfo : newInfos) {
								arrayList.add(commandInfo + newInfo);
							}
						} else {
							arrayList.add(commandInfo + content);
						}
					}

					commandinfos.clear();
					commandinfos = arrayList;
				}
				for (String corder : commandinfos) {
					CommandInfo rinfo = new CommandInfo(corder, String.valueOf(info.getId()));
					resultInfos.add(rinfo);
				}
			}

			// add mode data
			if (modes != null) {
				for (WifiBoxModeInfo info : modes) {
					if (!TextUtils.isEmpty(info.getControlIDs())) {
						mCommandInfo = new CommandInfo(info.getAction() + info.getModeName(), String.valueOf(info.getId()));
						LogManager.i("add mode " + info.getId() + "====" + info.getAction() + info.getModeName());
						resultInfos.add(mCommandInfo);
					}
				}
			}
			LogManager.e("===============================" + resultInfos.size());
			return resultInfos;
		} catch (Exception e) {
			// TODO: handle exception
			LogManager.e(Log.getStackTraceString(e));
		}

		return null;
	}

	/**
	 * @param controlId 控制列表id
	 * @return 通过id执行命令
	 */
	public boolean sendCommand(String controlId) {
		LogManager.i("sendCommand command id = " + controlId);

		try {
			int cmdId = Integer.valueOf(controlId);
			List<WifiControlInfo> infos = mWifiControlDao.selectById(cmdId);

			if (infos != null && infos.size() > 0) {

				if (isTouYingYi(infos)) {
					sendTwoTouYingYi(infos);
					return true;
				} else {
					return excuteCommands(infos);
				}

			} else {
				// 发送模式指令
				List<WifiBoxModeInfo> list = mWifiModeDao.selectById(controlId);

				if (list != null && !list.isEmpty()) {

					for (int i = 0; i < list.size(); i++) {
						WifiBoxModeInfo info = list.get(i);
						String controlIDs = info.getControlIDs();

						if (!TextUtils.isEmpty(controlIDs)) {
							String[] ids = controlIDs.split("\\|\\|");

							List<WifiControlInfo> wifiControlInfos;
							if (ids.length == 1) {
								wifiControlInfos = mWifiControlDao.selectById(Integer.valueOf(ids[0]));

								if (isTouYingYi(infos)) {
									sendTwoTouYingYi(infos);
									return true;
								} else {
									return excuteCommands(wifiControlInfos);
								}

							} else if (ids.length > 1) {
								// 执行多条指令，除去第一条指令
								excuteMoreCommand(ids);
								// 独自执行一条指令
								wifiControlInfos = mWifiControlDao.selectById(Integer.valueOf(ids[0]));
								if (isTouYingYi(infos)) {
									sendTwoTouYingYi(infos);
									return true;
								} else {
									return excuteCommands(wifiControlInfos);
								}
							}
						}
					}
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return false;
	}

	private boolean isTouYingYi(final List<WifiControlInfo> infos) {
		if (infos != null && !infos.isEmpty()) {
			WifiControlInfo info = infos.get(0);
			if (info.getAction().equals("关闭||投影仪")) {
				return true;
			}
		}
		return false;
	}

	// 打开投影仪执行2次
	private void sendTwoTouYingYi(final List<WifiControlInfo> infos) {
		if (infos != null && !infos.isEmpty()) {
			new Thread(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					excuteCommands(infos);
					WaitUtil.sleep(800);
					excuteCommands(infos);
				}
			}).start();
		}
	}

	private final static int SEND_CMD_CODE_DEFAULT = -1;
	private final static int SEND_CMD_CODE_OK = 0;
	private final static int SEND_CMD_CODE_ERROR = 1;
	private int sendCode = SEND_CMD_CODE_DEFAULT;
	private long startTime;
	private List<WifiControlInfo> mInfos;

	/**
	 * 执行指令
	 * 
	 * @param infos
	 * @return
	 */
	private boolean excuteCommands(List<WifiControlInfo> infos) {
		if (infos == null || infos.isEmpty()) {
			LogManager.i("执行指令:null");
			return false;
		}

		WifiControlInfo info = infos.get(0);
		this.mInfos = infos;

		LogManager.i("执行指令:" + info.getAction());

		List<WifiDeviceInfo> devicesinfos = mWifiDeviceDao.selectByDeviceId(info.getDeviceid());
		String model = info.getDeviceModel();
		String command = info.getDorder();

		if (model != null && model.contains("DEVICE_MODEL_BL")) {
			BroadlinkDevice broadlinkDevice = new BroadlinkDevice(context);
			int code = broadlinkDevice.operation(model, devicesinfos.get(0).getMacadd(), info.getDorder());
			LogManager.i("send broadlink command=" + info.getDorder() + "||code=" + code);
			return code == 0 ? true : false;

		} else if (model.equals(HardwareUtils.DEVICE_MODEL_OB_ALLONE)) {
			OrviboDevice orviboDevice = OrviboDevice.getInstance(context);
			sendCode = SEND_CMD_CODE_DEFAULT;

			if (devicesinfos != null && devicesinfos.size() > 0) {
				String uid = devicesinfos.get(0).getMacadd();
				LogManager.i("send Orvibo controlHF uid=" + uid + "||command=" + command);

				orviboDevice.sendHF(uid, command, new AlloneControlResult() {
					@Override
					public void onFailure(String arg0, int arg1) {
						// TODO Auto-generated method stub
						sendCode = SEND_CMD_CODE_ERROR;
					}

					@Override
					public void onSuccess(String arg0) {
						// TODO Auto-generated method stub
						sendCode = SEND_CMD_CODE_OK;
					}
				});
				startTime = System.currentTimeMillis();

				while ((System.currentTimeMillis() - startTime < 5000) & sendCode == SEND_CMD_CODE_DEFAULT) {
					WaitUtil.sleep(500);
				}

				LogManager.e("sendCode=" + sendCode);
				if (sendCode == SEND_CMD_CODE_ERROR || sendCode == SEND_CMD_CODE_DEFAULT) {
					return false;

				} else if (sendCode == SEND_CMD_CODE_OK) {
					return true;

				}

				return true;
			}

		} else if (model.equals(HardwareUtils.DEVICE_MODEL_OB_S20)) {
			OrviboDevice orviboDevice = OrviboDevice.getInstance(context);

			if (devicesinfos != null && devicesinfos.size() > 0) {

				String uid = devicesinfos.get(0).getMacadd();
				String action = info.getAction();
				LogManager.i("Orvibo controlHF uid=" + uid + "||action=" + action);

				if (action != null) {
					if (action.contains("打开")) {
						orviboDevice.controlOnOff(uid, true);
					} else if (action.contains("关闭")) {
						orviboDevice.controlOnOff(uid, false);
					}

					return true;
				}

			}
		} else if (model.equals(HardwareUtils.DEVICE_MODEL_HT_BULBS)) {
			if (devicesinfos != null && devicesinfos.size() > 0) {
				HuanTengDevice ht = HuanTengDevice.getHuanTengDeviceInstance(context);
				String action = info.getAction();
				String uid = devicesinfos.get(0).getMacadd();
				LogManager.i("HuanTeng uid=" + uid + "||action=" + action);
				if (ht.makeBasicToken() == null) {
					return false;
				}
				if (action.contains("打开")) {
					ht.controlOnOff(uid, true);
				} else if (action.contains("关闭")) {
					ht.controlOnOff(uid, false);
				}
				return true;
			}
		} else if (model.equals(HardwareUtils.DEVICE_MODEL_WW_TS)) {
			if (devicesinfos != null && devicesinfos.size() > 0) {
				WoWoDevice ww = WoWoDevice.getInstance();
				String action = info.getAction();
				String uid = devicesinfos.get(0).getMacadd();
				LogManager.i("WoWo uid=" + uid + "||action=" + action);
				if (action.contains("打开")) {
					ww.controlOnOff(uid, true);
				} else {
					ww.controlOnOff(uid, false);
				}
			}
			return true;
		}else if(model.equals(HardwareUtils.DEVICE_MODEL_YKB_3S)){
			if (devicesinfos != null && devicesinfos.size() > 0) {
				YaoKongBaoDevice ykbDevice = YaoKongBaoDevice.getInstance();
				String action = info.getAction();
				String uid = devicesinfos.get(0).getMacadd();
				LogManager.i("yaokongbao uid=" + uid + "||action=" + action);
				if (action.contains("打开")) {
					ykbDevice.controlOnOff(uid, true,command);
				} else {
					ykbDevice.controlOnOff(uid, false,command);
				}
			}
			return true;
		}

		return false;
	}

	/**
	 * 执行多个指令,去除第一条指令
	 * 
	 * @param ids
	 */
	private void excuteMoreCommand(final String ids[]) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				for (int j = 1; j < ids.length; j++) {
					WaitUtil.sleep(800);
					List<WifiControlInfo> wifiControlInfos = mWifiControlDao.selectById(Integer.valueOf(ids[j]));

					if (isTouYingYi(wifiControlInfos)) {
						sendTwoTouYingYi(wifiControlInfos);
					} else {
						excuteCommands(wifiControlInfos);
					}
				}
			}
		}).start();
	}
}
