package com.jzj.alarm.ui;

import com.jzj.alarm.ui.tools.AlarmController;
import com.jzj.util.Debug;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * 时间日期监听器
 * 
 * @author jzj
 */
public class TimeReceiver extends BroadcastReceiver {

	static final String ACTION_TIMEZONE_CHANGED = Intent.ACTION_TIMEZONE_CHANGED;
	static final String ACTION_DATE_CHANGED = Intent.ACTION_DATE_CHANGED;
	static final String ACTION_TIME_CHANGED = Intent.ACTION_TIME_CHANGED;

	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		if (Debug.DBG)
			Debug.log(this, action);
		AlarmController.getAlarmMgr(context).calcSetNextAlarm();
	}
}
