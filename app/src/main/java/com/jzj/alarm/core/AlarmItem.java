package com.jzj.alarm.core;

import java.util.Calendar;

/**
 * 闹铃项
 * 
 * @author jzj
 */
public class AlarmItem {

	// 闹铃模式
	public static final int MODE_WAKEUP = 0;
	public static final int MODE_NORMAL = 1;

	// 闹铃重复
	public static final int MON = 0x01;
	public static final int TUE = 0x02;
	public static final int WED = 0x04;
	public static final int THU = 0x08;
	public static final int FRI = 0x10;
	public static final int SAT = 0x20;
	public static final int SUN = 0x40;
	public static final int REPEAT_ONCE = 0x00;
	public static final int REPEAT_WEEKDAY = MON + TUE + WED + THU + FRI;
	public static final int REPEAT_WEEKEND = SAT + SUN;
	public static final int REPEAT_EVERYDAY = REPEAT_WEEKDAY + REPEAT_WEEKEND;
	public static final int[] WEEK = { MON, TUE, WED, THU, FRI, SAT, SUN };

	protected int id = 0;
	protected int mode = MODE_NORMAL;
	protected int repeat = REPEAT_ONCE;
	protected int hour = 0;
	protected int minute = 0;
	protected boolean ON_OFF = false;
	protected String notes = null;
	protected Calendar date = null;

	protected AlarmItem() {

	}

	/**
	 * 精简构造函数：默认普通模式，重复一次，无备注，开启闹铃,日期为空
	 * 
	 * @param hour
	 * @param minute
	 */
	public AlarmItem(int hour, int minute) {
		this.hour = hour;
		this.minute = minute;
		this.ON_OFF = true;
	}

	/**
	 * 复制构造函数
	 * 
	 * @param a
	 */
	public AlarmItem(AlarmItem a) {
		this.hour = a.hour;
		this.minute = a.minute;
		this.mode = a.mode;
		this.repeat = a.repeat;
		this.notes = a.notes;
		this.ON_OFF = a.ON_OFF;
		this.date = a.date;
	}

	/**
	 * 计算下次闹铃时间
	 * 
	 * @return 下次闹铃时间 TimeMillis
	 */
	public long calcNextAlarmTimeMillis() {

		// 如果闹铃关闭，返回MaxVal
		if (this.ON_OFF == false) {
			return Long.MAX_VALUE;
		}

		Calendar now = Calendar.getInstance();

		// 如果重复一次，且日期非空：为日程提醒
		if (this.repeat == REPEAT_ONCE && this.date != null) {
			date.set(Calendar.HOUR_OF_DAY, hour);
			date.set(Calendar.MINUTE, minute);
			date.set(Calendar.SECOND, 0);
			date.set(Calendar.MILLISECOND, 0);

			// 如果c所表示的时间已经过去(c<now)，则返回最大值
			if (date.before(now))
				return Long.MAX_VALUE;
			else
				return date.getTimeInMillis();
		}

		Calendar c = (Calendar) now.clone();
		c.set(Calendar.HOUR_OF_DAY, hour);
		c.set(Calendar.MINUTE, minute);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);

		// 闹铃为WAKEUP模式，启动时间 = 设置值

		// 如果c所表示的时间已经过去(c<now)，则将c增加一天
		if (c.before(now)) {
			c.add(Calendar.DATE, 1);
		}

		// 如果闹铃设置为只响一次，则不需再判断星期
		if (this.repeat == REPEAT_ONCE) {

		} else {
			// c表示的那一天对应的星期不在重复闹铃范围内，则继续增加天数(最多测试7天)
			for (int i = 0; i < 7; ++i) {
				int day = calcDayOfWeek(c.get(Calendar.DAY_OF_WEEK));
				if ((repeat & day) != 0) {
					break;
				}
				c.add(Calendar.DATE, 1);
			}
		}
		return c.getTimeInMillis();
	}

	protected final int calcDayOfWeek(int d) {
		switch (d) {
		case Calendar.MONDAY:
			return MON;
		case Calendar.TUESDAY:
			return TUE;
		case Calendar.WEDNESDAY:
			return WED;
		case Calendar.THURSDAY:
			return THU;
		case Calendar.FRIDAY:
			return FRI;
		case Calendar.SATURDAY:
			return SAT;
		case Calendar.SUNDAY:
			return SUN;
		}
		return 0;
	}

	// -------------------------------Time-------------------------------//
	public final AlarmItem setTime(int hour, int minute) {
		this.hour = hour;
		this.minute = minute;
		return this;
	}

	public final int getHour() {
		return hour;
	}

	public final AlarmItem setHour(int hour) {
		this.hour = hour;
		return this;
	}

	public final AlarmItem setMinute(int minute) {
		this.minute = minute;
		return this;
	}

	public final int getMinute() {
		return minute;
	}

	// -------------------------------Mode-------------------------------//
	public final int getMode() {
		return mode;
	}

	public final AlarmItem setMode(int mode) {
		this.mode = mode;
		return this;
	}

	// -------------------------------OnOff-------------------------------//
	public final boolean isOn() {
		return ON_OFF;
	}

	public final AlarmItem setOnOff(boolean on_off) {
		ON_OFF = on_off;
		return this;
	}

	public final void flipOnOff() {
		ON_OFF = !ON_OFF;
	}

	// -------------------------------Notes-------------------------------//
	public final String getNotes() {
		return notes;
	}

	public final AlarmItem setNotes(String notes) {
		this.notes = notes;
		return this;
	}

	// -------------------------------Id-------------------------------//
	public final int getId() {
		return id;
	}

	public final AlarmItem setId(int id) {
		this.id = id;
		return this;
	}

	// -------------------------------Repeat-------------------------------//
	public final AlarmItem setDate(Calendar date) {
		this.date = date;
		return this;
	}

	public final Calendar getDate() {
		return this.date;
	}

	public final AlarmItem setRepeat(int repeat) {
		this.repeat = repeat;
		return this;
	}

	public final int getRepeat() {
		return repeat;
	}

	public final boolean isRepeat(int week) {
		return (repeat & week) != 0;
	}
}
