package com.orctom.laputa.service.domain;

import java.util.Objects;

public class SKU {

  private Long id;
  private String name;
  private Category category;

  public SKU() {}

  public SKU(Long id, String name, Category category) {
    this.id = id;
    this.name = name;
    this.category = category;
  }

  public Category getCategory() {
    return category;
  }

  public void setCategory(Category category) {
    this.category = category;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof SKU)) return false;
    SKU sku = (SKU) o;
    return Objects.equals(id, sku.id) &&
        Objects.equals(name, sku.name) &&
        Objects.equals(category, sku.category);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, name, category);
  }

  @Override
  public String toString() {
    return "SKU{" +
        "category=" + category +
        ", id=" + id +
        ", name='" + name + '\'' +
        '}';
  }
}
