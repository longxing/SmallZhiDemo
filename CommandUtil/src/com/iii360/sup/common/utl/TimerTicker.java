package com.iii360.sup.common.utl;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

import com.iii360.sup.common.utl.ITimeUnit;

public class TimerTicker implements Serializable, ITimeUnit {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public boolean repeatFlag = false;
	public int repeatDistance = 1;
	public int repeatType = 0;
	public Long BaseTime;

	public boolean avalibeFlag = false;
	public int avalibeFrom = 0;
	public int avalibeTo = 0;
	public Date time;

	public TimerTicker(long time, boolean repeatFlag, int repeatDistance, int repeatType) {
		this.BaseTime = time;
		this.repeatFlag = repeatFlag;
		this.repeatDistance = repeatDistance;
		this.repeatType = repeatType;

	}

	public void setAvalibe(boolean avalibeFlag, int avalibFrom, int avalibTo) {
		this.avalibeFlag = avalibeFlag;
		this.avalibeFrom = avalibFrom;
		this.avalibeTo = avalibTo;
	}

	@Override
	public long getRunTime() {
		// TODO Auto-generated method stub
		Calendar changeCale = Calendar.getInstance();
		changeCale.setTimeInMillis(BaseTime);
		time = changeCale.getTime();
		if (repeatFlag) {
			while ((time.getTime() - System.currentTimeMillis()) <= 2) {

				changeCale.add(repeatType, repeatDistance);
				if (avalibeFlag) {
					int weekday = changeCale.get(Calendar.DAY_OF_WEEK);
					weekday--;
					if (weekday == 0) {
						weekday = 7;
					}
					if (weekday < avalibeFrom || weekday > avalibeTo) {
						LogManager.e("continue");
						continue;
					}
				}

				time = changeCale.getTime();
			}
			return changeCale.getTime().getTime();
		}

		return BaseTime;
	}

	@Override
	public boolean getReapteFlag() {
		// TODO Auto-generated method stub
		return repeatFlag;
	}

}
