package com.iii360.box.music;

import java.util.ArrayList;

import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.iii360.box.R;
import com.iii360.box.base.BaseActivity;
import com.iii360.box.util.BoxManagerUtils;

public class MusicSearchItemAdapter extends BaseAdapter {
	private ArrayList<MusicSearchBean> beans;
	private BaseActivity context;
	private LayoutInflater inflater;
	private OnClickListener inputListener;
	private OnClickListener deleteListener;
	private int type;
	private float screenDensity;
	public ArrayList<MusicSearchBean> getBeans() {
		return beans;
	}
	public int getType() {
		return type;
	}

	public MusicSearchItemAdapter(ArrayList<MusicSearchBean> beans, BaseActivity context, int type) {
		setBeans(beans, type);
		this.context = context;
		inflater = LayoutInflater.from(this.context);
		screenDensity = BoxManagerUtils.getScreenDensity(this.context);
	}

	public void setBeans(ArrayList<MusicSearchBean> beans, int type) {
		this.type = type;
		if (beans == null)
			this.beans = new ArrayList<MusicSearchBean>();
		else
			this.beans = beans;
	}

	@Override
	public int getCount() {
		return beans.size();
	}

	@Override
	public Object getItem(int position) {
		return beans.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View v, ViewGroup parent) {
		ViewHolder vh = null;
		if (v == null) {
			v = inflater.inflate(R.layout.ximalaya_audio_item, null);
			vh = new ViewHolder();
			v.setTag(vh);
			vh.msgTv = (TextView) v.findViewById(R.id.ximalaya_audio_title_tv);
			vh.inputBtn = (ImageView) v.findViewById(R.id.music_play_right_btn);
			vh.belowTv = (TextView) v.findViewById(R.id.ximalaya_audio_name_tv);
		} else {
			vh = (ViewHolder) v.getTag();
		}

		if (inputListener == null) {
			inputListener = new OnClickListener() {
				public void onClick(View v) {
					ViewHolder viewHolder = (ViewHolder) v.getTag();
					String msg = beans.get(viewHolder.position).getMessage();
					((MusicSearchActivity) context).musicSearchEt.setText("" + msg.replaceAll("\\^", " "));

				}
			};
		}
		if (deleteListener == null) {
			deleteListener = new OnClickListener() {
				public void onClick(View v) {
					ViewHolder viewHolder = (ViewHolder) v.getTag();
					MusicSearchBean bean = beans.get(viewHolder.position);
					((MusicSearchActivity) context).musicSearchDao.delete(bean.getId());
					((MusicSearchActivity) context).getHistoryData();

				}
			};
		}
		vh.position = position;
		vh.inputBtn.setTag(vh);
		LinearLayout.LayoutParams params_tv = (android.widget.LinearLayout.LayoutParams) vh.msgTv.getLayoutParams();
		RelativeLayout.LayoutParams params = (android.widget.RelativeLayout.LayoutParams) vh.inputBtn.getLayoutParams();
		if (MusicSearchActivity.ADAPTER_SHOW_TYPE_HISTORY == type) {
			params_tv.topMargin = (int) (screenDensity * 10);
			params_tv.bottomMargin = (int) (screenDensity * 10);
			vh.msgTv.setLayoutParams(params_tv);
			params.rightMargin = (int) (screenDensity * 5);
			int padding = (int) (screenDensity * 10);
			vh.inputBtn.setPadding(padding, padding, padding, padding);
			vh.inputBtn.setLayoutParams(params);
			vh.inputBtn.setImageResource(R.drawable.music_search_delete);
			vh.belowTv.setVisibility(View.GONE);
			vh.inputBtn.setOnClickListener(deleteListener);
			MusicSearchBean bean = beans.get(position);
			if (position == beans.size() - 1) {
				vh.msgTv.setText("" + bean.getMessage());
				vh.inputBtn.setVisibility(View.GONE);
			} else {
				vh.inputBtn.setVisibility(View.VISIBLE);
				vh.msgTv.setText("" + bean.getMessage().replaceAll("\\^", " "));
			}

		} else if (MusicSearchActivity.ADAPTER_SHOW_TYPE_LOAD == type) {
			vh.inputBtn.setVisibility(View.VISIBLE);
			params_tv.topMargin = (int) (screenDensity* 10);
			params_tv.bottomMargin = (int) (screenDensity* 10);
			vh.msgTv.setLayoutParams(params_tv);
			params.rightMargin = (int) (screenDensity * 5);
			int padding = (int) (screenDensity* 10);
			vh.inputBtn.setPadding(padding, padding, padding, padding);
			vh.inputBtn.setLayoutParams(params);
			vh.belowTv.setVisibility(View.GONE);
			vh.inputBtn.setOnClickListener(inputListener);
			vh.inputBtn.setImageResource(R.drawable.music_search_input_btn);
			MusicSearchBean bean = beans.get(position);
			vh.msgTv.setText("" + bean.getMessage().replaceAll("\\^", " "));
		} else if (MusicSearchActivity.ADAPTER_SHOW_TYPE_RESULT == type) {
			vh.inputBtn.setVisibility(View.INVISIBLE);
			params_tv.topMargin = (int) (screenDensity * 5);
			params_tv.bottomMargin = (int) (screenDensity * 5);
			vh.msgTv.setLayoutParams(params_tv);
			params.rightMargin = (int) (screenDensity * 12);
			vh.inputBtn.setPadding(0, 0, 0, 0);
			vh.inputBtn.setLayoutParams(params);
			vh.inputBtn.setOnClickListener(null);
			vh.inputBtn.setImageResource(R.drawable.music_more);
			vh.belowTv.setVisibility(View.VISIBLE);
			MusicSearchBean bean = beans.get(position);
			String[] arr = bean.getMessage().split("\\^");
			String singer = arr[0];
			vh.belowTv.setText(singer);
			vh.msgTv.setText(arr[1]);
		}
		return v;
	}

	private class ViewHolder {
		TextView msgTv;
		ImageView inputBtn;
		TextView belowTv;
		int position;
	}
}
