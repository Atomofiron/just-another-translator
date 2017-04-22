package ru.atomofiron.translator.Utils;

import android.os.AsyncTask;

public class AsyncCall extends AsyncTask<Void, Void, Integer> {

	private ProcessListener processListener = null;

	public AsyncCall(ProcessListener listener) {
		processListener = listener;
	}

	@Override
	protected Integer doInBackground(Void... params) {
		while (!processListener.onBackgroundDone())
			try {
				Thread.sleep(3000);
			} catch (Exception ignored) {}

		return 0;
	}

	@Override
	protected void onPostExecute(Integer integer) {
		super.onPostExecute(integer);

		processListener.onDone();
	}

	public interface ProcessListener {
		boolean onBackgroundDone();
		void onDone();
	}
}
