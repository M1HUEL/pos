package pos.inventory.repository;

import java.util.List;
import java.util.Optional;
import pos.inventory.model.StockItem;

public interface InventoryRepository {

  List<StockItem> findAll();

  Optional<StockItem> findById(Long id);

  Optional<StockItem> findByProductId(Long productId);

  StockItem save(StockItem stockItem);
}
