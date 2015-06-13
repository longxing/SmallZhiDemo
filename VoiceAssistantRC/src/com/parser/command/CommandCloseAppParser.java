package com.parser.command;

import java.util.regex.Matcher;

import com.base.data.CommandInfo;


public class CommandCloseAppParser extends AbstractCommandParser {

    public CommandCloseAppParser(Matcher matcher) {

    	super(AbstractCommandParser.COMMAND_NAME_CLOSE_APP, matcher);
    }


    public CommandCloseAppParser(String arg) {
        super(AbstractCommandParser.COMMAND_NAME_CLOSE_APP, arg);

    }

    public CommandInfo parser() {
    	CommandInfo info = null;
        info = new CommandInfo();
        info._commandName = getCommandName();
        info._answer="很抱歉，小智无法关闭要关闭的应用";
        return info;

    }

}
