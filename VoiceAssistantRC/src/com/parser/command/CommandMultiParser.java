package com.parser.command;

import java.util.regex.Matcher;

import com.base.data.CommandInfo;

public class CommandMultiParser extends AbstractCommandParser {
    
    public CommandMultiParser(String arg) {
        super(COMMAND_NAME_MULTI, arg);
        // TODO Auto-generated constructor stub
    }


    public CommandMultiParser(Matcher matcher) {
        super(COMMAND_NAME_MULTI, matcher);
        // TODO Auto-generated constructor stub
    }

    @Override
    protected void extractParamsByExpression(Matcher matcher) {
        // TODO Auto-generated method stub
        super.extractParamsByExpression(matcher);
//        String src = matcher.group();
//        String mark = matcher.group(4);
//        int curIndex = src.indexOf(mark);
//        
//        while(curIndex >= 0) {
//            
//        }
//        
//        setParams(0, matcher.group(3));
//        setParams(1,matcher.group(5));
        
    }

    @Override
    public CommandInfo parser() {
        // TODO Auto-generated method stub
        return super.parser();
    }

    

}
