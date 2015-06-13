package com.iii360.box.connect;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.iii.client.WifiConfig;
import com.iii.wifi.dao.info.WifiBoxSystemInfo;
import com.iii.wifi.dao.newmanager.WifiCRUDForBoxSystem;
import com.iii360.box.MyApplication;
import com.iii360.box.base.BaseActivity;
import com.iii360.box.util.KeyList;
import com.iii360.box.util.ToastUtils;
import com.iii360.box.util.WifiCRUDUtil;
import com.iii360.box.view.MyProgressDialog;

public class OnLineBoxHandler {
	private BaseActivity activity;
	private Handler handler;
	private Map<String, Long> map;
	public static final int HANDLER_CONNECT_SUCCESS = 1;
	public static final int HANDLER_CONNECT_FAIL = 2;
	public static final int HANDLER_CHANGE_LIST = 3;
//	public static final int HANDLER_RETRY_OLD_TCPPORT = 4;
	private boolean isClick;
	private MyProgressDialog dialog;

	public OnLineBoxHandler(BaseActivity activity) {
		this.activity = activity;
		initHandler();
	}

	public OnLineBoxHandler(BaseActivity activity, MyProgressDialog dialog, boolean isClick) {
		this.isClick = isClick;
		this.activity = activity;
		this.dialog = dialog;
		initHandler();
	}

	public void handle() {
		validateConnect();
	}

	public void clearProgressDialog() {
		if (dialog != null && !activity.isFinishing())
			dialog.dismiss();
		dialog = null;
	}

	private void initHandler() {
		handler = new Handler() {

			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case HANDLER_CHANGE_LIST:
					if (activity instanceof OnLineBoxListActivity) {
						((OnLineBoxListActivity) activity).initDatas();
					}
					break;
				case HANDLER_CONNECT_SUCCESS:
					String ip = (String) msg.obj;
					try {
						if (currConnectingIp == null || !currConnectingIp.equals(ip)) {
							return;
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
					if (!TextUtils.isEmpty(ip) && map.containsKey(ip)) {
						activity.setPrefString(KeyList.GKEY_BOX_IP_ADDRESS, ip);
						cleanActivitys();
						activity.startToMainActvitiy();
					} else {
						if (handler != null)
							handler.sendEmptyMessage(HANDLER_CONNECT_FAIL);
					}
					// startToActvitiy(MainActivity.class);
					break;
				case HANDLER_CONNECT_FAIL:
					if (isClick) {
						// activity.setPrefString(KeyList.GKEY_BOX_IP_ADDRESS,
						// "");
						ToastUtils.show(activity, "连接超时，请稍候再试");
						if (handler != null)
							handler.sendEmptyMessage(HANDLER_CHANGE_LIST);
						return;
					}
					activity.setPrefString(KeyList.GKEY_BOX_IP_ADDRESS, "");
					activity.startToActvitiy(OnLineBoxListActivity.class);
					// TODO 连接失败
					// failTime++;
					// if (failTime == 1) {
					// if
					// (map.containsKey(activity.getPrefString(KeyList.GKEY_BOX_IP_ADDRESS)))
					// {
					// connectBox();
					// } else {
					// activity.setPrefString(KeyList.GKEY_BOX_IP_ADDRESS, "");
					// activity.startToActvitiy(OnLineBoxListActivity.class);
					// }
					// } else {
					//
					// }

					break;
//				case HANDLER_RETRY_OLD_TCPPORT:
//					ip = (String) msg.obj;
//					try {
//						if (currConnectingIp == null || !currConnectingIp.equals(ip)) {
//							return;
//						}
//					} catch (Exception e) {
//						e.printStackTrace();
//					}
//					if (!TextUtils.isEmpty(ip) && map.containsKey(ip)) {
//						retryOldTcpPort(ip);
//					} else {
//						if (handler != null) {
//							handler.post(dismissDialog);
//							handler.sendEmptyMessage(HANDLER_CONNECT_FAIL);
//						}
//					}
//
//					break;

				}
			}
		};

	}

	private Runnable dismissDialog = new Runnable() {

		@Override
		public void run() {
			if (dialog != null && !activity.isFinishing())
				dialog.dismiss();
		}
	};

	private void validateConnect() {
		map = MyApplication.getBoxAdds();
		if (map.containsKey(activity.getPrefString(KeyList.GKEY_BOX_IP_ADDRESS))) {
			connectBox(activity.getPrefString(KeyList.GKEY_BOX_IP_ADDRESS));
			return;
		}
		if (map.size() == 1) {
			connectBox(map.keySet().iterator().next());
		} else {
			activity.setPrefString(KeyList.GKEY_BOX_IP_ADDRESS, "");
			new Handler().post(new Runnable() {
				public void run() {// TODO 没有设备或多个设备
					activity.startToActvitiy(OnLineBoxListActivity.class);
				}
			});
		}
	}

	private String currConnectingIp;

