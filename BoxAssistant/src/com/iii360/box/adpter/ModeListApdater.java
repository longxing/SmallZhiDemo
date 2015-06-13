package com.iii360.box.adpter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.iii360.box.MainModeDetailActivity;
import com.iii360.box.R;
import com.iii360.box.entity.ViewHolder;
import com.iii360.box.util.KeyList;

/**
 * 
 * @author hefeng
 * 
 */
public class ModeListApdater extends BaseAdapter {
    private List<String> list;
    private LayoutInflater mInflater;
    private ViewHolder mViewHolder;
    private Context context;
    private ArrayList<Map<Integer, String>> detailList ;

    public ModeListApdater(Context context, List<String> list,ArrayList<Map<Integer, String>> detailList) {
        // TODO Auto-generated constructor stub
        mInflater = LayoutInflater.from(context);
        this.list = list;
        this.context = context;
        this.detailList = detailList;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return this.list.size();
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        if (convertView == null) {

            mViewHolder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.view_control_device_listview_item, null);

            mViewHolder.setTv1((TextView) (convertView.findViewById(R.id.control_device_name_tv)));
            mViewHolder.setTv2((TextView) (convertView.findViewById(R.id.control_switch_tv)));
            mViewHolder.setLayout4((RelativeLayout) (convertView.findViewById(R.id.control_layout)));
            mViewHolder.setView1(convertView.findViewById(R.id.divider_view));
            convertView.setTag(mViewHolder);

        } else {
            mViewHolder = (ViewHolder) convertView.getTag();
        }


        mViewHolder.getTv1().setText(KeyList.GKEY_MODE_ARRAY[position].getValue());
        mViewHolder.getTv2().setText(list.get(position));
        if(position==KeyList.GKEY_MODE_ARRAY.length-1){
        	mViewHolder.getView1().setVisibility(View.GONE);
        }else{
        	mViewHolder.getView1().setVisibility(View.VISIBLE);
        }
        mViewHolder.getLayout4().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent intent = new Intent(context, MainModeDetailActivity.class);
                intent.putExtra(KeyList.IKEY_BOXMODE_ENUM, KeyList.GKEY_MODE_ARRAY[position]);
                intent.putExtra(KeyList.IKEY_BOXMODE_DETAIL_DATA, detailList);
                context.startActivity(intent);
            }
        });

        return convertView;
    }
}
