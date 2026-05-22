package pos.sale.validation;

import java.math.BigDecimal;
import pos.sale.exception.SaleException;
import pos.sale.model.Sale;
import pos.sale.model.SaleItem;

public class SaleValidator {

  public void validateId(Long id) {
    if (id == null) {
      throw new SaleException("Sale ID cannot be null");
    }
  }

  public void validateSaleNumber(String saleNumber) {
    if (saleNumber == null || saleNumber.trim().isEmpty()) {
      throw new SaleException("Sale number cannot be null or empty");
    }
  }

  public void validate(Sale sale) {
    if (sale == null) {
      throw new SaleException("Sale data cannot be null");
    }

    if (sale.getItems() == null || sale.getItems().isEmpty()) {
      throw new SaleException("Sale must contain at least one item");
    }

    if (sale.getTotalAmount() == null || sale.getTotalAmount().compareTo(BigDecimal.ZERO) < 0) {
      throw new SaleException("Sale total amount cannot be null or negative");
    }

    for (SaleItem item : sale.getItems()) {
      validateItem(item);
    }
  }

  private void validateItem(SaleItem item) {
    if (item == null) {
      throw new SaleException("Sale item data cannot be null");
    }

    if (item.getProductId() == null) {
      throw new SaleException("Product ID is required for all sale items");
    }

    if (item.getQuantity() == null || item.getQuantity() <= 0) {
      throw new SaleException("Quantity must be greater than zero for product ID: " + item.getProductId());
    }

    if (item.getUnitPrice() == null || item.getUnitPrice().compareTo(BigDecimal.ZERO) < 0) {
      throw new SaleException("Unit price cannot be null or negative for product ID: " + item.getProductId());
    }

    if (item.getSubTotal() == null || item.getSubTotal().compareTo(BigDecimal.ZERO) < 0) {
      throw new SaleException("Subtotal cannot be null or negative for product ID: " + item.getProductId());
    }
  }
}
