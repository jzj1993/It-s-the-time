package com.jzj.alarm.ui;

import com.jzj.alarm.ui.tools.AlarmController;
import com.jzj.util.Debug;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * 开机启动 Receiver，用于重新设定闹铃
 * 
 * @author jzj
 */
public class BootReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		if (Debug.DBG)
			Debug.log(this, "onReceive");
		AlarmController.getAlarmMgr(context).calcSetNextAlarm();
	}
}
