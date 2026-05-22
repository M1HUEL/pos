package pos.sale.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class Sale {

  private Long id;
  private String saleNumber;
  private LocalDateTime dateTime;
  private List<SaleItem> items;
  private BigDecimal totalAmount;
  private BigDecimal taxAmount;
  private BigDecimal discountAmount;
  private String paymentMethod;
  private SaleStatus status;

  public Sale() {
    // ...
  }

  public Sale(Long id, String saleNumber, LocalDateTime dateTime, List<SaleItem> items, BigDecimal totalAmount, BigDecimal taxAmount, BigDecimal discountAmount, String paymentMethod, SaleStatus status) {
    this.id = id;
    this.saleNumber = saleNumber;
    this.dateTime = dateTime;
    this.items = items;
    this.totalAmount = totalAmount;
    this.taxAmount = taxAmount;
    this.discountAmount = discountAmount;
    this.paymentMethod = paymentMethod;
    this.status = status;
  }

  public Long getId() {
    return id;
  }

  public String getSaleNumber() {
    return saleNumber;
  }

  public LocalDateTime getDateTime() {
    return dateTime;
  }

  public List<SaleItem> getItems() {
    return items;
  }

  public BigDecimal getTotalAmount() {
    return totalAmount;
  }

  public BigDecimal getTaxAmount() {
    return taxAmount;
  }

  public BigDecimal getDiscountAmount() {
    return discountAmount;
  }

  public String getPaymentMethod() {
    return paymentMethod;
  }

  public SaleStatus getStatus() {
    return status;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public void setSaleNumber(String saleNumber) {
    this.saleNumber = saleNumber;
  }

  public void setDateTime(LocalDateTime dateTime) {
    this.dateTime = dateTime;
  }

  public void setItems(List<SaleItem> items) {
    this.items = items;
  }

  public void setTotalAmount(BigDecimal totalAmount) {
    this.totalAmount = totalAmount;
  }

  public void setTaxAmount(BigDecimal taxAmount) {
    this.taxAmount = taxAmount;
  }

  public void setDiscountAmount(BigDecimal discountAmount) {
    this.discountAmount = discountAmount;
  }

  public void setPaymentMethod(String paymentMethod) {
    this.paymentMethod = paymentMethod;
  }

  public void setStatus(SaleStatus status) {
    this.status = status;
  }

}
