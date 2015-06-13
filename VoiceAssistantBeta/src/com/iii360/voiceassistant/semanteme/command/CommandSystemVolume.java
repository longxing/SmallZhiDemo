package com.iii360.voiceassistant.semanteme.command;

//20130726001-yanglin-begin
import android.content.Context;
import android.media.AudioManager;

import com.base.data.CommandInfo;
import com.iii360.base.inf.BasicServiceUnion;
import com.iii360.base.inf.parse.IVoiceCommand;

public class CommandSystemVolume extends AbstractVoiceCommand {

	private String argISIncreaseVolume = "";
	private String commandExt = "";

	public CommandSystemVolume(BasicServiceUnion union, CommandInfo commandInfo) {
		super(union, commandInfo, COMMAND_NAME_SYSTEM_VOLUME, "音量控制");
		argISIncreaseVolume = commandInfo.getArg(0);
		commandExt = commandInfo.getArg(1);
	}

	private void increaseVolume() {
		AudioManager audioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);

		if ("1".equals(commandExt)) {
			audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
					audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC), 0);
			sendAnswerSession("音量已经调到最高");
		} else {
			int current = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
			audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, current + 2, 0);
			
			sendAnswerSession("音量已经调高");

		}
	}

	private void decreaseVolume() {
		AudioManager manager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
		if ("1".equals(commandExt)) {
			manager.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0);
			sendAnswerSession("音量已经调到最低");
		} else {
			int current = manager.getStreamVolume(AudioManager.STREAM_MUSIC);
			manager.setStreamVolume(AudioManager.STREAM_MUSIC, current - 2, 0);
			sendAnswerSession("音量已经调低");
		}
	}

	@Override
	public IVoiceCommand execute() {
		super.execute();
		if ("1".equals(argISIncreaseVolume)) {
			increaseVolume();
		} else if ("0".equals(argISIncreaseVolume)) {
			decreaseVolume();
		}
		return null;
	}
}
// 20130726001-yanglin-end
