package com.iii360.box.adpter;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.iii.wifi.dao.info.WifiRoomInfo;
import com.iii360.box.R;
import com.iii360.box.common.BasePreferences;
import com.iii360.box.config.DetailRoomActivity;
import com.iii360.box.entity.ViewHolder;
import com.iii360.box.util.KeyList;
import com.iii360.box.util.LogManager;

/**
 * 
 * @author hefeng
 * 
 */
public class MainListApdater extends BaseAdapter {
    private int[] id = { R.drawable.ba_room_color_bg02, R.drawable.ba_room_color_bg03, R.drawable.ba_room_color_bg04, R.drawable.ba_room_color_bg05,
            R.drawable.ba_room_color_bg06, R.drawable.ba_room_color_bg07 };

    private List<WifiRoomInfo> list;
    private LayoutInflater mInflater;
    private ViewHolder mViewHolder;
    private int mSize;
    private int mCount;
    private Context context;
    private BasePreferences mBasePreferences;

    public MainListApdater(Context context, List<WifiRoomInfo> list) {
        // TODO Auto-generated constructor stub
        this.mInflater = LayoutInflater.from(context);
        this.mBasePreferences = new BasePreferences(context) ;
        this.context = context;
        this.list = list;
        this.mSize = this.list.size();
        this.mCount = mSize / 2 + mSize % 2;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        if (list == null) {
            return 0;
        } else {
            return mCount;
        }
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
            convertView = mInflater.inflate(R.layout.view_main_listview_item, null);
            mViewHolder.setTv1((TextView) (convertView.findViewById(R.id.main_small_tv01)));
            mViewHolder.setTv2((TextView) (convertView.findViewById(R.id.main_small_tv02)));
            convertView.setTag(mViewHolder);

        } else {
            mViewHolder = (ViewHolder) convertView.getTag();
        }

//        LogManager.d("基数 mSize=" + mSize + "||po=" + position + "||t=" + mCount);
        if (mSize % 2 != 0 && position == mCount - 1) {
//            LogManager.i("基数，最后一个");
//            LogManager.i(mCount + "第" + position + "行，p3=" + mCount);
            if (mSize == 1) {
                mViewHolder.getTv1().setText(list.get(0).getRoomName());
                mViewHolder.getTv1().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub
                        LogManager.i("click room : " + list.get(0).getRoomId() + list.get(0).getRoomName());
                        startToDetail(list.get(0).getRoomId(),list.get(0).getRoomName());
                    }
                });
            } else {
                mViewHolder.getTv1().setText(list.get(mCount).getRoomName());
                mViewHolder.getTv1().setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub
                        LogManager.i("click room : " + list.get(mCount).getRoomId() + list.get(mCount).getRoomName());
                        startToDetail(list.get(mCount).getRoomId(),list.get(mCount).getRoomName());
                    }
                });

            }
            mViewHolder.getTv1().setBackgroundResource(getImageID(position));
            mViewHolder.getTv2().setVisibility(View.INVISIBLE);
           
        } else {

            mViewHolder.getTv1().setText(list.get(position * 2).getRoomName());
            mViewHolder.getTv1().setBackgroundResource(getImageID(position + 5));

            mViewHolder.getTv2().setText(list.get(position * 2 + 1).getRoomName());
            mViewHolder.getTv2().setBackgroundResource(getImageID(position + 3));

//            LogManager.i(mCount + "第" + position + "行，p1=" + (position + position) + "||p2=" + (position + position + 1));

            mViewHolder.getTv1().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub

                    LogManager.i("click room : " + list.get(position * 2).getRoomId() + list.get(position * 2).getRoomName());
                    startToDetail(list.get(position * 2).getRoomId(), list.get(position * 2).getRoomName());

                }
            });
            mViewHolder.getTv2().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    LogManager.i("click room : " + list.get(position * 2 + 1).getRoomId() + list.get(position * 2 + 1).getRoomName());
                    startToDetail(list.get(position * 2 + 1).getRoomId(), list.get(position * 2 + 1).getRoomName());

                }
            });
        }

        return convertView;
    }

    private void startToDetail(String roomId, String roomName) {
        
        mBasePreferences.setPrefString(KeyList.PKEY_ROOM_NAME, roomName);
        
        Intent intent = new Intent(context, DetailRoomActivity.class);
        intent.putExtra(KeyList.IKEY_ROOM_ID, roomId);
        intent.putExtra(KeyList.IKEY_ROOM_NAME, roomName);
        context.startActivity(intent);
    }

    private int mId = 1;

    private int getImageID(int position) {
        if (position < id.length) {

            return id[position];

        } else {

            mId++;
            if (mId >= id.length) {
                mId = 0;
            }
            return id[mId];
        }
    }
}
