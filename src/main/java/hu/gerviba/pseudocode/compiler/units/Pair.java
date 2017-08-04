package hu.gerviba.pseudocode.compiler.units;

import java.util.LinkedList;

public class Pair<K, V> {
	
	private final K key;
	private final V value;
	
	public Pair(K key, V value) {
		this.key = key;
		this.value = value;
	}

	public K getKey() {
		return key;
	}

	public V getValue() {
		return value;
	}

	@SafeVarargs
	public static <T> LinkedList<T> asList(T... elements) {
		LinkedList<T> list = new LinkedList<>();
		for (T e : elements) {
			list.add(e);
		}
		return list;
	}
	
}
