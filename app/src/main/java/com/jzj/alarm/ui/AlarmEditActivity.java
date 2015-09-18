package com.jzj.alarm.ui;

import java.util.Calendar;

import com.jzj.alarm.CirclePickerView;
import com.jzj.alarm.HexCheckBox;
import com.jzj.alarm.HexViewGroup;
import com.jzj.alarm.ToastView;
import com.jzj.alarm.R;
import com.jzj.alarm.core.AlarmItem;
import com.jzj.alarm.core.AlarmMgr;
import com.jzj.alarm.core.SettingMgr;
import com.jzj.alarm.ui.tools.AlarmController;
import com.jzj.alarm.ui.tools.AlarmString;
import com.jzj.alarm.ui.tools.Theme;
import com.jzj.alarm.ui.tools.SimpleBuilder;
import com.jzj.util.SystemUtils;
import com.jzj.util.ui.ColorUtils;
import com.jzj.util.ui.DisplayUtils;
import com.jzj.util.ui.PopupBuilder;

import android.app.Activity;
import android.content.DialogInterface;
import android.hardware.Sensor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.DatePicker;
import android.widget.PopupWindow;

/**
 * 新增/编辑闹铃界面
 * 
 * @author jzj
 */
public class AlarmEditActivity extends Activity implements OnClickListener,
		OnLongClickListener {

	private static final int[] week = AlarmItem.WEEK;
	private static final int idWeek[] = {
			R.id.hex_mon,
			R.id.hex_tue,
			R.id.hex_wed,
			R.id.hex_thu,
			R.id.hex_fri,
			R.id.hex_sat,
			R.id.hex_sun };

	private static final boolean MODE_EDIT = true; // 编辑已有闹铃模式
	private static final boolean MODE_NEW = false; // 新增闹铃模式

	private boolean mode = MODE_EDIT;
	private boolean repeatOnceFlag = true;

	private AlarmItem alarm;
	private AlarmString as;
	private AlarmMgr am;

	private HexCheckBox vHour, vMinute, vMode, vRepeat, vNotes;
	private HexCheckBox vWeek[] = new HexCheckBox[7]; // vMon~vSun
	private PopupWindow pop;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_alarm_edit);

		am = AlarmController.getAlarmMgr(this);

		final int id = this.getIntent().getIntExtra(AlarmMgr.EXTRA_ID, -1);
		if (id != -1) {
			alarm = am.getAlarmById(id);
		}
		// 传入的Intent没有指定闹铃id,或id对应闹铃不存在,则为新建闹铃模式
		if (alarm == null) {
			Calendar c = Calendar.getInstance();
			alarm = new AlarmItem(c.get(Calendar.HOUR_OF_DAY),
					c.get(Calendar.MINUTE));
			mode = MODE_NEW;
		} else {
			mode = MODE_EDIT;
		}
		as = new AlarmString(this, alarm);

		initViews();
		refreshViews();

		new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
			@Override
			public void run() {
				checkFirstRun();
			}
		}, 1000);
	}

	/**
	 * 首次运行
	 */
	private final void checkFirstRun() {
		SettingMgr set = SettingMgr.getInstance(this);
		if (set.isFirstRun(SettingMgr.FirstRun.EDIT)) {
			set.setFirstRun(SettingMgr.FirstRun.EDIT, false);
			Animation anim = AnimationUtils.loadAnimation(
					AlarmEditActivity.this,
					R.anim.scale_alpha_reverse_repeat_400);
			vHour.clearAnimation();
			vMinute.clearAnimation();
			vRepeat.clearAnimation();
			vHour.startAnimation(anim);
			vMinute.startAnimation(anim);
			vRepeat.startAnimation(anim);

			pop = PopupBuilder.from(this, R.layout.dialog_edit_hint)
					.setOnClickDismissView(R.id.tx_edit_hint_ok)
					.setAnimationStyle(R.style.quick_scale_anim)
					.setOnDismissListener(new PopupWindow.OnDismissListener() {
						@Override
						public void onDismiss() {
							vHour.clearAnimation();
							vMinute.clearAnimation();
							vRepeat.clearAnimation();
						}
					}).showUnder(vMode);
		}
	}

	private final void initViews() {
		HexViewGroup gp = (HexViewGroup) this
				.findViewById(R.id.hexgroup_edit_container);

		for (int i = 0; i < 7; ++i) {
			vWeek[i] = (HexCheckBox) gp.findViewById(idWeek[i]);
			vWeek[i].setOnClickListener(this);
		}

		vHour = (HexCheckBox) gp.findViewById(R.id.hex_hour);
		vMinute = (HexCheckBox) gp.findViewById(R.id.hex_minute);
		vMode = (HexCheckBox) gp.findViewById(R.id.hex_mode);
		vRepeat = (HexCheckBox) gp.findViewById(R.id.hex_repeat);
		vNotes = (HexCheckBox) gp.findViewById(R.id.hex_notes);

		vHour.setOnClickListener(this);
		vMinute.setOnClickListener(this);
		vMode.setOnClickListener(this);
		vRepeat.setOnClickListener(this);
		vNotes.setOnClickListener(this);

		vHour.setOnLongClickListener(this);
		vMinute.setOnLongClickListener(this);
		vRepeat.setOnLongClickListener(this);
	}

	private final void refreshViews() {
		this.refreshTime();
		this.refreshMode();
		this.refreshNotes();
		this.refreshRepeat();
		this.refreshWeekView();
	}

	private final void refreshTime() {
		// 将分钟转化为5分钟的整数倍
		int x = alarm.getMinute() % 5;
		if (x > 0) {
			alarm.setMinute(alarm.getMinute() - x);
		}
		vHour.setText(as.hour());
		vMinute.setText(as.minute());
	}

	private final void refreshMode() {
		vMode.setText(as.mode());
	}

	private final void refreshNotes() {
		vNotes.setText(as.notes());
	}

	private final void refreshRepeat() {
		vRepeat.setText(as.repeatSimple());
	}

	/**
	 * 刷新星期View的状态(Repeat)
	 */
	private final void refreshWeekView() {
		final int repeatStatus = alarm.getRepeat();
		for (int i = 0; i < 7; ++i) {
			if ((repeatStatus & week[i]) != 0) {
				vWeek[i].setChecked(true);
			} else {
				vWeek[i].setChecked(false);
			}
		}
	}

	private final void onClickTime(final boolean isHour) {
		// this.onLongClickTime(isHour);
		if (isHour) {
			alarm.setHour((alarm.getHour() + 1) % 24);
		} else {
			int m = alarm.getMinute();
			m = (m < 55) ? (m + 5) : 0;
			alarm.setMinute(m);
		}
		this.refreshTime();
	}

	private final void onLongClickTime(final boolean isHour) {

		final CirclePickerView picker = new CirclePickerView(this);
		picker.setCircleColor(0x88FFFFFF);
		picker.setArcColor(0xFFFFFFFF);
		picker.setTextColor(0xFFFFFFFF);
		if (isHour) {
			picker.setValue(1, 24, alarm.getHour());
		} else {
			picker.setValue(5, 60, alarm.getMinute());
		}

		int color = Theme.getRandomBgColor(this);
		color = ColorUtils.colorMixArgb(color, 0xCCAAAAAA);

		final int len = DisplayUtils.getInstance(this).getScreenWidth() * 4 / 5;

		SimpleBuilder.from(this).setTitle(R.string.edit_set_time_title)
				.setView(picker, new ViewGroup.LayoutParams(len, len))
				.setPositiveButton(new OnClickListener() {

					@Override
					public void onClick(View v) {
						if (isHour)
							alarm.setHour(picker.getCurrentValue());
						else
							alarm.setMinute(picker.getCurrentValue());
						refreshTime();
					}
				}).setNegativeButton(null).setBackgroundColor(color).show();
	}

	private final void onLongClickRepeat() {
		this.setDate();
	}

	private final void onClickMode() {
		switch (alarm.getMode()) {
		case AlarmItem.MODE_NORMAL:
			alarm.setMode(AlarmItem.MODE_WAKEUP);
			break;
		case AlarmItem.MODE_WAKEUP:
			alarm.setMode(AlarmItem.MODE_NORMAL);
			break;
		}
		this.refreshMode();
	}

	private final void onClickNotes() {
		final SimpleBuilder b = SimpleBuilder.from(this)
				.setTitle(R.string.please_input).setEditText(alarm.getNotes());
		b.setPositiveButton(new OnClickListener() {
			@Override
			public void onClick(View v) {
				alarm.setNotes(b.getEditString());
				refreshNotes();
			}
		}).setNegativeButton(null).show();
		SystemUtils.showIme(this, b.getEditText());
	}

	/**
	 * Repeat按钮按下响应
	 */
	private final void onClickRepeat() {
		switch (alarm.getRepeat()) {
		case AlarmItem.REPEAT_ONCE:
			if (!repeatOnceFlag) {
				repeatOnceFlag = true;
				this.setDate();
				return;
			} else {
				alarm.setRepeat(AlarmItem.REPEAT_WEEKDAY);
			}
			break;
		case AlarmItem.REPEAT_WEEKDAY:
			alarm.setRepeat(AlarmItem.REPEAT_WEEKEND);
			break;
		case AlarmItem.REPEAT_WEEKEND:
			alarm.setRepeat(AlarmItem.REPEAT_EVERYDAY);
			break;
		case AlarmItem.REPEAT_EVERYDAY:
		default:
			alarm.setRepeat(AlarmItem.REPEAT_ONCE);
			alarm.setDate(null);
			repeatOnceFlag = false;
			// this.setDate();
			break;
		}
		this.refreshRepeat();
		this.refreshWeekView();
	}

	/**
	 * 星期View点击响应
	 * 
	 * @param day
	 *            0~6:MON~SUN
	 */
	private final void onClickWeekView(int day) {
		alarm.setRepeat((char) (alarm.getRepeat() ^ week[day]));
		vWeek[day].flipChecked();
		this.refreshRepeat();
	}

	private final void setDate() {

		vRepeat.setText(R.string.alarm_repeat_date);

		final DatePicker date = new DatePicker(this);
		Calendar c = alarm.getDate();
		if (c != null) {
			date.updateDate(c.get(Calendar.YEAR), c.get(Calendar.MONTH),
					c.get(Calendar.DAY_OF_MONTH));
		}
		SimpleBuilder.from(this).setTitle(R.string.edit_select_date)
				.setView(date, null).setPositiveButton(new OnClickListener() {
					@Override
					public void onClick(View v) {
						Calendar c = Calendar.getInstance();
						c.set(Calendar.YEAR, date.getYear());
						c.set(Calendar.MONTH, date.getMonth());
						c.set(Calendar.DAY_OF_MONTH, date.getDayOfMonth());
						alarm.setDate(c);
						alarm.setRepeat(AlarmItem.REPEAT_ONCE);
						refreshRepeat();
						refreshWeekView();
					}
				}).setNegativeButton(null).getDialogBuilder()
				.setOnCancelListener(new DialogInterface.OnCancelListener() {
					@Override
					public void onCancel(DialogInterface dialog) {
						alarm.setDate(null);
						alarm.setRepeat(AlarmItem.REPEAT_ONCE);
						refreshRepeat();
						refreshWeekView();
					}
				}).show();
	}

	/**
	 * 添加闹铃
	 */
	private final void addAlarm() {
		final int res;
		if (mode == MODE_NEW) {
			// 如果设置为起床模式，则检测是否有加速度传感器
			if (alarm.getMode() == AlarmItem.MODE_WAKEUP
					&& (!SystemUtils.checkSensor(this,
							Sensor.TYPE_ACCELEROMETER))) {
				ToastView.show(this, R.string.edit_no_sensor);
				return;
			}
			res = am.addAlarm(alarm) ? R.string.edit_alarm_added
					: R.string.edit_alarm_failed_to_add;
		} else {
			res = am.setAlarmById(alarm.getId(), alarm) ? R.string.edit_alarm_edited
					: R.string.edit_alarm_failed_to_edit;
		}
		ToastView.show(this, res);
		this.finish();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.hex_mon:
			onClickWeekView(0);
			break;
		case R.id.hex_tue:
			onClickWeekView(1);
			break;
		case R.id.hex_wed:
			onClickWeekView(2);
			break;
		case R.id.hex_thu:
			onClickWeekView(3);
			break;
		case R.id.hex_fri:
			onClickWeekView(4);
			break;
		case R.id.hex_sat:
			onClickWeekView(5);
			break;
		case R.id.hex_sun:
			onClickWeekView(6);
			break;
		case R.id.hex_repeat:
			onClickRepeat();
			break;
		case R.id.hex_mode:
			onClickMode();
			break;
		case R.id.hex_hour:
			onClickTime(true);
			break;
		case R.id.hex_minute:
			onClickTime(false);
			break;
		case R.id.hex_notes:
			onClickNotes();
			break;
		}
	}

	@Override
	public boolean onLongClick(View v) {
		switch (v.getId()) {
		case R.id.hex_hour:
			onLongClickTime(true);
			break;
		case R.id.hex_minute:
			onLongClickTime(false);
			break;
		case R.id.hex_repeat:
			onLongClickRepeat();
			break;
		}
		return true;
	}

	@Override
	public void onBackPressed() {

		if (pop != null && pop.isShowing()) {
			pop.dismiss();
			return;
		}

		final int titleRes = (mode == MODE_NEW) ? R.string.confirm_add
				: R.string.confirm_edit;

		SimpleBuilder.from(this).setTitle(titleRes)
				.setPositiveButton(new OnClickListener() {
					@Override
					public void onClick(View v) {
						addAlarm();
					}
				}).setNegativeButton(new OnClickListener() {
					@Override
					public void onClick(View v) {
						AlarmEditActivity.this.finish();
					}
				}).show();
	}
}
