package com.jzj.alarm;

import com.jzj.util.painter.HexPainter;
import com.jzj.util.painter.TextPainter;
import com.jzj.util.ui.ColorUtils;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.AttributeSet;
import android.util.FloatMath;
import android.view.MotionEvent;
import android.view.View;

/**
 * 六边形View 默认为横向
 * 
 * @author jzj
 */
public class HexViewSuper extends View {

	private static final float cos30 = FloatMath.cos((float) Math.PI / 6f);

	protected HexPainter hex;
	protected TextPainter tx;

	private boolean sizeInitialized = false; // 尺寸已被初始化标志

	private boolean horizontal = true;

	private int bgColor;
	private boolean textEnable; // 显示文本
	private float textSizePercent; // 文本尺寸占控件高度的百分比

	/**
	 * 从程序代码实例化
	 * 
	 * @param context
	 */
	public HexViewSuper(Context context) {
		this(context, null);
	}

	/**
	 * 从XML文件实例化
	 * 
	 * @param context
	 * @param attrs
	 */
	public HexViewSuper(Context context, AttributeSet attrs) {
		super(context, attrs);

		hex = new HexPainter();
		tx = new TextPainter(context);
		tx.setTextColor(Color.WHITE);

		int bgColor = Color.BLUE;
		boolean textEnable = true;
		String text = "";
		int textColor = Color.WHITE;
		float textSizePercent = 0.15f;

		TypedArray appearance = context.obtainStyledAttributes(attrs,
				R.styleable.HexViewSuper);

		if (appearance != null) {
			int n = appearance.getIndexCount();
			for (int i = 0; i < n; i++) {
				int attr = appearance.getIndex(i);

				switch (attr) {
				case R.styleable.HexViewSuper_bg_color:
					bgColor = appearance.getColor(attr, bgColor);
					break;
				case R.styleable.HexViewSuper_text:
					text = appearance.getString(attr);
					break;
				case R.styleable.HexViewSuper_text_color:
					textColor = appearance.getColor(attr, textColor);
					break;
				case R.styleable.HexViewSuper_text_enable:
					textEnable = appearance.getBoolean(attr, textEnable);
					break;
				case R.styleable.HexViewSuper_text_size_percent:
					textSizePercent = appearance.getFraction(attr, 1, 1,
							textSizePercent);
					break;
				}
			}

			appearance.recycle();
		}

		this.setBackgroundColor(bgColor);
		this.setTextEnable(textEnable);
		this.setText(text);
		this.setTextColor(textColor);
		this.setTextSizePercent(textSizePercent);
	}

	/**
	 * 初始化尺寸,在控件尺寸可获取时会被调用
	 */
	protected void initSize(int width, int height) {
		if (this.textSizePercent > 0)
			tx.setSize(height * this.textSizePercent);
	}

