package com.jzj.alarm.ui;

import java.io.File;

import com.jzj.alarm.EffectTextView;
import com.jzj.alarm.EffectTextView.Effect;
import com.jzj.alarm.R;
import com.jzj.alarm.ToastView;
import com.jzj.alarm.core.SettingMgr;
import com.jzj.alarm.core.SettingMgr.ClockTone;
import com.jzj.alarm.core.SettingMgr.SettingItem;
import com.jzj.alarm.ui.tools.AlarmController;
import com.jzj.alarm.ui.tools.Theme;
import com.jzj.alarm.ui.tools.MediaTools;
import com.jzj.alarm.ui.tools.SimpleBuilder;
import com.jzj.util.Debug;
import com.jzj.util.SystemUtils;
import com.jzj.util.ui.DisplayUtils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.provider.MediaStore.Audio.Media;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

/**
 * 设置界面
 * 
 * @author jzj
 */
public class SettingActivity extends Activity {

	/**
	 * 定义所有可用设置项及其排列顺序
	 */
	private static final SettingItem[] item0 = {
			// UI
			SettingItem.MAIN_VOICE,
			SettingItem.NOTI_MODE,
			SettingItem.COLOR_THEME,
			// set
			SettingItem.SENSITIVITY,
			// info
			SettingItem.TIPS,
			// app
			SettingItem.GUIDE,
			SettingItem.ABOUT,
			// 清除
			SettingItem.FIRST_RUN,
			SettingItem.CLEAR, };

	private static final SettingItem[] item1 = {
			// 提示
			SettingItem.VIBRATE,
			SettingItem.CRESCENDO,
			SettingItem.ALARM_TONE,
			SettingItem.CLOCK_TONE,
			SettingItem.ALARM_LOCKED_TONE,
			// 参数调节
			SettingItem.MAX_DELAY,
			SettingItem.DELAY_TIME,
			SettingItem.PAUSE_TIME,
			SettingItem.UNLOCK_STEPS, };

	private SettingMgr set;
	private Theme colors;

	private MediaPlayer player;
	private LayoutInflater inflater;

