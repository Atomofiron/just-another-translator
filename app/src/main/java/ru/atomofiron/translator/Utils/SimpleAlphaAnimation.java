package ru.atomofiron.translator.Utils;

import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;

public class SimpleAlphaAnimation implements Animation.AnimationListener {

	private OnActionListener onActionListener = null;
	private View[] views;
	private int count;

	public SimpleAlphaAnimation(View... views) {
		this.views = views;
	}

	public void start(final OnActionListener listener) {
		onActionListener = listener;
		count = 0;

		for (View view : views) {
			Animation anim = new AlphaAnimation(1, 0);
			anim.setDuration(200);
			anim.setAnimationListener(this);

			view.startAnimation(anim);
		}
	}

	@Override
	public void onAnimationEnd(Animation animation) {
		count++;

		if (views.length == count) {
			if (onActionListener != null)
				onActionListener.onAnimHalfway(views);

			Animation anim = new AlphaAnimation(0, 1);
			anim.setDuration(200);

			for (View view : views)
				view.startAnimation(anim);
		}
	}

	@Override
	public void onAnimationStart(Animation animation) {}

	@Override
	public void onAnimationRepeat(Animation animation) {}

	public interface OnActionListener {
		void onAnimHalfway(View... views);
	}
}