package com.jzj.alarm.ui.tools;

import com.jzj.alarm.R;
import com.jzj.alarm.core.AlarmDataStruct;
import com.jzj.alarm.core.AlarmItem;
import com.jzj.alarm.core.SettingMgr;
import com.jzj.alarm.ui.MainActivity;
import com.jzj.util.CalendarPlus;
import com.jzj.util.Debug;

import android.app.Notification.Builder;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

/**
 * Notification界面，通过AlarmMgr的回调刷新显示信息
 * 
 * @author jzj
 */
public class NotificationMgr {

	private static final int ntId = 0;

	private static NotificationMgr noti;

	private final Context context;
	private final SettingMgr set;
	private final Builder b;
	private final NotificationManager nm;

	private NotificationMgr(Context context) {

		this.context = context;
		set = SettingMgr.getInstance(context);
		b = new Builder(context);
		nm = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);

		// 点击时启动的Intent
		Intent intent = new Intent(context, MainActivity.class);
		PendingIntent pi = PendingIntent.getActivity(context, 0, intent, 0);

		String title = context.getString(R.string.app_name);

		b.setWhen(System.currentTimeMillis()) // 显示时间
				.setSmallIcon(R.drawable.ic_launcher) // 图标
				.setContentTitle(title) // 标题
				.setDefaults(0) // 关闭声音，震动，闪屏等默认值
				.setOngoing(true) // "正在运行"组
				.setAutoCancel(false) // 不清除通知
				.setContentIntent(pi); // 点击启动Intent
	}

	private static NotificationMgr getInstance(Context context) {
		if (noti == null)
			noti = new NotificationMgr(context);
		return noti;
	}

	public static final void display(Context context, AlarmDataStruct info) {
		getInstance(context).displayAlarm(info);
	}

	private void displayAlarm(AlarmDataStruct info) {
		boolean hasAlarm = false;
		if (info != null && info.getAlarmItem() != null
				&& info.getAlarmTime() > 0) {
			hasAlarm = true;
		}
		final boolean disp;
		switch (set.getNotificationMode()) {
		default:
		case ALWAYS:
			disp = true;
			break;
		case NECESSARY:
			disp = hasAlarm;
			break;
		case NEVER:
			disp = false;
			break;
		}
		if (disp) {
			b.setContentText(getNotiText(info, hasAlarm));
			nm.notify(ntId, b.getNotification());
		} else {
			nm.cancel(ntId);
		}
	}

	private CharSequence getNotiText(AlarmDataStruct info, boolean hasAlarm) {

		if (!hasAlarm) {
			return context.getString(R.string.noti_no_alarm);
		}

		CalendarPlus now = new CalendarPlus();
		CalendarPlus c = new CalendarPlus(info.getAlarmTime());

		// 0今天，1明天，2后天，<7可能为本周，其他：普通
		final long dayDiff = CalendarPlus.calcDayDiff(c, now);
		boolean sameWeek = false;
		if (dayDiff > 2 && dayDiff < 7) {
			sameWeek = now.isInSameWeek(c, true);
		}

		if (Debug.DBG) {
			Debug.log(this, now.toString());
			Debug.log(this,
					CalendarPlus.format(now, CalendarPlus.PATTERN_YMD_HMS_E));

			Debug.log(this, c.toString());
			Debug.log(this, CalendarPlus.format(info.getAlarmTime(),
					CalendarPlus.PATTERN_YMD_HMS_E));

			Debug.log(this, "dayDiff = " + dayDiff);
			Debug.log(this, "sameWeek = " + sameWeek);
		}

		final int resId;
		switch (info.getAlarmItem().getMode()) {
		case AlarmItem.MODE_WAKEUP:
			if (sameWeek)
				resId = R.string.noti_wakeup_this_week;
			else if (dayDiff == 0)
				resId = R.string.noti_wakeup_today;
			else if (dayDiff == 1)
				resId = R.string.noti_wakeup_tomorrow;
			else if (dayDiff == 2)
				resId = R.string.noti_wakeup_day_after_tomorrow;
			else
				resId = R.string.noti_wakeup_other;
			break;
		case AlarmItem.MODE_NORMAL:
		default:
			if (sameWeek)
				resId = R.string.noti_normal_this_week;
			else if (dayDiff == 0)
				resId = R.string.noti_normal_today;
			else if (dayDiff == 1)
				resId = R.string.noti_normal_tomorrow;
			else if (dayDiff == 2)
				resId = R.string.noti_normal_day_after_tomorrow;
			else
				resId = R.string.noti_normal_other;
			break;
		}
		return CalendarPlus.format(info.getAlarmTime(),
				context.getString(resId));
	}
}
