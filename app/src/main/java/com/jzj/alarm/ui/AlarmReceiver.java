package com.jzj.alarm.ui;

import com.jzj.util.Debug;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * 闹铃启动 Receiver，用于启动闹铃启动界面
 * 
 * @author jzj
 */
public class AlarmReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		if (Debug.DBG)
			Debug.log(this, "onReceive");
		intent.setClass(context, AlarmStartActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(intent);
	}
}
