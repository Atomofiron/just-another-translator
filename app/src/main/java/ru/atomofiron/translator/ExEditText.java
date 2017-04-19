package ru.atomofiron.translator;

import android.content.Context;
import android.util.AttributeSet;

public class ExEditText extends android.support.v7.widget.AppCompatEditText {

	private OnSelectionChangedListener onSelectionChangedListener = null;

	public ExEditText(Context context) {
		super(context);
	}

	public ExEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public ExEditText(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}


	public void setOnSelectionChangedListener(OnSelectionChangedListener onSelectionChangedListener) {
		this.onSelectionChangedListener = onSelectionChangedListener;
	}

	@Override
	protected void onSelectionChanged(int selStart, int selEnd) {
		super.onSelectionChanged(selStart, selEnd);

		if (onSelectionChangedListener != null)
			onSelectionChangedListener.onSelectionChanged(selStart, selEnd);
	}



	public interface OnSelectionChangedListener {
		void onSelectionChanged(int selStart, int selEnd);
	}
}
