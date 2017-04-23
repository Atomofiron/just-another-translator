package ru.atomofiron.translator.Utils.Retrofit;
import java.util.List;

import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Для чтения данных, полученных от API Яндекс.Переводчика.
 */
public class LangsResponse {

	@SerializedName("dirs")
	@Expose
	private List<String> dirs = null;
	@SerializedName("langs")
	@Expose
	private JsonObject langs = null;

	public List<String> getDirs() {
		return dirs;
	}

	public JsonObject getLangs() {
		return langs;
	}

}