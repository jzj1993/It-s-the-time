package com.jzj.alarm.ui;

import com.jzj.alarm.R;
import com.jzj.alarm.ui.tools.Theme;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

public class AboutActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		View v = getLayoutInflater().inflate(R.layout.activity_about, null);
		v.setBackgroundColor(Theme.getRandomBgColor(this));
		setContentView(v);
	}
}
