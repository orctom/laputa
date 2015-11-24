package com.orctom.laputa.server.test;

import com.orctom.laputa.server.annotation.Export;
import com.orctom.laputa.server.annotation.Path;
import com.orctom.laputa.server.annotation.PathParam;

@Export
@Path("/product")
public class Product {

	@Path("/hello")
	public String hello() {
		return "hello";
	}

	@Path("/hello/{name}")
	public String hello(@PathParam("name") String name) {
		return "hello " + name;
	}

	@Path("/hello/{name}/a")
	public String helloA(@PathParam("name") String name) {
		return "hello " + name;
	}

	@Path("/hello/{name}/b")
	public String helloB(@PathParam("name") String name) {
		return "hello " + name;
	}

	@Path("/hello/{name}/attribute/{attribute}")
	public String helloAttribute(@PathParam("name") String name, String attribute) {
		return "hello " + name;
	}
}
