package com.jzj.alarm.ui;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Vibrator;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.jzj.alarm.ToastView;
import com.jzj.alarm.R;
import com.jzj.alarm.core.AlarmItem;
import com.jzj.alarm.core.AlarmMgr;
import com.jzj.alarm.core.SettingMgr;
import com.jzj.alarm.ui.tools.AlarmController;
import com.jzj.alarm.ui.tools.MediaTools;
import com.jzj.alarm.ui.tools.SimpleBuilder;
import com.jzj.alarm.ui.tools.Theme;
import com.jzj.util.Debug;
import com.jzj.util.SystemUtils;
import com.jzj.util.ui.ColorUtils;
import com.jzj.util.ui.LockedActivity;

/**
 * 闹铃启动界面
 * 
 * @author jzj
 */
public class AlarmStartActivity extends LockedActivity {

	private CountDownTimer timer;
	private SettingMgr set;
	private AlarmMgr am;
	private MediaPlayer player;
	private AlarmItem alarm;

	private TextView txNote;
	private TextView txTime;
	private Button bnDelay;
	private Button bnStop;

	/** 剩余推迟次数,为-1说明从Intent读取发生错误 **/
	private int remain = -1;
	private Vibrator vi;
	private float vol;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		am = AlarmController.getAlarmMgr(this);
		final Intent intent = this.getIntent();
		final int id = intent.getIntExtra(AlarmMgr.EXTRA_ID, -1);
		if (id != -1) {
			alarm = am.getAlarmById(id);
		}
		if (alarm == null) { // 没有获取到id,或者id没有对应的闹铃
			this.handleError("failed to get alarm id", true);
			return;
		}

		this.setContentView(R.layout.activity_alarm_start);

		set = SettingMgr.getInstance(this);

		// 获取剩余推迟次数
		remain = intent.getIntExtra(AlarmMgr.EXTRA_REMAIN_TIME, -1);

		if (Debug.DBG) {
			Debug.log(this, "alarm id = " + id + ", remain = " + remain);
		}

