package com.parser.iengine;

import android.content.Context;

import com.base.data.CommandInfo;
import com.base.util.KeyManager;
import com.parser.command.AbstractCommandParser;
import com.parser.command.CommandParserFactory;

public class ContextEngine extends AbstractEngine {

    private String mId;
    
    ContextEngine(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
    }
    
    

    @Override
    CommandInfo parse(String text, RequestParams params) {
    	CommandInfo info = null;
    	
        if(mId == null || !mId.equals(params.getId())) {
        	mId = params.getId();
            return info;
        }
        
        AbstractCommandParser lastParser = (AbstractCommandParser) getGlobalObject(KeyManager.GKEY_OBJ_LAST_PASER);
        if(lastParser != null) {
            AbstractCommandParser temp = CommandParserFactory.matchCommandParser(lastParser.getClass(), text);
            if(temp == null) {
                info = lastParser.getNextConversation(text, params.getId());
            } else {
                info = temp.parser();
            }
            
        }
        
        if(info != null) {
            info._appId = params.getParam(RequestParams.PARAM_APP_ID);
        }
        
        return info;
    }

}
