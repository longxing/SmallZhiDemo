package com.iii360.box.view;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout.LayoutParams;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.iii360.box.R;

public class PopupWindowView implements OnClickListener {
	private Context context;
	private View mChildView;
	private PopupWindow mPopupWindow;
	private TextView mRemoveTv;
	private TextView mDeleteTv;

	public PopupWindowView(Context context) {
		// TODO Auto-generated constructor stub
		this.context = context;
		mChildView = LayoutInflater.from(context).inflate(R.layout.view_detail_menu, null);
		mRemoveTv = (TextView) mChildView.findViewById(R.id.menu_remove_tv);
		mDeleteTv = (TextView) mChildView.findViewById(R.id.menu_delete_tv);
		mRemoveTv.setOnClickListener(this);
		mDeleteTv.setOnClickListener(this);
	}

	public void show(View parent) {
		if (isShowing()) {
			mPopupWindow.dismiss();
		}
		mPopupWindow = new PopupWindow(mChildView, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, true);
		mPopupWindow.setBackgroundDrawable(new BitmapDrawable());
		mPopupWindow.setOutsideTouchable(true);
		mPopupWindow.update();
		mPopupWindow.showAsDropDown(parent, (parent.getWidth() - mPopupWindow.getWidth()) / 2, 0);
	}

	public void dismissMoveBtn() {
		mRemoveTv.setVisibility(View.GONE);
	}
	public void showMoveBtn() {
		mRemoveTv.setVisibility(View.VISIBLE);
	}

	public void dismiss() {
		if (mPopupWindow != null) {
			mPopupWindow.dismiss();
		}
	}

	public boolean isShowing() {
		if (mPopupWindow != null) {
			return mPopupWindow.isShowing();
		}
		return false;
	}

	private PopupWindowListener popupListener;

	public void setPopupListener(PopupWindowListener popupListener) {
		this.popupListener = popupListener;
	}

	public interface PopupWindowListener {
		public void onRemoveClick(View v);

		public void onDeleteClick(View v);

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

		mPopupWindow.dismiss();

		if (v == mDeleteTv) {
			if (popupListener != null) {
				this.popupListener.onDeleteClick(v);
			}
		} else if (v == mRemoveTv) {
			if (popupListener != null) {
				this.popupListener.onRemoveClick(v);
			}
		}
	}

}
