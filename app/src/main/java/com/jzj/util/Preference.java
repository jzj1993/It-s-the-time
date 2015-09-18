package com.jzj.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * 操作SharedPreference的辅助类
 * <p>
 * 推荐用法示例:
 * <p>
 * public class SettingMgr extends Preference {
 * <p>
 * // 设置项键值:首次运行
 * <p>
 * private static final String KEY_FIRST_RUN = "first_run";
 * <p>
 * private static SettingMgr set;
 * <p>
 * protected SettingMgr(Context context) {
 * <p>
 * super(context);
 * <p>
 * }
 * <p>
 * public static SettingMgr getInstance(Context context) {
 * <p>
 * if(set == null) {
 * <p>
 * set = new SettingMgr(context);
 * <p>
 * }
 * <p>
 * return set;
 * <p>
 * }
 * <p>
 * public final boolean isFirstRun() {
 * <p>
 * return this.getBoolean(KEY_FIRST_RUN);
 * <p>
 * }
 * <p>
 * public final void setFirstRun(boolean b) {
 * <p>
 * this.put(KEY_FIRST_RUN, b);
 * <p>
 * }
 * <p>
 * }
 * <p>
 * 
 * @since 2014-04-26
 * @author jzj
 */
public class Preference {

	private SharedPreferences sp;
	private SharedPreferences.Editor edit;

	protected Preference(Context context) {
		sp = PreferenceManager.getDefaultSharedPreferences(context);
		edit = sp.edit();
	}

	/**
	 * 清除全部数据
	 */
	public final void clear() {
		edit.clear();
		edit.commit();
	}

	/**
	 * 插入一项设置
	 * 
	 * @param key
	 *            键值
	 * @param value
	 *            要插入的值
	 */
	public final void put(String key, boolean value) {
		edit.putBoolean(key, value);
		edit.commit();
	}

	/**
	 * 插入一项设置
	 * 
	 * @param key
	 *            键值
	 * @param value
	 *            要插入的值
	 */
	public final void put(String key, int value) {
		edit.putInt(key, value);
		edit.commit();
	}

	/**
	 * 插入一项设置
	 * 
	 * @param key
	 *            键值
	 * @param value
	 *            要插入的值
	 */
	public final void put(String key, long value) {
		edit.putLong(key, value);
		edit.commit();
	}

	/**
	 * 插入一项设置
	 * 
	 * @param key
	 *            键值
	 * @param value
	 *            要插入的值
	 */
	public final void put(String key, String value) {
		edit.putString(key, value);
		edit.commit();
	}

	/**
	 * 读取设置
	 * 
	 * @param key
	 *            键值
	 * @param def
	 *            默认值
	 * @return 出错返回false，没有该项键值返回默认值，否则返回读取的值
	 */
	public final boolean getBoolean(String key, boolean def) {
		return sp.getBoolean(key, def);
	}

	/**
	 * 读取设置
	 * 
	 * @param key
	 *            键值
	 * @param def
	 *            默认值
	 * @return 出错返回false，没有该项键值返回默认值true，否则返回读取的值
	 */
	public final boolean getBoolean(String key) {
		return getBoolean(key, true);
	}

	/**
	 * 读取设置
	 * 
	 * @param key
	 *            键值
	 * @param def
	 *            默认值
	 * @return 出错返回-1，没有该项键值返回默认值，否则返回读取的值
	 */
	public final int getInt(String key, int def) {
		return sp.getInt(key, def);
	}

	/**
	 * 读取设置
	 * 
	 * @param key
	 *            键值
	 * @param def
	 *            默认值
	 * @return 出错返回-1，没有该项键值返回默认值-1，否则返回读取的值
	 */
	public final int getInt(String key) {
		return getInt(key, -1);
	}

	/**
	 * 读取设置
	 * 
	 * @param key
	 *            键值
	 * @param def
	 *            默认值
	 * @return 出错返回-1，没有该项键值返回默认值，否则返回读取的值
	 */
	public final long getLong(String key, long def) {
		return sp.getLong(key, def);
	}

	/**
	 * 读取设置
	 * 
	 * @param key
	 *            键值
	 * @param def
	 *            默认值
	 * @return 出错返回-1，没有该项键值返回默认值-1，否则返回读取的值
	 */
	public final long getLong(String key) {
		return getLong(key, -1L);
	}

	/**
	 * 读取设置
	 * 
	 * @param key
	 *            键值
	 * @param def
	 *            默认值
	 * @return 出错返回null，没有该项键值返回默认值，否则返回读取的值
	 */
	public final String getString(String key, String def) {
		return sp.getString(key, def);
	}

	/**
	 * 读取设置
	 * 
	 * @param key
	 *            键值
	 * @param def
	 *            默认值
	 * @return 出错返回null，没有该项键值返回默认值null，否则返回读取的值
	 */
	public final String getString(String key) {
		return getString(key, null);
	}
}
