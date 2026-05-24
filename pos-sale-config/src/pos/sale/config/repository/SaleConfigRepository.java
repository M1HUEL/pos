package pos.sale.config.repository;

import pos.sale.config.model.SaleConfig;

public interface SaleConfigRepository {

  SaleConfig getConfig();

  SaleConfig save(SaleConfig config);
}
