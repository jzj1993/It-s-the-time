package com.jzj.alarm.ui;

import com.jzj.alarm.PageView;
import com.jzj.alarm.R;
import com.jzj.alarm.PageView.OnPageScrollListener;
import com.jzj.util.Debug;
import com.jzj.util.ui.ColorUtils;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

/**
 * 向导界面
 *
 * @author jzj
 */
public class GuideActivity extends Activity {

	private static final int layoutIds[] = {
			R.layout.guide01_ui,
			R.layout.guide02_alarm,
			R.layout.guide03_exercise,
			R.layout.guide04_voice,
			R.layout.guide05_its_the_time };

	private int colors[];
	private PageView pv;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		colors = this.getResources().getIntArray(R.array.colors_guide_bg);
		pv = new PageView(this);
		pv.setBackgroundColor(colors[0]);
		pv.setOnPageScrollListener(onPageScrollListener);
		LayoutInflater in = getLayoutInflater();
		for (int i = 0; i < layoutIds.length; ++i) {
			final int id = layoutIds[i];
			View v = in.inflate(id, null);
			v.setBackgroundColor(0);
			pv.addPageView(v);
		}
		pv.addPageView(new View(this));
		View v = pv.getPageView(0).findViewById(R.id.tx_guide_arrow);
		Animation animation = AnimationUtils.loadAnimation(this,
				R.anim.guide_trans_alpha);
		v.startAnimation(animation);
		this.setContentView(pv);
	}

	private final void startMain() {
		this.finish();
		this.overridePendingTransition(0, R.anim.alpha_exit_400);
	}

	private OnPageScrollListener onPageScrollListener = new OnPageScrollListener() {

		@Override
		public void onPageScroll(int prevPage, float percent) {
			if (Debug.DBG)
				Debug.log(this, "prevPage=" + prevPage + ",per=" + percent);
			if ((prevPage == 4 && percent >= 0.9f) || prevPage > 4) {
				startMain();
				return;
			}
			try {
				final int[] cs = { colors[prevPage], colors[prevPage + 1] };
				final float[] ws = { 1f - percent, percent };
				pv.setBackgroundColor(ColorUtils.colorMixWeight(cs, ws));
			} catch (Exception e) {
				if (Debug.DBG)
					e.printStackTrace();
			}
		}
	};
}
