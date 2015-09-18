package com.jzj.alarm.core;

import com.jzj.alarm.R;
import com.jzj.util.Preference;

import android.content.Context;

/**
 * 设置数据管理，给UI提供方便的访问接口，包括设置项目、默认值的管理等
 * 
 * @author jzj
 */
public class SettingMgr extends Preference {

	/**
	 * 枚举类型定义所有设置项(类型 = 默认值)(其他说明)
	 * 
	 * @author jzj
	 */
	public static enum SettingItem {
		// ----------------------- //
		/**
		 * 首页按钮默认为语音识别(boolean = false)
		 */
		MAIN_VOICE,
		/**
		 * Notification显示模式(int / Noti = NECESSARY)
		 */
		NOTI_MODE,
		/**
		 * 颜色主题(int = 0)
		 */
		COLOR_THEME,
		// ----------------------- //
		/**
		 * 闹铃震动(boolean = true)
		 */
		VIBRATE,
		/**
		 * 音量渐强(boolean = true)
		 */
		CRESCENDO,
		/**
		 * 闹铃暂停倒计时时钟滴答声(int = R.raw.clock / ClockTone = CLOCK)
		 * <p>
		 * (int = 0 / ClockTone = NONE 则为关闭)
		 */
		CLOCK_TONE,
		/**
		 * 闹铃音(String = DEFAULT_TONE)
		 */
		ALARM_TONE,
		/**
		 * 闹铃锁定音(String = DEFAULT_TONE)
		 */
		ALARM_LOCKED_TONE,
		// ----------------------- //
		/**
		 * 闹铃最大推迟次数(int = 2) (为0则不限制推迟次数)
		 */
		MAX_DELAY,
		/**
		 * 推迟一次时长(int = 10)(单位min)
		 */
		DELAY_TIME,
		/**
		 * 闹铃暂停等待时长(int = 20) (单位min)
		 */
		PAUSE_TIME,
		// ----------------------- //
		/**
		 * 计步器灵敏度(int = 30) (值越小越灵敏)
		 */
		SENSITIVITY,
		/**
		 * 解锁步数(int = 20)
		 */
		UNLOCK_STEPS,
		// ----------------------- //
		/**
		 * 睡眠知识
		 */
		TIPS,
		/**
		 * 引导界面
		 */
		GUIDE,
		/**
		 * 关于
		 */
		ABOUT,
		// ----------------------- //
		/**
		 * 首次运行(int = 0 / boolean = true)
		 * <p>
		 * (首次运行则相应位为0,运行结束置1)
		 * <p>
		 * (传入参数类型为FirstRun)
		 */
		FIRST_RUN,
		/**
		 * 清除所有设置(还原默认值)
		 */
		CLEAR,
	}

	/** 首次运行项 **/
	public static enum FirstRun {
		/** 添加SampleAlarm */
		TIPS_SAMPLE_ALARM,
		/** 启动引导界面 */
		GUIDE,
		/** 添加快捷方式 */
		SHORTCUT,
		/** 闹铃编辑界面提示 */
		EDIT,
	}

	/** Notification显示模式 **/
	public static enum Noti {
		ALWAYS, NECESSARY, NEVER
	}

	/** 时钟倒计时铃音 **/
	public static enum ClockTone {
		CLOCK, TICK, WATER, NONE
	}

	private static final int[] rawClockTone = {
			R.raw.tick_clock,
			R.raw.tick_clock2,
			R.raw.tick_water,
			0 };

	/** 默认铃音 **/
	public static final String DEFAULT_TONE = "default";

	private static final String FIRST_RUN_PREFIX = "FIRST_RUN_";

	private static SettingMgr set;

	private SettingMgr(Context context) {
		super(context);
	}

	public static final SettingMgr getInstance(Context context) {
		if (set == null)
			set = new SettingMgr(context);
		return set;
	}

	/** @see SettingItem#MAIN_VOICE **/
	public final boolean getMainVoice() {
		return this.getBoolean(SettingItem.MAIN_VOICE.name(), false);
	}

	/** @see SettingItem#MAIN_VOICE **/
	public final void setMainVoice(boolean b) {
		this.put(SettingItem.MAIN_VOICE.name(), b);
	}

	/** @see SettingItem#VIBRATE **/
	public final boolean getViberate() {
		return this.getBoolean(SettingItem.VIBRATE.name(), true);
	}

	/** @see SettingItem#VIBRATE **/
	public final void setViberate(boolean b) {
		this.put(SettingItem.VIBRATE.name(), b);
	}

	/** @see SettingItem#CRESCENDO **/
	public final boolean getCreascendo() {
		return this.getBoolean(SettingItem.CRESCENDO.name(), true);
	}

	/** @see SettingItem#CRESCENDO **/
	public final void setCreascendo(boolean b) {
		this.put(SettingItem.CRESCENDO.name(), b);
	}

