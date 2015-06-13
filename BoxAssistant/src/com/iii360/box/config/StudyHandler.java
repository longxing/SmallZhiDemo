package com.iii360.box.config;

import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;

import com.iii360.box.R;

public class StudyHandler extends Handler {
    public static final int HANDLER_START_STUDY = 0;
    public static final int HANDLER_STUDYING = 1;
    public static final int HANDLER_STUDY_SUCCESS_CLICK = 2;
    public static final int HANDLER_STUDY_SUCCESS_UNCLICK = 3;
    public static final int HANDLER_STUDY_AFRESH = 4;
    
    public static final int TAG_STUDY_SUCCESS = 10;

    private Button studyBtn;
    private Button deleteBtn;

    public StudyHandler(Button studyBtn, Button deleteBtn) {
        // TODO Auto-generated constructor stub
        this.studyBtn = studyBtn;
        this.deleteBtn = deleteBtn;
    }

    @Override
    public void handleMessage(Message msg) {
        // TODO Auto-generated method stub
        super.handleMessage(msg);

        int what = msg.what;

        switch (what) {

        case HANDLER_START_STUDY:
            //开始学习
            studyBtn.setText(R.string.ba_start_study);
            deleteBtn.setVisibility(View.GONE);
            studyBtn.setClickable(true);

            break;

        case HANDLER_STUDYING:
            //正在学习
            studyBtn.setText(R.string.ba_studying);
            deleteBtn.setVisibility(View.GONE);
            studyBtn.setClickable(false);

            break;

        case HANDLER_STUDY_SUCCESS_CLICK:
            //完成学习,可以点击
            studyBtn.setText(R.string.ba_complete_study);
            deleteBtn.setVisibility(View.GONE);
            studyBtn.setClickable(true);
            studyBtn.setTag(TAG_STUDY_SUCCESS);

            break;
            
        case HANDLER_STUDY_SUCCESS_UNCLICK:
            //完成学习,不可以点击
            studyBtn.setText(R.string.ba_complete_study);
            deleteBtn.setVisibility(View.VISIBLE);
            studyBtn.setClickable(false);
            
            break;

        case HANDLER_STUDY_AFRESH:
            //重新学习            
            studyBtn.setText(R.string.ba_refresh_study);
            deleteBtn.setVisibility(View.GONE);
            studyBtn.setClickable(true);

        default:
            break;
        }
    }
}
