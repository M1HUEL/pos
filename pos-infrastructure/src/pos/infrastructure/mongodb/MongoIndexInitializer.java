package pos.infrastructure.mongodb;

import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;

public class MongoIndexInitializer {

  private final MongoDatabase database;

  public MongoIndexInitializer(MongoDatabase database) {
    this.database = database;
  }

  public void initialize() {
    createProductIndexes();
    createSaleIndexes();
    createStockIndexes();
    createSupplierIndexes();
    createPurchaseOrderIndexes();
  }

  private void createProductIndexes() {
    database.getCollection("product")
      .createIndex(Indexes.ascending("sku"), new IndexOptions().unique(true));
  }

  private void createSaleIndexes() {
    database.getCollection("sale")
      .createIndex(Indexes.ascending("saleNumber"), new IndexOptions().unique(true));
  }

  private void createStockIndexes() {
    database.getCollection("stock_item")
      .createIndex(Indexes.ascending("productId"), new IndexOptions().unique(true));
  }

  private void createSupplierIndexes() {
    database.getCollection("supplier")
      .createIndex(Indexes.ascending("name"), new IndexOptions().unique(true));
  }

  private void createPurchaseOrderIndexes() {
    database.getCollection("purchase_order")
      .createIndex(Indexes.ascending("orderNumber"), new IndexOptions().unique(true));
  }
}
