package com.smallzhi.homeappliances.control;

import java.util.HashMap;

import android.content.Context;

import com.iii360.base.common.utl.LogManager;
import com.iii360.base.inf.ControlInterface;
import com.smallzhi.homeappliances.util.CoreUtil;
import com.smallzhi.homeappliances.util.CoreUtil.OnGetDevAdd;

public class ClientManager implements ControlInterface {

	HashMap<String, IControlInterface> clients = new HashMap<String, IControlInterface>();
	private NewOnGetDevAdd newAdd;
	String result2 = "";
	String result1 = "";
	private Context mContext;

	public interface NewOnGetDevAdd {
		/**
		 * 当连接到主机的时候，
		 * 
		 * @param control
		 */
		public void onGetAdd(ControlInterface control, String result);

		/**
		 * 当和家庭主机断开连接的时候
		 */
		public void onDisConnect();
	}

	public void setOngetDev(NewOnGetDevAdd onAdd) {
		this.newAdd = onAdd;
		CoreUtil coreUtil = CoreUtil.getInstance(mContext);
		coreUtil.setOnGetDevAdd(new OnGetDevAdd() {

			@Override
			public void onGetAdd(IControlInterface control) {
				// String type = keys.getMainClass().getHash().get("IP") +
				// keys.getMainClass().getHash().get("PORT");
				String type = "~~~";
				if (control == null) {

					// result1 = "连接主机失败，请检查ip地址设置和端口设置，";
					result1 = " ";
				} else {
					LogManager.e(type);
					addClient(control, type);
					// result1 = "连接主机成功，";
					result1 = " ";
				}
				LogManager.e(result1);
				if (result2.length() > 0) {
					newAdd.onGetAdd(ClientManager.this, result1 + result2);
				}

			}

			@Override
			public void onDisConnect() {

				String type = "~~~";
				removeClient(type);
				// newAdd.onDisConnect();
			}
		});
		coreUtil.findDevice();
		new DogClient(mContext, new OnGetDevAdd() {

			@Override
			public void onGetAdd(IControlInterface control) {

				if (control != null) {
					addClient(control, DogClient.TYPE);
				} else {
					// result2 = "连接小狗失败";
					result2 = "  ";
				}
				LogManager.e(result2);
				if (result1.length() > 0) {
					newAdd.onGetAdd(ClientManager.this, result1 + result2);
				}
			}

			@Override
			public void onDisConnect() {
				removeClient(DogClient.TYPE);
			}

		});

	}

	public ClientManager(Context c) {
		mContext = c;
	}

	public void addClient(IControlInterface control, String type) {
		clients.put(type, control);
	}

	public void removeClient(String type) {
		clients.remove(type);
	}

	@Override
	public void init() {
		// TODO Auto-generated method stub
	}

	@Override
	public String sendMsg(String msg, String target) {
		LogManager.e(target);
		if (clients.containsKey(target)) {
			return clients.get(target).sendMsg(msg);
		} else {
			// for(IControlInterface _interface:clients.values()){
			// _interface.sendMsg(msg);
			// }
			return "没有找到目标设备";
		}
	}

	@Override
	public void setOnGetMsg(IgetMsg onGetMeg) {
		// TODO Auto-generated method stub

	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "客户端大总管";
	}

	@Override
	public void destory() {
		for (IControlInterface client : clients.values()) {
			client.destory();
		}
		clients.clear();
	}

}
