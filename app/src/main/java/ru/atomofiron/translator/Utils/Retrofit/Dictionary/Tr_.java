package ru.atomofiron.translator.Utils.Retrofit.Dictionary;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Tr_ {

	@SerializedName("text")
	@Expose
	private String text;

	public String getText() {
		return text;
	}

	@Override
	public String toString() {
		return "{ text : "+text+" }";
	}

}