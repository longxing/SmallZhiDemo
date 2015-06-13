package com.voice.assistant.utl;

import android.content.Context;

import com.iii360.base.umeng.OnlineConfigurationUtil;
import com.iii360.base.umeng.UmengOnlineConfig;
import com.iii360.sup.common.utl.SystemUtil;
//import com.iii360.base.upgrade.DownloadManager;

public class IFlytecUtil {
	private static final String IFLYTEC_PACKAGE_NAME = "com.iflytek.speechcloud";
	public static  boolean  isInstallIFlytec(Context context) {
		return SystemUtil.isInstallPackage(context, IFLYTEC_PACKAGE_NAME);
	}
	
	public static void downloadIFlyTec(Context context) {
		OnlineConfigurationUtil onLineConfigurationUtil = new OnlineConfigurationUtil(context);
		String path = onLineConfigurationUtil.getOnLineParam(UmengOnlineConfig.UMKEY_IFLYTEC_DOWNLOAD_URL);
		
//		DownloadManager downloadManager = new DownloadManager(context.getApplicationContext(), "讯飞+", path);//第二个参数是文件名称 第三个是下载路径
//		downloadManager.downLoad();
	}
}
