package ru.atomofiron.translator.Fragments;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.ArrayList;

import retrofit2.Response;
import ru.atomofiron.translator.Adapters.InputAdapter;
import ru.atomofiron.translator.Adapters.ListAdapter;
import ru.atomofiron.translator.Adapters.ViewPagerAdapter;
import ru.atomofiron.translator.App;
import ru.atomofiron.translator.CustomViews.ProgressView;
import ru.atomofiron.translator.Utils.Cache;
import ru.atomofiron.translator.Utils.I;
import ru.atomofiron.translator.R;
import ru.atomofiron.translator.Utils.AsyncCall;
import ru.atomofiron.translator.Utils.Base;
import ru.atomofiron.translator.Utils.Languages;
import ru.atomofiron.translator.Utils.Node;
import ru.atomofiron.translator.Utils.Retrofit.DetectResponse;
import ru.atomofiron.translator.Utils.Retrofit.Dictionary.Def;
import ru.atomofiron.translator.Utils.Retrofit.Dictionary.Ex;
import ru.atomofiron.translator.Utils.Retrofit.DictionaryResponse;
import ru.atomofiron.translator.Utils.Retrofit.Dictionary.Mean;
import ru.atomofiron.translator.Utils.Retrofit.Dictionary.Syn;
import ru.atomofiron.translator.Utils.Retrofit.Dictionary.Tr;
import ru.atomofiron.translator.Utils.Retrofit.Dictionary.Tr_;
import ru.atomofiron.translator.Utils.Retrofit.LangsResponse;
import ru.atomofiron.translator.Utils.Retrofit.TranslateResponse;
import ru.atomofiron.translator.Utils.SimpleAlphaAnimation;

public class MainFragment extends Fragment implements InputAdapter.OnInputListener, View.OnClickListener, AdapterView.OnItemClickListener {

	private static final int HISTORY_TAB_NUM = 0;
	private static final int TRANSLATE_TAB_NUM = 1;
	private static final int FAVORITES_TAB_NUM = 2;

	private View mainView = null;
	private Activity ac;
	private SharedPreferences sp;
	private Base base;

	private FloatingActionButton fab;
	private Button firstLangButton;
	private Button secondLangButton;
	private ImageButton favoriteButton;
	private ProgressView progressView;
	private LinearLayout resultContainer;
	private ViewPager viewPager;
	private ImageView catView;

	private Languages languages;
	private String currentFirstLangCode;
	private String currentSecondLangCode;
	private String inputPhrase;
	private String translatedPhrase;
	private InputAdapter inputAdapter;
	private ListAdapter historyAdapter;
	private ListAdapter favoriteAdapter;

	private Cache<Node, DictionaryResponse> cache = new Cache<>(10);

    public MainFragment() {}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		ac = getActivity();
		sp = I.SP(ac);
		base = new Base(ac);

		new AsyncCall(new AsyncCall.ProcessListener() {
			private Response response;

			/* нельзя вызывать (execute()) один и тот же объект запроса апи,
			   поэтому таймер в AsynkCall, а создание очередного запроса здесь */
			public boolean onBackgroundDone() {
				try {
					response = App.getApi().getLangs(I.API_KEY, I.getUICode(ac)).execute();
				} catch (Exception ignored) {
					return false;
				}
				return true;
			}
			public void onDone() {
				LangsResponse langsResponse = (LangsResponse) response.body();

				if (langsResponse.getCode() < 400)
					initTranslator(new Languages(langsResponse.getLangs()));
				else
					I.Toast(ac, R.string.error);

				progressView.hide();
			}
		}).execute();

