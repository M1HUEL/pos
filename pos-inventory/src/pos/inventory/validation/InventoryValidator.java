package pos.inventory.validation;

import pos.inventory.exception.InventoryException;
import pos.inventory.model.StockItem;

public class InventoryValidator {

  public void validateId(Long id) {
    if (id == null) {
      throw new InventoryException("Stock item ID cannot be null");
    }
  }

  public void validateProductId(Long productId) {
    if (productId == null) {
      throw new InventoryException("Product ID cannot be null");
    }
  }

  public void validate(StockItem stockItem) {
    if (stockItem == null) {
      throw new InventoryException("Stock item data cannot be null");
    }

    if (stockItem.getProductId() == null) {
      throw new InventoryException("Product ID is required for stock item");
    }

    if (stockItem.getStock() == null || stockItem.getStock() < 0) {
      throw new InventoryException("Stock quantity cannot be null or negative");
    }

    if (stockItem.getMinStock() == null || stockItem.getMinStock() < 0) {
      throw new InventoryException("Minimum stock cannot be null or negative");
    }
  }
}
