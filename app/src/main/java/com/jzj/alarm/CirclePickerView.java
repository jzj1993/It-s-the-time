package com.jzj.alarm;

import com.jzj.alarm.R;
import com.jzj.util.painter.TextPainter;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * 圆形参数设置View，可快速设置小时分钟等
 * <p>
 * 点击中心区域时，执行点击操作(执行清零当前值/当前值步进等操作)
 * <p>
 * 点击中心区域并滑动时，无操作
 * <p>
 * 点击非中心区域时，执行设置操作
 * <p>
 * 点击非中心区域并滑动时，执行设置操作
 * <p>
 * 依赖R.styleable.*(attr.xml)
 * <p>
 * 
 * @author jzj
 * @since 2014-08-22
 */
public class CirclePickerView extends View {

	// 以下为一些尺寸占控件边长的百分比(归一化尺寸)
	private static final float BLANK_PER = 0.1f; // 圆环外围空余
	private static final float WIDTH_PER = 0.08f; // 圆环宽度
	private static final float TEXTSIZE_PER = 0.3f; // 文字尺寸
	private static final float clickR = 0.5f - BLANK_PER - WIDTH_PER * 1.8f; // 中心点击区域半径
	private static final float clickArea = clickR * clickR; // 中心点击区域面积

	private int maxValue, curValue, stepValue; // 一圈表示的最大值,当前值,步进值,总步数
	private boolean touchOutside; // touchProcess方法中使用的变量:触摸中心圆外区域,可滑动设置值

	private Paint circlePaint, arcPaint;
	private RectF rect;
	private TextPainter text;

	public CirclePickerView(Context context) {
		this(context, null);
	}

	public CirclePickerView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
		// 从xml资源文件加载属性设置
		TypedArray attr = context.obtainStyledAttributes(attrs,
				R.styleable.CircleView);
		final int arcColor = attr.getColor(R.styleable.CircleView_arc_color,
				Color.WHITE);
		final int circleColor = attr.getColor(
				R.styleable.CircleView_circle_color, Color.GRAY);
		final int txColor = attr.getColor(R.styleable.CircleView_tx_color,
				Color.BLUE);
		final int stepVal = attr.getInteger(R.styleable.CircleView_step_val, 5);
		final int maxVal = attr.getInteger(R.styleable.CircleView_max_val, 60);
		final int curVal = attr.getInteger(R.styleable.CircleView_cur_val, 55);
		attr.recycle();

		this.setArcColor(arcColor);
		this.setCircleColor(circleColor);
		this.setTextColor(txColor);
		if (!this.setValue(stepVal, maxVal, curVal))
			this.setValue(5, 60, 55);
	}

	private final void init(Context context) {
		text = new TextPainter(context, "");
		rect = new RectF();

		circlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		circlePaint.setStyle(Paint.Style.STROKE);

		arcPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		arcPaint.setStyle(Paint.Style.STROKE);
		// arcPaint.setStrokeCap(Cap.ROUND);
	}

	/**
	 * 初始化尺寸
	 * 
	 * @param length
	 *            View边长
	 */
	private final void initSize(float length) {
		final float w = WIDTH_PER * length;
		final float l_t = (BLANK_PER + WIDTH_PER / 2) * length;
		final float r_b = length - l_t;
		rect.set(l_t, l_t, r_b, r_b);
		text.setSize(TEXTSIZE_PER * length);
		arcPaint.setStrokeWidth(w);
		circlePaint.setStrokeWidth(w);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		canvas.drawArc(rect, 0, 360, false, circlePaint);
		canvas.drawArc(rect, -90, 360f * curValue / maxValue, false, arcPaint);
		text.setText(curValue + "");
		text.drawAtCenter(canvas, getWidth(), getHeight());
	}

	/**
	 * 计算控件尺寸
	 */
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		final int width = MeasureSpec.getSize(widthMeasureSpec);
		final int height = MeasureSpec.getSize(heightMeasureSpec);
		final int length = (width < height) ? width : height;
		initSize(length);
		setMeasuredDimension(length, length);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {

		switch (event.getAction() & MotionEvent.ACTION_MASK) {
		case MotionEvent.ACTION_DOWN:
			touchProcess(false, event.getX(), event.getY());
			return true;
		case MotionEvent.ACTION_MOVE:
			touchProcess(true, event.getX(), event.getY());
			return true;
			// case MotionEvent.ACTION_CANCEL:
			// case MotionEvent.ACTION_UP: // 释放
			// return true;
		}
		return super.onTouchEvent(event);
	}

	/**
	 * Touch事件处理子程序
	 * 
	 * @param isMove
	 *            true表示动作为Move，false表示动作为Down
	 * @param touchX
	 *            Touch事件x坐标
	 * @param touchY
	 *            Touch事件y坐标
	 */
	private void touchProcess(boolean isMove, float touchX, float touchY) {

		if (isMove && !touchOutside)
			return;

		// 控件尺寸
		final int w = getWidth();
		final int h = getHeight();
		// 控件中心坐标
		final float cx = w * 0.5f;
		final float cy = h * 0.5f;
		// 点击位置归一化坐标(以控件中心为原点,+x方向向右,+y方向向下)
		final float x = (touchX - cx) / w;
		final float y = (touchY - cy) / h;

		if (!isMove) {
			if (x * x + y * y < clickArea) {
				/*** 执行点击操作 ***/
				this.setCurrentValue(curValue + stepValue);
				touchOutside = false;
				return;
			} else {
				touchOutside = true;
			}
		}

		/*** 执行设置操作 ***/
		// 计算与正上方向夹角弧度占圆周的比例
		final double percent = Math.atan2(x, -y) / (Math.PI * 2); // atan2:[-PI,PI]
		this.setCurrentValue(Math.round((float) (maxValue * percent)));
	}

	/**
	 * 设置文本的颜色
	 * 
	 * @param c
	 */
	public final void setTextColor(int c) {
		this.text.setTextColor(c);
		this.invalidate();
	}

	/**
	 * 设置圆弧的颜色
	 * 
	 * @param c
	 */
	public final void setArcColor(int c) {
		this.arcPaint.setColor(c);
		this.invalidate();
	}

	/**
	 * 设置圆环的颜色
	 * 
	 * @param c
	 */
	public final void setCircleColor(int c) {
		this.circlePaint.setColor(c);
		this.invalidate();
	}

	/**
	 * 设置值
	 * 
	 * @param step
	 *            步进值,应为正整数
	 * @param max
	 *            最大值,应为步进值的整数倍
	 * @param cur
	 *            当前值,应大于或等于0,小于最大值(自动转换为步进值的整数倍)
	 * @return step和max的值不符合要求时返回false(cur的值自动处理)
	 */
	public boolean setValue(int step, int max, int cur) {
		if (step <= 0)
			return false;
		if (max % step != 0)
			return false;
		maxValue = max;
		stepValue = step;
		this.setCurrentValue(cur);
		return true;
	}

	/**
	 * 设置当前值
	 * 
	 * @param cur
	 *            当前值,应大于或等于0,小于最大值(自动转换为步进值的整数倍)
	 */
	public void setCurrentValue(int cur) {

		// cur转换为步进值的整数倍(四舍五入)
		cur = Math.round((float) cur / stepValue) * stepValue;

		// cur的范围转换至[0,max)
		if (cur < 0) {
			while (cur < 0)
				cur += maxValue;
		} else if (cur >= maxValue) {
			cur %= maxValue;
		}
		curValue = cur;
		this.invalidate();
	}

	/**
	 * 获取当前值
	 * 
	 * @return
	 */
	public final int getCurrentValue() {
		return this.curValue;
	}
}
