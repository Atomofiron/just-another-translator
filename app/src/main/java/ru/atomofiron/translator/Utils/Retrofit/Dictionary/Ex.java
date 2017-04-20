package ru.atomofiron.translator.Utils.Retrofit.Dictionary;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Ex {

	@SerializedName("text")
	@Expose
	private String text;
	@SerializedName("tr")
	@Expose
	private List<Tr_> tr = null;

	public String getText() {
		return text;
	}

	public List<Tr_> getTr() {
		return tr;
	}

	@Override
	public String toString() {
		return "{ text : "+text+", tr : "+tr+" }";
	}

}