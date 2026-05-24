package pos.infrastructure.mongodb.mapper;

import java.math.BigDecimal;
import org.bson.Document;
import org.bson.types.Decimal128;
import org.bson.types.ObjectId;
import pos.sale.config.model.SaleConfig;

public class SaleConfigMapper {

  public static SaleConfig toEntity(Document doc) {
    if (doc == null) {
      return null;
    }

    SaleConfig config = new SaleConfig();

    if (doc.getObjectId("_id") != null) {
      config.setId(doc.getObjectId("_id").toHexString());
    }

    config.setTaxRate(doc.get("taxRate") != null ? doc.get("taxRate", Decimal128.class).bigDecimalValue() : BigDecimal.ZERO);
    config.setDefaultDiscount(doc.get("defaultDiscount") != null ? doc.get("defaultDiscount", Decimal128.class).bigDecimalValue() : BigDecimal.ZERO);

    return config;
  }

  public static Document toDocument(SaleConfig config) {
    if (config == null) {
      return null;
    }

    Document doc = new Document();

    if (config.getId() != null && ObjectId.isValid(config.getId())) {
      doc.append("_id", new ObjectId(config.getId()));
    }

    if (config.getTaxRate() != null) {
      doc.append("taxRate", new Decimal128(config.getTaxRate()));
    }

    if (config.getDefaultDiscount() != null) {
      doc.append("defaultDiscount", new Decimal128(config.getDefaultDiscount()));
    }

    return doc;
  }
}
