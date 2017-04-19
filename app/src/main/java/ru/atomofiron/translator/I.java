package ru.atomofiron.translator;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.Toast;

public class I {

	public static final String API_KEY = "trnsl.1.1.20170419T031622Z.8e68bdd8f4d61856.840a8a3eeae258956ff37db9eb6d04d0a6b9d51d";

	public static final String PREF_FIRST_LANG_CODE = "last_first_lang_code";
	public static final String PREF_SECOND_LANG_CODE = "last_second_lang_code";

	public static void Log(String log) {
		if (BuildConfig.DEBUG)
			Log.e("atomofiron", log);
		else
			Log.d("atomofiron", log);
	}

	public static void Toast(Context co, int stringId) {
		Toast.makeText(co, co.getString(stringId), Toast.LENGTH_LONG).show();
	}

	public static SharedPreferences SP(Context co) {
		return PreferenceManager.getDefaultSharedPreferences(co);
	}

	public static int getScreenWidth(Activity ac) {
		DisplayMetrics metrics = new DisplayMetrics();
		ac.getWindowManager().getDefaultDisplay().getMetrics(metrics);

		return metrics.widthPixels;
	}
}