	private int halfScreenWidth;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_settings);

		set = SettingMgr.getInstance(this);
		colors = Theme.getInstance(this);

		this.initViews();
	}

	@Override
	public void onResume() {
		super.onResume();
		adapter0.notifyDataSetChanged();
		adapter1.notifyDataSetChanged();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_MENU:
		case KeyEvent.KEYCODE_BACK:
			if (this.stopMusic())
				return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	private final void initViews() {

		inflater = this.getLayoutInflater();
		halfScreenWidth = DisplayUtils.getInstance(this).getScreenWidth() / 2;

		final RadioGroup rg = (RadioGroup) findViewById(R.id.radiogroup_setting);
		final ViewPager vp = (ViewPager) findViewById(R.id.viewpager_setting);
		final RadioButton[] r = new RadioButton[2];
		r[0] = (RadioButton) findViewById(R.id.radio_setting_0);
		r[1] = (RadioButton) findViewById(R.id.radio_setting_1);
		final View[] v = generatePages();

		vp.setAdapter(new PagerAdapter() {

			@Override
			public int getCount() {
				return 2;
			}

			@Override
			public Object instantiateItem(View vp, int page) {
				((ViewPager) vp).addView(v[page]);
				return v[page];
			}

			@Override
			public void destroyItem(View vp, int page, Object obj) {
				((ViewPager) vp).removeView(v[page]);
			}

			@Override
			public boolean isViewFromObject(View view, Object obj) {
				return view == obj;
			}

			@Override
			public int getItemPosition(Object object) {
				return super.getItemPosition(object);
			}

			// @Override
			// public void restoreState(Parcelable arg0, ClassLoader arg1) {
			// }
			//
			// @Override
			// public Parcelable saveState() {
			// return null;
			// }
			//
			// @Override
			// public void startUpdate(View arg0) {
			// }
			//
			// @Override
			// public void finishUpdate(View arg0) {
			// }
		});

		vp.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int page) {
				if (Debug.DBG)
					Debug.log(OnPageChangeListener.class, "page = " + page);
				setChecked(r, page);
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				stopMusic();
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
			}
		});

		rg.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				if (Debug.DBG)
					Debug.log(OnCheckedChangeListener.class, "checkedId = "
							+ checkedId);
				switch (checkedId) {
				case R.id.radio_setting_0:
					vp.setCurrentItem(0, true);
					break;
				case R.id.radio_setting_1:
					vp.setCurrentItem(1, true);
					break;
				}
			}
		});

		vp.setCurrentItem(0);
		setChecked(r, 0);
	}

	private void setChecked(RadioButton[] r, int page) {
		if (page == 0 || page == 1) {
			r[page].setChecked(true);
			r[page].setTextColor(0xFFA0A0A0);
			r[1 - page].setChecked(false);
			r[1 - page].setTextColor(0xFFFFFFFF);
		}
	}

	private View[] generatePages() {
		final View[] v = new View[2];
		v[0] = inflater.inflate(R.layout.fragment_setting_list, null);
		v[1] = inflater.inflate(R.layout.fragment_setting_list, null);

		ListView listView0 = (ListView) v[0]
				.findViewById(R.id.listview_settings);
		listView0.setOnItemClickListener(listener0);
		listView0.setAdapter(adapter0);

		// listView.setOnItemLongClickListener(longListener);
		ListView listView1 = (ListView) v[1]
				.findViewById(R.id.listview_settings);
		listView1.setOnItemClickListener(listener1);
		listView1.setAdapter(adapter1);
		listView1.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				switch (scrollState) {
				case SCROLL_STATE_TOUCH_SCROLL:
					stopMusic();
					break;
				}
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
			}
		});
		return v;
	}

	private View getItemView(int page, int pos) {
		final int p = (page == 0) ? pos : pos + item0.length;
		final SettingItem item = page == 0 ? item0[pos] : item1[pos];
		final View v = inflater.inflate(R.layout.adapter_setting_item, null);
		v.setBackgroundColor(colors.getColor(p));

		final TextView tk = (TextView) v
				.findViewById(R.id.tx_setting_adapter_key);
		final EffectTextView tv = (EffectTextView) v
				.findViewById(R.id.tx_setting_adapter_value);
		final EffectTextView th = (EffectTextView) v
				.findViewById(R.id.tx_setting_adapter_help);

		tv.setMaxWidth(halfScreenWidth);

		int thResId = -1;
		switch (item) {
		case TIPS:
			tk.setText(R.string.set_tips);
			tv.setText("");
			break;
		case CLEAR:
			tk.setText(R.string.set_reset_settings);
			tv.setText("");
			break;
		case ABOUT:
			tk.setText(R.string.set_about);
			tv.setText("");
			break;
		case GUIDE:
			tk.setText(R.string.set_guide);
			tv.setText("");
			break;
		case MAIN_VOICE:
			tk.setText(R.string.set_main_voice);
			if (set.getMainVoice())
				tv.setText(R.string.set_main_voice_add);
			else
				tv.setText(R.string.set_main_normal_add);
			break;
		case VIBRATE:
			tk.setText(R.string.set_vibrate);
			if (set.getViberate())
				tv.setText(R.string.set_on);
			else
				tv.setText(R.string.set_off);
			break;
		case CRESCENDO:
			tk.setText(R.string.set_crescendo);
			if (set.getCreascendo())
				tv.setText(R.string.set_on);
			else
				tv.setText(R.string.set_off);
			break;
		case FIRST_RUN:
			tk.setText(R.string.set_first_run);
			tv.setText("");
			break;
		case NOTI_MODE:
			tk.setText(R.string.set_noti_mode);
			switch (set.getNotificationMode()) {
			default:
			case ALWAYS:
				tv.setText(R.string.set_noti_mode_always);
				break;
			case NECESSARY:
				tv.setText(R.string.set_noti_mode_necessary);
				break;
			case NEVER:
				tv.setText(R.string.set_noti_mode_never);
				break;
			}
			break;
		case MAX_DELAY:
			tk.setText(R.string.set_max_delay);
			tv.setText(String.valueOf(set.getMaxDelayTimes()));
			thResId = R.string.set_max_delay_tips;
			break;
		case DELAY_TIME:
			tk.setText(R.string.set_delay_time);
			tv.setText(set.getDelayTime() + this.getString(R.string.minute));
			thResId = R.string.set_delay_time_tips;
			break;
		case PAUSE_TIME:
			tk.setText(R.string.set_pause_time);
			tv.setText(set.getAlarmPauseTime()
					+ this.getString(R.string.minute));
			thResId = R.string.set_pause_time_tips;
			break;
		case SENSITIVITY:
			tk.setText(R.string.set_sensitivity);
			tv.setText(String.valueOf(set.getSensitivity()));
			// thResId = R.string.set_sensitivity_tips;
			break;
		case UNLOCK_STEPS:
			tk.setText(R.string.set_unlock_steps);
			tv.setText(String.valueOf(set.getUnlockSteps()));
			thResId = R.string.set_unlock_steps_tips;
			break;
		case COLOR_THEME:
			tk.setText(R.string.set_color_theme);
			tv.setText(colors.getCurrentName());
			break;
		case CLOCK_TONE:
			tk.setText(R.string.set_clock_tone);
			switch (set.getClockTone()) {
			case CLOCK:
				tv.setText(R.string.set_clock_tone_clock);
				break;
			case TICK:
				tv.setText(R.string.set_clock_tone_tick);
				break;
			case WATER:
				tv.setText(R.string.set_clock_tone_water);
				break;
			case NONE:
				tv.setText(R.string.set_clock_tone_none);
				break;
			}
			thResId = R.string.set_clock_tone_tips;
			break;
		case ALARM_TONE:
			tk.setText(R.string.set_alarm_tone);
			String s = set.getAlarmTone();
			if (s == null || s.equals(SettingMgr.DEFAULT_TONE)) {
				tv.setText(R.string.set_tone_default);
			} else {
				s = new File(s).getName();
				tv.setEffect(Effect.MARQUEE);
				// tv.setEffect(Effect.MULTI_LINE_ELLIPSIS);
				tv.setTextSize(TypedValue.COMPLEX_UNIT_PX,
						tv.getTextSize() * 0.7f);
				tv.setText(s);
			}
			thResId = R.string.set_alarm_tone_tips;
			break;
		case ALARM_LOCKED_TONE:
			tk.setText(R.string.set_alarm_locked_tone);
			String s1 = set.getAlarmLockedTone();
			if (s1 == null || s1.equals(SettingMgr.DEFAULT_TONE)) {
				tv.setText(R.string.set_tone_default);
			} else {
				s1 = new File(s1).getName();
				tv.setEffect(Effect.MARQUEE);
				// tv.setEffect(Effect.MULTI_LINE_ELLIPSIS);
				tv.setTextSize(TypedValue.COMPLEX_UNIT_PX,
						tv.getTextSize() * 0.7f);
				tv.setText(s1);
			}
			thResId = R.string.set_alarm_locked_tone_tips;
			break;
		default:
			if (Debug.DBG)
				Debug.error(this, "get item view error");
			tk.setText("");
			tv.setText("");
			break;
		}
		if (thResId != -1) {
			th.setEffect(Effect.MULTI_LINE_ELLIPSIS);
			th.setText(thResId);
		} else {
			th.setVisibility(View.GONE);
		}
		return v;
	}

	private final void onItemClick(int page, int position) {
		SettingItem i = page == 0 ? item0[position] : item1[position];
		switch (i) {
		case TIPS:
			this.startActivity(new Intent(this, TipsActivity.class));
			break;
		case ABOUT:
			this.startActivity(new Intent(this, AboutActivity.class));
			break;
		case GUIDE:
			this.startActivity(new Intent(this, GuideActivity.class));
			break;
		case CLEAR:
			setClearSetting();
			break;
		case MAIN_VOICE:
			set.setMainVoice(!set.getMainVoice());
			break;
		case VIBRATE:
			boolean b = !set.getViberate();
			set.setViberate(b);
			if (b)
				SystemUtils.vibrateOnce(this, 500);
			break;
		case CRESCENDO:
			set.setCreascendo(!set.getCreascendo());
			break;
		case FIRST_RUN:
			set.resetFirstRun();
			ToastView.show(this, R.string.set_first_run_cleared);
			break;
		case NOTI_MODE:
			setNotiMode();
			break;
		case MAX_DELAY:
			setMaxDelay();
			break;
		case DELAY_TIME:
			setDelayTime();
			break;
		case PAUSE_TIME:
			setPauseTime();
			break;
		case SENSITIVITY:
			setSensitivity();
			break;
		case UNLOCK_STEPS:
			setUnlockSteps();
			break;
		case COLOR_THEME:
			colors.setNextTheme();
			break;
		case CLOCK_TONE:
			setClockTone();
			break;
		case ALARM_TONE:
			setAlarmTone();
			break;
		case ALARM_LOCKED_TONE:
			setAlarmLockedTone();
			break;
		default:
			if (Debug.DBG)
				Debug.error(this, "item click error");
		}
		adapter0.notifyDataSetChanged();
		adapter1.notifyDataSetChanged();
	}

	private void setAlarmLockedTone() {
		setToneType(new OnGetStringListener() {

			@Override
			public void onGetString(String str) {
				set.setAlarmLockedTone(str);
				adapter0.notifyDataSetChanged();
				adapter1.notifyDataSetChanged();
				playMusic(str);
			}
		});
	}

	private void setAlarmTone() {
		setToneType(new OnGetStringListener() {

			@Override
			public void onGetString(String str) {
				set.setAlarmTone(str);
				adapter0.notifyDataSetChanged();
				adapter1.notifyDataSetChanged();
				playMusic(str);
			}
		});
	}

	private void setClockTone() {
		switch (set.getClockTone()) {
		case CLOCK:
			set.setClockTones(ClockTone.TICK);
			break;
		case TICK:
			set.setClockTones(ClockTone.WATER);
			break;
		case WATER:
			set.setClockTones(ClockTone.NONE);
			break;
		case NONE:
			set.setClockTones(ClockTone.CLOCK);
			break;
		}
		final int resId = set.getRawClockTone();
		if (resId != 0)
			this.playMusic(resId);
	}

	private void setUnlockSteps() {
		SimpleBuilder.OnGetIntListener ls = new SimpleBuilder.OnGetIntListener() {

			@Override
			public void onGetInt(int d) {
				if (d > 0) {
					set.setUnlockSteps(d);
					adapter0.notifyDataSetChanged();
					adapter1.notifyDataSetChanged();
				}
			}
		};
		SimpleBuilder.inputInt(this, R.string.set_unlock_steps,
				set.getUnlockSteps(), ls);
	}

	private void setSensitivity() {
		this.startActivity(new Intent(this, PedometerActivity.class));
	}

	private void setPauseTime() {
		SimpleBuilder.OnGetIntListener ls = new SimpleBuilder.OnGetIntListener() {

			@Override
			public void onGetInt(int d) {
				if (d > 0 && d < 120) {
					set.setAlarmPauseTime(d);
					adapter0.notifyDataSetChanged();
					adapter1.notifyDataSetChanged();
				}
			}
		};
		SimpleBuilder.inputInt(this, R.string.set_pause_time,
				set.getAlarmPauseTime(), ls);
	}

	private void setDelayTime() {
		SimpleBuilder.OnGetIntListener ls = new SimpleBuilder.OnGetIntListener() {

			@Override
			public void onGetInt(int d) {
				if (d > 0 && d < 120) {
					set.setDelayTime(d);
					adapter0.notifyDataSetChanged();
					adapter1.notifyDataSetChanged();
				}
			}
		};
		SimpleBuilder.inputInt(this, R.string.set_delay_time,
				set.getDelayTime(), ls);
	}

	private void setMaxDelay() {
		SimpleBuilder.OnGetIntListener ls = new SimpleBuilder.OnGetIntListener() {

			@Override
			public void onGetInt(int d) {
				if (d >= 0) { // 输入0则不限制
					set.setMaxDelayTime(d);
					adapter0.notifyDataSetChanged();
					adapter1.notifyDataSetChanged();
				}
			}
		};
		SimpleBuilder.inputInt(this, R.string.set_max_delay,
				set.getMaxDelayTimes(), ls);
	}

	private void setNotiMode() {
		switch (set.getNotificationMode()) {
		default:
		case ALWAYS:
			set.setNotificationMode(SettingMgr.Noti.NECESSARY);
			break;
		case NECESSARY:
			set.setNotificationMode(SettingMgr.Noti.NEVER);
			break;
		case NEVER:
			set.setNotificationMode(SettingMgr.Noti.ALWAYS);
			break;
		}
		// 刷新Notification显示
		AlarmController.getAlarmMgr(this).calcSetNextAlarm();
	}

	private void setClearSetting() {
		SimpleBuilder.from(this).setTitle(R.string.confirm_clear)
				.setPositiveButton(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						set.clear();
						colors.refresh();
						adapter0.notifyDataSetChanged();
						adapter1.notifyDataSetChanged();
						ToastView.show(SettingActivity.this,
								R.string.set_settings_cleared);
					}
				}).setNegativeButton(null).show();
	}

	private final boolean playMusic(int resId) {
		stopMusic();
		try {
			player = MediaTools.createMediaPlayer(this, resId);
			player.start();
			return true;
		} catch (Exception e) {
			if (Debug.DBG)
				e.printStackTrace();
		}
		ToastView.show(this, R.string.set_play_error);
		return false;
	}

	private final boolean playMusic(String setVal) {
		stopMusic();
		player = MediaTools.createMediaPlayer(this, setVal);
		if (player != null) {
			try {
				player.setLooping(false);
				player.start();
				return true;
			} catch (Exception e) {
				if (Debug.DBG)
					e.printStackTrace();
			}
		}
		ToastView.show(this, R.string.set_play_error);
		return false;
	}

	private final boolean stopMusic() {
		try {
			if (player != null && player.isPlaying()) {
				player.stop();
				return true;
			}
		} catch (Exception e) {
			if (Debug.DBG)
				e.printStackTrace();
		}
		return false;
	}

	private final void setToneType(final OnGetStringListener lis) {

		final CharSequence[] items = {
				this.getString(R.string.set_tone_default),
				this.getString(R.string.set_tone_internal),
				this.getString(R.string.set_tone_external) };

		new AlertDialog.Builder(this).setItems(items, new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int pos) {
				switch (pos) {
				case 0:
				default:
					lis.onGetString(null);
					break;
				case 1:
					selectTone(lis, true);
					break;
				case 2:
					selectTone(lis, false);
					break;
				}
			}
		}).show();
	}

	private final void selectTone(final OnGetStringListener lis,
			final boolean internal) {

		final String[] cols = new String[] {
				Media._ID,
				Media.DISPLAY_NAME,
				Media.DATA };
		final ContentResolver cr = this.getContentResolver();
		final Cursor cur = cr.query(internal ? Media.INTERNAL_CONTENT_URI
				: Media.EXTERNAL_CONTENT_URI, cols, null, null, null);

		new AlertDialog.Builder(this).setCursor(cur, new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int pos) {
				try {
					if (cur.moveToFirst() && cur.move(pos))
						lis.onGetString(cur.getString(2));
				} catch (Exception e) {
					if (Debug.DBG)
						e.printStackTrace();
				}
			}
		}, Media.DISPLAY_NAME).show();
	}

	private interface OnGetStringListener {
		public void onGetString(String str1);
	}

	private final OnItemClickListener listener0 = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View v, int position,
				long id) {
			SettingActivity.this.onItemClick(0, position);
		}
	};

	private final OnItemClickListener listener1 = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			SettingActivity.this.onItemClick(1, position);
		}
	};

	private final BaseAdapter adapter0 = new BaseAdapter() {

		@Override
		public int getCount() {
			return item0.length;
		}

		@Override
		public Object getItem(int position) {
			return position;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			return SettingActivity.this.getItemView(0, position);
		}
	};

	private final BaseAdapter adapter1 = new BaseAdapter() {

		@Override
		public int getCount() {
			return item1.length;
		}

		@Override
		public Object getItem(int position) {
			return position;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			return SettingActivity.this.getItemView(1, position);
		}
	};
}
