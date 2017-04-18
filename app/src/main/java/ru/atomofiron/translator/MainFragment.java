package ru.atomofiron.translator;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

public class MainFragment extends Fragment {

    public MainFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_main, container, false);

		initInput(view);

        return view;
    }

    private void initInput(View view) {

		int width = I.getScreenWidth(getActivity());

		HorizontalScrollView hsv = (HorizontalScrollView)view.findViewById(R.id.input_scroll);
		((ExHorizontalScrollView)hsv).setScreenWidth(width);

		LinearLayout ll = ((LinearLayout)hsv.findViewById(R.id.input_layout));

		LayoutInflater inflater = LayoutInflater.from(getActivity());
		for (int i = 0; i < 5; i++) {
			EditText et = (EditText)inflater.inflate(R.layout.edit_text_input, null, false);

			et.setText("TEXT "+i);
			et.setLayoutParams(new LinearLayout.LayoutParams(width, ViewGroup.LayoutParams.MATCH_PARENT));

			ll.addView(et);
		}
	}

}
