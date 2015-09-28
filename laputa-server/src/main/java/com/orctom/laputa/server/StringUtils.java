package com.orctom.laputa.server;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils {

	public static Map<String, String> extractParams(String pattern, String withValues) {
		String keyPattern = pattern.replaceAll("", "");
		System.out.println("pattern = " + pattern);
		String valuePattern = pattern.replaceAll("\\{[^\\{]+\\}", "(.*)");
		Pattern p = Pattern.compile(valuePattern);
		Matcher m = p.matcher(withValues);
		if (m.matches()) {
			for (int i = 1; i <= m.groupCount(); i++) {
				System.out.println("group " + i + ": " + m.group(i));
			}
		}
		return null;
	}

	public static void main(String[] args) {
		StringUtils.extractParams("/products/{id}", "/products/234235234");
		StringUtils.extractParams("/products/{id}/attributes/{attid}", "/products/234235234/attributes/222222");
	}
}
