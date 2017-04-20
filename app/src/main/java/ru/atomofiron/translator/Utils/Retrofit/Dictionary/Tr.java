package ru.atomofiron.translator.Utils.Retrofit.Dictionary;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Tr {

	@SerializedName("text")
	@Expose
	private String text;
	@SerializedName("pos")
	@Expose
	private String pos;
	@SerializedName("syn")
	@Expose
	private List<Syn> syn = null;
	@SerializedName("mean")
	@Expose
	private List<Mean> mean = null;
	@SerializedName("ex")
	@Expose
	private List<Ex> ex = null;

	public String getText() {
		return text;
	}

	public String getPos() {
		return pos;
	}

	public List<Syn> getSyn() {
		return syn;
	}

	public List<Mean> getMean() {
		return mean;
	}

	public List<Ex> getEx() {
		return ex;
	}

	@Override
	public String toString() {
		return "{ text : " + text + ", pos : "+pos+", syn : "+syn+", mean : "+mean+", ex : "+ex+" }";
	}
}