package com.jzj.alarm;

import com.jzj.util.painter.ImagePainter;
import com.jzj.util.painter.AlignTool;
import com.jzj.util.painter.AlignTool.Direction;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.util.AttributeSet;

/**
 * 横向六边形View 用于显示六边形Option
 * 
 * @author jzj
 */
public class HexView extends HexViewSuper {

	public static enum Position {
		FIRST, MEDIUM, LAST
	}

	private Position posStyle = Position.MEDIUM; // View的排序位置：第一个，中间，最后一个
	private boolean isLeft = false; // View的左右位置：左，右
	private boolean iconEnable; // 显示图标(图标在文字左方，和文字等高)
	private float iconSizePercent; // 图标边长占控件高度的百分比,默认和文字等高

	protected ImagePainter img;

	/**
	 * 从程序代码实例化
	 * 
	 * @param context
	 */
	public HexView(Context context) {
		this(context, null);
	}

	/**
	 * 从XML文件实例化
	 * 
	 * @param context
	 * @param attrs
	 */
	public HexView(Context context, AttributeSet attrs) {
		super(context, attrs);
		img = new ImagePainter(context);

		int icon = 0;
		boolean iconEnable = false;
		float iconSizePercent = 0.15f;

		TypedArray appearance = context.obtainStyledAttributes(attrs,
				R.styleable.HexView);

		if (appearance != null) {
			int n = appearance.getIndexCount();
			for (int i = 0; i < n; i++) {
				int attr = appearance.getIndex(i);

				switch (attr) {
				case R.styleable.HexView_icon:
					icon = appearance.getResourceId(attr, icon);
					break;
				case R.styleable.HexView_icon_enable:
					iconEnable = appearance.getBoolean(attr, iconEnable);
					break;
				case R.styleable.HexView_icon_size_percent:
					iconSizePercent = appearance.getFraction(attr, 1, 1,
							iconSizePercent);
					break;
				}
			}

			appearance.recycle();
		}

		this.setIcon(icon);
		this.setIconEnable(iconEnable);
		this.setIconSizePercent(iconSizePercent);
	}

	@Override
	protected void initSize(int width, int height) {
		super.initSize(width, height);
		if (this.iconSizePercent > 0) {
			this.setIconSize(iconSizePercent * height);
		} else {
			this.setIconSize(this.getTextSize());
		}
	}

	@Override
	protected void drawElements(Canvas canvas) {
		final float cx = getTextCX();
		final float cy = getTextCY();
		if (isTextEnable()) {
			tx.setAlign(AlignTool.ALIGN_CENTER, cx, cy);
			if (isIconEnable()) {
				float offset = img.getWidth() * 0.2f;
				tx.move(Direction.RIGHT, (img.getWidth() + offset) * 0.5f);
				img.setAlign(AlignTool.ALIGN_CENTER_VERTICAL
						| AlignTool.ALIGN_TO_LEFT_OF, tx);
				img.move(Direction.LEFT, offset);
			}
		} else if (isIconEnable()) {
			img.setAlign(AlignTool.ALIGN_CENTER, cx, cy);
		}
		if (isTextEnable())
			tx.draw(canvas);
		if (isIconEnable())
			img.draw(canvas);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
	}

	/**
	 * 获取合适的x轴文字显示中心值
	 * 
	 * @return
	 */
	private final float getTextCX() {
		return getWidth() / 2;
		// if (isLeft())
		// return getWidth() * 17 / 32;
		// else
		// return getWidth() * 15 / 32;
	}

	/**
	 * 获取合适的y轴文字显示中心值
	 * 
	 * @return
	 */
	private final float getTextCY() {
		switch (posStyle) {
		case FIRST:
			return getHeight() * 3 / 4;
		case LAST:
			return getHeight() / 4;
		case MEDIUM:
		default:
			return getHeight() / 2;
		}
	}

	/**
	 * 设置位置风格：最上方，中间，最下方
	 * 
	 * @param pos
	 */
	public final void setPosStyle(Position pos) {
		this.posStyle = pos;
		this.invalidate();
	}

	/**
	 * 设置左右位置：左，右
	 * 
	 * @param isLeft
	 */
	public final void setLeft(boolean isLeft) {
		this.isLeft = isLeft;
		this.invalidate();
	}

	public final boolean isLeft() {
		return isLeft;
	}

	/**
	 * 设置图标资源
	 * 
	 * @param icon
	 */
	public final void setIcon(int icon) {
		this.img.setImage(icon);
		this.invalidate();
	}

	/**
	 * 设置图标尺寸(边长)
	 * 
	 * @param size
	 */
	public final void setIconSize(float size) {
		img.setHeight(size * 0.8f);
		img.setWidth(size * 0.8f);
		this.invalidate();
	}

	/**
	 * 设置图标尺寸,占控件高度的百分比
	 * 
	 * @param iconSizePer
	 */
	public final void setIconSizePercent(float iconSizePer) {
		if (iconSizePer <= 0 || iconSizePer >= 1)
			return;
		this.iconSizePercent = iconSizePer;
		// 如果尺寸已被初始化,直接在此处设置尺寸,否则等待initSize函数设置
		if (this.isSizeInitialized()) {
			this.setIconSize(getHeight() * iconSizePer);
		}
	}

	/**
	 * 获取图标尺寸占控件高度的百分比
	 * 
	 * @return
	 */
	public final float getIconSizePercent() {
		return iconSizePercent;
	}

	/**
	 * 设置是否显示图标
	 * 
	 * @param iconEnable
	 */
	public final void setIconEnable(boolean iconEnable) {
		this.iconEnable = iconEnable;
		this.invalidate();
	}

	/**
	 * 是否显示图标
	 * 
	 * @return
	 */
	public final boolean isIconEnable() {
		return iconEnable;
	}
}
