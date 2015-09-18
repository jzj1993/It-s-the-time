package com.jzj.alarm;

import com.jzj.alarm.R;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.FloatMath;
import android.view.View;

/**
 * 指针式钟表View
 * 依赖：R.drawable.bg_clock
 * 
 * @author jzj
 * @since 2014-08-22
 */
public class ClockView extends View {

	protected int radius;
	protected Pointer pointerM, pointerS;

	public ClockView(Context context) {
		this(context, null);
	}

	public ClockView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	private final void init() {
		this.setBackgroundResource(R.drawable.bg_clock);
		pointerM = new Pointer(60);
		pointerS = new Pointer(60);
		setTime(5, 35);
	}

	/**
	 * 设置时间
	 * 
	 * @param minute
	 * @param second
	 */
	public void setTime(int minute, int second) {
		pointerM.setCurrentVal(minute);
		pointerS.setCurrentVal(second);
		this.invalidate();
	}

	protected void initPointerSize() {
		pointerM.setPointerLength((int) (radius * 0.6f));
		pointerM.setPointerWidth((int) (radius * 0.06f));
		pointerS.setPointerLength((int) (radius * 0.7f));
		pointerS.setPointerWidth((int) (radius * 0.04f));
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		final int centerX = getWidth() / 2;
		final int centerY = getHeight() / 2;
		pointerM.draw(canvas, centerX, centerY);
		pointerS.draw(canvas, centerX, centerY);
	}

	/**
	 * 计算控件尺寸
	 */
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		final int width = MeasureSpec.getSize(widthMeasureSpec);
		final int height = MeasureSpec.getSize(heightMeasureSpec);
		final int length = (width < height) ? width : height;
		radius = length / 2;
		initPointerSize();
		setMeasuredDimension(length, length);
	}

	// @Override
	// public void onLayout(boolean changed, int left, int top, int right,
	// int bottom) {
	// super.onLayout(changed, left, top, right, bottom);
	// }

	/**
	 * 指针控件
	 */
	protected class Pointer {

		protected Paint paint;
		protected int roundVal, currentVal; // 一圈表示的值 // 当前值
		protected int pointerLength, pointerWidth; // 指针宽度和长度
		protected int offsetX, offsetY; // 指针终点坐标偏移

		public Pointer() {
			this(60);
		}

		public Pointer(int roundVal) {
			paint = new Paint(Paint.ANTI_ALIAS_FLAG);
			paint.setColor(Color.WHITE);
			this.setRoundVal(roundVal);
		}

		public void setRoundVal(int roundVal) {
			this.roundVal = roundVal;
		}

		public void setPointerLength(int pointerLength) {
			this.pointerLength = pointerLength;
			this.calcOffset();
		}

		public void setPointerWidth(int pointerWidth) {
			this.pointerWidth = pointerWidth;
			paint.setStrokeWidth(pointerWidth);
		}

		public void setCurrentVal(int currentVal) {
			this.currentVal = currentVal;
			this.calcOffset();
		}

		/**
		 * 绘制指针
		 * 
		 * @param canvas
		 * @param centerX
		 * @param centerY
		 */
		public void draw(Canvas canvas, float centerX, float centerY) {
			canvas.drawLine(centerX, centerY, centerX + offsetX, centerY
					+ offsetY, paint);
		}

		/**
		 * 计算指针终点坐标偏移
		 */
		protected void calcOffset() {
			final float radians = (2f * (float) Math.PI) * currentVal
					/ roundVal; // 指针指向角度
			offsetX = (int) (pointerLength * FloatMath.sin(radians));
			offsetY = -(int) (pointerLength * FloatMath.cos(radians));
		}
	}
}
