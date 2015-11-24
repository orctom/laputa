package com.orctom.laputa.server.test;

import com.google.common.base.Stopwatch;
import com.orctom.laputa.server.PathParamsUtils;
import org.junit.Test;

import java.util.Map;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.*;

public class PathParamsUtilTest {

	@Test
	public void testExtractParams1() {
		String pattern = "/products/{id}";
		String path = "/products/234235234";
		Stopwatch sw = Stopwatch.createStarted();
		Map<String, String> params = PathParamsUtils.extractParams(pattern, path);
		sw.stop();
		System.out.println(sw.toString());
		assertThat(params.size(), equalTo(1));
		assertThat(params.get("id"), equalTo("234235234"));
	}

	@Test
	public void testExtractParams2() {
		String pattern = "/products/{id}/attributes/{attid}";
		String path = "/products/234235234/attributes/2222223";
		Stopwatch sw = Stopwatch.createStarted();
		Map<String, String> params = PathParamsUtils.extractParams(pattern, path);
		sw.stop();
		System.out.println(sw.toString());
		assertThat(params.size(), equalTo(2));
		assertThat(params.get("id"), equalTo("234235234"));
		assertThat(params.get("attid"), equalTo("2222223"));
	}

	@Test
	public void testExtractParams3() {
		String pattern = "/say{sth}to";
		String path = "/sayhelloto";
		Stopwatch sw = Stopwatch.createStarted();
		Map<String, String> params = PathParamsUtils.extractParams(pattern, path);
		sw.stop();
		System.out.println(sw.toString());
		assertThat(params.size(), equalTo(1));
		assertThat(params.get("sth"), equalTo("hello"));
	}

	@Test
	public void testValidationPass() {
		PathParamsUtils.validate("/say{sth}to");
	}

	@Test(expected = IllegalArgumentException.class)
	public void testValidationNotPass() {
		PathParamsUtils.validate("/say{{sth}to");
	}
}
