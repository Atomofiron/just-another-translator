package ru.atomofiron.translator.Utils;

import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;

public class SimpleAlphaAnimation {

	private View[] views;
	private int count;

	public SimpleAlphaAnimation(View... views) {
		this.views = views;
	}

	public void start(final OnActionListener listener) {
		count = 0;

		for (View view : views) {
			Animation anim = new AlphaAnimation(1, 0);
			anim.setDuration(200);
			anim.setAnimationListener(new Animation.AnimationListener() {
				public void onAnimationStart(Animation animation) {
				}

				public void onAnimationRepeat(Animation animation) {
				}

				public void onAnimationEnd(Animation animation) {
					count++;

					if (views.length == count) {
						if (listener != null)
							listener.onAnimHalfway(views);

						Animation anim = new AlphaAnimation(0, 1);
						anim.setDuration(200);

						for (View view : views)
							view.startAnimation(anim);
					}
				}
			});

			view.startAnimation(anim);
		}
	}

	public interface OnActionListener {
		void onAnimHalfway(View... views);
	}
}
