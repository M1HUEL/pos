package pos.infrastructure.mongodb.repository;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.bson.Document;
import org.bson.types.ObjectId;
import pos.infrastructure.mongodb.mapper.ProductMapper;
import pos.product.model.Product;
import pos.product.repository.ProductRepository;

public class ProductMongoRepository implements ProductRepository {

  private final MongoCollection<Document> collection;

  public ProductMongoRepository(MongoDatabase database) {
    this.collection = database.getCollection("product");
  }

  @Override
  public List<Product> findAll() {
    List<Product> products = new ArrayList<>();

    for (Document doc : collection.find()) {
      products.add(ProductMapper.toEntity(doc));
    }

    return products;
  }

  @Override
  public Optional<Product> findById(String id) {
    if (id == null || !ObjectId.isValid(id)) {
      return Optional.empty();
    }

    Document doc = collection.find(Filters.eq("_id", new ObjectId(id))).first();

    return Optional.ofNullable(doc).map(ProductMapper::toEntity);
  }

  @Override
  public Optional<Product> findBySku(String sku) {
    if (sku == null) {
      return Optional.empty();
    }

    Document doc = collection.find(Filters.eq("sku", sku)).first();

    return Optional.ofNullable(doc).map(ProductMapper::toEntity);
  }

  @Override
  public Product create(Product product) {
    if (product.getId() == null || product.getId().trim().isEmpty()) {
      product.setId(new ObjectId().toHexString());
    }

    Document doc = ProductMapper.toDocument(product);

    collection.insertOne(doc);

    return product;
  }

  @Override
  public Product update(Product product) {
    if (product.getId() == null || !ObjectId.isValid(product.getId())) {
      throw new IllegalArgumentException("Cannot update a Product without a valid hex ObjectId");
    }

    Document doc = ProductMapper.toDocument(product);

    collection.replaceOne(Filters.eq("_id", new ObjectId(product.getId())), doc);

    return product;
  }

  @Override
  public void deleteById(String id) {
    if (id == null || !ObjectId.isValid(id)) {
      throw new IllegalArgumentException("Cannot delete: Invalid hex ObjectId format");
    }

    collection.deleteOne(Filters.eq("_id", new ObjectId(id)));
  }

}
