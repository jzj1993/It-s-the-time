package com.jzj.alarm.ui.tools;

import android.content.Context;

import com.jzj.alarm.core.AlarmDataStruct;
import com.jzj.alarm.core.AlarmMgr;
import com.jzj.alarm.core.AlarmMgr.OnNextAlarmChangedListener;

/**
 * 本类用于协调AlarmMgr数据和NotificationMgr界面刷新逻辑
 *
 * @author jzj
 */
public class AlarmController {

	private static OnNextAlarmChangedListener ls;

	public static AlarmMgr getAlarmMgr(Context context) {
		return AlarmMgr.getInstance(context, getAlarmChangedListener(context));
	}

	private static OnNextAlarmChangedListener getAlarmChangedListener(
			final Context context) {
		if (ls == null) {
			ls = new OnNextAlarmChangedListener() {
				@Override
				public void onNextAlarmChanged(AlarmDataStruct info) {
					NotificationMgr.display(context, info);
				}
			};
		}
		return ls;
	}
}
