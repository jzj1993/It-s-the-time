package com.jzj.alarm.ui;

import java.util.List;
import com.jzj.alarm.HexListView;
import com.jzj.alarm.HexAlarmView;
import com.jzj.alarm.HexView;
import com.jzj.alarm.R;
import com.jzj.alarm.ScaleImageView;
import com.jzj.alarm.ToastView;
import com.jzj.alarm.core.AlarmItem;
import com.jzj.alarm.core.AlarmMgr;
import com.jzj.alarm.core.SettingMgr;
import com.jzj.alarm.ui.tools.AlarmController;
import com.jzj.alarm.ui.tools.DoubleClicker;
import com.jzj.alarm.ui.tools.FirstRunTools;
import com.jzj.alarm.ui.tools.PopMenu;
import com.jzj.alarm.ui.tools.Theme;
import com.jzj.alarm.ui.tools.SimpleBuilder;
import com.jzj.alarm.ui.tools.UndoBar;
import com.jzj.alarm.voice.SemanticAlarm;
import com.jzj.alarm.voice.SemanticResult;
import com.jzj.alarm.voice.Understander;
import com.jzj.util.AppErrorController;
import com.jzj.util.Debug;
import com.jzj.util.SystemUtils;
import com.jzj.util.ui.DialogBuilder;

import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Looper;
import android.os.Message;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.PopupWindow;

/**
 * 主界面(闹铃列表界面)
 * 
 * @author jzj
 */
public class MainActivity extends Activity {

	public static final String EXTRA_VOICE = "VOICE";

	private AlarmMgr am;
	private SettingMgr set;
	private Understander ud;
	private Theme theme;

