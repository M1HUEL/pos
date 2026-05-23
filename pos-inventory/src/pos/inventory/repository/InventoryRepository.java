package pos.inventory.repository;

import java.util.List;
import java.util.Optional;
import pos.inventory.model.StockItem;

public interface InventoryRepository {

  List<StockItem> findAll();

  Optional<StockItem> findById(String id);

  Optional<StockItem> findByProductId(String productId);

  StockItem create(StockItem stockItem);

  StockItem update(StockItem stockItem);

  void deleteById(String id);
}
