package com.iii360.box.adpter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.iii360.box.R;
import com.iii360.box.entity.ViewHolder;
import com.iii360.box.entity.WifiInfoMessage;

/**
 * 盒子助手wifi设置适配器
 * 
 * @author hefeng
 * 
 */
public class WifiConfigListApdater extends BaseAdapter {
    private List<WifiInfoMessage> list;
    private LayoutInflater mInflater;
    private ViewHolder mViewHolder;
    private String mSsid;

    public WifiConfigListApdater(Context context, List<WifiInfoMessage> list) {
        // TODO Auto-generated constructor stub
        mInflater = LayoutInflater.from(context);
        this.list = list;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        if (convertView == null) {

            mViewHolder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.activity_wifi_config_list_item, null);
            mViewHolder.setTv1((TextView) (convertView.findViewById(R.id.wifi_name_tv)));
            mViewHolder.setIv1((ImageView) (convertView.findViewById(R.id.wifi_encrypt_iv)));

            convertView.setTag(mViewHolder);

        } else {

            mViewHolder = (ViewHolder) convertView.getTag();

        }

        mSsid = list.get(position).getSsid();
        
        mViewHolder.getTv1().setText(mSsid);

        if (list.get(position).isEncryption()) {

            mViewHolder.getIv1().setImageResource(R.drawable.ba_hava_wifi_pwd);

        } else {
            mViewHolder.getIv1().setImageResource(R.drawable.ba_no_wifi_pwd);

        }

        return convertView;
    }
}
