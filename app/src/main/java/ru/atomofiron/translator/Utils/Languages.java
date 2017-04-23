package ru.atomofiron.translator.Utils;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.Map;

// парсинг полученного списка языков
public class Languages {
	private final ArrayList<Language> languages = new ArrayList<>();
	private final ArrayList<String> dirs = new ArrayList<>();

	public Languages(JsonObject langsObj) {
		for (Map.Entry<String, JsonElement> e : langsObj.entrySet()) {
			languages.add(new Language(e.getKey(), e.getValue().getAsString()));
			dirs.add(e.getKey());
		}
	}

	public int size() {
		return languages.size();
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

	public int indexOfDir(String code) {
		return dirs.indexOf(code);
	}

	public String getDirByPosition(int i) {
		return dirs.get(i);
	}

	public String[] getDirsNames() {
		String[] arr = new String[dirs.size()];

		for (int i = 0; i < arr.length; i++)
			arr[i] = getByCode(dirs.get(i)).name;

		return arr;
	}

	public String[] getStringArray() {
		String[] arr = new String[size()];
		for (int i = 0; i < arr.length; i++)
			arr[i] = languages.get(i).name;

		return arr;
	}

	public boolean contains(String code) {
		return indexByCode(code) != -1;
	}

	public class Language {
		String code;
		String name;

		Language(String code, String name) {
			this.code = code == null ? "" : code;
			this.name = name == null ? "" : name;
		}

		public String getCode() {
			return code;
		}

		public String getName() {
			return name;
		}

		@Override
		public boolean equals(Object obj) {
			if (obj == null || !getClass().equals(obj.getClass()))
				return false;

			// ignoring dirs comparing
			Language langObj = (Language) obj;
			return code.equals(langObj.code) && name.equals(langObj.name);
		}
	}

}
