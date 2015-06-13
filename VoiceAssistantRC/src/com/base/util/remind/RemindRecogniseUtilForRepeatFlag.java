package com.base.util.remind;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//主要用来处理带"每月"，"下周"，"每周"情况
public class RemindRecogniseUtilForRepeatFlag {
	private static List<String> mWeekNumberList = new ArrayList<String>();
	private static List<String> mWeekChineseList = new ArrayList<String>();
	private static List<String> mWeekSpeList = new ArrayList<String>();
	public static char[] _mFlag = { '0', '0', '0', '0', '0', '0', '0', '0' };
	private static Pattern mPatternMonth = Pattern.compile(".*每个?月.*");
	private static Pattern mPatternEveryDay = Pattern.compile(".*每(天|日).*");
	private static Pattern[] patternsWeek = {
			Pattern.compile("(.*)(每?)(星期|周|礼拜)([1234567一二三四五六七日])(到|至)(星期|周|礼拜)([1234567一二三四五六七日])(.*)?"),
			Pattern.compile(".*?(每?(((周|星期|礼拜)([1234567一二三四五六七日]))+))(.*)?"),
			// Pattern.compile(".*?(每?(((周|星期|礼拜)[1234567一二三四五六七日]){1}[1234567一二三四五六七日]+)).*?")
			// }
			Pattern.compile(".*?(每?(((周|星期|礼拜)[1234567一二三四五六七日]){1}[1234567一二三四五六七日]+)).*?"),
			Pattern.compile(".*?((每?(周|星期|礼拜)[123456789一二三四五六七日])+[123456789一二三四五六七日]*)(\\d(点|时候?))?.*") };

	public static void recogniseMonthFlag(String content) {
		Matcher m1 = mPatternMonth.matcher(content);
		char[] a = new char[] { '0', '0', '0', '0', '0', '0', '0' };
		if (m1.matches()) {
			_mFlag[0] = '3';
			// ID20121010001 hujinrong begin
			RemindRecogniseUtilForDate._mIsSetDate = true;
			// ID20121010001 hujinrong end
			// ID20121220001 hujinrong begin
			if (RemindRecogniseUtilForDate._mIsobscureDayOfMonth) {
				_mFlag[1] = '1';
			}
			// ID20121220001 hujinrong end
		}
	}

	public static void recogniseDayRepeatFlag(String content) {
		Matcher m1 = mPatternEveryDay.matcher(content);
		if (m1.matches()) {
			_mFlag[0] = '1';
			// ID20121010001 hujinrong begin
			RemindRecogniseUtilForDate._mIsSetDate = true;
			// ID20121010001 hujinrong end
		}
	}

