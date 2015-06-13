package com.iii360.box.ximalaya;

public class XimalayaAudio {
	private int id;
	private String title;
	private String nickName;
	private String AudioUrl32;
	private String AudioUrl64;
	private int page;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	public String getAudioUrl32() {
		return AudioUrl32;
	}

	public void setAudioUrl32(String audioUrl32) {
		AudioUrl32 = audioUrl32;
	}

	public String getAudioUrl64() {
		return AudioUrl64;
	}

	public void setAudioUrl64(String audioUrl64) {
		AudioUrl64 = audioUrl64;
	}

	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		XimalayaAudio other = (XimalayaAudio) obj;
		if (id != other.id)
			return false;
		return true;
	}

}
