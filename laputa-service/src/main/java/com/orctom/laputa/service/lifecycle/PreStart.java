package com.orctom.laputa.service.lifecycle;

@FunctionalInterface
public interface PreStart extends Runnable {

  void run();
}
