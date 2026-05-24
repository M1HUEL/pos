package pos.sale.config.model;

import java.math.BigDecimal;

public class SaleConfig {

  private String id;
  private BigDecimal taxRate;
  private BigDecimal defaultDiscount;

  public SaleConfig() {
    // ...
  }

  public SaleConfig(String id, BigDecimal taxRate, BigDecimal defaultDiscount) {
    this.id = id;
    this.taxRate = taxRate;
    this.defaultDiscount = defaultDiscount;
  }

  public String getId() {
    return id;
  }

  public BigDecimal getTaxRate() {
    return taxRate;
  }

  public BigDecimal getDefaultDiscount() {
    return defaultDiscount;
  }

  public void setId(String id) {
    this.id = id;
  }

  public void setTaxRate(BigDecimal taxRate) {
    this.taxRate = taxRate;
  }

  public void setDefaultDiscount(BigDecimal defaultDiscount) {
    this.defaultDiscount = defaultDiscount;
  }

}
