package com.iii360.box.adpter;

import java.util.ArrayList;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

public class MusicPagerAdapter extends PagerAdapter {
	private ArrayList<View> views;

	public MusicPagerAdapter(ArrayList<View> views) {
		if (views != null)
			this.views = views;
		else
			this.views = new ArrayList<View>();
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return views.size();
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		// 根据位置 从集合中获取将被移除的item对象
		View v = views.get(position);
		// 将该item从pager中移除
		container.removeView(v);
	}

	@Override
	public void finishUpdate(ViewGroup container) {
		// TODO Auto-generated method stub
		super.finishUpdate(container);
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		// 根据position从集合中获取将被添加到容器的item
		View v = views.get(position);
		// 将该item添加到容器
		container.addView(v);
		return v;
	}

	@Override
	public void startUpdate(ViewGroup container) {
		// TODO Auto-generated method stub
		super.startUpdate(container);
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		// TODO Auto-generated method stub
		return arg0 == arg1;
	}

}
