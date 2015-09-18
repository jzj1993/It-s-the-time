package com.jzj.util;

import android.hardware.SensorManager;

/**
 * 计步器算法
 * 
 * @author jzj
 * @since 2014-08-24
 */
public abstract class Pedometer {

	private final float scale = -(240 * (1.0f / (SensorManager.STANDARD_GRAVITY * 2)));
	private final float offset = 240;

	/* 阈值越低 越灵敏 */
	private int threshold = 30;

	private float lastVal;
	private float lastDiff;
	private float extremes[] = new float[2];
	private int lastDirection;
	private int lastMatch = -1;

	/**
	 * @param threshold
	 *            阈值越低 越灵敏
	 */
	public Pedometer(int threshold) {
		this.setThreshold(threshold);
	}

	/**
	 * 设置阈值(灵敏度)
	 * 
	 * @param threshold
	 *            阈值越低 越灵敏
	 * @return
	 */
	public final Pedometer setThreshold(int threshold) {
		this.threshold = threshold;
		this.reset();
		return this;
	}

	/**
	 * 添加传感器数据进行计算分析
	 * 
	 * @param val
	 *            加速度传感器的值
	 */
	public void putSensorData(float[] val) {

		final float v = (val[0] + val[1] + val[2]) / 3 * scale + offset;

		// v增大则d=1
		int direction = (v > lastVal ? 1 : (v < lastVal ? -1 : 0));

		if (direction == -lastDirection) {

			// v的方向变化时 产生极值点
			final int extType = (direction > 0 ? 0 : 1); // minumum or maximum?
			extremes[extType] = lastVal; // ex[0] ex[1] 分别为极小值和极大值

			final float diff = Math.abs(extremes[0] - extremes[1]); // 两个极值点之差

			if (diff > threshold) {

				boolean isAlmostAsLargeAsPrevious = diff > (lastDiff * 2 / 3);
				boolean isPreviousLargeEnough = lastDiff > (diff / 3);
				boolean isNotContra = (lastMatch != 1 - extType); // 每两次达到阈值增加一步

				if (isAlmostAsLargeAsPrevious && isPreviousLargeEnough
						&& isNotContra) {
					this.onStep();
					lastMatch = extType;
				} else {
					lastMatch = -1;
				}
			}
			lastDiff = diff;
		}

		lastDirection = direction;
		lastVal = v;
	}

	protected abstract void onStep();

	private final void reset() {
		lastVal = 0;
		lastDiff = 0;
		extremes[0] = extremes[1] = 0;
		lastDirection = 0;
		lastMatch = -1;
	}
}