		this.setLocked(true);
		this.initViews();
	}

	public void onResume() {
		startAlert(); // 放在onResume:如果Activity被重启,重新启动闹铃
		super.onResume();
	}

	@Override
	public void onPause() {
		if (!isLocked()) { // 如果Activity已经被解锁,则停止闹铃
			stopAlert();
		}
		super.onPause();
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

	private final void initViews() {
		txNote = (TextView) this.findViewById(R.id.tx_alarm_notes);
		txTime = (TextView) this.findViewById(R.id.tx_alarm_time);
		bnDelay = (Button) this.findViewById(R.id.bn_alarm_delay);
		bnStop = (Button) this.findViewById(R.id.bn_alarm_stop);

		String s = alarm.getNotes();
		if (TextUtils.isEmpty(s))
			txNote.setText(R.string.alarm_no_notes);
		else
			txNote.setText(s);

		txTime.setText(String.format("%02d:%02d", alarm.getHour(),
				alarm.getMinute()));
		switch (alarm.getMode()) {
		case AlarmItem.MODE_NORMAL:
			bnStop.setText(R.string.alarm_stop);
			break;
		case AlarmItem.MODE_WAKEUP:
			bnStop.setText(R.string.alarm_pause);
			break;
		}
		bnDelay.setOnClickListener(listener);
		bnStop.setOnClickListener(listener);

		final int color = Theme.getRandomBgColor(this);
		txNote.setTextColor(color);
		bnDelay.setTextColor(color);
		bnStop.setTextColor(color);
		findViewById(R.id.layout_alarm_time).setBackgroundColor(
				ColorUtils.setAlpha(color, 200));
	}

	/**
	 * 启动所有闹铃提示信息:声音,震动
	 */
	private final void startAlert() {
		if (set.getViberate()) { // 重新启动震动
			if (vi != null)
				vi.cancel();
			vi = SystemUtils.startVibrate(this);
		}
		if (player == null || !player.isPlaying()) { // 重新启动响铃
			player = MediaTools.autoCreateMediaPlayer(this, set.getAlarmTone());
			if (player == null) {
				this.handleError("create media player failed", true);
				return;
			}
			player.setLooping(true);

			vol = SystemUtils.getAudioVolume(this); // 保存系统音量
			if (vol < 0.6f) {
				SystemUtils.setAudioVolume(this, 1f); // 系统音量设为最大
			}

			if (!set.getCreascendo()) {
				// 如果不是音量渐强,直接将音量设为最大值
				player.setVolume(1f, 1f);
				player.start();
			} else {
				// 音量渐强
				final float vol = 0f;
				player.setVolume(vol, vol);
				player.start();

				// 音量逐渐增大 100 ms * 200
				timer = new CountDownTimer(20000, 100) {

					@Override
					public void onTick(long remainMillis) {
						final float vol = (float) (20000 - remainMillis) / 20000f;
						player.setVolume(vol, vol);
					}

					@Override
					public void onFinish() {
						timer = null;
					}
				};
				timer.start();
			}
		}
	}

	/**
	 * 停止所有闹铃提示信息:声音,震动
	 */
	private final void stopAlert() {
		// Debug.log(this, "stop all alert");
		if (timer != null) {
			timer.cancel();
			timer = null;
		}
		if (player != null && player.isPlaying()) {
			try {
				player.stop();
				player.release();
				player = null;
			} catch (IllegalStateException e) {
				if (Debug.DBG)
					e.printStackTrace();
			}
		}
		SystemUtils.setAudioVolume(this, vol); // 还原系统音量
		if (vi != null) {
			vi.cancel();
			vi = null;
		}
	}

	private final void delayAlarm() {

		final String s;
		final boolean delay;
		final int maxDelayTimes = set.getMaxDelayTimes();

		if (maxDelayTimes <= 0) {
			/* 1.不限推迟次数:推迟闹铃 (maxDelayTimes <= 0) */
			// 闹铃已被推迟
			s = this.getString(R.string.alarm_delay_unlimited_times);
			delay = true;
		} else if (remain < 0 || remain > maxDelayTimes) {
			/* 2.发生错误:修正错误,推迟闹铃 (maxDelayTimes > 0, remainDelayTimes < 0 || remainDelayTimes > maxDelayTimes) */
			remain = maxDelayTimes - 1;
			s = this.getString(R.string.alarm_delay_unlimited_times);
			delay = true;
		} else if (remain > 0) {
			/* 3.还有剩余次数:计算刷新剩余次数,推迟闹铃 (maxDelayTimes > 0, 0 < remainDelayTimes <= maxDelayTimes) */
			--remain;
			// 闹铃已被推迟%1$d次,还可推迟%2$d次
			s = String.format(this.getString(R.string.alarm_delay_left_times),
					maxDelayTimes - remain, remain);
			delay = true;
		} else {
			/* 4.剩余次数为0:提示用户,不推迟闹铃 (maxDelayTimes > 0, remainDelayTimes == 0) */
			// 闹铃已被推迟%1$d次,无法再推迟
			s = String.format(
					this.getString(R.string.alarm_delay_no_times_left),
					maxDelayTimes);
			delay = false;
		}

		ToastView.show(this, s);
		if (delay) {
			am.setDelayedAlarm(set.getDelayTime() * 60, alarm, remain);
			this.finish();
		}
	}

	private final void pauseAlarm() {
		ToastView.show(this, R.string.alarm_paused);
		Intent i = new Intent(this, AlarmWaitActivity.class);
		i.putExtra(AlarmMgr.EXTRA_ID, alarm.getId());
		this.startActivity(i);
		this.finish();
	}

	private final void stopAlarm() {
		ToastView.show(this, R.string.alarm_stopped);
		// 如果闹铃仅重复一次，关闭闹铃
		if (alarm.getRepeat() == AlarmItem.REPEAT_ONCE) {
			am.setAlarmOnOffById(alarm.getId(), false);
		}
		// 设置下次闹铃
		am.calcSetNextAlarm();
		this.finish();
	}

	/** 点击监听器 **/
	private OnClickListener listener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			SimpleBuilder b = SimpleBuilder.from(AlarmStartActivity.this)
					.setNegativeButton(null);
			if (v == bnStop) {
				switch (alarm.getMode()) {
				case AlarmItem.MODE_WAKEUP:
					b.setTitle(R.string.alarm_confirm_pause)
							.setPositiveButton(new OnClickListener() {
								@Override
								public void onClick(View v) {
									pauseAlarm();
								}
							}).show();
					break;
				case AlarmItem.MODE_NORMAL:
					b.setTitle(R.string.alarm_confirm_stop)
							.setPositiveButton(new OnClickListener() {
								@Override
								public void onClick(View v) {
									stopAlarm();
								}
							}).show();
					break;
				}
			} else if (v == bnDelay) {
				b.setTitle(R.string.alarm_confirm_delay)
						.setPositiveButton(new OnClickListener() {
							@Override
							public void onClick(View v) {
								delayAlarm();
							}
						}).show();
			}
		}
	};
}
