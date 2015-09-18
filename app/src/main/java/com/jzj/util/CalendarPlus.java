package com.jzj.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Calendar扩展工具类
 *
 * @author jzj
 * @since 2014-08-25
 */
public class CalendarPlus extends GregorianCalendar {

	public static final String PATTERN_YMD = "yyyy-MM-dd";
	public static final String PATTERN_YMD_HMS = "yyyy-MM-dd HH:mm:ss";
	public static final String PATTERN_YMD_HMS_E = "yyyy-MM-dd HH:mm:ss E";

	public static final int[] CalendarWeeks = {
			Calendar.MONDAY,
			Calendar.TUESDAY,
			Calendar.WEDNESDAY,
			Calendar.THURSDAY,
			Calendar.FRIDAY,
			Calendar.SATURDAY,
			Calendar.SUNDAY };

	private static final boolean DBG = true;

	private static final long serialVersionUID = 9187446035696158241L;

	private static final long msPerDay = 24 * 3600 * 1000;

	/**
	 * 实例化，使用当前时间和默认时区
	 */
	public CalendarPlus() {
		super();
	}

	/**
	 * 实例化，使用指定时间和默认时区
	 */
	public CalendarPlus(long ms) {
		super();
		this.setTimeInMillis(ms);
	}

	/**
	 * @return
	 *         输出格式为"yyyy-MM-dd HH:mm:ss E"
	 */
	@Override
	public String toString() {
		return format(this, PATTERN_YMD_HMS_E);
	}

	/**
	 * 打印输出
	 */
	public void print() {
		System.out.println(this.toString());
	}

	/**
	 * 打印输出Calendar
	 * 输出格式为"yyyy-MM-dd HH:mm:ss E"
	 *
	 * @param c
	 */
	public static final void print(Calendar c) {
		System.out.println(format(c, PATTERN_YMD_HMS_E));
	}

	/**
	 * 判断是否在同一周(默认一周第一天为周日)
	 *
	 * @param c1
	 * @return 在同一周返回true
	 */
	public boolean isInSameWeek(Calendar c) {
		return isInSameWeek(this, c);
	}

	/**
	 * 判断是否在同一周
	 *
	 * @param c1
	 * @return 在同一周返回true
	 */
	public boolean isInSameWeek(Calendar c, boolean firstMonday) {
		return isInSameWeek(this, c, firstMonday);
	}

	/**
	 * 设置时间为一天的00:00:00.00
	 *
	 * @param c
	 * @return
	 */
	public static final Calendar setZeroTimeInDay(Calendar c) {
		return setTimeInDay(c, 0, 0, 0, 0);
	}

	/**
	 * 设置时间 时分秒
	 *
	 * @param c
	 * @param h
	 * @param m
	 * @param s
	 * @return
	 */
	public static final Calendar setTimeInDay(Calendar c, int h, int m, int s) {
		return setTimeInDay(c, h, m, s, 0);
	}

	/**
	 * 设置时间 时分秒毫秒
	 *
	 * @param c
	 * @param h
	 * @param m
	 * @param s
	 * @param ms
	 * @return
	 */
	public static final Calendar setTimeInDay(Calendar c, int h, int m, int s,
											  int ms) {
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);
		return c;
	}

	/**
	 * 计算天数差(使用默认时区)
	 *
	 * @param c1
	 * @param c2
	 * @return c1-c2天数差
	 */
	public static final long calcDayDiff(Calendar c1, Calendar c2) {
		Calendar c2_zero = setZeroTimeInDay((Calendar) c2.clone());
		return (c1.getTimeInMillis() - c2_zero.getTimeInMillis()) / msPerDay;
	}

	/**
	 * 判断是否在同一周
	 *
	 * @param c1
	 * @param c2
	 * @return 在同一周返回true
	 */
	public static final boolean isInSameWeek(Calendar c1, Calendar c2,
											 boolean firstMonday) {
		// start比end早
		final Calendar start, end;
		if (c1.before(c2)) {
			start = (Calendar) c1.clone();
			end = (Calendar) c2.clone();
		} else {
			start = (Calendar) c2.clone();
			end = (Calendar) c1.clone();
		}
		if (DBG) {
			print(start);
			print(end);
		}
		// 如果end倒退到那一周的第一天0点，不比start晚，说明是同一周
		// 这里如果使用Calendar.set(Calendar.MONDAY)有时运行结果不对
		end.add(Calendar.DAY_OF_YEAR, -getDayOfWeek(end, firstMonday));
		setZeroTimeInDay(end);
		end.getTimeInMillis();
		if (DBG) {
			print(start);
			print(end);
		}
		return !end.after(start);
	}

	/**
	 * 获取当前为一个星期的第几天
	 *
	 * @param c
	 * @param firstMonday
	 *            星期一为一周第一天
	 * @return firstMonday=true: 周一返回0，周六返回5，周日返回6
	 *         firstMonday=false: 周日返回0，周一返回1，周六返回6
	 */
	public static int getDayOfWeek(Calendar c, boolean firstMonday) {
		int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
		int d = -1;
		for (int i = 0; i < CalendarWeeks.length; ++i) {
			if (CalendarWeeks[i] == dayOfWeek) {
				d = i;
				break;
			}
		}
		if (d == -1)
			return d;
		if (firstMonday) {
			return d;
		} else {
			return ++d % 7;
		}
	}

	/**
	 * 判断是否在同一周(默认一周的第一天为周日)
	 *
	 * @param c1
	 * @param c2
	 * @return 在同一周返回true
	 */
	public static final boolean isInSameWeek(Calendar c1, Calendar c2) {
		return isInSameWeek(c1, c2, false);
	}

	/**
	 * 从ms格式化
	 *
	 * @param milliseconds
	 * @param pattern
	 * @return
	 */
	public static final String format(long milliseconds, String pattern) {
		return format(new Date(milliseconds), pattern);
	}

	/**
	 * 从Calendar格式化
	 *
	 * @param c
	 * @param pattern
	 * @return
	 */
	public static final String format(Calendar c, String pattern) {
		if (c == null)
			return null;
		return format(c.getTime(), pattern);
	}

	/**
	 * 从Data格式化
	 *
	 * @param date
	 * @param pattern
	 * @return
	 */
	public static final String format(Date date, String pattern) {
		if (date == null || pattern == null)
			return null;
		try {
			return new SimpleDateFormat(pattern).format(date);
		} catch (Exception e) {
			if (Debug.DBG)
				e.printStackTrace();
		}
		return null;
	}

	/**
	 * 解析为Date
	 *
	 * @param s
	 * @param pattern
	 * @return
	 */
	public static final Date parseDate(String s, String pattern) {
		if (s == null || pattern == null || s.length() != pattern.length())
			return null;
		try {
			return new SimpleDateFormat(pattern).parse(s);
		} catch (ParseException e) {
			if (Debug.DBG)
				e.printStackTrace();
			return null;
		}
	}

	/**
	 * 解析为Calendar
	 *
	 * @param s
	 * @param pattern
	 * @return
	 */
	public static final Calendar parseCalendar(String s, String pattern) {
		Date d = parseDate(s, pattern);
		if (d != null) {
			Calendar c = Calendar.getInstance();
			c.setTime(d);
			return c;
		} else {
			return null;
		}
	}
}
