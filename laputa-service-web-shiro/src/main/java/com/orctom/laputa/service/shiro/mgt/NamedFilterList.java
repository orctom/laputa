package com.orctom.laputa.service.shiro.mgt;

import com.orctom.laputa.service.shiro.filter.Filter;
import org.apache.shiro.util.StringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class NamedFilterList {

  private String name;
  private List<Filter> backingList;

  public NamedFilterList(String name) {
    this(name, new ArrayList<>());
  }

  public NamedFilterList(String name, List<Filter> backingList) {
    this.name = name;
    this.backingList = backingList;
  }

  protected void setName(String name) {
    if (!StringUtils.hasText(name)) {
      throw new IllegalArgumentException("Cannot specify a null or empty name.");
    }
    this.name = name;
  }

  public String getName() {
    return name;
  }

  public boolean add(Filter filter) {
    return this.backingList.add(filter);
  }

  public void add(int index, Filter filter) {
    this.backingList.add(index, filter);
  }

  public boolean addAll(Collection<? extends Filter> c) {
    return this.backingList.addAll(c);
  }

  public boolean addAll(int index, Collection<? extends Filter> c) {
    return this.backingList.addAll(index, c);
  }

  public void clear() {
    this.backingList.clear();
  }

  public boolean contains(Object o) {
    return this.backingList.contains(o);
  }

  public boolean containsAll(Collection<?> c) {
    return this.backingList.containsAll(c);
  }

  public Filter get(int index) {
    return this.backingList.get(index);
  }

  public int indexOf(Object o) {
    return this.backingList.indexOf(o);
  }

  public boolean isEmpty() {
    return this.backingList.isEmpty();
  }

  public Iterator<Filter> iterator() {
    return this.backingList.iterator();
  }

  public int lastIndexOf(Object o) {
    return this.backingList.lastIndexOf(o);
  }

  public ListIterator<Filter> listIterator() {
    return this.backingList.listIterator();
  }

  public ListIterator<Filter> listIterator(int index) {
    return this.backingList.listIterator(index);
  }

  public Filter remove(int index) {
    return this.backingList.remove(index);
  }

  public boolean remove(Object o) {
    return this.backingList.remove(o);
  }

  public boolean removeAll(Collection<?> c) {
    return this.backingList.removeAll(c);
  }

  public boolean retainAll(Collection<?> c) {
    return this.backingList.retainAll(c);
  }

  public Filter set(int index, Filter filter) {
    return this.backingList.set(index, filter);
  }

  public int size() {
    return this.backingList.size();
  }
}
