package pos.purchase.model;

import java.math.BigDecimal;

public class PurchaseOrderItem {

  private String id;
  private String productId;
  private String productName;
  private Integer quantity;
  private BigDecimal unitCost;
  private BigDecimal totalCost;

  public PurchaseOrderItem() {
    // ...
  }

  public PurchaseOrderItem(String id, String productId, String productName, Integer quantity, BigDecimal unitCost, BigDecimal totalCost) {
    this.id = id;
    this.productId = productId;
    this.productName = productName;
    this.quantity = quantity;
    this.unitCost = unitCost;
    this.totalCost = totalCost;
  }

  public String getId() {
    return id;
  }

  public String getProductId() {
    return productId;
  }

  public String getProductName() {
    return productName;
  }

  public Integer getQuantity() {
    return quantity;
  }

  public BigDecimal getUnitCost() {
    return unitCost;
  }

  public BigDecimal getTotalCost() {
    return totalCost;
  }

  public void setId(String id) {
    this.id = id;
  }

  public void setProductId(String productId) {
    this.productId = productId;
  }

  public void setProductName(String productName) {
    this.productName = productName;
  }

  public void setQuantity(Integer quantity) {
    this.quantity = quantity;
  }

  public void setUnitCost(BigDecimal unitCost) {
    this.unitCost = unitCost;
  }

  public void setTotalCost(BigDecimal totalCost) {
    this.totalCost = totalCost;
  }

}
