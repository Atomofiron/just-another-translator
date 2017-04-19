package ru.atomofiron.translator;

import android.app.Activity;
import android.util.DisplayMetrics;
import android.util.Log;

public class I {

	public static final String API_KEY = "trnsl.1.1.20170419T031622Z.8e68bdd8f4d61856.840a8a3eeae258956ff37db9eb6d04d0a6b9d51d";

	public static void Log(String log) {
		if (BuildConfig.DEBUG)
			Log.e("atomofiron", log);
		else
			Log.d("atomofiron", log);
	}

	public static int getScreenWidth(Activity ac) {
		DisplayMetrics metrics = new DisplayMetrics();
		ac.getWindowManager().getDefaultDisplay().getMetrics(metrics);

		return metrics.widthPixels;
	}
}
