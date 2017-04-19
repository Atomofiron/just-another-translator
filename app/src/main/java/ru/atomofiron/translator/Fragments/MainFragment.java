package ru.atomofiron.translator.Fragments;

import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import ru.atomofiron.translator.CustomViews.ExEditText;
import ru.atomofiron.translator.I;
import ru.atomofiron.translator.Adapters.InputAdapter;
import ru.atomofiron.translator.R;
import ru.atomofiron.translator.Utils.Languages;

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
		new AvailableLanguagesGetter().execute();

        return view;
    }

    private void initInput(View view) {
		TextView yandexLabel = (TextView) view.findViewById(R.id.yandex_label);
		yandexLabel.setText(Html.fromHtml(getString(R.string.yandex_label)));
		yandexLabel.setMovementMethod(LinkMovementMethod.getInstance());

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

	private class AvailableLanguagesGetter extends AsyncTask<Void, Void, Languages> {

		@Override
		protected Languages doInBackground(Void... params) {
			String resultJson = "";
			HttpURLConnection urlConnection = null;
			BufferedReader reader = null;
			try {
				URL url = new URL("https://translate.yandex.net/api/v1.5/tr.json/getLangs?" +
						"key=" + I.API_KEY +
						"&ui=ru");

				urlConnection = (HttpURLConnection) url.openConnection();
				urlConnection.setRequestMethod("GET");
				urlConnection.connect();

				InputStream inputStream = urlConnection.getInputStream();
				StringBuilder buffer = new StringBuilder();

				reader = new BufferedReader(new InputStreamReader(inputStream));

				String line;
				while ((line = reader.readLine()) != null)
					buffer.append(line);

				resultJson = buffer.toString();
			} catch (Exception e) {
				I.Log(e.toString());
			} finally {
				if (urlConnection != null)
					urlConnection.disconnect();
				try {
					if (reader != null)
						reader.close();
				} catch (Exception ignored) {}
			}

			// парсинг должен выполняться тоже не в UI потоке
			return new Languages(resultJson);
		}

		@Override
		protected void onPostExecute(Languages languages) {
			super.onPostExecute(languages);
		}
	}

}
