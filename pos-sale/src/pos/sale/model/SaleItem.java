package pos.sale.model;

import java.math.BigDecimal;

public class SaleItem {

  private String id;
  private String productId;
  private String productName;
  private Integer quantity;
  private BigDecimal unitPrice;
  private BigDecimal discountAmount;
  private BigDecimal subTotal;

  public SaleItem() {
    // ...
  }

  public SaleItem(String id, String productId, String productName, Integer quantity, BigDecimal unitPrice, BigDecimal discountAmount, BigDecimal subTotal) {
    this.id = id;
    this.productId = productId;
    this.productName = productName;
    this.quantity = quantity;
    this.unitPrice = unitPrice;
    this.discountAmount = discountAmount;
    this.subTotal = subTotal;
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

  public BigDecimal getUnitPrice() {
    return unitPrice;
  }

  public BigDecimal getDiscountAmount() {
    return discountAmount;
  }

  public BigDecimal getSubTotal() {
    return subTotal;
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

  public void setUnitPrice(BigDecimal unitPrice) {
    this.unitPrice = unitPrice;
  }

  public void setDiscountAmount(BigDecimal discountAmount) {
    this.discountAmount = discountAmount;
  }

  public void setSubTotal(BigDecimal subTotal) {
    this.subTotal = subTotal;
  }

}
