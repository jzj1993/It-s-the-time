package com.jzj.util.ui;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

/**
 * 自定义View的快速创建，常用于对话框、PopupWindow、Toast等窗口
 * (设置子View的文本，动画，监听器，可见性等)
 *
 * @author jzj
 * @since 2014-08-21
 */
public class ViewBuilder {

	protected Context context;
	protected View contentView;

	private ArrayList<Anim> anims = new ArrayList<Anim>();

	public ViewBuilder(Context context, int layoutResId) {
		this.context = context;
		this.contentView = LayoutInflater.from(context).inflate(layoutResId,
				null);
	}

	public static ViewBuilder from(Context context, int layoutResId) {
		return new ViewBuilder(context, layoutResId);
	}

	public View getContentView() {
		return contentView;
	}

	public ViewBuilder setBackgroungColor(int color) {
		contentView.setBackgroundColor(color);
		return this;
	}

	public ViewBuilder addChildView(View v, int index) {
		if (contentView instanceof ViewGroup) {
			((ViewGroup) contentView).addView(v, index);
		}
		return this;
	}

	public ViewBuilder addChildView(View v, LayoutParams lp) {
		if (contentView instanceof ViewGroup) {
			((ViewGroup) contentView).addView(v, lp);
		}
		return this;
	}

	public ViewBuilder addChildView(View v, int index, LayoutParams lp) {
		if (contentView instanceof ViewGroup) {
			((ViewGroup) contentView).addView(v, index, lp);
		}
		return this;
	}

	public ViewBuilder setOnClickListener(int viewResId,
										  OnClickListener listener) {
		getVisibleView(viewResId).setOnClickListener(listener);
		return this;
	}

	public ViewBuilder setViewText(int viewResId, String text) {
		View v = getVisibleView(viewResId);
		if (v instanceof TextView) {
			((TextView) v).setText(text);
		}
		return this;
	}

	public ViewBuilder setViewText(int viewResId, int textResId) {
		View v = getVisibleView(viewResId);
		if (v instanceof TextView) {
			((TextView) v).setText(textResId);
		}
		return this;
	}

	public ViewBuilder setViewVisibility(int viewResId, int visibility) {
		getVisibleView(viewResId).setVisibility(visibility);
		return this;
	}

	public ViewBuilder setViewHide(int viewResId) {
		return this.setViewVisibility(viewResId, View.GONE);
	}

	public ViewBuilder setViewAnimation(int viewResId, int animResId) {
		anims.add(new Anim(viewResId, animResId));
		return this;
	}

	public View createContentView() {
		for (Anim a : anims) {
			a.start();
		}
		return contentView;
	}

	private final View getVisibleView(int id) {
		View v = contentView.findViewById(id);
		v.setVisibility(View.VISIBLE);
		return v;
	}

	private class Anim {

		private int viewResId;
		private int animResId;

		private Anim(int viewResId, int animResId) {
			this.viewResId = viewResId;
			this.animResId = animResId;
		}

		private void start() {
			Animation animation = AnimationUtils.loadAnimation(context,
					animResId);
			getVisibleView(viewResId).startAnimation(animation);
		}
	}
}