	public void connectBox(final String ip) {
		currConnectingIp = ip;
		if (dialog != null)
			dialog.show();
		map = MyApplication.getBoxAdds();
		if (TextUtils.isEmpty(ip)) {
			if (handler != null)
				handler.sendEmptyMessage(HANDLER_CONNECT_FAIL);
			return;
		}
		WifiCRUDForBoxSystem sys = new WifiCRUDForBoxSystem(ip, WifiConfig.TCP_DEFAULT_PORT);
		sys.getSystemInfo(new WifiCRUDForBoxSystem.ResultListener() {
			public void onResult(String code, final WifiBoxSystemInfo info) {
				if (dialog != null && !dialog.isShowing()) {
					return;
				}
				if (handler != null)
					handler.post(dismissDialog);
				if (WifiCRUDUtil.isSuccessAll(code)) {
					activity.setPrefString(KeyList.PKEY_REQUEST_HARDVERSION, info.getVersionCode() + "");
					Message msg = new Message();
					msg.what = HANDLER_CONNECT_SUCCESS;
					msg.obj = ip;
					if (handler != null)
						handler.sendMessage(msg);
				} else {
					if (handler != null)
						// ToastUtils.show(context, "音箱版本不支持此功能");
						handler.sendEmptyMessage(HANDLER_CONNECT_FAIL);
					// ToastUtils.show(context, "音箱版本不支持此功能");
					// handler.sendEmptyMessage(HANDLER_CONNECT_FAIL);
//					Message msg = new Message();
//					msg.what = HANDLER_RETRY_OLD_TCPPORT;
//					msg.obj = ip;
//					if (handler != null)
//						handler.sendMessage(msg);
					// handler.post(new Runnable() {
					//
					// @Override
					// public void run() {
					// retryOldTcpPort();
					// }
					// });
				}
			}
		});
		// new Thread() {
		// public void run() {
		//
		//
		// boolean isConnected = false;
		// try {
		// Socket mSocket = new Socket();
		// try {
		// mSocket.connect(new
		// InetSocketAddress(BoxManagerUtils.getBoxIP(activity),
		// BoxManagerUtils.getBoxTcpPort(activity)));
		// isConnected = mSocket.isConnected();
		// } catch (Exception e1) {
		// e1.printStackTrace();
		// } finally {
		// try {
		// mSocket.close();
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
		// }
		// for (int i = 0; !isConnected && i < 2; i++) {
		// if
		// (map.containsKey(activity.getPrefString(KeyList.GKEY_BOX_IP_ADDRESS)))
		// {
		// try {
		// Thread.sleep(100);
		// } catch (InterruptedException e) {
		// e.printStackTrace();
		// }
		// mSocket = new Socket();
		// try {
		// mSocket.connect(new
		// InetSocketAddress(BoxManagerUtils.getBoxIP(activity),
		// BoxManagerUtils.getBoxTcpPort(activity)));
		// isConnected = mSocket.isConnected();
		// } catch (Exception e1) {
		// e1.printStackTrace();
		// } finally {
		// try {
		// mSocket.close();
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
		// }
		// }
		// }
		//
		// } catch (Exception e) {
		// e.printStackTrace();
		// handler.sendEmptyMessage(HANDLER_CONNECT_FAIL);
		// return;
		// }
		// if (isConnected) {
		// handler.sendEmptyMessage(HANDLER_CONNECT_SUCCESS);
		// } else {
		// handler.sendEmptyMessage(HANDLER_CONNECT_FAIL);
		// }
		//
		// }
		// }.start();
	}

//	protected void retryOldTcpPort(final String ip) {
//		WifiCRUDForBoxSystem sys = new WifiCRUDForBoxSystem(ip, WifiConfig.TCP_DEFAULT_PORT);
//		sys.getSystemInfo(new WifiCRUDForBoxSystem.ResultListener() {
//			public void onResult(String code, final WifiBoxSystemInfo info) {
//				if (dialog != null && !dialog.isShowing()) {
//					return;
//				}
//				if (handler != null)
//					handler.post(dismissDialog);
//				if (WifiCRUDUtil.isSuccessAll(code)) {
//					String versionCode = "";
//					try {
//						versionCode = info.getVersionCode().trim();
//					} catch (Exception e) {
//					}
//					if (versionCode != null && !"null".equals(versionCode) && !"".equals(versionCode) && versionCode.compareTo("2.5.2") <= 0) {
////						ToastUtils.show(MyApplication.instance, "请升级音箱，当前版本:" + info.getVersionCode());
//						if (handler != null)
//							handler.postDelayed(new Runnable() {
//								public void run() {
//									playTTSTip(ip);
//								}
//							}, 0);
//					} else {
//						if (handler != null)
//							handler.sendEmptyMessage(HANDLER_CONNECT_FAIL);
//					}
//				} else {
//					if (handler != null)
//						// ToastUtils.show(context, "音箱版本不支持此功能");
//						handler.sendEmptyMessage(HANDLER_CONNECT_FAIL);
//				}
//			}
//		});
//	}

//	protected void playTTSTip(String ip) {
//		// if (!isClick) {
//		WifiCRUDUtil.playTTS(ip, WifiConfig.TCP_DEFAULT_PORT, activity, "当前音箱系统版本过低，请保持音箱处于联网状态，等待几分钟后，音箱系统将会自动进行升级，若升级失败请拨打音箱底部客服电话");
//		// TODO
//		LogUtil.d(ip + "为旧音箱");
//		// activity.setPrefString(KeyList.GKEY_BOX_IP_ADDRESS, "");
//		if (!(activity instanceof OnLineBoxListActivity)) {
//			activity.startToActvitiy(OnLineBoxListActivity.class);
//		}
//		// }
//	}

	public void cleanMainThreadTask() {
		try {
			if (handler != null)
				handler.removeCallbacksAndMessages(null);
			handler = null;
		} catch (Exception e) {
		}
	}

	private void cleanActivitys() {
		List<Activity> acts = MyApplication.getInstance().getActivityList();
		for (Activity ac : acts) {
			if (ac instanceof OnLineBoxListActivity) {
				continue;
			}
			if (ac instanceof BootActivity) {
				continue;
			}
			ac.finish();
		}
		MyApplication.getInstance().setActivityList(new LinkedList<Activity>());
	}

}
