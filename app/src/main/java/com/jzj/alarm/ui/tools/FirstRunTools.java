package com.jzj.alarm.ui.tools;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.content.Context;
import android.content.Intent;

import com.jzj.alarm.R;
import com.jzj.alarm.core.AlarmItem;
import com.jzj.alarm.core.AlarmMgr;
import com.jzj.alarm.core.SettingMgr;
import com.jzj.alarm.core.SettingMgr.FirstRun;
import com.jzj.alarm.ui.MainActivity;
import com.jzj.alarm.ui.SplashActivity;
import com.jzj.util.SystemUtils;

public class FirstRunTools {

	/**
	 * 检测并创建快捷方式
	 *
	 * @param context
	 */
	public static final void checkAddShortcut(Context context) {

		SettingMgr set = SettingMgr.getInstance(context);
		if (!set.isFirstRun(FirstRun.SHORTCUT))
			return;

		set.setFirstRun(FirstRun.SHORTCUT, false);

		Intent intent = new Intent(Intent.ACTION_MAIN);
		intent.setClass(context, SplashActivity.class);
		SystemUtils.addShortcut(context, intent, R.string.app_name,
				R.drawable.ic_launcher);

		Intent intent1 = new Intent("MAIN");
		intent1.setClass(context, MainActivity.class);
		intent1.putExtra(MainActivity.EXTRA_VOICE, true);
		SystemUtils.addShortcut(context, intent1, R.string.app_voice_add,
				R.drawable.ic_launcher_voice);
	}

	/**
	 * 检测并添加示例闹铃,按备注搜索,避免重复添加
	 *
	 * @param context
	 * @param am
	 */
	public static final void checkAddSampleAlarm(Context context) {

		SettingMgr set = SettingMgr.getInstance(context);
		if (!set.isFirstRun(FirstRun.TIPS_SAMPLE_ALARM))
			return;

		set.setFirstRun(FirstRun.TIPS_SAMPLE_ALARM, false);

		AlarmMgr am = AlarmController.getAlarmMgr(context);
		List<AlarmItem> as = new ArrayList<AlarmItem>();
		List<AlarmItem> als = am.getAllAlarms();
		final String[] sampleAlarmTitle = {
				"点击开关闹铃",
				"双击删除闹铃",
				"长按编辑闹铃",
				"摇一摇换主题" };
		for (int i = 0; i < sampleAlarmTitle.length; ++i) {
			final String s = sampleAlarmTitle[i];
			boolean find = false;
			for (AlarmItem a : als) {
				if (s.equals(a.getNotes())) {
					find = true;
					break;
				}
			}
			if (!find) {
				switch (i) {
					case 0: // "点击开关闹铃"
						int r = AlarmItem.MON | AlarmItem.WED | AlarmItem.FRI;
						as.add(new AlarmItem(7, 30).setNotes(s).setRepeat(r)
								.setOnOff(true));
						break;
					case 1: // "双击删除闹铃"
						as.add(new AlarmItem(8, 0).setNotes(s)
								.setRepeat(AlarmItem.REPEAT_ONCE).setOnOff(false));
						break;
					case 2: // "长按编辑闹铃"
						as.add(new AlarmItem(8, 10).setNotes(s)
								.setRepeat(AlarmItem.REPEAT_WEEKDAY)
								.setMode(AlarmItem.MODE_WAKEUP).setOnOff(false));
						break;
					case 3: // "摇一摇换主题"
						Calendar c = Calendar.getInstance();
						c.add(Calendar.DAY_OF_YEAR, 1);
						as.add(new AlarmItem(8, 30).setNotes(s)
								.setRepeat(AlarmItem.REPEAT_ONCE).setDate(c)
								.setOnOff(false));
						break;
				}
			}
		}
		am.addAlarms(as);
	}
}
