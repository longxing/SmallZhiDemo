package com.voice.assistant.main.music;

import java.io.Serializable;

import com.iii360.base.common.utl.LogManager;
import com.voice.assistant.main.newmusic.MusicInfo;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 音频媒体文件信息 - 与文件有关
 * @author ldear
 *
 */
public class MediaInfo implements Parcelable{

	public boolean _isPlaying = true;
	public boolean _isVideo;
	public int _curPos;
	public int _size;
	public String _name;
	public String _path;
	public String _path2;
	public String _duration;
	public String _type;
	public boolean _isFromNet;
	public String _singerName;
	public String _Id;
	public long _updateTime;
	public String _collect_state;
	
	public boolean _isCollected;

	public MusicInfo _musicInfo;

	public MediaInfo(String id) {
		_Id = id;
	}
	
	public MediaInfo(String path, MusicInfo info) {
		this(path, path, "", false);
		_musicInfo = info;
	}
	
	public MediaInfo(String name, String path, String duration, boolean isVideo) {

		this(name, path, duration, isVideo, 0, false);
	}

	public MediaInfo(String name, String path, String duration, boolean isVideo, String singerName) {
		this(name, path, duration, isVideo, 0, false, singerName, name);
	}

	public MediaInfo(String name, String path, String duration, boolean isVideo, boolean isfromnet) {
		this(name, path, duration, isVideo, 0, isfromnet);
	}

	public MediaInfo(String name, String path, String duration, boolean isVideo, boolean isfromnet, String singerName) {

		this(name, path, duration, isVideo, 0, isfromnet, singerName, name);
	}

	public MediaInfo(String name, String path, String duration, boolean isVideo, boolean isfromnet, String singerName,
			String id) {
		this(name, path, duration, isVideo, 0, isfromnet, singerName, id);
	}

	public MediaInfo(String name, String path, String path2, String duration, boolean isVideo, boolean isfromnet,
			String singerName) {

		this(name, path, path2, duration, isVideo, 0, isfromnet, singerName);
	}

	public MediaInfo(String name, String path, String duration, boolean isVideo, int pos, boolean isfromNet) {
		_name = name;
		_path = path;
		_duration = duration;
		_isVideo = isVideo;
		_curPos = pos;
		_isFromNet = isfromNet;
		_Id = name;

	}

	public MediaInfo(String name, String path, String duration, boolean isVideo, int pos, boolean isfromNet,
			String singerName, String id) {
		_name = name;
		_path = path;
		_duration = duration;
		_isVideo = isVideo;
		_curPos = pos;
		_isFromNet = isfromNet;
		_singerName = singerName;
		_Id = id;
		LogManager.e(_Id);
	}

	public MediaInfo(String name, String path, String path2, String duration, boolean isVideo, int pos,
			boolean isfromNet, String singerName) {
		_name = name;
		_path = path;
		_path2 = path2;
		_duration = duration;
		_isVideo = isVideo;
		_curPos = pos;
		_isFromNet = isfromNet;
		_singerName = singerName;
		_Id = name;

	}

	public MediaInfo(String name, String path, String path2, String duration, boolean isVideo, int pos,
			boolean isfromNet, String singerName, String id) {
		_name = name;
		_path = path;
		_path2 = path2;
		_duration = duration;
		_isVideo = isVideo;
		_curPos = pos;
		_isFromNet = isfromNet;
		_singerName = singerName;
		_Id = id;

	}

	public MediaInfo(Parcel source) {
		_isPlaying = source.readByte() == 1;
		_isVideo = source.readByte() == 1;
		_isFromNet = source.readByte() == 1;
		_curPos = source.readInt();
		_size = source.readInt();
		_name = source.readString();
		_path = source.readString();
		_path2 = source.readString();
		_duration = source.readString();
		_type = source.readString();
		_singerName = source.readString();
		_Id = source.readString();

	}

	public static final Parcelable.Creator<MediaInfo> CREATOR = new Creator<MediaInfo>() {

		@Override
		public MediaInfo createFromParcel(Parcel source) {
			MediaInfo info = new MediaInfo(source);
			return info;
		}

		@Override
		public MediaInfo[] newArray(int size) {

			return new MediaInfo[size];
		}

	};

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeByte((byte) (_isPlaying ? 1 : 0));
		dest.writeByte((byte) (_isVideo ? 1 : 0));
		dest.writeByte((byte) (_isFromNet ? 1 : 0));
		dest.writeInt(_curPos);
		dest.writeInt(_size);
		dest.writeString(_name);
		dest.writeString(_path);
		dest.writeString(_path2);
		dest.writeString(_duration);
		dest.writeString(_type);
		dest.writeString(_singerName);
		dest.writeString(_Id);
	}

    @Override
    public String toString() {
        return "MediaInfo [_isPlaying=" + _isPlaying + ", _isVideo=" + _isVideo + ", _curPos=" + _curPos + ", _size=" + _size + ", _name=" + _name
                + ", _path=" + _path + ", _path2=" + _path2 + ", _duration=" + _duration + ", _type=" + _type + ", _isFromNet=" + _isFromNet
                + ", _singerName=" + _singerName + ", _Id=" + _Id + ", _musicInfo=" + _musicInfo + "]";
    }
}
// ID20120813001 zhanglin end