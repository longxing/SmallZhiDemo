package com.voice.assistant.main.newmusic;

import java.io.Serializable;
import java.util.Calendar;

import com.voice.assistant.main.music.MediaInfo;

/**
 * 与服务器对应的媒体数据，用于显示项
 */
public class MusicInfo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static final String MUISC_DOWNLOAD_SERVER = "http://cdnmusic.hezi.360iii.net/hezimusic/";
	public static int CATEGORY_NUMBERS = 15;
	public String mName;
	public String mSingerName;
	public String mLanguage;
	public String mID;
	public String mTimeLength;
	public String mFitTime;
	public String mFitHoliDay;
	public String mFitAge;
	public String mFitSex;
	public String mFitMood;
	public String mTags;
	public String mAlbum;
	public String mProductTime;
	public String mDownLoadUri;
	public String mDownLoadFileSize;
	public int mBaseNum = 0;
	public int mScore = 0;

	public long mPlayTime = 0;
	public long mSecondPlayTime = 0;
	
	public boolean _isCollected;

	public MusicInfo(String infos[]) {
		for (String info : infos) {
			if (info == null) {
				info = "";
			}
		}
		if (infos.length >= CATEGORY_NUMBERS) {
			mID = infos[0];
			mName = infos[1];
			mSingerName = infos[2];
			mTimeLength = infos[3];
			mLanguage = infos[4];
			mFitTime = infos[5];
			mFitHoliDay = infos[6];
			mFitAge = infos[7];
			mFitSex = infos[8];
			mFitMood = infos[9];
			mTags = infos[10];
			mAlbum = infos[11];
			mProductTime = infos[12];
			// mDownLoadUri = infos[13];
			mDownLoadUri = MUISC_DOWNLOAD_SERVER + mID + ".mp3";
			mDownLoadFileSize = infos[14];
		}
	}

	public MusicInfo(String id) {
		mID = id;
		mDownLoadUri = MUISC_DOWNLOAD_SERVER + mID + ".mp3";
	}

	public boolean isAgeFit(int age) {
		// 儿童：0-15岁 青年：15-30岁 中年：30-50岁 老年：50-80岁
		if (mFitAge == null) {
			return false;
		}
		int[] ageTag = { 0, 15, 30, 50, 80 };
		String[] tens = { "儿童", "青年", "中年", "老年" };
		String mage = "~";
		for (int i = 0; i < (ageTag.length - 1); i++) {
			if (age > ageTag[i] && age < ageTag[i + 1]) {
				mage = tens[i];
			}
		}

		return mFitAge.contains(mage);
	}

	public boolean isTimeFit() {
		// 早晨：6-9点 上午：9-11点 中午：11-13点 下午：13-18点 晚上:18-23点
		if (mFitTime == null) {
			return false;
		}
		int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
		String time = "---";
		switch (hour) {
		case 6:
		case 7:
		case 8:
			time = "早晨";
			break;
		case 9:
		case 10:
			time = "上午";
			break;
		case 11:
		case 12:
			time = "中午";
			break;
		case 13:
		case 14:
		case 15:
		case 16:
		case 17:
			time = "下午";
			break;
		case 18:
		case 19:
		case 20:
		case 21:
		case 22:
			time = "晚上";
			break;

		default:
			break;
		}

		return mFitTime.contains(time);
	}

	public boolean isMoodFit(String mood) {
		// 快乐 悲伤 轻松 睡前 幸福 兴奋 愤怒 安静
		if (mFitMood == null || mood == null) {
			return false;
		}
		return mFitMood.contains(mood);
	}

	public boolean isHolidayFit(String festval) {
		// 元旦 情人节 愚人节 清明节 劳动节 万圣节 圣诞节 母亲节 教师节 除夕
		// 父亲节 妇女节 感恩节 春节 元宵节 端午节 七夕节 中秋节 光棍节 儿童节
		// 劳动节 国庆节
		if (mFitHoliDay == null || festval == null) {
			return false;
		}
		return mFitHoliDay.contains(festval);
	}

	public boolean isSexFit(boolean sex) {
		// 男，女
		if (mFitSex == null) {
			return false;
		}
		if (sex && mFitSex.equals("男")) {
			return true;
		}
		return false;
	}
	
	public MediaInfo convertMediaInfo() {
		MediaInfo mediaInfo = new MediaInfo(mID, mDownLoadUri, "", false);
		mediaInfo._musicInfo = this;
		return mediaInfo;
	}

	@Override
	public String toString() {
		return mName + " " + mID + " " + mFitAge + (" ") + (mFitHoliDay) + (" ") + (mFitMood) + "  " + mBaseNum;
	}

}
