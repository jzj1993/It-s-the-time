package com.jzj.util.painter;

/**
 * 控件尺寸控制 位置对齐 辅助设置类(Text Image等,通过继承方式使用此类)
 * 
 * @author jzj
 * @since 2014-09-24
 */
public class AlignTool {

	/** 与另一个对象横向中间对齐 **/
	public static final int ALIGN_CENTER_HORIZONTAL = 0x00;
	/** 与另一个对象左对齐 **/
	public static final int ALIGN_LEFT = 0x01;
	/** 与另一个对象右对齐 **/
	public static final int ALIGN_RIGHT = 0x02;
	/** 对齐到另一个对象的左侧 **/
	public static final int ALIGN_TO_LEFT_OF = 0x04;
	/** 对齐到另一个对象的右侧 **/
	public static final int ALIGN_TO_RIGHT_OF = 0x08;

	/** 与另一个对象纵向中间对齐 **/
	public static final int ALIGN_CENTER_VERTICAL = 0x00;
	/** 与另一个对象底部对齐 **/
	public static final int ALIGN_BOTTOM = 0x10;
	/** 与另一个对象顶部对齐 **/
	public static final int ALIGN_TOP = 0x20;
	/** 对齐到另一个对象的底部 **/
	public static final int ALIGN_TO_BOTTOM_OF = 0x40;
	/** 对齐到另一个对象的顶部 **/
	public static final int ALIGN_TO_TOP_OF = 0x80;

	/** 对齐到另一个对象中心 **/
	public static final int ALIGN_CENTER = ALIGN_CENTER_HORIZONTAL
			| ALIGN_CENTER_VERTICAL;

	protected CoordinateCalculator coor;

	/**
	 * 方向，用于微调坐标
	 * 
	 * @author jzj
	 */
	public static enum Direction {
		UP, DOWN, LEFT, RIGHT
	}

	public AlignTool() {
		coor = new CoordinateCalculator();
	}

	/**
	 * 获取坐标
	 * 
	 * @return
	 */
	// public CoordinateCalculator getCoordinate() {
	// return coor;
	// }

	/**
	 * 设置高度
	 * 
	 * @param height
	 */
	public void setHeight(float height) {
		coor.setHeight(height);
	}

	/**
	 * 获取高度
	 * 
	 * @return
	 */
	public float getHeight() {
		return coor.getHeight();
	}

	/**
	 * 设置宽度
	 * 
	 * @param height
	 */
	public void setWidth(float width) {
		coor.setWidth(width);
	}

	/**
	 * 获取宽度
	 * 
	 * @return
	 */
	public float getWidth() {
		return coor.getWidth();
	}

	/**
	 * 设置控件位置(与给定的坐标点对齐)
	 * 
	 * @param align
	 * @param x
	 * @param y
	 */
	public void setAlign(int align, float x, float y) {

		final int alignx = align & 0x0F;
		final int aligny = align & 0xF0;
		switch (alignx) {
		case ALIGN_LEFT: // 左对齐
		case ALIGN_TO_RIGHT_OF: // 对齐到另一个对象的右侧
			coor.setxLeft(x);
			break;
		case ALIGN_RIGHT:// 右对齐
		case ALIGN_TO_LEFT_OF:// 对齐到另一个对象的左侧
			coor.setxRight(x);
			break;
		case ALIGN_CENTER_HORIZONTAL:// 横向中间对齐
		default:
			coor.setxCenter(x);
			break;
		}
		switch (aligny) {
		case ALIGN_BOTTOM:// 底部对齐
		case ALIGN_TO_TOP_OF:// 对齐到另一个对象的顶部
			coor.setyBottom(y);
			break;
		case ALIGN_TOP:// 顶部对齐
		case ALIGN_TO_BOTTOM_OF:// 对齐到另一个对象的底部
			coor.setyTop(y);
			break;
		case ALIGN_CENTER_VERTICAL:// 纵向中间对齐
		default:
			coor.setyCenter(y);
			break;
		}
	}

	/**
	 * 设置本对象与另一个对象的对齐方式
	 * 
	 * @param align
	 * @param dst
	 */
	public void setAlign(int align, AlignTool dst) {
		final int alignx = align & 0x0F;
		final int aligny = align & 0xF0;
		final CoordinateCalculator coor2 = dst.coor;
		switch (alignx) {
		case ALIGN_LEFT:// 左对齐
			coor.setxLeft(coor2.getxLeft());
			break;
		case ALIGN_RIGHT:// 右对齐
			coor.setxRight(coor2.getxRight());
			break;
		case ALIGN_TO_LEFT_OF:// 对齐到另一个对象的左侧
			coor.setxRight(coor2.getxLeft());
			break;
		case ALIGN_TO_RIGHT_OF:// 对齐到另一个对象的右侧
			coor.setxLeft(coor2.getxRight());
			break;
		case ALIGN_CENTER_HORIZONTAL:// 横向中间对齐
		default:
			coor.setxCenter(coor2.getxCenter());
			break;
		}
		switch (aligny) {
		case ALIGN_BOTTOM:// 底部对齐
			coor.setyBottom(coor2.getyBottom());
			break;
		case ALIGN_TOP:// 顶部对齐
			coor.setyTop(coor2.getyTop());
			break;
		case ALIGN_TO_BOTTOM_OF:// 对齐到另一个对象的底部
			coor.setyTop(coor2.getyBottom());
			break;
		case ALIGN_TO_TOP_OF:// 对齐到另一个对象的顶部
			coor.setyBottom(coor2.getyTop());
			break;
		case ALIGN_CENTER_VERTICAL:// 纵向中间对齐
		default:
			coor.setyCenter(coor2.getyCenter());
			break;
		}
	}

	/**
	 * 坐标位置微调(移动)
	 * 
	 * @param d
	 * @param px
	 */
	public void move(Direction d, float px) {
		switch (d) {
		case LEFT:
			coor.setxCenter(coor.getxCenter() - px);
			break;
		case RIGHT:
			coor.setxCenter(coor.getxCenter() + px);
			break;
		case UP:
			coor.setyCenter(coor.getyCenter() - px);
			break;
		case DOWN:
			coor.setyCenter(coor.getyCenter() + px);
			break;
		}
	}

	/**
	 * 获取坐标
	 * 
	 * @return
	 */
	// public CoordinateCalculator getCoordinate() {
	// return coor;
	// }

}
