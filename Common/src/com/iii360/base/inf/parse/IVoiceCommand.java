package com.iii360.base.inf.parse;


public interface IVoiceCommand {
	public static final String COMMAND_HEAD = "com.iii360.voiceassistant.semanteme.command.";
	public static final String COMMAND_NAME_QUESTION = "CommandQuestion";
	public static final String COMMAND_NAME_ANSWER = "CommandAnswer";
	public static final String COMMAND_NAME_BASE = "VoiceCommand";
	public static final String COMMAND_NAME_CHAT = "CommandChat";
	public static final String COMMAND_NAME_CHATMODE = "CommandChatMode";
	public static final String COMMAND_NAME_MEDIA_CONTROL = "CommandMediaControl";
	public static final String COMMAND_NAME_HANDLE_ERR = "CommandHandleError";
	public static final String COMMAND_PLAYMEDIA_HEZI_NULL = "CommandPlayMediaHeziNull";
	public static final String COMMAND_NAME_WEATHER = "CommandQueryWeather";
	public static final String COMMAND_NAME_REMIND = "CommandRemind";
	public static final String COMMAND_NAME_TRANSLATION = "CommandTranslation";
	public static final String COMMAND_NAME_STUDY = "CommandStudy";
	public static final String COMMAND_NAME_PLAY_MEDIA = "CommandPlayMedia";
	public static final String COMMAND_NAME_CONFIRM = "CommandConfirm";
	public static final String COMMAND_NAME_SYSTEM = "CommandSystem";
	public static final String COMMAND_NAME_SYSTEM_VOLUME = "CommandSystemVolume";
	public static final String COMMAND_NAME_SYSTEM_REMIND = "CommandSystemRemind";

	public static final String COMMAND_CONTIU_OPERITE = "CommandContiuOperite";
	public static final String COMMAND_SYSTEM_INFO = "CommandSystemInfo";

	public IVoiceCommand execute();

	public void release();
}
