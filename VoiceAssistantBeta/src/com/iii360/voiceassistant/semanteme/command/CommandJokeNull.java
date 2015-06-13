package com.iii360.voiceassistant.semanteme.command;

import android.content.Context;

import com.base.data.CommandInfo;
import com.iii360.base.common.utl.LogManager;
import com.iii360.base.inf.BasicServiceUnion;
import com.iii360.base.inf.ITTSController.ITTSStateListener;
import com.iii360.base.inf.parse.IVoiceCommand;

public class CommandJokeNull extends AbstractVoiceCommand {

	public CommandJokeNull(BasicServiceUnion union, CommandInfo commandInfo) {
		super(union, commandInfo, COMMAND_PLAYMEDIA_HEZI_NULL, "没找到笑话");
		// TODO Auto-generated constructor stub
	}

	@Override
	public IVoiceCommand execute() {
		super.execute();
		// TODO Auto-generated method stub
		getUnion().getTTSController().play("不好意思，没有找到这个笑话，将会随机帮您找个笑话");
		getUnion().getTTSController().setListener(new ITTSStateListener() {

			@Override
			public void onStart() {
				// TODO Auto-generated method stub

			}

			@Override
			public void onInit() {
				// TODO Auto-generated method stub

			}

			@Override
			public void onError() {
				// TODO Auto-generated method stub

			}

			@Override
			public void onEnd() {
				// TODO Auto-generated method stub
				LogManager.e("test");
				getUnion().getCommandEngine().handleText("笑话");
				getUnion().getTTSController().setListener(null);
			}
		});
		return null;
	}
}
