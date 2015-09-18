package com.jzj.util.ui;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnDismissListener;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;

/**
 * 自定义Dialog创建
 * (本类为通用工具类，不依赖任何外部xml资源，需要的资源直接在代码中动态设置)
 *
 * @author jzj
 * @since 2014-08-21
 */
public class DialogBuilder {

	private Dialog dialog;
	private ViewBuilder vb;

	public DialogBuilder(Context context, int layoutResId, int style) {
		vb = new ViewBuilder(context, layoutResId);
		this.dialog = new Dialog(context, style);
	}

	public static DialogBuilder from(Context context, int layoutResId, int style) {
		return new DialogBuilder(context, layoutResId, style);
	}

	public DialogBuilder setAnimationStyle(int anim) {
		dialog.getWindow().setWindowAnimations(anim);
		return this;
	}

	public DialogBuilder setBackgroungColor(int color) {
		vb.setBackgroungColor(color);
		return this;
	}

	public DialogBuilder setBackKeyCancelable(boolean flag) {
		dialog.setCancelable(flag);
		return this;
	}

	public DialogBuilder setCanceledOnTouchOutside(boolean flag) {
		dialog.setCanceledOnTouchOutside(flag);
		return this;
	}

	public DialogBuilder setOnDismissListener(OnDismissListener listener) {
		dialog.setOnDismissListener(listener);
		return this;
	}

	public DialogBuilder setOnCancelListener(OnCancelListener listener) {
		dialog.setOnCancelListener(listener);
		return this;
	}

	public DialogBuilder addChildView(View v, LayoutParams lp) {
		vb.addChildView(v, lp);
		return this;
	}

	public DialogBuilder addChildView(View v, int index) {
		vb.addChildView(v, index);
		return this;
	}

	public DialogBuilder addChildView(View v, int index, LayoutParams lp) {
		vb.addChildView(v, index, lp);
		return this;
	}

	public DialogBuilder setViewText(int viewResId, String text) {
		vb.setViewText(viewResId, text);
		return this;
	}

	public DialogBuilder setViewText(int viewResId, int textResId) {
		vb.setViewText(viewResId, textResId);
		return this;
	}

	public DialogBuilder setViewVisibility(int viewResId, int visibility) {
		vb.setViewVisibility(viewResId, visibility);
		return this;
	}

	public DialogBuilder setViewHide(int viewResId) {
		vb.setViewHide(viewResId);
		return this;
	}

	public DialogBuilder setViewAnimation(int viewResId, int animResId) {
		vb.setViewAnimation(viewResId, animResId);
		return this;
	}

	public DialogBuilder setOnClickListener(int viewResId,
											OnClickListener listener) {
		vb.setOnClickListener(viewResId, listener);
		return this;
	}

	public DialogBuilder setOnClickDismissView(int viewResId,
											   final OnClickListener listener) {
		vb.setOnClickListener(viewResId, new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (listener != null)
					listener.onClick(v);
				dialog.dismiss();
			}
		});
		return this;
	}

	public DialogBuilder setOnClickDismissView(int viewResId) {
		return this.setOnClickDismissView(viewResId, null);
	}

	public DialogBuilder setOnClickCancelView(int viewResId,
											  final OnClickListener listener) {
		vb.setOnClickListener(viewResId, new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (listener != null)
					listener.onClick(v);
				dialog.cancel();
			}
		});
		return this;
	}

	public DialogBuilder setOnClickCancelView(int viewResId) {
		return this.setOnClickCancelView(viewResId, null);
	}

	public View getView(int resId) {
		return vb.getContentView().findViewById(resId);
	}

	public View getContentView() {
		return vb.getContentView();
	}

	public Dialog create() {
		dialog.setContentView(vb.createContentView(), new LayoutParams(
				WindowManager.LayoutParams.WRAP_CONTENT,
				WindowManager.LayoutParams.WRAP_CONTENT));
		return dialog;
	}

	public Dialog show() {
		create().show();
		return dialog;
	}

	public Dialog showWithOffset(int gravity, int xoff, int yoff) {
		create();
		Window w = dialog.getWindow();
		WindowManager.LayoutParams lp = w.getAttributes();
		lp.x = xoff;
		lp.y = yoff;
		lp.gravity = gravity;
		w.setAttributes(lp);
		dialog.show();
		return dialog;
	}

	public Dialog showAtLocation(int x, int y) {
		return showWithOffset(Gravity.LEFT | Gravity.TOP, x, y);
	}
}
