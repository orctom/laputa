package com.orctom.laputa.server.config;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * Trie tree node
 * Created by hao on 9/23/15.
 */
public class PathTrie {

	private Handler handler;

	private Map<String, PathTrie> children = new HashMap<>();

	public PathTrie() {
	}

	public PathTrie(Handler handler) {
		this.handler = handler;
	}

	public PathTrie(String uri, Class<?> handlerClass, Method handlerMethod) {
		this.handler = new Handler(uri, handlerClass, handlerMethod);
	}

	public Handler getHandler() {
		return handler;
	}

	public Map<String, PathTrie> getChildren() {
		return children;
	}

	@Override
	public String toString() {
		StringBuilder str = new StringBuilder();
		str.append("/");
		for (Map.Entry<String, PathTrie> entry : children.entrySet()) {
			str.append("\n\t").append(entry.getKey()).append("\t").append(entry.getValue().toString());
		}

		return str.toString();
	}
}
