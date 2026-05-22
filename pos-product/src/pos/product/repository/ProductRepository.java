package pos.product.repository;

import java.util.List;
import java.util.Optional;
import pos.product.model.Product;

public interface ProductRepository {

  List<Product> findAll();

  Optional<Product> findById(Long id);

  Optional<Product> findBySku(String sku);

  Product save(Product product);

  void deleteById(Long id);
}
