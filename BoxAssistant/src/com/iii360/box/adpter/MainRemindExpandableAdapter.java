package com.iii360.box.adpter;

import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.iii360.box.R;
import com.iii360.box.base.ConfirmButtonListener;
import com.iii360.box.entity.MainRemindViewHolder;
import com.iii360.box.remind.ExpiredRemind;
import com.iii360.box.remind.RemindDataHelp;
import com.iii360.box.remind.UpdateRemind;
import com.iii360.box.util.KeyList;
import com.iii360.box.util.LogManager;
import com.iii360.box.view.MyExitDialog;
import com.voice.common.util.Remind;

public class MainRemindExpandableAdapter extends BaseExpandableListAdapter {
    private LayoutInflater mInflater;
    private Context context;
    private Map<String, List<Remind>> mDataList;
    private MainRemindViewHolder mViewHolder;
    private RemindDataHelp mRemindDataHelp;
    private UpdateRemind mUpdateRemind;

    private Button mLastDeleteBtn;
    private Button mDeleteBtn;
    private float mDownX;
    private float mMoveX;

    public MainRemindExpandableAdapter(Context context, Map<String, List<Remind>> mDataList) {
        // TODO Auto-generated constructor stub
        this.mInflater = LayoutInflater.from(context);
        this.mRemindDataHelp = new RemindDataHelp();
        this.mDataList = mDataList;
        this.context = context;
    }

