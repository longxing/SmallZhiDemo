package com.smallzhi.TTS.Engine;

import java.util.Iterator;
import java.util.List;

import android.content.Context;
import android.content.pm.ApplicationInfo;

import com.smallzhi.TTS.Auxiliary.PacageNotFoundException;

/**
 * 目前只是用讯飞的离线TTS播报
 * 
 * @author Peter
 * @data 2015年4月14日下午6:39:46
 */
public class TTSPlayerFactory {
	public static ITTSPlayer TTS_XUNFEI = null; // need check APK has installed
	public static ITTSPlayer TTS_XUNFEI_NET = null;

	private static final String XUNFEI_PACAGENAME = "com.iflytek.speechcloud";

	public static final int TYPE_XUNFEI = 1001;
	public static final int TYPE_XUNFEINET = 1002;

	private static Context mContext = null;

	public static void init(Context context) {
		mContext = context;
	}

	public static ITTSPlayer creatPlayer(int type) throws PacageNotFoundException {
		switch (type) {
		case TYPE_XUNFEI:
			if (TTS_XUNFEI == null) {
				if (hasInstall(XUNFEI_PACAGENAME)) {
					TTS_XUNFEI = new TTSXunFeiLocal(mContext);

				} else {
					throw new PacageNotFoundException(XUNFEI_PACAGENAME);
				}
			}
			return TTS_XUNFEI;
		case TYPE_XUNFEINET:
			if (TTS_XUNFEI_NET == null) {
				if (hasInstall(XUNFEI_PACAGENAME)) {
					TTS_XUNFEI_NET = new TTSXunFeiNet(mContext);

				} else {
					throw new PacageNotFoundException(XUNFEI_PACAGENAME);
				}
			}
			return TTS_XUNFEI_NET;
		default:
			if (TTS_XUNFEI == null) {
				if (hasInstall(XUNFEI_PACAGENAME)) {
					TTS_XUNFEI = new TTSXunFeiLocal(mContext);

				} else {
					throw new PacageNotFoundException(XUNFEI_PACAGENAME);
				}
			}
			return TTS_XUNFEI;
		}

	}

	private static boolean hasInstall(String packName) {
		List<ApplicationInfo> packages = mContext.getPackageManager().getInstalledApplications(0);
		Iterator<ApplicationInfo> iter = packages.iterator();
		while (iter.hasNext()) {
			ApplicationInfo app = (ApplicationInfo) iter.next();
			String pkg = app.packageName;

			if (pkg.equals(packName)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 用这种方式来释放 tts ,避免重复bindservice 导致service 出错.
	 * 
	 * @param type
	 * @data Jul 8, 2013 1:22:53 PM
	 * @Edit jushang ...
	 */
	public static void release(int type) {
		switch (type) {

		case TYPE_XUNFEI:
			if (TTS_XUNFEI != null) {
				TTS_XUNFEI.release();
				TTS_XUNFEI = null;
			}
			break;
		case TYPE_XUNFEINET:
			if (TTS_XUNFEI_NET != null) {
				TTS_XUNFEI_NET.release();
				TTS_XUNFEI_NET = null;
			}
			break;
		default:
			break;
		}
	}

}
