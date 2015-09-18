package com.jzj.alarm.ui.tools;

import android.content.Context;
import android.content.res.Resources;

import com.jzj.alarm.R;

public class ThemeLoader {

	private static final int[] sweetCandy = {
			R.string.theme_sweet_candy,
			R.array.colors_sweet_candy,
			R.drawable.theme_sweet_candy };

	private static final int[] girlInTheRain = {
			R.string.theme_girl_in_the_rain,
			R.array.colors_girl_in_the_rain,
			R.drawable.theme_girl_in_the_rain };

	private static final int[] sunsetBloodRed = {
			R.string.theme_sunset_blood_red,
			R.array.colors_sunset_blood_red,
			R.drawable.theme_sunset_blood_red };

	private static final int[] strawberry = {
			R.string.theme_strawberry,
			R.array.colors_strawberry,
			R.drawable.theme_strawberry };

	private static final int[] sakura = {
			R.string.theme_sakura,
			R.array.colors_sakura,
			R.drawable.theme_sakura };

	private static final int[] flowers = {
			R.string.theme_flowers,
			R.array.colors_flowers,
			R.drawable.theme_flowers };

	private static final int[] mysticSea = {
			R.string.theme_mystic_sea,
			R.array.colors_mystic_sea,
			R.drawable.theme_mystic_sea };

	private static final int[] mountain = {
			R.string.theme_mountain,
			R.array.colors_mountain,
			R.drawable.theme_mountain };

	private static final int[] springInTheAir = {
			R.string.theme_spring_in_the_air,
			R.array.colors_spring_in_the_air,
			R.drawable.theme_spring_in_the_air };

	// private static final int[] firework = {
	// R.string.theme_firework,
	// R.array.colors_firework,
	// R.drawable.theme_firework };
	// private static final int[] brightMoon = {
	// R.string.theme_bright_moon,
	// R.array.colors_bright_moon,
	// R.drawable.theme_bright_moon };
	// private static final int[] mountainGreenery = {
	// R.string.theme_mountain_greenery,
	// R.array.colors_mountain_greenery,
	// R.drawable.theme_mountain_greenery };

	private static final int[][] theme = {
			girlInTheRain,
			strawberry,
			sakura,
			sweetCandy,
			sunsetBloodRed,
			flowers,
			springInTheAir,
			mountain,
			mysticSea };

	private final Resources res;

	public ThemeLoader(Context context) {
		res = context.getResources();
	}

	public static final int getThemeCount() {
		return theme.length;
	}

	public final String getName(int themeId) {
		int id = getResId(themeId, 0);
		return id == 0 ? null : res.getString(id);
	}

	public final int[] getColors(int themeId) {
		int id = getResId(themeId, 1);
		return id == 0 ? null : res.getIntArray(id);
	}

	public final int getImage(int themeId) {
		return getResId(themeId, 2);
	}

	private static final int getResId(int themeId, int n) {
		if (themeId < 0 || themeId >= theme.length)
			return 0;
		return theme[themeId][n];
	}
}
