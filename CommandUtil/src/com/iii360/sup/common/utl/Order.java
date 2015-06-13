package com.iii360.sup.common.utl;

public class Order {
	private int room = 0;
	private int devices = 0;
	private int order = 0;
//	private int content = 0;

	private String content;
	
	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public int getRoom() {
		return room;
	}

	public void setRoom(int room) {
		this.room = room;
	}

	public int getDevices() {
		return devices;
	}

	public void setDevices(int devices) {
		this.devices = devices;
	}

	public int getOrder() {
		return order;
	}

	public void setOrder(int order) {
		this.order = order;
	}
}
