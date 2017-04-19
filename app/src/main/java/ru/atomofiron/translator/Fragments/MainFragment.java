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
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import ru.atomofiron.translator.CustomViews.ExEditText;
import ru.atomofiron.translator.I;
import ru.atomofiron.translator.Adapters.InputAdapter;
import ru.atomofiron.translator.R;
import ru.atomofiron.translator.Utils.Languages;
import ru.atomofiron.translator.Utils.LanguagesLoader;

public class MainFragment extends Fragment implements InputAdapter.OnSlideListener, TextView.OnEditorActionListener, View.OnClickListener {

	private View mainView = null;
	private Activity ac;
	private SharedPreferences sp;
	private ExEditText currentEditText = null;
	private RecyclerView recyclerView;
	private InputAdapter inputAdapter;
	private Languages languages;
	private String currentFirstLangCode;
	private String currentSecondLangCode;
	private Button firstLangButton;
	private Button secondLangButton;

    public MainFragment() {}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		ac = getActivity();
		sp = I.SP(ac);

		new LanguagesLoader("ru", new LanguagesLoader.OnLoadedListener() {
			public void onLoaded(Languages languages) {
				initTranslator(languages);
			}
		}).execute();

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
		inputAdapter.setOnSlideListener(this);

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
		firstLangButton.setText(languages.getByCode(currentFirstLangCode).name);
		secondLangButton.setText(languages.getByCode(currentSecondLangCode).name);
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

	@Override
	public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
		String value = currentEditText.getText().toString();
		addToHistory(value);
		updateTranslation(value);

		return false;
	}

	private void addToHistory(String value) {
		// todo add to real history
		inputAdapter.add(value);
	}

	private void updateTranslation(String value) {
		// todo translate
	}

	private void saveCurrentLangs() {
		SharedPreferences.Editor ed = sp.edit();
		ed.putString(I.PREF_FIRST_LANG_CODE, currentFirstLangCode);
		ed.putString(I.PREF_SECOND_LANG_CODE, currentSecondLangCode);
		ed.apply();
	}

	@Override
	public void onSlide(ExEditText currentView) {
		currentEditText = currentView;
		currentEditText.setOnEditorActionListener(this);
		I.Log("currentEditText: "+currentEditText.getText().toString());
		currentView.requestFocus();
		currentView.setSelection(currentView.length());
	}


}
