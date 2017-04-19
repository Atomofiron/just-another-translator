package ru.atomofiron.translator;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;

public class ExEditText extends android.support.v7.widget.AppCompatEditText implements TextWatcher {

	private OnTextChangedListener onTextChangedListener = null;

	public ExEditText(Context context) {
		super(context);
		init(context);
	}

	public ExEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public ExEditText(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init(context);
	}

	private void init(Context context) {
		addTextChangedListener(this);
	}

	public void setOnTextChangedListener(OnTextChangedListener onTextChangedListener) {
		this.onTextChangedListener = onTextChangedListener;
	}

	@Override
	public InputConnection onCreateInputConnection(EditorInfo outAttrs) {
		InputConnection conn = super.onCreateInputConnection(outAttrs);
		outAttrs.imeOptions &= ~EditorInfo.IME_FLAG_NO_ENTER_ACTION;
		return conn;
	}


	@Override
	public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {}

	@Override
	public void afterTextChanged(Editable s) {
		if (onTextChangedListener != null)
			onTextChangedListener.onTextChanged(s);
	}

	public interface OnTextChangedListener {
		void onTextChanged(Editable s);
	}
}
