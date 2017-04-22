package ru.atomofiron.translator.Utils.Retrofit;

import java.util.List;

import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import ru.atomofiron.translator.Utils.Retrofit.Dictionary.Def;

public class DictionaryResponse {

	@SerializedName("head")
	@Expose
	private JsonObject head;
	@SerializedName("def")
	@Expose
	private List<Def> def = null;

	public JsonObject getHead() {
		return head;
	}

	public List<Def> getDef() {
		return def;
	}

	@Override
	public String toString() {
		return def.toString();
	}
}