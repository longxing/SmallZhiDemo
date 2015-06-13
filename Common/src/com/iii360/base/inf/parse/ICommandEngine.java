package com.iii360.base.inf.parse;


public interface ICommandEngine  {

	public void handleText(String text, boolean isOnlyToServe,
			boolean isNeedShowWidget);

	public void handleText(String text);
}
