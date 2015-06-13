package com.iii360.box.adpter;

import java.util.ArrayList;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

public class GuideViewAdapter extends PagerAdapter {
	private ArrayList<View> views;

	public GuideViewAdapter(ArrayList<View> views) {
		if (views != null)
			this.views = views;
		else
			this.views = new ArrayList<View>();
	}

	@Override
	public int getCount() {
		return views.size();
	}
	
	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		return arg0 == arg1;
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		View v = views.get(position);
		// 将该item添加到容器
		container.addView(v);
		return v;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		View v = views.get(position);
		// 将该item从pager中移除
		container.removeView(v);
	}
}
