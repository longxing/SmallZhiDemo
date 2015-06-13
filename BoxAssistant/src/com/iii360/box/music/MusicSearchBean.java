package com.iii360.box.music;

public class MusicSearchBean {
	private int id;
	private String message;
	private String url;
	private long createTime;
	public static final String TABLE_NAME = "music_search_history_table";
	public static final String ID_COLUMN_NAME = "_id";
	public static final String MESSAGE_COLUMN_NAME = "msg";
	public static final String URL_COLUMN_NAME = "msg_url";
	public static final String CREATE_TIME_COLUMN_NAME="create_time";
	private int page;
	public MusicSearchBean() {
	}

	public MusicSearchBean(String message, String url,long createTime) {
		super();
		this.message = message;
		this.url = url;
		this.createTime = createTime;
	}

	public MusicSearchBean(int id, String message, String url,long createTime) {
		super();
		this.id = id;
		this.message = message;
		this.url = url;
		this.createTime=createTime;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public long getCreateTime() {
		return createTime;
	}

	public void setCreateTime(long createTime) {
		this.createTime = createTime;
	}

	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	@Override
	public String toString() {
		return "url:"+url+"--page:"+page+"------";
	}
	
}
