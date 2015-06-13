package com.voice.upgrade.receiver;

import java.io.IOException;
import java.io.InputStream;

import com.iii360.base.common.utl.LogManager;
import com.iii360.sup.common.utl.ShellUtils;
import com.iii360.sup.common.utl.file.FileUtil;
import com.voice.upgrade.service.UpgradeService;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootBroadcastReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(final Context context, Intent arg1) {
		LogManager.d("service launch");
		context.startService(new Intent(context, UpgradeService.class));
		// 低版本清除日志
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				try {
					String result = "";
					// 清除低版本日志
					copyModels(context);
					result = ShellUtils.execute(false, "su", "-c", "rm", "-fr", "/mnt/sdcard/orvibo");
					result = ShellUtils.execute(false, "su", "-c", "rm", "-f", "/mnt/sdcard/voice360log.txt");
				} catch (IOException e) {
					e.printStackTrace();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

	private void copyModels(Context context) {
		// 强制拷贝modules
		InputStream hotTextIn = null;
		try {
			hotTextIn = context.getAssets().open("HotText2");
			FileUtil.writeFile(hotTextIn, "/mnt/sdcard/VoiceAssistant/models/", "HotText2", true);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (hotTextIn != null) {
					hotTextIn.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}
}
