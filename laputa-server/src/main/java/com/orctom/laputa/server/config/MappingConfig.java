package com.orctom.laputa.server.config;

import com.google.common.base.Strings;
import com.orctom.laputa.server.annotation.*;
import com.orctom.laputa.util.ClassUtils;
import com.orctom.laputa.util.exception.ClassLoadingException;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Holding url mappings...
 * Created by hao on 9/21/15.
 */
public class MappingConfig {

	private static final MappingConfig INSTANCE = new MappingConfig();

	private Map<String, Handler> staticMappings = new HashMap<>();
	private PathTrie wildcardMappings = new PathTrie();

	private static final Pattern PATTERN_DOUBLE_SLASHES = Pattern.compile("//");
	private static final Pattern PATTERN_TAIL_SLASH = Pattern.compile("/$");
	private static final String KEY_PATH_PARAM = "{*}";

	private MappingConfig() {}

	public static  MappingConfig getInstance() {
		return INSTANCE;
	}

	public Handler getHandler(String uri, HTTPMethod httpMethod) {
		Handler handler = staticMappings.get(uri + "/" + httpMethod.getKey());
		if (null != handler) {
			return handler;
		}

		return getHandlerForWildcardUri(uri, httpMethod);
	}

	/**
	 * There are 4 types of `paths`:<br/>
	 * <li>1) static at middle of the uri</li>
	 * <li>2) static at end of the uri</li>
	 * <li>3) dynamic at middle of the uri</li>
	 * <li>4) dynamic at end of the uri</li>
	 */
	private Handler getHandlerForWildcardUri(String uri, HTTPMethod httpMethod) {
		String[] paths = uri.split("/");
		if (2 < paths.length) {
			return null;
		}

		PathTrie parent = wildcardMappings;

		for (int i = 1; i < paths.length; i++) {
			String path = paths[i];
			boolean isEndPath = i == paths.length - 1;

			// 1) and 2)
			PathTrie child = parent.getChildren().get(path);
			if (null != child) {
				if (isEndPath) {
					return child.getChildren().get(httpMethod.getKey()).getHandler();
				}
				parent = child;
				continue;
			}

			// 3) and 4)
			child = parent.getChildren().get(KEY_PATH_PARAM);
			if (null != child) {
				if (isEndPath) {
					return child.getChildren().get(httpMethod.getKey()).getHandler();
				}
				parent = child;
			}
		}

		return null;
	}

	public void scan(Class<? extends Annotation> annotation, String... basePackages) throws ClassLoadingException {
		List<Class<?>> services = new ArrayList<>();
		for (String packageName : basePackages) {
			services.addAll(ClassUtils.getClassesWithAnnotation(packageName, annotation));
		}
		for (Class<?> serviceClass : services) {
			configureMappings(serviceClass);
		}

		System.out.println("static mappings:");
		for (Handler handler : staticMappings.values()) {
			System.out.println(handler.toString());
		}
		System.out.println("dynamic mappings:");
		System.out.println(wildcardMappings.toString());
	}

	private void configureMappings(Class<?> clazz) {
		String basePath = "";
		if (clazz.isAnnotationPresent(Path.class)) {
			basePath = clazz.getAnnotation(Path.class).value();
		}

		for (Method  method : clazz.getMethods()) {
			Path path = method.getAnnotation(Path.class);
			HTTPMethod httpMethod = getHttpMethod(method);
			if (null != path) {
				String uri = path.value();
				if (Strings.isNullOrEmpty(uri)) {
					throw new IllegalArgumentException(
							"Empty value of Path annotation on " + clazz.getCanonicalName() + " " + method.getName());
				}
				addToMappings(clazz, method, basePath + path.value(), httpMethod);
			}
		}
	}

	private HTTPMethod getHttpMethod(Method method) {
		if (method.isAnnotationPresent(POST.class)) {
			return HTTPMethod.POST;
		}
		if (method.isAnnotationPresent(PUT.class)) {
			return HTTPMethod.PUT;
		}
		if (method.isAnnotationPresent(DELETE.class)) {
			return HTTPMethod.DELETE;
		}
		if (method.isAnnotationPresent(HEAD.class)) {
			return HTTPMethod.HEAD;
		}
		if (method.isAnnotationPresent(OPTIONS.class)) {
			return HTTPMethod.OPTIONS;
		}
		return HTTPMethod.GET;
	}

	private void addToMappings(Class<?> clazz, Method method, String rawPath, HTTPMethod httpMethod) {
		String uri = normalize(rawPath);
		if (uri.contains("{")) {
			addToWildCardMappings(clazz, method, uri, httpMethod);
		}

		staticMappings.put(uri + "/" + httpMethod.getKey(), new Handler(uri, clazz, method));
	}

	private String normalize(String uri) {
		uri = "/" + uri;
		uri = PATTERN_DOUBLE_SLASHES.matcher(uri).replaceAll("/");
		uri = PATTERN_TAIL_SLASH.matcher(uri).replaceAll("");
		return uri;
	}

	private void addToWildCardMappings(Class<?> clazz, Method method, String uri, HTTPMethod httpMethod) {
		String[] paths = uri.split("/");

		if (paths.length < 2) {
			return;
		}

		PathTrie parent = wildcardMappings;
		for (int i = 1; i < paths.length; i++) {
			String path = paths[i];
			boolean containsParam = path.contains("{");
			PathTrie child = parent.getChildren().get(path);

			if (null == child) {
				child = new PathTrie();
				parent.getChildren().put(containsParam ? KEY_PATH_PARAM : path, child);
			}

			parent = child;

			boolean setHandler = i == paths.length - 1;
			if (setHandler) {
				PathTrie leaf = new PathTrie(uri, clazz, method);
				child.getChildren().put(httpMethod.getKey(), leaf);
			}
		}
	}

}
