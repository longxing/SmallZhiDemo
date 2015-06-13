package com.iii360.box.view;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.iii360.box.R;
import com.iii360.box.base.BaseDialog;

/**
 * 退出类型的Dialog
 * 
 * @author hefeng
 * 
 */
public class MyExitDialog extends BaseDialog implements android.view.View.OnClickListener {
    private TextView mTitleTv;
    private Button mCancelBtn;
    private Button mConfirmBtn;

    private String mTitle;

    public MyExitDialog(Context context) {
        super(context, R.style.MyDialog);
        // TODO Auto-generated constructor stub
    }

    public MyExitDialog(Context context, String title) {
        super(context, R.style.MyDialog);
        this.mTitle = title;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_exit_dialog);

        this.mTitleTv = (TextView) findViewById(R.id.exit_dialog_title_tv);
        this.mCancelBtn = (Button) findViewById(R.id.ba_cancel_btn);
        this.mConfirmBtn = (Button) findViewById(R.id.ba_confirm_btn);

        this.mTitleTv.setText(mTitle);

        this.mCancelBtn.setOnClickListener(this);
        this.mConfirmBtn.setOnClickListener(this);
    }

    public void setTitle(String title) {
        this.mTitle = title;
    }

    @Override
    public void show() {
        // TODO Auto-generated method stub
        super.show();
        this.mTitleTv.setText(mTitle);
    }

    public void setEditText(String content) {

    }

    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        super.onBackPressed();
        this.dismiss();
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        if (v == this.mCancelBtn) {

            dismiss();

            setCancelClick(v);

        } else if (v == this.mConfirmBtn) {

            setConfirmClick(v);
        }
    }
}
