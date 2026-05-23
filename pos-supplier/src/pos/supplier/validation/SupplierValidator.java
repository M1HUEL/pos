package pos.supplier.validation;

import org.bson.types.ObjectId;
import pos.supplier.exception.SupplierException;
import pos.supplier.model.Supplier;

public class SupplierValidator {

  public void validateId(String id) {
    if (id == null || id.trim().isEmpty()) {
      throw new SupplierException("Supplier ID cannot be null or empty");
    }
    if (!ObjectId.isValid(id)) {
      throw new SupplierException("Supplier ID has an invalid format");
    }
  }

  public void validate(Supplier supplier) {
    if (supplier == null) {
      throw new SupplierException("Supplier data cannot be null");
    }
    if (supplier.getName() == null || supplier.getName().trim().isEmpty()) {
      throw new SupplierException("Supplier name cannot be null or empty");
    }
    if (supplier.getEmail() != null && !supplier.getEmail().trim().isEmpty()) {
      if (!supplier.getEmail().matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$")) {
        throw new SupplierException("Supplier email has an invalid format");
      }
    }
  }
}
