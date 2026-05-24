package pos.infrastructure.mongodb.repository;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import java.math.BigDecimal;
import org.bson.Document;
import org.bson.types.ObjectId;
import pos.infrastructure.mongodb.mapper.SaleConfigMapper;
import pos.sale.config.model.SaleConfig;
import pos.sale.config.repository.SaleConfigRepository;

public class SaleConfigMongoRepository implements SaleConfigRepository {

  private final MongoCollection<Document> collection;

  public SaleConfigMongoRepository(MongoDatabase database) {
    this.collection = database.getCollection("sale_config");
  }

  @Override
  public SaleConfig getConfig() {
    Document doc = collection.find().first();

    if (doc == null) {
      return save(createDefault());
    }

    return SaleConfigMapper.toEntity(doc);
  }

  @Override
  public SaleConfig save(SaleConfig config) {
    if (config.getId() == null || config.getId().trim().isEmpty()) {
      config.setId(new ObjectId().toHexString());
    }

    Document doc = SaleConfigMapper.toDocument(config);

    collection.replaceOne(Filters.eq("_id", new ObjectId(config.getId())), doc, new ReplaceOptions().upsert(true));

    return config;
  }

  private SaleConfig createDefault() {
    SaleConfig config = new SaleConfig();
    config.setTaxRate(new BigDecimal("0.16"));
    config.setDefaultDiscount(BigDecimal.ZERO);

    return config;
  }
}
