package pos.sale.exception;

public class SaleException extends RuntimeException {

  public SaleException() {
  }

  public SaleException(String message) {
    super(message);
  }

  public SaleException(String message, Throwable cause) {
    super(message, cause);
  }

  public SaleException(Throwable cause) {
    super(cause);
  }

}
