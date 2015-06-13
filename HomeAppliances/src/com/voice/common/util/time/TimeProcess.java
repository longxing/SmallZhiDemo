package com.voice.common.util.time;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.content.Context;

public class TimeProcess {
	private long halfday = 12 * 3600 * 1000;
	private TimeNormalizer normalizer;
	final String[] Distancehanzi = { "年", "月", "天", "个小时", "分钟", "秒钟" };
	final int[] TimeStep = { 0, 12, 30, 24, 60, 60 };
	int leves[] = { Calendar.YEAR, Calendar.MONTH, Calendar.DAY_OF_MONTH, Calendar.HOUR_OF_DAY, Calendar.MINUTE,
			Calendar.SECOND };
	/**
	 * 月份是从0开始 ，所以加上1来保证月份从1开始
	 */
	int distance[] = { 0, 1, 0, 0, 0, 0 };

	public TimeProcess(Context context) {
		normalizer = new TimeNormalizer(context);
	}

	/**
	 * 获得文字中包含的时间信息，
	 * 
	 * @param text
	 * @return
	 */
	public TimeUnit handText(String text) {
		TimeUnit[] timeUnits = normalizer.parse(text);
		System.out.println(timeUnits.length);
		if (timeUnits.length > 0) {
			return timeUnits[0];
		}
		return null;
	}

	public boolean isTimeBehinde(TimeUnit timeUnit) {
		long offset = timeUnit.getRunTime() - System.currentTimeMillis();
		if (offset < 0) {
			return false;
		}
		return true;
	}

	/**
	 * 生成正式的提醒时间
	 * 
	 * @param timeUnit
	 * @return
	 */
	public String getTimeText(TimeUnit timeUnit) {

		int i = 0;
		int start = 99;
		int end = 0;
		String confirm = "";
		Calendar currentCale = Calendar.getInstance();
		Calendar OriginCale = Calendar.getInstance();
		OriginCale.setTimeInMillis(timeUnit.getRunTime());
		int times[] = new int[6];
		String[] time_grid = new String[6];
		time_grid = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(Calendar.getInstance().getTime()).split("-");
		int[] ini = new int[6];
		for (i = 0; i < 6; i++)
			ini[i] = Integer.parseInt(time_grid[i]);

		for (i = 0; i < 6; i++) {
			int z = OriginCale.get(leves[i]) - currentCale.get(leves[i]);
			if (z != 0) {
				times[i] = 1;
				ini[i] = OriginCale.get(leves[i]) + distance[i];
				if (i > 0) {
					times[i - 1] = 1;
					ini[i - 1] = OriginCale.get(leves[i - 1]) + distance[i - 1];
				}
			}
		}
		for (i = 0; i < times.length; i++) {
			if (times[i] != 0) {
				if (i < start) {
					start = i;
				}
				if (i > end) {
					end = i;
				}
			}
		}

		for (i = start; i <= end; i++) {
			confirm += (ini[i] + timeUnit.hanzi[i]);
		}
		return confirm;
	}

	/**
	 * 获得用户说的提醒时间，
	 * 
	 * @return
	 */
	public String getSaysTime(TimeUnit timeUnit) {
		String saysTime = timeUnit.Time_Expression;
		return saysTime;
	}

	/**
	 * 获得执行时间到当前时间的剩余时间
	 * 
	 * @return
	 */
	public String getDistanceTime(TimeUnit timeUnit) {
		Calendar currentCale = Calendar.getInstance();
		Calendar OriginCale = Calendar.getInstance();

		String confirm = "";
		OriginCale.setTimeInMillis(timeUnit.getRunTime());
		int times[] = new int[6];

		for (int i = 0; i < 6; i++) {
			int z = OriginCale.get(leves[i]) - currentCale.get(leves[i]);
			if (z != 0) {
				times[i] = z;

			}
		}
		for (int i = 5; i >= 1; i--) {
			if (times[i] < 0) {
				times[i - 1]--;
				times[i] += TimeStep[i];
			}
		}
		

		for (int i = 0; i < 6; i++) {
			if (times[i] < 0) {
				return "小于当前时间";
			}
			if (times[i] > 0)
				confirm += (times[i] + Distancehanzi[i]);
		}

		//
		return confirm;
	}
}
