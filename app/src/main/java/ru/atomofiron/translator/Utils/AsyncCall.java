package ru.atomofiron.translator.Utils;

import android.os.AsyncTask;

public class AsyncCall extends AsyncTask<Void, Void, Integer> {

	private Callback callback = null;

	public AsyncCall(Callback callback) {
		this.callback = callback;
	}

	@Override
	protected Integer doInBackground(Void... params) {
		while (!callback.onBackground())
			try {
				Thread.sleep(3000);
			} catch (Exception ignored) {}

		return 0;
	}

	@Override
	protected void onPostExecute(Integer integer) {
		super.onPostExecute(integer);

		callback.onDone();
	}

	public interface Callback {
		boolean onBackground();
		void onDone();
	}
}
