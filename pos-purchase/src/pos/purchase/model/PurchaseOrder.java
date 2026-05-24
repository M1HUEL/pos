package pos.purchase.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class PurchaseOrder {

  private String id;
  private String orderNumber;
  private String supplierId;
  private String supplierName;
  private LocalDateTime date;
  private List<PurchaseOrderItem> items;
  private BigDecimal totalCost;
  private PurchaseOrderStatus status;

  public PurchaseOrder() {
    // ...
  }

  public PurchaseOrder(String id, String orderNumber, String supplierId, String supplierName, LocalDateTime date, List<PurchaseOrderItem> items, BigDecimal totalCost, PurchaseOrderStatus status) {
    this.id = id;
    this.orderNumber = orderNumber;
    this.supplierId = supplierId;
    this.supplierName = supplierName;
    this.date = date;
    this.items = items;
    this.totalCost = totalCost;
    this.status = status;
  }

  public String getId() {
    return id;
  }

  public String getOrderNumber() {
    return orderNumber;
  }

  public String getSupplierId() {
    return supplierId;
  }

  public String getSupplierName() {
    return supplierName;
  }

  public LocalDateTime getDate() {
    return date;
  }

  public List<PurchaseOrderItem> getItems() {
    return items;
  }

  public BigDecimal getTotalCost() {
    return totalCost;
  }

  public PurchaseOrderStatus getStatus() {
    return status;
  }

  public void setId(String id) {
    this.id = id;
  }

  public void setOrderNumber(String orderNumber) {
    this.orderNumber = orderNumber;
  }

  public void setSupplierId(String supplierId) {
    this.supplierId = supplierId;
  }

  public void setSupplierName(String supplierName) {
    this.supplierName = supplierName;
  }

  public void setDate(LocalDateTime date) {
    this.date = date;
  }

  public void setItems(List<PurchaseOrderItem> items) {
    this.items = items;
  }

  public void setTotalCost(BigDecimal totalCost) {
    this.totalCost = totalCost;
  }

  public void setStatus(PurchaseOrderStatus status) {
    this.status = status;
  }

}
