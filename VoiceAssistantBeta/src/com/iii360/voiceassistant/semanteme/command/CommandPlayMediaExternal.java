package com.iii360.voiceassistant.semanteme.command;

import java.util.HashMap;
import java.util.Random;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.inputmethodservice.Keyboard.Key;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.base.data.CommandInfo;
import com.iii360.base.common.utl.LogManager;
import com.iii360.base.inf.BasicServiceUnion;
import com.iii360.base.inf.parse.IVoiceCommand;
import com.voice.assistant.main.KeyList;
import com.voice.assistant.main.music.MediaInfo;
import com.voice.assistant.main.music.MediaInfoList;
import com.voice.assistant.main.music.MediaUtil;

public class CommandPlayMediaExternal extends AbstractVoiceCommandExternal {
	private MediaUtil mediaUtil;
	private MediaInfoList mMediaList;
	private CommandInfo mCommandInfo;
	private Context mContext;

	// public static WidgetMediaPlayer mMediaPlayer;

	public CommandPlayMediaExternal(BasicServiceUnion union, CommandInfo commandInfo) {
		super(union, "扩展播放歌曲");
		mContext = union.getBaseContext().getContext();
		mCommandInfo = commandInfo;

	}

	@Override
	public IVoiceCommand execute() {
		super.execute();
		mediaUtil = new MediaUtil(mContext);
		mediaUtil.parseCommand(mCommandInfo);
		mMediaList = mediaUtil.getList();
		new Thread(new Runnable() {

			@Override
			public void run() {

				LogManager.e("123");
				if (mMediaList.size() > 0) {
					playMedia(new Random().nextInt(mMediaList.size()));
				} else {
					// getUnion().getCommandEngine().handleText(mCommandInfo._question,
					// true, false);
					sendAnswerSession("没有找到您要的歌曲");
				}
			}
		}).start();

		return null;
	}

	private void playMedia(int position) {
		mMediaList.setCurIndex(position);

		HashMap<String, Object> date = new HashMap<String, Object>();
		date.put("list", mMediaList);

		Intent i = new Intent();
		i.setAction(KeyList.PKEY_MUSIC_EXIT);

		// CAUTION 需要保留musicplayer_layout.xml文件, 执行的时候会报错,, 或者修改这个类
		if (mMediaList.get()._isVideo) {

			// mMediaPlayer = new WidgetMoviePlayer(mContext, date);
			throw new RuntimeException();
		} else {
			// mMediaPlayer = new WidgetMusicPlayer(mContext, date);
		}
		registerReciver();
	}

	private void registerReciver() {
		// IntentFilter f = new IntentFilter();
		// f.addAction(KeyList.PKEY_MUSIC_EXIT);
		// f.addAction(KeyList.PKEY_MUSIC_PAUSE);
		// f.addAction(KeyList.PKEY_MUSIC_RESUME);
		// f.addAction(KeyList.PKEY_MUSIC_NEXT);
		// f.addAction(KeyList.PKEY_MUSIC_PRE);
		// BroadcastReceiver receiver = new BroadcastReceiver() {
		//
		// @Override
		// public void onReceive(Context context, Intent intent) {
		// if (mMediaPlayer != null) {
		// Message msg = new Message();
		// msg.arg1 = WidgetMediaPlayer.HANDLE_TYPE_ALL;
		// if (intent.getAction().equals(KeyList.PKEY_MUSIC_PAUSE)) {
		// msg.what = WidgetMediaPlayer.COMMAND_W_PAUSE;
		// mMediaPlayer.handleWidgetMsg(msg);
		// } else if (intent.getAction().equals(KeyList.PKEY_MUSIC_RESUME)) {
		// msg.what = WidgetMediaPlayer.COMMAND_W_RESUME;
		// mMediaPlayer.handleWidgetMsg(msg);
		// } else if (intent.getAction().equals(KeyList.PKEY_MUSIC_EXIT)) {
		// mMediaPlayer.destory();
		// mMediaPlayer = null;
		// } else if (intent.getAction().equals(KeyList.PKEY_MUSIC_NEXT)) {
		// msg.what = WidgetMediaPlayer.COMMAND_W_MEDIA_NEXT;
		// mMediaPlayer.handleWidgetMsg(msg);
		// } else if (intent.getAction().equals(KeyList.PKEY_MUSIC_PRE)) {
		// msg.what = WidgetMediaPlayer.COMMAND_W_MEDIA_PRE;
		// mMediaPlayer.handleWidgetMsg(msg);
		// }
		//
		// }
		//
		// }
		// };
		// mContext.registerReceiver(receiver, f);
	}

}
