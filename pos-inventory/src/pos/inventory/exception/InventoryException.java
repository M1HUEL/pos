package pos.inventory.exception;

public class InventoryException extends RuntimeException {

  public InventoryException() {
  }

  public InventoryException(String message) {
    super(message);
  }

  public InventoryException(String message, Throwable cause) {
    super(message, cause);
  }

  public InventoryException(Throwable cause) {
    super(cause);
  }

}
