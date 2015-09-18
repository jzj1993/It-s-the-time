package com.jzj.alarm.ui.tools;

import com.jzj.alarm.R;
import com.jzj.alarm.core.AlarmItem;
import com.jzj.util.CalendarPlus;

import android.content.Context;

/**
 * 将闹铃转换为用于显示的字符串
 * 
 * @author jzj
 */
public class AlarmString {

	private final Context context;
	private final AlarmItem alarm;

	public AlarmString(Context context, AlarmItem alarm) {
		this.context = context;
		this.alarm = alarm;
	}

	public final String hour() {
		return String.format("%02d", alarm.getHour());
	}

	public final String minute() {
		return String.format("%02d", alarm.getMinute());
	}

	public final String time() {
		return String.format("%02d:%02d", alarm.getHour(), alarm.getMinute());
	}

	public final String notes() {
		String notes = alarm.getNotes();
		if (notes == null || notes.length() == 0) {
			return res(R.string.alarm_no_notes);
		} else if (notes.length() > 7) {
			return notes.substring(0, 6) + "...";
		}
		return new String(notes);
	}

	public final String mode() {
		final int res;
		switch (alarm.getMode()) {
		case AlarmItem.MODE_NORMAL:
			res = R.string.alarm_mode_normal;
			break;
		case AlarmItem.MODE_WAKEUP:
		default:
			res = R.string.alarm_mode_wakeup;
			break;
		}
		return res(res);
	}

	/**
	 * 完整版的“重复”选项显示:自定义时显示具体星期
	 * 
	 * @return
	 */
	public final String repeat() {
		switch (alarm.getRepeat()) {
		case AlarmItem.REPEAT_ONCE:
		case AlarmItem.REPEAT_EVERYDAY:
		case AlarmItem.REPEAT_WEEKDAY:
		case AlarmItem.REPEAT_WEEKEND:
			return repeatSimple();
		default:
			final int[] sw = {
					R.string.alarm_repeat_mon,
					R.string.alarm_repeat_tue,
					R.string.alarm_repeat_wed,
					R.string.alarm_repeat_thu,
					R.string.alarm_repeat_fri,
					R.string.alarm_repeat_sat,
					R.string.alarm_repeat_sun };
			final StringBuilder b = new StringBuilder();
			for (int i = 0; i < AlarmItem.WEEK.length; ++i) {
				if (alarm.isRepeat(AlarmItem.WEEK[i])) {
					b.append(res(sw[i])).append(' ');
				}
			}
			return b.toString().trim(); // 删除末尾空格
		}
	}

	/**
	 * 简化版的“重复”选项显示:自定义时只显示“自定义”，不显示具体星期
	 * 
	 * @return
	 */
	public final String repeatSimple() {
		switch (alarm.getRepeat()) {
		case AlarmItem.REPEAT_ONCE:
			if (alarm.getDate() == null) {
				return res(R.string.alarm_repeat_once);
			} else {
				return CalendarPlus.format(alarm.getDate(),
						CalendarPlus.PATTERN_YMD);
			}
		case AlarmItem.REPEAT_EVERYDAY:
			return res(R.string.alarm_repeat_everyday);
		case AlarmItem.REPEAT_WEEKDAY:
			return res(R.string.alarm_repeat_weekday);
		case AlarmItem.REPEAT_WEEKEND:
			return res(R.string.alarm_repeat_weekend);
		default:
			return res(R.string.alarm_repeat_custom);
		}
	}

	private final String res(int res) {
		return context.getString(res);
	}
}
