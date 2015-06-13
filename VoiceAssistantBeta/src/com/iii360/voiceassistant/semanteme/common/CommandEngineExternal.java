package com.iii360.voiceassistant.semanteme.common;

import android.content.Context;
import android.content.Intent;

import com.base.data.CommandInfo;
import com.base.platform.OnDataReceivedListener;
import com.iii360.base.common.utl.LogManager;
import com.iii360.base.inf.BasicServiceUnion;
import com.voice.assistant.main.AssistantMainActivity;

public final class CommandEngineExternal extends CommandEngine {

	OnDataReceivedListener mDataReceivedListener;

	public CommandEngineExternal(BasicServiceUnion union) {
		super(union);

		// TODO Auto-generated constructor stub
		mDataReceivedListener = new OnDataReceivedListener() {

			@Override
			public void onError(int errorCode) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onDataReceived(CommandInfo commandInfo) {
				// TODO Auto-generated method stub
				LogManager.i("commandInfo is " + commandInfo);
				String souceCommandName = commandInfo._commandName;
				commandInfo._commandName += "External";
				try {
					executeUseCommandInfo(commandInfo, new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
							throw new RuntimeException("CommandIsNull!!!");
						}
					});
				} catch (RuntimeException e) {
					commandInfo._commandName = souceCommandName;
					sendToMain(mContext, commandInfo._question);
				}
			}
		};
		setOnDataReceivedListener(mDataReceivedListener);
	}


	@Override
	public void handleText(String text) {
		// TODO Auto-generated method stub
		handleText(text, false, false);
	}

	private void sendToMain(Context context, String text) {
		Intent intent = new Intent(context, AssistantMainActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.putExtra("value", text);
		context.startActivity(intent);
	}

	@Override
	protected void sendAnswerSession(String string) {
		// TODO Auto-generated method stub
		// super.sendAnswerSession(string);
	}
}
