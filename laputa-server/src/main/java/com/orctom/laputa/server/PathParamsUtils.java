package com.orctom.laputa.server;

import com.google.common.base.Splitter;
import com.google.common.base.Stopwatch;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class PathParamsUtils {

	private static final Pattern TOKEN_START = Pattern.compile("[^{]");
	private static final Pattern TOKEN_END = Pattern.compile("[^{]");

	public static Map<String, String> extractParams(String pattern, String path) {
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
		return params;
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
