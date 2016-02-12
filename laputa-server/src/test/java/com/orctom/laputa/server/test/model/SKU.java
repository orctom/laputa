package com.orctom.laputa.server.test.model;

/**
 * dummy sku
 * Created by hao on 11/25/15.
 */
public class SKU {

  private String sku;
  private String desc;
  private int category;
  private int stock;

  public static final SKU EMPTY = new SKU();

  public SKU() {
  }

  public SKU(String sku, String desc, int category, int stock) {
    this.sku = sku;
    this.desc = desc;
    this.category = category;
    this.stock = stock;
  }


  public int getCategory() {
    return category;
  }

  public void setCategory(int category) {
    this.category = category;
  }

  public String getDesc() {
    return desc;
  }

  public void setDesc(String desc) {
    this.desc = desc;
  }

  public String getSku() {
    return sku;
  }

  public void setSku(String sku) {
    this.sku = sku;
  }

  public int getStock() {
    return stock;
  }

  public void setStock(int stock) {
    this.stock = stock;
  }
}
