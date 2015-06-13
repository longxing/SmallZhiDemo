package com.iii360.box.adpter;

import java.util.ArrayList;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class GuideViewFragmentAdapter extends FragmentPagerAdapter {
	private ArrayList<Fragment> list;

	public GuideViewFragmentAdapter(FragmentManager fm, ArrayList<Fragment> list) {
		super(fm);
		setList(list);
	}

	public void setList(ArrayList<Fragment> list) {
		if (list == null) {
			this.list = new ArrayList<Fragment>();
		} else
			this.list = list;
	}

	@Override
	public Fragment getItem(int index) {
		return list.get(index);
	}

	@Override
	public int getCount() {
		return list.size();
	}

}
