package pos.inventory.service;

import java.util.List;
import java.util.Optional;
import pos.inventory.model.StockItem;

public interface InventoryService {

  List<StockItem> getAllStockItems();

  Optional<StockItem> getStockByProductId(String productId);

  StockItem initializeStock(StockItem stockItem);

  void reduceStock(String productId, Integer quantity);

  void increaseStock(String productId, Integer quantity);
}
