package pos.sale.repository;

import java.util.List;
import java.util.Optional;
import pos.sale.model.Sale;

public interface SaleRepository {

  List<Sale> findAll();

  Optional<Sale> findById(Long id);

  Optional<Sale> findBySaleNumber(String saleNumber);

  Sale save(Sale sale);

  void deleteById(Long id);
}
