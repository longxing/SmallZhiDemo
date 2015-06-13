package com.iii360.box.adpter;

import java.util.Arrays;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.iii360.box.R;

public class UserInfoAdapter extends BaseAdapter {
	private String[] keys;
	private String[] values;
	private LayoutInflater layoutInflater;
	private ListView listView;

	public UserInfoAdapter(String[] keys, String[] values, Context ctx, ListView listView) {
		layoutInflater = LayoutInflater.from(ctx);
		setKeys(keys);
		setValues(values);
		this.listView = listView;
	}

	public String[] getKeys() {
		return keys;
	}

	public void setKeys(String[] keys) {
		if (keys != null)
			this.keys = keys;
		else {
			this.keys = new String[] {};
		}
		setValues(null);
	}

	@Override
	public void notifyDataSetChanged() {
		super.notifyDataSetChanged();
		setListViewHeightBasedOnChildren(listView);
	}

	public void setListViewHeightBasedOnChildren(ListView listView) {
		ListAdapter listAdapter = listView.getAdapter();
		if (listAdapter == null) {
			return;
		}

		int totalHeight = 0;
		for (int i = 0; i < listAdapter.getCount(); i++) {
			View listItem = listAdapter.getView(i, null, listView);
			listItem.measure(0, 0);
			totalHeight += listItem.getMeasuredHeight();
		}

		ViewGroup.LayoutParams params = listView.getLayoutParams();
		params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
		listView.setLayoutParams(params);
	}

	public void setValues(String[] values) {
		if (values != null && values.length != 0)
			this.values = values;
		else {
			if (keys != null) {
				this.values = new String[keys.length];
				Arrays.fill(this.values, "");
			} else {
				this.values = new String[] {};
			}
		}
	}

	public String[] getValues() {
		return values;
	}

	@Override
	public int getCount() {
		return keys.length;
	}

	@Override
	public Object getItem(int position) {
		return keys[position];
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View v, ViewGroup parent) {
		ViewHolder vh = null;
		if (v == null) {
			v = layoutInflater.inflate(R.layout.userinfo_item, null);
			vh = new ViewHolder();
			v.setTag(vh);
			vh.dividerView = v.findViewById(R.id.user_info_divider_view);
			vh.itemKey = (TextView) v.findViewById(R.id.user_info_item_key_tv);
			vh.itemValues = (TextView) v.findViewById(R.id.user_info_item_value_tv);
		} else {
			vh = (ViewHolder) v.getTag();
		}
		vh.itemKey.setText(keys[position]);
		vh.itemValues.setText(values[position]);
		if (position == keys.length - 1) {
			vh.dividerView.setVisibility(View.GONE);
		} else
			vh.dividerView.setVisibility(View.VISIBLE);
		return v;
	}

	class ViewHolder {
		View dividerView;
		TextView itemKey;
		TextView itemValues;
	}
}
