package com.voice.common.util;

//ID20120924001 liuwen begin
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.view.View;

import com.iii360.base.common.utl.LogManager;
import com.voice.assistant.main.R;

public class ScreenShot {
	private static Bitmap takeScreenShot(Context context) {
		Activity activity = (Activity) context;
		View view = activity.getWindow().getDecorView();
		view.setDrawingCacheEnabled(true);
		view.buildDrawingCache();
		Bitmap b1 = view.getDrawingCache();
		Rect frame = new Rect();
		activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
		int statusBarHeight = frame.top;
		int width = activity.getWindowManager().getDefaultDisplay().getWidth();
		int height = activity.getWindowManager().getDefaultDisplay().getHeight();
		Bitmap b = null;
		if (b1 != null) {
			b = Bitmap.createBitmap(b1, 0, statusBarHeight, width, height - statusBarHeight);
		}
		view.destroyDrawingCache();
		return b;
	}

	private static boolean savePic(Bitmap b, String strFileName) {

		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(strFileName);
			if (null != fos && b != null) {
				b.compress(Bitmap.CompressFormat.PNG, 95, fos);
				fos.flush();
				fos.close();
			} else {
				return false;
			}
			if (b != null && !b.isRecycled()) {
				b.recycle();
				return true;
			}
		} catch (FileNotFoundException e) {
			// e.printStackTrace();
			LogManager.printStackTrace(e);
		} catch (IOException e) {
			// e.printStackTrace();
			LogManager.printStackTrace(e);
		}
		return false;
	}

	public static boolean shoot(Context context) {
		return ScreenShot.savePic(ScreenShot.takeScreenShot(context), context.getString(R.string.ass_screenshotpath));

	}
	// ID20120924001 liuwen end
}