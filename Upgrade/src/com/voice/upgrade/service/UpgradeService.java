package com.voice.upgrade.service;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.zip.ZipException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import com.base.network.NetworkTool;
import com.iii360.base.common.utl.BaseContext;
import com.iii360.base.common.utl.LogManager;
import com.iii360.base.inf.IGlobalValueOperation;
import com.iii360.base.inf.IPrefrenceOperation;
import com.iii360.base.inf.ITTSController.ITTSStateListener;
import com.iii360.sup.common.utl.ShellUtils;
import com.iii360.sup.common.utl.file.FileUtil;
import com.iii360.sup.common.utl.file.SimpleFileDownload;
import com.iii360.sup.common.utl.file.ZipUtil;
import com.voice.upgrade.service.aidl.UpgradeManager;
import com.voice.upgrade.util.LightService;
import com.voice.upgrade.util.TTSUtil;

public class UpgradeService extends Service {
	// private int status = KeyList.RUN_STATUS_IDLE;// 0闲置状态1忙碌状态
	private Properties config = new Properties();
	private String installVersion = null;// 要更新的版本
	private String sdcardPath = "/sdcard.zip";

	// 调度线程
	private Thread dispatchThread = null;
	// 准备线程
	private Thread readyThread = null;
	// 安装线程
	private Thread installThread = null;

	/*
	 * 安装步骤
	 */
	private final static int APK_STEP_INIT = 0;// 刚下载
	private final static int APK_STEP_WAIT = 1;// 等待答复，广播已发出
	private final static int APK_STEP_READY = 2;// 准备安装，答复已收到
	private final static int APK_STEP_END = 3;// 安装完毕

	/**
	 * 更新明细
	 */
	private JSONArray installDetails = null;
	private boolean isInstallSelf = false;// 是否需要更新服务自己

	private static boolean isInstalling = false;// 是否安装过程中

	public static boolean isInstalling() {
		return isInstalling;
	}