	/**
	 * 绘制控件上的文本等元素,在onDraw时会被调用,重载本函数以实现其他绘制方式
	 * 
	 * @param canvas
	 */
	protected void drawElements(Canvas canvas) {
		if (this.isTextEnable())
			tx.drawAtCenter(canvas, getWidth(), getHeight());
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		// 首次执行onDraw,初始化尺寸
		final int w = getWidth();
		final int h = getHeight();
		if (!this.sizeInitialized) {
			initSize(w, h);
			this.sizeInitialized = true;
		}
		// 边缘留出一点空白
		final float w1 = Math.max(1, (float) w * 0.005f);
		final float h1 = Math.max(1, (float) h * 0.005f);
		canvas.translate(w1, h1);
		if (horizontal)
			hex.drawHorizontal(canvas, w - 2 * w1, h - 2 * h1);
		else
			hex.drawVertical(canvas, w - 2 * w1, h - 2 * h1);
		canvas.translate(-w1, -h1);
		this.drawElements(canvas);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {

		switch (event.getAction() & MotionEvent.ACTION_MASK) {

		case MotionEvent.ACTION_DOWN:

			// 椭圆形有效点击区域的横向/纵向半径，如果是正六边形，则两者相等
			final float rx;
			final float ry;
			if (horizontal) {
				rx = getWidth() * (0.5f * cos30);
				ry = getHeight() * 0.5f;
			} else {
				rx = getWidth() * 0.5f;
				ry = getHeight() * (0.5f * cos30);
			}

			// 点击位置对控件中心构成椭圆的横向/纵向归一化半径
			final float x = (event.getX() - getWidth() * 0.5f) / rx;
			final float y = (event.getY() - getHeight() * 0.5f) / ry;

			// 点到椭圆形的有效点击区域以外,不响应点击事件
			if (x * x + y * y > 1f) {
				return false;
			} else {
				// 按下时颜色加深
				float[] hsv = new float[3];
				Color.colorToHSV(bgColor, hsv);
				hsv[2] = hsv[2] / 2;
				hex.setColor(Color.HSVToColor(hsv));
				this.invalidate();
			}
			break;

		case MotionEvent.ACTION_CANCEL:
		case MotionEvent.ACTION_UP:
			// 释放时颜色还原
			hex.setColor(bgColor);
			this.invalidate();
			break;
		}
		return super.onTouchEvent(event);
	}

	/**
	 * 设置背景颜色，已被重载
	 * 
	 * @param color
	 */
	@Override
	public final void setBackgroundColor(int color) {
		setColor(color);
		// stepSetBackgroundColor(color);
	}

	private final void setColor(int color) {
		this.bgColor = color;
		this.hex.setColor(bgColor);
		this.invalidate();
	}

	public final void stepSetBackgroundColor(int color) {
		this.stepSetBackgroundColor(bgColor, color);
	}

	private final void stepSetBackgroundColor(final int sColor, final int dColor) {
		final int step = 100;
		final int inteval = 10;
		new Thread(new Runnable() {

			@Override
			public void run() {
				for (int i = 0; i <= step; ++i) {
					int color = ColorUtils
							.colorMix(sColor, dColor, step - i, i);
					h.obtainMessage(0, color, color).sendToTarget();
					try {
						Thread.sleep(inteval);
					} catch (Exception e) {
					}
				}
			}
		}).start();
	}

	private Handler h = new Handler(Looper.getMainLooper(),
			new Handler.Callback() {
				@Override
				public boolean handleMessage(Message msg) {
					if (msg.what == 0) {
						setColor(msg.arg1);
					}
					return true;
				}
			});

	/**
	 * 设置六边形为横向
	 */
	public final void setHorizontal() {
		this.horizontal = true;
		this.invalidate();
	}

	/**
	 * 设置六边形为纵向
	 */
	public final void setVertical() {
		this.horizontal = false;
		this.invalidate();
	}

	/**
	 * 设置文本
	 * 
	 * @param text
	 *            资源id
	 */
	public final void setText(int text) {
		tx.setText(text);
		this.invalidate();
	}

	/**
	 * 设置文本
	 * 
	 * @param text
	 */
	public final void setText(String text) {
		tx.setText(text);
		this.invalidate();
	}

	/**
	 * 设置文本尺寸
	 * 
	 * @param textSize
	 */
	public final void setTextSize(float textSize) {
		tx.setSize(textSize);
		this.invalidate();
	}

	/**
	 * 获取文本尺寸
	 * 
	 * @return
	 */
	public final float getTextSize() {
		return tx.getSize();
	}

	/**
	 * 设置文本尺寸,以sp为单位
	 * 
	 * @param textSizeSp
	 */
	public final void setTextSizeSp(float textSizeSp) {
		tx.setSizeSp(textSizeSp);
		this.invalidate();
	}

	/**
	 * 设置文本尺寸,占控件高度的百分比
	 * 
	 * @param textSizePer
	 */
	public final void setTextSizePercent(float textSizePer) {
		if (textSizePer <= 0 || textSizePer >= 1)
			return;
		this.textSizePercent = textSizePer;
		// 如果文本尺寸已被初始化,直接在此处设置尺寸,否则等待initSize函数设置
		if (this.isSizeInitialized()) {
			this.setTextSize(getHeight() * textSizePer);
		}
	}

	/**
	 * 设置文本颜色
	 * 
	 * @param textColor
	 */
	public final void setTextColor(int textColor) {
		tx.setTextColor(textColor);
		this.invalidate();
	}

	/**
	 * 是否显示文本
	 * 
	 * @return
	 */
	public final boolean isTextEnable() {
		return textEnable;
	}

	/**
	 * 设置是否显示文本
	 * 
	 * @param textEnable
	 */
	public final void setTextEnable(boolean textEnable) {
		this.textEnable = textEnable;
		this.invalidate();
	}

	/**
	 * 判断尺寸是否已被初始化
	 * 
	 * @return
	 */
	public final boolean isSizeInitialized() {
		return sizeInitialized;
	}
}
