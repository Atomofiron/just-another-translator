package ru.atomofiron.translator;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainFragment extends Fragment implements InputAdapter.OnSlideListener, TextView.OnEditorActionListener {

	private ExEditText currentEditText = null;
	private RecyclerView recyclerView;
	private InputAdapter inputAdapter;

    public MainFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) { // вот щас вообще не до гранулированности коммитов

        View view = inflater.inflate(R.layout.fragment_main, container, false);

		initInput(view);

        return view;
    }

    private void initInput(View view) {
		recyclerView = (RecyclerView) view.findViewById(R.id.input_recycler_view);
		inputAdapter = new InputAdapter(recyclerView, I.getScreenWidth(getActivity()));

		recyclerView.setLayoutManager
				(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
		recyclerView.setAdapter(inputAdapter);
		inputAdapter.setOnSlideListener(this);

		inputAdapter.add("apple");
		inputAdapter.add("google");
		inputAdapter.add("one plus");
		inputAdapter.add("yandex");
	}
	@Override
	public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
		String value = currentEditText.getText().toString();
		addToHistory(value);
		updateTranslation(value);

		return false;
	}

	private void addToHistory(String value) {
		// todo add to real history
		inputAdapter.add(value);
	}

	private void updateTranslation(String value) {
		// todo translate
	}

	@Override
	public void onSlide(ExEditText currentView) {
		currentEditText = currentView;
		currentEditText.setOnEditorActionListener(this);
		I.Log("currentEditText: "+currentEditText.getText().toString());
		currentView.requestFocus();
		currentView.setSelection(currentView.length());
	}

}
