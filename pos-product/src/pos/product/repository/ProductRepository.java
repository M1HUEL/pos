package pos.product.repository;

import java.util.List;
import java.util.Optional;
import pos.product.model.Product;

public interface ProductRepository {

  List<Product> findAll();

  Optional<Product> findById(String id);

  Optional<Product> findBySku(String sku);

  Product create(Product product);

  Product update(Product product);

  void deleteById(String id);
}
