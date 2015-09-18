package com.jzj.alarm;

import com.jzj.alarm.core.AlarmItem;
import com.jzj.alarm.ui.tools.AlarmString;
import com.jzj.util.painter.AlignTool;
import com.jzj.util.painter.TextPainter;
import com.jzj.util.painter.AlignTool.Direction;
import com.jzj.util.ui.ColorUtils;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.AttributeSet;

/**
 * 用于显示闹铃的View
 * 
 * @author jzj
 */
public class HexAlarmView extends HexView {

	private AlarmItem alarm;
	private AlarmString as;
	private TextPainter tx0_note, tx1_rept, tx2_mode;// 备注，重复，模式

	/**
	 * 从程序代码实例化
	 * 
	 * @param context
	 * @param alarm
	 */
	public HexAlarmView(Context context, AlarmItem alarm) {
		super(context);
		init(context);
		setAlarm(alarm);
	}

	/**
	 * 从XML文件实例化
	 * 
	 * @param context
	 * @param attrs
	 */
	public HexAlarmView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
		setAlarm(null);
	}

	private final void init(Context context) {
		tx0_note = new TextPainter(context);
		tx1_rept = new TextPainter(context);
		tx2_mode = new TextPainter(context);
		tx0_note.setTextColor(ColorUtils.setAlpha(Color.WHITE, 200));
		tx1_rept.setTextColor(ColorUtils.setAlpha(Color.WHITE, 180));
		tx2_mode.setTextColor(ColorUtils.setAlpha(Color.WHITE, 160));
	}

	@Override
	protected void initSize(int width, int height) {
		super.initSize(width, height);
		this.setIconSize(height * 0.2f);
		this.setTextSize(height * 0.27f);
		tx0_note.setSize(height * 0.1f);
		if (tx0_note.getWidth() > tx.getWidth())
			tx0_note.setWidth(tx.getWidth());
		tx1_rept.setSize(height * 0.08f);
		tx2_mode.setSize(height * 0.08f);
	}

	@Override
	protected void drawElements(Canvas canvas) {

		// final float x = getWidth() / 2;
		// final float x = getWidth() * (isLeft() ? 33 : 31) / 64;
		final float x = getWidth() * (isLeft() ? 17 : 15) / 32;
		final float y = getHeight() / 2;

		tx.setAlign(AlignTool.ALIGN_CENTER, x, y);

		if (isIconEnable()) {

			if (isLeft()) {
				img.setImage(R.drawable.ic_bell_left_borderless);
				img.setAlign(AlignTool.ALIGN_CENTER_VERTICAL
						| AlignTool.ALIGN_TO_RIGHT_OF, tx);
			} else {
				img.setImage(R.drawable.ic_bell_right_borderless);
				img.setAlign(AlignTool.ALIGN_CENTER_VERTICAL
						| AlignTool.ALIGN_TO_LEFT_OF, tx);
			}
			img.setAlpha(210);
		}

		// int align = AlignTool.ALIGN_CENTER_HORIZONTAL;
		int align = isLeft() ? AlignTool.ALIGN_LEFT : AlignTool.ALIGN_RIGHT;
		tx0_note.setAlign(align | AlignTool.ALIGN_TO_TOP_OF, tx);
		tx0_note.move(Direction.UP, 6);
		tx1_rept.setAlign(align | AlignTool.ALIGN_TO_BOTTOM_OF, tx);
		tx1_rept.move(Direction.DOWN, 2);
		tx2_mode.setAlign(align | AlignTool.ALIGN_TO_BOTTOM_OF, tx1_rept);
		tx2_mode.move(Direction.DOWN, 4);

		if (isIconEnable())
			img.draw(canvas);
		tx.draw(canvas);
		tx0_note.draw(canvas);
		tx1_rept.draw(canvas);
		tx2_mode.draw(canvas);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
	}

	/**
	 * 获取闹铃id
	 */
	public final int getAlarmId() {
		return alarm.getId();
	}

	/**
	 * 设置闹铃
	 * 
	 * @param alarm
	 */
	public final void setAlarm(AlarmItem alarm) {
		if (alarm == null) {
			this.alarm = new AlarmItem(0, 0);
		} else {
			this.alarm = alarm;
		}
		if (as == null)
			as = new AlarmString(this.getContext(), this.alarm);
		this.setIconEnable(this.alarm.isOn());
		this.setTextEnable(true);
		this.setText(as.time());
		tx0_note.setText(as.notes());
		tx1_rept.setText(as.repeat());
		tx2_mode.setText(as.mode());
	}

	/**
	 * 设置闹铃状态并刷新显示
	 */
	public final void setAlarmOnOff(boolean on_off) {
		this.alarm.setOnOff(on_off);
		this.setIconEnable(on_off);
		this.invalidate();
	}

	/**
	 * 翻转闹铃状态并刷新显示
	 */
	public final void flipAlarmOnOff() {
		this.alarm.flipOnOff();
		this.setIconEnable(!this.isIconEnable());
		this.invalidate();
	}

	/**
	 * 获取闹铃状态
	 */
	public final boolean isAlarmOn() {
		return alarm.isOn();
	}
}
