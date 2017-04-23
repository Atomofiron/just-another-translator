package ru.atomofiron.translator.Utils;

import java.util.ArrayList;

/**
 * Кэширование результатов перевода через Яндекс Словарь.
 * Сравнение ключей не по хэшу, а по Object.equals().
 *
 * @param <K>  Тип ключа.
 * @param <V>  Тип значения.
 */
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

	/**
	 * Возвращает ключ, который равен передаваемуму посредством Object.equals().
	 *
	 * @param key  Ключ.
	 * @return     Другой ключ.
	 */
	// Костыль тот ещё, см. Node.equals()
	public K getKey(K key) {
		if (key == null)
			return null;

		int n = keys.indexOf(key);
		return n == -1 ? null : keys.get(n);
	}

	/**
	 * Добавляет значение в кэш, или обновляет, если ключ уже содержится.
	 *
	 * @param key    Ключ.
	 * @param value  Значеие.
	 * @return       Значение добавлено успешно.
	 */
	public boolean put(K key, V value) {
		if (key == null || value == null)
			return false;

		int n = keys.indexOf(key);

		if (n == -1) {
			keys.add(key);
			values.add(value);

			trim();
		} else {
			values.remove(n);
			values.add(n, value);
		}
		return true;
	}

	private void trim() {
		while (keys.size() > limit) {
			keys.remove(0);
			values.remove(0);
		}
	}

	public boolean containsKey(K key) {
		return keys.indexOf(key) != -1;
	}

}
