package com.orctom.laputa.service.model;

import java.util.ArrayList;

public class LazyArrayList<E> extends ArrayList<E> {

  private Factory<E> factory;

  public LazyArrayList(Factory<E> factory) {
    this.factory = factory;
  }

  @Override
  public E get(int index) {
    if (index >= size()) {
      for (int i = size(); i <= index; i++) {
        add(factory.create());
      }
    }
    return super.get(index);
  }
}
