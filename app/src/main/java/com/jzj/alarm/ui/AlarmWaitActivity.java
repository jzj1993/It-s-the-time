package com.jzj.alarm.ui;

import com.jzj.alarm.ClockView;
import com.jzj.alarm.ToastView;
import com.jzj.alarm.R;
import com.jzj.alarm.core.AlarmItem;
import com.jzj.alarm.core.AlarmMgr;
import com.jzj.alarm.core.SettingMgr;
import com.jzj.alarm.ui.tools.AlarmController;
import com.jzj.alarm.ui.tools.Theme;
import com.jzj.alarm.ui.tools.MediaTools;
import com.jzj.util.Debug;
import com.jzj.util.Pedometer;
import com.jzj.util.SystemUtils;
import com.jzj.util.ui.LockedActivity;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.TextView;

/**
 * 闹铃暂停并倒计时等待用户起床界面 / 闹铃锁定界面（起床模式才会启动本Activity）
 * 
 * @author jzj
 */
public class AlarmWaitActivity extends LockedActivity {

	private float vol;
	private int alarmId;
	private int currentSteps = 0; // 目前已经进行的步数
	private int unlockSteps; // 解锁闹铃需要步行的步数

	private SettingMgr set;
	private AlarmMgr am;
	private MediaPlayer player;

	/** 计步器：每运动一步，刷新当前运动量显示；运动量足够，解锁闹铃 */
	private Pedometer pedometer;

	/** 倒计时：每秒刷新ClockView；倒计时结束调用lockAlarm */
	private CountDownTimer timer;

	private ClockView clockView;
	private TextView txRemainTime, txCurSteps;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		View v = getLayoutInflater()
				.inflate(R.layout.activity_alarm_wait, null);
		v.setBackgroundColor(Theme.getRandomBgColor(this));
		this.setContentView(v);

		alarmId = this.getIntent().getIntExtra(AlarmMgr.EXTRA_ID, -1);

		if (alarmId == -1) {
			this.handleError("failed to get alarm id", true);
			return;
		}

		set = SettingMgr.getInstance(this);

		this.setLocked(true);

		vol = SystemUtils.getAudioVolume(this); // 保存系统音量
		if (vol < 0.5f)
			SystemUtils.setAudioVolume(this, 0.5f); // 系统音量设为0.5f

		am = AlarmController.getAlarmMgr(this);

		initViews();
		startClock();
		startPedometer();
	}

	@Override
	public void onPause() {
		if (!isLocked()) {
			if (timer != null) {
				timer.cancel();
				timer = null;
			}
			if (player != null && player.isPlaying()) {
				player.stop();
				player = null;
			}
			SystemUtils.stopSensor(this, sensorListener);
			SystemUtils.setAudioVolume(this, vol); // 还原系统音量
		}
		super.onPause();
	}

	/**
	 * 初始化View
	 */
	private final void initViews() {
		clockView = (ClockView) this.findViewById(R.id.clock_wait_count_down);
		txRemainTime = (TextView) this.findViewById(R.id.tx_wait_remain_time);
		txCurSteps = (TextView) this.findViewById(R.id.tx_wait_cur_steps);
	}

	/**
	 * 初始化并启动倒计时定时器
	 */
	private final void startClock() {

		player = MediaTools.createMediaPlayer(this, set.getRawClockTone());

		final long seconds = set.getAlarmPauseTime() * 60;
		this.refreshClockView(seconds);

		timer = new CountDownTimer(seconds * 1000, 1000) {

			@Override
			public void onTick(long remainMillis) {
				try {
					if (player != null && !player.isPlaying()) {
						player.start();
					}
				} catch (Exception e) {
					if (Debug.DBG)
					e.printStackTrace();
				}
				refreshClockView(remainMillis / 1000);
			}

			@Override
			public void onFinish() {
				lockAlarm();
				timer = null;
			}
		};
		timer.start();
	}

	/**
	 * 初始化并启动计步器
	 */
	private final void startPedometer() {
		unlockSteps = set.getUnlockSteps();
		refreshStepsView(0, unlockSteps);
		pedometer = new Pedometer(set.getSensitivity()) {

			@Override
			protected void onStep() {
				++currentSteps;
				refreshStepsView(currentSteps, unlockSteps);
				if (currentSteps >= unlockSteps) {
					unlockAlarm();
				}
			}
		};
		// 如果没有相应传感器,会启动失败,应在设置闹铃时检测
		if (!SystemUtils.startSensor(this, Sensor.TYPE_ACCELEROMETER,
				sensorListener)) {
			ToastView.show(this, R.string.alarm_wait_sensor_failure);
			this.handleError("failed to start accelerometer sensor", true);
		}
	}

	/**
	 * 倒计时结束，锁定闹铃(计时器计时结束调用本函数，计步器继续工作)
	 */
	private final void lockAlarm() {

		txRemainTime.setText(R.string.alarm_wait_locked);
		ToastView.show(this, R.string.alarm_wait_locked);

		SystemUtils.setAudioVolume(this, 1f); // 系统音量设为最大

		player = MediaTools.autoCreateMediaPlayer(this,
				set.getAlarmLockedTone());
		if (player == null) {
			this.handleError("create media player failed", false);
		} else {
			player.setLooping(true);
			player.start();
		}
	}

	/**
	 * 解除/解锁闹铃(计步器达到指定的步数调用此函数解除闹铃,
	 * 可能发生在闹铃暂停时,也可能发生在闹铃锁定时)
	 */
	private final void unlockAlarm() {
		ToastView.show(this, R.string.alarm_wait_unlocked);

		// 如果闹铃仅重复一次，关闭闹铃
		AlarmItem a = am.getAlarmById(alarmId);
		if (a != null && a.getRepeat() == AlarmItem.REPEAT_ONCE)
			am.setAlarmOnOffById(alarmId, false);

		// 设置下次闹铃
		am.calcSetNextAlarm();
		this.finish();
	}

	/**
	 * 刷新Clock倒计时时间显示
	 * 
	 * @param remainSeconds
	 *            剩余秒数
	 */
	private final void refreshClockView(long remainSeconds) {
		final int m = (int) (remainSeconds / 60);
		final int s = (int) (remainSeconds % 60);
		clockView.setTime(m, s);
		txRemainTime.setText(String.format(
				this.getString(R.string.alarm_wait_remain_time), m, s));
	}

	/**
	 * 刷新对当前运动量的显示
	 * 
	 * @param curSteps
	 * @param unlockSteps
	 */
	private final void refreshStepsView(int curSteps, int unlockSteps) {
		txCurSteps.setText(String.format(
				this.getString(R.string.alarm_wait_cur_steps), curSteps,
				unlockSteps));
	}

	/**
	 * 发生错误
	 * 
	 * @param errorMsg
	 *            错误信息
	 * @param serious
	 *            是否为严重错误(如果是,无法继续执行,直接退出)
	 */
	private final void handleError(String errorMsg, boolean serious) {
		if (Debug.DBG)
			Debug.error(this, errorMsg);
		if (serious) {
			if (am == null)
				am = AlarmController.getAlarmMgr(this);
			am.calcSetNextAlarm();
			ToastView.show(this, "a serious error occured!");
			this.finish();
		}
	}

	/**
	 * 加速度传感器监听器：将加速度数据传入计步器对象进行分析
	 */
	private SensorEventListener sensorListener = new SensorEventListener() {
		@Override
		public void onSensorChanged(SensorEvent event) {
			if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
				pedometer.putSensorData(event.values);
			}
		}

		@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy) {

		}
	};
}
