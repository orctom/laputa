package com.orctom.laputa.service;

import com.orctom.laputa.Laputa;

/**
 * Created by hao on 4/28/15.
 */
public class DummyServiceImpl implements DummyService {

	@Override
	public String hello(String name) {
		return name;
	}

	public static void main(String[] args) throws InstantiationException, IllegalAccessException {
		DummyService service = Laputa.instrument(DummyService.class);
		System.out.println("==================");
		System.out.println("<<" + service.hello("baby") + ">>");
		System.out.println("==================");
	}
}
