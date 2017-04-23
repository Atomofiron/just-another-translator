package ru.atomofiron.translator.Adapters;


import android.content.Context;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;

import ru.atomofiron.translator.R;
import ru.atomofiron.translator.Utils.I;

/**
 * Для ввода теста, его удаления и возврата.
 */
public class InputAdapter extends PagerAdapter implements ViewPager.OnPageChangeListener, TextWatcher {
	private static final int DELAY_AFTER_TYPING_MS = 1000;

	private LayoutInflater inflater;
	private List<EditText> pages = new ArrayList<>();
	private ViewPager pager;

	private Handler handler = new Handler();
	private OnInputListener onInputListener = null;

	private boolean ignoreTextChanges = false;
	private String lastInputText = "";

	public InputAdapter(Context co, ViewPager pager){
		this.pager = pager;

		inflater = LayoutInflater.from(co);

		createView();
		pager.setOnPageChangeListener(this);
	}

	public void setText(String text) {
		if (pages.size() == 1)
			createView();

		ignoreTextChanges = true;
		pages.get(0).setText(text);
		pages.get(1).setText("");
		ignoreTextChanges = false;

		notifyDataSetChanged();
		pager.setCurrentItem(0, false);
		pages.get(0).setSelection(text.length());
	}

	private void createView() {
		EditText editText = (EditText) inflater.inflate(R.layout.edit_text_input, null, false);
		editText.addTextChangedListener(this);
		pages.add(editText);
	}

	@Override
	public Object instantiateItem(View collection, int position) {
		View v = pages.get(position);
		((ViewPager) collection).addView(v, 0);
		return v;
	}

	@Override
	public void destroyItem(View collection, int position, Object view){
		((ViewPager) collection).removeView((View) view);
	}

	@Override
	public int getCount(){
		return pages.size();
	}

	@Override
	public boolean isViewFromObject(View view, Object object){
		return view.equals(object);
	}

	public void setOnInputListener(OnInputListener onInputListener) {
		this.onInputListener = onInputListener;
	}

	@Override
	public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

	@Override
	public void onPageSelected(int position) {
		onInputListener.onInput(pages.get(position).getText().toString());
	}

	@Override
	public void onPageScrollStateChanged(int state) {}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {}

	@Override
	public void afterTextChanged(final Editable s) {
		if (ignoreTextChanges)
			return;

		handler.removeMessages(0);

		// автоматический перевод при вводе текста с задержкой в 1000 мс
		String text = I.clearInput(s.toString());
		if (!text.isEmpty() && !text.equals(lastInputText)) {
			lastInputText = text;
			handler.postDelayed(new Runnable() {
				public void run() {
					String str = s.toString();
					onInputListener.onInput(str);
					setText(str);
				}
			}, DELAY_AFTER_TYPING_MS);
		}
	}

	public interface OnInputListener {
		void onInput(String text);
	}
}
