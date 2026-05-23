package pos.supplier.exception;

public class SupplierException extends RuntimeException {

  public SupplierException() {
  }

  public SupplierException(String message) {
    super(message);
  }

  public SupplierException(String message, Throwable cause) {
    super(message, cause);
  }

  public SupplierException(Throwable cause) {
    super(cause);
  }

}
