package ru.atomofiron.translator.Utils;

import android.support.annotation.NonNull;

/**
 * Для хранения результов переводов.
 */
public class Node {
	public enum TYPE { HISTORY, FAVORITE }

	private String phrase;
	private String translation;
	private String direction;
	private TYPE type;

	public Node(String phrase, String translation, String direction, @NonNull TYPE type) {
		this.phrase = phrase == null ? "" : phrase;
		this.translation = translation == null ? "" : translation;
		this.direction = direction == null ? "" : direction;
		this.type = type;
	}

	public String getPhrase() {
		return phrase;
	}

	public String getTranslation() {
		return translation;
	}

	public String getDirection() {
		return direction;
	}

	public TYPE getType() {
		return type;
	}

	public String getTypeString() {
		return type.toString();
	}

	public boolean isHistory() {
		return type.equals(TYPE.HISTORY);
	}

	/* поле translation игнорируется.
	   так сделано, чтобы можно было находить объект, у которого отличается поле с переводом */
	@Override
	public boolean equals(Object obj) {
		if (obj == null || !getClass().equals(obj.getClass()))
			return false;

		Node node = (Node) obj;
		return phrase.equals(node.phrase) &&
				direction.equals(node.direction) &&
				type.equals(node.type);
	}

	@Override
	public String toString() {
		return phrase+"_"+translation+"_"+direction+"_"+type;
	}
}
