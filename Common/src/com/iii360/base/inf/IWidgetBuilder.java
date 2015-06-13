package com.iii360.base.inf;

import java.util.Map;

import android.content.Context;

public interface IWidgetBuilder {
	 public IVoiceWidget createWidget(String commandName, Context context, Map<String, Object> data) ;
}
