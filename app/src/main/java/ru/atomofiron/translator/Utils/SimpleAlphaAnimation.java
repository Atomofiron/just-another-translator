package ru.atomofiron.translator.Utils;

import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;

public class SimpleAlphaAnimation implements Animation.AnimationListener {
	private static final int DURATION = 200;

	private CallbackAnimHalfway callbackAnimHalfway = null;
	private View[] views;

	public SimpleAlphaAnimation(View... views) {
		this.views = views;
	}

	public void start(CallbackAnimHalfway callback) {
		callbackAnimHalfway = callback;

		for (int i = 0; i < views.length; i++) {
			Animation anim = new AlphaAnimation(1, 0);
			anim.setDuration(DURATION);

			if (i == views.length - 1)
				anim.setAnimationListener(this);

			views[i].startAnimation(anim);
		}

	}

	@Override
	public void onAnimationEnd(Animation animation) {
		if (callbackAnimHalfway != null)
			callbackAnimHalfway.onAnimHalfway(views);

		Animation anim = new AlphaAnimation(0, 1);
		anim.setDuration(DURATION);

		for (View view : views)
			view.startAnimation(anim);
	}


	@Override
	public void onAnimationStart(Animation animation) {}

	@Override
	public void onAnimationRepeat(Animation animation) {}

	public interface CallbackAnimHalfway {
		void onAnimHalfway(View... views);
	}
}