	public static void recogniseWeekFlag(String content) {
		Matcher m1 = patternsWeek[0].matcher(content);
		Matcher m2 = patternsWeek[1].matcher(content);
		// ID20121121001 hujinrong begin
		// Matcher m3 = patternsWeek[2].matcher(content);
		// Matcher m4 = patternsWeek[3].matcher(content);
		// ID20121121001 hujinrong end
		boolean weekRepeatFlag = false;
		// ID20121121001 hujinrong begin
		if (!(content.contains("下周") || content.contains("下下周") || content.contains("下下下周"))) {
			// ID20121121001 hujinrong end
			if (m1.matches()) {
				weekRepeatFlag = true;
				int first = utilForGetNumber(m1.group(4));
				int second = utilForGetNumber(m1.group(7));
				// ID20121010001 hujinrong begin
				RemindRecogniseUtilForDate._mIsSetDate = true;
				// ID20121010001 hujinrong end
				char[] a = new char[] { '0', '0', '0', '0', '0', '0', '0' };
				if (second >= first) {
					for (int i = first; i <= second; i++) {
						if (i <= 5) {
							a[i + 1] = '1';
						} else if (i >= 6) {
							a[i - 6] = '1';
						}
					}
					char desc[] = new char[8];
					desc[0] = '2';
					for (int i = 0; i < a.length; i++) {
						desc[i + 1] = a[i];
					}
					utilForCopyCharArray(_mFlag, desc);
				} else {
					a = new char[] { '1', '1', '1', '1', '1', '1', '1' };
					for (int i = second + 1; i < first; i++) {
						if (i <= 5) {
							a[i + 1] = '0';
						} else if (i >= 6) {
							a[i - 6] = '0';
						}
					}
					char desc[] = new char[8];
					desc[0] = '2';
					for (int i = 0; i < a.length; i++) {
						desc[i + 1] = a[i];
					}
					utilForCopyCharArray(_mFlag, desc);
				}
				// ID20121121001 hujinrong begin
			} else if (m2.matches()) {
				String infomation = null;
				// ID20121010001 hujinrong begin
				RemindRecogniseUtilForDate._mIsSetDate = true;
				// ID20121010001 hujinrong end
				// if (m3.matches()) {
				// infomation = m3.group(1);
				// } else {
				infomation = m2.group(1);
				// }
				char[] a = new char[] { '0', '0', '0', '0', '0', '0', '0' };
				for (int i = 0; i < 7; i++) {
					String number = mWeekNumberList.get(i);
					String chinese = mWeekChineseList.get(i);
					if (infomation.contains(number) || infomation.contains(chinese)) {
						a[i] = '1';
					}
				}
				for (int i = 0; i < mWeekSpeList.size(); i++) {
					String special = mWeekSpeList.get(i);
					if (infomation.contains(special)) {
						a[1] = '1';
					}
				}
				if ((infomation.contains("每")) || !isOnlyOne(a)) {
					char desc[] = new char[8];
					desc[0] = '2';
					for (int i = 0; i < a.length; i++) {
						desc[i + 1] = a[i];
					}
					utilForCopyCharArray(_mFlag, desc);
					weekRepeatFlag = true;
				}
			}
			// else if (m4.matches()) {
			// String infomation = m4.group(1);
			// //ID20121010001 hujinrong begin
			// RemindRecogniseUtilForDate._mIsSetDate = true;
			// //ID20121010001 hujinrong end
			// char[] a = new char[] { '0', '0', '0', '0', '0', '0', '0' };
			// for (int i = 0; i < 7; i++) {
			// String number = mWeekNumberList.get(i);
			// String chinese = mWeekChineseList.get(i);
			// if (infomation.contains(number) || infomation.contains(chinese))
			// {
			// a[i] = '1';
			// }
			// }
			// for (int i = 0; i < mWeekSpeList.size(); i++) {
			// String special = mWeekSpeList.get(i);
			// if (infomation.contains(special)) {
			// a[1] = '1';
			// }
			// }
			// if ((infomation.contains("每")) || !isOnlyOne(a)) {
			// char desc[] = new char[8];
			// desc[0] = '2';
			// for (int i = 0; i < a.length; i++) {
			// desc[i + 1] = a[i];
			// }
			// utilForCopyCharArray(_mFlag, desc);
			// weekRepeatFlag = true;
			// }
			// }
		}
		// ID20121121001 hujinrong end
		if (!weekRepeatFlag) {
			processIsNotRepeat(content);
		}
	}

	private static int utilForGetNumber(String text) {
		if (text.equals("一")) {
			return 1;
		} else if (text.equals("二")) {
			return 2;
		} else if (text.equals("三")) {
			return 3;
		} else if (text.equals("四")) {
			return 4;
		} else if (text.equals("五")) {
			return 5;
		} else if (text.equals("六")) {
			return 6;
		} else {
			return 7;
		}

	}

	private static void utilForCopyCharArray(char[] source, char[] dest) {
		for (int i = 0; i < dest.length; i++) {
			source[i] = dest[i];
		}
	}

	private static boolean isOnlyOne(char[] a) {
		int number = 0;
		for (int i = 0; i < a.length; i++) {
			if (a[i] == '1') {
				number++;
			}
		}
		return number <= 1 ? true : false;
	}

