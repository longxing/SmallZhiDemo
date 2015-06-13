//ID20120723001 liuwen begin
package com.parser.command;

import java.util.regex.Matcher;

import com.base.data.CommandInfo;

public class CommandHelpParser extends AbstractCommandParser {


    public CommandHelpParser(String arg) {
        super(COMMAND_NAME_HELP, arg);

    }

    public CommandHelpParser(Matcher matcher) {
        super(COMMAND_NAME_HELP, matcher);
    }

    public CommandInfo parser() {
    	CommandInfo msg = new CommandInfo();
        msg._commandName = getCommandName();
        msg._answer="我可以帮您控制家电，将笑话，播报天气，将儿童故事，播报新闻，播报日期，还有更多功能正在学习中";
        return msg;
    }

}
//ID20120723001 liuwen end