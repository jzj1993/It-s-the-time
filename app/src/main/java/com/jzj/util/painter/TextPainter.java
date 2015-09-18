package com.jzj.util.painter;

import com.jzj.util.ui.DisplayUtils;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.FontMetrics;
import android.text.TextPaint;
import android.text.TextUtils;

/**
 * 文本绘制辅助类
 * 
 * @author jzj
 */
public class TextPainter extends AlignTool {

	private Context context;
	private TextPaint tx;
	private String text;

	public TextPainter(Context context) {
		this.context = context;
		tx = new TextPaint(Paint.ANTI_ALIAS_FLAG);
		tx.setTextAlign(Align.CENTER);
	}

	public TextPainter(Context context, String text) {
		this(context);
		this.setText(text);
	}

	/**
	 * 绘制单行文本,根据坐标设置的位置绘制
	 * 
	 * @param canvas
	 */
	public final void draw(Canvas canvas) {
		if (!TextUtils.isEmpty(text)) {
			FontMetrics fm = tx.getFontMetrics();
			float x = coor.getxCenter();
			float y = coor.getyCenter() - fm.descent + (fm.descent - fm.ascent)
					/ 2;
			canvas.drawText(text, x, y, tx);
		}
	}

	/**
	 * 在View中心绘制文本
	 * 
	 * @param canvas
	 * @param parentWidth
	 * @param parentHeight
	 */
	public final void drawAtCenter(Canvas canvas, float parentWidth,
			float parentHeight) {
		this.setAlign(ALIGN_CENTER, parentWidth / 2f, parentHeight / 2f);
		this.draw(canvas);
	}

	/**
	 * 设置文本
	 * 
	 * @param text
	 *            资源id
	 */
	public final void setText(int text) {
		this.setText(context.getString(text));
	}

	/**
	 * 设置文本
	 * 
	 * @param text
	 */
	public final void setText(String text) {
		this.text = text;
		coor.setHeight(this.getHeight()); // 重新设置高度
		coor.setWidth(this.getWidth()); // 重新设置宽度
	}

	/**
	 * 设置文本颜色
	 * 
	 * @param color
	 */
	public final void setTextColor(int color) {
		tx.setColor(color);
	}

	/**
	 * 设置文本尺寸
	 * 
	 * @param size
	 */
	public final void setSize(float size) {
		tx.setTextSize(size);
		coor.setHeight(getHeight()); // 重新设置高度
		coor.setWidth(getWidth()); // 重新设置宽度
	}

	/**
	 * 设置文本尺寸，以SP为单位
	 * 
	 * @param size
	 *            单位为sp
	 */
	public final void setSizeSp(float size) {
		this.setSize(DisplayUtils.getInstance(context).sp2px(size));
	}

	/**
	 * 获取文本尺寸
	 * 
	 * @return
	 */
	public final float getSize() {
		return tx.getTextSize();
	}

	/**
	 * 设置文本宽度(根据参数自动设置文本尺寸)
	 */
	@Override
	public final void setWidth(float width) {
		// 按比例设置文本尺寸: newSize/curSize = width/curWidth
		if (width > 0)
			setSize(getSize() * width / getWidth());
	}

	/**
	 * 获取文本宽度
	 * 
	 * @return
	 */
	@Override
	public final float getWidth() {
		if (TextUtils.isEmpty(text))
			return 0;
		else
			return tx.measureText(text);
	}

	/**
	 * 设置文本高度(根据参数自动设置文本尺寸)
	 */
	@Override
	public final void setHeight(float height) {
		this.setSize(height);
	}

	/**
	 * 获取文本高度
	 * 
	 * @return
	 */
	@Override
	public final float getHeight() {
		return tx.getTextSize();
		// return (tx.getFontMetrics().descent - tx.getFontMetrics().top + 2);
	}

	// /**
	// * 在View中心绘制文本 注：仅此函数支持字符串中的换行符
	// *
	// * @param canvas
	// * @param parentWidth
	// * @param parentHeight
	// */
	// public final void drawAtCenter(Canvas canvas, float parentWidth,
	// float parentHeight) {
	//
	// if (!TextUtils.isEmpty(text)) {
	// // 居中绘制Text
	// StaticLayout layout = new StaticLayout(text, tx, 240,
	// Alignment.ALIGN_NORMAL, 1.0F, 0.0F, true);
	//
	// final float offsetX = parentWidth * 0.5f;
	// final float offsetY = (parentHeight - layout.getHeight()) * 0.5f;
	//
	// canvas.translate(offsetX, offsetY);
	// layout.draw(canvas);
	// canvas.translate(-offsetX, -offsetY); // 复位convas
	// }
	// }
}
