package com.voice.assistant.main.music;

import java.util.ArrayList;
import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;

public class MediaInfoList implements Parcelable {
	private ArrayList<MediaInfo> mFileList;

	private int mCurIndex;

	public static int PLAY_MODE = IMediaPlayerInterface.PLAY_MODE_SIGNAL;

	private boolean isFirstPull = true;

	public boolean isFirstPull() {
		return isFirstPull;
	}

	public void setFirstPull(boolean isFirstPull) {
		this.isFirstPull = isFirstPull;
	}

	public void setPlayMode(int playMode) {
		PLAY_MODE = playMode;
	}

	public int getPlayMode() {
		return PLAY_MODE;
	}

	public MediaInfoList() {
		mFileList = new ArrayList<MediaInfo>();
		mCurIndex = 0;
	}

	public void dump(int startIndex, int endIndex) {
		if (mFileList != null && mFileList.size() > startIndex) {
			ArrayList<MediaInfo> temp = new ArrayList<MediaInfo>();
			for (int i = startIndex; i < endIndex; i++) {
				temp.add(mFileList.get(i));
			}
			mFileList.clear();
			mCurIndex -= startIndex;
			mFileList = temp;
		}
	}

	public boolean isLast() {
		return mFileList == null || mCurIndex == mFileList.size() - 1;
	}

	public int size() {
		return mFileList.size();
	}

	public void addIndex() {
		mCurIndex++;
	}

	public void setCurIndex(int index) {
		mCurIndex = index;
	}

	public void add(MediaInfo info) {
		mFileList.add(info);
	}

	public void addAll(List<MediaInfo> minfoList) {
		mFileList.clear();
		mFileList.addAll(minfoList);
	}

	public void remove(int index) {
		if (index >= 0 && index < mFileList.size()) {
			mFileList.remove(index);
			if (mCurIndex > index) {
				mCurIndex--;
			}
		}

	}

	public ArrayList<MediaInfo> getAll() {
		return mFileList;
	}

	public void remove(MediaInfo info) {
		mFileList.remove(info);
	}

	public boolean hasItem() {
		return mFileList != null && mCurIndex < mFileList.size() && mCurIndex >= 0; 
	}

	// ID20120831005 zhanglin end
	public MediaInfo get() {
		if (mFileList != null && mFileList.size() > 0) {
			if (mFileList.size() > mCurIndex) {
				return mFileList.get(mCurIndex);
			} else {
				return mFileList.get(0);
			}

		}
		return null;
	}

	// ID20130114001 zhanglin end
	// ID20120629001 zhanglin end
	public int getIndex() {
		return mCurIndex;
	}

	public MediaInfo get(int index) {
		return mFileList.get(index);
	}

	// -1为没有上一首
	public int seekPrev() {
		int index = mCurIndex - 1;
		switch (PLAY_MODE) {
		case IMediaPlayerInterface.PLAY_MODE_SIGNAL:// 单曲一次
			return 0;
		case IMediaPlayerInterface.PLAY_MODE_ONLINE:
		case IMediaPlayerInterface.PLAY_MODE_ALL:// 单轮一次
			if (mFileList == null || index < 0) {
				return 0;
			} else if (index >= mFileList.size()) {
				return mFileList.size()-1;
			} else {
				return index;
			}
		case IMediaPlayerInterface.PLAY_MODE_LOOPSIGNAL:// 单曲循环
			return mCurIndex;
		case IMediaPlayerInterface.PLAY_MODE_LOOPALL:// 播放列表循环
			if (mFileList != null && mFileList.size() > 0) {
				if (index >= mFileList.size()||index<0) {// 最后一首歌时，重新播放,第一首歌时指定播放第一首
					return 0;
				}else {// 过程中的歌曲
					return index; 
				}
			}
			break;
		}
		return -1;
	}

	public MediaInfo getPrev() {
		MediaInfo info = null;
		int seekPrev = seekPrev();
		if (seekPrev >= 0) {
			mCurIndex = seekPrev;
			info = mFileList.get(mCurIndex);
		}

		return info;
	}

	// -1为没有下一首
	public int seekNext() {
		int index = mCurIndex + 1;
		switch (PLAY_MODE) {
		case IMediaPlayerInterface.PLAY_MODE_SIGNAL:// 单曲一次
			return 0;
		case IMediaPlayerInterface.PLAY_MODE_ONLINE:
		case IMediaPlayerInterface.PLAY_MODE_ALL:// 单轮一次
			if (mFileList != null && index < mFileList.size() && index >= 0) {
				return index;
			}
			break;
		case IMediaPlayerInterface.PLAY_MODE_LOOPSIGNAL:// 单曲循环
			return mCurIndex;
		case IMediaPlayerInterface.PLAY_MODE_LOOPALL:// 播放列表循环
			if (mFileList != null && mFileList.size() > 0) {
				if (index >= mFileList.size()) {// 最后一首歌时，重新播放
					return 0;
				} else {// 过程中的歌曲
					return index;
				}
			}
			break;
		}

		return -1;
	}

	public MediaInfo getNext() {
		MediaInfo info = null;
		int seekNext = seekNext();
		if (seekNext >= 0) {
			mCurIndex = seekNext;
			info = mFileList.get(mCurIndex);
		}

		return info;
	}
	
	// 判断当前列表是否包含此id的歌曲
	public int isContainId(String id) {
		if(mFileList!=null && mFileList.size()>0){
			for (int i =0; i<mFileList.size() ; i++) {
				if(mFileList.get(i)._Id.equals(id)){
					return i;
				}
			}
		}
		return -1;
	}
	

	@SuppressWarnings("unchecked")
	public MediaInfoList(Parcel source) {
		mFileList = source.readArrayList(MediaInfo.class.getClassLoader());
		mCurIndex = source.readInt();
		PLAY_MODE = source.readInt();
	}

	public static final Parcelable.Creator<MediaInfoList> CREATOR = new Creator<MediaInfoList>() {

		@Override
		public MediaInfoList createFromParcel(Parcel source) {
			MediaInfoList list = new MediaInfoList(source);
			return list;
		}

		@Override
		public MediaInfoList[] newArray(int size) {
			// TODO Auto-generated method stub
			return null;
		}

	};

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeList(mFileList);
		dest.writeInt(mCurIndex);
		dest.writeInt(PLAY_MODE);
	}

	public void clear() {
		mFileList.clear();
		mCurIndex = 0;
	}
}
