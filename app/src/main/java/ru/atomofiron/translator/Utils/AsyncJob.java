package ru.atomofiron.translator.Utils;

import android.os.AsyncTask;

// для упрощения работы с AsyncTask
public class AsyncJob extends AsyncTask<Void, Void, Integer> {

	private Job job = null;

	public AsyncJob(Job job) {
		this.job = job;
	}

	@Override
	protected Integer doInBackground(Void... params) {

		if (job != null)
			job.onAsyncJobStart();

		return 0;
	}

	@Override
	protected void onPostExecute(Integer integer) {
		super.onPostExecute(integer);

		if (job != null)
			job.onJobEnd();
	}

	public interface Job {
		void onAsyncJobStart();
		void onJobEnd();
	}
}
