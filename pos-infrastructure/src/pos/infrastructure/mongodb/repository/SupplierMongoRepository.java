package pos.infrastructure.mongodb.repository;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.bson.Document;
import org.bson.types.ObjectId;
import pos.infrastructure.mongodb.mapper.SupplierMapper;
import pos.supplier.model.Supplier;
import pos.supplier.repository.SupplierRepository;

public class SupplierMongoRepository implements SupplierRepository {

  private final MongoCollection<Document> collection;

  public SupplierMongoRepository(MongoDatabase database) {
    this.collection = database.getCollection("supplier");
  }

  @Override
  public List<Supplier> findAll() {
    List<Supplier> suppliers = new ArrayList<>();

    for (Document doc : collection.find()) {
      suppliers.add(SupplierMapper.toEntity(doc));
    }

    return suppliers;
  }

  @Override
  public Optional<Supplier> findById(String id) {
    if (id == null || !ObjectId.isValid(id)) {
      return Optional.empty();
    }

    Document doc = collection.find(Filters.eq("_id", new ObjectId(id))).first();

    return Optional.ofNullable(doc).map(SupplierMapper::toEntity);
  }

  @Override
  public Optional<Supplier> findByName(String name) {
    if (name == null) {
      return Optional.empty();
    }

    Document doc = collection.find(Filters.eq("name", name)).first();

    return Optional.ofNullable(doc).map(SupplierMapper::toEntity);
  }

  @Override
  public Supplier create(Supplier supplier) {
    if (supplier.getId() == null || supplier.getId().trim().isEmpty()) {
      supplier.setId(new ObjectId().toHexString());
    }

    Document doc = SupplierMapper.toDocument(supplier);

    collection.insertOne(doc);

    return supplier;
  }

  @Override
  public Supplier update(Supplier supplier) {
    if (supplier.getId() == null || !ObjectId.isValid(supplier.getId())) {
      throw new IllegalArgumentException("Cannot update a Supplier without a valid hex ObjectId");
    }

    Document doc = SupplierMapper.toDocument(supplier);

    collection.replaceOne(Filters.eq("_id", new ObjectId(supplier.getId())), doc);

    return supplier;
  }

  @Override
  public void deleteById(String id) {
    if (id == null || !ObjectId.isValid(id)) {
      throw new IllegalArgumentException("Cannot delete: Invalid hex ObjectId format");
    }

    collection.deleteOne(Filters.eq("_id", new ObjectId(id)));
  }
}
