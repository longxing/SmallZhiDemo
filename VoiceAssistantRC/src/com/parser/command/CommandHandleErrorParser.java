/**
 * 
 */
package com.parser.command;

import java.util.regex.Matcher;

import com.base.data.CommandInfo;

/**
 * @author yixuanliao
 *
 */
public class CommandHandleErrorParser extends AbstractCommandParser {

    public static  String ERROR_MSG = "";
    

    public CommandHandleErrorParser(String arg) {
        super(COMMAND_NAME_HANDLE_ERR, arg);
        
    }
    
    /**
     * @param text
     * @param handler
     * @param context
     */
    public CommandHandleErrorParser(Matcher matcher) {
        super(COMMAND_NAME_HANDLE_ERR, matcher);

    }


    public CommandInfo parser() {
    	CommandInfo info = null;
        info = new CommandInfo();
        info._commandName = getCommandName();
        info._answer="很抱歉，小智听不懂";
        return info;

    }
}
