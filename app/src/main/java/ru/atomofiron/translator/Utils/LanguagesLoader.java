package ru.atomofiron.translator.Utils;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import ru.atomofiron.translator.I;

public class LanguagesLoader extends AsyncTask<Void, Void, Languages> {

	private String ui;
	private OnLoadedListener onLoadedListener = null;

	public LanguagesLoader(String ui, OnLoadedListener listener) {
		this.ui = ui;
		onLoadedListener = listener;
	}

	@Override
	protected Languages doInBackground(Void... params) {
		String resultJson = "";
		HttpURLConnection urlConnection = null;
		BufferedReader reader = null;
		try {
			URL url = new URL("https://translate.yandex.net/api/v1.5/tr.json/getLangs?" +
					"key=" + I.API_KEY + "&ui=" + ui);

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

		// todo to consider errors 401 and 402

		// парсинг должен выполняться тоже не в UI потоке
		return new Languages(resultJson);
	}

	@Override
	protected void onPostExecute(Languages languages) {
		super.onPostExecute(languages);

		if (onLoadedListener != null)
			onLoadedListener.onLoaded(languages);
	}

	public interface OnLoadedListener {
		void onLoaded(Languages languages);
	}
}