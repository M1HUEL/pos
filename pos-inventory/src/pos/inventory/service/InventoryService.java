package pos.inventory.service;

import java.util.List;
import java.util.Optional;
import pos.inventory.model.StockItem;

public interface InventoryService {

  List<StockItem> getAllStockItems();

  Optional<StockItem> getStockByProductId(Long productId);

  StockItem initializeStock(StockItem stockItem);

  void reduceStock(Long productId, Integer quantity);

  void increaseStock(Long productId, Integer quantity);
}
