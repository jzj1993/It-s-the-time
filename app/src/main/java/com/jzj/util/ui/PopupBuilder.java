package com.jzj.util.ui;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager.LayoutParams;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;

/**
 * 自定义PopupWindow创建
 * (本类为通用工具类，不依赖任何外部xml资源，需要的资源直接在代码中动态设置)
 *
 * @author jzj
 * @since 2014-09-05
 */
public class PopupBuilder {

	private PopupWindow pop;
	private ViewBuilder vb;

	public PopupBuilder(Context context, int layoutResId) {
		vb = new ViewBuilder(context, layoutResId);
		pop = new PopupWindow(vb.getContentView(), LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);
	}

	public static PopupBuilder from(Context context, int layoutResId) {
		return new PopupBuilder(context, layoutResId);
	}

	public View getContentView() {
		return vb.getContentView();
	}

	public PopupBuilder setBackgroungColor(int color) {
		vb.setBackgroungColor(color);
		return this;
	}

	public PopupBuilder setOnClickListener(int viewResId,
										   OnClickListener listener) {
		vb.setOnClickListener(viewResId, listener);
		return this;
	}

	public PopupBuilder setViewAnimation(int viewResId, int animResId) {
		vb.setViewAnimation(viewResId, animResId);
		return this;
	}

	public PopupBuilder setViewText(int viewResId, String text) {
		vb.setViewText(viewResId, text);
		return this;
	}

	public PopupBuilder setViewText(int viewResId, int textResId) {
		vb.setViewText(viewResId, textResId);
		return this;
	}

	public PopupBuilder setOnClickDismissView(int viewResId) {
		vb.setOnClickListener(viewResId, new OnClickListener() {
			@Override
			public void onClick(View v) {
				pop.dismiss();
			}
		});
		return this;
	}

	public PopupBuilder setOnDismissListener(OnDismissListener listener) {
		pop.setOnDismissListener(listener);
		return this;
	}

	public PopupBuilder setCanceledOnTouchOutside(boolean flag) {
		pop.setOutsideTouchable(flag);
		if (flag)
			pop.setBackgroundDrawable(new ColorDrawable(0));
		return this;
	}

	public PopupBuilder setAnimationStyle(int anim) {
		pop.setAnimationStyle(anim);
		return this;
	}

	public PopupWindow create() {
		pop.update();
		return pop;
	}

	public PopupWindow showAsDropDown(View anchor) {
		create();
		pop.showAsDropDown(anchor);
		return pop;
	}

	public PopupWindow showAsDropDown(View anchor, int xoff, int yoff) {
		create();
		pop.showAsDropDown(anchor, xoff, yoff);
		return pop;
	}

	public PopupWindow showAtLocation(View parent, int gravity, int x, int y) {
		create();
		pop.showAtLocation(parent, gravity, x, y);
		return pop;
	}

	public PopupWindow showUnder(View view) {
		int[] location = { 0, 0 };
		view.getLocationOnScreen(location);
		location[1] += view.getHeight();
		return showAtLocation(view, Gravity.CENTER_HORIZONTAL | Gravity.TOP, 0,
				location[1]);
	}
}
