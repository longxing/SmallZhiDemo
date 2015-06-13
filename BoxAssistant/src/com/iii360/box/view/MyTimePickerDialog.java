package com.iii360.box.view;

import android.app.TimePickerDialog;
import android.content.Context;

public class MyTimePickerDialog extends TimePickerDialog {

    public MyTimePickerDialog(Context context, int theme, OnTimeSetListener callBack, int hourOfDay, int minute, boolean is24HourView) {
        super(context, theme, callBack, hourOfDay, minute, is24HourView);
        // TODO Auto-generated constructor stub
    }

    public MyTimePickerDialog(Context context, OnTimeSetListener callBack, int hourOfDay, int minute, boolean is24HourView) {
        super(context, callBack, hourOfDay, minute, is24HourView);
        // TODO Auto-generated constructor stub
    }

    @Override
    protected void onStop() {
        // TODO Auto-generated method stub
        //super.onStop();
        //点击确定和取消按钮时，会出发onTimeSet；在dialog的onStop（比如dialog dismiss时）中，也调用了onTimeSet方法。
    }
}
