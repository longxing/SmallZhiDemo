package com.iii360.box.adpter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.iii.wifi.dao.info.WifiRemindInfos;
import com.iii.wifi.dao.manager.WifiCRUDForRemind;
import com.iii.wifi.dao.manager.WifiCRUDForRemind.ResultForRemindListener;
import com.iii360.box.R;
import com.iii360.box.base.ConfirmButtonListener;
import com.iii360.box.remind.ExpiredRemind;
import com.iii360.box.remind.RemindDataHelp;
import com.iii360.box.set.SendSetBoxData;
import com.iii360.box.util.BoxManagerUtils;
import com.iii360.box.util.LogManager;
import com.iii360.box.util.ToastUtils;
import com.iii360.box.util.WifiCRUDUtil;
import com.iii360.box.view.MyExitDialog;
import com.voice.common.util.Remind;

public class MainRemindListAdapter extends BaseAdapter {
    private LayoutInflater mInflater;
    private Context context;
    private List<Remind> list;
    private ViewHolder mViewHolder;
    private RemindDataHelp mRemindDataHelp;
    private Button mDeleteBtn;
    private Button mEventBtn;
    private long mBoxTime;
    private Map<Integer, Boolean> mShowMap;
    private boolean mIsShow ;
    private int mShowPosition;

    public MainRemindListAdapter(Context context, List<Remind> list, long mBoxTime) {
        // TODO Auto-generated constructor stub
        this.mInflater = LayoutInflater.from(context);
        this.mRemindDataHelp = new RemindDataHelp();
        this.mShowMap = new HashMap<Integer, Boolean>(list.size());
        this.context = context;
        this.list = list;
        this.mBoxTime = mBoxTime;
        this.initShow();
    }

    private void initShow() {
        for (int i = 0; i < list.size(); i++) {
            mShowMap.put(i, false);
        }
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.activity_main_remind_list_child, null);
            mViewHolder = new ViewHolder();
            mViewHolder.setTime((TextView) convertView.findViewById(R.id.main_remind_time_tv));
            mViewHolder.setEvent((Button) convertView.findViewById(R.id.main_remind_event_btn));
            mViewHolder.setDelete((Button) convertView.findViewById(R.id.main_remind_delete_btn));

            convertView.setTag(mViewHolder);
        } else {
            mViewHolder = (ViewHolder) convertView.getTag();
        }

        Remind remind = list.get(position);
        String time = mRemindDataHelp.getShowTime(mRemindDataHelp.getRemindType(remind), remind);

        mViewHolder.getEvent().setText(remind.needHand);
        mViewHolder.getTime().setBackgroundResource(R.drawable.ba_remind_time_btn_normal);
        mViewHolder.getTime().setText(time);
        mEventBtn = mViewHolder.getEvent();
        mDeleteBtn = mViewHolder.getDelete();

        if (mShowMap.get(position)) {
            mDeleteBtn.setVisibility(View.VISIBLE);
        } else {
            mDeleteBtn.setVisibility(View.GONE);
        }

        if (ExpiredRemind.isRepeatTime(remind) || ExpiredRemind.isExpiredBoxTime(remind.BaseTime, mBoxTime)) {
            mViewHolder.getEvent().setBackgroundResource(R.drawable.main_remind_useful);
        } else {
            mEventBtn.setBackgroundResource(R.drawable.main_remind_useless);
        }

        mEventBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                
                if (mIsShow) {
                    initShow();
                    mIsShow = false ;
                    //显示的不是是当前的
                    if (mShowPosition != position) {
                        mShowMap.put(position, true);
                        mShowPosition = position;
                        mIsShow = true ;
                    }
                } else {
                    mShowMap.put(position, true);
                    mShowPosition = position;
                    mIsShow = true ;
                }
                notifyDataSetChanged();
            }
        });
        mDeleteBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                initShow();
                notifyDataSetChanged();
                showDeleteDialog(position);
            }
        });
        convertView.setFocusable(true);
        return convertView;
    }

    /**
     * 显示删除对话框
     * 
     * @param groupPosition
     * @param childPosition
     */
    private void showDeleteDialog(final int position) {
        final MyExitDialog mMyExitDialog = new MyExitDialog(context, "确定要删除吗？");
        mMyExitDialog.setConfirmListener(new ConfirmButtonListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                mMyExitDialog.dismiss();
                deleteRemind(position);
            }
        });
        mMyExitDialog.show();
    }

    /**
     * 通过ID删除盒子备忘
     * 
     * @param remindId
     */
    private void deleteRemind(final int position) {

        WifiCRUDForRemind wifiCRUDForRemind = new WifiCRUDForRemind(context, BoxManagerUtils.getBoxIP(context), BoxManagerUtils.getBoxTcpPort(context));
        wifiCRUDForRemind.deleteRemind(list.get(position).id, new ResultForRemindListener() {
            @Override
            public void onResult(String type, String errorCode, WifiRemindInfos infos) {
                // TODO Auto-generated method stub
                if (WifiCRUDUtil.isSuccessAll(errorCode)) {
                    
                    closeSetWeather(list.get(position));
                    
                    list.remove(position);
                    new Handler(context.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            // TODO Auto-generated method stub
                            notifyDataSetChanged();
                        }
                    });
                    LogManager.i("删除备忘数据 成功");
                    ToastUtils.cancel();
                    ToastUtils.show(context, R.string.ba_delete_success_toast);

                } else {
                    LogManager.i("删除备忘数据 失败");
                    ToastUtils.show(context, R.string.ba_delete_error_toast);
                }
            }
        });
    }
    
    private void closeSetWeather(Remind remind) {
        if (remind.needHand.equals("播报天气")) {
            SendSetBoxData data = new SendSetBoxData(context);
            data.sendWeatherSwitch("false");
        }
    }


    public class ViewHolder {
        public TextView time;
        public Button event;
        public Button delete;

        public TextView getTime() {
            return time;
        }

        public void setTime(TextView time) {
            this.time = time;
        }

        public Button getEvent() {
            return event;
        }

        public void setEvent(Button event) {
            this.event = event;
        }

        public Button getDelete() {
            return delete;
        }

        public void setDelete(Button delete) {
            this.delete = delete;
        }

    }
}
