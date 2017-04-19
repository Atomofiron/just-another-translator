package ru.atomofiron.translator.Adapters;

import android.animation.ValueAnimator;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.ArrayList;

import ru.atomofiron.translator.CustomViews.ExEditText;
import ru.atomofiron.translator.I;
import ru.atomofiron.translator.R;

public class InputAdapter extends RecyclerView.Adapter<InputAdapter.ViewHolder> implements ValueAnimator.AnimatorUpdateListener {

	private RecyclerView recyclerView;
	private int screenWidth;
	private final ArrayList<String> list = new ArrayList<>();
	private OnSlideListener onSlideListener = null;
	private int currentPosition = -1;

	public InputAdapter(RecyclerView recyclerView, final int screenWidth) {
		this.recyclerView = recyclerView;
		this.screenWidth = screenWidth;

		recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
			private int offset = 0;
			private boolean afterDragging = false;

			@Override
			public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
				super.onScrollStateChanged(recyclerView, newState);
				I.Log("onScrollStateChanged() "+newState);

				afterDragging |= newState == RecyclerView.SCROLL_STATE_DRAGGING;

				if (afterDragging && newState == RecyclerView.SCROLL_STATE_IDLE) {
					afterDragging = false;

					currentPosition = offset < (screenWidth / 2) ? 0 : 1;
					ExEditText currentView = (ExEditText)recyclerView.getChildAt(currentPosition);
					recyclerView.smoothScrollToPosition(recyclerView.getChildAdapterPosition(currentView));

					if (onSlideListener != null)
						onSlideListener.onSlide(currentView);
				}
			}

			@Override
			public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
				super.onScrolled(recyclerView, dx, dy);
				offset += dx;
				offset = offset % screenWidth;
				while (offset < 0)
					offset += screenWidth;
			}
		});
	}


	@Override
	public void onAnimationUpdate(ValueAnimator animation) {
		recyclerView.scrollTo(((int)animation.getAnimatedValue()), 0);
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		ExEditText v = (ExEditText) LayoutInflater.from(parent.getContext())
				.inflate(R.layout.edit_text_input, parent, false);

		return new ViewHolder(v);
	}

	@Override
	public void onBindViewHolder(ViewHolder holder, int position) {
		holder.editText.setText(position >= list.size() ? "" : list.get(position));
		holder.editText.setWidth(screenWidth);
	}

	@Override
	public int getItemCount() {
		return list.size() + 1;
	}

	public void updateList(ArrayList<String> list) {
		this.list.clear();
		this.list.addAll(list);

		notifyDataSetChanged();
		recyclerView.scrollToPosition(list.size() - 1);
	}

	public void add(String string) {
		if (recyclerView.isComputingLayout())
			return;

		list.remove(string);
		list.add(string);

		notifyDataSetChanged();
		recyclerView.scrollToPosition(list.size() - 1);
	}

	public void setOnSlideListener(OnSlideListener onSlideListener) {
		this.onSlideListener = onSlideListener;
	}

	static class ViewHolder extends RecyclerView.ViewHolder {
		public ExEditText editText;

		ViewHolder(ExEditText v) {
			super(v);
			editText = v;
		}
	}

	public interface OnSlideListener {
		void onSlide(ExEditText currentView);
	}
}
