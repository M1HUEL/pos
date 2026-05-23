package pos.infrastructure.mongodb.mapper;

import org.bson.Document;
import org.bson.types.Decimal128;
import org.bson.types.ObjectId;
import pos.product.model.Product;

public class ProductMapper {

  public static Product toEntity(Document doc) {
    if (doc == null) {
      return null;
    }

    Product product = new Product();
    if (doc.getObjectId("_id") != null) {
      product.setId(doc.getObjectId("_id").toHexString());
    }
    product.setSku(doc.getString("sku"));
    product.setName(doc.getString("name"));
    product.setDescription(doc.getString("description"));
    if (doc.get("price") != null) {
      product.setPrice(doc.get("price", Decimal128.class).bigDecimalValue());
    }
    product.setActive(doc.getBoolean("active"));
    return product;
  }

  public static Document toDocument(Product product) {
    if (product == null) {
      return null;
    }

    Document doc = new Document();
    if (product.getId() != null && ObjectId.isValid(product.getId())) {
      doc.append("_id", new ObjectId(product.getId()));
    }
    doc.append("sku", product.getSku());
    doc.append("name", product.getName());
    doc.append("description", product.getDescription());
    if (product.getPrice() != null) {
      doc.append("price", new Decimal128(product.getPrice()));
    }
    doc.append("active", product.getActive());
    return doc;
  }
}
