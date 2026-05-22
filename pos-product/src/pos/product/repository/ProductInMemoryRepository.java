package pos.product.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import pos.product.model.Product;

public class ProductInMemoryRepository implements ProductRepository {

  private final Map<Long, Product> database = new ConcurrentHashMap<>();
  private final AtomicLong idGenerator = new AtomicLong(1);

  @Override
  public List<Product> findAll() {
    return new ArrayList<>(database.values());
  }

  @Override
  public Optional<Product> findById(Long id) {
    return Optional.ofNullable(database.get(id));
  }

  @Override
  public Optional<Product> findBySku(String sku) {
    if (sku == null) {
      return Optional.empty();
    }

    return database.values().stream().filter(product -> sku.equals(product.getSku())).findFirst();
  }

  @Override
  public Product save(Product product) {
    if (product.getId() == null) {
      product.setId(idGenerator.getAndIncrement());
    }

    database.put(product.getId(), product);

    return product;
  }

  @Override
  public void deleteById(Long id) {
    database.remove(id);
  }

}
