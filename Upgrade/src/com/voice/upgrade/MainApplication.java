package com.voice.upgrade;

import android.app.Application;
import android.content.Intent;

import com.iii360.base.common.utl.LogManager;
import com.voice.upgrade.service.UpgradeService;

public class MainApplication extends Application {

	@Override
	public void onCreate() {
		super.onCreate();
		LogManager.d("view launch");
		this.startService(new Intent(this, UpgradeService.class));
	}

}
