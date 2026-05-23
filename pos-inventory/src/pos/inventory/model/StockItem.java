package pos.inventory.model;

public class StockItem {

  private String id;
  private String productId;
  private Integer stock;
  private Integer minStock;

  public StockItem() {
    // ...
  }

  public StockItem(String id, String productId, Integer stock, Integer minStock) {
    this.id = id;
    this.productId = productId;
    this.stock = stock;
    this.minStock = minStock;
  }

  public String getId() {
    return id;
  }

  public String getProductId() {
    return productId;
  }

  public Integer getStock() {
    return stock;
  }

  public Integer getMinStock() {
    return minStock;
  }

  public void setId(String id) {
    this.id = id;
  }

  public void setProductId(String productId) {
    this.productId = productId;
  }

  public void setStock(Integer stock) {
    this.stock = stock;
  }

  public void setMinStock(Integer minStock) {
    this.minStock = minStock;
  }

}
