package com.orctom.laputa.service.example.model;

import com.orctom.laputa.service.model.LazyArrayList;

import java.util.Collection;
import java.util.List;

public class Products {

  private long uid;
  private List<SKU> skus = new LazyArrayList<>(SKU::new);

  public long getUid() {
    return uid;
  }

  public void setUid(long uid) {
    this.uid = uid;
  }

  public Collection<SKU> getSkus() {
    return skus;
  }

  public void setSkus(List<SKU> skus) {
    this.skus = skus;
  }

  @Override
  public String toString() {
    return "SKUS{" +
        "uid=" + uid +
        ", skus=" + skus +
        '}';
  }
}
