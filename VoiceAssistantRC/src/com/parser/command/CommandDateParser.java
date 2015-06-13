//ID20120906002 hujinrong begin
package com.parser.command;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.base.data.CommandInfo;
import com.base.util.remind.RemindObject;
import com.base.util.remind.RemindRecogniser;

public class CommandDateParser extends AbstractCommandParser {

    private final static int TIME_FLAG = 1;
    private final static int DATE_FLAG = 2;

    private String mRecContent;
    private String mRecContentGroup1;
    private RemindObject mRemindObject;

    private static final Pattern[] exceptPatterns = new Pattern[] { Pattern.compile("(.*)(不想|不需要|除非|不要|放屁|乱说|听|播放|唱)(.*)?"),
            Pattern.compile("(.*)(发?(信息)|(短信)?).*((提醒)|(叫)|(通知)|(告诉).*)") };

    private Pattern[] match_time_pattern = new Pattern[] {

    Pattern.compile("(.*)(现在几点)(.*)?"), Pattern.compile("(.*)(现在什么时间)(.*)?"), Pattern.compile("(.*)(现在什么时候)(.*)?"),
            Pattern.compile("(.*)(现在时间)(.*)?"), Pattern.compile("(.*)(现在的时间)(.*)?"),

    };

    private Pattern[] match_day_pattern = new Pattern[] {

    Pattern.compile("(.*)农历(是|的)?(几|(多少)|(初几))(号|月)?"), Pattern.compile("(.*)的农历"), Pattern.compile("(.*)(星期几)(.*)?"),
            Pattern.compile("(.*)(几号)(.*)?"), Pattern.compile("(.*)(周几)(.*)?"), Pattern.compile("(.*)(日期)(.*)?"), Pattern.compile("(.*)(什么日子)(.*)?"),

    };

    public CommandDateParser(Matcher matcher) {
        super(COMMAND_NAME_DATE, matcher);
        // TODO Auto-generated constructor stub
        mRecContent = matcher.group();
    }

    public CommandDateParser(String arg) {
        super(COMMAND_NAME_DATE, arg);
    }

    @Override
    protected Pattern[] getExceptPatterns() {
        // TODO Auto-generated method stub
        return exceptPatterns;
    }

    public CommandInfo parser() {
        CommandInfo info = null;
        if (!isItselfCommand()) {
            return null;
        }
        for (int i = 0; i < match_time_pattern.length; i++) {
            Matcher matcher = match_time_pattern[i].matcher(mRecContent);
            if (matcher.matches()) {
                info = new CommandInfo();
                info._commandName = COMMAND_NAME_DATE;
                info.addArg(TIME_FLAG + "");
                return info;
            }
        }

        for (int i = 0; i < match_day_pattern.length; i++) {
            Matcher matcher = match_day_pattern[i].matcher(mRecContent);
            if (matcher.matches()) {
                mRecContentGroup1 = matcher.group(1);
                break;
            }
        }

        if (mRecContentGroup1 == null || mRecContentGroup1.contains("农历"))
            return null;
        else {
            mRemindObject = new RemindRecogniser(mRecContentGroup1).getRemindObject();
            info = new CommandInfo();
            info._commandName = COMMAND_NAME_DATE;
            info.addArg(DATE_FLAG + "");
            info.addArg(mRemindObject._reminderDay);
            
            return info;
        }

    }

}
// ID20120906002 hujinrong end