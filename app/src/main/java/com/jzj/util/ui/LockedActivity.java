package com.jzj.util.ui;

import com.jzj.util.SystemUtils;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.PowerManager.WakeLock;
import android.view.KeyEvent;

/**
 * 屏蔽手机按键的Activity
 * 全屏，锁定状态直接解锁显示，保持屏幕常亮，屏蔽所有按键(Power键除外，Home键屏蔽对部分机型有效)，onPause重启，finish自动解锁
 * <p>
 * 用法：
 * <p>
 * 自定义Activity并继承本Activity，并在Manifest中注册即可(本Activity不需注册)
 * <p>
 * &#60;activity
 * <p>
 * android:name=".ui.MyActivity"
 * <p>
 * android:launchMode="singleInstance"
 * <p>
 * android:theme="@android:style/Theme.NoTitleBar.Fullscreen" />
 * <p>
 * 
 * @author jzj
 * @since 2014-09-21
 */
public class LockedActivity extends Activity {

	private boolean locked = false;
	private WakeLock wakeLock = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		SystemUtils.setFullScreen(this);
		SystemUtils.disableKeyguard(this);
		SystemUtils.showWhenLocked(this);
		// SystemUtils.keepScreenOn(this);
		SystemUtils.blockHomeKey(this);
	}

	/**
	 * 锁定状态下：重新点亮屏幕并重启Activity
	 */
	@Override
	public void onPause() {
		super.onPause();
		if (locked) {
			// onPause被执行: 电源键按下,或其他Activity启动(如点击状态栏等)
			// 电源键按下: onPause,屏幕点亮onResume
			// 重新唤醒屏幕，重启Activity
			acquire();
			this.startActivity(new Intent(this, this.getClass()));
		} else {
			// SystemUtils.disableKeepScreenOn(this);
			release();
		}
	}

	/**
	 * 锁定状态下拦截所有可能的按键，解锁状态下不拦截
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		return locked ? true : super.onKeyDown(keyCode, event);
	}

	/**
	 * 解锁并退出Activity
	 */
	@Override
	public void finish() {
		this.setLocked(false);
		super.finish();
	}

	/**
	 * 设置锁定状态，设置锁定会点亮屏幕(如果屏幕处于熄灭状态)
	 * 
	 * @param locked
	 */
	protected final void setLocked(boolean locked) {
		this.locked = locked;
		if (locked) {
			acquire();
		}
	}

	/**
	 * 判断锁定状态
	 * 
	 * @return
	 */
	protected final boolean isLocked() {
		return locked;
	}

	private final void acquire() {
		if (wakeLock != null)
			wakeLock.acquire();
		else
			wakeLock = SystemUtils.acquireLightWakeLock(this);
	}

	private final void release() {
		if (wakeLock != null) {
			wakeLock.release();
			wakeLock = null;
		}
	}
}
