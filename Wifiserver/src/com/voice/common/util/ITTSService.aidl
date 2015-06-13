package  com.voice.common.util;

interface ITTSService{
	void play(String ttsPlayContent);
	void stopPlay();
	void settype(int type);
	void setLightStatusAndTime(String from,String to,boolean isOpen);
	void setWeatherStatusAndTime(String time,boolean isOpen);
	void setWeatherEnable(boolean enable);
	void setWeatherTime(String time);
	void setLightOn(boolean isOn);
	void setLightTime(String from,String to);
}