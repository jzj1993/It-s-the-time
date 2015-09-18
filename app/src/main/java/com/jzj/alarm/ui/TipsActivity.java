package com.jzj.alarm.ui;

import com.jzj.alarm.R;
import com.jzj.alarm.ui.tools.Theme;
import com.jzj.util.ui.ColorUtils;

import android.app.Activity;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

public class TipsActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		View v = getLayoutInflater().inflate(R.layout.activity_tips, null);
		v.setBackgroundColor(ColorUtils.colorMixRgb(
				Theme.getRandomBgColor(this), 0x22000000));
		TextView tx = (TextView) v.findViewById(R.id.tx_tips);
		tx.setText(Html.fromHtml(getString(R.string.app_tips)));
		setContentView(v);
	}
}
