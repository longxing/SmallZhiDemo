package com.iii.wifi.thirdpart.inter;

import android.content.Context;

public class JSTTSUtils {
	public static String tts1 = "发现了1个新配件";
	public static String tts2 = "连接配件成功！请打开小智助手，对配件进行设置";
	public static boolean isPlaying = false;

	public synchronized static void findDeviceTTS(Context context) {
//		LogManager.e("-------start -----isPlaying-----=" + isPlaying);
//		SuperBaseContext sc=new SuperBaseContext(context);
//		long l=sc.getPrefLong("KET_FIND_NEW",0L);
//		if (isPlaying || (System.currentTimeMillis()-l)<=2*1000*60) {
//			return;
//		}
//		sc.setPrefLong("KET_FIND_NEW", System.currentTimeMillis());
//		new Thread(new Runnable() {
//			@Override
//			public void run() {
//				// TODO Auto-generated method stub
//				
//				isPlaying = true;
//				if (KeyList.TTSUtil.isWorking())
//					KeyList.TTSUtil.playContent(tts1);
//				WaitUtil.sleep(4000);
//				if (KeyList.TTSUtil.isWorking())
//					KeyList.TTSUtil.playContent(tts2);
//				long t = System.currentTimeMillis();
//				WaitUtil.sleep(7000);
//				LogManager.e("-------end----------");
//				
//				isPlaying = false;
//			}
//		}).start();
	}
}
