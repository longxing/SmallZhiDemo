//ID20120723001 liuwen begin
package com.parser.command;

import java.util.regex.Matcher;

import com.base.data.CommandInfo;

public class CommandStudyParser extends AbstractCommandParser {
	private static String TEACHER_FLAG = "1";

    public CommandStudyParser(String arg) {
        super(COMMAND_NAME_STUDY, arg);

    }

    public CommandStudyParser(Matcher matcher) {
        super(COMMAND_NAME_STUDY, matcher);

    }

    @Override
    public CommandInfo parser() {
    	CommandInfo info = new CommandInfo();
    	info.addArg("");
    	info.addArg("");
    	info.addArg(TEACHER_FLAG);
        info._commandName = getCommandName();        
        return info;
    }

}
//ID20120723001 liuwen end