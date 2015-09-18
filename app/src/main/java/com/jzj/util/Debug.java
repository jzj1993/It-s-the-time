package com.jzj.util;

import android.util.Log;

/**
 * 调试信息输出辅助类
 * 
 * @author jzj
 */
public class Debug {

	public static final boolean DBG = true;
	private static final String LOG_TAG = "TAG_JZJ";

	/**
	 * 输出调试信息,并显示输出log的类名
	 * 
	 * @param c
	 * @param s
	 */
	public static final void log(String className, String s) {
		Log.d(LOG_TAG, className + ": " + s);
	}

	/**
	 * 输出调试信息,并显示输出log的类名
	 * 
	 * @param c
	 * @param s
	 */
	public static final void log(Object o, String s) {
		Log.d(LOG_TAG, o.getClass().getSimpleName() + ": " + s);
	}

	/**
	 * 程序运行发生错误信息,并显示输出log的类名
	 * 
	 * @param c
	 * @param e
	 */
	public static final void error(Object o, String e) {
		Log.e(LOG_TAG, o.getClass().getSimpleName() + ": " + e);
	}
}
