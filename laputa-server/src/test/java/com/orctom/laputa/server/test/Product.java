package com.orctom.laputa.server.test;

import com.orctom.laputa.server.annotation.Export;
import com.orctom.laputa.server.annotation.Path;
import com.orctom.laputa.server.annotation.PathParam;

@Export
@Path("/test")
public class Product {

	@Path("/hello")
	public String hello() {
		return "hello";
	}

	@Path("/hello/{name}")
	public String hello(@PathParam("name") String name) {
		return "hello " + name;
	}
}
