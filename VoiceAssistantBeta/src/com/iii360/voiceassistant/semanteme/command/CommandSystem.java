package com.iii360.voiceassistant.semanteme.command;

import android.content.Intent;
import android.os.Handler;

import com.base.data.CommandInfo;
import com.iii360.base.common.utl.BaseContext;
import com.iii360.base.common.utl.LogManager;
import com.iii360.base.inf.BasicServiceUnion;
import com.iii360.base.inf.parse.IVoiceCommand;
import com.voice.assistant.main.KeyList;
import com.voice.assistant.utl.AirPlayMusicController;
import com.voice.assistant.utl.DlanMusicController;

public class CommandSystem extends AbstractVoiceCommand {
	private BaseContext mBaseContext = null;

	private String[] answers = { "一见倾城，再见倾国", "主人再见", "主人拜拜", "也许放弃，才能靠近你；不再见你，你才会把我记起", "人生若只如初见，又何苦伤离别",
			"伤离别，离别虽然在眼前，说再见，再见不会太遥远", "再见就是我们给彼此最好的纪念", "小别胜新婚", "我先退下了，主人保重", "晚安主人，晚安，所有孤独的人们", "那我去休息了" };

	public CommandSystem(BasicServiceUnion union , CommandInfo commandInfo) {
		super(union, commandInfo, COMMAND_NAME_SYSTEM, "系统控制");
	}

	private void closeVoiceAssistant() {
		mBaseContext = new BaseContext(mContext);
		boolean hasVoice = mBaseContext.getPrefBoolean(
				KeyList.PKEY_ASS_HAS_VOICE, true);
		if (!hasVoice) {
			sendCloseAction() ;
		} else {
			sendAnswerSession("再見");
			new Handler().postDelayed(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					sendCloseAction() ;
				}
			}, 2000);
			
		}
	}

	@Override
	public IVoiceCommand execute() {
		super.execute();
//		getUnion().getTTSController().play(answers[new Random().nextInt(answers.length)]);
		getUnion().getTTSController().play("");
		
		LogManager.e("onReceive, readly release");
		//Fixme This is fixed the bug when shut down the xiaozhi, the music cannot be played again.
		getUnion().getMediaInterface().pause();		
		new DlanMusicController(mContext).stop();
        AirPlayMusicController airPlayMusicController = new AirPlayMusicController(mContext);
        airPlayMusicController.stop();
        this.getUnion().getRecogniseSystem().startWakeup();
		
		return null;
	}
	
	private void sendCloseAction()  {
		LogManager.e("onEnd");
		Intent intent = new Intent();
		intent.setAction(KeyList.AKEY_SYS_CLOSE_ACTION);
		mContext.sendBroadcast(intent);
	}
}
