package pos.infrastructure.mongodb.mapper;

import org.bson.Document;
import org.bson.types.ObjectId;
import pos.supplier.model.Supplier;

public class SupplierMapper {

  public static Supplier toEntity(Document doc) {
    if (doc == null) {
      return null;
    }

    Supplier supplier = new Supplier();

    if (doc.getObjectId("_id") != null) {
      supplier.setId(doc.getObjectId("_id").toHexString());
    }

    supplier.setName(doc.getString("name"));
    supplier.setContactName(doc.getString("contactName"));
    supplier.setPhone(doc.getString("phone"));
    supplier.setEmail(doc.getString("email"));
    supplier.setAddress(doc.getString("address"));
    supplier.setActive(doc.getBoolean("active"));

    return supplier;
  }

  public static Document toDocument(Supplier supplier) {
    if (supplier == null) {
      return null;
    }

    Document doc = new Document();

    if (supplier.getId() != null && ObjectId.isValid(supplier.getId())) {
      doc.append("_id", new ObjectId(supplier.getId()));
    }

    doc.append("name", supplier.getName());
    doc.append("contactName", supplier.getContactName());
    doc.append("phone", supplier.getPhone());
    doc.append("email", supplier.getEmail());
    doc.append("address", supplier.getAddress());
    doc.append("active", supplier.getActive());

    return doc;
  }
}
