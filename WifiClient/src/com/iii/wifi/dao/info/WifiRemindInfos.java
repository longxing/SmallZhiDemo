package com.iii.wifi.dao.info;

import java.util.ArrayList;
import java.util.List;

import com.voice.common.util.Remind;


public class WifiRemindInfos {

	private String type;
	private List<Remind> remindInfos;
	private long currentTime;

	public long getCurrentTime() {
		return currentTime;
	}

	public void setCurrentTime(long currentTime) {
		this.currentTime = currentTime;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public List<Remind> getRemindInfos() {
		return remindInfos;
	}

	public void setRemindInfos(List<Remind> remindInfos) {
		this.remindInfos = remindInfos;
	}

	public void addRemind(Remind r) {
		if (remindInfos == null) {
			remindInfos = new ArrayList<Remind>();
		}
		remindInfos.add(r);
	}

}
