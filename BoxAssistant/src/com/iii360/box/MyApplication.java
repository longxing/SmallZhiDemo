package com.iii360.box;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import com.iflytek.cloud.SpeechUtility;
import com.iii360.box.common.BasePreferences;
import com.iii360.box.receiver.GlobalReceiver;
import com.iii360.box.util.AppUtils;
import com.iii360.box.util.BoxManagerUtils;
import com.iii360.box.util.KeyList;
import com.iii360.box.util.LogManager;
import com.iii360.box.util.LogUtil;
import com.iii360.box.util.WifiUtils;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

public class MyApplication extends Application {
	private List<Activity> activityList = new LinkedList<Activity>();

	public static int screenWidthPx;

	public static int screenHeightPx;

	public static float density;

	public static int densityDPI;

	private static MyApplication mMyApplication;
	private static Map<String, Long> boxAdds = new ConcurrentHashMap<String, Long>();
	private static Map<String, String> serialNums = new ConcurrentHashMap<String, String>();
	public static final boolean isRelease = false;
	public static final boolean isNeedJudgeVersion = true;
	public static MyApplication instance;

	public static Map<String, Long> getBoxAdds() {
		return boxAdds;
	}

	public List<Activity> getActivityList() {
		return activityList;
	}
	public void setActivityList(List<Activity> activityList) {
		this.activityList = activityList;
	}
	public static void initImageLoader(Context context) {

		// File cacheDir = StorageUtils.getOwnCacheDirectory(context,
		// "ShwootideMDM/Cache");
		ImageLoaderConfiguration cfg = new ImageLoaderConfiguration.Builder(
				context)
				.threadPriority(Thread.NORM_PRIORITY - 2)
				.denyCacheImageMultipleSizesInMemory()
				// .threadPoolSize(5)
				// .discCache(new UnlimitedDiscCache(cacheDir))
				.discCacheFileNameGenerator(new Md5FileNameGenerator())
				.tasksProcessingOrder(QueueProcessingType.LIFO).build();
		ImageLoader.getInstance().init(cfg);
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		LogUtil.i("myapplication onCreate");
		try {
			ApplicationInfo appInfo = this.getPackageManager()

			.getApplicationInfo(getPackageName(),

			PackageManager.GET_META_DATA);

			String msg = appInfo.metaData.getString("IFLYTEK_APPKEY");

			SpeechUtility.createUtility(this, "appid=" + msg);
		} catch (Exception e) {
			e.printStackTrace();
		}
		super.onCreate();
		getDeviceParams();
		initImageLoader(this);
		instance = this;
		BasePreferences basePreferences = new BasePreferences(this);
		String ip = basePreferences.getPrefString(KeyList.GKEY_BOX_IP_ADDRESS);
		if (ip == null) {
			basePreferences.setPrefString(KeyList.GKEY_BOX_IP_ADDRESS, "");
		}

		WifiUtils.startScan(this);

		if (!AppUtils.isServiceRunning(this, "com.iii360.box.MyService")) {
			startService(new Intent(this, MyService.class));
			// AppUtils.repeat(this, "com.iii360.box.MyService");
		}
		registerReceiver(new GlobalReceiver(), new IntentFilter(
				Intent.ACTION_TIME_TICK));

		LogManager.e("udp=" + BoxManagerUtils.getBoxUdpPort(this));

		// test
		// new BasePreferences(this).setPrefString(KeyList.GKEY_BOX_IP_ADDRESS,
		// "192.168.20.102");
	}

	private void getDeviceParams() {
		WindowManager manager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
		DisplayMetrics dm = new DisplayMetrics();
		manager.getDefaultDisplay().getMetrics(dm);
		density = dm.density; // 屏幕密度（像素比例：0.75/1.0/1.5/2.0）
		densityDPI = dm.densityDpi;
		screenWidthPx = dm.widthPixels; // 屏幕宽（dip，如：320dip）
		screenHeightPx = dm.heightPixels;
		// Log.i("info", density+","+densityDPI);
		// Log.i("info", screenWidthPx+","+screenHeightPx);
	}

	// 单例模式中获取唯一的MyApplication实例
	public static MyApplication getInstance() {
		if (null == mMyApplication) {
			synchronized (MyApplication.class) {
				if (null == mMyApplication)
					mMyApplication = new MyApplication();
			}
		}
		return mMyApplication;
	}

	// 添加Activity到容器中
	public void addActivity(Activity activity) {
		activityList.add(activity);
	}

	// 遍历所有Activity并finish
	public void exit() {
		for (Activity activity : activityList) {
			activity.finish();
		}
		activityList.clear();
		// ImageLoader.getInstance().clearDiscCache();
		try {
			// FinalBitmap.create(this).clearDiskCache();
			ImageLoader.getInstance().clearMemoryCache();
			// ImageLoader.getInstance().clearDiscCache();
		} catch (Exception e) {
			// e.printStackTrace();
		}
		// System.exit(0);
	}

	public static Map<String, String> getSerialNums() {
		return serialNums;
	}
}
