package pos.purchase.exception;

public class PurchaseOrderException extends RuntimeException {

  public PurchaseOrderException() {
    // ...
  }

  public PurchaseOrderException(String message) {
    super(message);
  }

  public PurchaseOrderException(String message, Throwable cause) {
    super(message, cause);
  }

  public PurchaseOrderException(Throwable cause) {
    super(cause);
  }

}
