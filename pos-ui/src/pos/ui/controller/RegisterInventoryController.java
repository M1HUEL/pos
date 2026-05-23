package pos.ui.controller;

import java.util.List;
import java.util.Optional;
import pos.inventory.model.StockItem;
import pos.inventory.service.InventoryService;
import pos.product.model.Product;
import pos.product.service.ProductService;

public class RegisterInventoryController {

  private final InventoryService inventoryService;
  private final ProductService productService;

  public RegisterInventoryController(InventoryService inventoryService,
    ProductService productService) {
    this.inventoryService = inventoryService;
    this.productService = productService;
  }

  public List<Product> getAllProducts() {
    return productService.getAllProducts();
  }

  public Optional<Product> findProductById(String id) {
    return productService.getProductById(id);
  }

  public List<StockItem> getAllStockItems() {
    return inventoryService.getAllStockItems();
  }

  public StockItem initializeStock(String productId, int stock, int minStock) {
    StockItem stockItem = new StockItem();
    stockItem.setProductId(productId);
    stockItem.setStock(stock);
    stockItem.setMinStock(minStock);
    return inventoryService.initializeStock(stockItem);
  }

  public StockItem updateStock(String productId, int stock, int minStock) {
    return inventoryService.updateStock(productId, stock, minStock);
  }

  public void deleteStock(String productId) {
    inventoryService.deleteStockByProductId(productId);
  }
}
