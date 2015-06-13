package com.parser.iengine;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import android.content.Context;

import com.base.data.CommandInfo;
import com.base.network.XmlParserNew;
import com.iii360.sup.common.utl.LogManager;

public class TestEngine extends AbstractEngine {
    
    private static final String mHead = "http://match.360iii.net:7799/matchService/match/matchAction_test.action?question=";
    private XmlParserNew mXmlParserNew;
    TestEngine(Context context) {
        super(context);
        mXmlParserNew = new XmlParserNew();
    }

    @Override
    protected CommandInfo parse(String text, RequestParams params) {
    	CommandInfo msg = null;
        
        
        String utfString = "";
        try {
            utfString = URLEncoder.encode(text, "utf-8");
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            LogManager.printStackTrace(e);
        }
        String send = mHead + utfString + "&id=" + "k50038620-e9c7-4ad9-a262-c27b81d88929";
        mXmlParserNew.parse(send);
        
        msg = mXmlParserNew.getInfo();
        if(msg != null) {
            msg._question = text;
        }
        
        
        return msg;
    }

}