	private HexView vMenu;
	private HexView vNew;
	private HexListView container;
	private View themeImgContainer;
	private Animation themeImgAnim;
	private ScaleImageView themeImg;
	private Dialog voiceDialog;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 显示虚拟菜单键
		SystemUtils.showVirtualMenuKey(this);
		setContentView(R.layout.activity_alarm_list);
		// 检测上次错误
		checkErrorLog();
		// 语音识别模块
		ud = Understander.getInstance(this);
		// 检测是否点击了语音添加快捷方式
		checkVoiceIntent();
		// 初始化界面相关View
		initViews();
		// 初始化其他模块
		set = SettingMgr.getInstance(this);
		am = AlarmController.getAlarmMgr(this);
	}

	@Override
	public void onResume() {
		themeImgContainer.setVisibility(View.GONE);
		super.onResume();
		if (set.isFirstRun(SettingMgr.FirstRun.GUIDE)) {
			set.setFirstRun(SettingMgr.FirstRun.GUIDE, false);
			// 启动引导界面
			guide();
			handler.postDelayed(new Runnable() {
				@Override
				public void run() {
					FirstRunTools.checkAddSampleAlarm(MainActivity.this);
					refreshViews();
					themeImgContainer.setVisibility(View.GONE);
				}
			}, 500);
			return;
		}
		// 添加快捷方式
		FirstRunTools.checkAddShortcut(this);
		// 刷新界面
		refreshViews();
		// 启动动画
		startThemeImgAnim();
		// 注册监听器
		registerShakeSensor();
	}

	@Override
	public void onPause() {
		unregisterShakeSensor();
		menu.dismiss();
		undo.dismiss();
		super.onPause();
	}

	@Override
	public void onDestroy() {
		if (ud != null) {
			ud.destroy();
			ud = null;
		}
		super.onDestroy();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_MENU:
			menu.switchPop();
			return true;
		case KeyEvent.KEYCODE_BACK:
			if (menu.dismiss())
				return true;
			break;
		case KeyEvent.KEYCODE_VOLUME_UP:
			if (Debug.DBG) {
				test();
				return true;
			}
		}
		// 继续执行父类的其他点击事件
		return super.onKeyDown(keyCode, event);
	}

	final void test() {
		String[] item = new String[] {
				"test alarm start 1",
				"test alarm start 2",
				"test alarm pause 1",
				"test alarm pause 2",
				"test exception",
				"test json" };
		DialogInterface.OnClickListener lis = new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Class<?> cls = null;
				int pos = 0;
				switch (which) {
				case 0:
					pos = 0;
					cls = AlarmStartActivity.class;
					break;
				case 1:
					pos = 1;
					cls = AlarmStartActivity.class;
					break;
				case 2:
					pos = 0;
					cls = AlarmWaitActivity.class;
					break;
				case 3:
					pos = 1;
					cls = AlarmWaitActivity.class;
					break;
				case 4:
					int d = 5 / 0;
					if (d == 0)
						;
					break;
				case 5:
					final String json = "{\"semantic\":{\"slots\":{\"datetime\":{"
							+ "\"date\":\"2014-08-21\",\"type\":\"DT_BASIC\","
							+ "\"time\":\"17:45:01\",\"timeOrig\":\"半小时后\"},"
							+ "\"name\":\"clock\",\"content\":\"起床\"}},\"rc\":0,"
							+ "\"operation\":\"CREATE\",\"service\":\"schedule\","
							+ "\"text\":\"半小时后叫我起床\"}";
					ud.testJson(json);
					break;
				}
				final AlarmItem a = am.getAlarmByPos(pos);
				if (a != null && cls != null) {
					Intent intent = new Intent(MainActivity.this, cls);
					intent.putExtra(AlarmMgr.EXTRA_ID, a.getId());
					intent.putExtra(AlarmMgr.EXTRA_REMAIN_TIME,
							set.getMaxDelayTimes());
					MainActivity.this.startActivity(intent);
				}
			}
		};
		new AlertDialog.Builder(this).setItems(item, lis).show();
	}

	private final void registerShakeSensor() {
		SensorManager mgr = (SensorManager) getSystemService(SENSOR_SERVICE);
		Sensor sensor = mgr.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		mgr.registerListener(sensorListener, sensor,
				SensorManager.SENSOR_DELAY_NORMAL);
	}

	private final void unregisterShakeSensor() {
		SensorManager mgr = (SensorManager) getSystemService(SENSOR_SERVICE);
		mgr.unregisterListener(sensorListener);
	}

	private final void initViews() {
		container = (HexListView) findViewById(R.id.hexgroup_main_container);

		vMenu = new HexView(this);
		vMenu.setIcon(R.drawable.ic_menu_borderless);
		vMenu.setIconEnable(true);
		vMenu.setText(getString(R.string.menu));
		vMenu.setOnClickListener(listener);
		vMenu.setOnLongClickListener(listenerLong);

		vNew = new HexView(this);
		vNew.setIconEnable(true);
		vNew.setText(getString(R.string.add));
		vNew.setOnClickListener(listener);
		vNew.setOnLongClickListener(listenerLong);

		themeImg = (ScaleImageView) findViewById(R.id.img_main_theme_bg);
		themeImg.initLayoutParamas(new FrameLayout.LayoutParams(0, 0));
		themeImgContainer = findViewById(R.id.layout_main_img_container);
		themeImgAnim = AnimationUtils.loadAnimation(this,
				R.anim.alpha_enter_3000);

		theme = Theme.getInstance(this);
	}

	private final void refreshViews() {

		if (Debug.DBG)
			Debug.log(this, "refresh views");

		container.removeAllViews();
		container.addView(vMenu);

		List<AlarmItem> alarms = am.getAllAlarms();
		if (alarms != null) {
			for (AlarmItem alarm : alarms) {
				final HexAlarmView v = new HexAlarmView(this, alarm);
				v.setOnClickListener(listener);
				v.setOnLongClickListener(listenerLong);
				container.addView(v);
			}
		}

		vNew.setIcon(set.getMainVoice() ? R.drawable.ic_voice_borderless
				: R.drawable.ic_add_borderless);
		container.addView(vNew);

		theme.refresh();
		int cnt = container.getChildCount();
		for (int i = 0; i < cnt; ++i) {
			container.getChildAt(i).setBackgroundColor(theme.getColor(i));
		}

		themeImg.setImageResourceAndResize(theme.getImage());
		themeImgContainer.setVisibility(View.VISIBLE);
	}

	private final void startThemeImgAnim() {
		themeImgContainer.startAnimation(themeImgAnim);
	}

	private final boolean handleUnderstanderMsg(Message msg) {
		switch (msg.what) {
		case Understander.MSG_GET_RESULT:
			dismissVoiceDialog();
			if (msg.obj != null && msg.obj instanceof SemanticResult) {
				final SemanticResult r = (SemanticResult) msg.obj;
				final AlarmItem alarm = SemanticAlarm.createAlarm(r);
				if (alarm != null) {
					final int resId = am.addAlarm(alarm) ? R.string.edit_alarm_added
							: R.string.edit_alarm_failed_to_add;
					ToastView.show(this, resId);
					this.refreshViews();
					return true;
				}
			}
			ToastView.show(this, R.string.voice_no_result);
			break;
		case Understander.MSG_NET_ERROR:
			dismissVoiceDialog();
			ToastView.show(this, R.string.voice_net_error);
			break;
		case Understander.MSG_OTHER_ERROR:
		case Understander.MSG_NO_RESULT:
			dismissVoiceDialog();
			ToastView.show(this, R.string.voice_no_result);
			break;
		}
		return true;
	}

	private final boolean checkVoiceIntent() {
		if (this.getIntent().getBooleanExtra(EXTRA_VOICE, false)) {
			this.getIntent().removeExtra(EXTRA_VOICE);
			if (Understander.hasLogined() && ud != null) {
				voiceAddAlarm();
			} else {
				handler.postDelayed(new Runnable() {
					@Override
					public void run() {
						voiceAddAlarm();
					}
				}, 500); // 延迟半秒再次尝试
			}
			return true;
		}
		return false;
	}

	private final void showVoiceDialog() {
		voiceDialog = DialogBuilder
				.from(this, R.layout.dialog_main_voice, R.style.dialog_style)
				.setAnimationStyle(R.style.quick_scale_anim)
				.setBackgroungColor(Theme.getRandomBgColor(this))
				.setViewAnimation(R.id.img_voice_anim,
						R.anim.rotate_repeat_2000)
				.setOnClickDismissView(R.id.bn_voice_cancel,
						new OnClickListener() {
							@Override
							public void onClick(View v) {
								ud.cancel();
							}
						})
				.setOnClickDismissView(R.id.bn_voice_finish,
						new OnClickListener() {
							@Override
							public void onClick(View v) {
								ud.stop();
							}
						}) //
				.setOnCancelListener(new OnCancelListener() {
					@Override
					public void onCancel(DialogInterface dialog) {
						ud.cancel();
					}
				}).show();
	}

	private final void dismissVoiceDialog() {
		if (voiceDialog != null && voiceDialog.isShowing()) {
			voiceDialog.dismiss();
		}
		voiceDialog = null;
	}

	/**
	 * 检测上次运行错误
	 * 
	 * @return 检测到错误返回true
	 */
	private final boolean checkErrorLog() {
		final String ex = AppErrorController.getLastErrorLogAndDel(this);
		if (ex != null) {
			SimpleBuilder.from(this).setTitle(R.string.exception_dialog_title)
					.setMessage(R.string.exception_dialog_msg)
					.setPositiveButton(new OnClickListener() {
						@Override
						public void onClick(View v) {
							sendBugReportEmail(ex);
						}
					}).setNegativeButton(null).show();
			return true;
		}
		return false;
	}

	private final void sendBugReportEmail(String ex) {
		String prefix = getString(R.string.exception_email_content_prefix);
		String subject = getString(R.string.exception_email_subject);
		String to = getString(R.string.app_email_address);
		String title = getString(R.string.exception_send_email_title);
		AppErrorController.sendBugReportEmail(this, R.string.app_name, prefix,
				ex, title, subject, to);
	}

	private final void addAlarm() {
		if (set.getMainVoice()) { // 语音添加
			this.voiceAddAlarm();
		} else { // 常规添加
			this.generalAddAlarm();
		}
	}

	private final void voiceAddAlarm() {
		if (Understander.hasLogined() && ud != null) {
			dismissVoiceDialog();
			ud.start(handler);
			showVoiceDialog();
		} else {
			ToastView.show(this, R.string.error_occured);
		}
	}

	private final void startActivity(Class<?> cls) {
		this.startActivity(new Intent(this, cls));
	}

	private final void generalAddAlarm() {
		this.startActivity(AlarmEditActivity.class);
	}

	private final void editAlarm(int id) {
		Intent intent = new Intent(this, AlarmEditActivity.class);
		intent.putExtra(AlarmMgr.EXTRA_ID, id);
		this.startActivity(intent);
	}

	private final void delAlarm(final int id) {
		AlarmItem data = am.getAlarmById(id);
		boolean result = am.delAlarmById(id);
		int text = result ? R.string.edit_alarm_deleted
				: R.string.edit_alarm_failed_to_del;
		refreshViews();
		undo.delete(data, text);
	}

	private final void setting() {
		this.startActivity(SettingActivity.class);
	}

	private final void guide() {
		this.startActivity(GuideActivity.class);
	}

	private final void changeTheme() {
		theme.setRandomTheme();
		String s = getString(R.string.main_change_theme)
				+ theme.getCurrentName();
		ToastView.show(this, s);
		refreshViews();
		startThemeImgAnim();
	}

	private final void about() {
		this.startActivity(AboutActivity.class);
	}

	private final void onLongClickNew() {

		final CharSequence[] items = {
				MainActivity.this.getString(R.string.set_main_normal_add),
				MainActivity.this.getString(R.string.set_main_voice_add) };

		Builder b = new AlertDialog.Builder(MainActivity.this);

		b.setItems(items, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface arg0, int pos) {
				switch (pos) {
				case 0:
					set.setMainVoice(false);
					refreshViews();
					break;
				case 1:
					set.setMainVoice(true);
					refreshViews();
					break;
				}
			}
		});
		b.show();
	}

	private final void onLongClickSet() {

		final CharSequence[] items = {
				this.getString(R.string.set_tips),
				this.getString(R.string.set_guide),
				this.getString(R.string.set_about) };

		Builder b = new AlertDialog.Builder(this);

		b.setItems(items, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface arg0, int pos) {
				switch (pos) {
				case 0:
					changeTheme();
					break;
				case 1:
					guide();
					break;
				case 2:
					about();
					break;
				}
			}
		});
		b.show();
	}

	private final void onLongClickAlarm(final int id) {
		editAlarm(id);
	}

	private UndoBar<AlarmItem> undo = new UndoBar<AlarmItem>(this) {

		@Override
		public final int getLayoutId() {
			return R.layout.dialog_main_undo;
		}

		@Override
		public final int getAnimationStyleId() {
			return R.style.undo_alpha_anim;
		}

		@Override
		public int getUndoButtonId() {
			return R.id.bn_main_undo;
		}

		@Override
		public int getUndoTextId() {
			return R.id.tx_main_undo;
		}

		@Override
		public void showPop(PopupWindow pop) {
			pop.showAtLocation(findViewById(R.id.layout_main),
					Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM, 0, 90);
		}

		@Override
		public String onUndoEvent(AlarmItem data) {
			final boolean result = am.addAlarm(data);
			final int res = result ? R.string.edit_alarm_undo
					: R.string.edit_alarm_failed_to_undo;
			refreshViews();
			return getString(res);
		}

		@Override
		public String onUndoNoDataEvent() {
			return getString(R.string.edit_no_undo_data);
		}
	};

	private PopMenu menu = new PopMenu(this) {

		@Override
		public void showPop(PopupWindow pop) {
			pop.showAsDropDown(vMenu);
			// pop.showAtLocation(findViewById(R.id.layout_main),
			// Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM, 0, 30);
		}

		@Override
		public final int[] getOnClickViewIds() {
			return new int[] {
					R.id.bn_main_menu_add,
					R.id.bn_main_menu_voice_add,
					R.id.bn_main_menu_setting,
					R.id.bn_main_menu_theme };
		}

		@Override
		public final int getLayoutId() {
			return R.layout.dialog_main_menu;
		}

		@Override
		public final int getAnimationStyleId() {
			return R.style.quick_scale_anim;
		}

		@Override
		public boolean onViewClick(View v) {
			switch (v.getId()) {
			case R.id.bn_main_menu_add:
				generalAddAlarm();
				break;
			case R.id.bn_main_menu_voice_add:
				voiceAddAlarm();
				break;
			case R.id.bn_main_menu_setting:
				setting();
				break;
			case R.id.bn_main_menu_theme:
				changeTheme();
				break;
			}
			return true;
		}
	};

	/**
	 * 主要用于处理语音识别事件的Handler
	 */
	private final Handler handler = new Handler(Looper.getMainLooper(),
			new Callback() {
				@Override
				public boolean handleMessage(Message msg) {
					return MainActivity.this.handleUnderstanderMsg(msg);
				}
			});

	/**
	 * 加速度传感器监听器
	 */
	private SensorEventListener sensorListener = new SensorEventListener() {

		long lastTime = 0;

		@Override
		public void onSensorChanged(SensorEvent event) {
			if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
				float[] v = event.values;
				if ((v[0] * v[0] + v[1] * v[1] + v[2] * v[2]) > 350) {
					long ms = System.currentTimeMillis();
					if (ms - lastTime > 500) {
						changeTheme(); // 至少间隔500ms才能切换一次
						lastTime = ms;
					}
				}
			}
		}

		@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy) {
		}
	};

	/**
	 * 闹铃项点击监听器
	 */
	private final OnClickListener listener = new OnClickListener() {

		private DoubleClicker dc = new DoubleClicker() {

			@Override
			public void onSingleClick(View v) {
				if (v instanceof HexAlarmView) {
					HexAlarmView hex = (HexAlarmView) v;
					hex.flipAlarmOnOff();
					am.setAlarmOnOffById(hex.getAlarmId(), hex.isAlarmOn());
				}
			}

			@Override
			public void onDoubleClick(View v) {
				if (v instanceof HexAlarmView) {
					HexAlarmView hex = (HexAlarmView) v;
					delAlarm(hex.getAlarmId());
				}
			}
		};

		@Override
		public void onClick(View v) {
			if (v == vNew) {
				addAlarm();
			} else if (v == vMenu) {
				menu.show();
			} else {
				dc.clickEvent(v);
			}
		}
	};

	/**
	 * 闹铃项长按监听器
	 */
	private final OnLongClickListener listenerLong = new OnLongClickListener() {
		@Override
		public boolean onLongClick(final View v) {

			if (v == vNew) {
				onLongClickNew();
			} else if (v == vMenu) {
				onLongClickSet();
			} else {
				if (!(v instanceof HexAlarmView))
					return false;
				final int id = ((HexAlarmView) v).getAlarmId();
				onLongClickAlarm(id);
			}
			return true;
		}
	};
}
