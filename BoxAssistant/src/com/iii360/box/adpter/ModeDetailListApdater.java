package com.iii360.box.adpter;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.iii360.box.R;
import com.iii360.box.entity.ViewHolder;
import com.iii360.box.util.KeyList;
import com.iii360.box.util.LogManager;

/**
 * 
 * @author hefeng
 * 
 */
public class ModeDetailListApdater extends BaseAdapter {
    private SelectListener selectListener;
    private LayoutInflater mInflater;
    private ViewHolder mViewHolder;
    private Context context;
    /**
     * [{1=开/关客厅空调}, {2=开/关客厅电视机}] id为控制的ID
     */
    private ArrayList<Map<Integer, String>> mDetailList;
    private Set<Integer> mSelectList;

    /**
     * {1=开/关客厅空调}
     */
    private Map<Integer, String> mMapList;
    private int i = 0;

    /**
     * @param context
     * @param mDetailList [{1=开/关客厅空调}, {2=开/关客厅电视机}] id为控制的ID
     * @param mSelectList  选中的数据
     */
    public ModeDetailListApdater(Context context, ArrayList<Map<Integer, String>> mDetailList, Set<Integer> mSelectList) {
        // TODO Auto-generated constructor stub
        this.mInflater = LayoutInflater.from(context);
        this.context = context;
        this.mDetailList = mDetailList;
        this.mSelectList = mSelectList;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return this.mDetailList.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return mDetailList.get(position);
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
            convertView = mInflater.inflate(R.layout.view_control_device_listview_3_item, null);

            mViewHolder.setTv1((TextView) (convertView.findViewById(R.id.mode_action_tv1)));
            mViewHolder.setTv2((TextView) (convertView.findViewById(R.id.mode_action_tv2)));

            mViewHolder.setLayout1((LinearLayout) (convertView.findViewById(R.id.mode_layout1)));
            mViewHolder.setLayout2((LinearLayout) (convertView.findViewById(R.id.mode_layout2)));

            mViewHolder.setCb1((CheckBox) (convertView.findViewById(R.id.mode_select_cb1)));
            mViewHolder.setCb2((CheckBox) (convertView.findViewById(R.id.mode_select_cb2)));

            convertView.setTag(mViewHolder);

        } else {
            mViewHolder = (ViewHolder) convertView.getTag();
        }

        setClick(mViewHolder);

        mMapList = mDetailList.get(position);
        //如果列表有1个则作为一项，如果有2个则2个作为一项
        switch (mMapList.size()) {

        case 1:

            mViewHolder.getLayout1().setVisibility(View.VISIBLE);
            mViewHolder.getLayout2().setVisibility(View.GONE);

            i = 0;
            for (Integer key : mMapList.keySet()) {
                if (i == 0) {
                    mViewHolder.getTv1().setTag(key);
                    mViewHolder.getTv1().setText(mMapList.get(key));
                    if (mSelectList.contains(key)) {
                        mViewHolder.getCb1().setChecked(true);
                    } else {
                        mViewHolder.getCb1().setChecked(false);
                    }
                }
            }

            break;
        case 2:
            mViewHolder.getLayout1().setVisibility(View.VISIBLE);
            mViewHolder.getLayout2().setVisibility(View.VISIBLE);

            i = 0;
            for (Integer key : mMapList.keySet()) {
                if (i == 0) {
                    mViewHolder.getTv1().setTag(key);
                    mViewHolder.getTv1().setText(mMapList.get(key));
                    if (mSelectList.contains(key)) {
                        mViewHolder.getCb1().setChecked(true);
                    } else {
                        mViewHolder.getCb1().setChecked(false);
                    }

                } else if (i == 1) {

                    mViewHolder.getTv2().setTag(key);
                    mViewHolder.getTv2().setText(mMapList.get(key));
                    if (mSelectList.contains(key)) {
                        mViewHolder.getCb2().setChecked(true);
                    } else {
                        mViewHolder.getCb2().setChecked(false);
                    }

                }

                i++;
            }

            break;

        default:
            break;
        }

        return convertView;
    }

    private void setClick(final ViewHolder mViewHolder) {
        //第一个选项
        mViewHolder.getLayout1().setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (mViewHolder.getLayout2().getVisibility() == View.VISIBLE) {
                    mViewHolder.getCb2().setChecked(false);
                    mSelectList.remove(mViewHolder.getTv2().getTag());
                }

                if (mViewHolder.getCb1().isChecked()) {
                    mViewHolder.getCb1().setChecked(false);
                    mSelectList.remove(mViewHolder.getTv1().getTag());
                } else {
                    mViewHolder.getCb1().setChecked(true);
                    mSelectList.add(Integer.parseInt(mViewHolder.getTv1().getTag().toString()));
                }

                callBackSelect();
            }
        });
        
        //第二个选项
        mViewHolder.getLayout2().setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                mViewHolder.getCb1().setChecked(false);
                mSelectList.remove(mViewHolder.getTv1().getTag());

                if (mViewHolder.getCb2().isChecked()) {
                    mViewHolder.getCb2().setChecked(false);
                    mSelectList.remove(mViewHolder.getTv2().getTag());
                } else {
                    mViewHolder.getCb2().setChecked(true);
                    try {
                        mSelectList.add(Integer.parseInt(mViewHolder.getTv2().getTag().toString()));
                    } catch (NumberFormatException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }

                callBackSelect();
            }
        });
    }

    /**
     * 回调保存设置的控制ID
     */
    private void callBackSelect() {
        StringBuffer buffer = new StringBuffer();

        if (mSelectList.isEmpty()) {
            if (selectListener != null) {
                selectListener.onResult("");
            }
        } else {

            for (Integer id : mSelectList) {
                buffer.append(id);
                buffer.append(KeyList.SEPARATOR_ACTION);
            }
            String s = buffer.substring(0, buffer.length() - 2);
            LogManager.i("save select id : " + s);
            if (selectListener != null) {
                selectListener.onResult(s);
            }
        }
    }

    public void setSelectListener(SelectListener selectListener) {
        this.selectListener = selectListener;
    }

    public interface SelectListener {
        public void onResult(String result);
    }
}
