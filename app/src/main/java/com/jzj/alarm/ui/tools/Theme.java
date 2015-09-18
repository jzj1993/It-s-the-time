package com.jzj.alarm.ui.tools;

import android.content.Context;

import com.jzj.alarm.R;
import com.jzj.alarm.core.SettingMgr;

/**
 * UI配色方案管理
 *
 * @author jzj
 */
public class Theme {

	private static final int THEME_RANDOM = -1;
	private static final int themeCount;
	static {
		themeCount = ThemeLoader.getThemeCount();
	}

	private static Theme theme;

	private boolean random = false; // 是否为随机主题
	private int themeId = 0; // 当前主题
	private int colors[]; // 当前主题的所有颜色数组
	private int colorsCount; // 当前主题的颜色数目
	private int startPos; // 当前主题的起始颜色位置

	private Context context;
	private SettingMgr set;
	private ThemeLoader loader;

	private Theme(Context context) {
		this.context = context;
		this.set = SettingMgr.getInstance(context);
		this.loader = new ThemeLoader(context);
		this.refresh();
	}

	public static Theme getInstance(Context context) {
		if (theme == null)
			theme = new Theme(context);
		return theme;
	}

	/**
	 * 刷新颜色设置(根据SharedPreference保存的值,
	 * 初始化random,themeId,colors,colorsCount,startPos)
	 */
	public final void refresh() {
		int setThemeId = set.getTheme();
		random = (setThemeId == THEME_RANDOM);
		if (random) {
			themeId = (int) (Math.random() * themeCount);
		} else {
			themeId = setThemeId;
		}
		colors = loader.getColors(themeId);
		if (colors != null)
			colorsCount = colors.length;
		startPos = (int) (Math.random() * colorsCount);
	}

	/**
	 * 获取颜色
	 *
	 * @param pos
	 *            View所在位置
	 * @return View的颜色
	 */
	public final int getColor(int pos) {
		if (colors == null)
			return 0;
		pos += startPos;
		while (pos < 0) { // 负数转换为正数再取模
			pos += colorsCount;
		}
		return colors[pos % colorsCount];
	}

	/**
	 * 获取图片
	 *
	 * @return
	 */
	public final int getImage() {
		return loader.getImage(themeId);
	}

	/**
	 * 获取当前主题名称
	 *
	 * @return 出错返回null
	 */
	public final String getCurrentName() {
		if (random)
			return context.getString(R.string.theme_random); // 随机
		else
			return loader.getName(themeId); // 其他
	}

	/**
	 * 切换到下一个颜色主题
	 */
	public final void setNextTheme() {
		setTheme(random ? 0 : themeId + 1);
	}

	/**
	 * 切换到一个随机主题
	 */
	public final void setRandomTheme() {
		setTheme((int) (Math.random() * themeCount));
	}

	/**
	 * 设置颜色主题(保存到SharedPreference并刷新数据)
	 *
	 * @param themeId
	 *            如果超出范围(负值或超出最大值)则设为随机
	 */
	private final void setTheme(int themeId) {
		if (themeId < 0 || themeId >= themeCount) {
			set.setTheme(THEME_RANDOM);
		} else {
			set.setTheme(themeId);
		}
		this.refresh();
	}

	/**
	 * 获取随机的背景颜色
	 *
	 * @param context
	 * @return
	 */
	public static final int getRandomBgColor(Context context) {
		int colors[] = context.getResources().getIntArray(
				R.array.colors_random_bg);
		return colors[(int) (Math.random() * colors.length)];
	}

	// public static int getRandomBgColor() {
	// // 利用HSV颜色空间随机产生色彩，各种颜色分布较均匀
	// // 再利用Lab颜色空间对色彩亮度进行调整( L <= 60; a,b = -128 ~ +127 )
	// // 然后通过在RGB颜色空间的颜色对比度计算，保证产生的颜色和白色对比较强以免看不清Logo
	// while (true) {
	// float[] hsv = { (float) (Math.random() * 360.0), 80, 60 };
	// int bgColor = ColorUtils.HSVToColor(hsv);
	// double[] lab = ColorUtils.color2lab(bgColor);
	// if (lab[0] > 60) {
	// lab[0] = 60;
	// bgColor = ColorUtils.lab2Color(lab);
	// }
	// if (ColorUtils.calcContrast(bgColor, ColorUtils.WHITE) > 0.5f) {
	// return bgColor;
	// }
	// }
	// }
}
