package com.iii360.box.ximalaya;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.iii360.box.R;

public class AudioAdapter extends BaseAdapter {
	private ArrayList<XimalayaAudio> audios;
	private LayoutInflater inflater;
	private Context context;

	public AudioAdapter(Context context, ArrayList<XimalayaAudio> audios) {
		setAudios(audios);
		this.context = context;
		inflater = LayoutInflater.from(this.context);
	}

	public void setAudios(ArrayList<XimalayaAudio> audios) {
		if (audios == null)
			this.audios = new ArrayList<XimalayaAudio>();
		else
			this.audios = audios;
	}

	public ArrayList<XimalayaAudio> getAudios() {
		return audios;
	}

	@Override
	public int getCount() {
		return audios.size();
	}

	@Override
	public XimalayaAudio getItem(int position) {
		return audios.get(position);
	}

	@Override
	public long getItemId(int position) {
		return audios.get(position).getId();
	}

	@Override
	public View getView(int position, View v, ViewGroup parent) {
		ViewHolder vh = null;
		if (v == null) {
			vh = new ViewHolder();
			v = inflater.inflate(R.layout.ximalaya_audio_item, null);
			vh.titleTv = (TextView) v.findViewById(R.id.ximalaya_audio_title_tv);
			vh.nameTv = (TextView) v.findViewById(R.id.ximalaya_audio_name_tv);
			vh.rightIv = (ImageView) v.findViewById(R.id.music_play_right_btn);
			v.setTag(vh);
		} else {
			vh = (ViewHolder) v.getTag();
		}
		vh.rightIv.setVisibility(View.GONE);
		vh.nameTv.setText("" + getItem(position).getNickName());
		vh.titleTv.setText("" + getItem(position).getTitle());
		return v;
	}

	private class ViewHolder {
		TextView titleTv;
		TextView nameTv;
		ImageView rightIv;

	}
}
