package com.jzj.alarm;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.ImageView;

/**
 * 自动缩放的ImageView
 * 原理：使用ScaleType.CENTER_CROP，可以保证缩放后图片宽高大于或等于控件尺寸
 * 设置控件宽为已知值，高为0或WRAP_CONTENT，然后动态改变控件高度，使得图片显示完整
 *
 * @author jzj
 * @since 2014-08-24
 */
public class ScaleImageView extends ImageView {

	private boolean resize = false;
	private boolean sizeInit = false;
	private ViewGroup.LayoutParams params;

	public ScaleImageView(Context context) {
		super(context);
		this.setScaleType(ScaleType.CENTER_CROP);
	}

	public ScaleImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.setScaleType(ScaleType.CENTER_CROP);
	}

	public ScaleImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.setScaleType(ScaleType.CENTER_CROP);
	}

	/**
	 * 设置LayoutParams实例，根据Layout布局中ScaleImageView控件的父容器来设置。
	 * 如: new RelativeLayout.LayoutParams(0,0)
	 *
	 * @param params
	 */
	public void initLayoutParamas(ViewGroup.LayoutParams params) {
		this.params = params;
	}

	/**
	 * 设置图片并刷新尺寸
	 * 需要先调用{@link #initLayoutParamas(ViewGroup.LayoutParams)} 设置LayoutParams实例
	 *
	 * @param resId
	 */
	public void setImageResourceAndResize(int resId) {
		if (sizeInit) {
			super.setImageResource(resId);
			resize();
		} else {
			resize = true;
			super.setImageResource(resId);
		}
	}

	/**
	 * 设置图片并刷新尺寸
	 * 需要先调用{@link #initLayoutParamas(ViewGroup.LayoutParams)} 设置LayoutParams实例
	 *
	 * @param drawable
	 */
	public void setImageDrawableAndResize(Drawable drawable) {
		if (sizeInit) {
			super.setImageDrawable(drawable);
			resize();
		} else {
			resize = true;
			super.setImageDrawable(drawable);
		}
	}

	@Override
	public void onSizeChanged(int w, int h, int oldw, int oldh) {
		if (resize) {
			resize = false;
			resize(w, h);
		}
		super.onSizeChanged(w, h, oldw, oldh);
		sizeInit = true;
	}

	protected final void resize() {
		resize(this.getWidth(), this.getHeight());
	}

	protected final void resize(int w, int h) {
		if (params == null)
			return;
		// Log.d("TAG_J", "ScaleImageView: resize");
		Drawable drawable = this.getDrawable();
		int height = (int) ((float) w / drawable.getMinimumWidth() * drawable
				.getMinimumHeight());
		params.height = height;
		params.width = ViewGroup.LayoutParams.MATCH_PARENT;
		this.setLayoutParams(params);
	}
}
