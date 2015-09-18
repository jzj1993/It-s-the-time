package com.jzj.alarm.ui.tools;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.PopupWindow;

import com.jzj.util.ui.PopupBuilder;

public abstract class PopMenu implements OnClickListener {

	protected Context context;
	protected PopupWindow popMenu;
	protected PopupBuilder builder;

	public PopMenu(Context context) {
		this.context = context;
	}

	/**
	 * 获取布局id
	 *
	 * @return
	 */
	public abstract int getLayoutId();

	/**
	 * 获取窗口动画id
	 *
	 * @return
	 */
	public abstract int getAnimationStyleId();

	/**
	 * 获取布局中可点击的控件id
	 *
	 * @return
	 */
	public abstract int[] getOnClickViewIds();

	/**
	 * 显示PopupWindow
	 *
	 * @param pop
	 */
	public abstract void showPop(PopupWindow pop);

	/**
	 * 控件被点击
	 *
	 * @param v
	 * @return 返回true则菜单消失，否则菜单保持显示
	 */
	public abstract boolean onViewClick(View v);

	@Override
	public void onClick(View v) {
		if (onViewClick(v))
			dismiss();
	}

	/**
	 * 获取Builder
	 *
	 * @return
	 */
	public PopupBuilder getBuilder() {
		if (builder == null) {
			builder = PopupBuilder.from(context, getLayoutId())
					.setCanceledOnTouchOutside(true);

			final int anim = getAnimationStyleId();
			if (anim != 0)
				builder.setAnimationStyle(anim);

			int[] ids = getOnClickViewIds();
			if (ids != null) {
				for (int id : ids)
					builder.setOnClickListener(id, this);
			}
		}
		return builder;
	}

	/**
	 * 创建PopupWindow
	 *
	 * @return
	 */
	public PopupWindow build() {
		return popMenu = getBuilder().create();
	}

	/**
	 * 显示菜单
	 */
	public void show() {
		if (popMenu == null)
			build();
		showPop(popMenu);
	}

	/**
	 * 显示/隐藏菜单
	 */
	public void switchPop() {
		if (isShowing())
			popMenu.dismiss();
		else
			show();
	}

	/**
	 * 判断菜单是否在显示
	 *
	 * @return
	 */
	public boolean isShowing() {
		return (popMenu != null && popMenu.isShowing());
	}

	/**
	 * 取消菜单显示
	 *
	 * @return 调用本函数之前，菜单是否在显示
	 */
	public boolean dismiss() {
		if (isShowing()) {
			popMenu.dismiss();
			return true;
		}
		return false;
	}
}
