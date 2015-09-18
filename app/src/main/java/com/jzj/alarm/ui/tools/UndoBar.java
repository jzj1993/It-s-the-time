package com.jzj.alarm.ui.tools;

import java.util.Stack;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.View;

public abstract class UndoBar<DataType> extends PopMenu {

	private Stack<DataType> stack = new Stack<DataType>();
	private Handler handler = new Handler(Looper.getMainLooper());
	private Runnable runnable = new Runnable() {
		@Override
		public void run() {
			dismiss();
		}
	};

	public UndoBar(Context context) {
		super(context);
	}

	/**
	 * 获取撤销操作的按钮id
	 *
	 * @return
	 */
	public abstract int getUndoButtonId();

	/**
	 * 获取文本控件id
	 *
	 * @return
	 */
	public abstract int getUndoTextId();

	/**
	 * Undo事件发生(按钮被点击同时有可恢复数据)
	 *
	 * @param 可恢复的数据
	 * @return 显示的文本
	 */
	public abstract String onUndoEvent(DataType data);

	/**
	 * Undo事件发生(按钮被点击同时无可恢复数据)
	 *
	 * @return 显示的文本
	 */
	public abstract String onUndoNoDataEvent();

	@Override
	public boolean onViewClick(View v) {
		if (v.getId() == getUndoButtonId()) {
			final DataType dat = getUndoData();
			final String text = (dat == null) ? onUndoNoDataEvent()
					: onUndoEvent(dat);
			showUndoBar(text);
		}
		return false;
	}

	@Override
	public int[] getOnClickViewIds() {
		return new int[] { getUndoButtonId(), getUndoTextId() };
	}

	/**
	 * 删除操作
	 *
	 * @param data
	 *            删除的数据
	 * @param textResId
	 *            显示文本
	 */
	public void delete(DataType data, int textResId) {
		stack.push(data);
		showUndoBar(context.getString(textResId));
	}

	/**
	 * 删除操作
	 *
	 * @param data
	 *            删除的数据
	 * @param text
	 *            显示文本
	 */
	public void delete(DataType data, String text) {
		stack.push(data);
		showUndoBar(text);
	}

	/**
	 * 显示或刷新UndoBar，并清零定时器(用于延迟隐藏窗口)
	 *
	 * @param text
	 */
	public void showUndoBar(String text) {
		super.getBuilder().setCanceledOnTouchOutside(false)
				.setViewText(getUndoTextId(), text);
		super.show();
		handler.removeCallbacks(runnable);
		handler.postDelayed(runnable, 5000);
	}

	/**
	 * 撤销操作
	 *
	 * @return 撤销的数据
	 */
	private final DataType getUndoData() {
		return stack.empty() ? null : stack.pop();
	}

	/**
	 * 隐藏显示，并清空可恢复数据
	 *
	 * @return
	 */
	public boolean dismiss() {
		stack.clear();
		return super.dismiss();
	}
}
