package com.jzj.alarm.ui.tools;

import com.jzj.alarm.R;
import com.jzj.util.SystemUtils;
import com.jzj.util.ui.DialogBuilder;

import android.content.Context;
import android.content.DialogInterface.OnDismissListener;
import android.text.InputType;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.EditText;

/**
 * 快速创建自定义对话框，使用方法类似AlertDialog.Builder
 * 本工具类只适用于此工程，对话框布局为R.layout.dialog_general.xml
 * 对话框的构建使用了DialogBuilder，因此对于本工具类中未提供的接口，
 * 可通过getDialogBuilder获取DialogBuilder进一步设置
 * 另外本工具类提供了几个静态方法，用于更快速的生成常用的几种对话框
 *
 * @author jzj
 * @since 2014-08-21
 */
public class SimpleBuilder {

	private DialogBuilder b;
	private EditText edit;

	public SimpleBuilder(Context context) {
		b = DialogBuilder
				.from(context, R.layout.dialog_general, R.style.dialog_style)
				.setAnimationStyle(R.style.quick_scale_anim)
				.setBackgroungColor(Theme.getRandomBgColor(context));
	}

	public static SimpleBuilder from(Context context) {
		return new SimpleBuilder(context);
	}

	public SimpleBuilder setBackgroundColor(int color) {
		b.setBackgroungColor(color);
		return this;
	}

	public SimpleBuilder setOnDismissListener(OnDismissListener dismissListener) {
		b.setOnDismissListener(dismissListener);
		return this;
	}

	public SimpleBuilder setPositiveButton(OnClickListener listener) {
		b.setOnClickDismissView(R.id.bn_dialog_ok, listener);
		return this;
	}

	public SimpleBuilder setNegativeButton(OnClickListener listener) {
		b.setOnClickCancelView(R.id.bn_dialog_cancel, listener);
		return this;
	}

	public SimpleBuilder setTitle(int resId) {
		b.setViewText(R.id.tx_dialog_title, resId);
		return this;
	}

	public SimpleBuilder setMessage(int resId) {
		b.setViewText(R.id.tx_dialog_msg, resId);
		return this;
	}

	public SimpleBuilder setView(View v) {
		return this.setView(v, null);
	}

	public SimpleBuilder setView(View v, LayoutParams lp) {
		if (lp != null)
			b.addChildView(v, 1, lp);
		else
			b.addChildView(v, 1);
		return this;
	}

	public SimpleBuilder setEditText(String defVal) {
		b.setViewText(R.id.ed_dialog_input, defVal);
		edit = (EditText) b.getView(R.id.ed_dialog_input);
		return this;
	}

	public SimpleBuilder setRawInputType(int type) {
		if (edit != null)
			edit.setRawInputType(type);
		return this;
	}

	public EditText getEditText() {
		return edit;
	}

	public String getEditString() {
		return edit == null ? "" : edit.getText().toString();
	}

	public DialogBuilder getDialogBuilder() {
		return b;
	}

	public SimpleBuilder show() {
		b.show();
		return this;
	}

	public static final void showText(Context context, int titleResId, int resId) {
		showText(context, titleResId, resId, null);
	}

	public static final void showText(Context context, int titleResId,
									  int resId, OnDismissListener dismissListener) {
		SimpleBuilder.from(context).setTitle(titleResId).setMessage(resId)
				.setPositiveButton(null).setOnDismissListener(dismissListener)
				.show();
	}

	public static void inputInt(Context context, final int title,
								final int defVal, final OnGetIntListener listener) {
		final SimpleBuilder b = SimpleBuilder.from(context).setTitle(title)
				.setEditText(String.valueOf(defVal))
				.setRawInputType(InputType.TYPE_CLASS_NUMBER);
		b.setPositiveButton(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					int d = Integer.parseInt(b.getEditString());
					listener.onGetInt(d);
				} catch (Exception e) {
				}
			}
		}).setNegativeButton(null).show();
		SystemUtils.showIme(context, b.getEditText());
	}

	public interface OnGetIntListener {
		public void onGetInt(int d);
	}
}
