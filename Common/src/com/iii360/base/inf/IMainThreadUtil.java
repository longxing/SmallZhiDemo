package com.iii360.base.inf;

/**
 * 把一些需要在主线程中跑的代码仍到这里来，就不用写那么多postRunable 了
 * 
 * @author jushang
 * 
 */
public interface IMainThreadUtil {

	public void setCurrentUnion(BasicServiceUnion union);

	/**
	 * 往界面上发送一个聊天回复的widget ， 有播报的
	 * 
	 * @param content
	 *            显示的文字内容
	 */
	public void sendNormalWidget(String content);

	/**
	 * 往界面上发送一个问题的widget ， 有播报的
	 * 
	 * @param content
	 *            显示的文字内容
	 */
	public void sendQuestionWidget(String content);

	/**
	 * 推送一个自定义的widget
	 * 
	 * @param wiget
	 */
	public void pushNewWidget(IVoiceWidget wiget);

}
