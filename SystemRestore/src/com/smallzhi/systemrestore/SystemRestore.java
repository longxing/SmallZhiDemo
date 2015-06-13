package com.smallzhi.systemrestore;

import com.iii360.base.common.utl.LogManager;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class SystemRestore extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LogManager.d("view launch");
		this.startService(new Intent(this, RestoreService.class));
	}
	
	
}
