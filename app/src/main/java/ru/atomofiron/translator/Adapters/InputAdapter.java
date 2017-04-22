package ru.atomofiron.translator.Adapters;

import android.animation.ValueAnimator;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;

import ru.atomofiron.translator.CustomViews.ExEditText;
import ru.atomofiron.translator.R;
import ru.atomofiron.translator.Utils.Base;
import ru.atomofiron.translator.Utils.Node;

public class InputAdapter extends RecyclerView.Adapter<InputAdapter.ViewHolder> implements ValueAnimator.AnimatorUpdateListener, TextView.OnEditorActionListener {

	private RecyclerView recyclerView;
	private int screenWidth;
	private final ArrayList<Node> list = new ArrayList<>();
	private OnInputListener onInputListener = null;
	private Base base;
	private EditText currentView;

	public InputAdapter(RecyclerView recyclerView, Base base, final int screenWidth) {
		this.recyclerView = recyclerView;
		this.base = base;
		this.screenWidth = screenWidth;

		updateList(base.get(Node.TYPE.HISTORY));

		recyclerView.setOnScrollListener(new ScrollListener());
	}

	@Override
	public void onAnimationUpdate(ValueAnimator animation) {
		recyclerView.scrollTo(((int)animation.getAnimatedValue()), 0);
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		ExEditText v = (ExEditText) LayoutInflater.from(parent.getContext())
				.inflate(R.layout.edit_text_input, parent, false);
		v.setOnEditorActionListener(this);

		return new ViewHolder(v);
	}

	@Override
	public void onBindViewHolder(ViewHolder holder, int position) {
		holder.editText.setText(position >= list.size() ? "" : list.get(position).getPhrase());
		holder.editText.setWidth(screenWidth);
	}

	@Override
	public int getItemCount() {
		return list.size() + 1;
	}

	private void updateList(ArrayList<Node> list) {
		this.list.clear();
		this.list.addAll(list);

		notifyDataSetChanged();
		recyclerView.scrollToPosition(list.size());
	}

	public void add(Node node) {
		if (recyclerView.isComputingLayout())
			return;

		list.remove(node);
		list.add(node);
		base.put(node);

		notifyDataSetChanged();
		recyclerView.scrollToPosition(list.size() - 1);
	}

	public void setCurrentText(String text) {
		if (currentView == null)
			currentView = (EditText) recyclerView.getChildAt(list.size());

		currentView.setText(text);
	}


	public void setOnInputListener(OnInputListener onInputListener) {
		this.onInputListener = onInputListener;
	}

	@Override
	public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
		if (actionId == KeyEvent.KEYCODE_ENDCALL && onInputListener != null)
			onInputListener.onInput(v.getText().toString(), false);

		return false;
	}

	static class ViewHolder extends RecyclerView.ViewHolder {
		ExEditText editText;

		ViewHolder(ExEditText v) {
			super(v);
			editText = v;
		}
	}

	private class ScrollListener extends RecyclerView.OnScrollListener {
		private int offset = 0;
		private boolean alreadySlided = false;

		@Override
		public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
			super.onScrollStateChanged(recyclerView, newState);

			if (newState == RecyclerView.SCROLL_STATE_IDLE && !alreadySlided)
				slide(recyclerView);
			else
				if (newState == RecyclerView.SCROLL_STATE_DRAGGING)
					alreadySlided = false;
		}

		@Override
		public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
			super.onScrolled(recyclerView, dx, dy);
			offset += dx;
			offset = offset % screenWidth;

			while (offset < 0)
				offset += screenWidth;
		}

		private void slide(RecyclerView recyclerView) {
			alreadySlided = true;
			int currentPosition = offset < (screenWidth / 2) ? 0 : 1;

			currentView = (EditText) recyclerView.getChildAt(currentPosition);
			currentView.requestFocus();

			if (onInputListener != null)
				onInputListener.onInput(currentView.getText().toString(), true);

			recyclerView.smoothScrollToPosition(recyclerView.getChildAdapterPosition(currentView));
		}
	}

	public interface OnInputListener {
		void onInput(String text, boolean fromHistory);
	}
}
