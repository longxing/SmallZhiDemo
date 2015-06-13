package com.parser.iengine;

import android.content.Context;

import com.base.data.CommandInfo;
import com.base.util.AmazingBoxExcute;
import com.iii360.base.common.utl.BaseContext;
import com.iii360.sup.common.utl.HomeConstants;

public class AmazingBoxEngine extends AbstractEngine {
    private BaseContext mBaseContext;
    AmazingBoxExcute mAmazingBoxExcute;

    AmazingBoxEngine(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
        mAmazingBoxExcute = new AmazingBoxExcute(context);
        mBaseContext = new BaseContext(context);
    }

    @Override
    CommandInfo parse(String text, RequestParams params) {
        // TODO Auto-generated method stub
        //判断amazing box是否在线
        if (!mBaseContext.getPrefBoolean(HomeConstants.ABOX_CONNECT, false)) {
            return null;
        }
        return mAmazingBoxExcute.request(text, params);
    }

}
