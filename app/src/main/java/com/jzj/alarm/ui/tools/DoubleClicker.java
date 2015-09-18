package com.jzj.alarm.ui.tools;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;

/**
 * 控件单击双击判决
 *
 * @author jzj
 * @since 2014-09-25
 * @param <ViewType>
 */
public abstract class DoubleClicker {

	private static final long INTERVAL = 200;
	private static final int MSG_CLICK = 1;

	private View lastView;
	private long lastTime = 0;

	public void clickEvent(View v) {
		long curTime = System.currentTimeMillis();
		if (lastView == v && curTime - lastTime < INTERVAL) {
			handler.removeMessages(MSG_CLICK);
			onDoubleClick(v);
		} else {
			Message msg = Message.obtain(handler, MSG_CLICK, v);
			handler.sendMessageDelayed(msg, INTERVAL);
		}
		lastView = v;
		lastTime = curTime;
	}

	private Handler handler = new Handler(Looper.getMainLooper(),
			new Handler.Callback() {

				@Override
				public boolean handleMessage(Message msg) {
					if (msg.what == MSG_CLICK && msg.obj != null
							&& msg.obj instanceof View) {
						onSingleClick((View) msg.obj);
						return true;
					}
					return false;
				}
			});

	public abstract void onSingleClick(View v);

	public abstract void onDoubleClick(View v);
}
