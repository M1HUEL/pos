package pos.sale.repository;

import java.util.List;
import java.util.Optional;
import pos.sale.model.Sale;

public interface SaleRepository {

  List<Sale> findAll();

  Optional<Sale> findById(String id);

  Optional<Sale> findBySaleNumber(String saleNumber);

  Sale create(Sale sale);

  Sale update(Sale sale);

  void deleteById(String id);
}
