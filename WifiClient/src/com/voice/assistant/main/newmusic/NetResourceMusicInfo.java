package com.voice.assistant.main.newmusic;

import android.os.Parcel;
import android.os.Parcelable;

public class NetResourceMusicInfo implements Parcelable {
	/**
	 * 音频文件名称
	 */
	private String name;
	/**
	 * 歌手名
	 */
	private String author;

	/**
	 * 歌曲ID
	 */
	private String musicId;
	/**
	 * 歌曲路径
	 */
	private String musicUrl;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getMusicId() {
		return musicId;
	}

	public void setMusicId(String musicId) {
		this.musicId = musicId;
	}

	public String getMusicUrl() {
		return musicUrl;
	}

	public void setMusicUrl(String musicUrl) {
		this.musicUrl = musicUrl;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub
		dest.writeString(name);
		dest.writeString(author);
		dest.writeString(musicId);
		dest.writeString(musicUrl);
	}

	public static final Parcelable.Creator<NetResourceMusicInfo> CREATOR = new Parcelable.Creator<NetResourceMusicInfo>() {

		@Override
		public NetResourceMusicInfo createFromParcel(Parcel source) {
			// TODO Auto-generated method stub
			return new NetResourceMusicInfo(source);
		}

		@Override
		public NetResourceMusicInfo[] newArray(int size) {
			// TODO Auto-generated method stub
			return new NetResourceMusicInfo[size];
		}

	};

	private NetResourceMusicInfo(Parcel in) {
		readFromParcel(in);
	}
	public NetResourceMusicInfo(String name,String author,String musicId,String musicUrl) {
		this.name = name;
		this.author = author;
		this.musicId = musicId;
		this.musicUrl = musicUrl;
	}

	private void readFromParcel(Parcel in) {
		// TODO Auto-generated method stub
		name = in.readString();
		author = in.readString();
		musicId = in.readString();
		musicUrl = in.readString();
	}

}
