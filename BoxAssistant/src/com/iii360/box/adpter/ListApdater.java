package com.iii360.box.adpter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.iii360.box.R;
import com.iii360.box.entity.ViewHolder;

/**
 * 
 * @author hefeng
 * 
 */
public class ListApdater extends BaseAdapter {
    private List<String> list;
    private LayoutInflater mInflater;
    private ViewHolder mViewHolder;

    public ListApdater(Context context, List<String> list) {
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
            convertView = mInflater.inflate(R.layout.view_listdialog_listview_item, null);
            mViewHolder.setTv1((TextView) (convertView.findViewById(R.id.title)));

            convertView.setTag(mViewHolder);

        } else {

            mViewHolder = (ViewHolder) convertView.getTag();

        }

        mViewHolder.getTv1().setText(list.get(position));

        return convertView;
    }
}
