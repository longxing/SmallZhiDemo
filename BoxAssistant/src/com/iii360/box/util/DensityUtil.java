package com.iii360.box.util;

import android.content.Context;

public class DensityUtil {
    /**
     * 将dip转换为px
     * 
     * @param context
     * @param dipValue
     * @return
     */
    public static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

}
