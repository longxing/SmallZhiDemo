package com.iii360.box.base;


import android.view.View;

public class ButtonListener implements IButtonListener{
    
    private CancelButtonListener cancelListener;
    private ConfirmButtonListener confirmListener;

    public void setCancelListener(CancelButtonListener cancelListener) {
        this.cancelListener = cancelListener;
    }

    public void setConfirmListener(ConfirmButtonListener confirmListener) {
        this.confirmListener = confirmListener;
    }

    public void setCancelClick(View v) {
        if (cancelListener != null) {
            cancelListener.onClick(v);
        }
    }

    public void setConfirmClick(View v) {
        if (confirmListener != null) {
            confirmListener.onClick(v);
        }
    }
}
