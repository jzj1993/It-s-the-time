package com.jzj.alarm;

import android.content.Context;
import android.widget.Toast;

/**
 * 自定义Toast视图
 * 
 * @author jzj
 */
public class ToastView {

	public static final void show(Context context, CharSequence text) {
		// TODO
		Toast t = Toast.makeText(context, text, Toast.LENGTH_SHORT);
		t.getView().setBackgroundColor(0xAA2f8cec);
		t.show();
	}

	public static final void show(Context context, int text) {
		// TODO
		Toast t = Toast.makeText(context, text, Toast.LENGTH_SHORT);
		t.getView().setBackgroundColor(0xAA2f8cec);
		t.show();
	}
}
