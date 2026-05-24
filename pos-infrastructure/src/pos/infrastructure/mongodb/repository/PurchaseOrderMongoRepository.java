package pos.infrastructure.mongodb.repository;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.bson.Document;
import org.bson.types.ObjectId;
import pos.infrastructure.mongodb.mapper.PurchaseOrderMapper;
import pos.purchase.model.PurchaseOrder;
import pos.purchase.model.PurchaseOrderStatus;
import pos.purchase.repository.PurchaseOrderRepository;

public class PurchaseOrderMongoRepository implements PurchaseOrderRepository {

  private final MongoCollection<Document> collection;

  public PurchaseOrderMongoRepository(MongoDatabase database) {
    this.collection = database.getCollection("purchase_order");
  }

  @Override
  public List<PurchaseOrder> findAll() {
    List<PurchaseOrder> orders = new ArrayList<>();

    for (Document doc : collection.find()) {
      orders.add(PurchaseOrderMapper.toEntity(doc));
    }

    return orders;
  }

  @Override
  public List<PurchaseOrder> findBySupplierId(String supplierId) {
    List<PurchaseOrder> orders = new ArrayList<>();

    if (supplierId == null) {
      return orders;
    }

    for (Document doc : collection.find(Filters.eq("supplierId", supplierId))) {
      orders.add(PurchaseOrderMapper.toEntity(doc));
    }

    return orders;
  }

  @Override
  public List<PurchaseOrder> findByStatus(PurchaseOrderStatus status) {
    List<PurchaseOrder> orders = new ArrayList<>();

    if (status == null) {
      return orders;
    }

    for (Document doc : collection.find(Filters.eq("status", status.name()))) {
      orders.add(PurchaseOrderMapper.toEntity(doc));
    }

    return orders;
  }

  @Override
  public Optional<PurchaseOrder> findById(String id) {
    if (id == null || !ObjectId.isValid(id)) {
      return Optional.empty();
    }

    Document doc = collection.find(Filters.eq("_id", new ObjectId(id))).first();

    return Optional.ofNullable(doc).map(PurchaseOrderMapper::toEntity);
  }

  @Override
  public PurchaseOrder create(PurchaseOrder order) {
    if (order.getId() == null || order.getId().trim().isEmpty()) {
      order.setId(new ObjectId().toHexString());
    }

    collection.insertOne(PurchaseOrderMapper.toDocument(order));

    return order;
  }

  @Override
  public PurchaseOrder update(PurchaseOrder order) {
    if (order.getId() == null || !ObjectId.isValid(order.getId())) {
      throw new IllegalArgumentException("Cannot update a PurchaseOrder without a valid hex ObjectId");
    }

    collection.replaceOne(Filters.eq("_id", new ObjectId(order.getId())), PurchaseOrderMapper.toDocument(order));

    return order;
  }
}
