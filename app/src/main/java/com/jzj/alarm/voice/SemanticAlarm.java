package com.jzj.alarm.voice;

import java.util.Calendar;

import android.text.TextUtils;

import com.jzj.alarm.core.AlarmItem;
import com.jzj.alarm.voice.SemanticResult.Semantic.Slots;
import com.jzj.util.CalendarPlus;
import com.jzj.util.Debug;

/**
 * 从语音识别的语义结果创建闹铃
 *
 * @author jzj
 */
public class SemanticAlarm {

	private static final String[][] repeatKeywords = {
			{ "1", "一" },
			{ "2", "二" },
			{ "3", "三", },
			{ "4", "四", },
			{ "5", "五", },
			{ "6", "六", },
			{ "7", "七", "日", },
			{ "ONCE" },
			{ "EVERYDAY" },
			{ "WORKDAY" },
			{ "WEEKEND" } };

	private static final int[] repeatValue = {
			AlarmItem.MON,
			AlarmItem.THU,
			AlarmItem.WED,
			AlarmItem.THU,
			AlarmItem.FRI,
			AlarmItem.SAT,
			AlarmItem.SUN,
			AlarmItem.REPEAT_ONCE,
			AlarmItem.REPEAT_EVERYDAY,
			AlarmItem.REPEAT_WEEKDAY,
			AlarmItem.REPEAT_WEEKEND };

	private static final String[] kwdsWakeUp = { "起床", "起来", "上班", "上学" };

	public static final AlarmItem createAlarm(SemanticResult r) {
		try {
			if (r == null)
				return null;

			if (r.service == null || !r.service.equals("schedule"))
				return null;
			if (r.operation == null || !r.operation.equals("CREATE"))
				return null;

			Slots slot = r.getSlots();
			if (slot == null)
				return null;

			// 时间
			Calendar date = CalendarPlus.parseCalendar(slot.datetime.time,
					"HH:mm:ss");
			AlarmItem alarm = new AlarmItem(date.get(Calendar.HOUR_OF_DAY),
					date.get(Calendar.MINUTE));
			// 模式
			if (isWakeUpMode(r)) {
				alarm.setMode(AlarmItem.MODE_WAKEUP);
			} else {
				alarm.setMode(AlarmItem.MODE_NORMAL);
			}
			// 重复
			alarm.setRepeat(getRepeat(slot.repeat));
			// 如果只重复一次则获取闹铃日期
			Calendar c = CalendarPlus.parseCalendar(slot.datetime.date,
					"yyyy-MM-dd");
			if (c != null) {
				alarm.setDate(c);
			}
			// 备注
			if (!TextUtils.isEmpty(slot.content)) {
				alarm.setNotes(slot.content);
			} else if (alarm.getMode() == AlarmItem.MODE_WAKEUP) {
				alarm.setNotes("起床");
			}
			return alarm;
		} catch (Exception e) { // NullPointerException
			if (Debug.DBG)
				e.printStackTrace();
		}
		return null;
	}

	private static boolean isWakeUpMode(SemanticResult r) {
		try {
			if (!TextUtils.isEmpty(r.text)) {
				for (String s : kwdsWakeUp) {
					if (r.text.contains(s))
						return true;
				}
			}
		} catch (Exception e) {
			if (Debug.DBG)
				e.printStackTrace();
		}
		return false;
	}

	private static int getRepeat(String repeat) {
		try {
			if (!TextUtils.isEmpty(repeat)) {
				if (repeat.contains("-")) { // 周一-二
					int start = -1, end = -1;
					for (int i = 0; i < 7; ++i) {
						for (String s : repeatKeywords[i]) {
							if (repeat.contains(s)) {
								if (start == -1) {
									start = i;
								} else if (end == -1) {
									end = i;
								} else {
									// error
								}
							}
						}
					}
					if (start != -1 && end != -1) {
						if (start > end) {
							final int x = end;
							end = start;
							start = x;
						}
						int ret = AlarmItem.REPEAT_ONCE;
						for (int i = start; i <= end; ++i) {
							ret |= repeatValue[i];
						}
						return ret;
					}
				} else {
					int ret = AlarmItem.REPEAT_ONCE;
					for (int i = 0; i < repeatKeywords.length; ++i) {
						for (String s : repeatKeywords[i]) {
							if (repeat.contains(s)) {
								ret |= repeatValue[i];
							}
						}
					}
					return ret;
				}
			} else {
				return AlarmItem.REPEAT_ONCE;
			}
		} catch (Exception e) {
			if (Debug.DBG)
				e.printStackTrace();
		}
		return AlarmItem.REPEAT_ONCE;
	}
}
