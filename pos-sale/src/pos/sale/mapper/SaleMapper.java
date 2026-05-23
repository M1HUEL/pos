package pos.sale.mapper;

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
import pos.sale.model.Sale;
import pos.sale.model.SaleItem;
import pos.sale.model.SaleStatus;

public class SaleMapper {

  public static Sale toEntity(Document doc) {
    if (doc == null) {
      return null;
    }
    Sale sale = new Sale();
    if (doc.getObjectId("_id") != null) {
      sale.setId(doc.getObjectId("_id").toHexString());
    }
    sale.setSaleNumber(doc.getString("saleNumber"));

    if (doc.getDate("dateTime") != null) {
      sale.setDateTime(LocalDateTime.ofInstant(doc.getDate("dateTime").toInstant(), ZoneId.systemDefault()));
    }

    sale.setTotalAmount(doc.get("totalAmount") != null ? doc.get("totalAmount", Decimal128.class).bigDecimalValue() : BigDecimal.ZERO);
    sale.setTaxAmount(doc.get("taxAmount") != null ? doc.get("taxAmount", Decimal128.class).bigDecimalValue() : BigDecimal.ZERO);
    sale.setDiscountAmount(doc.get("discountAmount") != null ? doc.get("discountAmount", Decimal128.class).bigDecimalValue() : BigDecimal.ZERO);
    sale.setPaymentMethod(doc.getString("paymentMethod"));

    if (doc.getString("status") != null) {
      sale.setStatus(SaleStatus.valueOf(doc.getString("status")));
    }

    List<Document> itemDocs = doc.getList("items", Document.class);
    if (itemDocs != null) {
      sale.setItems(itemDocs.stream().map(SaleMapper::toSaleItemEntity).collect(Collectors.toList()));
    } else {
      sale.setItems(new ArrayList<>());
    }

    return sale;
  }

  public static Document toDocument(Sale sale) {
    if (sale == null) {
      return null;
    }
    Document doc = new Document();
    if (sale.getId() != null && ObjectId.isValid(sale.getId())) {
      doc.append("_id", new ObjectId(sale.getId()));
    }
    doc.append("saleNumber", sale.getSaleNumber());

    if (sale.getDateTime() != null) {
      doc.append("dateTime", Date.from(sale.getDateTime().atZone(ZoneId.systemDefault()).toInstant()));
    }

    if (sale.getTotalAmount() != null) {
      doc.append("totalAmount", new Decimal128(sale.getTotalAmount()));
    }
    if (sale.getTaxAmount() != null) {
      doc.append("taxAmount", new Decimal128(sale.getTaxAmount()));
    }
    if (sale.getDiscountAmount() != null) {
      doc.append("discountAmount", new Decimal128(sale.getDiscountAmount()));
    }
    doc.append("paymentMethod", sale.getPaymentMethod());
    if (sale.getStatus() != null) {
      doc.append("status", sale.getStatus().name());
    }

    if (sale.getItems() != null) {
      List<Document> itemDocs = sale.getItems().stream().map(SaleMapper::toSaleItemDocument).collect(Collectors.toList());
      doc.append("items", itemDocs);
    }

    return doc;
  }

  private static SaleItem toSaleItemEntity(Document doc) {
    if (doc == null) {
      return null;
    }
    SaleItem item = new SaleItem();
    if (doc.getObjectId("id") != null) {
      item.setId(doc.getObjectId("id").toHexString());
    }
    item.setProductId(doc.getString("productId"));
    item.setProductName(doc.getString("productName"));
    item.setQuantity(doc.getInteger("quantity"));
    item.setUnitPrice(doc.get("unitPrice") != null ? doc.get("unitPrice", Decimal128.class).bigDecimalValue() : BigDecimal.ZERO);
    item.setDiscountAmount(doc.get("discountAmount") != null ? doc.get("discountAmount", Decimal128.class).bigDecimalValue() : BigDecimal.ZERO);
    item.setSubTotal(doc.get("subTotal") != null ? doc.get("subTotal", Decimal128.class).bigDecimalValue() : BigDecimal.ZERO);
    return item;
  }

  private static Document toSaleItemDocument(SaleItem item) {
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
    if (item.getUnitPrice() != null) {
      doc.append("unitPrice", new Decimal128(item.getUnitPrice()));
    }
    if (item.getDiscountAmount() != null) {
      doc.append("discountAmount", new Decimal128(item.getDiscountAmount()));
    }
    if (item.getSubTotal() != null) {
      doc.append("subTotal", new Decimal128(item.getSubTotal()));
    }
    return doc;
  }
}
