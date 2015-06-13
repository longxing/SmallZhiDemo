package com.iii360.box.view;

import android.content.Context;

public abstract class AbsPopupView {
    private Context context;

    public AbsPopupView(Context context) {
        // TODO Auto-generated constructor stub
        this.context = context;
    }

    public abstract void show();

    public abstract void dismiss();

    public abstract boolean isShowing();
}
