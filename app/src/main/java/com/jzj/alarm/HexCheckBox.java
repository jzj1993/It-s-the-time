package com.jzj.alarm;

import com.jzj.util.painter.HexPainter;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.AttributeSet;

/**
 * 纵向六边形CheckBox
 * 
 * @author jzj
 */
public class HexCheckBox extends HexViewSuper {

	// 里面的小六边形占大六边形尺寸的比例
	private static final float SIZE_PERCENT = 0.8f;

	private HexPainter hexSmall;
	private boolean checked = true;

	public HexCheckBox(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.setVertical();
		hexSmall = new HexPainter(Color.WHITE, 127);

		// 从xml资源文件加载属性设置
		TypedArray attr = context.obtainStyledAttributes(attrs,
				R.styleable.HexCheckBox);
		final boolean checked = attr.getBoolean(
				R.styleable.HexCheckBox_checked, true);
		this.setChecked(checked);
		attr.recycle();
	}

	// @Override
	// protected void initSize(int width, int height) {
	// super.initSize(width, height);
	// tx.setTextSize(height * 0.3f);
	// }

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if (checked) {
			// 使用半透明白色画小六边形,表示被选中

			final float w = getWidth();
			final float h = getHeight();

			final float w1 = w * SIZE_PERCENT;
			final float h1 = h * SIZE_PERCENT;

			final float offsetX = (w - w1) * 0.5f;
			final float offsetY = (h - h1) * 0.5f;

			canvas.translate(offsetX, offsetY);
			hexSmall.drawVertical(canvas, w1, h1);
			canvas.translate(-offsetX, -offsetY); // 复位Convas
		}
	}

	/**
	 * 设置选中状态
	 * 
	 * @param checked
	 */
	public final void setChecked(boolean checked) {
		if (this.checked != checked) {
			this.checked = checked;
			this.invalidate(); // 刷新显示
		}
	}

	/**
	 * 翻转选中状态
	 */
	public final void flipChecked() {
		this.checked = !this.checked;
		this.invalidate(); // 刷新显示
	}

	/**
	 * 是否选中
	 * 
	 * @return
	 */
	public final boolean isChecked() {
		return checked;
	}
}
