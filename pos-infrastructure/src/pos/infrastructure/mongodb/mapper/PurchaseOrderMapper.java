package pos.infrastructure.mongodb.mapper;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import org.bson.Document;
import org.bson.types.Decimal128;
import org.bson.types.ObjectId;
import pos.purchase.model.PurchaseOrder;
import pos.purchase.model.PurchaseOrderItem;
import pos.purchase.model.PurchaseOrderStatus;

public class PurchaseOrderMapper {

  public static PurchaseOrder toEntity(Document doc) {
    if (doc == null) {
      return null;
    }

    PurchaseOrder order = new PurchaseOrder();

    if (doc.getObjectId("_id") != null) {
      order.setId(doc.getObjectId("_id").toHexString());
    }

    order.setOrderNumber(doc.getString("orderNumber"));
    order.setSupplierId(doc.getString("supplierId"));
    order.setSupplierName(doc.getString("supplierName"));

    if (doc.getDate("date") != null) {
      order.setDate(LocalDateTime.ofInstant(doc.getDate("date").toInstant(), ZoneId.systemDefault()));
    }

    if (doc.getString("status") != null) {
      order.setStatus(PurchaseOrderStatus.valueOf(doc.getString("status")));
    }

    order.setTotalCost(doc.get("totalCost") != null ? doc.get("totalCost", Decimal128.class).bigDecimalValue() : BigDecimal.ZERO);

    List<Document> itemDocs = doc.getList("items", Document.class);

    if (itemDocs != null) {
      order.setItems(itemDocs.stream().map(PurchaseOrderMapper::toItemEntity).collect(Collectors.toList()));
    } else {
      order.setItems(new ArrayList<>());
    }

    return order;
  }

  public static Document toDocument(PurchaseOrder order) {
    if (order == null) {
      return null;
    }

    Document doc = new Document();

    if (order.getId() != null && ObjectId.isValid(order.getId())) {
      doc.append("_id", new ObjectId(order.getId()));
    }

    doc.append("orderNumber", order.getOrderNumber());
    doc.append("supplierId", order.getSupplierId());
    doc.append("supplierName", order.getSupplierName());

    if (order.getDate() != null) {
      doc.append("date", Date.from(order.getDate().atZone(ZoneId.systemDefault()).toInstant()));
    }

    if (order.getStatus() != null) {
      doc.append("status", order.getStatus().name());
    }

    if (order.getTotalCost() != null) {
      doc.append("totalCost", new Decimal128(order.getTotalCost()));
    }

    if (order.getItems() != null) {
      doc.append("items", order.getItems().stream().map(PurchaseOrderMapper::toItemDocument).collect(Collectors.toList()));
    }

    return doc;
  }

  private static PurchaseOrderItem toItemEntity(Document doc) {
    if (doc == null) {
      return null;
    }

    PurchaseOrderItem item = new PurchaseOrderItem();

    if (doc.getObjectId("id") != null) {
      item.setId(doc.getObjectId("id").toHexString());
    }

    item.setProductId(doc.getString("productId"));
    item.setProductName(doc.getString("productName"));
    item.setQuantity(doc.getInteger("quantity"));
    item.setUnitCost(doc.get("unitCost") != null ? doc.get("unitCost", Decimal128.class).bigDecimalValue() : BigDecimal.ZERO);
    item.setTotalCost(doc.get("totalCost") != null ? doc.get("totalCost", Decimal128.class).bigDecimalValue() : BigDecimal.ZERO);

    return item;
  }

  private static Document toItemDocument(PurchaseOrderItem item) {
    if (item == null) {
      return null;
    }

    Document doc = new Document();

    if (item.getId() != null && ObjectId.isValid(item.getId())) {
      doc.append("id", new ObjectId(item.getId()));
    } else {
      doc.append("id", new ObjectId());
    }

    doc.append("productId", item.getProductId());
    doc.append("productName", item.getProductName());
    doc.append("quantity", item.getQuantity());

    if (item.getUnitCost() != null) {
      doc.append("unitCost", new Decimal128(item.getUnitCost()));
    }

    if (item.getTotalCost() != null) {
      doc.append("totalCost", new Decimal128(item.getTotalCost()));
    }

    return doc;
  }
}
