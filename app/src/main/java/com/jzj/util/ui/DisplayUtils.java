package com.jzj.util.ui;

import android.content.Context;
import android.util.DisplayMetrics;

/**
 * 显示相关工具集(尺寸单位转换等)
 * 
 * @since 2014-07-31
 * @author jzj
 */
public class DisplayUtils {

	private static DisplayUtils disp;

	private DisplayMetrics metrics;

	/**
	 * 安卓屏幕尺寸相关知识
	 * <p>
	 * 1.px = Pixels,像素值
	 * <p>
	 * 2.dp = dip = device independent pixel,设备独立像素(安卓专用虚拟像素单位)
	 * <p>
	 * 3.density,屏幕密度
	 * <p>
	 * (1)近似换算关系 px = dip*density, dip = px/density
	 * <p>
	 * (2)相同像素值(px)的控件,在不同密度屏幕上,显示效果差异较大;因此控件尺寸通常使用dip定义
	 * <p>
	 * 4.densityDpi,每英寸多少个px像素点
	 * <p>
	 * (1)该值为近似值。如华为荣耀3C,1280*720像素,对角线像素约1468.6,对角线实际尺寸为5寸,实际DPI约294,
	 * 而实测densityDpi取值为320
	 * <p>
	 * (2)大部分手机屏幕使用正方形的像素点,横向和纵向DPI相同
	 * <p>
	 * (3)根据densityDpi的大小决定调用哪个文件夹下的图片资源:
	 * ldpi是120dpi，mdpi是160dpi，hdpi是240dpi，xhdpi是320dpi
	 * <p>
	 * (4)换算关系 density = densityDpi/160,对于160 densityDpi屏幕,density = 1,此时px =
	 * dip
	 * <p>
	 * 5.分辨率:每英寸上的px像素点数,单位为DPI(但经常不准确的被用来指代 图片的像素点总数)
	 * <p>
	 * 6.sp = scaled pixels,用于字体的大小
	 * <p>
	 * 7.scaledDensity 文字缩放系数
	 * <p>
	 * (1)换算关系： sp = px/scaledDensity, px = sp*scaledDensity
	 * <p>
	 */
	private DisplayUtils(Context context) {
		metrics = context.getResources().getDisplayMetrics();
	}

	public static DisplayUtils getInstance(Context context) {
		if (disp == null)
			disp = new DisplayUtils(context);
		return disp;
	}

	/**
	 * 获取屏幕宽度px
	 * 
	 * @return
	 */
	public final int getScreenWidth() {
		return metrics.widthPixels;
	}

	/**
	 * 获取屏幕高度px
	 * 
	 * @return
	 */
	public final int getScreenHeight() {
		return metrics.heightPixels;
	}

	/**
	 * 将px值转换为dip或dp值
	 * 
	 * @param pxValue
	 * @return
	 */
	public final float px2dip(float pxValue) {
		return pxValue / metrics.density + 0.5f;
	}

	/**
	 * 将dip或dp值转换为px值
	 * 
	 * @param dipValue
	 * @return
	 */
	public final float dip2px(float dipValue) {
		return dipValue * metrics.density + 0.5f;
	}

	/**
	 * 将px值转换为sp值
	 * 
	 * @param pxValue
	 * @return
	 */
	public final float px2sp(float pxValue) {
		return pxValue / metrics.scaledDensity + 0.5f;
	}

	/**
	 * 将sp值转换为px值
	 * 
	 * @param spValue
	 * @return
	 */
	public final float sp2px(float spValue) {
		return spValue * metrics.scaledDensity + 0.5f;
	}
}
