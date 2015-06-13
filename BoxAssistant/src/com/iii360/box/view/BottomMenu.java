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
import android.widget.TextView;

import com.iii360.box.R;

/**
 * 菜单menu
 * 
 * @author Administrator
 * 
 */
public class BottomMenu extends AbsPopupView implements OnClickListener {
	private LinearLayout mPlay;
	private LinearLayout mDelete;
	private View mBlankView;
	private PopupWindow mPopupWindow;
	private View view1;
	private View view2;
	private View view3;
	private View mChildView;
	private TextView cancelBtn;
	private LinearLayout mAddToPlayList;
	private LinearLayout mSetRemindRing;
	private View view4;

	public BottomMenu(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		mChildView = LayoutInflater.from(context).inflate(R.layout.bottom_menu_popup, null);
		mPlay = (LinearLayout) mChildView.findViewById(R.id.music_paly_layout);
		mDelete = (LinearLayout) mChildView.findViewById(R.id.music_stop_layout);
		mAddToPlayList = (LinearLayout) mChildView.findViewById(R.id.music_add_playlist_layout);
		mSetRemindRing = (LinearLayout) mChildView.findViewById(R.id.music_setremind_layout);
		mBlankView = mChildView.findViewById(R.id.blank_view);
		cancelBtn = (TextView) mChildView.findViewById(R.id.bottom_menu_cancel);
		view1 = mChildView.findViewById(R.id.view1);
		view2 = mChildView.findViewById(R.id.view2);
		view3 = mChildView.findViewById(R.id.view3);
		view4 = mChildView.findViewById(R.id.view4);

		mPlay.setOnClickListener(this);
		mDelete.setOnClickListener(this);
		mBlankView.setOnClickListener(this);
		cancelBtn.setOnClickListener(this);
		mAddToPlayList.setOnClickListener(this);
		mSetRemindRing.setOnClickListener(this);
	}

	public void dismissDeleteBtn() {
		mDelete.setVisibility(View.GONE);
		view3.setVisibility(View.GONE);
	}

	public void dismissAddToPlayListBtn() {
		mAddToPlayList.setVisibility(View.GONE);
		view2.setVisibility(View.GONE);
	}

	public void dismissPlayBtn() {
		mPlay.setVisibility(View.GONE);
		view1.setVisibility(View.GONE);
	}

	public void dismissSetRemindRingBtn() {
		mSetRemindRing.setVisibility(View.GONE);
		view4.setVisibility(View.GONE);
	}

	@Override
	public void show() {
		// TODO Auto-generated method stub
		mPopupWindow = new PopupWindow(mChildView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, true);
		// 实例化一个ColorDrawable颜色为半透明
		ColorDrawable dw = new ColorDrawable(0x26000000);
		// 设置SelectPicPopupWindow弹出窗体的背景
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
	private OnClickListener addToPlayListListener;
	private OnClickListener cancelListener;
	private OnClickListener setRemindRingListener;

	public void setSetRemindRingListener(OnClickListener setRemindRingListener) {
		this.setRemindRingListener = setRemindRingListener;
	}

	public void setPlayListener(OnClickListener playListener) {
		this.playListener = playListener;
	}

	public void setCancelListener(OnClickListener cancelListener) {
		this.cancelListener = cancelListener;
	}

	public void setAddToPlayListListener(OnClickListener addToPlayListListener) {
		this.addToPlayListListener = addToPlayListListener;
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
		} else if (v == cancelBtn) {
			// if (cancelListener != null) {
			// cancelListener.onClick(cancelBtn);
			// }
		} else if (v == mAddToPlayList) {
			if (addToPlayListListener != null) {
				addToPlayListListener.onClick(mAddToPlayList);
			}
		} else if (v == mSetRemindRing) {
			if (setRemindRingListener != null) {
				setRemindRingListener.onClick(mSetRemindRing);
			}
		}
	}

}