	/** @see SettingItem#FIRST_RUN **/
	public final boolean isFirstRun(FirstRun k) {
		return this.getBoolean(FIRST_RUN_PREFIX + k.name(), true);
	}

	/** @see SettingItem#FIRST_RUN **/
	public final void setFirstRun(FirstRun k, boolean b) {
		this.put(FIRST_RUN_PREFIX + k.name(), b);
	}

	/**
	 * 重置全部FirstRun为true TODO
	 * 
	 * @see SettingItem#FIRST_RUN
	 */
	public final void resetFirstRun() {
		for (FirstRun k : FirstRun.values()) {
			this.setFirstRun(k, true);
		}
	}

	/** @see SettingItem#NOTI_MODE **/
	public final Noti getNotificationMode() {
		int d = this.getInt(SettingItem.NOTI_MODE.name(),
				Noti.NECESSARY.ordinal());
		return Noti.values()[d];
	}

	/** @see SettingItem#NOTI_MODE **/
	public final void setNotificationMode(Noti mode) {
		this.put(SettingItem.NOTI_MODE.name(), mode.ordinal());
	}

	/** @see SettingItem#PAUSE_TIME **/
	public final int getAlarmPauseTime() {
		return this.getInt(SettingItem.PAUSE_TIME.name(), 20);
	}

	/** @see SettingItem#PAUSE_TIME **/
	public final void setAlarmPauseTime(int d) {
		this.put(SettingItem.PAUSE_TIME.name(), d);
	}

	/** @see SettingItem#SENSITIVITY **/
	public final int getSensitivity() {
		return this.getInt(SettingItem.SENSITIVITY.name(), 30);
	}

	/** @see SettingItem#SENSITIVITY **/
	public final void setSencitivity(int d) {
		this.put(SettingItem.SENSITIVITY.name(), d);
	}

	/** @see SettingItem#UNLOCK_STEPS **/
	public final int getUnlockSteps() {
		return this.getInt(SettingItem.UNLOCK_STEPS.name(), 20);
	}

	/** @see SettingItem#UNLOCK_STEPS **/
	public final void setUnlockSteps(int d) {
		this.put(SettingItem.UNLOCK_STEPS.name(), d);
	}

	/** @see SettingItem#DELAY_TIME **/
	public final int getDelayTime() {
		return this.getInt(SettingItem.DELAY_TIME.name(), 10);
	}

	/** @see SettingItem#DELAY_TIME **/
	public final void setDelayTime(int d) {
		this.put(SettingItem.DELAY_TIME.name(), d);
	}

	/** @see SettingItem#MAX_DELAY **/
	public final int getMaxDelayTimes() {
		return this.getInt(SettingItem.MAX_DELAY.name(), 2);
	}

	/** @see SettingItem#MAX_DELAY **/
	public final void setMaxDelayTime(int d) {
		this.put(SettingItem.MAX_DELAY.name(), d);
	}

	/** @see SettingItem#CLOCK_TONE **/
	public final int getRawClockTone() {
		return this.getInt(SettingItem.CLOCK_TONE.name(), rawClockTone[0]);
	}

	/** @see SettingItem#CLOCK_TONE **/
	public final ClockTone getClockTone() {
		int raw_tone = this.getRawClockTone();
		for (int i = 0; i < rawClockTone.length; ++i) {
			if (raw_tone == rawClockTone[i])
				return ClockTone.values()[i];
		}
		return ClockTone.CLOCK;
	}

	/** @see SettingItem#CLOCK_TONE **/
	public final void setClockTones(ClockTone tone) {
		this.put(SettingItem.CLOCK_TONE.name(), rawClockTone[tone.ordinal()]);
	}

	/** @see SettingItem#ALARM_TONE **/
	public final String getAlarmTone() {
		return this.getString(SettingItem.ALARM_TONE.name(), DEFAULT_TONE);
	}

	/** @see SettingItem#ALARM_TONE **/
	public final void setAlarmTone(String tone) {
		if (tone == null)
			tone = DEFAULT_TONE;
		this.put(SettingItem.ALARM_TONE.name(), tone);
	}

	/** @see SettingItem#ALARM_LOCKED_TONE **/
	public final String getAlarmLockedTone() {
		return this.getString(SettingItem.ALARM_LOCKED_TONE.name(),
				DEFAULT_TONE);
	}

	/** @see SettingItem#ALARM_LOCKED_TONE **/
	public final void setAlarmLockedTone(String tone) {
		if (tone == null)
			tone = DEFAULT_TONE;
		this.put(SettingItem.ALARM_LOCKED_TONE.name(), tone);
	}

	/** @see SettingItem#COLOR_THEME **/
	public final int getTheme() {
		return this.getInt(SettingItem.COLOR_THEME.name(), 0);
	}

	/** @see SettingItem#COLOR_THEME **/
	public final void setTheme(int d) {
		this.put(SettingItem.COLOR_THEME.name(), d);
	}
}
