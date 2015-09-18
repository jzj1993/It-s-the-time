package com.jzj.alarm;

import com.jzj.util.Debug;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;

/**
 * 自定义PageView(上下滑动翻页)
 *
 * @author jzj
 * @since 2014-09-23
 */
public class PageView extends ScrollView {

	/** 屏幕宽度 **/
	private int screenWidth;

	/** 屏幕高度 **/
	private int screenHeight;

	/** 当前页面 **/
	private int currentPage = 0;

	/** 移动的距离 **/
	private int scrollDistance = 0;

	/** 是否已经响应Fling动作翻页 **/
	private boolean hasFlinged = false;

	/** View容器 **/
	private LinearLayout container;

	private OnPageChangedListener onPageChangedListener;
	private OnPageScrollListener onPageScrollListener;
	private GestureDetector gestureDetector;

	public PageView(Context context) {
		super(context);
		init(context);
	}

	private final void init(Context context) {
		DisplayMetrics dm = context.getResources().getDisplayMetrics();
		screenWidth = dm.widthPixels;
		screenHeight = dm.heightPixels;

		gestureDetector = new GestureDetector(context, onGestureListener);
		container = new LinearLayout(context);
		container.setOrientation(LinearLayout.VERTICAL);
		this.setVerticalFadingEdgeEnabled(false);
		super.addView(container);
		this.scrollTo(0, 0);
	}

	/**
	 * 设置页面切换监听器
	 *
	 * @param onPageChangedListener
	 */
	public void setOnPageChangedListener(
			OnPageChangedListener onPageChangedListener) {
		this.onPageChangedListener = onPageChangedListener;
	}

	/**
	 * 设置页面滚动监听器
	 *
	 * @param onPageScrollListener
	 */
	public void setOnPageScrollListener(
			OnPageScrollListener onPageScrollListener) {
		this.onPageScrollListener = onPageScrollListener;
	}

	/**
	 * 添加页面
	 *
	 * @param v
	 */
	public void addPageView(View v) {
		if (v != null) {
			container.addView(v, new ViewGroup.LayoutParams(screenWidth,
					screenHeight));
		}
	}

	/**
	 * 获取页面
	 *
	 * @param v
	 */
	public View getPageView(int index) {
		return container.getChildAt(index);
	}

	/**
	 * 添加页面
	 *
	 * @param v
	 */
	@Override
	public void addView(View v) {
		this.addPageView(v);
	}

	/**
	 * 获取页面数目
	 *
	 * @return
	 */
	public int getPageCount() {
		return container.getChildCount();
	}

	/**
	 * 移动到页面
	 *
	 * @param page
	 */
	public void scrollToPage(int page) {
		this.moveToPage(page);
	}

	/**
	 * 移动页面偏移
	 *
	 * @param offset
	 */
	public void scrollByPage(int offset) {
		this.scrollToPage(currentPage + offset);
	}

	/**
	 * 移动至指定页面(动画效果)
	 *
	 * @param page
	 *            要滑动到的页面,其值可以和当前页面相同
	 */
	private void moveToPage(int page) {
		if (page < 0 || page >= this.getPageCount())
			return;
		currentPage = page;
		int dis = screenHeight * currentPage - this.getScrollY();
		new Thread(new PageMover(dis)).start();
	}

	/**
	 * 分配TouchEvent事件
	 * <p>
	 * 切换页面有两种方式:
	 * <p>
	 * 1.快速滑动抛出 onFling (滑动达到一定距离和速度)
	 * <p>
	 * 2.缓慢滑动onScroll (滑动达到较大距离)
	 * <p>
	 * 滑动时GestureDetector会不断回调OnGestureListener的onScroll方法,
	 * <p>
	 * onScroll方法让屏幕跟随用户动作滑动,同时记录下本次滑动的总距离scrollDistance
	 * <p>
	 * 一次滑动结束,如果GestureDetector将其判断为Fling动作,回调onFling,
	 * <p>
	 * 则切换页面,并设置hasFlinged=true
	 * <p>
	 * 否则不回调onFling,不设置hasFlinged标志位,则dispatchTouchEvent将动作分派给onTouchEvent处理,
	 * <p>
	 * 如果判断动作为抬起,且抬起前记录的scrollDistance超过屏幕相应尺寸的一半,则翻页
	 * <p>
	 * 否则,滑动回到原有页面
	 * <p>
	 *
	 * @param event
	 * @return
	 */
	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {
		gestureDetector.onTouchEvent(event);
		if (hasFlinged == false) {
			onTouchEvent(event);
		}
		hasFlinged = false;
		return true;
	}

