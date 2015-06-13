//ID20120818001 zhanglin begin
package com.parser.command;

import java.util.regex.Matcher;

import com.base.data.CommandInfo;
import com.parser.command.AbstractCommandParser;

public class CommandMovieTicketParser extends AbstractCommandParser {
	private String mQustion;
	private String mName;
	private String mActorName;
	private String mCinema;

	public CommandMovieTicketParser(Matcher matcher) {
		super(COMMAND_NAME_MOVIE_TICKET, matcher);
		// TODO Auto-generated constructor stub
		mQustion = matcher.group();
		mName = matcher.group(3);
		mActorName = matcher.group(2);
		mCinema = matcher.group(1);
	}

	@Override
	public CommandInfo parser() {
		// TODO Auto-generated method stub
		CommandInfo mInfo = new CommandInfo();
		mInfo.addArg(mName);
		mInfo.addArg(mCinema);
		mInfo.addArg(mActorName);
		mInfo._question = mQustion;
		mInfo._commandName = getCommandName();

		mInfo._answer = "很抱歉，小智暂时不支持订票功能";
		return mInfo;
	}
}
// ID20120818001 zhanglin end