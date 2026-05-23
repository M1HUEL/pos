package pos.product.validation;

import java.math.BigDecimal;
import org.bson.types.ObjectId;
import pos.product.exception.ProductException;
import pos.product.model.Product;

public class ProductValidator {

  public void validateId(String id) {
    if (id == null || id.trim().isEmpty()) {
      throw new ProductException("Product ID cannot be null or empty");
    }
    if (!ObjectId.isValid(id)) {
      throw new ProductException("Product ID has an invalid format");
    }
  }

  public void validateSku(String sku) {
    if (sku == null || sku.trim().isEmpty()) {
      throw new ProductException("Product SKU cannot be null or empty");
    }
  }

  public void validate(Product product) {
    if (product == null) {
      throw new ProductException("Product data cannot be null");
    }
    if (product.getName() == null || product.getName().trim().isEmpty()) {
      throw new ProductException("Product name cannot be null or empty");
    }
    if (product.getPrice() == null || product.getPrice().compareTo(BigDecimal.ZERO) < 0) {
      throw new ProductException("Product price cannot be null or negative");
    }
  }
}