	private TTSUtil ttsUtil = null;

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	public void updateMode() {
		String sec = "4";
		String mode = "update";
		FileOutputStream fileOutputStream = null;
		try {

			fileOutputStream = new FileOutputStream("mnt/sdcard/shut_config");
			fileOutputStream.write((sec + " " + mode).getBytes("UTF-8"));
			isInstalling = true;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (fileOutputStream != null) {
				try {
					fileOutputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void normalMode() {
		String sec = "3";
		String mode = "normal";
		FileOutputStream fileOutputStream = null;
		try {

			fileOutputStream = new FileOutputStream("mnt/sdcard/shut_config");
			fileOutputStream.write((sec + " " + mode).getBytes("UTF-8"));
			isInstalling = false;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (fileOutputStream != null) {
				try {
					fileOutputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private boolean isNull = false;
	private Properties properties;
	@Override
	public void onCreate() {
		super.onCreate();
		this.loadConfig();
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				while (true) {
					try {
						Thread.sleep(1000*60 *5);
						ActivityManager activityManager=(ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);  
				        String runningActivity=activityManager.getRunningTasks(1).get(0).topActivity.getClassName();
				        if(runningActivity==null || runningActivity.equals("") || !runningActivity.equals("com.voice.assistant.main.AssistantMainActivity")){
				        	ShellUtils.execute(false, "su", "-c", "am", "force-stop", "com.voice.assistant.main");
				        	write("PKEY_IS_UNCAUGHT_EXCEPTION", String.valueOf(true));
				        	ShellUtils.execute(false, "su", "-c", "am", "start", "-n", "com.voice.assistant.main/com.voice.assistant.main.AssistantLauncherActivity");
				        }
					} catch (InterruptedException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}).start();
		try {
			if(new File("system/app/com_voice_assistant.apk").exists()){
				ShellUtils.execute(false, "su", "-c", "mount", "-o", "remount", "rw", "/mnt/sdcard");
				ShellUtils.execute(false, "su", "-c", Environment.getExternalStorageDirectory().getPath() + "/quanxian.sh");
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		new Thread(new Runnable() {

			@Override
			public void run() {
				String appPaths = "system/app/";
				String[] names = { "com_voice_assistant", "com_iii_wifiserver" };
				for (String name : names) {
					File file = new File(appPaths + name + ".apk");
					String n_path = "mnt/sdcard/install_packages/" + name+ ".apk";
					String n_dir = "mnt/sdcard/install_packages/" + name;
					File f_n_path = new File(n_path);
					try {
						if ((!file.exists() || (f_n_path.exists() && file.length()!=f_n_path.length())) && new File(n_dir).exists()) {
							installSystem(new File(n_path));
							isNull = true;
						}
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					}catch (IOException e) {
						e.printStackTrace();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				handler.sendEmptyMessage(1);
			}
		}).start();

		this.disptach();

	}
	
	private void write(String key, String value) {
		FileOutputStream fos = null;
		String path = "/mnt/sdcard/com.voice.assistant.main/properties/main_preferences.properties";
		try {
			File file = new File(path);
			if (!file.getParentFile().exists()) {
				file.getParentFile().mkdirs();
			}
			if (!file.exists()) {
				file.createNewFile();
			}
			properties = getProperties(path);
			fos = new FileOutputStream(path);
			if (value == null) {
				value = "";
			}
			properties.setProperty(key, value);
			properties.store(fos, null);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (fos != null) {
					fos.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	private Properties getProperties(String propertiesPath) {
		InputStream inputStream = null;
		if (properties == null) {
			properties = new Properties();
		}
		try {
			inputStream = new BufferedInputStream(new FileInputStream(propertiesPath));
			properties.load(inputStream);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return properties;
	}

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			if (isNull) {
				try {
					isNull = false;
					Runtime.getRuntime().exec("su -c reboot");
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		};
	};

	@Override
	public void onDestroy() {
		super.onDestroy();
		// 重新启动
		this.startService(new Intent(this, this.getClass()));
	}

	/**
	 * 加载配置信息
	 */
	private void loadConfig() {
		InputStream in = null;
		InputStream apkInstallIn = null;
		InputStream apkCleanIn = null;
		InputStream upgradeInstallIn = null;
		try {
			File file = new File(Environment.getExternalStorageDirectory().getPath() + "/upgrade.properties");
			if (!file.exists()) {
				in = this.getAssets().open("config.properties");
				FileOutputStream fout = new FileOutputStream(file);
				this.config.load(in);
				this.config.store(fout, "config for upgrade server.");
				fout.close();
			} else {
				in = new FileInputStream(file);
				this.config.load(in);
			}
			// 拷贝权限脚本
			String shName = "quanxian.sh";
			apkInstallIn = this.getAssets().open(shName);
			FileUtil.writeFile(apkInstallIn, Environment.getExternalStorageDirectory().getPath() + "/", shName, true);
			// 拷贝安装脚本
			shName = "apkInstall.sh";
			apkInstallIn = this.getAssets().open(shName);
			FileUtil.writeFile(apkInstallIn, Environment.getExternalStorageDirectory().getPath() + "/", shName, true);
			// 清除脚本
			shName = "apkClean.sh";
			apkCleanIn = this.getAssets().open(shName);
			FileUtil.writeFile(apkCleanIn, Environment.getExternalStorageDirectory().getPath() + "/", shName, true);
			// 更新服务自身更新脚本
			shName = "upgradeInstall.sh";
			upgradeInstallIn = this.getAssets().open(shName);
			FileUtil.writeFile(upgradeInstallIn, Environment.getExternalStorageDirectory().getPath() + "/", shName, true);
			// 安装耗时-默认30分钟
			String stime = config.getProperty("installWaste");
			long installWaste = stime == null ? 30 * 60 * 1000 : Integer.valueOf(stime);
			config.setProperty("installWaste", "" + installWaste);
		} catch (FileNotFoundException e) {
			LogManager.e(e.getMessage());
		} catch (IOException e) {
			LogManager.e(e.getMessage());
		} finally {

			try {
				if (in != null) {
					in.close();
				}
				if (apkInstallIn != null) {
					apkInstallIn.close();
				}
				if (apkCleanIn != null) {
					apkCleanIn.close();
				}
				if (upgradeInstallIn != null) {
					upgradeInstallIn.close();
				}
			} catch (IOException e) {
				LogManager.e(e.getMessage());
			}

		}
		// 初始化TTS
		try {
			ttsUtil = TTSUtil.getInstance(this);
		} catch (Exception e) {
			LogManager.e(e.getMessage());
		}
	}

	/**
	 * 开启更新调度
	 */
	private void disptach() {
		dispatchThread = new Thread(new Runnable() {
			private boolean isError = false;

			public void run() {
				normalMode();
				for (;;) {
					try {
						if (!isError) {
							// 检查间隔时间
							long interval = Integer.valueOf(config.getProperty("interval"));
							// 准备下一次更新检查
							Thread.sleep(interval);
						}
						isError = false;
						LogManager.e("-----------check version!-------------");
						UpgradeService.this.check();
					} catch (InterruptedException e) {
						// 安装超时
						try {
							Thread.sleep(1 * 60 * 1000);
						} catch (InterruptedException e1) {
							e1.printStackTrace();
						}
						isError = true;
						normalMode();
					} catch (Exception e) {
						LogManager.e(e.getMessage());
						try {
							Thread.sleep(1 * 60 * 1000);
						} catch (InterruptedException e1) {
							e1.printStackTrace();
						}
						isError = true;
						normalMode();
					}
				}
			}
		});
		dispatchThread.start();
	}

	/**
	 * 更新服务程序的版本号
	 * 
	 * @return
	 */
	public String getServerVersion() {
		try {
			PackageInfo packageInfo = this.getPackageManager().getPackageInfo(this.getPackageName(), 0);
			return packageInfo.versionName;
		} catch (NameNotFoundException e) {
			return this.config.getProperty("default_server_version");
		}
	}

	/**
	 * 应用套件版本号
	 * 
	 * @return
	 */
	public String getVersion() {
		return this.config.getProperty("version");
	}

	/**
	 * 检查更新
	 * 
	 * @throws InterruptedException
	 * @throws IOException
	 * @throws JSONException
	 * @throws ZipException
	 * @throws FileNotFoundException
	 */
	public void check() throws InterruptedException, FileNotFoundException, ZipException, JSONException, IOException {
		// 删除上一次的
		try {
			// this.unregisterReceiver(LoopReceiver);
			// this.unregisterReceiver(agreeReceiver);
		} catch (Exception e) {

		}
		// 请求网络，查询版本号
		String url = this.config.getProperty("url") + "?version=" + this.config.getProperty("version") + "&sn=" + ShellUtils.readSerialNumber();
		final JSONObject json = NetworkTool.getNetworkAsJson(url);
		if (json != null) {
			// 开始安装
			Date checkTime = new Date();// 开始更新的时间
			if (json.getInt("code") == 1) {
				LogManager.e("check response:" + json.toString());
				if (json.getLong("time") <= checkTime.getTime()) {
					// 清除安装过程
					clear();
					// 注册回路接收广播
					IntentFilter loopIntentFilter = new IntentFilter();
					loopIntentFilter.setPriority(IntentFilter.SYSTEM_LOW_PRIORITY);

					installVersion = json.getString("version");
					// 有新的更新,并且到了更新时间, 状态为忙碌，避免重复更新
					// status = KeyList.RUN_STATUS_BUSY;
					// 下载apk包
					installDetails = json.getJSONArray("apk");
					for (int i = 0; i < installDetails.length(); i++) {
						JSONObject apkItem = installDetails.getJSONObject(i);
						String key = apkItem.getString("key");
						String downloadUrl = apkItem.getString("url");
						int pi = downloadUrl.lastIndexOf('.');
						pi = pi < 0 ? downloadUrl.length() : pi;
						String filename = key.replace('.', '_');
						apkItem.put("filename", filename);
						apkItem.put("readyState", APK_STEP_INIT);// 未发出广播通知
						LogManager.e("-----------download apk!-------------");
						if (!downloadPackage(downloadUrl, filename + ".apk")) {
							LogManager.e("[Upgrade|check]error, version=" + installVersion + ",time=" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(checkTime) + ", download file="
									+ downloadUrl); // 终止此次更新
							LogManager.printMessageToServer("[Upgrade|check]error, version=" + installVersion + ",time=" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(checkTime) + ", download file="
									+ downloadUrl);
							// status = KeyList.RUN_STATUS_IDLE;
							return;
						}
						String fileName="/mnt/sdcard/install_packages/" + filename+ ".apk";
						if(!getMd5ByFile(new File(fileName)).trim().equalsIgnoreCase(apkItem.getString("md5").trim())){
							return;
						}
						// 解压缩
						String path = ZipUtil.autoUnapk(fileName, "/mnt/sdcard/install_packages/");
						if (path == null || !new File(path).exists()) {
							return;
						}
						// 广播通知更新
						loopIntentFilter.addAction(key + ".upgrade.READY_UPGRADE");
					}
					// 更新sdcard文件
					LogManager.e("-----------download sdcard!-------------");
					String sdcardFileUrl = json.optString("sdcard");
					if (sdcardFileUrl != null && sdcardFileUrl.length() > 0 && !sdcardFileUrl.equals("null")) {
						if (!downloadSdcard(sdcardFileUrl)) {
							LogManager.e("[Upgrade|check]error, version=" + installVersion + ",time=" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(checkTime) + ", download file="
									+ sdcardFileUrl); // 终止此次更新
							LogManager.printMessageToServer("[Upgrade|check]error, version=" + installVersion + ",time=" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(checkTime) + ", download file="
									+ sdcardFileUrl);
							// status = KeyList.RUN_STATUS_IDLE;
							return;
						}
						// 解压缩
						String sdcardDir = Environment.getExternalStorageDirectory().getPath();
						ZipUtil.unzip(sdcardDir + sdcardPath, sdcardDir);

					}
					// 注册回路监听
					// registerReceiver(LoopReceiver, loopIntentFilter);
					// registerReceiver(agreeReceiver, new IntentFilter(
					// "com.voice.upgrade.AGREE_UPGRADE"));
					// 发送广播, 通知是否可以重新使得安装生效
					Thread.sleep(1000);// 让注册生效
					LogManager.e("-----------wait for broadcast!-------------");
					sendUpgradeBroadcast();
				}

				// 等待安装广播
				long installWaste = Integer.valueOf(config.getProperty("installWaste"));
				Thread.sleep(installWaste);
				LogManager.e("-----------setup overtime!-------------");
				// 是否正在安装
				if (isInstalling && installThread != null) {
					// 安装中，则等待安装完成
					LogManager.e("-----------wait for setup finish!-------------");
					installThread.join();
				} else {
					// 广播超时
					try {
						// this.unregisterReceiver(LoopReceiver);
						// this.unregisterReceiver(agreeReceiver);
					} catch (Exception e) {

					}
				}
				return;
			} else {
				LogManager.d("check response:" + json.toString());
			}
		}

	}
	
	private String getMd5ByFile(File file) throws FileNotFoundException {
        String value = null;  
        FileInputStream in = new FileInputStream(file);  
    try {  
        MappedByteBuffer byteBuffer = in.getChannel().map(FileChannel.MapMode.READ_ONLY, 0, file.length());  
        MessageDigest md5 = MessageDigest.getInstance("MD5");  
        md5.update(byteBuffer);  
        String HEX = "0123456789abcdef";  
        byte[] bytes=md5.digest();
        StringBuilder sb = new StringBuilder(bytes.length * 2);  
        for (byte b : bytes){  
            sb.append(HEX.charAt((b >> 4) & 0x0f));  
            sb.append(HEX.charAt(b & 0x0f));  
        }  
        value=sb.toString(); 
    } catch (Exception e) {  
        e.printStackTrace();  
    } finally {  
            if(null != in) {  
                try {  
                in.close();  
            } catch (IOException e) {  
                e.printStackTrace();  
            }  
        }  
    }  
    return value;  
    }  

	public boolean downloadSdcard(String apkurl) {
		String sdcardDir = Environment.getExternalStorageDirectory().getPath();
		// String filename = apkurl.substring(apkurl.lastIndexOf('/') + 1);
		return SimpleFileDownload.downLoad(apkurl, sdcardDir + sdcardPath);
	}

	/**
	 * 下载安装文件
	 * 
	 * @param apkurl
	 * @return
	 */
	public boolean downloadPackage(String apkurl, String filename) {
		String packagesDir = Environment.getExternalStorageDirectory().getPath() + this.config.getProperty("install_packages");
		File installPackages = new File(packagesDir);

		if (!installPackages.exists()) {
			installPackages.mkdirs();
		}
		// String filename = apkurl.substring(apkurl.lastIndexOf('/') + 1);
		return SimpleFileDownload.downLoad(apkurl, packagesDir + "/" + filename);
	}

	/**
	 * 安装到沙盒里, /data/app
	 * 
	 * @param apkpath
	 * @throws IOException
	 * @throws InterruptedException
	 */
	// private void installSandbox(File file) throws IOException,
	// InterruptedException {
	// String apkpath = file.getPath();
	// String result = ShellUtils.execute(false, new String[] { "adb",
	// "install", "-r", apkpath });
	// if (result == "") {
	// // 失败
	// LogManager.e("[Upgrade|install]exec error, apkdir=" + apkpath);
	// throw new RuntimeException("[Upgrade|install]exec error, apkdir="
	// + apkpath);
	// } else {
	// // 成功
	// LogManager.e("[Upgrade|install]runtime success, apkdir=" + apkpath);
	// }
	//
	// }
	// private void installSandbox(File file) throws IOException,
	// InterruptedException {
	// String apkpath = file.getPath();
	// String result = ShellUtils.execute(false, new String[] { "adb",
	// "install", "-r", apkpath });
	// if (result == "") {
	// // 失败
	// LogManager.e("[Upgrade|install]exec error, apkdir=" + apkpath);
	// throw new RuntimeException("[Upgrade|install]exec error, apkdir="
	// + apkpath);
	// } else {
	// // 成功
	// LogManager.e("[Upgrade|install]runtime success, apkdir=" + apkpath);
	// }
	//
	// }
//	private void installSandbox(File file) throws IOException, InterruptedException {
//		boolean result = ShellUtils.slientInstall(file);
//		String apkpath = file.getPath();
//
//		if (file.getName().contains("assistant")) {
//			String pg_main = "com.voice.assistant.main";
//			ShellUtils.execute(false, "su", "-c", "am", "start", "-n", pg_main + "/" + pg_main + ".AssistantLauncherActivity");
//		}
//		if (!result) {
//			// 失败
//			LogManager.printMessageToServer("[Upgrade|install]exec error, apkdir=" + apkpath);
//			
//
//			// if(file.getName().contains("wifiserver")){
//			// String pg_wify="com.iii.wifiserver";
//			// ShellUtils.execute(false, "su", "-c", "am",
//			// "startservice","-n",pg_wify+"/"+pg_wify+".DogControllerService");
//			// }
//
//			throw new RuntimeException("[Upgrade|install]exec error, apkdir=" + apkpath);
//		} else {
//			// 成功
//			LogManager.printMessageToServer("[Upgrade|install]runtime success, apkdir=" + apkpath);
//			ShellUtils.execute(false, "su", "-c", "mount", "-o", "remount", "rw", "/system");
//			String path = "/system/app/" + file.getName();
//			if (new File(path).exists()) {
//				ShellUtils.execute(false, "su", "-c", "rm", "-f", path);
//			}
//		}
//	}

	/**
	 * 安装到系统里，/system/data
	 * 
	 * @param apkpath
	 * @throws IOException
	 * @throws InterruptedException
	 */
	private void installSystem(File file) throws IOException, InterruptedException {
		// 截取名字
		String apkpath = file.getPath();
		int pi = apkpath.lastIndexOf('.');
		pi = pi < 0 ? apkpath.length() : pi;
		// 解压目录
		String apkdir = apkpath.substring(0, pi);
		apkdir = apkdir + "/";

		String result = "";
		LogManager.printMessageToServer("[Upgrade|install]runtime success, apkdir=" + apkpath);
		// 挂载sdcard
		ShellUtils.execute(false, "su", "-c", "mount", "-o", "remount", "rw", "/mnt/sdcard");
		// 关掉apk
		String pg_name = "";
		if (file.getName().contains("assistant")) {
			pg_name = "com.voice.assistant.main";
		} else if (file.getName().contains("wifiserver")) {
			pg_name = "com.iii.wifiserver";
		}
		if (!pg_name.equals("")) {
			ShellUtils.execute(false, "su", "-c", "am", "force-stop", pg_name);
		}

		// 安装到system
		result = ShellUtils.execute(false, "su", "-c", Environment.getExternalStorageDirectory().getPath() + "/apkInstall.sh", apkpath, apkdir + "/lib/armeabi");


		if (result == null || result == "") {
			// 失败
			LogManager.printMessageToServer("[Upgrade|install]exec error, apkdir=" + apkpath);
			throw new RuntimeException("[Upgrade|install]exec error, apkdir=" + apkpath);
		} else {
			// 成功
			LogManager.printMessageToServer("[Upgrade|install]runtime success, apkdir=" + apkpath);
			// 修复兼容性
			this.hack(apkdir + "/assets/hack.sh");
		}
	}

	/**
	 * 安装更新服务自己
	 * 
	 * @throws IOException
	 * @throws InterruptedException
	 */
	private void installSelf() throws IOException, InterruptedException {
		String packagesDir = Environment.getExternalStorageDirectory().getPath() + this.config.getProperty("install_packages");
		String selfPackage = this.getPackageName().replace('.', '_') + ".apk";
		File file = new File(packagesDir + "/" + selfPackage);
		// 截取名字
		String apkpath = file.getPath();
		int pi = apkpath.lastIndexOf('.');
		pi = pi < 0 ? apkpath.length() : pi;
		// 解压目录
		String apkdir = apkpath.substring(0, pi) + "/";

		// 修复兼容性
		this.hack(apkdir + "/assets/hack.sh");
		String result = "";
		// 执行完成自动重启了
		LogManager.printMessageToServer("[Upgrade|install]runtime success, apkdir=" + apkpath);
		// 挂载sdcard
		result = ShellUtils.execute(false, "su", "-c", "mount", "-o", "remount", "rw", "/mnt/sdcard");
		// 挂载system
		result = ShellUtils.execute(false, "su", "-c", Environment.getExternalStorageDirectory().getPath() + "/upgradeInstall.sh", apkpath, apkdir + "/lib/armeabi");
	}

	public synchronized void install() throws IOException, InterruptedException {
		LogManager.printMessageToServer("install now......");

		final LightService lightService = LightService.getInstance();
		try {
			if (lightService.isWorking()) {
				// 播报
				ttsUtil.play("正在更新系统，请不要断开电源。");
				ttsUtil.setTTSStateListener(new ITTSStateListener() {

					@Override
					public void onStart() {
						// TODO Auto-generated method stub

					}

					@Override
					public void onInit() {
						// TODO Auto-generated method stub

					}

					@Override
					public void onError() {
						onEnd();
					}

					@Override
					public void onEnd() {
						lightService.playAnimation();
						ttsUtil.setTTSStateListener(null);
						synchronized (lightService) {
							lightService.notifyAll();
						}
					}
				});
				// 阻塞,等播报结束
				synchronized (lightService) {
					lightService.wait();
				}
			} else {
				// 灯

				lightService.playAnimation();
				synchronized (lightService) {
					lightService.notifyAll();
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			synchronized (lightService) {
				lightService.notifyAll();
			}
		}

		//
		String packagesDir = Environment.getExternalStorageDirectory().getPath() + this.config.getProperty("install_packages");
		File installPackages = new File(packagesDir);
		if (!installPackages.exists()) {
			// 无可安装项
			return;
		}

		String selfPackage = this.getPackageName().replace('.', '_') + ".apk";
		File[] apks = installPackages.listFiles();
		for (File file : apks) {
			if (!file.isDirectory()) {
				if (file.getName().equals(selfPackage)) {
					// 更新自身的服务
					this.isInstallSelf = true;
				} else {
					// 安装到沙盒
					// this.installSandbox(file);
					// 安装到系统
					this.installSystem(file);
				}
			}
		}

		// 保存当前套件版本号
		this.config.setProperty("version", this.installVersion);
		this.preConfig();

		// 更新自身，追加在后面
		if (this.isInstallSelf) {
			this.isInstallSelf = false;
			this.installSelf();
		}

		// 保存配置
		this.saveConfig();
		// 关闭提示灯
		LightService.getInstance().stopAnimation();
	}

	/**
	 * 准备一个配置文件
	 */
	public void preConfig() {
		LogManager.e("pre config");

		FileOutputStream out = null;
		try {
			out = new FileOutputStream(Environment.getExternalStorageDirectory().getPath() + "/upgrade.cache");
			this.config.store(out, "config for upgrade server.");

		} catch (FileNotFoundException e) {
			LogManager.e(e.getMessage());
		} catch (IOException e) {
			LogManager.e(e.getMessage());
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 保存配置文件
	 * 
	 * @throws InterruptedException
	 * @throws IOException
	 */
	public void saveConfig() throws IOException, InterruptedException {
		LogManager.e("save config:" + this.config);
		String result = "";
		result = ShellUtils.execute(false, "su", "-c", "cp", "-f", Environment.getExternalStorageDirectory().getPath() + "/upgrade.cache", Environment.getExternalStorageDirectory().getPath()
				+ "/upgrade.properties");
	}

	/**
	 * 更新之前清理上次更新的数据
	 * 
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public void clear() throws IOException, InterruptedException {
		this.isInstallSelf = false;
		// 挂载sdcard
		String result = "";
		result = ShellUtils.execute(false, "su", "-c", "mount", "-o", "remount", "rw", "/mnt/sdcard");
		result = ShellUtils.execute(false, "su", "-c", Environment.getExternalStorageDirectory().getPath() + "/apkClean.sh");
		result = ShellUtils.execute(false, "su", "-c", "rm", "-f", Environment.getExternalStorageDirectory().getPath() + "/sdcard.zip");
		result = ShellUtils.execute(false, "su", "-c", "rm", "-f", Environment.getExternalStorageDirectory().getPath() + "/upgrade.cache");
	}

	/**
	 * 向下兼容性
	 * 
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public void hack(String hackFile) throws IOException, InterruptedException {
		if (new File(hackFile).exists()) {
			LogManager.printMessageToServer("[Upgrade|install]hack old version:");
			// 执行脚本
			String result = "";
			result = ShellUtils.execute(false, "su", "-c", "mount", "-o", "remount", "rw", "/mnt/sdcard");
			result = ShellUtils.execute(false, "su", "-c", hackFile);
		}
	}

	public void uninstall(String[] libname) {
		for (String lib : libname) {
			// 执行删除脚本
			try {
				Runtime.getRuntime().exec("ls");
				LogManager.e("[Upgrade|uninstall]runtime success, lib=" + lib);
			} catch (IOException e) {
				LogManager.e("[Upgrade|uninstall]exec error, lib=" + lib + ", " + e.getMessage());
			}
		}
	}

	/**
	 * aidl support
	 */
	private UpgradeManager.Stub mBinder = new UpgradeManager.Stub() {

		@Override
		public String version() throws RemoteException {
			return UpgradeService.this.getVersion();
		}

		@Override
		public void install(String[] apkfile) throws RemoteException {
			try {
				UpgradeService.this.install();
			} catch (IOException e) {
				LogManager.e(e.getMessage());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		@Override
		public void uninstall(String[] libfiles) throws RemoteException {
			UpgradeService.this.uninstall(libfiles);
		}

		@Override
		public String serverVersion() throws RemoteException {
			return UpgradeService.this.getServerVersion();
		}

		@Override
		public void check() throws RemoteException {
			try {
				UpgradeService.this.check();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	};

	// -----------------broadcast support---------------//
	/**
	 * 发送更新广播
	 * 
	 * @param action
	 */
	public void sendUpgradeBroadcast() {
		try {
			for (int i = 0; i < this.installDetails.length(); i++) {
				JSONObject apkItem = this.installDetails.getJSONObject(i);
				String action = apkItem.getString("key") + ".upgrade.READY_UPGRADE";
				Intent intent = new Intent();
				intent.setAction(action);
				intent.putExtra("serverBroadcast", "com.voice.upgrade.AGREE_UPGRADE");
				// this.sendOrderedBroadcast(intent, null);
				apkItem.put("readyState", APK_STEP_WAIT);// 等到答复
				LogManager.e("send broadcast >>" + action);
				agree(action);
			}
		} catch (JSONException e) {

		}
	}

	/**
	 * 如果发出去的广播最终由回路接收，说明没有其他人收到广播
	 */
	// private BroadcastReceiver LoopReceiver = new BroadcastReceiver() {
	//
	// public void onReceive(Context arg0, final Intent intent) {
	// if (installDetails == null || installDetails.length() == 0) {
	// return;
	// }
	// abortBroadcast();
	// Animate.post(new Runnable() {
	//
	// @Override
	// public void run() {
	// String key = intent.getAction();
	// LogManager.e("receiver broadcast myself <<" + key);
	// agree(key);
	// }
	// });
	// }
	//
	// };

	/**
	 * 某个apk同意升级
	 */
	// private BroadcastReceiver agreeReceiver = new BroadcastReceiver() {
	// public void onReceive(Context arg0, final Intent intent) {
	// if (installDetails == null || installDetails.length() == 0) {
	// return;
	// }
	// // abortBroadcast();
	// Animate.post(new Runnable() {
	//
	// @Override
	// public void run() {
	// String key = intent.getStringExtra("clientBroadcast");
	// LogManager.e("receiver broadcast agree <<" + key);
	// agree(key);
	// }
	// });
	// }
	// };

	/**
	 * 同意
	 * 
	 * @param key
	 */
	public synchronized void agree(String key) {
		boolean isAgreeAll = true;
		try {
			// 将当前广播设为APK_STEP_READY
			for (int i = 0; i < this.installDetails.length(); i++) {
				JSONObject apkItem = this.installDetails.getJSONObject(i);
				String action = apkItem.getString("key") + ".upgrade.READY_UPGRADE";
				int readyState = apkItem.getInt("readyState");
				// 设置标志位
				if (action.equals(key)) {
					LogManager.e("action=" + key + ", readyState=" + readyState + "=>" + APK_STEP_READY);
					// 可以立刻更新
					apkItem.put("readyState", APK_STEP_READY);
				}
			}
			LogManager.printMessageToServer("-----------check ready!-------------");
			// 检查是否所有广播都接收完毕
			for (int i = 0; i < this.installDetails.length(); i++) {
				JSONObject apkItem = this.installDetails.getJSONObject(i);
				int readyState = apkItem.getInt("readyState");
				// 存在未就绪的组件，则不更新
				if (readyState != APK_STEP_READY) {
					// 未能立刻更新
					LogManager.printMessageToServer("[" + apkItem.getString("key") + "] is not ready!");
					isAgreeAll = false;
					return;
				}
			}
		} catch (JSONException e) {

		}
		if (isAgreeAll) {
			// 处理超时
			installThread = new Thread(new Runnable() {

				@Override
				public void run() {
					updateMode();

					//
					// 全部同意，重启设备使安装生效
					try {
						// unregisterReceiver(LoopReceiver);
						// unregisterReceiver(agreeReceiver);
					} catch (Exception e) {

					}
					installDetails = null;
					try {
						// 立刻安装
						LogManager.e("------------install start!---------------");
						install();
						// 重启
						ShellUtils
								.execute(false, "su", "-c", "rm", "-f",
										"/data/data/com.android.launcher/databases/launcher.db");
						Runtime.getRuntime().exec("su -c reboot");
					} catch (IOException e) {
						LogManager.e(e.getMessage());
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					// 复位
					normalMode();
					// 恢复到自动更新
					dispatchThread.interrupt();
				}
			});
			installThread.start();
		}
	}
}
