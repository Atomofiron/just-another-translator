package ru.atomofiron.translator.Utils;

public class Node {

	public static final String typeHistory = "history";
	public static final String typeFavorite = "favorite";

	public String title;
	public String subtitle;
	public String dir;
	public boolean isHistory;

	public Node(String title, String subtitle, String dir, String type) {
		this.title = title;
		this.subtitle = subtitle;
		this.dir = dir;
		this.isHistory = typeHistory.equals(type);
	}

	public String getType() {
		return isHistory ? typeHistory : typeFavorite;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null || !getClass().equals(obj.getClass()))
			return false;

		Node node = (Node) obj;

		return title.equals(node.title) && subtitle.equals(node.subtitle) && dir.equals(node.dir);
	}

	@Override
	public String toString() {
		return "Node : { " + title + " : " + subtitle + " : " + dir + " : " + getType() + " }";
	}
}
