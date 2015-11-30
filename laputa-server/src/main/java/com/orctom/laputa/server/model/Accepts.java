package com.orctom.laputa.server.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * accepts in headers
 * Created by hao on 11/30/15.
 */
public class Accepts {

	private List<Tuple<String, String>> accepts;

	public Accepts(String accept) {
		String[] acceptArray = accept.split(",");
		int len = acceptArray.length;

		accepts = new ArrayList<>(len);

		for (String item : acceptArray) {
			int semiColonIndex = item.indexOf(";");
			if (semiColonIndex > 0) {
				String key = item.substring(0, semiColonIndex);
				String value = item.substring(semiColonIndex + 1);
				accepts.add(new Tuple<>(key, value));
			} else {
				accepts.add(new Tuple<>(item, "q=1.0"));
			}
		}
	}

	public List<String> getAccepts() {
		Collections.sort(accepts, (o1, o2) -> o2.getValue().compareTo(o1.getValue()));
		return accepts.stream().map(Tuple::getKey).collect(Collectors.toList());
	}
}
