package com.iii360.box.remind;

import java.util.Calendar;

import com.iii360.box.util.NumberFormatUtils;
import com.iii360.box.util.TimeUtil;
import com.voice.common.util.Remind;

public class RemindDataHelp {
	/**
	 * @param milliseconds
	 * @return 时间格式：2014/4
	 */
	public String getYearAndMonth(long milliseconds) {
		StringBuffer buffer = new StringBuffer(TimeUtil.getYear(milliseconds));
		buffer.append("/");
		buffer.append(TimeUtil.getMonth(milliseconds));
		return buffer.toString();
	}

	public final static int REMIND_TYPE_EVERYDAY = 0;
	public final static int REMIND_TYPE_EVERYMONTH = 9;
	public final static int REMIND_TYPE_EVERYYEAR = 10;
	public final static int REMIND_TYPE_EVERYWEEK = 11;

	public final static int REMIND_TYPE_INTERVAL_DAY = 1;
	public final static int REMIND_TYPE_INTERVAL_WEEK = 7;
	public final static int REMIND_TYPE_INTERVAL_MINUTE = 4;
	public final static int REMIND_TYPE_INTERVAL_MONTH = 5;
	public final static int REMIND_TYPE_INTERVAL_YEAR = 6;

	public final static int REMIND_TYPE_DAY_TO_DAY = 2;
	public final static int REMIND_TYPE_ONCE = 3;

	/**
	 * @param remind
	 * @return 备忘类型
	 */
	public int getRemindType(Remind remind) {

		if (remind.avalibeFlag) {
			// 周几～周几
			return REMIND_TYPE_DAY_TO_DAY;

		} else if (remind.repeatFlag) {

			if (remind.repeatDistance == 1) {

				int event = REMIND_TYPE_EVERYDAY;

				switch (remind.repeatType) {

				case Calendar.DAY_OF_MONTH:
					// 每天
					event = REMIND_TYPE_EVERYDAY;

					break;

				case Calendar.WEEK_OF_YEAR:
					// 每周
					event = REMIND_TYPE_EVERYWEEK;
					break;

				case Calendar.MONTH:
					// 每月
					event = REMIND_TYPE_EVERYMONTH;
					break;

				case Calendar.YEAR:
					// 每年
					event = REMIND_TYPE_EVERYYEAR;
					break;
				case Calendar.MINUTE:
					event = REMIND_TYPE_INTERVAL_MINUTE;
				default:
					break;
				}

				return event;

			} else {

				int t = REMIND_TYPE_INTERVAL_MINUTE;

				switch (remind.repeatType) {

				case Calendar.MINUTE:
					// 每隔2分钟
					t = REMIND_TYPE_INTERVAL_MINUTE;
					break;

				case Calendar.DAY_OF_MONTH:
					// 每隔2天
					t = REMIND_TYPE_INTERVAL_DAY;
					break;

				case Calendar.WEEK_OF_YEAR:
					// 每周几提醒我
					t = REMIND_TYPE_INTERVAL_WEEK;
					break;

				case Calendar.MONTH:
					// 每隔2个月
					t = REMIND_TYPE_INTERVAL_MONTH;

					break;
				case Calendar.YEAR:
					// 每隔2年
					t = REMIND_TYPE_INTERVAL_YEAR;

					break;
				default:
					break;
				}
				// 每隔几天
				return t;
			}

		} else {
			// 明天/后天/一次的
			return REMIND_TYPE_ONCE;
		}
	}

	private StringBuffer mBuffer;

	/**
	 * @param type
	 * @param remind
	 * @return 显示备忘具体时间
	 */
	public String getShowTime(int type, Remind remind) {
		mBuffer = new StringBuffer();

		switch (type) {

		case REMIND_TYPE_EVERYDAY:
			mBuffer.append("每天");

			break;

		case REMIND_TYPE_EVERYWEEK:
			mBuffer.append("每周 ");
			mBuffer.append(TimeUtil.getWeek(remind.BaseTime));

			break;

		case REMIND_TYPE_EVERYMONTH:
			mBuffer.append("每月 ");
			mBuffer.append(TimeUtil.getDay(remind.BaseTime));
			mBuffer.append("日 ");

			break;

		case REMIND_TYPE_EVERYYEAR:
			mBuffer.append("每年 ");
			mBuffer.append(TimeUtil.getMonth(remind.BaseTime));
			mBuffer.append("/");
			mBuffer.append(TimeUtil.getDay(remind.BaseTime));

			break;

		case REMIND_TYPE_INTERVAL_MINUTE:

			mBuffer.append("每");
			mBuffer.append(remind.repeatDistance);
			mBuffer.append("分钟");

			break;

		case REMIND_TYPE_INTERVAL_DAY:

			mBuffer.append("每");
			mBuffer.append(remind.repeatDistance);
			mBuffer.append("天");

			break;

		case REMIND_TYPE_INTERVAL_WEEK:

			mBuffer.append("每 ");
			mBuffer.append(remind.repeatDistance);
			mBuffer.append("周 ");
			mBuffer.append(TimeUtil.getWeek(remind.BaseTime));

			break;

		case REMIND_TYPE_INTERVAL_MONTH:
			mBuffer.append("每");
			mBuffer.append(remind.repeatDistance);
			mBuffer.append("个月");
			break;

		case REMIND_TYPE_INTERVAL_YEAR:
			mBuffer.append("每");
			mBuffer.append(remind.repeatDistance);
			mBuffer.append("年");
			break;

		case REMIND_TYPE_DAY_TO_DAY:
			mBuffer.append("周");
			mBuffer.append(NumberFormatUtils.getZhWeekNumber(remind.avalibeFrom));
			mBuffer.append("至");
			mBuffer.append("周");
			mBuffer.append(NumberFormatUtils.getZhWeekNumber(remind.avalibeTo));

			break;

		case REMIND_TYPE_ONCE:
			mBuffer.append(TimeUtil.getMonth(remind.BaseTime));
			mBuffer.append("-");
			mBuffer.append(TimeUtil.getDay(remind.BaseTime));

			break;

		default:
			break;
		}

//		setTimeBuffer(mBuffer, remind);
		// LogManager.e("remind show time : " + mBuffer.toString());

		return mBuffer.toString();
	}

	// private int detalRepeatDistance(int repeatDistance) {
	// if (repeatDistance == 1) {
	// return 1;
	// } else {
	// return repeatDistance - 1;
	// }
	// }

//    private int detalRepeatDistance(int repeatDistance) {
//        if (repeatDistance == 1) {
//            return 1;
//        } else {
//            return repeatDistance - 1;
//        }
//    }

    private void setTimeBuffer(StringBuffer mBuffer, Remind remind) {
        mBuffer.append(" ");
        mBuffer.append(TimeUtil.getHour(remind.BaseTime));
        mBuffer.append(":");
        mBuffer.append(TimeUtil.getMintue(remind.BaseTime));
    }

}
