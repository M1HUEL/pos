package pos.ui.controller;

import java.math.BigDecimal;
import java.math.RoundingMode;
import pos.sale.config.model.SaleConfig;
import pos.sale.config.service.SaleConfigService;

public class SaleConfigController {

  private final SaleConfigService saleConfigService;

  public SaleConfigController(SaleConfigService saleConfigService) {
    this.saleConfigService = saleConfigService;
  }

  public String getTaxRatePercent() {
    return saleConfigService.getConfig().getTaxRate().multiply(BigDecimal.valueOf(100)).stripTrailingZeros().toPlainString();
  }

  public String getDefaultDiscountAmount() {
    return saleConfigService.getConfig().getDefaultDiscount().setScale(2, RoundingMode.HALF_UP).toPlainString();
  }

  public SaleConfig updateConfig(String taxRatePercent, String defaultDiscount) {
    BigDecimal taxRate = new BigDecimal(taxRatePercent).divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP);
    BigDecimal discount = new BigDecimal(defaultDiscount);

    return saleConfigService.updateConfig(taxRate, discount);
  }
}
