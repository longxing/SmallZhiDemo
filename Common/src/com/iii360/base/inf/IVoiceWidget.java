package com.iii360.base.inf;


import android.content.Intent;
import android.view.View;
import android.view.animation.Animation;
import android.widget.LinearLayout;

public interface IVoiceWidget {
    public void setViewContainer(IViewContainer viewContainer);

    /**
     * Widget显示的时候调用
     */
    public void onShow();

    /**
     * 销毁
     */
    public void destory();

    /**
     * 销毁的时候调用。释放资源写在这里。
     */
    public void onDestory();

    /**
     * 最小化widget
     */
    public void minimize();

    /**
     * 
     * @return 是否在下一条对话来的时候删除本条对话
     */
    public boolean isDeleteInNextSession();

    /**
     * 
     * @return 是否清楚上一条对话
     */
    public boolean isClearPre();

    /**
     * 
     * @return 是否是回答
     */
    public boolean isAnswer();



    /**
     * 
     * @return 布局相关的参数
     */
    public LinearLayout.LayoutParams getLayoutParams();

    /**
     * 
     * @return Gravity.LEFT OR Gravity.RIGHT
     */
    public int getGravity();

    public void onActivityResult(int requestCode, int resultCode, Intent result);
    /**
     * 
     * @return 是否清楚之前的
     */
    public boolean isNeedClear();
    
    public void isNeedClear(boolean isNeedClear);
    
    
    public BasicServiceUnion getUnion();
}
