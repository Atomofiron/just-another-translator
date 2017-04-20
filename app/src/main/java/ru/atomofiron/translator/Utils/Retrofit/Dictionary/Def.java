
package ru.atomofiron.translator.Utils.Retrofit.Dictionary;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Def {

	@SerializedName("text")
	@Expose
	private String text;
	@SerializedName("pos")
	@Expose
	private String pos;
	@SerializedName("tr")
	@Expose
	private List<Tr> tr = null;

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getPos() {
		return pos;
	}

	public List<Tr> getTr() {
		return tr;
	}

	@Override
	public String toString() {
		return "{ text : "+ text + ", pos : " + pos +", tr : "+tr +" }";
	}
}