package com.iii360.voiceassistant.semanteme.command;

import com.base.data.CommandInfo;
import com.iii360.base.common.utl.IGloableHeap;
import com.iii360.base.common.utl.KeyList;
import com.iii360.base.common.utl.LogManager;
import com.iii360.base.inf.BasicServiceUnion;
import com.iii360.base.inf.IMediaInterface.OnEndOfLoopListener;
import com.iii360.base.inf.ITTSController;
import com.iii360.base.inf.parse.IVoiceCommand;
import com.iii360.sup.common.utl.SupKeyList;
import com.voice.assistant.main.music.MediaInfoList;
import com.voice.assistant.main.music.MediaUtil;
import com.voice.assistant.utl.AirPlayMusicController;
import com.voice.assistant.utl.DlanMusicController;

public class CommandPlayMedia extends AbstractVoiceCommand {

	private static String TAG = "CommandPlayMedia";
	private MediaUtil mediaUtil;
	private MediaInfoList mMediaList;
	private CommandInfo mCommandInfo;

	public CommandPlayMedia(BasicServiceUnion union, CommandInfo commandInfo) {
		super(union, commandInfo, COMMAND_NAME_PLAY_MEDIA, "播放音乐");
		mCommandInfo = commandInfo;

	}

	@Override
	public IVoiceCommand execute() {
		super.execute();

		mediaUtil = new MediaUtil(mContext);
		mediaUtil.parseCommand(mCommandInfo);
		LogManager.e(mCommandInfo.toCommandString() + "  " + mCommandInfo.getArgList().size());

		mMediaList = mediaUtil.getList();
		// 是否支持TTS DEBUG模式
		if (KeyList.IS_TTS_DEBUG && KeyList.IS_PLAYER_DEBUG) {
			IGloableHeap gloableHeap = ((IGloableHeap) mContext.getApplicationContext());
			ITTSController mITTSController = (ITTSController) gloableHeap.getGlobalObjectMap().get(KeyList.GKEY_TTS_CONTORLLER);
			// 是否为指定歌曲
			String spec = mCommandInfo.getArg(2);
			if (spec == null || spec.length() == 0) {
				mITTSController.syncPlay("播放规则为本地曲目");
			} else if (spec.equals("1")) {
				mITTSController.syncPlay("播放规则为指定曲目");
			} else {
				mITTSController.syncPlay("播放规则为范围曲库");
			}
		}

		if (mMediaList.size() > 0) {
			int currentPosition = mediaUtil.getCurrentLocalMusicId();
			LogManager.i(TAG, "play localmusic current id:" + currentPosition);
			if (currentPosition != -1) {
				playMedia(currentPosition);
			} else {
				playMedia(0);
			}

		} else {
			// 无列表播放，根据语义没有搜索到歌曲，再吃请求无列表播放。
			getUnion().getCommandEngine().handleText("无列表", true, false);
		}
		return null;
	}

	/**
	 * 同步锁防止，最后一首歌拉歌，交叉到下一个播放命令到达，造成拉歌和新播放歌曲同时播放
	 * 
	 * @param positionc
	 */
	private void playMedia(int position) {
		getUnion().getBaseContext().setGlobalInteger(KeyList.GKEY_PLAY_TYPE, KeyList.GKEY_PLAY_TYPE_MUSIC);

		DlanMusicController mDlanMusicController = new DlanMusicController(mContext);
		android.util.Log.e("hefeng", "CommandPlayMedia is dlan play:" + mDlanMusicController.isDlan());
		if (mDlanMusicController.isDlan()) {
			mDlanMusicController.stop(true);
		}

		AirPlayMusicController airPlayMusicController = new AirPlayMusicController(mContext);
		android.util.Log.e("hefeng", "CommandPlayMedia is airplay play:" + airPlayMusicController.isAirplay());
		if (airPlayMusicController.isAirplay()) {
			airPlayMusicController.stop();
			this.getUnion().getRecogniseSystem().startWakeup();
		}

		mMediaList.setCurIndex(position);
		getUnion().getMediaInterface().setOnEndOfLoopListener(new OnEndOfLoopListener() {

			@Override
			public boolean onEndOfLoop() {
				String spec = mCommandInfo.getArg(2);
				LogManager.i(TAG, "current musiclist  paly rules:" + spec);
				// 非指定歌曲，需要重新拉歌曲
				if (spec == null || spec.length() == 0) {
					// 本地歌曲
					return true;
				} else if (spec.equals("1")) {
					// 指定歌曲
					return true;
				} else {
					// 范围曲库
					if (KeyList.IS_TTS_DEBUG && KeyList.IS_PLAYER_DEBUG) {
						IGloableHeap gloableHeap = ((IGloableHeap) mContext.getApplicationContext());
						ITTSController mITTSController = (ITTSController) gloableHeap.getGlobalObjectMap().get(KeyList.GKEY_TTS_CONTORLLER);
						mITTSController.syncPlay("重新更新播放列表");
					}
					// 不需要欢迎词
					getUnion().getBaseContext().setGlobalBoolean(KeyList.GKEY_IS_PLAY_WELCOME, false);
					getUnion().getCommandEngine().handleText(mCommandInfo._question, true, false);
					return false;
				}
			}
		});
		getUnion().getRecogniseSystem().stopWakeup();
		getUnion().getMediaInterface().setMediaInfoList(mMediaList);
		getUnion().getMediaInterface().setPlayType(1);
		getUnion().getMediaInterface().start();
		// 唱歌命令开始执行
		SupKeyList.COMMAND_START_EXECUTE = System.currentTimeMillis();
	}
}
