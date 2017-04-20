package ru.atomofiron.translator.Fragments;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.TextView;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.atomofiron.translator.App;
import ru.atomofiron.translator.I;
import ru.atomofiron.translator.Adapters.InputAdapter;
import ru.atomofiron.translator.R;
import ru.atomofiron.translator.Utils.AsyncJob;
import ru.atomofiron.translator.Utils.Languages;
import ru.atomofiron.translator.Utils.Retrofit.DetectResponse;
import ru.atomofiron.translator.Utils.Retrofit.LangsResponse;
import ru.atomofiron.translator.Utils.Retrofit.TranslateResponse;
import ru.atomofiron.translator.Utils.SimpleAlphaAnimation;

public class MainFragment extends Fragment implements InputAdapter.OnInputListener, View.OnClickListener {

	private View mainView = null;
	private Activity ac;
	private SharedPreferences sp;
	private RecyclerView recyclerView;
	private Button firstLangButton;
	private Button secondLangButton;
	private TextView resultView;

	private InputAdapter inputAdapter;
	private Languages languages;
	private String currentFirstLangCode;
	private String currentSecondLangCode;

    public MainFragment() {}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		ac = getActivity();
		sp = I.SP(ac);

		/*new LanguagesLoader("ru", new LanguagesLoader.OnLoadedListener() {
			public void onLoaded(Languages languages) {
				initTranslator(languages);
			}
		}).execute();*/

		App.getApi().getLangs(I.API_KEY, "ru").enqueue(new Callback<LangsResponse>() {
			@Override
			public void onResponse(Call<LangsResponse> call, final Response<LangsResponse> response) {
				new AsyncJob(new AsyncJob.Job() { // для асинхронного парсинга и получения объекта Languages
					Languages languages;
					public void onAsyncJobStart() {
						LangsResponse langsResponse = response.body();
						languages = new Languages(langsResponse.getDirs(), langsResponse.getLangs());
					}
					public void onJobEnd() {
						initTranslator(this.languages);
					}
				}).execute();
			}

			@Override
			public void onFailure(Call<LangsResponse> call, Throwable t) {
				I.Loge("LangsResponse: " + t);
			}
		});

		currentFirstLangCode = sp.getString(I.PREF_FIRST_LANG_CODE, "");
		currentSecondLangCode = sp.getString(I.PREF_SECOND_LANG_CODE, "");

