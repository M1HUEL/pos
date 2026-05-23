package pos.infrastructure.mongodb.repository;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.bson.Document;
import org.bson.types.ObjectId;
import pos.infrastructure.mongodb.mapper.InventoryMapper;
import pos.inventory.model.StockItem;
import pos.inventory.repository.InventoryRepository;

public class InventoryMongoRepository implements InventoryRepository {

  private final MongoCollection<Document> collection;

  public InventoryMongoRepository(MongoDatabase database) {
    this.collection = database.getCollection("stock_item");
  }

  @Override
  public List<StockItem> findAll() {
    List<StockItem> stockItems = new ArrayList<>();

    for (Document doc : collection.find()) {
      stockItems.add(InventoryMapper.toEntity(doc));
    }

    return stockItems;
  }

  @Override
  public List<StockItem> findLowStock() {
    List<StockItem> stockItems = new ArrayList<>();

    Document filter = new Document("$expr", new Document("$lte", java.util.Arrays.asList("$stock", "$minStock")));

    for (Document doc : collection.find(filter)) {
      stockItems.add(InventoryMapper.toEntity(doc));
    }

    return stockItems;
  }

  @Override
  public Optional<StockItem> findById(String id) {
    if (id == null || !ObjectId.isValid(id)) {
      return Optional.empty();
    }

    Document doc = collection.find(Filters.eq("_id", new ObjectId(id))).first();

    return Optional.ofNullable(doc).map(InventoryMapper::toEntity);
  }

  @Override
  public Optional<StockItem> findByProductId(String productId) {
    if (productId == null) {
      return Optional.empty();
    }

    Document doc = collection.find(Filters.eq("productId", productId)).first();

    return Optional.ofNullable(doc).map(InventoryMapper::toEntity);
  }

  @Override
  public StockItem create(StockItem stockItem) {
    if (stockItem.getId() == null || stockItem.getId().trim().isEmpty()) {
      stockItem.setId(new ObjectId().toHexString());
    }

    Document doc = InventoryMapper.toDocument(stockItem);

    collection.insertOne(doc);

    return stockItem;
  }

  @Override
  public StockItem update(StockItem stockItem) {
    if (stockItem.getId() == null || !ObjectId.isValid(stockItem.getId())) {
      throw new IllegalArgumentException("Cannot update a StockItem without a valid hex ObjectId");
    }

    Document doc = InventoryMapper.toDocument(stockItem);

    collection.replaceOne(Filters.eq("_id", new ObjectId(stockItem.getId())), doc);

    return stockItem;
  }

  @Override
  public void deleteById(String id) {
    if (id == null || !ObjectId.isValid(id)) {
      throw new IllegalArgumentException("Cannot delete: Invalid hex ObjectId format");
    }

    collection.deleteOne(Filters.eq("_id", new ObjectId(id)));
  }
}
