package pos.purchase.validation;

import java.math.BigDecimal;
import org.bson.types.ObjectId;
import pos.purchase.exception.PurchaseOrderException;
import pos.purchase.model.PurchaseOrder;
import pos.purchase.model.PurchaseOrderItem;

public class PurchaseOrderValidator {

  public void validateId(String id) {
    if (id == null || id.trim().isEmpty()) {
      throw new PurchaseOrderException("Purchase order ID cannot be null or empty");
    }
    if (!ObjectId.isValid(id)) {
      throw new PurchaseOrderException("Purchase order ID has an invalid format");
    }
  }

  public void validate(PurchaseOrder order) {
    if (order == null) {
      throw new PurchaseOrderException("Purchase order data cannot be null");
    }
    if (order.getSupplierId() == null || order.getSupplierId().trim().isEmpty()) {
      throw new PurchaseOrderException("Supplier is required for purchase order");
    }
    if (!ObjectId.isValid(order.getSupplierId())) {
      throw new PurchaseOrderException("Supplier ID has an invalid format");
    }
    if (order.getItems() == null || order.getItems().isEmpty()) {
      throw new PurchaseOrderException("Purchase order must contain at least one item");
    }
    for (PurchaseOrderItem item : order.getItems()) {
      validateItem(item);
    }
    if (order.getTotalCost() == null || order.getTotalCost().compareTo(BigDecimal.ZERO) < 0) {
      throw new PurchaseOrderException("Total cost cannot be null or negative");
    }
  }

  private void validateItem(PurchaseOrderItem item) {
    if (item == null) {
      throw new PurchaseOrderException("Purchase order item cannot be null");
    }
    if (item.getProductId() == null || item.getProductId().trim().isEmpty()
      || !ObjectId.isValid(item.getProductId())) {
      throw new PurchaseOrderException("Item must link to a valid product ID");
    }
    if (item.getQuantity() == null || item.getQuantity() <= 0) {
      throw new PurchaseOrderException("Item quantity must be greater than zero");
    }
    if (item.getUnitCost() == null || item.getUnitCost().compareTo(BigDecimal.ZERO) < 0) {
      throw new PurchaseOrderException("Item unit cost cannot be null or negative");
    }

    BigDecimal expectedTotal = item.getUnitCost().multiply(BigDecimal.valueOf(item.getQuantity()));

    if (item.getTotalCost().compareTo(expectedTotal) != 0) {
      throw new PurchaseOrderException("Item totalCost does not match unitCost * quantity for product ID: " + item.getProductId());
    }
  }
}
