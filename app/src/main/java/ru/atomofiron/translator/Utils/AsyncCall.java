package ru.atomofiron.translator.Utils;

import android.os.AsyncTask;

/**
 * Для цикличной отправки запростов к API.
 */
public class AsyncCall extends AsyncTask<Void, Void, Integer> {

	private ProcessListener processListener = null;
	private boolean canceled = false;

	public AsyncCall(ProcessListener listener) {
		processListener = listener;
	}

	@Override
	protected Integer doInBackground(Void... params) {
		while (!processListener.onBackgroundDone() && !canceled)
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

	public void cancel() {
		canceled = true;
	}

	public interface ProcessListener {
		/**
		 * Вызывается в параллельном потоке.
		 *
		 * @return  повторить операцию.
		 */
		boolean onBackgroundDone();

		/**
		 * Вызывается в основном потоке после завершения вызовов onBackgroundDone().
		 */
		void onDone();
	}
}
