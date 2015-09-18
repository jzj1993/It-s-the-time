package com.jzj.alarm.ui;

import com.jzj.alarm.R;
import com.jzj.alarm.ui.tools.AlarmController;
import com.jzj.alarm.ui.tools.FirstRunTools;
import com.jzj.alarm.ui.tools.Theme;
import com.jzj.alarm.voice.Understander;
import com.jzj.util.AppErrorController;
import com.jzj.util.ui.LockedActivity;

import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Animation.AnimationListener;
import android.content.Intent;

/**
 * 启动界面
 *
 * @author jzj
 */
public class SplashActivity extends LockedActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(createContentView());

		// 在此处执行一些数据处理等任务

		// 设置异常捕获
		AppErrorController.setLogAndExitForException(this);

		// 首次运行添加示例闹铃
		FirstRunTools.checkAddSampleAlarm(this);

		// 刷新闹铃时间
		AlarmController.getAlarmMgr(this).calcSetNextAlarm();

		// 提前登陆讯飞API
		Understander.login(this);
	}

	private final View createContentView() {
		Animation anim = AnimationUtils
				.loadAnimation(this, R.anim.welcome_anim);
		// 动画效果执行完毕后,View对象保留在终止的位置
		anim.setFillEnabled(true);
		anim.setFillAfter(true);
		// 动画执行监听器
		anim.setAnimationListener(listener);

		View v = getLayoutInflater().inflate(R.layout.activity_splash, null);
		v.setBackgroundColor(Theme.getRandomBgColor(this));
		v.setAnimation(anim);
		return v;
	}

	/**
	 * 结束欢迎界面并转到软件的主界面
	 */
	private final void startMain() {
		this.startActivity(new Intent(this, MainActivity.class));
		this.finish();
		this.overridePendingTransition(0, 0);
	}

	/**
	 * 动画监听器
	 */
	private AnimationListener listener = new AnimationListener() {
		@Override
		public void onAnimationEnd(Animation animation) {
			startMain();
		}

		@Override
		public void onAnimationStart(Animation animation) {
		}

		@Override
		public void onAnimationRepeat(Animation animation) {
		}
	};
}
