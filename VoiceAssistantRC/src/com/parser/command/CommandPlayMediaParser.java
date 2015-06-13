//ID20120825002 zhanglin begin
package com.parser.command;

//ID20120517001 zhanglin begin
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.base.data.CommandInfo;
import com.iii360.sup.common.utl.LogManager;


public class CommandPlayMediaParser extends AbstractCommandParser {

	private String mName = "";
	private String mIndex = "";
	private String mAction = "2";
	private String mSingerName = "";
	private String mIsRandomPlay = "0";
	// ID20120828002 zhanglin begin
	private static final String[] mMusicNames = { "歌", "音乐", "歌曲" };
	// ID20120828002 zhanglin end
	// ID20120907002 zhanglin begin
	private static final String[] mVideoNames = { "视频", "电影" };

	// ID20120907002 zhanglin end
	public CommandPlayMediaParser(String arg) {
		super(COMMAND_NAME_PLAY_MEDIA, arg);

	}

	public CommandPlayMediaParser(Matcher matcher) {
		super(COMMAND_NAME_PLAY_MEDIA, matcher, true);

	}

	@Override
	public CommandInfo parser() {
		LogManager.d("CommandPlayMedia", "excute");
		mName = getParams(0);
		mName = mName == null ? "" : mName;

		mSingerName = getParams(1);
		mSingerName = mSingerName == null ? "" : mSingerName;

		mAction = getParams(2);
		mAction = mAction == null ? "" : mAction;

		setPlayValue();

		CommandInfo info = new CommandInfo();
		info._commandName = COMMAND_NAME_PLAY_MEDIA;
		info.addArg(mAction);
		info.addArg(mName);
		info.addArg(mSingerName);
		info.addArg(mIsRandomPlay);
		LogManager.e(mAction);
//
//		if (mAction.contains("1")) {
//			info._answer = "很抱歉，小智暂时不能播放视频";
//		} else {
//			info._answer = "很抱歉，小智暂时不能播放音乐";
//		}
		return info;
	}

	public void setPlayValue() {
		// mName.replaceAll("", replacement)

		if (mName != null) {
			for (int i = 0; i < mMusicNames.length; i++) {
				if (mName.equals(mMusicNames[i])) {
					mName = "";
					// ID20121010005 zhanglin begin
					if (!mAction.equals("播放")) {
						mIsRandomPlay = "1";
					}
					mAction = "听";
					// ID20121010005 zhanglin end
					break;
				}
			}
			for (int i = 0; i < mVideoNames.length; i++) {
				if (mName != null && mName.equals(mVideoNames[i])) {
					mName = "";
					mAction = "看";
					// ID20120825002 zhanglin begin
					// delete
					// ID20120825002 zhanglin end
					break;
				}
			}
		} else {
			mIsRandomPlay = "1";
		}
		if (mSingerName != null && !mSingerName.equals("")) {
		}
		if (mAction != null && !mAction.equals("")) {
			if (mAction.contains("看")) {
				mAction = "1";
			} else if (mAction.contains("听") || mAction.contains("唱")) {
				mAction = "0";
			} else {
				mAction = "2";
			}
		}
	}

}
// ID20120825002 zhanglin end