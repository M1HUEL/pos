package pos.sale.validation;

import java.math.BigDecimal;
import java.util.UUID;
import org.bson.types.ObjectId;
import pos.sale.exception.SaleException;
import pos.sale.model.Sale;
import pos.sale.model.SaleItem;

public class SaleValidator {

  public void validateId(String id) {
    if (id == null || id.trim().isEmpty()) {
      throw new SaleException("Sale ID cannot be null or empty");
    }
    if (!ObjectId.isValid(id)) {
      throw new SaleException("Sale ID has an invalid format");
    }
  }

  public void validateSaleNumber(String saleNumber) {
    if (saleNumber == null || saleNumber.trim().isEmpty()) {
      throw new SaleException("Sale number cannot be null or empty");
    }
    try {
      UUID.fromString(saleNumber);
    } catch (IllegalArgumentException e) {
      throw new SaleException("Sale number has an invalid UUID format");
    }
  }

  public void validate(Sale sale) {
    if (sale == null) {
      throw new SaleException("Sale data cannot be null");
    }
    if (sale.getItems() == null || sale.getItems().isEmpty()) {
      throw new SaleException("Sale must contain at least one item");
    }
    for (SaleItem item : sale.getItems()) {
      validateItem(item);
    }
    if (sale.getPaymentMethod() == null) {
      throw new SaleException("Payment method is required");
    }
    if (sale.getTotalAmount() == null || sale.getTotalAmount().compareTo(BigDecimal.ZERO) < 0) {
      throw new SaleException("Sale total amount cannot be null or negative");
    }
    validateTotalAmount(sale);
  }

  private void validateItem(SaleItem item) {
    if (item == null) {
      throw new SaleException("Sale item cannot be null");
    }
    if (item.getProductId() == null || item.getProductId().trim().isEmpty()) {
      throw new SaleException("Sale item must link to a valid product ID");
    }
    if (item.getQuantity() == null || item.getQuantity() <= 0) {
      throw new SaleException("Sale item quantity must be greater than zero");
    }
    if (item.getUnitPrice() == null || item.getUnitPrice().compareTo(BigDecimal.ZERO) < 0) {
      throw new SaleException("Sale item unit price cannot be null or negative");
    }
    if (item.getDiscountAmount() == null || item.getDiscountAmount().compareTo(BigDecimal.ZERO) < 0) {
      throw new SaleException("Sale item discount amount cannot be null or negative");
    }
    if (item.getSubTotal() == null || item.getSubTotal().compareTo(BigDecimal.ZERO) < 0) {
      throw new SaleException("Sale item subtotal cannot be null or negative");
    }

    BigDecimal expectedSubTotal = item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())).subtract(item.getDiscountAmount());

    if (item.getSubTotal().compareTo(expectedSubTotal) != 0) {
      throw new SaleException("Sale item subTotal does not match unitPrice * quantity - discountAmount for product ID: " + item.getProductId());
    }
  }

  private void validateTotalAmount(Sale sale) {
    BigDecimal itemsTotal = sale.getItems().stream().map(SaleItem::getSubTotal).reduce(BigDecimal.ZERO, BigDecimal::add);
    BigDecimal saleDiscount = sale.getDiscountAmount() != null ? sale.getDiscountAmount() : BigDecimal.ZERO;
    BigDecimal saleTax = sale.getTaxAmount() != null ? sale.getTaxAmount() : BigDecimal.ZERO;
    BigDecimal expectedTotal = itemsTotal.subtract(saleDiscount).add(saleTax);

    if (sale.getTotalAmount().compareTo(expectedTotal) != 0) {
      throw new SaleException("Sale totalAmount does not match sum of item subtotals minus discount plus tax");
    }
  }
}
