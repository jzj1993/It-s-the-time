package com.jzj.alarm.core;

import java.util.ArrayList;
import java.util.List;

import com.jzj.util.CalendarPlus;
import com.jzj.util.Debug;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * 用于从数据库存取闹铃
 * 
 * @author jzj
 */
public class AlarmAccess extends SQLiteOpenHelper {

	private static final int DATABASE_VERSION = 3;
	private static final String DATABASE_NAME = "alarm.db";
	private static final String tableAlarm = "alarm";

	private static final class K {
		static final String _ID = "id";
		static final String HOUR = "hour";
		static final String MIN = "minute";
		static final String MODE = "mode";
		static final String REPEAT = "repeat";
		static final String ON = "on_off";
		static final String NOTES = "notes";
		static final String DATE = "date";
	}

	public AlarmAccess(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		try {
			String sql = replace(
					"create table if not exists ?(? integer primary key,? integer,"
							+ "? integer,? integer,? integer,? integer,? text,? text);",
					new String[] {
							tableAlarm,
							K._ID,
							K.HOUR,
							K.MIN,
							K.MODE,
							K.REPEAT,
							K.ON,
							K.NOTES,
							K.DATE });
			db.execSQL(sql);
		} catch (Exception e) {
			if (Debug.DBG)
			e.printStackTrace();
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		try {
			String sql = replace("drop table if exists ?;", tableAlarm);
			db.execSQL(sql);
			String sql2 = replace(
					"create table if not exists ?(? integer primary key,? integer,"
							+ "? integer,? integer,? integer,? integer,? text,? text);",
					new String[] {
							tableAlarm,
							K._ID,
							K.HOUR,
							K.MIN,
							K.MODE,
							K.REPEAT,
							K.ON,
							K.NOTES,
							K.DATE });
			db.execSQL(sql2);
		} catch (Exception e) {
			if (Debug.DBG)
			e.printStackTrace();
		}
	}

	/**
	 * 读取所有闹铃,按照"小时:分钟"从小到大排序
	 * 
	 * @return 返回读取的闹铃
	 */
	public final List<AlarmItem> loadAllAlarm() {
		try {
			SQLiteDatabase db = this.getReadableDatabase();
			String sql = replace("select * from ? order by ?,? asc;",
					tableAlarm, K.HOUR, K.MIN);
			Cursor cur = db.rawQuery(sql, null);
			return createAlarmFromValue(cur, db);
		} catch (Exception e) {
			if (Debug.DBG)
				e.printStackTrace();
			return new ArrayList<AlarmItem>();
		}
	}

	/**
	 * 通过id读取闹铃
	 * 
	 * @param id
	 *            闹铃id
	 * @return 返回读取的闹铃，未找到或出错返回null
	 */
	public final AlarmItem getAlarmById(int id) {
		try {
			SQLiteDatabase db = this.getReadableDatabase();
			String sql = replace("select * from ? where ?=?;", tableAlarm,
					K._ID, String.valueOf(id));
			Cursor cur = db.rawQuery(sql, null);
			List<AlarmItem> as = createAlarmFromValue(cur, db);
			if (as.size() > 0)
				return as.get(0);
		} catch (Exception e) {
			if (Debug.DBG)
				e.printStackTrace();
		}
		return null;
	}

	/**
	 * 获取闹铃数目
	 * 
	 * @return 闹铃数目
	 */
	public final int getAlarmCnt() {
		try {
			SQLiteDatabase db = this.getReadableDatabase();
			String sql = replace("select count(*) from ?;", tableAlarm);
			Cursor cur = db.rawQuery(sql, null);
			cur.moveToFirst();
			int d = cur.getInt(0);
			cur.close();
			db.close();
			return d;
		} catch (Exception e) {
			if (Debug.DBG)
				e.printStackTrace();
		}
		return 0;
	}

	/**
	 * 添加闹铃
	 * 
	 * @param a
	 *            要添加的闹铃项 AlarmItem
	 * @return 操作成功返回true
	 */
	public final boolean addAlarm(AlarmItem a) {
		try {
			SQLiteDatabase db = this.getWritableDatabase();
			String sql = replace(
					"insert into ? values(null,#,#,#,#,#,#,#);",
					new String[] { tableAlarm },
					new Object[] {
							a.getHour(),
							a.getMinute(),
							a.getMode(),
							a.getRepeat(),
							a.isOn(),
							a.getNotes(),
							CalendarPlus.format(a.getDate(),
									CalendarPlus.PATTERN_YMD) });
			db.execSQL(sql);
			db.close();
			return true;
		} catch (Exception e) {
			if (Debug.DBG)
				e.printStackTrace();
		}
		return false;
	}

	/**
	 * 修改闹铃开关状态
	 * 
	 * @param id
	 * @param on
	 * @return
	 */
	public final boolean setAlarmOnOff(int id, boolean on) {
		try {
			SQLiteDatabase db = this.getWritableDatabase();
			String sql = replace("update ? set ?=# where ?=#;", new String[] {
					tableAlarm,
					K.ON,
					K._ID }, on, id);
			db.execSQL(sql);
			db.close();
			return true;
		} catch (Exception e) {
			if (Debug.DBG)
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 修改闹铃(修改完后闹铃id可能会发生变化)
	 * 
	 * @param id
	 * @param alarm
	 * @return
	 */
	public final boolean setAlarm(int id, AlarmItem alarm) {
		return (addAlarm(alarm) && delAlarm(id)); // 如果添加失败则不执行删除操作
	}

	/**
	 * 删除闹铃
	 * 
	 * @param id
	 *            闹铃ID
	 * @return 操作成功返回true
	 */
	public final boolean delAlarm(int id) {
		try {
			SQLiteDatabase db = this.getWritableDatabase();
			String sql = replace("delete from ? where ?=#;", new String[] {
					tableAlarm,
					K._ID }, id);
			db.execSQL(sql);
			db.close();
			return true;
		} catch (Exception e) {
			if (Debug.DBG)
				e.printStackTrace();
		}
		return false;
	}

	private final List<AlarmItem> createAlarmFromValue(Cursor cur,
			SQLiteDatabase db) {
		List<AlarmItem> ls = new ArrayList<AlarmItem>();
		while (cur.moveToNext()) {
			AlarmItem a = new AlarmItem();
			a.setId(cur.getInt(cur.getColumnIndex(K._ID)));
			a.setHour(cur.getInt(cur.getColumnIndex(K.HOUR)));
			a.setMinute(cur.getInt(cur.getColumnIndex(K.MIN)));
			a.setMode(cur.getInt(cur.getColumnIndex(K.MODE)));
			a.setRepeat(cur.getInt(cur.getColumnIndex(K.REPEAT)));
			a.setOnOff(cur.getInt(cur.getColumnIndex(K.ON)) != 0);
			a.setNotes(cur.getString(cur.getColumnIndex(K.NOTES)));
			a.setDate(CalendarPlus.parseCalendar(
					cur.getString(cur.getColumnIndex(K.DATE)),
					CalendarPlus.PATTERN_YMD));
			ls.add(a);
		}
		cur.close();
		db.close();
		return ls;
	}

	/**
	 * 将str中的指定字符逐个替换成replace
	 */
	protected final String replace(String str, char c, String... replace) {
		int i = 0;
		StringBuilder b = new StringBuilder(str);
		for (String s : replace) {
			i = b.indexOf(String.valueOf(c), i);
			if (i == -1)
				break;
			b.replace(i, i + 1, s);
		}
		return b.toString();
	}

	/**
	 * 将str中的问号逐个替换成ss
	 */
	protected final String replace(String str, String... ss) {
		str = replace(str, '?', ss);
		if (Debug.DBG)
			Debug.log(this, str);
		return str;
	}

	/**
	 * 将str中的问号逐个替换成ss,#号逐个替换成objs,
	 * 其中:
	 * Boolean型会被转换成"0"(false),"1"(true),
	 * String型会在两端添加单引号"\'"
	 */
	protected final String replace(String str, String[] ss, Object... objs) {
		str = replace(str, '?', ss);
		str = replace(str, '#', getStringArray(objs));
		if (Debug.DBG)
			Debug.log(this, str);
		return str;
	}

	/**
	 * 将objs数组转换成对应字符串数组
	 * 其中:
	 * Boolean型会被转换成"0"(false),"1"(true),
	 * String型会在两端添加单引号"\'"
	 */
	protected final String[] getStringArray(Object... objs) {
		final int d = objs.length;
		final String[] ss = new String[objs.length];
		for (int i = 0; i < d; ++i) {
			Object o = objs[i];
			if (o instanceof String) {
				ss[i] = '\'' + (String) o + '\'';
			} else if (o instanceof Boolean) {
				ss[i] = ((Boolean) o) ? "1" : "0";
			} else {
				ss[i] = String.valueOf(o);
			}
		}
		return ss;
	}
}
