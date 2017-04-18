package ru.atomofiron.translator;

import android.app.Activity;
import android.util.DisplayMetrics;
import android.util.Log;

public class I {


	static void Log(String log) {
		if (BuildConfig.DEBUG)
			Log.e("atomofiron", log);
		else
			Log.d("atomofiron", log);
	}

	static int getScreenWidth(Activity ac) {
		DisplayMetrics metrics = new DisplayMetrics();
		ac.getWindowManager().getDefaultDisplay().getMetrics(metrics);

		return metrics.widthPixels;
	}
}
