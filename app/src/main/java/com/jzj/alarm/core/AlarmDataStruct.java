package com.jzj.alarm.core;

public class AlarmDataStruct {

	private final AlarmItem alarm;
	private final long time;

	public AlarmDataStruct(AlarmItem alarm, long time) {
		this.alarm = alarm;
		this.time = time;
	}

	public final AlarmItem getAlarmItem() {
		return alarm;
	}

	public final long getAlarmTime() {
		return time;
	}
}
