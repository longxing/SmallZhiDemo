package com.iii360.box.entity;

import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainRemindViewHolder {
    private LinearLayout parentLayout;
    private LinearLayout timeLineLayout;
    private TextView timeLineTv;
    private TextView remindTimeTv;
    private TextView remindEventTv;
    private Button remindDeleteBtn;
    private Button remindEventBtn;
    private ImageView centerIv;

    public ImageView getCenterIv() {
        return centerIv;
    }

    public void setCenterIv(ImageView centerIv) {
        this.centerIv = centerIv;
    }

    public Button getRemindEventBtn() {
        return remindEventBtn;
    }

    public void setRemindEventBtn(Button remindEventBtn) {
        this.remindEventBtn = remindEventBtn;
    }

    public Button getRemindDeleteBtn() {
        return remindDeleteBtn;
    }

    public void setRemindDeleteBtn(Button remindDeleteBtn) {
        this.remindDeleteBtn = remindDeleteBtn;
    }

    public LinearLayout getParentLayout() {
        return parentLayout;
    }

    public void setParentLayout(LinearLayout parentLayout) {
        this.parentLayout = parentLayout;
    }

    public LinearLayout getTimeLineLayout() {
        return timeLineLayout;
    }

    public void setTimeLineLayout(LinearLayout timeLineLayout) {
        this.timeLineLayout = timeLineLayout;
    }

    public TextView getTimeLineTv() {
        return timeLineTv;
    }

    public void setTimeLineTv(TextView timeLineTv) {
        this.timeLineTv = timeLineTv;
    }

    public TextView getRemindTimeTv() {
        return remindTimeTv;
    }

    public void setRemindTimeTv(TextView remindTimeTv) {
        this.remindTimeTv = remindTimeTv;
    }

    public TextView getRemindEventTv() {
        return remindEventTv;
    }

    public void setRemindEventTv(TextView remindEventTv) {
        this.remindEventTv = remindEventTv;
    }

}
