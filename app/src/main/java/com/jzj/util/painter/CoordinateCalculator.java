package com.jzj.util.painter;

/**
 * 计算坐标辅助类
 * <p>
 * 使用规则:
 * <p>
 * 对于任何一个轴,设置尺寸和一个位置坐标即可完成初始化,
 * <p>
 * 之后修改其中任何一个值,其他几个值会自动被计算
 * <p>
 * 如:对于x轴方向
 * <p>
 * 1.设置尺寸,即宽度
 * <p>
 * 2.再设置位置,即xLeft/xCenter/xRight中的任意一个,即完成初始化
 * <p>
 * 3.之后修改其中任何一个值,其他几个值都会自动计算,可以随时获取
 * <p>
 * 
 * @author jzj
 */
public class CoordinateCalculator {

	private static enum Init {
		NONE, LEFT_TOP, CENTER, RIGHT_BOTTOM
	}

	private Init xInit = Init.NONE;
	private Init yInit = Init.NONE;

	// x, y的坐标，尺寸
	private float w = 0, w2 = 0, cx = 0, l = 0, r = 0;
	private float h = 0, h2 = 0, cy = 0, t = 0, b = 0;

	public final void setWidth(float w) {
		this.w = w;
		w2 = w * 0.5f;
		// 重新计算坐标
		switch (xInit) {
		case NONE: // 没有初始化位置
			break;
		case CENTER: // 已设置Center初始化位置
			this.setyCenter(cx);
			break;
		case LEFT_TOP: // 已设置Left/Top初始化位置
			this.setxLeft(l);
			break;
		case RIGHT_BOTTOM: // 已设置Right/Buttom初始化位置
			this.setxRight(r);
			break;
		}
	}

	public final float getWidth() {
		return w;
	}

	public final void setHeight(float h) {
		this.h = h;
		h2 = h * 0.5f;
		// 重新计算坐标
		switch (yInit) {
		case NONE: // 没有初始化位置
			break;
		case CENTER: // 已设置Center初始化位置
			this.setyCenter(cy);
			break;
		case LEFT_TOP: // 已设置Left/Top初始化位置
			this.setyTop(t);
			break;
		case RIGHT_BOTTOM: // 已设置Right/Buttom初始化位置
			this.setyBottom(b);
			break;
		}
	}

	public final float getHeight() {
		return h;
	}

	public final void setxCenter(float cx) {
		if (xInit == Init.NONE)
			xInit = Init.CENTER;
		this.cx = cx;
		l = cx - w2;
		r = cx + w2;
	}

	public final float getxCenter() {
		return cx;
	}

	public final void setxLeft(float l) {
		if (xInit == Init.NONE)
			xInit = Init.LEFT_TOP;
		this.l = l;
		cx = l + w2;
		r = cx + w2;
	}

	public final float getxLeft() {
		return l;
	}

	public final void setxRight(float r) {
		if (xInit == Init.NONE)
			xInit = Init.RIGHT_BOTTOM;
		this.r = r;
		cx = r - w2;
		l = cx - w2;
	}

	public final float getxRight() {
		return r;
	}

	public final void setyCenter(float cy) {
		if (yInit == Init.NONE)
			yInit = Init.CENTER;
		this.cy = cy;
		t = cy - h2;
		b = cy + h2;
	}

	public final float getyCenter() {
		return cy;
	}

	public final void setyTop(float t) {
		if (yInit == Init.NONE)
			yInit = Init.LEFT_TOP;
		this.t = t;
		cy = t + h2;
		b = cy + h2;
	}

	public final float getyTop() {
		return t;
	}

	public final void setyBottom(float b) {
		if (yInit == Init.NONE)
			yInit = Init.RIGHT_BOTTOM;
		this.b = b;
		cy = b - h2;
		t = cy - h2;
	}

	public final float getyBottom() {
		return b;
	}
}
