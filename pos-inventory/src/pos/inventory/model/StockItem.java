package pos.inventory.model;

public class StockItem {

  private Long id;
  private Long productId;
  private Integer stock;
  private Integer minStock;

  public StockItem() {
    // ...
  }

  public StockItem(Long id, Long productId, Integer stock, Integer minStock) {
    this.id = id;
    this.productId = productId;
    this.stock = stock;
    this.minStock = minStock;
  }

  public Long getId() {
    return id;
  }

  public Long getProductId() {
    return productId;
  }

  public Integer getStock() {
    return stock;
  }

  public Integer getMinStock() {
    return minStock;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public void setProductId(Long productId) {
    this.productId = productId;
  }

  public void setStock(Integer stock) {
    this.stock = stock;
  }

  public void setMinStock(Integer minStock) {
    this.minStock = minStock;
  }

}
