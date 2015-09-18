package com.jzj.alarm.core;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.jzj.alarm.ui.AlarmReceiver;
import com.jzj.util.Debug;
import com.jzj.util.SystemUtils;

import android.content.Context;
import android.content.Intent;

/**
 * 供UI界面调用的闹铃数据接口,本类只负责数据,与UI界面没有关联
 * 
 * @author jzj
 */
public class AlarmMgr {

	/** 闹铃id **/
	public static final String EXTRA_ID = "id";

	/** 闹铃剩余推迟次数 **/
	public static final String EXTRA_REMAIN_TIME = "delay";

	private static AlarmMgr am;

	private Context context;
	private OnNextAlarmChangedListener listener;
	private AlarmAccess alarmAccess;
	private List<AlarmItem> alarms;
	private SettingMgr set;

	private AlarmMgr(Context context, OnNextAlarmChangedListener listener) {
		this.context = context;
		this.listener = listener;
		alarmAccess = new AlarmAccess(context);
		set = SettingMgr.getInstance(context);
		reloadData();
		// calcSetNextAlarm();
	}

	public static final AlarmMgr getInstance(Context context,
			OnNextAlarmChangedListener listener) {
		if (am == null)
			am = new AlarmMgr(context, listener);
		return am;
	}

	// ---------------------------数据加载------------------------------//
	private final void reloadData() {
		if (Debug.DBG)
			Debug.log(this, "reload data");
		if (alarms != null)
			alarms.clear();
		alarms = alarmAccess.loadAllAlarm();
	}

	// ---------------------------增------------------------------//
	public final boolean addAlarm(AlarmItem alarm) {
		if (alarm == null)
			return false;
		boolean r = alarmAccess.addAlarm(alarm);
		reloadData();
		calcSetNextAlarm();
		return r;
	}

	public final boolean addAlarms(List<AlarmItem> alarms) {
		if (alarms == null || alarms.size() == 0)
			return false;
		boolean r = true;
		for (AlarmItem alarm : alarms) {
			if (alarm == null)
				return false;
			r &= alarmAccess.addAlarm(alarm);
		}
		reloadData();
		calcSetNextAlarm();
		return r;
	}

	public final boolean addAlarms(AlarmItem[] alarms) {
		if (alarms == null || alarms.length == 0)
			return false;
		boolean r = true;
		for (AlarmItem alarm : alarms) {
			if (alarm == null)
				return false;
			r &= alarmAccess.addAlarm(alarm);
		}
		reloadData();
		calcSetNextAlarm();
		return r;
	}

	// ---------------------------删------------------------------//
	public final boolean delAlarmById(int id) {
		if (id < 0)
			return false;
		boolean r = alarmAccess.delAlarm(id);
		reloadData();
		calcSetNextAlarm();
		return r;
	}

	// ---------------------------查------------------------------//
	public final AlarmItem getAlarmByPos(int pos) {
		if (pos < 0 || pos >= alarms.size())
			return null;
		return alarms.get(pos);
	}

	public final AlarmItem getAlarmById(int id) {
		if (id < 0)
			return null;
		// return alarmAccess.getAlarmById(id);
		for (AlarmItem alarm : alarms) {
			if (alarm.getId() == id)
				return alarm;
		}
		return null;
	}

	public final int getAlarmCnt() {
		return alarms.size();
	}

	public final List<AlarmItem> getAllAlarms() {
		return alarms;
	}

	// ---------------------------改------------------------------//
	public final boolean setAlarmById(int id, AlarmItem alarm) {
		if (id < 0)
			return false;
		boolean r = alarmAccess.setAlarm(id, alarm);
		reloadData();
		calcSetNextAlarm();
		return r;
	}

	public final boolean setAlarmOnOffById(int id, boolean on) {
		if (id < 0)
			return false;
		boolean r = alarmAccess.setAlarmOnOff(id, on);
		reloadData();
		calcSetNextAlarm();
		return r;
	}

	// ---------------------------设置闹铃------------------------------//

	AlarmDataStruct infoDelay = null;

	/**
	 * 计算并设置下次闹铃
	 * 
	 * @return 没有闹铃则返回null,否则返回设置的闹铃信息
	 */
	public final AlarmDataStruct calcSetNextAlarm() {
		AlarmDataStruct info = null;
		if (infoDelay != null
				&& infoDelay.getAlarmTime() > System.currentTimeMillis()
				&& infoDelay.getAlarmItem().isOn()) {
			info = infoDelay;
		} else {
			long millisecond = Long.MAX_VALUE;
			AlarmItem alarm = null;

			for (AlarmItem a : alarms) {
				long t = a.calcNextAlarmTimeMillis();
				if (t < millisecond) {
					millisecond = t;
					alarm = a;
				}
			}

			if (alarm != null) {

				if (Debug.DBG) { // Debug
					StringBuilder b = new StringBuilder("set alarm = ");
					b.append(new SimpleDateFormat("yy-MM-dd HH:mm E")
							.format(new Date(millisecond)));
					b.append(", id = ").append(alarm.getId());
					Debug.log(this, b.toString());
				}

				setNextAlarm(millisecond, alarm, set.getMaxDelayTimes());
				info = new AlarmDataStruct(alarm, millisecond);
			} else {
				if (Debug.DBG)
					Debug.log(this, "cancel alarm");
				SystemUtils.cancelPendingIntent(context, getIntent());
			}
		}
		if (listener != null)
			listener.onNextAlarmChanged(info);
		return info;
	}

	/**
	 * 设置推迟的闹铃
	 * 
	 * @param secondsLater
	 *            推迟时间长度(与系统当前时间相比)
	 * @param alarm
	 *            闹铃项
	 * @param delayTime
	 *            还可推迟次数
	 * @return 返回设置的闹铃信息
	 */
	public final AlarmDataStruct setDelayedAlarm(long secondsLater,
			AlarmItem alarm, int delayTime) {
		long ms = System.currentTimeMillis() + 1000 * secondsLater;
		setNextAlarm(ms, alarm, delayTime);
		AlarmDataStruct info = new AlarmDataStruct(alarm, ms);
		infoDelay = info;
		if (listener != null)
			listener.onNextAlarmChanged(info);
		return info;
	}

	// ---------------------------内部方法------------------------------//
	/**
	 * 设置下次闹铃
	 * 
	 * @param millisecond
	 *            时间
	 * @param alarm
	 *            闹铃项
	 * @param delayTime
	 *            还可推迟次数
	 */
	private final void setNextAlarm(long millisecond, AlarmItem alarm,
			int delayTime) {
		Intent intent = getIntent();
		intent.putExtra(EXTRA_ID, alarm.getId());
		intent.putExtra(EXTRA_REMAIN_TIME, delayTime);
		SystemUtils.sendPendingIntent(context, millisecond, intent);
	}

	private final Intent getIntent() {
		return new Intent(context, AlarmReceiver.class);
	}

	/**
	 * 即将启动的一次闹铃发生变化
	 * 
	 * @author jzj
	 */
	public interface OnNextAlarmChangedListener {

		/**
		 * 即将启动的一次闹铃发生变化
		 * 
		 * @param info
		 *            没有闹铃则为null,否则为闹铃参数
		 */
		public void onNextAlarmChanged(AlarmDataStruct info);
	}
}
