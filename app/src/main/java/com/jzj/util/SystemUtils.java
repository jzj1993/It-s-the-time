package com.jzj.util;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.Intent.ShortcutIconResource;
import android.database.Cursor;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.WifiLock;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.PowerManager;
import android.os.Vibrator;
import android.os.PowerManager.WakeLock;
import android.view.View;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.view.inputmethod.InputMethodManager;

/**
 * 常用系统工具集
 * (本类中所有方法为静态,每次调用时,一些对象都会被重新实例化,影响效率,
 * 为提高效率,或有特殊需求,可参考本类中的写法自行实现)
 * 
 * @since 2014-09-05
 * @author jzj
 */
public class SystemUtils {

	private static final int FLAG_HOMEKEY_DISPATCHED = 0x80000000;
	private static final int FLAG_NEEDS_MENU_KEY = 0x08000000;

	private static final String LOG_TAG = "TAG";

	/**
	 * 全屏，在setContentView之前调用，否则出错
	 * 
	 * @param act
	 */
	public static final void setFullScreen(Activity act) {
		act.requestWindowFeature(Window.FEATURE_NO_TITLE); // 取消标题栏
		act.getWindow().addFlags(LayoutParams.FLAG_FULLSCREEN); // 取消状态栏
	}

	/**
	 * 禁用系统锁屏(但超时屏幕仍然会熄灭)(不需权限)
	 * 
	 * @param act
	 */
	public static final void disableKeyguard(Activity act) {
		act.getWindow().addFlags(LayoutParams.FLAG_DISMISS_KEYGUARD);
	}

	/**
	 * 保持屏幕常亮(但熄灭状态不会点亮屏幕)(不需权限)
	 * 
	 * @param act
	 */
	public static final void keepScreenOn(Activity act) {
		act.getWindow().addFlags(LayoutParams.FLAG_KEEP_SCREEN_ON);
	}

	/**
	 * 取消保持屏幕常亮
	 * 
	 * @param act
	 */
	public static final void disableKeepScreenOn(Activity act) {
		act.getWindow().clearFlags(LayoutParams.FLAG_KEEP_SCREEN_ON);
	}

	/**
	 * 锁屏时直接显示界面(不需权限)
	 * 
	 * @param act
	 */
	public static final void showWhenLocked(Activity act) {
		act.getWindow().addFlags(LayoutParams.FLAG_SHOW_WHEN_LOCKED);
	}

	/**
	 * 屏蔽Home键(仅限于联发科平台的系统)(在onKeyDown中处理按键事件)
	 * 
	 * @param act
	 */
	public static final void blockHomeKey(Activity act) {
		act.getWindow().addFlags(FLAG_HOMEKEY_DISPATCHED);
	}

	/**
	 * 安卓4.0显示虚拟菜单按钮
	 * 
	 * @param act
	 */
	public static final void showVirtualMenuKey(Activity act) {
		act.getWindow().addFlags(FLAG_NEEDS_MENU_KEY);
	}

