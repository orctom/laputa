package com.orctom.laputa.service.lifecycle;

@FunctionalInterface
public interface PostStart extends Runnable {

  void run();
}
