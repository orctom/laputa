package com.orctom.laputa.service.domain;

import com.orctom.laputa.service.model.LazyArrayList;

import java.util.List;

public class Categories {

  private String id;

  private List<Category> categories = new LazyArrayList<>(Category::new);

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public List<Category> getCategories() {
    return categories;
  }

  public void setCategories(List<Category> categories) {
    this.categories = categories;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Categories that = (Categories) o;

    if (id != null ? !id.equals(that.id) : that.id != null) return false;
    return categories != null ? categories.equals(that.categories) : that.categories == null;
  }

  @Override
  public int hashCode() {
    int result = id != null ? id.hashCode() : 0;
    result = 31 * result + (categories != null ? categories.hashCode() : 0);
    return result;
  }

  @Override
  public String toString() {
    return "Categories{" +
        "id='" + id + '\'' +
        ", categories=" + categories +
        '}';
  }
}
