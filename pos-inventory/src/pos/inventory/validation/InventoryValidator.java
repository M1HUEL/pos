package pos.inventory.validation;

import org.bson.types.ObjectId;
import pos.inventory.exception.InventoryException;
import pos.inventory.model.StockItem;

public class InventoryValidator {

  public void validateId(String id) {
    if (id == null || id.trim().isEmpty()) {
      throw new InventoryException("Stock item ID cannot be null or empty");
    }
    if (!ObjectId.isValid(id)) {
      throw new InventoryException("Stock item ID has an invalid format");
    }
  }

  public void validateProductId(String productId) {
    if (productId == null || productId.trim().isEmpty()) {
      throw new InventoryException("Product ID cannot be null or empty");
    }
    if (!ObjectId.isValid(productId)) {
      throw new InventoryException("Product ID has an invalid format");
    }
  }

  public void validate(StockItem stockItem) {
    if (stockItem == null) {
      throw new InventoryException("Stock item data cannot be null");
    }

    if (stockItem.getProductId() == null || stockItem.getProductId().trim().isEmpty()) {
      throw new InventoryException("Product ID is required for stock item");
    }
    if (!ObjectId.isValid(stockItem.getProductId())) {
      throw new InventoryException("Product ID associated with the stock item has an invalid format");
    }

    if (stockItem.getStock() == null || stockItem.getStock() < 0) {
      throw new InventoryException("Stock quantity cannot be null or negative");
    }

    if (stockItem.getMinStock() == null || stockItem.getMinStock() < 0) {
      throw new InventoryException("Minimum stock cannot be null or negative");
    }
  }
}
