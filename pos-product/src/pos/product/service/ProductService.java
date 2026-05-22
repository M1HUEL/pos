package pos.product.service;

import java.util.List;
import java.util.Optional;
import pos.product.model.Product;

public interface ProductService {

  List<Product> getAllProducts();

  Optional<Product> getProductById(Long id);

  Optional<Product> getProductBySku(String sku);

  Product createProduct(Product product);

  Product updateProduct(Product product);

  void deleteProduct(Long id);
}
