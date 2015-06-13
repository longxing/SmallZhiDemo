package com.iii360.box.entity;

import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ViewHolder {
    private LinearLayout layout1;
    private LinearLayout layout2;
    private LinearLayout layout3;
    private RelativeLayout layout4;
    private TextView tv1;
    private TextView tv2;
    private TextView tv3;

    private CheckBox cb1;
    private CheckBox cb2;
    private CheckBox cb3;

    private ImageView iv1;

    private Button btn1;
    private Button btn2;
    private View view1;
    public LinearLayout getLayout1() {
        return layout1;
    }

    public void setLayout1(LinearLayout layout1) {
        this.layout1 = layout1;
    }

    public LinearLayout getLayout2() {
        return layout2;
    }

    public void setLayout2(LinearLayout layout2) {
        this.layout2 = layout2;
    }

    public LinearLayout getLayout3() {
        return layout3;
    }

    public void setLayout3(LinearLayout layout3) {
        this.layout3 = layout3;
    }

    public TextView getTv1() {
        return tv1;
    }

    public void setTv1(TextView tv1) {
        this.tv1 = tv1;
    }

    public TextView getTv2() {
        return tv2;
    }

    public void setTv2(TextView tv2) {
        this.tv2 = tv2;
    }

    public TextView getTv3() {
        return tv3;
    }

    public void setTv3(TextView tv3) {
        this.tv3 = tv3;
    }

    public ImageView getIv1() {
        return iv1;
    }

    public void setIv1(ImageView iv1) {
        this.iv1 = iv1;
    }

    public CheckBox getCb1() {
        return cb1;
    }

    public void setCb1(CheckBox cb1) {
        this.cb1 = cb1;
    }

    public CheckBox getCb2() {
        return cb2;
    }

    public void setCb2(CheckBox cb2) {
        this.cb2 = cb2;
    }

    public CheckBox getCb3() {
        return cb3;
    }

    public void setCb3(CheckBox cb3) {
        this.cb3 = cb3;
    }

    public Button getBtn1() {
        return btn1;
    }

    public void setBtn1(Button btn1) {
        this.btn1 = btn1;
    }

    public Button getBtn2() {
        return btn2;
    }

    public void setBtn2(Button btn2) {
        this.btn2 = btn2;
    }

	public RelativeLayout getLayout4() {
		return layout4;
	}

	public void setLayout4(RelativeLayout layout4) {
		this.layout4 = layout4;
	}

	public View getView1() {
		return view1;
	}

	public void setView1(View view1) {
		this.view1 = view1;
	}

}
