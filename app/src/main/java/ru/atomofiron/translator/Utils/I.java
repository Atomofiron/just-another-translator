package ru.atomofiron.translator.Utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.Toast;

import ru.atomofiron.translator.BuildConfig;
import ru.atomofiron.translator.R;

public class I {

	public static final String API_KEY = "trnsl.1.1.20170419T031622Z.8e68bdd8f4d61856.840a8a3eeae258956ff37db9eb6d04d0a6b9d51d";
	public static final String API_KEY_DIC = "dict.1.1.20170420T134953Z.e1cb85fe3027d322.448554239b3296420395590ff4b180d8c436a6b0";
	public static final String DIC_URL = "https://dictionary.yandex.net/api/v1/dicservice.json/lookup";

	public static int CODE_COLOR_POS;
	public static int CODE_COLOR_TEXT;
	public static int CODE_COLOR_MEAN;

	public static final String PREF_FIRST_LANG_CODE = "last_first_lang_code";
	public static final String PREF_SECOND_LANG_CODE = "last_second_lang_code";

	public static void Log(String log) {
		if (BuildConfig.DEBUG)
			Log.e("atomofiron", log);
		else
			Log.d("atomofiron", log);
	}

	public static void Loge(String log) {
		Log.e("atomofiron", log);
	}

	public static void Toast(Context co, int stringId) {
		Toast.makeText(co, co.getString(stringId), Toast.LENGTH_LONG).show();
	}

	public static void sleep(long ms) {
		try {
			Thread.sleep(ms);
		} catch (Exception ignored) {}
	}

	public static SharedPreferences SP(Context co) {
		return PreferenceManager.getDefaultSharedPreferences(co);
	}

	public static int getScreenWidth(Activity ac) {
		DisplayMetrics metrics = new DisplayMetrics();
		ac.getWindowManager().getDefaultDisplay().getMetrics(metrics);

		return metrics.widthPixels;
	}

	public static String getUICode(Context co) {
		return co.getResources().getConfiguration().locale.getLanguage();
	}

	public static String getFilesPath(Context co) {
		return co.getFilesDir().getAbsolutePath();
	}

	public static void initColors(Activity ac) {
		CODE_COLOR_POS = ac.getResources().getColor(R.color.colorResultPos);
		CODE_COLOR_TEXT = ac.getResources().getColor(R.color.colorResultText);
		CODE_COLOR_MEAN = ac.getResources().getColor(R.color.colorResultMean);
	}
}