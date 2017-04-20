package ru.atomofiron.translator.Utils.Retrofit.Dictionary;

import java.util.List;

import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Main {

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