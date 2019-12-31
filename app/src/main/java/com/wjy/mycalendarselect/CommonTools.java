package com.wjy.mycalendarselect;

import android.content.Context;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import java.util.Calendar;
import java.util.Date;

public class CommonTools {

	/**
	 * 输出标准 2016年01月01日
	 *
	 * @param date
	 * @return
	 */
	public static String getDateForStandard(String date) {
		String dates[] = date.substring(0, 10).split("-");
		String year = dates[0];
		String month = dates[1];
		String day = dates[2];
		return String.format("%d年%02d月%02d日", string2int(year),
				string2int(month), string2int(day));
	}

	public static int string2int(String str) {
		int i = 0;
		try {
			i = Integer.parseInt(str);
		} catch (Exception e) {
			return 0;
		}
		return i;
	}

	/**
	 * 通过日期得到星期
	 */
	public static String DateToWeek(Date date) {
		String[] WEEK = {"周日","周一","周二","周三","周四","周五","周六"};
		int WEEKDAYS = 7;
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		int dayIndex = calendar.get(Calendar.DAY_OF_WEEK);
		if (dayIndex < 1 || dayIndex > WEEKDAYS) {
			return null;
		}
		return WEEK[dayIndex - 1];
	}

	/**
	 * 获取两个日期之间的间隔天数
	 * @return
	 */
 	public static int getDayCount(Date startDate, Date endDate) {
 		Calendar fromCalendar = Calendar.getInstance();
 		fromCalendar.setTime(startDate);
 		fromCalendar.set(Calendar.HOUR_OF_DAY, 0);
		fromCalendar.set(Calendar.MINUTE, 0);
 		fromCalendar.set(Calendar.SECOND, 0);
 		fromCalendar.set(Calendar.MILLISECOND, 0);

		Calendar toCalendar = Calendar.getInstance();
		toCalendar.setTime(endDate);
 		toCalendar.set(Calendar.HOUR_OF_DAY, 0);
 		toCalendar.set(Calendar.MINUTE, 0);
 		toCalendar.set(Calendar.SECOND, 0);
 		toCalendar.set(Calendar.MILLISECOND, 0);
 		return (int) ((toCalendar.getTime().getTime() - fromCalendar.getTime().getTime()) / (1000 * 60 * 60 * 24));
	}

	public static boolean isEmpty(String s) {
		if (null == s)
			return true;
		if (s.length() == 0)
			return true;
		if (s.trim().length() == 0)
			return true;
		if (s.equals("null"))
			return true;
		return false;
	}
}
