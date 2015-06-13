package com.iii360.box;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.iii.wifi.dao.info.WifiDeviceInfo;
import com.iii.wifi.dao.info.WifiRoomInfo;
import com.iii.wifi.dao.manager.WifiCRUDForRoom;
import com.iii.wifi.dao.manager.WifiCRUDForRoom.ResultListener;
import com.iii.wifi.dao.manager.WifiCRUDForWeatherTime;
import com.iii360.box.about.MainAboutActivity;
import com.iii360.box.base.BaseActivity;
import com.iii360.box.config.GetNewDevice;
import com.iii360.box.config.NoSmartActivity;
import com.iii360.box.config.PartsManagerActivity;
import com.iii360.box.connect.OnLineBoxListActivity;
import com.iii360.box.connect.UnConnectWifiActivity;
import com.iii360.box.help.HelpActivity;
import com.iii360.box.music.MusicSearchActivity;
import com.iii360.box.set.MainSetActivity;
import com.iii360.box.util.AdaptUtil;
import com.iii360.box.util.BoxManagerUtils;
import com.iii360.box.util.KeyList;
import com.iii360.box.util.LogUtil;
import com.iii360.box.util.ToastUtils;
import com.iii360.box.util.WifiCRUDUtil;
import com.iii360.box.util.WifiUtils;
import com.iii360.box.view.IView;
import com.iii360.box.view.MyProgressDialog;
import com.iii360.box.voice.VoiceRecognizeActivity;
import com.iii360.box.ximalaya.XimalayaCategoryActivity;
import com.umeng.update.UmengUpdateAgent;

/**
 * 盒子助手主界面,一个房间只允许1个硬件控制，比如只允许机器狗控制客厅空调
 * 
 * @author hefeng
 * 
 */
public class MainActivity extends BaseActivity implements IView, OnClickListener {
	private BDLocationListener mMyLocationListener = new MyLocationListener();
	private LocationClient mLocationClient;
	private GetNewDevice mGetNewDevice;
	private ArrayList<WifiDeviceInfo> mWifiDeviceInfos;
	private MyProgressDialog myProgressDialog;
	private ImageButton mainMoreMenu;
	private View showMenuBg;

