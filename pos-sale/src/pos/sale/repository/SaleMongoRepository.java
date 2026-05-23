package pos.sale.repository;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.bson.Document;
import org.bson.types.ObjectId;
import pos.sale.mapper.SaleMapper;
import pos.sale.model.Sale;

public class SaleMongoRepository implements SaleRepository {

  private final MongoCollection<Document> collection;

  public SaleMongoRepository(MongoDatabase database) {
    this.collection = database.getCollection("sale");
  }

  @Override
  public List<Sale> findAll() {
    List<Sale> sales = new ArrayList<>();
    for (Document doc : collection.find()) {
      sales.add(SaleMapper.toEntity(doc));
    }
    return sales;
  }

  @Override
  public Optional<Sale> findById(String id) {
    if (id == null || !ObjectId.isValid(id)) {
      return Optional.empty();
    }
    Document doc = collection.find(Filters.eq("_id", new ObjectId(id))).first();
    return Optional.ofNullable(doc).map(SaleMapper::toEntity);
  }

  @Override
  public Optional<Sale> findBySaleNumber(String saleNumber) {
    if (saleNumber == null) {
      return Optional.empty();
    }
    Document doc = collection.find(Filters.eq("saleNumber", saleNumber)).first();
    return Optional.ofNullable(doc).map(SaleMapper::toEntity);
  }

  @Override
  public Sale create(Sale sale) {
    if (sale.getId() == null || sale.getId().trim().isEmpty()) {
      sale.setId(new ObjectId().toHexString());
    }
    Document doc = SaleMapper.toDocument(sale);
    collection.insertOne(doc);
    return sale;
  }

  @Override
  public Sale update(Sale sale) {
    if (sale.getId() == null || !ObjectId.isValid(sale.getId())) {
      throw new IllegalArgumentException("Cannot update a Sale without a valid hex ObjectId");
    }
    Document doc = SaleMapper.toDocument(sale);
    collection.replaceOne(Filters.eq("_id", new ObjectId(sale.getId())), doc);
    return sale;
  }

  @Override
  public void deleteById(String id) {
    if (id == null || !ObjectId.isValid(id)) {
      throw new IllegalArgumentException("Cannot delete: Invalid hex ObjectId format");
    }
    collection.deleteOne(Filters.eq("_id", new ObjectId(id)));
  }
}
