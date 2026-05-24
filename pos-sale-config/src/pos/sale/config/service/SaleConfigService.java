package pos.sale.config.service;

import java.math.BigDecimal;
import pos.sale.config.model.SaleConfig;

public interface SaleConfigService {

  SaleConfig getConfig();

  SaleConfig updateConfig(BigDecimal taxRate, BigDecimal defaultDiscount);
}
