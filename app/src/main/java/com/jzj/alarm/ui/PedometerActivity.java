package com.jzj.alarm.ui;

import com.jzj.alarm.R;
import com.jzj.alarm.ToastView;
import com.jzj.alarm.core.SettingMgr;
import com.jzj.alarm.ui.tools.SimpleBuilder;
import com.jzj.util.Pedometer;
import com.jzj.util.SystemUtils;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class PedometerActivity extends Activity {

	private int sens = 30;
	private int steps = 0;

	private String strSens;
	private String strSteps;

	private TextView txSens;
	private TextView txSteps;

	private SettingMgr set;
	private Pedometer pedometer;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (!SystemUtils.startSensor(this, Sensor.TYPE_ACCELEROMETER,
				sensorListener)) {
			ToastView.show(this, R.string.pedometer_no_sensor);
		}
		setContentView(R.layout.activity_pedometer);

		txSens = (TextView) findViewById(R.id.tx_pedometer_cur_sens);
		txSteps = (TextView) findViewById(R.id.tx_pedometer_cur_steps);
		findViewById(R.id.bn_pedometer_set_sens).setOnClickListener(
				setSensListener);

		set = SettingMgr.getInstance(this);

		sens = set.getSensitivity();

		strSteps = getString(R.string.pedometer_cur_steps);
		strSens = getString(R.string.pedometer_cur_sens);

		pedometer = new Pedometer(sens) {
			@Override
			protected void onStep() {
				++steps;
				refreshSteps();
			}
		};
		refreshSens();
		refreshSteps();
	}

	private void refreshSteps() {
		txSteps.setText(String.format(strSteps, steps));
	}

	private void refreshSens() {
		txSens.setText(String.format(strSens, sens));
	}

	private void sensitivity() {
		SimpleBuilder.OnGetIntListener ls = new SimpleBuilder.OnGetIntListener() {

			@Override
			public void onGetInt(int d) {
				if (d > 0 && d < 201) {
					sens = d;
					steps = 0;
					set.setSencitivity(d);
					pedometer.setThreshold(sens);
					refreshSens();
					refreshSteps();
				}
			}
		};
		SimpleBuilder.inputInt(this, R.string.set_sensitivity,
				set.getSensitivity(), ls);
	}

	private OnClickListener setSensListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			sensitivity();
		}
	};

	private SensorEventListener sensorListener = new SensorEventListener() {

		@Override
		public void onSensorChanged(SensorEvent event) {
			if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER
					&& pedometer != null) {
				pedometer.putSensorData(event.values);
			}
		}

		@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy) {
		}
	};
}
