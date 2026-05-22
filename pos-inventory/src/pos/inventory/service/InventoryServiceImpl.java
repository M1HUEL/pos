package pos.inventory.service;

import java.util.List;
import java.util.Optional;
import pos.inventory.exception.InventoryException;
import pos.inventory.model.StockItem;
import pos.inventory.repository.InventoryRepository;
import pos.inventory.validation.InventoryValidator;
import pos.product.service.ProductService;

public class InventoryServiceImpl implements InventoryService {

  private final InventoryRepository inventoryRepository;
  private final InventoryValidator inventoryValidator;
  private final ProductService productService;

  public InventoryServiceImpl(InventoryRepository inventoryRepository, InventoryValidator inventoryValidator, ProductService productService) {
    this.inventoryRepository = inventoryRepository;
    this.inventoryValidator = inventoryValidator;
    this.productService = productService;
  }

  @Override
  public List<StockItem> getAllStockItems() {
    return inventoryRepository.findAll();
  }

  @Override
  public Optional<StockItem> getStockByProductId(Long productId) {
    inventoryValidator.validateProductId(productId);
    return inventoryRepository.findByProductId(productId);
  }

  @Override
  public StockItem initializeStock(StockItem stockItem) {
    inventoryValidator.validate(stockItem);

    productService.getProductById(stockItem.getProductId()).orElseThrow(() -> new InventoryException("Cannot initialize stock. Product not found with ID: " + stockItem.getProductId()));

    if (inventoryRepository.findByProductId(stockItem.getProductId()).isPresent()) {
      throw new InventoryException("Stock records already exist for product ID: " + stockItem.getProductId());
    }

    return inventoryRepository.save(stockItem);
  }

  @Override
  public void reduceStock(Long productId, Integer quantity) {
    inventoryValidator.validateProductId(productId);

    if (quantity == null || quantity <= 0) {
      throw new InventoryException("Quantity to reduce must be greater than zero");
    }

    StockItem stockItem = inventoryRepository.findByProductId(productId).orElseThrow(() -> new InventoryException("No stock record found for product ID: " + productId));

    if (stockItem.getStock() < quantity) {
      throw new InventoryException("Insufficient stock. Available: " + stockItem.getStock() + ", Requested: " + quantity);
    }

    stockItem.setStock(stockItem.getStock() - quantity);
    inventoryRepository.save(stockItem);
  }

  @Override
  public void increaseStock(Long productId, Integer quantity) {
    inventoryValidator.validateProductId(productId);

    if (quantity == null || quantity <= 0) {
      throw new InventoryException("Quantity to increase must be greater than zero");
    }

    StockItem stockItem = inventoryRepository.findByProductId(productId).orElseThrow(() -> new InventoryException("No stock record found for product ID: " + productId));

    stockItem.setStock(stockItem.getStock() + quantity);
    inventoryRepository.save(stockItem);
  }

}
