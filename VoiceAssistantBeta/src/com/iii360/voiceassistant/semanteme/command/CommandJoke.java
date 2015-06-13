package com.iii360.voiceassistant.semanteme.command;

import com.base.data.CommandInfo;
import com.iii360.base.common.utl.IGloableHeap;
import com.iii360.base.common.utl.KeyList;
import com.iii360.base.common.utl.LogManager;
import com.iii360.base.inf.BasicServiceUnion;
import com.iii360.base.inf.IMediaInterface.OnEndOfLoopListener;
import com.iii360.base.inf.ITTSController;
import com.iii360.base.inf.parse.IVoiceCommand;
import com.voice.assistant.main.music.MediaInfoList;
import com.voice.assistant.main.music.MediaUtil;
import com.voice.assistant.utl.AirPlayMusicController;
import com.voice.assistant.utl.DlanMusicController;

public class CommandJoke extends AbstractVoiceCommand {
	private MediaUtil mediaUtil;
	private MediaInfoList mMediaList;
	private CommandInfo mCommandInfo;

	public CommandJoke(BasicServiceUnion union, CommandInfo commandInfo) {
		super(union, commandInfo, COMMAND_NAME_PLAY_MEDIA, "播放笑话");
		mCommandInfo = commandInfo;

	}

	@Override
	public IVoiceCommand execute() {
		super.execute();
		
//		new Thread(new Runnable() {
//			@Override
//			public void run() {
				mediaUtil = new MediaUtil(mContext);
				mediaUtil.parseCommand(mCommandInfo);
				LogManager.e(mCommandInfo.toCommandString() + "  " + mCommandInfo.getArgList().size());
				String ttsinfo = null;
				if (mCommandInfo.getArgList().size() == 4 && mCommandInfo._isFromNet) {
					ttsinfo = mCommandInfo.getArg(3).trim();
					if (ttsinfo.length() > 0) {
						LogManager.e("tts is:" + ttsinfo);
						getUnion().getTTSController().play(ttsinfo);
						try {
							Thread.sleep(3000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}

				mMediaList = mediaUtil.getList();
				// mMediaList.setPlayMode(IMediaPlayerInterface.PLAY_MODE_ALL);
				// LogManager.e("123");
				
				// 是否支持TTS DEBUG模式
				if (KeyList.IS_TTS_DEBUG) {
					IGloableHeap gloableHeap = ((IGloableHeap) mContext
							.getApplicationContext());
					ITTSController mITTSController = (ITTSController) gloableHeap
							.getGlobalObjectMap().get(KeyList.GKEY_TTS_CONTORLLER);
					// 是否为指定歌曲
					String spec = mCommandInfo.getArg(2);
					if (spec == null || spec.length()==0) {
						mITTSController.syncPlay("播放规则为本地曲目");
					} else if (spec.equals("1")) {
						mITTSController.syncPlay("播放规则为指定曲目");
					} else {
						mITTSController.syncPlay("播放规则为范围曲库");
					}
				}
				
				if (mMediaList.size() > 0) {
					// if (mediaUtil.isHasMoive()) {
					// final WidgetGenericList mGenericList = new
					// WidgetGenericList(mContext);
					// mAdapter = new MediaInfoAdapter(mMediaList, mContext);
					// mGenericList.setAdapter(mAdapter);
					// mGenericList.setOnItemClickListener(new
					// OnItemClickListener() {
					// @Override
					// public void onItemClick(AdapterView<?> arg0, View arg1,
					// int arg2, long arg3) {
					// playMedia(arg2);
					// mGenericList.destory();
					// }
					// });
					// getUnion().getMainThreadUtil().pushNewWidget(mGenericList);
					//
					// } else {
					// for (int i = 0; i < mMediaList.size(); i++) {
					// LogManager.e(i + "   " + mMediaList.size() + "  " +
					// mMediaList.get(i));
					// if (mMediaList.get(i)._isFromNet && i > 0) {
					playMedia(0);
					if (ttsinfo == null) {
						getUnion().getMainThreadUtil().sendNormalWidget(ttsinfo);
					}
					// return;
					// }
					// }
					// playMedia(new Random().nextInt(mMediaList.size()));
					// getUnion().getRecogniseSystem().startCaptureVoice();
				} else {
					getUnion().getCommandEngine().handleText(mCommandInfo._question, true, false);
				}
//			}
//		}).start();
		return null;
	}

	/**
	 * 同步锁防止，最后一首歌拉歌，交叉到下一个播放命令到达，造成拉歌和新播放歌曲同时播放
	 * @param position
	 */
	private void playMedia(int position) {
		getUnion().getBaseContext().setGlobalInteger(KeyList.GKEY_PLAY_TYPE, KeyList.GKEY_PLAY_TYPE_JOKE);

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
		getUnion().getMediaInterface().setOnEndOfLoopListener(
				new OnEndOfLoopListener() {

					@Override
					public boolean onEndOfLoop() {
						String spec = mCommandInfo.getArg(2);
						// 非指定歌曲，需要重新拉歌曲
						if (spec == null || spec.length()==0) {
							// 本地歌曲
							return true;
						} else if (spec.equals("1")) {
							// 指定歌曲
							return true;
						} else {
							// 范围曲库
							
							if (KeyList.IS_TTS_DEBUG && KeyList.IS_PLAYER_DEBUG) {
								IGloableHeap gloableHeap = ((IGloableHeap) mContext
										.getApplicationContext());
								ITTSController mITTSController = (ITTSController) gloableHeap
										.getGlobalObjectMap().get(KeyList.GKEY_TTS_CONTORLLER);
								mITTSController.syncPlay("重新更新播放列表");
							}
							// 不需要欢迎词
							getUnion().getBaseContext().setGlobalBoolean(KeyList.GKEY_IS_PLAY_WELCOME, false);
							getUnion().getCommandEngine().handleText(mCommandInfo._question, true, false);
							return false;
						}
					}
				});
//		getUnion().getMediaInterface().pause();
		getUnion().getRecogniseSystem().stopWakeup();
		getUnion().getMediaInterface().setMediaInfoList(mMediaList);
		getUnion().getMediaInterface().setPlayType(2);
		getUnion().getMediaInterface().start();
//		new Thread(){
//			public void run() {
//				try {
//					sleep(1000);
//				} catch (InterruptedException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//				
//			};
//		}.start();
		

	}
}
