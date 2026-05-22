package pos.inventory.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import pos.inventory.model.StockItem;

public class InventoryInMemoryRepository implements InventoryRepository {

  private final Map<Long, StockItem> database = new ConcurrentHashMap<>();
  private final AtomicLong idGenerator = new AtomicLong(1);

  @Override
  public List<StockItem> findAll() {
    return new ArrayList<>(database.values());
  }

  @Override
  public Optional<StockItem> findById(Long id) {
    return Optional.ofNullable(database.get(id));
  }

  @Override
  public Optional<StockItem> findByProductId(Long productId) {
    if (productId == null) {
      return Optional.empty();
    }

    return database.values().stream().filter(item -> productId.equals(item.getProductId())).findFirst();
  }

  @Override
  public StockItem save(StockItem stockItem) {
    if (stockItem.getId() == null) {
      stockItem.setId(idGenerator.getAndIncrement());
    }

    database.put(stockItem.getId(), stockItem);

    return stockItem;
  }

}
