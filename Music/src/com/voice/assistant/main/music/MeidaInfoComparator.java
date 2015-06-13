package com.voice.assistant.main.music;

import java.util.Comparator;

public class MeidaInfoComparator implements Comparator<MediaInfo> {

	@Override
	public int compare(MediaInfo lhs, MediaInfo rhs) {
		// TODO Auto-generated method stub
		//倒序排列
		long lhsUpdateTime = lhs._updateTime;
		long rhsUpdateTime = rhs._updateTime;
		if (lhsUpdateTime > rhsUpdateTime) {
			return -1;
		} else if (lhsUpdateTime < rhsUpdateTime) {
			return 1;
		} else {
			return 0;
		}

	}

}