		currentFirstLangCode = sp.getString(I.PREF_FIRST_LANG_CODE, "");
		currentSecondLangCode = sp.getString(I.PREF_SECOND_LANG_CODE, "");
	}


	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) { // вот щас вообще не до гранулированности коммитов

		if (mainView != null)
			return mainView;

		mainView = inflater.inflate(R.layout.fragment_main, container, false);

		fab = (FloatingActionButton) mainView.findViewById(R.id.fab);
		fab.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				if (viewPager.getCurrentItem() == 0)
					historyAdapter.clear();
				else
					favoriteAdapter.clear();

				fab.hide();
			}
		});
		fab.hide();

        return mainView;
    }

    private void init(View view) {
		progressView = (ProgressView) view.findViewById(R.id.progress_view);

		ViewPager inputPager = (ViewPager) view.findViewById(R.id.input_pager);
		inputAdapter = new InputAdapter(ac, inputPager);
		inputAdapter.setOnInputListener(this);
		inputPager.setAdapter(inputAdapter);

		firstLangButton = (Button) mainView.findViewById(R.id.first_language);
		secondLangButton = (Button) mainView.findViewById(R.id.second_language);
		firstLangButton.setOnClickListener(this);
		secondLangButton.setOnClickListener(this);
		View swapButton = mainView.findViewById(R.id.swap_langs);
		swapButton.setVisibility(View.VISIBLE);
		swapButton.setOnClickListener(this);

		favoriteButton = (ImageButton) mainView.findViewById(R.id.btn_bookmark);
		favoriteButton.setVisibility(View.VISIBLE);
		favoriteButton.setOnClickListener(this);

		LayoutInflater inflater = LayoutInflater.from(ac);

		final ListView historyListView = new ListView(ac);
		ScrollView scrollView = (ScrollView) inflater.inflate(R.layout.scroll_view_result, null, false);
		ListView favoriteListView = new ListView(ac);

		historyAdapter = new ListAdapter(ac, base, Node.TYPE.HISTORY);
		favoriteAdapter = new ListAdapter(ac, base, Node.TYPE.FAVORITE);
		historyListView.setAdapter(historyAdapter);
		favoriteListView.setAdapter(favoriteAdapter);
		historyListView.setOnItemClickListener(this);
		favoriteListView.setOnItemClickListener(this);

		ArrayList<View> viewList = new ArrayList<>();
		viewList.add(historyListView);
		viewList.add(scrollView);
		viewList.add(favoriteListView);

		ViewPagerAdapter pagerAdapter = new ViewPagerAdapter(ac, viewList);
		viewPager = (ViewPager) view.findViewById(R.id.view_pager);
		viewPager.setAdapter(pagerAdapter);
		viewPager.setCurrentItem(TRANSLATE_TAB_NUM);
		viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
			public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}
			public void onPageScrollStateChanged(int state) {}
			public void onPageSelected(int position) {
				if (position == HISTORY_TAB_NUM) {
					if (historyListView.getCount() > 0)
						fab.show();
				} else if (position == FAVORITES_TAB_NUM) {
					if (favoriteAdapter.getCount() > 0)
						fab.show();
				} else {
					fab.hide();
					updateLists();
				}
			}
		});
		((TabLayout) view.findViewById(R.id.tab_layout)).setupWithViewPager(viewPager);

		resultContainer = (LinearLayout) scrollView.findViewById(R.id.result_container);
		TextView yandexView = (TextView) view.findViewById(R.id.yandex_label);
		yandexView.setText(Html.fromHtml(ac.getString(R.string.yandex_label)));
		yandexView.setMovementMethod(LinkMovementMethod.getInstance());

		catView = (ImageView) inflater.inflate(R.layout.image_view_cat, resultContainer, false);
		resultContainer.addView(catView);
	}

	private void initTranslator(Languages languages) {
		this.languages = languages;

		if (currentFirstLangCode.isEmpty() || currentSecondLangCode.isEmpty()) {
			String code = I.getUICode(ac);
			currentFirstLangCode = languages.contains(code) ? code : "en";
			currentSecondLangCode = currentFirstLangCode.equals("en") ? "ru" : "en";
			saveCurrentLangs();
		}

		init(mainView);
		updateLangButtons();
	}

	@Override
	public void onStop() {
		super.onStop();

		updateLists();
	}

	private void updateLists() {
		if (historyAdapter != null) {
			historyAdapter.update();
			favoriteAdapter.update();
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		base.close();
	}




	private void showSelectingFirstLang() {
		new AlertDialog.Builder(ac)
				.setCancelable(true)
				.setSingleChoiceItems(languages.getStringArray(), languages.indexByCode(currentFirstLangCode), new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						currentFirstLangCode = languages.getDirByPosition(which);

						updateLangButtons();
						dialog.dismiss();
					}
				})
				.setPositiveButton(R.string.cancel, null)
				.create().show();
	}
	private void showSelectingSecondLang() {
		new AlertDialog.Builder(ac)
				.setCancelable(true)
				.setSingleChoiceItems(
						languages.getDirsNames(),
						languages.indexOfDir(currentSecondLangCode),
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
								currentSecondLangCode = languages.getDirByPosition(which);
								secondLangButton.setText(languages.getByCode(currentSecondLangCode).getName());

								dialog.dismiss();
							}
				})
				.setPositiveButton(R.string.cancel, null)
				.create().show();
	}
	private void swapLangs() {
		String code = currentFirstLangCode;
		currentFirstLangCode = currentSecondLangCode;
		currentSecondLangCode = code;

		updateLangButtons();

		if (translatedPhrase != null) {
			inputAdapter.setText(translatedPhrase);
			translate(translatedPhrase);
		}
	}

	private void updateLangButtons() {
		new SimpleAlphaAnimation(firstLangButton, secondLangButton).start(new SimpleAlphaAnimation.CallbackAnimHalfway() {
			public void onAnimHalfway(View... views) {
				firstLangButton.setText(languages.getByCode(currentFirstLangCode).getName());
				secondLangButton.setText(languages.getByCode(currentSecondLangCode).getName());
			}
		});
	}

	private void addToHistory() {
		Node node = getCurrentNode(Node.TYPE.HISTORY);
		if (node == null || node.getTranslation().isEmpty())
			return;

		base.put(node);
		historyAdapter.update();
	}

	private void addToFavorite(View v) {
		Node node = getCurrentNode(Node.TYPE.FAVORITE);
		if (node == null)
			return;

		if (base.contains(node)) {
			base.remove(node);
			v.setActivated(false);
		} else {
			base.put(node);
			v.setActivated(true);
		}

		favoriteAdapter.update();
	}

	private Node getCurrentNode(Node.TYPE type) {
		if (inputPhrase == null)
			return null;

		return new Node(inputPhrase, translatedPhrase, getCurrentLangs(), type);
	}

	private String getCurrentLangs() {
		return currentFirstLangCode + "-" + currentSecondLangCode;
	}

	private void saveCurrentLangs() {
		SharedPreferences.Editor ed = sp.edit();
		ed.putString(I.PREF_FIRST_LANG_CODE, currentFirstLangCode);
		ed.putString(I.PREF_SECOND_LANG_CODE, currentSecondLangCode);
		ed.apply();
	}

	private void clearResult() {
		translatedPhrase = null;
		resultContainer.removeAllViews();
		resultContainer.addView(catView);
	}

	private void checkIfFavorite() {
		favoriteButton.setActivated(base.contains(getCurrentNode(Node.TYPE.FAVORITE)));
	}

	private void updateLangsAndTranslate(final String text) {
		inputPhrase = text;
		progressView.show();

		new AsyncCall(new AsyncCall.ProcessListener() {
			private Response response;

			public boolean onBackgroundDone() {
				try {
					response = App.getApi().detect(I.API_KEY, text).execute();
				} catch (Exception ignored) {
					return false;
				}
				return true;
			}

			public void onDone() {
				DetectResponse detectResponse = (DetectResponse) response.body();
				if (detectResponse.getCode() == 200) {
					if (currentSecondLangCode.equals(detectResponse.getLang())) {
						translatedPhrase = null;
						swapLangs();
					}
					translate(text);
				} else {
					I.Toast(ac, R.string.error);
					progressView.hide();
				}
			}
		}).execute();
	}

	private void translate(final String value) {
		inputPhrase = value;

		if (wordInCache())
			return;

		progressView.show();
		translatedPhrase = null;

		new AsyncCall(new AsyncCall.ProcessListener() {
			private Response response;

			public boolean onBackgroundDone() {
				try {
					response = App.getApi().translate(I.API_KEY, value, currentSecondLangCode, "plain").execute();
				} catch (Exception ignored) {
					return false;
				}
				return true;
			}

			public void onDone() {
				TranslateResponse translateResponse = (TranslateResponse) response.body();

				if (translateResponse.getCode() != 200) {
					I.Toast(ac, R.string.error);
					progressView.hide();
					return;
				}

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
				for (String text : translateResponse.getText()) {
					if (textBuilder.length() > 0)
						textBuilder.append("\n");

					textBuilder.append(text);
				}

				translatedPhrase = textBuilder.toString();
				addToHistory();

				translateWord(value);
			}
		}).execute();
	}

	private void translateWord(final String value) {
		final String langs = getCurrentLangs();

		new AsyncCall(new AsyncCall.ProcessListener() {
			private Response response;

			public boolean onBackgroundDone() {
				try {
					response = App.getApi().translateWord(I.DIC_URL, I.API_KEY_DIC, langs, value, I.getUICode(ac)).execute();
				} catch (Exception ignored) {
					return false;
				}
				return true;
			}

			public void onDone() {
				DictionaryResponse dictionaryResponse = (DictionaryResponse) response.body();
				if (dictionaryResponse.getDef() != null) {
					addToCache(dictionaryResponse);
					showDictionary(dictionaryResponse);
				} else
					showTranslation();
			}
		}).execute();
	}

	private void showTranslation() {
		checkIfFavorite();

		new SimpleAlphaAnimation(resultContainer).start(new SimpleAlphaAnimation.CallbackAnimHalfway() {
			public void onAnimHalfway(View... views) {
				progressView.hide();

				printTranslation();
			}
		});
	}
	private void printTranslation() {
		resultContainer.removeAllViews();
		TextView textView = (TextView) LayoutInflater.from(ac)
				.inflate(R.layout.text_view_result_main, resultContainer, false);
		textView.setText(translatedPhrase);
		resultContainer.addView(textView);
	}
	private void showDictionary(final DictionaryResponse dictionaryResponse) {
		checkIfFavorite();

		new SimpleAlphaAnimation(resultContainer).start(new SimpleAlphaAnimation.CallbackAnimHalfway() {
			public void onAnimHalfway(View... views) {
				progressView.hide();

				parseTranslate(dictionaryResponse);
			}
		});
	}

	private boolean wordInCache() {
		Node node = getCurrentNode(Node.TYPE.HISTORY);

		if (cache.containsKey(node)) {
			translatedPhrase = cache.getKey(node).getTranslation();
			showDictionary(cache.get(node));
			return true;
		}
		return false;
	}

	private void addToCache(DictionaryResponse response) {
		Node node = getCurrentNode(Node.TYPE.HISTORY);

		cache.put(node, response);
	}

	private void parseTranslate(DictionaryResponse dictionaryResponse) {
		resultContainer.removeAllViews();

		LayoutInflater inflater = LayoutInflater.from(ac);

		String str;
		TextView textView = (TextView) inflater.inflate(R.layout.text_view_result_main, resultContainer, false);
		textView.setText(translatedPhrase);
		resultContainer.addView(textView);

		for (Def def : dictionaryResponse.getDef()) {
			CardView cardView = (CardView) inflater.inflate(R.layout.card_view_result, resultContainer, false);
			LinearLayout layout = new LinearLayout(ac);
			layout.setOrientation(LinearLayout.VERTICAL);

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
				swapLangs();
				break;
			case R.id.btn_bookmark:
				addToFavorite(v);
				break;
		}
	}

	@Override
	public void onInput(String text) {
		if (text.equals(inputPhrase))
			return;

		inputPhrase = text;
		favoriteButton.setActivated(false);

		if (wordInCache()) {
			if (!text.isEmpty())
				addToHistory();

			return;
		}

		if (!text.isEmpty()) {
			updateLangsAndTranslate(text);
			viewPager.setCurrentItem(TRANSLATE_TAB_NUM);
		} else
			clearResult();
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Node node = ((ListAdapter)parent.getAdapter()).getItem(position);
		String[] dir = node.getDirection().split("-");

		currentFirstLangCode = dir[0];
		currentSecondLangCode = dir[1];
		updateLangButtons();

		inputPhrase = node.getPhrase();
		translatedPhrase = node.getTranslation();

		viewPager.setCurrentItem(TRANSLATE_TAB_NUM);
		inputAdapter.setText(node.getPhrase());

		if (!wordInCache()) {
			printTranslation();
			translate(inputPhrase);
		}
	}
}