	// ID20121121001 hujinrong begin
	private static String numberToChinese(String old) {
		String chineseString = "一二三四五六七八九";
		String numberString = "123456789";
		int length = numberString.length();
		String newString = old;
		for (int i = 0; i < length; i++) {
			char oldChar = numberString.charAt(i);
			if (newString.contains(old)) {
				newString = newString.replace(oldChar, chineseString.charAt(i));
			}
		}
		return newString;
	}

	// ID20121121001 hujinrong end
	private static void processIsNotRepeat(String text) {
		// ID20121121001 hujinrong begin
		text = numberToChinese(text);
		// ID20121121001 hujinrong end
		int dayOfWeek = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
		int weekNumber = 0;
		if (text.contains("星期一") || text.contains("周一") || text.contains("星期二") || text.contains("周二")
				|| text.contains("星期三") || text.contains("周三") || text.contains("星期四") || text.contains("周四")
				|| text.contains("星期五") || text.contains("周五") || text.contains("星期六") || text.contains("周六")
				|| text.contains("星期日") || text.contains("周日") || text.contains("星期天")) {
			RemindRecogniseUtilForDate._mIsSetDate = true;
			if (text.contains("星期一") || text.contains("周一")) {
				if (text.contains("下下个星期一") || text.contains("下下星期一") || text.contains("下下个周一")
						|| text.contains("下下周一") || text.contains("下下周周一") || text.contains("下下个礼拜一")
						|| text.contains("下下礼拜一")) {
					weekNumber = 16;
				} else if (text.contains("下个星期一") || text.contains("下星期一") || text.contains("下个周一")
						|| text.contains("下周一") || text.contains("下周周一") || text.contains("下个礼拜一")
						|| text.contains("下礼拜一")) {
					weekNumber = 9;
				} else {
					weekNumber = 2;
				}
			} else if (text.contains("星期二") || text.contains("周二")) {
				if (text.contains("下下个星期二") || text.contains("下下星期二") || text.contains("下下个周二")
						|| text.contains("下下周二") || text.contains("下下周周二") || text.contains("下下个礼拜二")
						|| text.contains("下下礼拜二")) {
					weekNumber = 17;
				} else if (text.contains("下个星期二") || text.contains("下星期二") || text.contains("下个周二")
						|| text.contains("下周二") || text.contains("下周周二") || text.contains("下个礼拜二")
						|| text.contains("下礼拜二")) {
					weekNumber = 10;
				} else {
					weekNumber = 3;
				}
			} else if (text.contains("星期三") || text.contains("周三")) {
				if (text.contains("下下个星期三") || text.contains("下下星期三") || text.contains("下下个周三")
						|| text.contains("下下周三") || text.contains("下下周周三") || text.contains("下下个礼拜三")
						|| text.contains("下下礼拜三")) {
					weekNumber = 18;
				}

				else if (text.contains("下个星期三") || text.contains("下星期三") || text.contains("下个周三")
						|| text.contains("下周三") || text.contains("下周周三") || text.contains("下个礼拜三")
						|| text.contains("下礼拜三")) {
					weekNumber = 11;
				} else {
					weekNumber = 4;
				}
			}

			else if (text.contains("星期四") || text.contains("周四")) {
				if (text.contains("下下个星期四") || text.contains("下下星期四") || text.contains("下下个周四")
						|| text.contains("下下周四") || text.contains("下下周周四") || text.contains("下下个礼拜四")
						|| text.contains("下下礼拜四")) {
					weekNumber = 19;
				} else if (text.contains("下个星期四") || text.contains("下星期四") || text.contains("下个周四")
						|| text.contains("下周四") || text.contains("下周周四") || text.contains("下个礼拜四")
						|| text.contains("下礼拜四")) {
					weekNumber = 12;
				} else {
					weekNumber = 5;
				}
			}

			else if (text.contains("星期五") || text.contains("周五")) {
				if (text.contains("下下个星期五") || text.contains("下下星期五") || text.contains("下下个周五")
						|| text.contains("下下周五") || text.contains("下下周周五") || text.contains("下下个礼拜五")
						|| text.contains("下下礼拜五")) {
					weekNumber = 20;
				} else if (text.contains("下个星期五") || text.contains("下星期五") || text.contains("下个周五")
						|| text.contains("下周五") || text.contains("下周周五") || text.contains("下个礼拜五")
						|| text.contains("下礼拜五")) {
					weekNumber = 13;
				} else {
					weekNumber = 6;
				}
			}

			else if (text.contains("星期六") || text.contains("周六")) {
				if (text.contains("下下个星期六") || text.contains("下下星期六") || text.contains("下下个周六")
						|| text.contains("下下周六") || text.contains("下下周周六") || text.contains("下下个礼拜六")
						|| text.contains("下下礼拜六")) {
					weekNumber = 21;
				} else if (text.contains("下个星期六") || text.contains("下星期六") || text.contains("下个周六")
						|| text.contains("下周六") || text.contains("下周周六") || text.contains("下个礼拜六")
						|| text.contains("下礼拜六")) {
					weekNumber = 14;
				} else {
					weekNumber = 7;
				}
			}

			else if (text.contains("星期日") || text.contains("周日") || text.contains("星期天")) {
				if (text.contains("下下个星期日") || text.contains("下下星期日") || text.contains("下下个周日")
						|| text.contains("下下周日") || text.contains("下下周周日") || text.contains("下下个礼拜日")
						|| text.contains("下下礼拜日") || text.contains("下下个礼拜天") || text.contains("下下礼拜天")
						|| text.contains("下下个星期天") || text.contains("下下礼拜天")) {
					weekNumber = 15;
				}

				else if (text.contains("下个星期日") || text.contains("下星期日") || text.contains("下个周日")
						|| text.contains("下周日") || text.contains("下周周日") || text.contains("下个礼拜日")
						|| text.contains("下礼拜日") || text.contains("下个礼拜天") || text.contains("下礼拜天")
						|| text.contains("下个星期天") || text.contains("下礼拜天")) {
					weekNumber = 8;
				} else {
					weekNumber = 1;
				}
				// recalculate weekday

			}

			if (dayOfWeek < 6 && dayOfWeek > 2) {
				// ID20121121001 hujinrong begin
				// dayOfWeek = dayOfWeek - 1;
				// dayOfWeek = dayOfWeek - 1;
				// ID20121121001 hujinrong end
			} else if (dayOfWeek == 1) {
				dayOfWeek = 7;
			} else if (dayOfWeek == 7) {
				dayOfWeek = 6;
			}
			int nextWeek1Last = 7 - dayOfWeek;
			int weekStep = weekNumber / 7;
			int dayStep = 0;

			if (weekStep == 0) {
				if (weekNumber < dayOfWeek) {
					weekNumber = weekNumber + 7;
					dayStep = (weekStep - 1) * 7 + nextWeek1Last + weekNumber % 7;
				} else {
					dayStep = weekNumber - dayOfWeek;
				}
			}
			if (weekStep != 0) {
				dayStep = (weekStep - 1) * 7 + nextWeek1Last + weekNumber % 7;
			}

			RemindRecogniseUtilForDate.resetDate(dayStep);
		}

	}

	static {
		String weekNumberString = "6712345";
		String weekChineseString = "六七一二三四五";
		char[] weekNumberStringArray = weekNumberString.toCharArray();
		char[] weekCahineseStringArray = weekChineseString.toCharArray();
		for (int i = 0; i < 7; i++) {
			mWeekNumberList.add(weekNumberStringArray[i] + "");
			mWeekChineseList.add(weekCahineseStringArray[i] + "");
		}
		mWeekSpeList.add("天");
		mWeekSpeList.add("日");
	}
}
