package com.jzj.alarm;

import android.content.Context;
import android.util.AttributeSet;
import android.util.FloatMath;
import android.view.View;
import android.view.ViewGroup;

/**
 * 用于实现纵向六边形按钮之间的位置排列 (2323方式)
 * 
 * @author jzj
 */
public class HexViewGroup extends ViewGroup {

	private static final float cos30 = FloatMath.cos((float) Math.PI / 6f);

	public HexViewGroup(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {

		final int childWidth = (int) (getWidth() / 3);
		final int childHeight = (int) (childWidth / cos30);
		final int offsetX = childWidth / 2;
		final int offsetY = childHeight * 3 / 4;
		final int childCount = getChildCount();
		for (int i = 0; i < childCount; i++) {
			final int left, top;
			switch (i % 5) {
			case 0:
				left = offsetX;
				top = (i / 5) * offsetY * 2;
				break;
			case 1:
				left = offsetX + childWidth;
				top = (i / 5) * offsetY * 2;
				break;
			case 2:
				left = 0;
				top = (i / 5) * offsetY * 2 + offsetY;
				break;
			case 3:
				left = childWidth;
				top = (i / 5) * offsetY * 2 + offsetY;
				break;
			case 4:
			default:
				left = childWidth * 2;
				top = (i / 5) * offsetY * 2 + offsetY;
				break;
			}
			this.getChildAt(i).layout(left, top, left + childWidth,
					top + childHeight);
		}
	}

	/**
	 * 计算控件及子控件尺寸
	 */
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		final int width = MeasureSpec.getSize(widthMeasureSpec);
		// final int height = MeasureSpec.getSize(heightMeasureSpec);
		final int childWidth = (int) (width / 3);
		final int childHeight = (int) (childWidth / cos30);
		// height = r * h * 3/4 + h * 1/4
		final int r = calcRow(getChildCount());
		final int height = (int) ((r * 0.75f + 0.25f) * childHeight);
		setMeasuredDimension(width, height);
		for (int i = 0; i < getChildCount(); i++) {
			final View child = getChildAt(i);
			child.measure(child.getMeasuredWidth(), child.getMeasuredHeight());
		}
	}

	/**
	 * 计算子控件行数
	 * 
	 * @param childCount
	 * @return
	 */
	private final int calcRow(int childCount) {
		final int mod = childCount % 5;
		int row = childCount / 5 * 2;
		if (mod == 0)
			return row;
		if (mod < 3)
			return row + 1;
		return row + 2;
	}
}
