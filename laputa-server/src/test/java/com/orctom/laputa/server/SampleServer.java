package com.orctom.laputa.server;

import com.google.common.base.Splitter;
import com.orctom.laputa.server.annotation.Export;

import java.util.List;

public class SampleServer {

	public static void main(String[] args) {
		try {
			new LaputaService(9000)
					.scanPackage("com.orctom.laputa.server.test")
					.forAnnotation(Export.class)
					.startup();
		} catch (Exception e) {
			e.printStackTrace();
		}

//		SampleServer tester = new SampleServer();
//		tester.printPortions("/");
//		tester.printPortions("test");
//		tester.printPortions("/hello");
//		tester.printPortions("/hello/world");
//		tester.printPortions("/hello/world/test");
//		tester.printPortions("/hello/world/test/");
//		tester.printPortions("/hey");
//		tester.printPortions("/hey/");
	}

	public void printPortions(String path) {
		System.out.println("portions for " + path);
//		List<String> portions = Splitter.on("/").splitToList(path);
//		for (String portion : portions) {
		for (String portion : path.split("/")) {
			System.out.println(">>" + portion);
		}
		System.out.println();
	}
}
