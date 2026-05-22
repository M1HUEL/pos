package pos.sale.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import pos.sale.model.Sale;

public class SaleInMemoryRepository implements SaleRepository {

  private final Map<Long, Sale> database = new ConcurrentHashMap<>();
  private final AtomicLong idGenerator = new AtomicLong(1);

  @Override
  public List<Sale> findAll() {
    return new ArrayList<>(database.values());
  }

  @Override
  public Optional<Sale> findById(Long id) {
    return Optional.ofNullable(database.get(id));
  }

  @Override
  public Optional<Sale> findBySaleNumber(String saleNumber) {
    if (saleNumber == null) {
      return Optional.empty();
    }

    return database.values().stream().filter(sale -> saleNumber.equals(sale.getSaleNumber())).findFirst();
  }

  @Override
  public Sale save(Sale sale) {
    if (sale.getId() == null) {
      sale.setId(idGenerator.getAndIncrement());
    }

    database.put(sale.getId(), sale);

    return sale;
  }

  @Override
  public void deleteById(Long id) {
    database.remove(id);
  }

}
