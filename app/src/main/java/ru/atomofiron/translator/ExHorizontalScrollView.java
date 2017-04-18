package ru.atomofiron.translator;

import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.animation.AccelerateInterpolator;
import android.widget.HorizontalScrollView;

public class ExHorizontalScrollView extends HorizontalScrollView implements ValueAnimator.AnimatorUpdateListener {

	private int screenWidth;
	private int touchState = MotionEvent.ACTION_UP;
	boolean animAble = true;

	public ExHorizontalScrollView(Context context) {
		super(context);
	}

	public ExHorizontalScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public ExHorizontalScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
		super.onScrollChanged(l, t, oldl, oldt);

		if (animAble && Math.abs(oldl - l) == 1) {
			animAble = false;

			animateScroll();
		}
	}

	private void animateScroll() {
		if (touchState != MotionEvent.ACTION_UP)
			return;

		int x = getScrollX();
		int dif = x % screenWidth;

		ValueAnimator animator = ValueAnimator.ofInt(x, x - dif + (dif > screenWidth / 2 ? screenWidth : 0));

		animator.setDuration(300);
		animator.setInterpolator(new AccelerateInterpolator());
		animator.addUpdateListener(this);

		animator.start();
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		touchState = ev.getAction();
		if (touchState == MotionEvent.ACTION_UP)
			animateScroll();

		return super.onTouchEvent(ev);
	}

	public void setScreenWidth(int screenWidth) {
		this.screenWidth = screenWidth;
	}

	@Override
	public void onAnimationUpdate(ValueAnimator animation) {
		int value = ((int)animation.getAnimatedValue());
		animAble = value % screenWidth == 0;
		setScrollX(value);
	}
}