	public class MyLocationListener implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {
			String city = location.getCity();
			if (!TextUtils.isEmpty(city)) {
				// TODO 发送到音箱端
				LogUtil.d("location:" + city);
				WifiCRUDForWeatherTime wifiCRUDForWeatherTime = new WifiCRUDForWeatherTime(context, getBoxIp(), getBoxTcpPort());
				mLocationClient.stop();
				wifiCRUDForWeatherTime.setWeatherCityName(city, false, null);
//				ToastUtils.show(context, location.getDistrict());
			}
		}
	}

	public void dismissDialog() {
		new Handler(Looper.getMainLooper()).post(new Runnable() {

			@Override
			public void run() {
				if (myProgressDialog != null && !isFinishing())
					myProgressDialog.dismiss();
			}
		});
	}

	public void showDialog() {
		if (myProgressDialog == null)
			return;
		myProgressDialog.show();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main6);
		initViews();
		addListeners();
		this.initDatas();
		initUmengUpdate();
		initBaiduLoc();
	}

	private void initBaiduLoc() {
		mLocationClient = new LocationClient(getApplicationContext());
		mLocationClient.registerLocationListener(mMyLocationListener);
		LocationClientOption option = new LocationClientOption();
		option.setLocationMode(LocationMode.Hight_Accuracy);// 设置定位模式
		option.setCoorType("bd09ll");// 返回的定位结果是百度经纬度,默认值gcj02
		option.setScanSpan(5000);// 设置发起定位请求的间隔时间为5000ms
		option.setIsNeedAddress(true);// 返回的定位结果包含地址信息
		option.setNeedDeviceDirect(true);// 返回的定位结果包含手机机头的方向
		mLocationClient.setLocOption(option);
		mLocationClient.start();
	}

	private void initUmengUpdate() {
//		UmengUpdateAgent.setAppkey(KeyList.KEY_UMENG_APP_KEY);
//		UmengUpdateAgent.setChannel("Umeng");
		UmengUpdateAgent.silentUpdate(context);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (myProgressDialog != null && !this.isFinishing())
			myProgressDialog.dismiss();
		myProgressDialog = null;
		mLocationClient.stop();
	}

	public void initViews() {
		showMenuBg = findViewById(R.id.grey_bg);
		// waveView = (CircleWaveView) findViewById(R.id.main_circleWaveView);
		myProgressDialog = new MyProgressDialog(context);
		myProgressDialog.setMessage(getString(R.string.ba_update_date));
		mainMoreMenu = (ImageButton) findViewById(R.id.main_more_menu);
		myProgressDialog.setCanceledOnTouchOutside(false);
		final TextView mHeadTitle = (TextView) context.findViewById(R.id.head_title_tv);
		mHeadTitle.setText(getString(R.string.app_name));
	}

	/**
 * 
 */
	// terry start
	private void addListeners() {
		mainMoreMenu.setOnClickListener(this);
		RelativeLayout layout = (RelativeLayout) findViewById(R.id.main_relative_btn_smart);
		layout.setOnClickListener(this);
		layout = (RelativeLayout) findViewById(R.id.main_relative_btn_set);
		layout.setOnClickListener(this);
		layout = (RelativeLayout) findViewById(R.id.main_relative_btn_memo);
		layout.setOnClickListener(this);
		layout = (RelativeLayout) findViewById(R.id.main_relative_btn_mode);
		layout.setOnClickListener(this);
		layout = (RelativeLayout) findViewById(R.id.main_relative_btn_music);
		layout.setOnClickListener(this);
		layout = (RelativeLayout) findViewById(R.id.main_relative_btn_ximalaya);
		layout.setOnClickListener(this);
		layout = (RelativeLayout) findViewById(R.id.main_relative_btn_help);
		layout.setOnClickListener(this);
		layout = (RelativeLayout) findViewById(R.id.main_relative_btn_voice);
		layout.setOnClickListener(this);
		layout.setOnLongClickListener(new OnLongClickListener() {
			public boolean onLongClick(View v) {
				startVoiceActivity();
				return false;
			}
		});
	}


	@Override
	public void initDatas() {
		mWifiDeviceInfos = new ArrayList<WifiDeviceInfo>();
		mHandler.sendEmptyMessage(HANDLER_GET_NEW_DEVICE);
		mGetNewDevice = new GetNewDevice(context);
	}

	private static final int HANDLER_GET_NEW_DEVICE = 3;
	private static final int HANDLER_JUMP_TO_SMART_OR_MODE = 6;
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			int what = msg.what;

			switch (what) {
			case HANDLER_GET_NEW_DEVICE:
				mWifiDeviceInfos = mGetNewDevice.getNewDeviceByUdp();
				mHandler.sendEmptyMessageDelayed(HANDLER_GET_NEW_DEVICE, 1000);
				break;
			case HANDLER_JUMP_TO_SMART_OR_MODE:
				if (myProgressDialog != null && !isFinishing())
					myProgressDialog.dismiss();
				Object[] objarray = (Object[]) msg.obj;
				jump((Class) objarray[0], (Boolean) objarray[1]);
				break;
			default:
				break;
			}
		}

	};

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			sendBroadcast(new Intent(KeyList.AKEY_CANCEL_NEW_DEVICE_NOTIFICATION));
			MyApplication.getInstance().exit();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	// terry start
	@Override
	public void onClick(View v) {
		if (!WifiUtils.isConnectWifi(this)) {
			startToActvitiyNoFinish(UnConnectWifiActivity.class);
			return;
		}
		switch (v.getId()) {
		case R.id.main_relative_btn_music:
			jumpToMusic();
			break;
		case R.id.main_relative_btn_memo:
			startToActvitiyNoFinish(RemindListActivity.class);
			break;
		// case R.id.main_relative_btn_about:
		// startToActvitiyNoFinish(MainAboutActivity.class);
		//
		// break;
		case R.id.main_relative_btn_mode:
			jumpWith(MainModeActivity.class);
			break;
		case R.id.main_relative_btn_set:
			startToActvitiyNoFinish(MainSetActivity.class);
			break;
		case R.id.main_relative_btn_smart:

			jumpWith(PartsManagerActivity.class);
			break;
		case R.id.main_relative_btn_ximalaya:

			jumpToXimalaya();
			break;
		case R.id.main_relative_btn_help:
			jumpToWithChecknet(HelpActivity.class);
			break;
		case R.id.main_relative_btn_voice:
			startVoiceActivity();
			// waveView.setVisibility(View.VISIBLE);
			// findViewById(R.id.main_relative_btn_voice).setVisibility(
			// View.GONE);
			// setParam();
			// iatDialog.setListener(recognizerDialogListener);
			// iatDialog.show();
			break;
		case R.id.main_more_menu:
			showPopupWindow();
			break;
		}
	}

	public void startVoiceActivity() {
		if (!AdaptUtil.isNewProtocol252()) {
			ToastUtils.show(context, R.string.old_box_tip);
			return;
		}
		Intent intent = new Intent(context, VoiceRecognizeActivity.class);
		intent.putExtra("NEED_JUMP", true);
		startActivity(intent);
	}

	/***
	 * 跳转以前判断网络是不是可用
	 */
	private void jumpToWithChecknet(final Class cls) {
		myProgressDialog.show();
		new Thread() {
			public void run() {
				boolean available = checkNet();
				mHandler.post(dismissDialog);
				if (available) {
					mHandler.post(new Runnable() {
						public void run() {
							startToActvitiyNoFinish(cls);
						}
					});
				} else {
					ToastUtils.show(getApplicationContext(), "当前网络不可用");
				}
			};
		}.start();

	}

	/***
	 * 
	 * @return当前网络是不是能上网
	 */
	private boolean checkNet() {
		HttpClient client = null;
		try {
			client = new DefaultHttpClient();
			HttpResponse res = client.execute(new HttpGet("http://www.baidu.com"));
			if (res.getStatusLine().getStatusCode() == 200) {
				return true;
			}
		} catch (Exception e) {
		} finally {
			try {
				client.getConnectionManager().shutdown();
			} catch (Exception e) {
			}
		}
		return false;
	}

	private void jumpToMusic() {
		new Handler(Looper.getMainLooper()).post(new Runnable() {

			@Override
			public void run() {
				startToActvitiyNoFinish(PlayingMusicActivity.class);

			}
		});

		// myProgressDialog.show();
		// WifiCRUDForBoxSystem sys = new
		// WifiCRUDForBoxSystem(BoxManagerUtils.getBoxIP(context),
		// BoxManagerUtils.getBoxTcpPort(context));
		// sys.getSystemInfo(new WifiCRUDForBoxSystem.ResultListener() {
		// public void onResult(String code, final WifiBoxSystemInfo info) {
		// mHandler.post(dismissDialog);
		// if (WifiCRUDUtil.isSuccessAll(code)) {
		// mHandler.post(new Runnable() {
		//
		// @Override
		// public void run() {
		// Intent intent = new Intent(getApplicationContext(),
		// PlayingMusicActivity.class);
		// intent.putExtra(KeyList.KEY_HARDVERSION_EXTRA_STRING,
		// info.getVersionCode() != null ? info.getVersionCode() : "");
		// startActivity(intent);
		// }
		// });
		// } else {
		// // ToastUtils.show(context, "音箱版本不支持此功能");
		// ToastUtils.show(context, R.string.ba_get_data_error_toast);
		// }
		// }
		// });
	}

	private Runnable dismissDialog = new Runnable() {

		@Override
		public void run() {
			if (myProgressDialog != null && !isFinishing())
				myProgressDialog.dismiss();
		}
	};

	private void jumpToXimalaya() {
		if (AdaptUtil.isNewProtocol252()) {
			jumpToWithChecknet(XimalayaCategoryActivity.class);
		} else {
			ToastUtils.show(context, R.string.old_box_tip);
		}
	}

	// terry end
	private void jumpWith(final Class clss) {
		myProgressDialog.show();
		WifiCRUDForRoom room = new WifiCRUDForRoom(context, getBoxIp(), getBoxTcpPort());

		room.seleteAll(new ResultListener() {
			@Override
			public void onResult(String type, String errorCode, List<WifiRoomInfo> info) {
				// TODO Auto-generated method stub
				boolean data = false;
				if (WifiCRUDUtil.isSuccessAll(errorCode)) {
					if (null == info || info.isEmpty()) {
						data = false;
					} else {
						data = true;
					}
					Message msg = new Message();
					msg.what = HANDLER_JUMP_TO_SMART_OR_MODE;
					msg.obj = new Object[] { clss, data };
					mHandler.sendMessage(msg);
				} else {
					ToastUtils.show(context, R.string.ba_get_data_error_toast);
					mHandler.post(dismissDialog);
				}
			}
		});

	}

	private void jump(final Class clss, boolean data) {
		if ((mWifiDeviceInfos == null || mWifiDeviceInfos.isEmpty()) && !data) {
			Intent intent = new Intent(getApplicationContext(), NoSmartActivity.class);
			intent.putExtra("classname", clss);
			startActivity(intent);
		} else {
			startToActvitiyNoFinish(clss);
		}
	}

	private String[] items = { "搜索", "连接新音箱", "设置", "关于" };
	private int[] res = { R.drawable.ba_main_menu_search_selector, R.drawable.ba_main_menu_add_selector, R.drawable.ba_main_menu_set_selector, R.drawable.ba_main_menu_about_selector };
	private LinearLayout menuLayout;
	private PopupWindow menuPopupWindow;

	private class InnerAdapter extends BaseAdapter {
		private LayoutInflater inflater;

		public InnerAdapter() {
			inflater = LayoutInflater.from(context);
		}

		@Override
		public int getCount() {
			return items.length;
		}

		@Override
		public Object getItem(int position) {
			return items[position];
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View v, ViewGroup parent) {
			ViewHolder vh = null;
			if (v == null) {
				v = inflater.inflate(R.layout.main_menu_item, null);
				vh = new ViewHolder();
				vh.tv = (TextView) v.findViewById(R.id.main_menu_item_name);
				vh.layout = (RelativeLayout) v.findViewById(R.id.main_menu_item_layout);
				android.view.ViewGroup.LayoutParams params = vh.layout.getLayoutParams();
				params.width = BoxManagerUtils.getScreenWidthPx(context) / 3;
				vh.layout.setLayoutParams(params);
				RelativeLayout.LayoutParams tvParams = (android.widget.RelativeLayout.LayoutParams) vh.tv.getLayoutParams();
				tvParams.addRule(RelativeLayout.CENTER_IN_PARENT,RelativeLayout.TRUE);
				vh.tv.setLayoutParams(tvParams);
				v.setTag(vh);

			} else {
				vh = (ViewHolder) v.getTag();
			}

			vh.tv.setText(items[position]);
			Drawable rightDrawable = getResources().getDrawable(res[position]);
			rightDrawable.setBounds(0, 0, rightDrawable.getMinimumWidth(), rightDrawable.getMinimumHeight());
			vh.tv.setCompoundDrawables(null, rightDrawable, null, null);
			return v;
		}

		class ViewHolder {
			TextView tv;
			RelativeLayout layout;
		}
	}

	void showDialog(int x, int y) {

		View view = getLayoutInflater().inflate(R.layout.main_more_menu, null);
		fillView(view);
		Dialog dialog = new Dialog(this, R.style.transparentFrameWindowStyle);
		dialog.setContentView(view, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		Window window = dialog.getWindow();
		// 设置显示动画
		// window.setWindowAnimations(R.style.AnimationPreview);
		WindowManager.LayoutParams wl = window.getAttributes();
		wl.x = x;
		wl.y = y;
		// 以下这两句是为了保证按钮可以水平满屏
		wl.width = ViewGroup.LayoutParams.MATCH_PARENT;
		wl.height = ViewGroup.LayoutParams.WRAP_CONTENT;

		// 设置显示位置
		dialog.onWindowAttributesChanged(wl);
		// 设置点击外围解散
		dialog.setCanceledOnTouchOutside(true);
		dialog.show();
	}

	private void fillView(View view) {
		GridView gridView = (GridView) view.findViewById(R.id.main_menu_gridView);
		gridView.setNumColumns(3);
		gridView.setSelector(new ColorDrawable(Color.TRANSPARENT));
		gridView.setAdapter(new InnerAdapter());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add("menu");
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onMenuOpened(int featureId, Menu menu) {
		showPopupWindow();
		return false;
	}

	public void showPopupWindow() {
		mHandler.postDelayed(new Runnable() {
			public void run() {
				showMenuBg.setVisibility(View.VISIBLE);
			}
		}, 100);
		mainMoreMenu.setImageResource(R.drawable.main_more_pressed);
		menuLayout = (LinearLayout) LayoutInflater.from(MainActivity.this).inflate(R.layout.main_more_menu, null);
		menuLayout.setFocusableInTouchMode(true);
		// sub_view 是PopupWindow的子View
		menuLayout.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				// TODO Auto-generated method stub
				if ((keyCode == KeyEvent.KEYCODE_MENU) && (menuPopupWindow.isShowing())) {
					menuPopupWindow.dismiss();// 这里写明模拟menu的PopupWindow退出就行
					menuPopupWindow = null;
					return true;
				}
				return false;
			}
		});
		GridView gridView = (GridView) menuLayout.findViewById(R.id.main_menu_gridView);
		gridView.setNumColumns(3);
		gridView.setSelector(new ColorDrawable(Color.TRANSPARENT));
		gridView.setAdapter(new InnerAdapter());
		// 参数的设定
		menuPopupWindow = new PopupWindow(menuLayout, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, true);
		// popupWindow.setBackgroundDrawable(null);//new BitmapDrawable());
		// 文档上写可以设置一个背景图或者设为null,但是设置为null是不行的，感兴趣的朋友可以试试。
		// popupWindow.setBackgroundDrawable(new BitmapDrawable());
		// 这样设置一下就可以实现标题说的了，点击popupWindow以外的区域就可以让它消失了。
		// 其实我看了BitmapDrawable这个类，这个类的无参构造函数已经标为@deprecated了，
		// 就是说已经不建议使用了。我们这里这么用也是投机取巧了。
		menuPopupWindow.setBackgroundDrawable(new ColorDrawable());
		// menuPopupWindow.setWidth(getWindowManager().getDefaultDisplay()
		// .getWidth() / 3);
		// menuPopupWindow.setHeight(300);
		menuPopupWindow.setAnimationStyle(R.style.AnimationPreview);
		menuPopupWindow.update();
		// 设置popupWindow以外的区域可以相应触摸事件
		// menuPopupWindow.setOutsideTouchable(true);
		// 设置popupWindow.setFocusable(true);
		// 这样才能让popupWindow里面的布局控件获得点击的事件，否则就被它的父亲view给拦截了。
		// menuPopupWindow.setFocusable(true);
		menuPopupWindow.setOnDismissListener(new OnDismissListener() {
			public void onDismiss() {
				mainMoreMenu.setImageResource(R.drawable.main_more_normal);
				showMenuBg.setVisibility(View.GONE);
			}
		});
		menuPopupWindow.setContentView(menuLayout);
		// showAsDropDown会把里面的view作为参照物，所以要那满屏幕parent
		menuPopupWindow.showAsDropDown(findViewById(R.id.main_title), 0, 0);
		// menuPopupWindow.showAtLocation(findViewById(R.id.main),
		// Gravity.TOP, x, y);// 需要指定Gravity，默认情况是center.
		gridView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				// button.setText(title[arg2]);
				// 调用Dismiss进行了收回
				menuPopupWindow.dismiss();
				menuPopupWindow = null;
				switch (arg2) {
				case 0:
					if (!AdaptUtil.isNewProtocol252()) {
						ToastUtils.show(context, R.string.old_box_tip);
						return;
					}
					startToActvitiyNoFinish(MusicSearchActivity.class);
					break;
				case 1:
					startToActvitiyNoFinish(OnLineBoxListActivity.class);
					break;
				case 2:
					startToActvitiyNoFinish(MainSetActivity.class);
					break;
				case 3:
					startToActvitiyNoFinish(MainAboutActivity.class);
					break;
				}
			}
		});
	}
}
