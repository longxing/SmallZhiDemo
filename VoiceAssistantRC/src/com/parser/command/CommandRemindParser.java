package com.parser.command;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.base.data.CommandInfo;
import com.base.util.remind.RemindObject;
import com.base.util.remind.RemindRecogniser;

public class CommandRemindParser extends AbstractCommandParser {

    private String mRecContent, mRecContentGroup1;
    private String canNotDelay =null;
    private static final Pattern [] mExceptPatterns = new Pattern[]{
// ID20121220001 hujinrong begin
        Pattern.compile("(.*)(不想|不需要|除非|不要)(.*)?"),
// ID20121220001 hujinrong end
//ID20121013002 hujinrong begin
//        Pattern.compile("(.*)(发?(信息)|(短信)).*((提醒)|(叫)|(通知)|(告诉).*)")
//ID20121013002 hujinrong end
    };
//ID20121010001 hujinrong begin
    private static final Pattern  mTravelPattern = Pattern.compile(".*(火车|飞机|高铁|动车|时刻表|列车|机票|航线|航班).*");
    private static final Pattern  mRemdindKeyWord = Pattern.compile(".*(提醒|通知|叫我|开会).*");
//ID20121010001 hujinrong end
    private static final Pattern [] nextConversationPattern = new Pattern[]{
        
    };
    private static final Pattern[] match_special_pattern = new Pattern[] {
//ID20121010001 hujinrong begin
    Pattern.compile(".*[一1]点(也|都)?(儿|即|击|通|的|点|也|吗|不|就|一)(.*)?"),
//ID20121010001 hujinrong end
    };
    private RemindObject mRemindObject;

    public CommandRemindParser(String arg) {
        super(COMMAND_NAME_REMIND, arg);

    }

    public CommandRemindParser(Matcher matcher) {
        super(COMMAND_NAME_REMIND, matcher);        
        mRecContent = matcher.group();
        if(mRecContent.startsWith("不可延时的"))
        {
        	canNotDelay ="canNotDelay";
        }else{
        	canNotDelay = null;
        }
        mRecContent = mRecContent.replace("不可延时的", "");
        mRecContentGroup1 = matcher.group(3);
        mRecContent = mRecContent.replace("。", "");

    }

    @Override
    protected Pattern[] getMatchPatterns() {
        // TODO Auto-generated method stub
        return super.getMatchPatterns();
    }

    @Override
    protected Pattern[] getExceptPatterns() {
        // TODO Auto-generated method stub
        return mExceptPatterns;
    }

    @Override
    public CommandInfo getNextConversation(String text, String id) {
        // TODO Auto-generated method stub
        return super.getNextConversation(text, id);
    }

    public CommandInfo parser() {
        // TODO Auto-generated method stub
        // super.excute();
        if (!isItselfCommand()) {
            return null;
        }
        CommandInfo cmdInfo = null;
        if (mRecContent.contains("一点") || mRecContent.contains("1点")) {

            for (Pattern pattern : match_special_pattern) {// 不包含此正则表达式的内容才认为可能是备忘
                Matcher m = pattern.matcher(mRecContent);
                if (m.matches()) {
                    return null;
                }
            }
        } 
//ID20121010001 hujinrong begin
        //if exist travel key word .
        Matcher matcherTravel = mTravelPattern.matcher(mRecContent);
        Matcher matcherRemindKeyWord = mRemdindKeyWord.matcher(mRecContent);
        if(matcherTravel.matches()){
        	//no exist remind key word.
        	if(!matcherRemindKeyWord.matches())
        		return null;
        }
        //if exist travel key word .
//ID20121010001 hujinrong end        
        mRemindObject = new RemindRecogniser(mRecContent).getRemindObject();
        cmdInfo = new CommandInfo();
        cmdInfo._commandName = COMMAND_NAME_REMIND;
        cmdInfo.addArg(mRecContent);
//        cmdInfo.addArg(mRemindObject._reminderTime);
//        cmdInfo.addArg(mRemindObject._reminderDay);
        cmdInfo.addArg(canNotDelay);
//        cmdInfo._answer="很抱歉，小智暂时不能为您记录备忘";
        return cmdInfo;
    }

}
