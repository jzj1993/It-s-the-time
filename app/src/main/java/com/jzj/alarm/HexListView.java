package com.jzj.alarm;

import android.content.Context;
import android.util.AttributeSet;
import android.util.FloatMath;
import android.view.View;
import android.view.ViewGroup;

/**
 * 六边形ListView
 * 
 * @author jzj
 */
public class HexListView extends ViewGroup {

	private static final float cos30 = FloatMath.cos((float) Math.PI / 6f);

	/** 子View六边形高度占正六边形高度的比例，为1时子View为正六边形 **/
	private static final float childHeightPercent = 0.75f;

	/** false:第一个按钮在右上角; true:第一个按钮在左上角 **/
	private boolean leftTopFirst = false;

	public HexListView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	/**
	 * 第一个按钮左右位置
	 * 
	 * @param leftTopFirst
	 *            true:第一个按钮在左上角; false:第一个按钮在右上角
	 */
	public final void setLeftTopFirst(boolean leftTopFirst) {
		this.leftTopFirst = leftTopFirst;
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {

		final int childWidth = (int) (getWidth() * 0.8f);
		final int childHeight = (int) (childWidth * cos30 * childHeightPercent);
		final int childCount = getChildCount();

		for (int i = 0; i < childCount; ++i) {
			final boolean isLeft = (i % 2 == 0) ? leftTopFirst : !leftTopFirst;
			final int left = childWidth / (isLeft ? -4 : 2);
			final int top = (i - 1) * childHeight / 2;
			final View v = this.getChildAt(i);
			if (v instanceof HexView) {
				HexView hex = ((HexView) v);
				hex.setLeft(isLeft);
				if (i == 0) {
					hex.setPosStyle(HexView.Position.FIRST);
				} else if (i == childCount - 1) {
					hex.setPosStyle(HexView.Position.LAST);
				}
			}
			v.layout(left, top, left + childWidth, top + childHeight);
		}
	}

	/**
	 * 计算控件及子控件尺寸
	 */
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		final int width = MeasureSpec.getSize(widthMeasureSpec);
		final int height = (int) (width * 0.8f * cos30 * childHeightPercent)
				* (getChildCount() - 1) / 2;
		setMeasuredDimension(width, height);
		for (int i = 0; i < getChildCount(); i++) {
			final View child = getChildAt(i);
			child.measure(child.getMeasuredWidth(), child.getMeasuredHeight());
		}
	}
}
