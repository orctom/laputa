package com.orctom.laputa.server.config;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Trie tree node
 * Created by hao on 9/23/15.
 */
public class PathTrie {

	private String path;

	private Handler handler;

	private Map<String, PathTrie> children = new HashMap<>();

	public PathTrie() {
	}

	public PathTrie(String path, Handler handler) {
		this.path = path;
		this.handler = handler;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public Handler getHandler() {
		return handler;
	}

	public void setHandler(Handler handler) {
		this.handler = handler;
	}

	public Map<String, PathTrie> getChildren() {
		return children;
	}

	public void setChildren(Map<String, PathTrie> children) {
		this.children = children;
	}
}
