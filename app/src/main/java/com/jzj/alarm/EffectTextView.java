package com.jzj.alarm;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * 特效TextView
 * 
 * @author jzj
 * @since 2014-08-22
 */
public class EffectTextView extends TextView {

	public static enum Effect {
		/**
		 * 跑马灯效果
		 */
		MARQUEE,
		/**
		 * 多行超长自动省略效果(需要限制TextView宽度和行数/高度)
		 */
		MULTI_LINE_ELLIPSIS,
		/**
		 * 单行超长自动省略效果(需要限制TextView宽度)
		 */
		SINGLE_LINE_ELLIPSIS,
	}

	private boolean marquee = false;

	public EffectTextView(Context context) {
		super(context);
	}

	public EffectTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public EffectTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	/**
	 * 设置特效
	 */
	public void setEffect(Effect e) {
		this.marquee = false;
		switch (e) {
		case MARQUEE:
			this.marquee = true;
			this.setSingleLine(true);
			this.setEllipsize(TextUtils.TruncateAt.MARQUEE);
			this.setMarqueeRepeatLimit(-1);
			break;
		case MULTI_LINE_ELLIPSIS:
			this.setSingleLine(false);
			this.setEllipsize(TextUtils.TruncateAt.END);
			break;
		case SINGLE_LINE_ELLIPSIS:
			this.setSingleLine(true);
			this.setEllipsize(TextUtils.TruncateAt.END);
			break;
		}
	}

	@Override
	public boolean isFocused() {
		if (this.marquee)
			return true;
		else
			return super.isFocused();
	}
}
