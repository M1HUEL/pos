package pos.sale.service;

import java.util.List;
import java.util.Optional;
import pos.sale.model.Sale;

public interface SaleService {

  List<Sale> getAllSales();

  Optional<Sale> getSaleById(String id);

  Optional<Sale> getSaleByNumber(String saleNumber);

  Sale createSale(Sale sale);

  void cancelSale(String id);
}