	/**
	 * 处理滑动事件
	 *
	 * @param event
	 * @return
	 */
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		int action = event.getAction();
		if (action == MotionEvent.ACTION_UP) {
			// 如果移动距离大于屏幕高度的一半,则翻页,否则返回原有页面
			if (Math.abs(scrollDistance) > screenHeight / 2) {
				if (scrollDistance < 0) {
					this.scrollByPage(1);
				} else {
					this.scrollByPage(-1);
				}
			} else {
				this.scrollByPage(0);
			}
			scrollDistance = 0;
		}
		return true;
	}

	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
		super.onScrollChanged(l, t, oldl, oldt);
		int page = -1;
		if (onPageScrollListener != null) {
			page = t / screenHeight;
			final float percent = (float) (t % screenHeight)
					/ (float) screenHeight;
			onPageScrollListener.onPageScroll(page, percent);
		}
		if (onPageChangedListener != null) {
			if (t % screenHeight == 0) {
				if (page == -1) {
					page = t / screenHeight;
				}
				onPageChangedListener.onPageChanged(page);
			}
		}
	}

	private OnGestureListener onGestureListener = new OnGestureListener() {

		@Override
		public boolean onDown(MotionEvent e) {
			return false;
		}

		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
							   float velocityY) {
			int distanceY = (int) (e2.getY() - e1.getY());
			if (Math.abs(distanceY) >= 80 && Math.abs(velocityY) >= 500) {
				if (distanceY > 0)
					PageView.this.scrollByPage(-1);
				else
					PageView.this.scrollByPage(1);
				hasFlinged = true;
			}
			return false;
		}

		@Override
		public void onLongPress(MotionEvent e) {
		}

		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2,
								float distanceX, float distanceY) {
			scrollDistance = (int) (e2.getY() - e1.getY());
			PageView.this.scrollBy(0, (int) distanceY);
			return true;
		}

		@Override
		public void onShowPress(MotionEvent e) {
		}

		@Override
		public boolean onSingleTapUp(MotionEvent e) {
			return false;
		}
	};

	/**
	 * UI线程Handler,用于进行页面滑动
	 */
	private Handler handler = new Handler(Looper.getMainLooper(),
			new Handler.Callback() {
				@Override
				public boolean handleMessage(Message msg) {
					PageView.this.scrollBy(0, msg.what);
					return true;
				}
			});

	/**
	 * 发送消息动画滑动页面(Runnable对象,要在新线程中运行)
	 *
	 * @author jzj
	 */
	private class PageMover implements Runnable {

		private int distance;

		public PageMover(int distance) {
			this.distance = distance;
		}

		@Override
		public void run() {

			final int d = (int) (screenHeight / 300f + 0.5f);
			final int direction;
			if (distance > 0) {
				direction = 1;
			} else {
				distance = -distance;
				direction = -1;
			}
			while (distance > 0) {
				int offset = Math.min(distance, d);
				int interval = (screenHeight / 2 / distance - 1) / 2;
				distance -= offset;
				Message.obtain(handler, offset * direction).sendToTarget();
				try {
					Thread.sleep(Math.max(1, interval));
				} catch (InterruptedException e) {
					if (Debug.DBG)
						e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 页面切换监听器
	 *
	 * @author jzj
	 */
	public interface OnPageChangedListener {
		/**
		 * @param page
		 *            0 ~ childCount-1
		 */
		public void onPageChanged(int page);
	}

	/**
	 * 页面滚动监听器
	 *
	 * @author jzj
	 */
	public interface OnPageScrollListener {
		/**
		 * @param prevPage
		 *            0 ~ childCount-2
		 *            页面在prevPage和prevPage+1之间
		 * @param percent
		 *            滑动到的百分比
		 */
		public void onPageScroll(int prevPage, float percent);
	}
}
