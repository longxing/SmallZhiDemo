package com.iii360.box.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ExpandableListView;

import com.iii360.box.util.LogManager;

public class RemindExpandableListView extends ExpandableListView {
    private OnListClickListener onListClickListener;
    private float mDownX;
    private float mDownY;
    private float mUpX;
    private float mUpY;

    public void setClickListener(OnListClickListener onListClickListener) {
        this.onListClickListener = onListClickListener;
    }

    public RemindExpandableListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
        init();
    }

    public RemindExpandableListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        // TODO Auto-generated constructor stub
        init();
    }

    public RemindExpandableListView(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
        init();
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        // TODO Auto-generated method stub
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            //因为onTouchEvent事件监听不到
            mDownX = event.getX();
            mDownY = event.getY();
//            setListener();
//            LogManager.e("ACTION_DOWN   " + mDownX + ":" + mDownY);

        }

        return super.onInterceptTouchEvent(event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // TODO Auto-generated method stub

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            //事件监听不到
//            float x = event.getX();
//            float y = event.getY();
//
//            LogManager.e("ACTION_DOWN   " + x + ":" + y);

        } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
//            float x = event.getX();
//            float y = event.getY();

//            LogManager.v("ACTION_MOVE   "+x+":"+y);

        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            mUpX = event.getX();
            mUpY = event.getY();

//            LogManager.e("ACTION_UP   " + mUpX + ":" + mUpY);
//            LogManager.e("ACTION_UP  水平：    " + (mUpX - mDownX) + "=============垂直 :  " + (mUpY - mDownY));

            if (Math.abs(mUpX - mDownX) > Math.abs(mUpY - mDownY)) {
                return true;
            }

        }

        return super.onTouchEvent(event);
    }

    private void init() {
        this.setOnScrollListener(new OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                // TODO Auto-generated method stub

                if (scrollState == SCROLL_STATE_IDLE) {

                    if (getScrollYDistance() > 0) {
                        setListener();
                        LogManager.i("=============onScrollStateChanged");
                    }
//                    LogManager.i("=============getScrollYDistance()=" + getScrollYDistance());
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                // TODO Auto-generated method stub
//                LogManager.i("=============onScrollStateChanged");
            }
        });
        this.setOnGroupClickListener(new OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                // TODO Auto-generated method stub
                LogManager.i("=============OnGroupClickListener");

                setListener();
                return true;
            }
        });

        this.setOnChildClickListener(new OnChildClickListener() {

            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                // TODO Auto-generated method stub
                LogManager.i("=============OnChildClickListener");
                setListener();
                return true;
            }
        });
    }

    private int getScrollYDistance() {
        View c = getChildAt(0);
        int scrollY = -c.getTop() + getFirstVisiblePosition() * c.getHeight();
        return scrollY;
    }

    private void setListener() {
        if (onListClickListener != null) {
            onListClickListener.onListClick();
        }
    }

    /**
     * Interface definition for a callback to be invoked when a view is clicked.
     */
    public interface OnListClickListener {
        /**
         * Called when a view has been clicked.
         * 
         * @param v
         *            The view that was clicked.
         */
        void onListClick();
    }

}
