package pos.inventory.service;

import java.util.List;
import java.util.Optional;
import pos.inventory.model.StockItem;

public interface InventoryService {

  List<StockItem> getAllStockItems();

  Optional<StockItem> getStockByProductId(String productId);

  StockItem initializeStock(StockItem stockItem);

  StockItem updateStock(String productId, Integer stock, Integer minStock);

  void reduceStock(String productId, Integer quantity);

  void increaseStock(String productId, Integer quantity);

  void deleteStockByProductId(String productId);
}
