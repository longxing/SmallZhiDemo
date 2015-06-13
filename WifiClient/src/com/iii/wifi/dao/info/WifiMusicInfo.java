package com.iii.wifi.dao.info;

import android.R.integer;

public class WifiMusicInfo {
    private String id;
    /**
     * 歌曲名称
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
     * 播放状态,0没有播放的歌曲1正在播放2暂停，小于0表示操作失败
     */
    private String playStatus;
    
    /**
     * 歌曲当前音量，注意只有在获取当前歌曲信息和状态的时候才设置
     */
    private int musicCurrVolume;
    /**
     * 歌曲最大音量，注意只有在获取当前歌曲信息和状态的时候才设置
     */
    private int musicMaxVolume ;
    /**
     * 分页用的
     */
    private int currPage;
    /**
     * 播放当前位置
     */
    private String cuurPosition;

	public String getCuurPosition() {
		return cuurPosition;
	}

	public void setCuurPosition(String cuurPosition) {
		this.cuurPosition = cuurPosition;
	}

	public int getMusicCurrVolume() {
        return musicCurrVolume;
    }

    public void setMusicCurrVolume(int musicCurrVolume) {
        this.musicCurrVolume = musicCurrVolume;
    }

    public int getMusicMaxVolume() {
        return musicMaxVolume;
    }

    public void setMusicMaxVolume(int musicMaxVolume) {
        this.musicMaxVolume = musicMaxVolume;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

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

    public String getPlayStatus() {
        return playStatus;
    }

    public void setPlayStatus(String playStatus) {
        this.playStatus = playStatus;
    }

	public int getCurrPage() {
		return currPage;
	}

	public void setCurrPage(int currPage) {
		this.currPage = currPage;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((musicId == null) ? 0 : musicId.hashCode());
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
		WifiMusicInfo other = (WifiMusicInfo) obj;
		if (musicId == null) {
			if (other.musicId != null)
				return false;
		} else if (!musicId.equals(other.musicId))
			return false;
		return true;
	}

private boolean _isCollected;
	
	public boolean is_isCollected() {
		return _isCollected;
	}

	public void set_isCollected(boolean _isCollected) {
		this._isCollected = _isCollected;
	}
	
}
