package com.iii360.box.connect;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.iii360.box.MyApplication;
import com.iii360.box.R;
import com.iii360.box.adpter.OnLineBoxListAdapter;
import com.iii360.box.base.BaseActivity;
import com.iii360.box.config.WifiConfigActivity;
import com.iii360.box.util.KeyList;
import com.iii360.box.util.ToastUtils;
import com.iii360.box.util.WifiCRUDUtil;
import com.iii360.box.util.WifiUtils;
import com.iii360.box.view.IView;
import com.iii360.box.view.MyProgressDialog;

public class OnLineBoxListActivity extends BaseActivity implements IView, OnClickListener {
	private ListView onlineboxListView;
	private OnLineBoxListAdapter onlineBoxListAdapter;
	private TextView onlineboxNoneTv;
	private ArrayList<String> boxIpList;
	private Button btnBottomAdd;
	private TextView onlineBack;
	private ImageButton onlineAddbtn;
	private int time = 1000;
	private String inner;
	private MyProgressDialog dialog;
	private static final int HANDLE_CHECK = 1;
	private Handler mHandler = new Handler() {
		private ArrayList<String> list = new ArrayList<String>();

		public void handleMessage(Message msg) {
			switch (msg.what) {
			case HANDLE_CHECK:
				ArrayList<String> list = (ArrayList<String>) msg.obj;
				if (!list.equals(this.list)) {
					initDatas();
				}
				this.list = list;
				Message _msg = new Message();
				_msg.what = HANDLE_CHECK;
				_msg.obj = getCurrList();
				mHandler.sendMessageDelayed(_msg, time);
				break;
			}
		};
	};

	@Override
	protected void onResume() {
		super.onResume();

	}