		if (currentFirstLangCode.isEmpty() || currentSecondLangCode.isEmpty()) {
			currentFirstLangCode = "en";
			currentSecondLangCode = "ru";
			saveCurrentLangs();
		}
	}

	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) { // вот щас вообще не до гранулированности коммитов

		if (mainView != null)
			return mainView;

		mainView = inflater.inflate(R.layout.fragment_main, container, false);

		initInput(mainView);

		firstLangButton = (Button) mainView.findViewById(R.id.first_language);
		secondLangButton = (Button) mainView.findViewById(R.id.second_language);
		mainView.findViewById(R.id.swap_langs).setOnClickListener(this);
		firstLangButton.setOnClickListener(this);
		secondLangButton.setOnClickListener(this);

        return mainView;
    }

    private void initInput(View view) {
		TextView yandexLabel = (TextView) view.findViewById(R.id.yandex_label);
		yandexLabel.setText(Html.fromHtml(getString(R.string.yandex_label)));
		yandexLabel.setMovementMethod(LinkMovementMethod.getInstance());

		recyclerView = (RecyclerView) view.findViewById(R.id.input_recycler_view);
		inputAdapter = new InputAdapter(recyclerView, I.getScreenWidth(ac));

		recyclerView.setLayoutManager
				(new LinearLayoutManager(ac, LinearLayoutManager.HORIZONTAL, false));
		recyclerView.setAdapter(inputAdapter);
		inputAdapter.setOnInputListener(this);

		resultView = (TextView) view.findViewById(R.id.result_view);

		inputAdapter.add("apple");
		inputAdapter.add("google");
		inputAdapter.add("one plus");
		inputAdapter.add("yandex");
	}

	private void initTranslator(Languages languages) {
		this.languages = languages;

		updateLangButtons();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.first_language:
				showSelectingFirstLang();
				break;
			case R.id.second_language:
				showSelectingSecondLang();
				break;
			case R.id.swap_langs:
				if (!swapLangs())
					I.Toast(ac, R.string.lang_hasnt_translation);
				break;
		}
	}

	private void updateLangButtons() {
		new SimpleAlphaAnimation(firstLangButton, secondLangButton).start(new SimpleAlphaAnimation.OnActionListener() {
			public void onAnimHalfway(View... views) {
				firstLangButton.setText(languages.getByCode(currentFirstLangCode).name);
				secondLangButton.setText(languages.getByCode(currentSecondLangCode).name);
			}
		});
	}

	private void showSelectingFirstLang() {
		new AlertDialog.Builder(ac)
				.setCancelable(true)
				.setSingleChoiceItems(languages.getStringArray(), languages.indexByCode(currentFirstLangCode), new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						Languages.Language lang = languages.get(which);
						currentFirstLangCode = lang.code;

						if (!lang.containsDir(currentSecondLangCode))
							currentSecondLangCode = lang.getDirsNames()[0];

						updateLangButtons();
						dialog.dismiss();
					}
				})
				.setPositiveButton(R.string.cancel, null)
				.create().show();
	}
	private void showSelectingSecondLang() {
		Languages.Language lang = languages.getByCode(currentFirstLangCode);
		new AlertDialog.Builder(ac)
				.setCancelable(true)
				.setSingleChoiceItems(
						lang.getDirsNames(),
						lang.indexOfDir(currentSecondLangCode),
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
								currentSecondLangCode = languages.getByCode(currentFirstLangCode).getDirByPosition(which);
								secondLangButton.setText(languages.getByCode(currentSecondLangCode).name);

								dialog.dismiss();
							}
				})
				.setPositiveButton(R.string.cancel, null)
				.create().show();
	}
	private boolean swapLangs() {
		if (!languages.getByCode(currentSecondLangCode).containsDir(currentFirstLangCode))
			return false;

		String code = currentFirstLangCode;
		currentFirstLangCode = currentSecondLangCode;
		currentSecondLangCode = code;

		updateLangButtons();
		return true;
	}

	private void addToHistory(String value) {
		// todo add to real history
		inputAdapter.add(value);
	}

	private void updateLangsAndTranslate(final String value) {
		App.getApi().detect(I.API_KEY, value).enqueue(new Callback<DetectResponse>() {
			@Override
			public void onResponse(Call<DetectResponse> call, Response<DetectResponse> response) {
				DetectResponse detectResponse = response.body();
				if (detectResponse != null && detectResponse.getCode() == 200) {
					if (currentSecondLangCode.equals(detectResponse.getLang()))
						swapLangs();

					translate(value);
				} else {
					I.Loge("DetectResponse code: "+response.code());
					I.Toast(ac, R.string.error);
				}
			}
			@Override
			public void onFailure(Call<DetectResponse> call, Throwable t) {
				I.Loge("DetectResponse: " + t);
			}
		});
	}

	private void translate(String value) {
		App.getApi().translate(I.API_KEY, value, currentSecondLangCode, "plain").enqueue(new Callback<TranslateResponse>() {
			@Override
			public void onResponse(Call<TranslateResponse> call, Response<TranslateResponse> response) {
				TranslateResponse translateResponse = response.body();
				if (translateResponse != null && translateResponse.getCode() == 200) {
					String[] langs = translateResponse.getLang().split("-");
					if (!currentFirstLangCode.equals(langs[0])) {
						if (currentFirstLangCode.equals(langs[1]) && currentSecondLangCode.equals(langs[0]))
							swapLangs();
						else {
							currentFirstLangCode = langs[0];
							currentSecondLangCode = langs[1];

							updateLangButtons();
						}
					}

					StringBuilder textBuilder = new StringBuilder("");
					for (String text : translateResponse.getText())
						textBuilder.append(Html.fromHtml(text)).append("\n");

					showText(textBuilder.toString());
				} else {
					I.Loge("TranslateResponse code: "+response.code());
					I.Toast(ac, R.string.error);
				}
			}
			@Override
			public void onFailure(Call<TranslateResponse> call, Throwable t) {
				I.Loge("TranslateResponse: " + t);
			}
		});
	}

	private void showText(final String fullText) {
		new SimpleAlphaAnimation(resultView).start(new SimpleAlphaAnimation.OnActionListener() {
			public void onAnimHalfway(View... views) {
				((TextView)views[0]).setText(fullText);
			}
		});
	}

	private void saveCurrentLangs() {
		SharedPreferences.Editor ed = sp.edit();
		ed.putString(I.PREF_FIRST_LANG_CODE, currentFirstLangCode);
		ed.putString(I.PREF_SECOND_LANG_CODE, currentSecondLangCode);
		ed.apply();
	}

	public void onInput(String text) {
		I.Log("onInput: "+text);
		if (text.isEmpty())
			return;

		addToHistory(text);
		updateLangsAndTranslate(text);
	}


}