    @Override
    public int getGroupCount() {
        // TODO Auto-generated method stub
        return mDataList.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        // TODO Auto-generated method stub
        return mDataList.get("" + groupPosition).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        // TODO Auto-generated method stub
        return mDataList.get("" + groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        // TODO Auto-generated method stub
        return mDataList.get("" + groupPosition).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        // TODO Auto-generated method stub
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        // TODO Auto-generated method stub
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        // TODO Auto-generated method stub
        return true;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        // TODO Auto-generated method stub
        return true;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        if (convertView == null) {
            mViewHolder = new MainRemindViewHolder();
            convertView = mInflater.inflate(R.layout.view_main_remind_time_line, null);
            mViewHolder.setCenterIv((ImageView) convertView.findViewById(R.id.remind_top_center_iv));
            mViewHolder.setTimeLineTv((TextView) convertView.findViewById(R.id.main_remind_time_line_tv));
            convertView.setTag(mViewHolder);

        } else {
            mViewHolder = (MainRemindViewHolder) convertView.getTag();
        }

        if (groupPosition == 0) {
            mViewHolder.getCenterIv().setImageResource(R.drawable.main_remind_top_center);
        } else {
            mViewHolder.getCenterIv().setImageResource(R.drawable.main_remind_top_small_center);
        }
        List<Remind> list = mDataList.get("" + groupPosition);
        String time = "" + list.get(0).creatTime;
        mViewHolder.getTimeLineTv().setText(mRemindDataHelp.getYearAndMonth(Long.parseLong(time)));
        convertView.setClickable(true);
        return convertView;
    }

    @Override
    public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        if (convertView == null) {
            mViewHolder = new MainRemindViewHolder();
            convertView = mInflater.inflate(R.layout.activity_main_remind_list_child, null);
            mViewHolder.setParentLayout((LinearLayout) convertView.findViewById(R.id.parent_layout));
            mViewHolder.setRemindEventBtn((Button) convertView.findViewById(R.id.main_remind_event_btn));
            mViewHolder.setRemindTimeTv((TextView) convertView.findViewById(R.id.main_remind_time_tv));
            mViewHolder.setRemindDeleteBtn((Button) convertView.findViewById(R.id.main_remind_delete_btn));

            convertView.setTag(mViewHolder);
        } else {
            mViewHolder = (MainRemindViewHolder) convertView.getTag();
        }
        convertView.setClickable(true);

        Remind remind = mDataList.get("" + groupPosition).get(childPosition);
        mViewHolder.getRemindEventBtn().setText(remind.needHand);

        String time = mRemindDataHelp.getShowTime(mRemindDataHelp.getRemindType(remind), remind);
        mViewHolder.getRemindTimeTv().setText(time);

        Button parentRight = mViewHolder.getRemindEventBtn();

//        LogManager.i("set alarm icon : "+ExpiredRemind.isRepeatTime(remind)+"||"+ExpiredRemind.isExpiredTime(remind.BaseTime)) ;
        if (ExpiredRemind.isRepeatTime(remind) || ExpiredRemind.isExpiredTime(remind.BaseTime)) {
            parentRight.setBackgroundResource(R.drawable.main_remind_useful);
        }else{
            parentRight.setBackgroundResource(R.drawable.main_remind_useless);
        }
        
        mDeleteBtn = mViewHolder.getRemindDeleteBtn();

        showDeleteBtn(parentRight, mDeleteBtn);
        mDeleteBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                mDeleteBtn.setVisibility(View.GONE);
                showDeleteDialog("" + groupPosition, "" + childPosition);
            }
        });
        mViewHolder.getRemindTimeTv().setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                LogManager.e("RemindTime  click================");
                closeDeleteBtn();
                Remind remind = mDataList.get("" + groupPosition).get(childPosition);
                int type = mRemindDataHelp.getRemindType(remind);
                mUpdateRemind = new UpdateRemind(remind, context);

                if (type != RemindDataHelp.REMIND_TYPE_ONCE) {
                    mUpdateRemind.createTimeDialog(UpdateRemind.REMIND_TYPE_TIME, remind.repeatFlag);
                } else {
                    mUpdateRemind.createTimeDialog(UpdateRemind.REMIND_TYPE_DATE_TIME, remind.repeatFlag);
                }
            }
        });

        mViewHolder.getParentLayout().setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                LogManager.e("ParentLayout item click================");
                closeDeleteBtn();
            }
        });
        parentRight.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                LogManager.e("parentRight item click================");
                closeDeleteBtn();
            }
        });

        return convertView;
    }

    /**
     * 显示删除对话框
     * 
     * @param groupPosition
     * @param childPosition
     */
    private void showDeleteDialog(final String groupPosition, final String childPosition) {
        final MyExitDialog mMyExitDialog = new MyExitDialog(context, "确定要删除吗？");
        mMyExitDialog.setConfirmListener(new ConfirmButtonListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                mMyExitDialog.dismiss();
                Intent intent = new Intent(KeyList.AKEY_UPDATE_EXPANDABLE_LIST);
                intent.putExtra(KeyList.IKEY_ELIST_GROUP_POSITION, "" + groupPosition);
                intent.putExtra(KeyList.IKEY_ELIST_CHILD_POSITION, "" + childPosition);
                context.sendBroadcast(intent);
            }
        });
        mMyExitDialog.show();
    }

    /**
     * 显示删除按钮
     * 
     * @param rightEventBtn
     * @param rightDeleteBtn
     */
    private void showDeleteBtn(final Button rightEventBtn, final Button rightDeleteBtn) {
        rightEventBtn.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // TODO Auto-generated method stub
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    mDownX = event.getX();
                    rightDeleteBtn.setVisibility(View.GONE);
                    rightEventBtn.setClickable(true);

                } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
                    mMoveX = event.getX();

                    if (mDownX - mMoveX >= 20) {
                        closeDeleteBtn();
                        LogManager.e("hefeng", "向左滑动,显示......." + rightDeleteBtn.hashCode());
                        rightEventBtn.setClickable(false);
                        rightDeleteBtn.setVisibility(View.VISIBLE);
                        mLastDeleteBtn = rightDeleteBtn;

                        return true;

                    } else if (mMoveX - mDownX >= 20) {
                        LogManager.e("hefeng", "向右滑动,不显示.......");
                        rightDeleteBtn.setVisibility(View.GONE);
                        return true;
                    }

                }
                return false;
            }
        });
    }

    /**
     * 关闭删除按钮
     */
    public void closeDeleteBtn() {
        if (mLastDeleteBtn != null && (mLastDeleteBtn.getVisibility() == View.VISIBLE)) {
            mLastDeleteBtn.setVisibility(View.GONE);
            mLastDeleteBtn = null;
        }
    }
}
