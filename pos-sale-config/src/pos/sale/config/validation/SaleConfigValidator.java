package pos.sale.config.validation;

import java.math.BigDecimal;
import pos.sale.config.exception.SaleConfigException;

public class SaleConfigValidator {

  public void validate(BigDecimal taxRate, BigDecimal defaultDiscount) {
    if (taxRate == null) {
      throw new SaleConfigException("Tax rate cannot be null");
    }
    if (taxRate.compareTo(BigDecimal.ZERO) < 0 || taxRate.compareTo(BigDecimal.ONE) > 0) {
      throw new SaleConfigException("Tax rate must be between 0% and 100%");
    }
    if (defaultDiscount == null || defaultDiscount.compareTo(BigDecimal.ZERO) < 0) {
      throw new SaleConfigException("Default discount cannot be null or negative");
    }
  }
}
