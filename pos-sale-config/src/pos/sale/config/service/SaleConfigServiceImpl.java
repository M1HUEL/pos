package pos.sale.config.service;

import java.math.BigDecimal;
import pos.sale.config.model.SaleConfig;
import pos.sale.config.repository.SaleConfigRepository;
import pos.sale.config.validation.SaleConfigValidator;

public class SaleConfigServiceImpl implements SaleConfigService {

  private final SaleConfigRepository saleConfigRepository;
  private final SaleConfigValidator saleConfigValidator;

  public SaleConfigServiceImpl(SaleConfigRepository saleConfigRepository, SaleConfigValidator saleConfigValidator) {
    this.saleConfigRepository = saleConfigRepository;
    this.saleConfigValidator = saleConfigValidator;
  }

  @Override
  public SaleConfig getConfig() {
    return saleConfigRepository.getConfig();
  }

  @Override
  public SaleConfig updateConfig(BigDecimal taxRate, BigDecimal defaultDiscount) {
    saleConfigValidator.validate(taxRate, defaultDiscount);

    SaleConfig config = saleConfigRepository.getConfig();
    config.setTaxRate(taxRate);
    config.setDefaultDiscount(defaultDiscount);

    return saleConfigRepository.save(config);
  }
}
