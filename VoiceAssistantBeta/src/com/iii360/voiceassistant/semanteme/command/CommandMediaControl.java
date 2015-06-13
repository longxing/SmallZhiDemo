package com.iii360.voiceassistant.semanteme.command;

import java.util.HashMap;

import android.content.Intent;

import com.base.data.CommandInfo;
import com.iii360.base.common.utl.LogManager;
import com.iii360.base.inf.BasicServiceUnion;
import com.iii360.base.inf.IMediaInterface;
import com.iii360.base.inf.parse.IVoiceCommand;
import com.iii360.sup.common.utl.SupKeyList;
import com.voice.assistant.main.KeyList;
import com.voice.assistant.main.music.MyMusicHandler;
import com.voice.assistant.main.newmusic.MusicUtil;
import com.voice.assistant.utl.AirPlayMusicController;
import com.voice.assistant.utl.DlanMusicController;

public class CommandMediaControl extends AbstractVoiceCommand {

	private static final String TAG = "CommandMediaControl";

	private final static HashMap<String, String> mParamMap = new HashMap<String, String>();

	static {
		mParamMap.put("关闭", "0");
		mParamMap.put("退出", "0");
		mParamMap.put("关掉", "0");
		mParamMap.put("关了", "0");
		mParamMap.put("结束", "0");
		mParamMap.put("停止", "1");
		mParamMap.put("停掉", "1");
		mParamMap.put("停了", "1");
		mParamMap.put("停了", "1");
		mParamMap.put("停", "1");
		mParamMap.put("暂停", "1");
		mParamMap.put("停一下", "1");
		mParamMap.put("开始", "2");
		mParamMap.put("启动", "2");
		// mParamMap.put("播放", "2");
		mParamMap.put("继续", "2");
		mParamMap.put("继续播", "2");
		mParamMap.put("继续播放", "2");
		mParamMap.put("继续放", "2");
		mParamMap.put("后", "3");
		mParamMap.put("下", "3");
		mParamMap.put("上", "4");
		mParamMap.put("前", "4");
		mParamMap.put("换", "5");
		mParamMap.put("循环", "6");
		mParamMap.put("好听", "7");
		mParamMap.put("喜欢", "7");
		mParamMap.put("难听", "8");
		mParamMap.put("不好听", "8");
		mParamMap.put("讨厌", "8");
	}
	private CommandInfo mInfo;
	private DlanMusicController mDlanMusicController;
	private AirPlayMusicController mAirPlayMusicController;

	public CommandMediaControl(BasicServiceUnion union, CommandInfo commandInfo) {
		super(union, commandInfo, COMMAND_NAME_MEDIA_CONTROL, "播放控制");
		mInfo = commandInfo;
		mDlanMusicController = new DlanMusicController(mContext);
		mAirPlayMusicController = new AirPlayMusicController(mContext);
	}

