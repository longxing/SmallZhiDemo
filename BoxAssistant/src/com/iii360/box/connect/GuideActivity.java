package com.iii360.box.connect;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.iii360.box.R;
import com.iii360.box.adpter.GuideViewFragmentAdapter;
import com.iii360.box.common.BasePreferences;
import com.iii360.box.fragment.ImageViewFragment;
import com.iii360.box.fragment.WithButtonFragment;
import com.iii360.box.util.KeyList;
import com.iii360.box.util.WifiUtils;
import com.iii360.box.view.IView;

/***
 * 
 * @author terry 引导页
 */
public class GuideActivity extends FragmentActivity implements IView, OnPageChangeListener {
	private ViewPager guideViewPager;
	private GuideViewFragmentAdapter guideViewAdapter;
	private ImageView[] guideViewPoints;
	private int currentIndex;
	private BasePreferences preferences;
	private ArrayList<Fragment> fragments;
	private String inner;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_guide);
		preferences = new BasePreferences(this);
		inner = getIntent().getStringExtra("inner");
		boolean haveRecode = preferences.getPrefBoolean(KeyList.PKEY_HAVE_ENTER_RECODE);
		if (haveRecode && inner == null) {
			if (WifiUtils.isConnectWifi(this)) {
				startActivity(new Intent(getApplicationContext(), BootActivity.class));
			} else {
				Intent intent = new Intent(this, UnConnectWifiActivity.class);
				intent.putExtra(KeyList.PKEY_BOOLEAN_APP_START_NO_WIFI, true);
				startActivity(intent);
			}
			finish();
		} else {
			initViews();
			initDatas();
		}
	}

	@Override
	public void initViews() {
		guideViewPager = (ViewPager) findViewById(R.id.guide_view_viewpager);
		LinearLayout guideViewPointLinearlayout = (LinearLayout) findViewById(R.id.guide_view_point_linearlayout);
		guideViewPoints = new ImageView[3];
		for (int i = 0; i < guideViewPoints.length; i++) {
			guideViewPoints[i] = (ImageView) guideViewPointLinearlayout.getChildAt(i);
			guideViewPoints[i].setEnabled(false);
		}
	}

	@Override
	public void initDatas() {

		fragments = new ArrayList<Fragment>();
		ImageViewFragment fragment1 = new ImageViewFragment();
		Bundle bundle = new Bundle();
		bundle.putInt(KeyList.PKEY_GUIDE_VIEW_RESID, 1);
		fragment1.setArguments(bundle);
		fragments.add(fragment1);
		fragment1 = new ImageViewFragment();
		bundle = new Bundle();
		bundle.putInt(KeyList.PKEY_GUIDE_VIEW_RESID, 2);
		fragment1.setArguments(bundle);
		fragments.add(fragment1);
		WithButtonFragment withButton = new WithButtonFragment();
		bundle = new Bundle();
		bundle.putString("inner", inner);
		withButton.setArguments(bundle);
		fragments.add(withButton);
		guideViewAdapter = new GuideViewFragmentAdapter(getSupportFragmentManager(), fragments);
		guideViewPager.setAdapter(guideViewAdapter);
		guideViewPager.setOnPageChangeListener(this);
		guideViewPoints[0].setEnabled(true);
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {

	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {

	}

	@Override
	public void onPageSelected(int arg0) {
		setCurDot(arg0);
	}

	/**
	 * 设置当前的小点的位置
	 */
	private void setCurDot(int positon) {
		if (currentIndex == positon) {
			return;
		}

		guideViewPoints[positon].setEnabled(true);
		guideViewPoints[currentIndex].setEnabled(false);
		currentIndex = positon;
	}
}
