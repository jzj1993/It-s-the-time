package com.jzj.alarm.ui.tools;

import java.io.File;

import com.jzj.alarm.R;
import com.jzj.alarm.core.SettingMgr;
import com.jzj.util.Debug;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;

public class MediaTools {

	static final int defResId = R.raw.alarm_default;

	public static MediaPlayer createMediaPlayer(Context context, int resId) {
		if (resId == 0)
			return null;
		try {
			MediaPlayer player = MediaPlayer.create(context, resId);
			player.setLooping(false);
			return player;
		} catch (Exception e) {
			if (Debug.DBG)
				e.printStackTrace();
		}
		return null;
	}

	/**
	 * 根据设置创建MediaPlayer,如果创建失败自动使用默认值重新创建
	 *
	 * @param context
	 * @param setVal
	 * @return
	 */
	public static MediaPlayer autoCreateMediaPlayer(Context context,
													String setVal) {
		MediaPlayer mp = createMediaPlayer(context, setVal);
		if (mp == null) {
			mp = MediaPlayer.create(context, defResId);
		}
		return mp;
	}

	/**
	 * 根据设置创建MediaPlayer,如果创建失败返回null
	 *
	 * @param context
	 * @param setVal
	 * @return
	 */
	public static MediaPlayer createMediaPlayer(Context context, String setVal) {
		MediaPlayer mp = null;
		if (setVal == null || setVal.equals(SettingMgr.DEFAULT_TONE)) {
			mp = MediaPlayer.create(context, defResId);
		} else {
			final File f = new File(setVal);
			if (f.exists() && f.isFile())
				mp = MediaPlayer.create(context, Uri.fromFile(f));
		}
		return mp;
	}
}