	@Override
	public IVoiceCommand execute() {
		super.execute();
		// TODO Auto-generated method stub
		String arg = mInfo.getArg(0);
		LogManager.i(TAG, "execute   -----info:" + arg);

		// 记录歌曲命令开始执行的时间
		SupKeyList.COMMAND_START_EXECUTE = System.currentTimeMillis();
		int commandType = -1;

		if (mParamMap.containsKey(arg)) {
			commandType = Integer.valueOf(mParamMap.get(arg));
			// TODO
			Intent i = new Intent();

			if (commandType >= MusicUtil.COMMAND_NEXT && commandType <= MusicUtil.COMMAND_RANDOM) {
				mAirPlayMusicController.setAirplay(false);
				mAirPlayMusicController.stop();
				this.getUnion().getRecogniseSystem().startWakeup();
			}
            
			
			
			LogManager.i(TAG,"current commandType:"+commandType);
			switch (commandType) {
			case MusicUtil.COMMAND_EXIT:
				// TODO:如果添加停止音乐方法可能会影响到播放器状态,暂时改为暂停播放当前音乐
				getUnion().getMediaInterface().pause();
				mBaseContext.setGlobalLong("mediaPauseTime", System.currentTimeMillis());
				mDlanMusicController.sendToDlanStop();

				mAirPlayMusicController.stop();

				this.getUnion().getRecogniseSystem().startWakeup();

				break;
			case MusicUtil.COMMAND_STOP:

				if (mDlanMusicController.isDlan()) {
					LogManager.i(TAG, "dlna----------COMMAND_STOP---onpause");
					mDlanMusicController.pause();

				} else if (mAirPlayMusicController.isAirplay()) {
					LogManager.i(TAG, "airplay-----------COMMAND_STOP---onpause");
					mAirPlayMusicController.pause();

				} else {
					LogManager.i(TAG, "普通歌曲-----------COMMAND_STOP---onpause");
					getUnion().getMediaInterface().pause();
					mBaseContext.setGlobalLong("mediaPauseTime", System.currentTimeMillis());
				}

				break;
			case MusicUtil.COMMAND_START:
				if (mDlanMusicController.isDlan()) {
					LogManager.i(TAG, "dlna----------COMMAND_START---play");
					mDlanMusicController.paly();

				} else if (mAirPlayMusicController.isAirplay()) {
					LogManager.i(TAG, "airplay----------COMMAND_START---play");
					mAirPlayMusicController.play();

				} else {
					LogManager.i(TAG, "普通歌曲----------COMMAND_START---play");
					long distance = System.currentTimeMillis() - mBaseContext.getGlobalLong("mediaPauseTime");
					if (distance > 3600000) { // 同一首歌暂停有效期为1小时
						getUnion().getCommandEngine().handleText("唱歌");
						return null;
					} else {
						getUnion().getMediaInterface().resume();
					}
				}
				break;
			case MusicUtil.COMMAND_NEXT:
				forceNext();

				MusicUtil.logMusic(commandType, mBaseContext);
				return null;
			case MusicUtil.COMMAND_PRE:
				MusicUtil.logMusic(commandType, mBaseContext);
				getUnion().getMediaInterface().playPre();
				return null;
			case MusicUtil.COMMAND_RANDOM:
				// random not support
				MusicUtil.logMusic(commandType, mBaseContext);
				getUnion().getMediaInterface().playNext();
				return null;
			case MusicUtil.COMMAND_CIRCLE:
				i.setAction(KeyList.PKEY_MUSIC_CIRCLE);
				getUnion().getMediaInterface().setPlayMode(IMediaInterface.PLAY_MODE_SIGNAL);
				sendAnswerSession("好的，进入单曲循环");
				break;
			case MusicUtil.COMMAND_BAD:

				if (getUnion().getBaseContext().getGlobalBoolean(KeyList.GKEY_IS_MUSIC_IN_PLAYING)) {
					forceNext();
				}

				MusicUtil.logMusic(commandType, mBaseContext);
				return null;
			case MusicUtil.COMMAND_GOOD:
				if (getUnion().getBaseContext().getGlobalBoolean(KeyList.GKEY_IS_MUSIC_IN_PLAYING)) {
					sendAnswerSession("收藏成功");
				}
				MusicUtil.logMusic(commandType, mBaseContext);
				return null;
			default:
				break;
			}
		}
		MusicUtil.logMusic(commandType, mBaseContext);

		return null;
	}

	private void forceNext() {
		MyMusicHandler mediaInterface = (MyMusicHandler) getUnion().getMediaInterface();
		if (mediaInterface.getMediaInfoList() == null || mediaInterface.getPlayMode() == IMediaInterface.PLAY_MODE_SIGNAL || mediaInterface.getPlayMode() == IMediaInterface.PLAY_MODE_LOOPSIGNAL
				|| mediaInterface.getMediaInfoList().size() <= 1) {
			LogManager.i("CommandMediaControl" + "----->" + "getPlayMode:" + mediaInterface.getPlayMode() + "---->size:" + mediaInterface.getMediaInfoList().size());
			LogManager.i("CommandMediaControl" + "----->" + "播放本地歌曲");
			getUnion().getCommandEngine().handleText("唱歌");
		} else {
			LogManager.e(MusicUtil.COMMAND_NEXT + "----->" + "播放下一首");
			getUnion().getMediaInterface().playNext();
		}
	}
}
