package ru.atomofiron.translator.Utils;

import java.util.ArrayList;

import ru.atomofiron.translator.Utils.Retrofit.DictionaryResponse;

public class Cache<K, V> {
	private final ArrayList<K> keys = new ArrayList<>();
	private final ArrayList<V> values = new ArrayList<>();

	private int limit;

	public Cache(int limit) {
		this.limit = limit;
	}

	public V get(K key) {
		if (key == null)
			return null;

		int n = keys.indexOf(key);

		if (n == -1)
			return null;

		return values.get(n);
	}

	public K getKey(K key) {
		if (key == null)
			return null;

		int n = keys.indexOf(key);
		return n == -1 ? null : keys.get(n);
	}

	public boolean put(K key, V value) {
		if (key == null || value == null)
			return false;

		int n = keys.indexOf(key);

		if (n == -1) {
			keys.add(key);
			values.add(value);

			if (keys.size() > limit) {
				keys.remove(0);
				values.remove(0);
			}
		} else {
			values.remove(n);
			values.add(n, value);
		}
		return true;
	}

	public boolean containsKey(K key) {
		return keys.indexOf(key) != -1;
	}

}
