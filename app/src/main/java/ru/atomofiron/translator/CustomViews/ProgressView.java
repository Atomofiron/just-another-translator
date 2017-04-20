package ru.atomofiron.translator.CustomViews;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;

import ru.atomofiron.translator.R;

public class ProgressView extends FrameLayout {

	private Animation hideAnim;
	private Animation showAnim;
	private boolean isShowing = true;
	private Animation spinAnimation;
	private View spin;

	public ProgressView(Context context) {
		super(context);
		init(context);
	}

	public ProgressView(Context context, @Nullable AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public ProgressView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init(context);
	}

	private void init(Context co) {
		spin = LayoutInflater.from(co).inflate(R.layout.spin, this);

		spinAnimation = AnimationUtils.loadAnimation(co, R.anim.rotate_center);
		spin.startAnimation(spinAnimation);

		hideAnim = new AlphaAnimation(1, 0);
		hideAnim.setDuration(500);
		hideAnim.setAnimationListener(new Animation.AnimationListener() {
			public void onAnimationStart(Animation animation) {}
			public void onAnimationRepeat(Animation animation) {}
			public void onAnimationEnd(Animation animation) {
				spin.setVisibility(GONE);
			}
		});
		showAnim = new AlphaAnimation(0, 1);
		showAnim.setDuration(500);
	}

	public void hide() {
		if (isShowing)
			startAnimation(hideAnim);

		isShowing = false;
	}
	public void show() {
		if (!isShowing) {
			startAnimation(showAnim);
			spin.setVisibility(VISIBLE);
		}

		isShowing = true;
	}
}