	private Map<String, Long> map;
	private Handler handler;
	protected OnLineBoxHandler onlineboxHandler;
	public static final int HANDLER_CONNECT_SUCCESS = 1;
	public static final int HANDLER_CONNECT_FAIL = 2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_online_boxlist);
		inner = getIntent().getStringExtra("inner");
		map = MyApplication.getBoxAdds();
		initViews();
		initDatas();
		sendMessage();

		// createThread();
		// needCheck = true;
		// isLoop = true;
		// checkThead.start();
	}

	private void sendMessage() {
		Message msg = new Message();
		msg.what = HANDLE_CHECK;
		ArrayList<String> list = getCurrList();
		msg.obj = list;
		mHandler.sendMessage(msg);
	}

	private ArrayList<String> getCurrList() {
		Map<String, Long> map = MyApplication.getBoxAdds();
		ArrayList<String> list = new ArrayList<String>();
		Iterator<String> it = map.keySet().iterator();
		while (it.hasNext()) {
			list.add(it.next());
		}
		return list;
	}

	// private void createThread() {
	// checkThead = new Thread() {
	// @Override
	// public void run() {
	// while (isLoop) {
	// while (needCheck && isLoop) {
	// ArrayList<String> listBefore = getCurrList();
	// WaitUtils.sleep(500);
	// ArrayList<String> listAfter = new ArrayList<String>();
	// it = map.keySet().iterator();
	// while (it.hasNext()) {
	// listAfter.add(it.next());
	// }
	// if (!listAfter.equals(listBefore)) {
	// new Handler(Looper.getMainLooper())
	// .post(new Runnable() {
	//
	// @Override
	// public void run() {
	// initDatas();
	// }
	// });
	// }
	// }
	//
	// if (isLoop) {
	// synchronized (checkThead) {
	// try {
	// checkThead.wait();
	//
	// } catch (Exception e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// }
	// }
	// }
	// }
	// };
	// }

	@Override
	protected void onStop() {
		super.onStop();
		mHandler.removeCallbacksAndMessages(null);
	}

	@Override
	protected void onDestroy() {
		if (onlineboxHandler != null) {
			onlineboxHandler.clearProgressDialog();
		}
		if (dialog != null && !isFinishing()) {
			dialog.dismiss();
		}
		dialog = null;
		super.onDestroy();
		if (onlineboxHandler == null)
			return;
		onlineboxHandler.cleanMainThreadTask();
	}

	@Override
	protected void onRestart() {
		super.onRestart();
		sendMessage();
	}

	@Override
	public void initViews() {
		dialog = new MyProgressDialog(context);
		dialog.setMessage(getString(R.string.ba_set_connecting));
		((TextView) findViewById(R.id.head_title_tv)).setText("连接新音箱");
		onlineBack = (TextView) findViewById(R.id.head_left_textview);
		onlineBack.setText("返回");
		onlineBack.setOnClickListener(this);
		onlineAddbtn = (ImageButton) findViewById(R.id.head_right_btn);
		onlineAddbtn.setVisibility(View.VISIBLE);
		onlineAddbtn.setImageResource(R.drawable.online_box_add_selector);
		onlineAddbtn.setOnClickListener(this);
		btnBottomAdd = (Button) findViewById(R.id.online_box_bottom_add_btn);
		btnBottomAdd.setOnClickListener(this);
		onlineboxListView = (ListView) findViewById(R.id.online_box_listview);
		onlineBoxListAdapter = new OnLineBoxListAdapter(null, context);
		onlineboxListView.setAdapter(onlineBoxListAdapter);
		onlineboxNoneTv = (TextView) findViewById(R.id.online_box_none_textview);
		onlineboxNoneTv.setText("当前无线网络下没有发现在线音箱");
		onlineboxNoneTv.setVisibility(View.GONE);
		onlineboxListView.setVisibility(View.GONE);
		onlineboxHandler = new OnLineBoxHandler(OnLineBoxListActivity.this, dialog, true);
		onlineboxListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

				if (boxIpList.isEmpty()) {
					return;
				}
				// 播报‘我在这里’
				// say(position);

				String ClickBoxIp = boxIpList.get(position);
				// intent.putExtra("BoxIp", ClickBoxIp);
				// ((PreferenceActivity)
				// context).setPrefString(KeyList.GKEY_BOX_IP_ADDRESS,
				// ClickBoxIp);
				// cleanActivitys();
				onlineboxHandler.connectBox(ClickBoxIp);
				// context.startActivity(intent);
				// ((Activity) context).finish();
			}
		});
	}


	protected void say(int position) {
		String ip = boxIpList.get(position);
		WifiCRUDUtil.playTTS(ip, context, "我在这里");
	}

	@Override
	public void initDatas() {
		boxIpList = new ArrayList<String>();
		Map<String, Long> map = MyApplication.getBoxAdds();
		if (map.isEmpty()) {
			onlineboxNoneTv.setVisibility(View.VISIBLE);
			onlineboxListView.setVisibility(View.GONE);
			btnBottomAdd.setVisibility(View.VISIBLE);
			return;
		}
		btnBottomAdd.setVisibility(View.GONE);
		onlineboxNoneTv.setVisibility(View.GONE);
		onlineboxListView.setVisibility(View.VISIBLE);
		Iterator<String> it = map.keySet().iterator();
		while (it.hasNext()) {
			String ip = it.next();
			if (ip.equals(getPrefString(KeyList.GKEY_BOX_IP_ADDRESS))) {
				boxIpList.add(0, ip);
			} else {
				boxIpList.add(ip);
			}
		}

		/***
		 * 
		 * 
		 */

		// it = map.keySet().iterator();
		// while (it.hasNext()) {
		// boxIpList.add(it.next());
		// }

		/***
		 * 
		 * 
		 */

		onlineBoxListAdapter.setList(boxIpList);
		onlineBoxListAdapter.notifyDataSetChanged();
		// if (inner == null) {
		// if (boxIpList.size() == 1) {
		// startToActvitiy(ConnectActivity.class);
		// }
		// }
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.head_left_textview:
			finish();
			break;
		case R.id.head_right_btn:
			if (!WifiUtils.isConnectWifi(this)) {
				// ToastUtils.show(context, R.string.main_connect_wifi);
				startToActvitiyNoFinish(UnConnectWifiActivity.class);
				return;
			}
			if (WifiUtils.isConnectWifi(context, KeyList.BOX_WIFI_SSID)) {
				ToastUtils.show(context, "当前网络为盒子热点，请切换网络");
				startToActvitiyNoFinish(UnConnectWifiActivity.class);
				return;
			}
			startToActvitiyNoFinish(WifiConfigActivity.class);
			break;
		case R.id.online_box_bottom_add_btn:
			if (!WifiUtils.isConnectWifi(this)) {
				// ToastUtils.show(context, R.string.main_connect_wifi);
				startToActvitiyNoFinish(UnConnectWifiActivity.class);
				return;
			}
			if (WifiUtils.isConnectWifi(context, KeyList.BOX_WIFI_SSID)) {
				ToastUtils.show(context, "当前网络为盒子热点，请切换网络");
				startToActvitiyNoFinish(UnConnectWifiActivity.class);
				return;
			}
			startToActvitiyNoFinish(WifiConfigActivity.class);
			break;

		}
	}
}
