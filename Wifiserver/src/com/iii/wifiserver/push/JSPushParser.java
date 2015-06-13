package com.iii.wifiserver.push;

import java.util.Date;

import android.content.Context;
import android.text.TextUtils;

import com.iii.wifi.util.KeyList;
import com.iii.wifi.util.TTSUtil;
import com.iii.wifi.util.upLoadFileUtils;
import com.iii360.sup.common.utl.LogManager;
import com.smallzhi.push.entity.Message;
import com.smallzhi.push.manager.PushManager;
import com.smallzhi.push.util.PushConstant;
import com.voice.assistant.main.newmusic.MusicData;

public class JSPushParser {

	public static void excute(Context context, Message msg) {
		String action = msg.getAction();

		if (TextUtils.isEmpty(action)) {
			return;
		}

		if (PushConstant.ACTION_TTS.equals(action)) {

			if (KeyList.TTSUtil == null) {
				KeyList.TTSUtil = new TTSUtil(context);
			}

			KeyList.TTSUtil.playContent(msg.getContent());

		} else if (PushConstant.ACTION_MUSIC.equals(action)) {
			if (KeyList.sMusicData == null) {
				KeyList.sMusicData = new MusicData(context);
			}
			KeyList.sMusicData.playOrPause();
		} else if (PushConstant.ACTION_LOG.equals(action)) {
			LogManager.e("JSPushParser  push  log to server");
			msg.setType(PushConstant.TYPE_RESPONSE);
			msg.setTimestamp(new Date());
			msg.setSender(msg.getReceiver());
			msg.setStatus(PushConstant.STATUS_SUCCESS);
			PushManager.sendRequest(context, msg);
			upLoadFileUtils.sendLogToServer();
//			new Thread() {
//				public void run() {
//					upLoadFileUtils.sendLogToServerByHttp();
//				};
//			}.start();
			
		}

	}
}
