package com.iii360.box.adpter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.iii.client.WifiConfig;
import com.iii360.box.MyApplication;
import com.iii360.box.R;
import com.iii360.box.common.BasePreferences;
import com.iii360.box.util.KeyList;
import com.iii360.box.util.LogUtil;
import com.iii360.box.util.WifiCRUDUtil;

public class OnLineBoxListAdapter extends BaseAdapter {
	private ArrayList<String> list;
	private Context context;
	private LayoutInflater inflater;

	private ConnectionListener listener;
	private BasePreferences preferences;

	public OnLineBoxListAdapter(ArrayList<String> list, Context context) {
		setList(list);
		this.context = context;
		this.inflater = LayoutInflater.from(this.context);
		preferences = new BasePreferences(context);
	}

	public void setList(ArrayList<String> list) {
		if (list == null) {
			this.list = new ArrayList<String>();
		} else {
			this.list = list;
		}
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int arg0) {
		return list.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(int position, View v, ViewGroup viewGroup) {
		ViewHolder vh = null;
		if (v == null) {
			v = inflater.inflate(R.layout.online_box_listitem, null);
			vh = new ViewHolder();
			vh.boxIpTv = (TextView) v.findViewById(R.id.box_ip_tv);
			vh.boxNameTv = (TextView) v.findViewById(R.id.box_name_tv);
			vh.connectBoxBtn = (ImageView) v.findViewById(R.id.box_connect_btn);
			vh.PlayTTSBtn = (RelativeLayout) v.findViewById(R.id.box_play_btn);
			v.setTag(vh);
		} else {
			vh = (ViewHolder) v.getTag();
		}
		String ip = preferences.getPrefString(KeyList.GKEY_BOX_IP_ADDRESS);
		if (getItem(position).equals(ip)) {
			vh.connectBoxBtn.setVisibility(View.VISIBLE);
		} else {
			vh.connectBoxBtn.setVisibility(View.GONE);
		}
		if (listener == null)
			listener = new ConnectionListener();
		vh.position = position;
		vh.boxIpTv.setText(getItem(position) + "");

		vh.boxNameTv.setText("音箱");
		if (MyApplication.getSerialNums().containsKey(getItem(position))) {
			String num = MyApplication.getSerialNums().get(getItem(position));
			if (num != null && !"".equals(num.trim()))
				vh.boxNameTv.setText("音箱 (" + num.trim() + ")");
		}
		vh.PlayTTSBtn.setTag(vh);
		vh.PlayTTSBtn.setOnClickListener(listener);
		return v;
	}

	private class ViewHolder {
		TextView boxIpTv;
		TextView boxNameTv;
		ImageView connectBoxBtn;
		RelativeLayout PlayTTSBtn;
		int position;
	}

	private class ConnectionListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			ViewHolder vh = (ViewHolder) v.getTag();
			int position = vh.position;
			say(position);
			// String ClickBoxIp = list.get(position);
			// if (!MyApplication.getBoxAdds().containsKey(ClickBoxIp)) {
			// ToastUtils.show(context, "该音箱不在网段内，请刷新后重试");
			// return;
			// }
			// Intent intent = new Intent(context, ConnectActivity.class);
			// intent.putExtra("BoxIp", ClickBoxIp);
			// ((PreferenceActivity) context).setPrefString(
			// KeyList.GKEY_BOX_IP_ADDRESS, ClickBoxIp);
			// context.startActivity(intent);
			// ((Activity) context).finish();
		}

	}

	protected void say(int position) {
		final String ip = list.get(position);
		// WifiForCommonOprite oprite = new
		// WifiForCommonOprite(BoxManagerUtils.getBoxTcpPort(context), ip);
		// LogUtil.e("tts播报："+ip);
		// oprite.playTTS("我在这里", new ResultForWeatherTimeListener() {
		//
		// @Override
		// public void onResult(String type, String errorCode, String result) {
		// if (!WifiCRUDUtil.isSuccessAll(errorCode)) {
		// LogUtil.e("tts播报：新端口失败"+ip);
		// new Handler(Looper.getMainLooper()).post(new Runnable() {
		//
		// @Override
		// public void run() {
		retry(ip);
		// }
		// });
		//
		// }else{
		// LogUtil.e("tts播报：新端口成功"+ip);
		// }
		// }
		// });
	}

	protected void retry(String ip) {
		LogUtil.e("tts播报：尝试旧端口" + ip);
		WifiCRUDUtil.playTTS(ip, WifiConfig.TCP_DEFAULT_PORT, context, "我在这里");
	}

}
