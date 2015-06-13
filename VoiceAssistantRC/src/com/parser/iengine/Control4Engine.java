package com.parser.iengine;



import com.base.data.CommandInfo;
import com.base.util.HttpRequestInfo;
import com.iii360.base.common.utl.BaseContext;
import com.iii360.base.common.utl.LogManager;
import com.voice.recognise.KeyList;

import android.content.Context;

public class Control4Engine extends AbstractEngine {
	private Context mContext;
	Control4Engine(Context context) {
		super(context);
		mContext = context;
		// TODO Auto-generated constructor stub
	}

	@Override
	CommandInfo parse(String text, RequestParams params) {
		// TODO Auto-generated method stub
        BaseContext baseContext = new BaseContext(mContext);
        //搜索到control4
        if (baseContext.getGlobalBoolean(KeyList.GKEY_BOOL_CONNECT_CONTROL4, false)) {
            LogManager.e("go to contorl4");
            HttpRequestInfo httpRequestinfo = new HttpRequestInfo(mContext);
            return httpRequestinfo.request(text, params);
        }
        
        return null;
	}

}