	/**
	 * 获取电源锁：点亮屏幕，并保持常亮
	 * <p>
	 * 需要注册权限:
	 * <p>
	 * &#60;uses-permission android:name="android.permission.WAKE_LOCK" />
	 * <p>
	 * 
	 * @param context
	 *            context
	 * @return 执行成功返回WakeLock实例,执行失败返回null
	 *         (程序退出后,使用WakeLock.release释放)
	 */
	public static final WakeLock acquireLightWakeLock(Context context) {
		PowerManager pm = (PowerManager) context
				.getSystemService(Context.POWER_SERVICE);

		// FULL_WAKE_LOCK 保持屏幕全亮、键盘背光灯点亮和CPU运行。
		// SCREEN_BRIGHT_WAKE_LOCK 保持屏幕全亮和CPU运行。
		// SCREEN_DIM_WAKE_LOCK 保持屏幕开启（但是允许变暗）和CPU运行。
		// PARTIAL_WAKE_LOCK 保持CPU运行。

		// ACQUIRE_CAUSES_WAKEUP 持锁后,立即唤醒
		// (否则只能在唤醒状态下继续保持唤醒;不能和PARTIAL_WAKE_LOCK一起用)
		// ON_AFTER_RELEASE 释放锁时,不立即熄灭
		// (如果屏幕为亮,则按照系统定时再亮一会;如果屏幕为灭,不会唤醒屏幕)

		WakeLock wl = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP
				| PowerManager.SCREEN_DIM_WAKE_LOCK
				| PowerManager.ON_AFTER_RELEASE, LOG_TAG);
		if (wl != null) {
			wl.setReferenceCounted(false);
			wl.acquire();
		}
		return wl;
	}

	/**
	 * 获取电源锁：保持在屏幕熄灭时，CPU仍然运行
	 * <p>
	 * 需要权限
	 * <p>
	 * &#60;uses-permission android:name="android.permission.WAKE_LOCK" />
	 * <p>
	 * 可能需要权限
	 * <p>
	 * &#60;uses-permission android:name="android.permission.DEVICE_POWER" />
	 * <p>
	 * 
	 * @param context
	 *            context
	 * @return 执行成功返回WakeLock实例,执行失败返回null
	 *         (程序退出后,使用WakeLock.release释放)
	 */
	public static final WakeLock acquirePowerWakeLock(Context context) {
		PowerManager pm = (PowerManager) context
				.getSystemService(Context.POWER_SERVICE);

		// FULL_WAKE_LOCK 保持屏幕全亮、键盘背光灯点亮和CPU运行。
		// SCREEN_BRIGHT_WAKE_LOCK 保持屏幕全亮和CPU运行。
		// SCREEN_DIM_WAKE_LOCK 保持屏幕开启（但是允许变暗）和CPU运行。
		// PARTIAL_WAKE_LOCK 保持CPU运行。

		// ACQUIRE_CAUSES_WAKEUP 持锁后,立即唤醒
		// (否则只能在唤醒状态下继续保持唤醒;不能和PARTIAL_WAKE_LOCK一起用)
		// ON_AFTER_RELEASE 释放锁时,不立即熄灭
		// (如果屏幕为亮,则按照系统定时再亮一会;如果屏幕为灭,不会唤醒屏幕)

		WakeLock wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK
				| PowerManager.ON_AFTER_RELEASE, LOG_TAG);
		if (wakeLock != null) {
			wakeLock.setReferenceCounted(false);
			wakeLock.acquire();
		}
		return wakeLock;
	}

	/**
	 * 启动Wifi；同时获取Wifi锁：保持该服务在屏幕熄灭时仍然连接Wifi
	 * <p>
	 * 可能需要的权限
	 * <p>
	 * &#60;uses-permission android:name="android.permission.INTERNET" />
	 * <p>
	 * &#60;uses-permission
	 * android:name="android.permission.ACCESS_NETWORK_STATE" />
	 * <p>
	 * &#60;uses-permission
	 * android:name="android.permission.CHANGE_NETWORK_STATE" />
	 * <p>
	 * &#60;uses-permission android:name="android.permission.ACCESS_WIFI_STATE"
	 * />
	 * <p>
	 * &#60;uses-permission android:name="android.permission.CHANGE_WIFI_STATE"
	 * />
	 * <p>
	 * &#60;uses-permission
	 * android:name="android.permission.ACCESS_CHECKIN_PROPERTIES" />
	 * <p>
	 * &#60;uses-permission android:name="android.permission.MODIFY_PHONE_STATE"
	 * />
	 * 
	 * @param context
	 *            context
	 * @return 执行成功返回WifiLock实例，执行失败返回null
	 *         (程序退出后,使用WifiLock.release释放)
	 */
	public static final WifiLock acquireWifiLock(Context context) {
		WifiManager wm = (WifiManager) context
				.getSystemService(Context.WIFI_SERVICE);
		if (!wm.isWifiEnabled()) {
			wm.setWifiEnabled(true); // 启用Wifi
		}
		WifiLock wifiLock = wm.createWifiLock("wifiLock");
		if (wifiLock != null) {
			wifiLock.setReferenceCounted(false);
			wifiLock.acquire();
		}
		return wifiLock;
	}

	/**
	 * 显示输入法软键盘
	 * 
	 * @param v
	 *            输入法焦点
	 */
	public static final void showIme(Context context, final View v) {
		final InputMethodManager ime = (InputMethodManager) context
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		// 通过主线程调取输入法
		new Handler(Looper.getMainLooper()).post(new Runnable() {
			public void run() {
				v.requestFocus();
				ime.showSoftInput(v, 0);
				// ime.showSoftInput(v, InputMethodManager.SHOW_FORCED);
			}
		});
	}

	/**
	 * 隐藏输入法软键盘
	 * 
	 * @param v
	 *            输入法焦点
	 */
	public static final void hideIme(Context context, final View v) {
		final InputMethodManager ime = (InputMethodManager) context
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		// 通过主线程隐藏键盘
		new Handler(Looper.getMainLooper()).post(new Runnable() {
			public void run() {
				ime.hideSoftInputFromWindow(v.getWindowToken(), 0);
			}
		});
	}

	/**
	 * 设置屏幕亮度
	 * 
	 * @param brightness
	 *            亮度 范围 (0,1]
	 */
	public static final void setScreenBrightness(Activity act, float brightness) {
		if (brightness <= 0f) {
			brightness = 0f;
		} else if (brightness > 1f) {
			brightness = 1f;
		}
		Window window = act.getWindow();
		LayoutParams lp = window.getAttributes();
		lp.screenBrightness = brightness;
		window.setAttributes(lp);
	}

	/**
	 * 震动(震动1s 暂停0.5s 循环),注:在屏幕熄灭时(包括按电源键时)震动会停止
	 * <p>
	 * 需要权限
	 * <p>
	 * &#60;uses-permission android:name="android.permission.VIBRATE" />
	 * <p>
	 * 
	 * @return Vibrator实例,调用Vibrator.cancel停止震动
	 */
	public static final Vibrator startVibrate(Context context) {
		Vibrator vi = (Vibrator) context.getApplicationContext()
				.getSystemService(Service.VIBRATOR_SERVICE);
		vi.vibrate(new long[] { 1000, 500 }, 0);
		return vi;
	}

	/**
	 * 震动一次
	 * <p>
	 * 需要权限
	 * <p>
	 * &#60;uses-permission android:name="android.permission.VIBRATE" />
	 * <p>
	 * 
	 * @param context
	 * @param milliseconds
	 *            震动时长
	 * @return Vibrator实例,调用Vibrator.cancel停止震动
	 */
	public static final Vibrator vibrateOnce(Context context, long milliseconds) {
		Vibrator vi = (Vibrator) context.getApplicationContext()
				.getSystemService(Service.VIBRATOR_SERVICE);
		vi.vibrate(milliseconds);
		return vi;
	}

	/**
	 * 指定时间启动Intent (PendingIntent)
	 * 
	 * @param millisecond
	 *            启动时间,用毫秒方式表示
	 *            <p>
	 * @param intent
	 *            要启动的intent,需指定要启动的Receiver,可在其上绑定一些数据,如:
	 *            <p>
	 *            intent = new Intent(MyActivity.this, MyReceiver.class);
	 *            <p>
	 *            intent.putExtra("id", id);
	 */
	public static final void sendPendingIntent(Context context,
			long millisecond, Intent intent) {
		AlarmManager am = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);

		PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent,
				PendingIntent.FLAG_CANCEL_CURRENT);

		am.set(AlarmManager.RTC_WAKEUP, millisecond, pi);
	}

	/**
	 * 取消指定时间启动的Intent (PendingIntent)
	 * 
	 * @param intent
	 *            设置启动PendingIntent时使用的Intent
	 *            (取消闹铃的进程应和设置闹铃的进程一致;使用的Context、Intent应与调用时的相同，
	 *            Intent中的类应相同,其中附加的Extra可以不同)
	 * @return 操作成功返回true
	 */
	public static final boolean cancelPendingIntent(Context context,
			Intent intent) {
		AlarmManager am = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);

		PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent,
				PendingIntent.FLAG_NO_CREATE);

		if (pi != null) {
			am.cancel(pi);
			return true;
		}
		return false;
	}

	/**
	 * 设置音量(Music音频流)
	 * 
	 * @param vol
	 *            音量，范围0~1
	 */
	public static final void setAudioVolume(Context context, float vol) {
		AudioManager am = (AudioManager) context
				.getSystemService(Context.AUDIO_SERVICE);
		if (vol < 0f) {
			vol = 0f;
		} else if (vol > 1f) {
			vol = 1f;
		}
		int volume = (int) (am.getStreamMaxVolume(AudioManager.STREAM_MUSIC) * vol);
		// Flags: AudioManager.FLAG_SHOW_UI); // UI显示音量设置
		am.setStreamVolume(AudioManager.STREAM_MUSIC, volume, 0);
	}

	/**
	 * 获取音量(Music音频流)
	 * 
	 * @return
	 */
	public static final float getAudioVolume(Context context) {
		AudioManager am = (AudioManager) context
				.getSystemService(Context.AUDIO_SERVICE);
		return (float) am.getStreamVolume(AudioManager.STREAM_MUSIC)
				/ am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
	}

	/**
	 * 检测是否有传感器
	 * 
	 * @param sensorType
	 *            传感器类型，如 Sensor.TYPE_ACCELEROMETER
	 * @return 有返回true，没有返回false
	 */
	public static final boolean checkSensor(Context context, int sensorType) {
		SensorManager sm = (SensorManager) context
				.getSystemService(Context.SENSOR_SERVICE);
		return (sm.getDefaultSensor(sensorType) != null);
	}

	/**
	 * 启动传感器
	 * 
	 * @param sensorType
	 *            传感器类型，如 Sensor.TYPE_ACCELEROMETER
	 * @param sensorEventListener
	 *            监听器,如果为null,返回false
	 * @return 监听器为null，或没有加速度传感器，返回false
	 */
	public static final boolean startSensor(Context context, int sensorType,
			SensorEventListener sensorEventListener) {
		if (!checkSensor(context, sensorType))
			return false; // 没有相应传感器
		if (sensorEventListener == null)
			return false;
		SensorManager sm = (SensorManager) context
				.getSystemService(Context.SENSOR_SERVICE);
		sm.registerListener(sensorEventListener,
				sm.getDefaultSensor(sensorType),
				SensorManager.SENSOR_DELAY_NORMAL);
		return true;
	}

	/**
	 * 停止传感器
	 * 
	 * @param sensorEventListener
	 *            监听器
	 */
	public static final void stopSensor(Context context,
			SensorEventListener sensorEventListener) {
		if (sensorEventListener != null) {
			SensorManager sm = (SensorManager) context
					.getSystemService(Context.SENSOR_SERVICE);
			sm.unregisterListener(sensorEventListener);
		}
	}

	/**
	 * 显示Notification
	 * <p>
	 * 如果设置默认震动,需要注册权限:
	 * <p>
	 * &#60;uses-permission android:name="android.permission.VIBRATE" />
	 * <p>
	 * 
	 * @param ntId
	 *            Notification的id,取消Notification时使用相同id即可取消
	 * @param iconResId
	 *            产生提示时,在状态栏显示的图标资源文件id
	 * @param tickerText
	 *            产生提示时,在状态栏显示的文本
	 * @param contentTitle
	 *            展开状态栏时,显示的标题
	 * @param contentText
	 *            展开状态栏时,显示的文本
	 * @param intent
	 *            展开状态栏并点击时,打开的Intent
	 */
	public static final void notification(Context context, int ntId,
			int iconResId, CharSequence tickerText, CharSequence contentTitle,
			CharSequence contentText, Intent intent) {

		NotificationManager nm = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);

		PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
				intent, 0);

		Notification n = new Notification.Builder(context)
				.setWhen(System.currentTimeMillis()) // 产生提示的时间
				.setSmallIcon(iconResId) // 产生提示时,在状态栏显示的图标
				.setTicker(tickerText) // 产生提示时,在状态栏显示的文本
				.setDefaults(Notification.DEFAULT_ALL) // 开启声音，震动，闪屏等默认值
				.setOngoing(true) // 放到“正在运行”分组
				.setAutoCancel(false) // 不清除通知
				.setContentTitle(contentTitle) // 展开状态栏时,显示的标题
				.setContentText(contentText) // 展开状态栏时,显示的文本
				.setContentIntent(pendingIntent) // 展开状态栏并点击时,打开的Intent
				.getNotification();

		nm.notify(ntId, n);
	}

	/**
	 * 取消Notification
	 * 
	 * @param ntId
	 *            Notification的id,使用与显示时相同的id即可
	 */
	public static final void cancelNotification(Context context, int ntId) {
		NotificationManager nm = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		nm.cancel(ntId);
	}

	/**
	 * 创建快捷方式
	 * <p>
	 * 需要权限
	 * <p>
	 * &#60;uses-permission
	 * android:name="com.android.launcher.permission.INSTALL_SHORTCUT" />
	 * <p>
	 * 
	 * @param context
	 *            Context
	 * @param intent
	 *            要启动的Intent
	 * @param name
	 *            快捷方式名
	 * @param icon
	 *            快捷方式图标
	 */
	public static final void addShortcut(Context context, Intent intent,
			int name, int icon) {
		addShortcut(context, intent, context.getString(name), icon);
	}

	/**
	 * 创建快捷方式
	 * <p>
	 * 需要权限
	 * <p>
	 * &#60;uses-permission
	 * android:name="com.android.launcher.permission.INSTALL_SHORTCUT" />
	 * <p>
	 * 
	 * @param context
	 *            Context
	 * @param intent
	 *            要启动的Intent
	 * @param name
	 *            快捷方式名
	 * @param icon
	 *            快捷方式图标
	 */
	public static final void addShortcut(Context context, Intent intent,
			String name, int icon) {
		ShortcutIconResource iconRes = Intent.ShortcutIconResource.fromContext(
				context, icon);
		Intent sc = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
		sc.putExtra(Intent.EXTRA_SHORTCUT_NAME, name); // 快捷方式名
		sc.putExtra("duplicate", false); // 不允许重复创建
		sc.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, iconRes); // 快捷方式图标
		sc.putExtra(Intent.EXTRA_SHORTCUT_INTENT, intent); // 启动Intent
		context.sendBroadcast(sc);
	}

	/**
	 * 删除当前应用的桌面快捷方式
	 * <p>
	 * 需要权限
	 * <p>
	 * &#60;uses-permission
	 * android:name="com.android.launcher.permission.UNINSTALL_SHORTCUT" />
	 * <p>
	 * 
	 * @param context
	 *            Context
	 * @param name
	 *            快捷方式名
	 */
	public static final void delShortcut(Context context, Intent intent) {
		Intent sc = new Intent("com.android.launcher.action.UNINSTALL_SHORTCUT");
		// sc.putExtra(Intent.EXTRA_SHORTCUT_NAME, name); // 快捷方式名
		sc.putExtra(Intent.EXTRA_SHORTCUT_INTENT, intent); // 启动Intent
		context.sendBroadcast(sc);
	}

	/**
	 * 判断是否有快捷方式(对于不同机型不一定有效)
	 * <p>
	 * 需要权限
	 * <p>
	 * &#60;uses-permission
	 * android:name="com.android.launcher.permission.READ_SETTINGS" />
	 * <p>
	 * 
	 * @param context
	 *            Context
	 * @param name
	 *            快捷方式名
	 * @return
	 */
	public static final boolean hasShortcut(Context context, String name) {
		String uriString = "content://com.android.launcher.settings/favorites?notify=true";
		Uri uri = Uri.parse(uriString);
		Cursor c = context.getContentResolver().query(uri,
				new String[] { "title" }, "title=?", new String[] { name },
				null);
		return (c != null && c.getCount() > 0);
	}

	/**
	 * 调用系统App发送邮件
	 * 
	 * @param context
	 * @param title
	 *            发送邮件…
	 * @param to
	 *            发送到的邮件地址
	 * @param subject
	 *            主题
	 * @param content
	 *            内容
	 */
	public static final void sendEmail(Context context, String title,
			String subject, String content, String... to) {
		// 创建Intent
		Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
		// 设置内容类型
		emailIntent.setType("plain/text");
		// 设置额外信息
		emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, to);
		emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, subject);
		emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, content);
		// 启动Activity
		context.startActivity(Intent.createChooser(emailIntent, title));
	}

	/**
	 * 获取系统信息
	 * 
	 * @return
	 */
	public static String getDeviceInfo() {
		StringBuilder b = new StringBuilder();
		b.append("Product: ").append(Build.PRODUCT);
		b.append("\nCPU_ABI: ").append(Build.CPU_ABI);
		b.append("\nTAGS: ").append(Build.TAGS);
		b.append("\nVERSION_CODES.BASE: ").append(Build.VERSION_CODES.BASE);
		b.append("\nMODEL: ").append(Build.MODEL);
		b.append("\nSDK_INT: ").append(Build.VERSION.SDK_INT);
		b.append("\nVERSION.RELEASE: ").append(Build.VERSION.RELEASE);
		b.append("\nDEVICE: ").append(Build.DEVICE);
		b.append("\nDISPLAY: ").append(Build.DISPLAY);
		b.append("\nBRAND: ").append(Build.BRAND);
		b.append("\nBOARD: ").append(Build.BOARD);
		b.append("\nFINGERPRINT: ").append(Build.FINGERPRINT);
		b.append("\nID: ").append(Build.ID);
		b.append("\nMANUFACTURER: ").append(Build.MANUFACTURER);
		b.append("\nUSER: ").append(Build.USER).append('\n');
		return b.toString();
	}
}
