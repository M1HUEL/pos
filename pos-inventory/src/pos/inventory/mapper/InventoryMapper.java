package pos.inventory.mapper;

import org.bson.Document;
import org.bson.types.ObjectId;
import pos.inventory.model.StockItem;

public class InventoryMapper {

  public static StockItem toEntity(Document doc) {
    if (doc == null) {
      return null;
    }

    StockItem item = new StockItem();

    if (doc.getObjectId("_id") != null) {
      item.setId(doc.getObjectId("_id").toHexString());
    }

    item.setProductId(doc.getString("productId"));
    item.setStock(doc.getInteger("stock"));
    item.setMinStock(doc.getInteger("minStock"));

    return item;
  }

  public static Document toDocument(StockItem item) {
    if (item == null) {
      return null;
    }

    Document doc = new Document();

    if (item.getId() != null && ObjectId.isValid(item.getId())) {
      doc.append("_id", new ObjectId(item.getId()));
    }

    doc.append("productId", item.getProductId());
    doc.append("stock", item.getStock());
    doc.append("minStock", item.getMinStock());

    return doc;
  }
}
