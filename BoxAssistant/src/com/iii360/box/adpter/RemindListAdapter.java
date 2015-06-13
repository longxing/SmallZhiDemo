package com.iii360.box.adpter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.iii360.box.R;
import com.iii360.box.remind.ExpiredRemind;
import com.iii360.box.remind.RemindDataHelp;
import com.voice.common.util.Remind;

public class RemindListAdapter extends BaseAdapter {
	private List<Remind> remindList;
	private Context context;
	private long mBoxTime;
	private LayoutInflater inflater;
	private RemindDataHelp mRemindDataHelp;

	public RemindListAdapter(Context context, List<Remind> list, long mBoxTime) {
		this.context = context;
		setRemindList(list, mBoxTime);
		inflater = LayoutInflater.from(context);
		this.mRemindDataHelp = new RemindDataHelp();
	}

	public void setRemindList(List<Remind> remindList, long mBoxTime) {
		if (remindList == null) {
			this.remindList = new ArrayList<Remind>();
			this.mBoxTime = 0;
		} else {
			this.remindList = remindList;
			this.mBoxTime = mBoxTime;
		}

	}

	public void changeData(List<Remind> remindList, long mBoxTime) {
		setRemindList(remindList, mBoxTime);
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return remindList.size();
	}

	@Override
	public Remind getItem(int position) {
		return remindList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	private String formatToDate(long time) {
		Date date = new Date(time);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
		try {
			return sdf.format(date);
		} catch (Exception e) {
		}
		return "";
	}

	@Override
	public int getViewTypeCount() {
		// menu type count
		return 1;
	}

	@Override
	public int getItemViewType(int position) {
		// current menu type
		return 0;
	}

	private String formatToHour(long time) {
		Date date = new Date(time);
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
		try {
			return sdf.format(date);
		} catch (Exception e) {
		}
		return "";
	}

	@Override
	public View getView(int position, View v, ViewGroup parent) {
		ViewHolder vh = null;
		if (v == null) {
			v = inflater.inflate(R.layout.remind_item_list, null);
			vh = new ViewHolder();
			v.setTag(vh);
			vh.msgTv = (TextView) v.findViewById(R.id.remind_msg_tv);
			vh.mainLayout = (RelativeLayout) v.findViewById(R.id.remind_main_layout);
			vh.clockIv = (ImageView) v.findViewById(R.id.remind_clock_iv);
			vh.hourTv = (TextView) v.findViewById(R.id.remind_hour_tv);
			vh.dateTv = (TextView) v.findViewById(R.id.remind_datetime_tv);
		} else {
			vh = (ViewHolder) v.getTag();
		}
		Remind remind = remindList.get(position);
		String time = mRemindDataHelp.getShowTime(mRemindDataHelp.getRemindType(remind), remind);
		vh.msgTv.setText(remind.needHand);
		vh.dateTv.setText(time);
		vh.hourTv.setText(formatToHour(remind.BaseTime));
		if (ExpiredRemind.isRepeatTime(remind) || ExpiredRemind.isExpiredBoxTime(remind.BaseTime, mBoxTime)) {
			vh.clockIv.setImageResource(R.drawable.remind_logo);
			vh.mainLayout.setBackgroundColor(Color.parseColor("#ffffff"));
		} else {
			vh.clockIv.setImageResource(R.drawable.remind_guoqi_logo);
			vh.mainLayout.setBackgroundColor(Color.parseColor("#f7f7f7"));
		}

		return v;
	}

	private class ViewHolder {
		TextView msgTv;
		TextView dateTv;
		TextView hourTv;
		RelativeLayout mainLayout;
		ImageView clockIv;
	}
}
