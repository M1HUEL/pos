package pos.ui.controller;

import java.math.BigDecimal;
import java.util.List;
import pos.product.model.Product;
import pos.product.service.ProductService;

public class RegisterProductController {

  private final ProductService productService;

  public RegisterProductController(ProductService productService) {
    this.productService = productService;
  }

  public List<Product> getAllProducts() {
    return productService.getAllProducts();
  }

  public Product createProduct(String sku, String name, String description,
    BigDecimal price, boolean active) {
    Product product = new Product();
    product.setSku(sku);
    product.setName(name);
    product.setDescription(description);
    product.setPrice(price);
    product.setActive(active);
    return productService.createProduct(product);
  }

  public Product updateProduct(String id, String sku, String name, String description,
    BigDecimal price, boolean active) {
    Product product = new Product();
    product.setId(id);
    product.setSku(sku);
    product.setName(name);
    product.setDescription(description);
    product.setPrice(price);
    product.setActive(active);
    return productService.updateProduct(product);
  }

  public void deleteProduct(String id) {
    productService.deleteProduct(id);
  }
}
