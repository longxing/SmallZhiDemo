package com.voice.assistant.main.music;

import android.content.Context;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;

import com.iii360.base.common.utl.LogManager;
import com.voice.assistant.main.music.MediaPlayerService.OnBufferingOverdueListener;

public class MusicPlayer implements IMediaPlayerInterface {

	private Context mContext;

	private MediaPlayerService mMusicControlService;

	// 初始化
	public MusicPlayer(Context context) {
		mContext = context;
		mMusicControlService = MediaPlayerService.getInstance(mContext);
	}

	// ****************操作命令****************** //	
	// 设置歌单
	@Override
	public void setMediaInfo(final MediaInfo info) {
		String path = info._path;
		mMusicControlService.setMediaInfoList(path);
	}

	@Override
	public void seekTo(final int pos) {
		mMusicControlService.seekTo(pos);
	}

	// 开始播放
	@Override
	public void start() {
		try {
			mMusicControlService.start();
		} catch (Exception e) {
			LogManager.printStackTrace(e);
		}

	}
	
	@Override
	public void simpleStart() {
		try {
			mMusicControlService.simpleStart();
		} catch (Exception e) {
			LogManager.printStackTrace(e);
		}
	}
	
	// 暂停
	@Override
	public void pause() {
		try {
			mMusicControlService.pause();
		} catch (Exception e) {
			LogManager.printStackTrace(e);
		}

	}

	@Override
	public void simplePause() {
		try {
			mMusicControlService.simplePause();
		} catch (Exception e) {
			LogManager.printStackTrace(e);
		}
	}


	@Override
	public void release() {
		if (mMusicControlService != null) {
			mMusicControlService.release();
		}
	}

	// ****************监听回调****************** //
	@Override
	public void setOnPreparedListener(OnPreparedListener l) {
		mMusicControlService.setOnPreparedListener(l);
	}

	@Override
	public void setOnCompletionListener(OnCompletionListener l) {
		mMusicControlService.setOnCompletionListener(l);
	}

	@Override
	public void setOnErrorListener(OnErrorListener l) {
		mMusicControlService.setOnErrorListener(l);
	}

	// ****************状态位查询****************** //
	@Override
	public int getDuration() {
		return mMusicControlService.getDuration();
	}

	@Override
	public int getCurrentPosition() {
		return mMusicControlService.getCurrentPosition();
	}

	@Override
	public boolean isPlaying() {
		if (mMusicControlService != null) {
			return mMusicControlService.isPlaying();
		} else {
			return false;
		}
	}

	@Override
	public int getBufferPercentage() {
		return mMusicControlService.getBufferPercentage();
	}

	@Override
	public boolean canPause() {
		return mMusicControlService.canPause();
	}

	@Override
	public boolean canSeekBackward() {
		return mMusicControlService.canSeekBackward();
	}

	@Override
	public boolean canSeekForward() {
		return mMusicControlService.canSeekForward();
	}

	@Override
	public int getDownPer() {
		return mMusicControlService.getBufferPercentage();
	}

	@Override
	public void setOnBufferingOverdueListener(OnBufferingOverdueListener onBufferingOverdueListener) {
		mMusicControlService.setOnBufferingOverdueListener(onBufferingOverdueListener);
	}

	@Override
	public void setPreMediaInfoList(String path) {
//		mMusicControlService.setPreMediaInfoList(path);
	}

	@Override
	public MediaPlayerService getMediaPlayerService() {
		return mMusicControlService;
	}

}
