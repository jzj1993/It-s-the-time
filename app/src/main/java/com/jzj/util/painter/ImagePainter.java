package com.jzj.util.painter;

import com.jzj.util.Debug;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;

/**
 * 图形绘制辅助类
 * 
 * @author jzj
 * @since 2014-09-24
 */
public class ImagePainter extends AlignTool {

	private Drawable drawable;
	private Context context;
	private int image = 0;
	private int alpha = 255;

	public ImagePainter(Context context) {
		this.context = context;
		// coor = new CoordinateCalculator();
	}

	public ImagePainter(Context context, int image) {
		this(context);
		setImage(image);
	}

	public final void setImage(int image) {
		this.image = image;
	}

	public final int getImage() {
		return image;
	}

	public final void setAlpha(int alpha) {
		this.alpha = alpha;
	}

	public final int getAlpha() {
		return alpha;
	}

	/**
	 * 绘制图形
	 * 
	 * @param canvas
	 */
	public final void draw(Canvas canvas) {
		if (image == 0)
			return;
		try {
			drawable = context.getResources().getDrawable(image);
		} catch (Resources.NotFoundException e) {
			if (Debug.DBG)
				e.printStackTrace();
		}
		if (drawable != null) {
			drawable.setAlpha(alpha);
			// 限定图像绘制位置（矩形区域）
			drawable.setBounds((int) (coor.getxLeft() + 0.5f),
					(int) (coor.getyTop() + 0.5f),
					(int) (coor.getxRight() + 0.5f),
					(int) (coor.getyBottom() + 0.5f));
			drawable.draw(canvas);
		}
	}
}
