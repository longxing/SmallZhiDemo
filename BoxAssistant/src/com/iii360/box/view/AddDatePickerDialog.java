package com.iii360.box.view;

import android.app.DatePickerDialog;
import android.content.Context;
import android.widget.DatePicker;

public class AddDatePickerDialog extends DatePickerDialog {

    public AddDatePickerDialog(Context context, int theme, OnDateSetListener callBack, int year, int monthOfYear, int dayOfMonth) {
        super(context, theme, callBack, year, monthOfYear, dayOfMonth);
        // TODO Auto-generated constructor stub
    }

    public AddDatePickerDialog(Context context, OnDateSetListener callBack, int year, int monthOfYear, int dayOfMonth) {
        super(context, callBack, year, monthOfYear, dayOfMonth);
        // TODO Auto-generated constructor stub
    }

    @Override
    public void onDateChanged(DatePicker view, int year, int month, int day) {
        // TODO Auto-generated method stub
        if (minYear != 0 && year < minYear) {
            year = maxYear;
            month = 0;
            day = 1;
        }
        if (maxYear != 0 && year > maxYear) {
            year = minYear;
            month = 0;
            day = 1;
        }
        super.onDateChanged(view, year, month, day);
    }

    private int maxYear;
    private int minYear;

    public void setMaxYear(int maxYear) {
        this.maxYear = maxYear;
    }

    public void setMinYear(int minYear) {
        this.minYear = minYear;
    }
}
