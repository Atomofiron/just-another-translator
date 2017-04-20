package ru.atomofiron.translator.Fragments;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.atomofiron.translator.App;
import ru.atomofiron.translator.CustomViews.ProgressView;
import ru.atomofiron.translator.I;
import ru.atomofiron.translator.Adapters.InputAdapter;
import ru.atomofiron.translator.R;
import ru.atomofiron.translator.Utils.AsyncJob;
import ru.atomofiron.translator.Utils.Languages;
import ru.atomofiron.translator.Utils.Retrofit.DetectResponse;
import ru.atomofiron.translator.Utils.Retrofit.Dictionary.Def;
import ru.atomofiron.translator.Utils.Retrofit.Dictionary.Ex;
import ru.atomofiron.translator.Utils.Retrofit.Dictionary.Main;
import ru.atomofiron.translator.Utils.Retrofit.Dictionary.Mean;
import ru.atomofiron.translator.Utils.Retrofit.Dictionary.Syn;
import ru.atomofiron.translator.Utils.Retrofit.Dictionary.Tr;
import ru.atomofiron.translator.Utils.Retrofit.Dictionary.Tr_;
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
	private ProgressView progressView;
	private LinearLayout resultContainer;

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

		/*new LanguagesLoader(I.getUICode(ac), new LanguagesLoader.OnLoadedListener() {
			public void onLoaded(Languages languages) {
				initTranslator(languages);
			}
		}).execute();*/

		App.getApi().getLangs(I.API_KEY, I.getUICode(ac)).enqueue(new Callback<LangsResponse>() {
			@Override
			public void onResponse(Call<LangsResponse> call, final Response<LangsResponse> response) {
				new AsyncJob(new AsyncJob.Job() { // для асинхронного парсинга и получения объекта Languages
					Languages languages;
					public void onAsyncJobStart() {
						LangsResponse langsResponse = response.body();
						languages = new Languages(langsResponse.getLangs());
					}
					public void onJobEnd() {
						initTranslator(this.languages);
					}
				}).execute();
			}

			@Override
			public void onFailure(Call<LangsResponse> call, Throwable t) {
				I.Loge("LangsResponse: " + t);
				progressView.hide();
			}
		});

		currentFirstLangCode = sp.getString(I.PREF_FIRST_LANG_CODE, "");
		currentSecondLangCode = sp.getString(I.PREF_SECOND_LANG_CODE, "");

		if (currentFirstLangCode.isEmpty() || currentSecondLangCode.isEmpty()) {
			String code = I.getUICode(ac);
			currentFirstLangCode = languages.contains(code) ? code : "en";
			currentSecondLangCode = currentFirstLangCode.equals("en") ? "ru" : "en";
			saveCurrentLangs();
		}
	}

	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) { // вот щас вообще не до гранулированности коммитов

		if (mainView != null)
			return mainView;

		mainView = inflater.inflate(R.layout.fragment_main, container, false);

        return mainView;
    }

    private void init(View view) {
		TextView yandexLabel = (TextView) view.findViewById(R.id.yandex_label);
		yandexLabel.setText(Html.fromHtml(getString(R.string.yandex_label)));
		yandexLabel.setMovementMethod(LinkMovementMethod.getInstance());

		recyclerView = (RecyclerView) view.findViewById(R.id.input_recycler_view);
		inputAdapter = new InputAdapter(recyclerView, I.getScreenWidth(ac));

		recyclerView.setLayoutManager
				(new LinearLayoutManager(ac, LinearLayoutManager.HORIZONTAL, false));
		recyclerView.setAdapter(inputAdapter);
		inputAdapter.setOnInputListener(this);

		resultContainer = (LinearLayout) view.findViewById(R.id.result_container);

		inputAdapter.add("apple");
		inputAdapter.add("google");
		inputAdapter.add("one plus");
		inputAdapter.add("yandex");

		firstLangButton = (Button) mainView.findViewById(R.id.first_language);
		secondLangButton = (Button) mainView.findViewById(R.id.second_language);
		mainView.findViewById(R.id.swap_langs).setOnClickListener(this);
		firstLangButton.setOnClickListener(this);
		secondLangButton.setOnClickListener(this);

		progressView = (ProgressView) view.findViewById(R.id.progress_view);
	}

	private void initTranslator(Languages languages) {
		this.languages = languages;

		init(mainView);
		updateLangButtons();
		progressView.hide();
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
		progressView.show();

		App.getApi().detect(I.API_KEY, value).enqueue(new Callback<DetectResponse>() {
			@Override
			public void onResponse(Call<DetectResponse> call, Response<DetectResponse> response) {
				DetectResponse detectResponse = response.body();
				if (detectResponse != null && detectResponse.getCode() == 200) {
					if (currentSecondLangCode.equals(detectResponse.getLang()))
						swapLangs();

					translate2(value);
				} else {
					I.Loge("DetectResponse code: "+response.code());
					I.Toast(ac, R.string.error);
				}

				progressView.hide();
			}
			@Override
			public void onFailure(Call<DetectResponse> call, Throwable t) {
				I.Loge("DetectResponse: " + t);
				progressView.hide();
			}
		});
	}

	private void translate2(String value) {
		progressView.show();

		String langs = currentFirstLangCode + "-" + currentSecondLangCode;
		App.getApi().translate2(I.DIC_URL, I.API_KEY_DIC, langs, value, I.getUICode(ac)).enqueue(new Callback<Main>() {
			@Override
			public void onResponse(Call<Main> call, Response<Main> response) {
				if (response.isSuccessful())
					showFullText(response.body());
				else
					I.Toast(ac, R.string.error);
			}
			@Override
			public void onFailure(Call<Main> call, Throwable t) {
				I.Log("onFailure: " + t);
				I.Toast(ac, R.string.error);
			}
		});
	}
	private void translate(String value) {
		progressView.show();

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

				progressView.hide();
			}
			@Override
			public void onFailure(Call<TranslateResponse> call, Throwable t) {
				I.Loge("TranslateResponse: " + t);
				progressView.hide();
			}
		});
	}

	private void showText(final String fullText) {
		new SimpleAlphaAnimation(resultContainer).start(new SimpleAlphaAnimation.OnActionListener() {
			public void onAnimHalfway(View... views) {
				((TextView)views[0]).setText(fullText);
			}
		});
	}
	private void showFullText(final Main main) {
		I.Log(main.toString());
		new SimpleAlphaAnimation(resultContainer).start(new SimpleAlphaAnimation.OnActionListener() {
			public void onAnimHalfway(View... views) {
				resultContainer.removeAllViews();

				LayoutInflater inflater = LayoutInflater.from(ac);


				String str;
				if ((str = main.getDef().get(0).getTr().get(0).getText()) != null) {
					TextView textView = (TextView) inflater.inflate(R.layout.text_view_result_main, resultContainer, false);
					textView.setText(str);
					resultContainer.addView(textView);
				}

				for (Def def : main.getDef()) {
					CardView cardView = (CardView) inflater.inflate(R.layout.card_view_result, resultContainer, false);
					LinearLayout layout = new LinearLayout(ac);
					layout.setOrientation(LinearLayout.VERTICAL);
					TextView textView;

					for (Tr tr : def.getTr()) {
						if ((str = tr.getPos()) != null) {
							textView = (TextView) inflater.inflate(R.layout.text_view_result_pos, cardView, false);
							textView.setText(str);
							layout.addView(textView);
						}
						textView = null;

						if ((str = tr.getText()) != null) {
							textView = (TextView) inflater.inflate(R.layout.text_view_result_syn, cardView, false);
							textView.setText(str);
						}

						if (tr.getSyn() != null) {
							if (textView == null)
								textView = (TextView) inflater.inflate(R.layout.text_view_result_syn, cardView, false);

							for (Syn s : tr.getSyn()) {
								if (s != null && (str = s.getText()) != null) {
									if (textView.length() > 0)
										textView.append(", ");

									textView.append(str);
								}
							}
						}
						if (textView != null)
							layout.addView(textView);

						if (tr.getMean() != null) {
							textView = (TextView) inflater.inflate(R.layout.text_view_result_mean, cardView, false);
							textView.setText("( ");

							for (Mean m : tr.getMean())
								if ((str = m.getText()) != null) {
									textView.append(str);
									textView.append(" ");
								}

							textView.append(")");

							if (textView.length() > 3)
								layout.addView(textView);
						}

						if (tr.getEx() != null) {
							for (Ex e : tr.getEx()) {
								textView = (TextView) inflater.inflate(R.layout.text_view_result_text, cardView, false);

								textView.setText(e.getText() + " - ");

								for (Tr_ t : e.getTr())
									if ((str = t.getText()) != null)
										textView.append(str + ", ");

								if (!textView.getText().equals("null - "))
									layout.addView(textView);
							}
						}
					}

					cardView.addView(layout);
					resultContainer.addView(cardView);
				}
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
