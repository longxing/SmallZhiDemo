package com.iii360.box.base;

import android.view.View;


public interface IButtonListener {
    
    public void setCancelListener(CancelButtonListener cancelListener);

    public void setConfirmListener(ConfirmButtonListener confirmListener);

    public void setCancelClick(View v);

    public void setConfirmClick(View v);
}
