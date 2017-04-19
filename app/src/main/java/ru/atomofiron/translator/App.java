package ru.atomofiron.translator;

import android.app.Application;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import ru.atomofiron.translator.Utils.Retrofit.Api;

public class App extends Application {


	private static Api api;
	private Retrofit retrofit;

	@Override
	public void onCreate() {
		super.onCreate();

		retrofit = new Retrofit.Builder()
				.baseUrl("https://translate.yandex.net/")
				.addConverterFactory(GsonConverterFactory.create())
				.build();

		api = retrofit.create(Api.class);
	}

	public static Api getApi() {
		return api;
	}
}
