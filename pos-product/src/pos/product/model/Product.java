package pos.product.model;

import java.math.BigDecimal;

public class Product {

  private Long id;
  private String sku;
  private String name;
  private String description;
  private BigDecimal price;
  private Integer stock;
  private Boolean active;

  public Product() {
    // ...
  }

  public Product(Long id, String sku, String name, String description, BigDecimal price, Integer stock, Boolean active) {
    this.id = id;
    this.sku = sku;
    this.name = name;
    this.description = description;
    this.price = price;
    this.stock = stock;
    this.active = active;
  }

  public Long getId() {
    return id;
  }

  public String getSku() {
    return sku;
  }

  public String getName() {
    return name;
  }

  public String getDescription() {
    return description;
  }

  public BigDecimal getPrice() {
    return price;
  }

  public Integer getStock() {
    return stock;
  }

  public Boolean getActive() {
    return active;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public void setSku(String sku) {
    this.sku = sku;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public void setPrice(BigDecimal price) {
    this.price = price;
  }

  public void setStock(Integer stock) {
    this.stock = stock;
  }

  public void setActive(Boolean active) {
    this.active = active;
  }

}
