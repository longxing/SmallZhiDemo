package com.iii360.voiceassistant.semanteme.command;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import android.os.Environment;

import com.base.data.CommandInfo;
import com.iii360.base.common.utl.KeyList;
import com.iii360.base.inf.BasicServiceUnion;
import com.iii360.base.inf.parse.IVoiceCommand;
import com.iii360.sup.common.utl.NetWorkUtil;
import com.iii360.sup.common.utl.net.WifiAdmin;
import com.smallzhi.TTS.Util.TranslateUtil;

public class CommandSystemInfo extends AbstractVoiceCommand {

	private CommandInfo mCommandInfo;

	public CommandSystemInfo(BasicServiceUnion union, CommandInfo commandInfo) {
		super(union, commandInfo, IVoiceCommand.COMMAND_SYSTEM_INFO, "系统");
		// TODO Auto-generated constructor stub
		mCommandInfo = commandInfo;

	}

	@Override
	public IVoiceCommand execute() {
		super.execute();
		// TODO Auto-generated method stub
		String content = mCommandInfo._question;
		WifiAdmin admin = WifiAdmin.getInstance(mContext);
		String anser = null;

		if (content.equals("当前网络")) {
			anser = admin.getWifiInfo().getSSID();
		} else if (content.equals("物理地址")) {
			anser = TranslateUtil.original(NetWorkUtil.getLocalMacAddress(mContext));
		} else if (content.equalsIgnoreCase("IP地址")) {
			anser = TranslateUtil.original(NetWorkUtil.getLocalIpAddress());

		} else if (content.equals("调试环境")) {
			KeyList.IS_TTS_DEBUG = true;
			anser = "当前为调试环境";
		} else if (content.equals("正式环境")) {
			KeyList.IS_TTS_DEBUG = false;
			anser = "当前为正式环境";
		} else if (content.equals("当前版本")) {
			anser = "当前版本为1.0.0.0";
			//
			File file = new File(Environment.getExternalStorageDirectory().getPath() + "/upgrade.properties");
			FileInputStream in = null;
			if (file.exists()) {
				try {
					in = new FileInputStream(file);
					Properties properties = new Properties();
					properties.load(in);
					anser = "当前版本为" + properties.getProperty("version");
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					try {
						in.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}

			}
			anser = TranslateUtil.original(anser);
		} else if (content.equals("播放器调试")) {
			if (KeyList.IS_TTS_DEBUG) {
				KeyList.IS_PLAYER_DEBUG = true;
				anser = "播放器调试开启";
			} else {
				anser = "你需要先开启调试环境";
			}
		} else if (content.equals("识别录音调试")) {
			if (KeyList.IS_TTS_DEBUG) {
				KeyList.IS_RECO_DEBUG = true;
				anser = "识别录音调试开启";
			} else {
				anser = "你需要先开启调试环境";
			}
		} else if (content.equals("音乐唤醒调试")) {
			KeyList.IS_WAKEUP_IN_PLAYING_DEBUG = true;
			anser = "音乐唤醒模式";
		}
		getUnion().getMainThreadUtil().sendNormalWidget(anser);
		return null;
	}

}
