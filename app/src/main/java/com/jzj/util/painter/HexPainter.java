package com.jzj.util.painter;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;

/**
 * 六边形绘制辅助类
 * 
 * @author jzj
 */
public class HexPainter extends Paint {

	/**
	 * 使用默认颜色，默认透明度
	 */
	public HexPainter() {
		this(Color.BLUE, 255);
	}

	/**
	 * 使用指定颜色，默认透明度
	 * 
	 * @param color
	 */
	public HexPainter(int color) {
		this(color, 255);
	}

	/**
	 * 使用指定颜色，指定透明度
	 * 
	 * @param color
	 * @param alpha
	 */
	public HexPainter(int color, int alpha) {
		super();
		this.setAntiAlias(true);
		this.setStyle(Style.FILL);
		this.setColor(color);
		this.setAlpha(alpha);
	}

	/**
	 * 绘制横向六边形
	 * 
	 * @param canvas
	 *            Canvas对象，坐标原点在左上角
	 * @param w
	 *            六边形横向宽度(x轴)
	 * @param h
	 *            六边形纵向高度(y轴)
	 */
	public final void drawHorizontal(Canvas canvas, float w, float h) {

		Path path = new Path();
		path.moveTo(w * 0.25f, 0);
		path.lineTo(w * 0.75f, 0);
		path.lineTo(w, h * 0.5f);
		path.lineTo(w * 0.75f, h);
		path.lineTo(w * 0.25f, h);
		path.lineTo(0, h * 0.5f);
		path.close();

		canvas.drawPath(path, this);
	}

	/**
	 * 绘制纵向六边形
	 * 
	 * @param canvas
	 *            Canvas对象，坐标原点在左上角
	 * @param w
	 *            六边形横向宽度(x轴)
	 * @param h
	 *            六边形纵向高度(y轴)
	 */
	public final void drawVertical(Canvas canvas, float w, float h) {

		Path path = new Path();
		path.moveTo(w * 0.5f, 0);
		path.lineTo(w, h * 0.25f);
		path.lineTo(w, h * 0.75f);
		path.lineTo(w * 0.5f, h);
		path.lineTo(0, h * 0.75f);
		path.lineTo(0, h * 0.25f);
		path.close();

		canvas.drawPath(path, this);
	}
}
