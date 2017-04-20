package ru.atomofiron.translator.Utils;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import ru.atomofiron.translator.I;

public class Languages {

	private final ArrayList<Language> languages = new ArrayList<>();

	public Languages(String jsonString) {

		try {
			JSONObject mainObject = new JSONObject(jsonString);
			JSONArray dirsArray = mainObject.getJSONArray("dirs");
			JSONObject langsObject = mainObject.getJSONObject("langs");

			Language lang;
			for (int i = 0; i < dirsArray.length(); i++) {
				String[] codes = dirsArray.getString(i).split("-");

				lang = getByCode(codes[0]);
				if (lang == null) {
					lang = new Language(codes[0], langsObject.getString(codes[0]));
					languages.add(lang);
				}
				lang.dirs.add(codes[1]);
			}

			checkIfNull();
		} catch (JSONException e) {
			I.Log(e.toString());
		}
	}

	public Languages(List<String> dirs, JsonObject langsObj) {
		ArrayList<String> codes = new ArrayList<>();
		for (Map.Entry<String, JsonElement> e : langsObj.entrySet()) {
			languages.add(new Language(e.getKey(), e.getValue().getAsString(), codes));
			codes.add(e.getKey());
		}
	}

	public int size() {
		return languages.size();
	}

	public String[] getStringArray() {
		String[] arr = new String[size()];
		for (int i = 0; i < arr.length; i++)
			arr[i] = languages.get(i).name;

		return arr;
	}

	public Language get(int i) {
		return languages.get(i);
	}

	public Language getByCode(String code) {
		for (Language lang : languages)
			if (lang.code.equals(code))
				return lang;

		return null;
	}

	public int indexByCode(String code) {
		for (int i = 0; i < size(); i++)
			if (code.equals(languages.get(i).code))
				return i;

		return -1;
	}

	private void checkIfNull() {
		// todo check this and remove
		for (Language l : languages)
			if (l.dirs.isEmpty())
				I.Log("! ! ! WTF: "+l.name+" - NO DIRS");
	}

	public class Language {
		public String code;
		public String name;
		private ArrayList<String> dirs = new ArrayList<>();

		Language(String code, String name) {
			this.code = code;
			this.name = name;
		}
		Language(String code, String name, ArrayList<String> dirs) {
			this.code = code;
			this.name = name;
			this.dirs = dirs;
		}

		public String[] getDirsNames() {
			String[] arr = new String[dirs.size()];

			for (int i = 0; i < arr.length; i++)
				arr[i] = getByCode(dirs.get(i)).name;

			return arr;
		}

		public boolean containsDir(String code) {
			return dirs.contains(code);
		}

		public int indexOfDir(String code) {
			return dirs.indexOf(code);
		}

		public String getDirByPosition(int i) {
			return dirs.get(i);
		}

		@Override
		public boolean equals(Object obj) {
			if (obj == null || !getClass().equals(obj.getClass()))
				return false;

			Language langObj = (Language) obj;

			// todo to consider a null fields

			// ignoring dirs comparing
			return code.equals(langObj.code) && name.equals(langObj.name);
		}

		@Override
		public String toString() {
			return code + "_" + name;
		}
	}

}
