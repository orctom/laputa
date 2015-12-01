package com.orctom.laputa.server;

import com.google.common.base.Splitter;
import com.google.common.base.Stopwatch;
import com.google.common.base.Strings;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PathParamsUtils {

	private static final Pattern TOKEN_START = Pattern.compile("[^{]");
	private static final Pattern TOKEN_END = Pattern.compile("[^}]");

	public static Optional<Map<String, String>> extractParams(String pattern, String path, String queryStr) {
		Optional<Map<String, String>> pathParams = extractParams(pattern, path);
		Optional<Map<String, String>> queryParams = extractParams(queryStr);

		if (!pathParams.isPresent() && !queryParams.isPresent()) {
			return Optional.empty();
		}

		Map<String, String> params = new HashMap<>();
		if (pathParams.isPresent()) {
			params.putAll(pathParams.get());
		}
		if (queryParams.isPresent()) {
			params.putAll(queryParams.get());
		}

		return Optional.of(params);
	}

	public static Optional<Map<String, String>> extractParams(String pattern, String path) {
		if (!TOKEN_START.matcher(pattern).matches()) {
			return Optional.empty();
		}

		Map<String, String> params = new HashMap<>();
		String[] patternItems = pattern.split("/");
		String[] pathItems = path.split("/");
		for (int i = 0; i < patternItems.length; i++) {
			String patternItem = patternItems[i];
			String pathItem = pathItems[i];

			int patternItemLen = patternItem.length();
			if (0 == patternItemLen) {
				continue;
			}

			int tokenStartIndex = patternItem.indexOf("{");
			if (tokenStartIndex < 0) {
				continue;
			}

			int tokenEndIndex = patternItem.indexOf("}");
			String varName = patternItem.substring(tokenStartIndex + 1, tokenEndIndex);
			int varValueEndIndex = pathItem.length() - (patternItemLen - tokenEndIndex) + 1;
			String varValue = pathItem.substring(tokenStartIndex, varValueEndIndex);
			params.put(varName, varValue);
		}
		return Optional.ofNullable(params);
	}

	public static Optional<Map<String, String>> extractParams(String queryStr) {
		if (Strings.isNullOrEmpty(queryStr)) {
			return Optional.empty();
		}

		Map<String, String> params = Arrays.stream(queryStr.split("&"))
				.map(item -> item.split("="))
				.collect(Collectors.toMap(p -> p[0], p -> p[1]));
		return Optional.ofNullable(params);
	}

	public static void validate(String pattern) {
		Splitter.on("/").omitEmptyStrings().split(pattern).forEach(item -> {
			if (TOKEN_START.matcher(item).replaceAll("").length() > 1) {
				throw new IllegalArgumentException("Unsupported URL pattern: " + pattern);
			}
			if (TOKEN_END.matcher(item).replaceAll("").length() > 1) {
				throw new IllegalArgumentException("Unsupported URL pattern: " + pattern);
			}
		});
	}
}
