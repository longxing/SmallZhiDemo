package com.iii.wifi.dao.info;

public class WifiLedTimeInfo {
	private String ledTimeName;
	private boolean isOpen = false;

	public String getLedName() {
		return ledTimeName;
	}

	public void setLedName(String ledTimeName) {
		this.ledTimeName = ledTimeName;
	}

	public boolean isOpen() {
		return isOpen;
	}

	public void setOpen(boolean isOpen) {
		this.isOpen = isOpen;
	}

}
