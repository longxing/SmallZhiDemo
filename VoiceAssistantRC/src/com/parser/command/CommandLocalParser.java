//ID20120723001 liuwen begin
package com.parser.command;

import java.util.regex.Matcher;

import com.base.data.CommandInfo;

public class CommandLocalParser extends AbstractCommandParser {


    public CommandLocalParser(String arg) {
        super(COMMAND_NAME_LOCAL, arg);

    }

    public CommandLocalParser(Matcher matcher) {
        super(COMMAND_NAME_LOCAL, matcher);

    }

    @Override
    public CommandInfo parser() {
    	CommandInfo info = null;
        info = new CommandInfo();
        info._commandName = getCommandName();
        return info;

    }

}
//ID20120723001 liuwen end