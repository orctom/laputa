package com.orctom.laputa.server.config;

import com.google.common.base.Splitter;
import com.orctom.laputa.server.annotation.Path;
import com.orctom.laputa.util.ClassUtils;
import com.orctom.laputa.util.exception.ClassLoadingException;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Holding url mappings...
 * Created by hao on 9/21/15.
 */
public class MappingConfig {

	private static final MappingConfig INSTANCE = new MappingConfig();

	private Map<String, Handler> staticMappings = new HashMap<>();
	private PathTrie wildcardMappings = new PathTrie();

	private static final Pattern NORMALIZE = Pattern.compile("//");

	private MappingConfig() {}

	public static  MappingConfig getInstance() {
		return INSTANCE;
	}

	public Handler getHandler(String url) {
		Handler handler = staticMappings.get(url);
		if (null != handler) {
			return handler;
		}

		// TODO loop path trie tree
		return null;
	}

	public void scan(Class<? extends Annotation> annotation, String... basePackages) throws ClassLoadingException {
		List<Class<?>> services = new ArrayList<>();
		for (String packageName : basePackages) {
			services.addAll(ClassUtils.getClassesWithAnnotation(packageName, annotation));
		}
		for (Class<?> serviceClass : services) {
			loadingMappings(serviceClass);
		}
		for (Map.Entry<String, Handler> entry : staticMappings.entrySet()) {
			System.out.println(entry.getKey() + "   --->   " + entry.getValue().getHandlerMethod());
		}
	}

	private void loadingMappings(Class<?> clazz) {
		String basePath = "";
		if (clazz.isAnnotationPresent(Path.class)) {
			basePath = clazz.getAnnotation(Path.class).value();
		}

		for (Method  method : clazz.getMethods()) {
			Path path = method.getAnnotation(Path.class);
			if (null != path) {
				addToMappings(clazz, method, basePath + path.value());
			}
		}
	}

	private void addToMappings(Class<?> clazz, Method method, String rawPath) {
		String path = normalize(rawPath);
		if (path.contains("{")) {
			addToWildCardMappings(clazz, method, path);
		}

		staticMappings.put(path, new Handler(clazz, method));
	}

	private String normalize(String url) {
		return NORMALIZE.matcher("/" + url).replaceAll("/");
	}

	private void addToWildCardMappings(Class<?> clazz, Method method, String path) {
		String[] portions = path.split("/");
		if (0 == portions.length) {
			wildcardMappings.setPath("/");
			wildcardMappings.setHandler(new Handler(clazz, method));
		}

		PathTrie parent = wildcardMappings;
		for (int i = 1; i < portions.length; i++) {
			String portion = portions[i];
			PathTrie node = parent.getChildren().get(portion);

			if (null == node) {
				node = new PathTrie(portion, null);
				parent.getChildren().put(portion, node);
			}

			parent = node;

			boolean setHandler = i == portions.length - 1;
			if (setHandler) {
				node.setHandler(new Handler(clazz, method));
			}
		}
	}

}
