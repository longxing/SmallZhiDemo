package com.iii360.box.view;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.PopupWindow;

import com.iii360.box.R;

/**
 * 菜单menu
 * 
 * @author Administrator
 * 
 */
public class BottomPopupMenu extends AbsPopupView implements OnClickListener {
    private LinearLayout mPlay;
    private LinearLayout mDelete;
    private View mBlankView;
    private PopupWindow mPopupWindow;

    private View mChildView;

    public BottomPopupMenu(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
        mChildView = LayoutInflater.from(context).inflate(R.layout.view_bottom_menu, null);
        mPlay = (LinearLayout) mChildView.findViewById(R.id.music_paly_layout);
        mDelete = (LinearLayout) mChildView.findViewById(R.id.music_stop_layout);
        mBlankView = mChildView.findViewById(R.id.blank_view);

        mPlay.setOnClickListener(this);
        mDelete.setOnClickListener(this);
        mBlankView.setOnClickListener(this);
    }

    @Override
    public void show() {
        // TODO Auto-generated method stub
        mPopupWindow = new PopupWindow(mChildView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, true);
        //实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw = new ColorDrawable(0x26000000);
        //设置SelectPicPopupWindow弹出窗体的背景
        mPopupWindow.setBackgroundDrawable(dw);
        mPopupWindow.setOutsideTouchable(true);
        mPopupWindow.update();
        mPopupWindow.showAtLocation(mChildView, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
    }

    @Override
    public void dismiss() {
        // TODO Auto-generated method stub
        if (mPopupWindow != null && isShowing()) {
            mPopupWindow.dismiss();
        }
    }

    @Override
    public boolean isShowing() {
        // TODO Auto-generated method stub
        if (mPopupWindow != null) {
            return mPopupWindow.isShowing();
        }
        return false;
    }

    private OnClickListener playListener;
    private OnClickListener deleteListener;

    public void setPlayListener(OnClickListener playListener) {
        this.playListener = playListener;
    }

    public void setDeleteListener(OnClickListener deleteListener) {
        this.deleteListener = deleteListener;
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        dismiss();

        if (v == mPlay) {
            if (playListener != null) {
                playListener.onClick(mPlay);
            }
        } else if (v == mDelete) {
            if (deleteListener != null) {
                deleteListener.onClick(mDelete);
            }
        }
    }

}
