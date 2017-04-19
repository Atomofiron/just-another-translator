package ru.atomofiron.translator.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import ru.atomofiron.translator.I;

public class Languages {

	private final Map<String, Language> languages = new HashMap<>();

	public Languages(String jsonString) {

		try {
			JSONObject mainObject = new JSONObject(jsonString);
			JSONArray dirsArray = mainObject.getJSONArray("dirs");
			JSONObject langsObject = mainObject.getJSONObject("langs");

			Language lang;
			for (int i = 0; i < dirsArray.length(); i++) {
				String[] codes = dirsArray.getString(i).split("-");

				if (languages.containsKey(codes[0])) {
					lang = languages.get(codes[0]);
				} else {
					lang = new Language(codes[0], langsObject.getString(codes[0]));
					languages.put(codes[0], lang);
				}
				lang.dirs.add(codes[1]);
			}
		} catch (JSONException e) {
			I.Log(e.toString());
		}
	}

	public int size() {
		return languages.size();
	}

	public ArrayList<String> getDirs(String code) {
		// languages always contains code
		return languages.get(code).dirs;
	}

	public Map<String, Language> getLanguages() {
		return languages;
	}

	public class Language {
		String code;
		String lang;
		final ArrayList<String> dirs = new ArrayList<>();

		Language(String code, String lang) {
			this.code = code;
			this.lang = lang;
		}

		@Override
		public boolean equals(Object obj) {
			if (obj == null || !getClass().equals(obj.getClass()))
				return false;

			Language langObj = (Language) obj;

			// todo to consider a null fields

			return code.equals(langObj.code) && lang.equals(langObj.lang);
		}

		@Override
		public String toString() {
			return code + "_" + lang;
		}
	}

}
