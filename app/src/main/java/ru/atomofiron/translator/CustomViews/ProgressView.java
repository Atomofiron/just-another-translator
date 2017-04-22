package ru.atomofiron.translator.CustomViews;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import ru.atomofiron.translator.Utils.I;
import ru.atomofiron.translator.R;

public class ProgressView extends android.support.v7.widget.AppCompatImageView {
	private Animation anim;
	private boolean isShowing = true;

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
		setImageDrawable(co.getResources().getDrawable(R.drawable.spin));
		setBackgroundDrawable(co.getResources().getDrawable(R.drawable.circle));

		anim = AnimationUtils.loadAnimation(co, R.anim.rotate_center);
		startAnimation(anim);
	}

	public void hide() {
		if (!isShowing)
			return;
		isShowing = false;

		// нужно очистить анимацию, иначе не скрывается
		anim.cancel();
		clearAnimation();
		setVisibility(GONE);
	}
	public void show() {
		if (isShowing)
			return;
		isShowing = true;

		setVisibility(VISIBLE);
		startAnimation(anim);
	}
}
