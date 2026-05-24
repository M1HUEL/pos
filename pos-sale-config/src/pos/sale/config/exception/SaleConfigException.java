package pos.sale.config.exception;

public class SaleConfigException extends RuntimeException {

  public SaleConfigException() {
    // ...
  }

  public SaleConfigException(String message) {
    super(message);
  }

  public SaleConfigException(String message, Throwable cause) {
    super(message, cause);
  }

  public SaleConfigException(Throwable cause) {
    super(cause);
  }

}
