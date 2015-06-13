package com.iii360.box.base;

import android.app.Dialog;
import android.content.Context;
import android.view.View;

/**
 * 鍩虹被Dialog
 * 
 * @author hefeng
 * 
 */
public abstract class BaseDialog extends Dialog implements IButtonListener {
    protected Context context;
    protected ButtonListener mButtonListener;

    public BaseDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        // TODO Auto-generated constructor stub
        inits(context);
    }

    public BaseDialog(Context context, int theme) {
        super(context, theme);
        // TODO Auto-generated constructor stub
        inits(context);
    }

    public BaseDialog(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
        inits(context);
    }

    private void inits(Context context) {
        this.context = context;
        mButtonListener = new ButtonListener();
    }

    @Override
    public void setCancelListener(CancelButtonListener cancelListener) {
        // TODO Auto-generated method stub
        this.mButtonListener.setCancelListener(cancelListener);
    }

    @Override
    public void setConfirmListener(ConfirmButtonListener confirmListener) {
        // TODO Auto-generated method stub
        this.mButtonListener.setConfirmListener(confirmListener);
    }

    @Override
    public void setCancelClick(View v) {
        // TODO Auto-generated method stub
        this.mButtonListener.setCancelClick(v);
    }

    @Override
    public void setConfirmClick(View v) {
        // TODO Auto-generated method stub
        this.mButtonListener.setConfirmClick(v);
    }

}
