package pos.ui.controller;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import pos.product.model.Product;
import pos.product.service.ProductService;
import pos.purchase.model.PurchaseOrder;
import pos.purchase.model.PurchaseOrderItem;
import pos.purchase.service.PurchaseOrderService;
import pos.supplier.model.Supplier;
import pos.supplier.service.SupplierService;

public class PurchaseOrderController {

  private final PurchaseOrderService purchaseOrderService;
  private final SupplierService supplierService;
  private final ProductService productService;
  private final List<PurchaseOrderItem> currentItems = new ArrayList<>();

  public PurchaseOrderController(PurchaseOrderService purchaseOrderService, SupplierService supplierService, ProductService productService) {
    this.purchaseOrderService = purchaseOrderService;
    this.supplierService = supplierService;
    this.productService = productService;
  }

  public List<Supplier> getAllSuppliers() {
    return supplierService.getAllSuppliers();
  }

  public List<Product> getAllProducts() {
    return productService.getAllProducts();
  }

  public List<PurchaseOrder> getAllOrders() {
    return purchaseOrderService.getAllOrders();
  }

  public PurchaseOrderItem buildItem(Product product, int quantity, BigDecimal unitCost) {
    if (quantity <= 0) {
      throw new IllegalArgumentException("Quantity must be greater than zero");
    }

    if (unitCost == null || unitCost.compareTo(BigDecimal.ZERO) < 0) {
      throw new IllegalArgumentException("Unit cost cannot be null or negative");
    }

    BigDecimal totalCost = unitCost.multiply(BigDecimal.valueOf(quantity)).setScale(2, RoundingMode.HALF_UP);

    PurchaseOrderItem item = new PurchaseOrderItem();
    item.setProductId(product.getId());
    item.setProductName(product.getName());
    item.setQuantity(quantity);
    item.setUnitCost(unitCost);
    item.setTotalCost(totalCost);

    return item;
  }

  public void addItem(PurchaseOrderItem newItem) {
    for (PurchaseOrderItem existing : currentItems) {
      if (existing.getProductId().equals(newItem.getProductId())) {
        int mergedQty = existing.getQuantity() + newItem.getQuantity();

        BigDecimal mergedTotal = existing.getUnitCost().multiply(BigDecimal.valueOf(mergedQty)).setScale(2, RoundingMode.HALF_UP);

        existing.setQuantity(mergedQty);
        existing.setTotalCost(mergedTotal);

        return;
      }
    }
    currentItems.add(newItem);
  }

  public void removeItem(int index) {
    currentItems.remove(index);
  }

  public List<PurchaseOrderItem> getCurrentItems() {
    return new ArrayList<>(currentItems);
  }

  public BigDecimal calculateTotalCost() {
    return currentItems.stream().map(PurchaseOrderItem::getTotalCost).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);
  }

  public PurchaseOrder createOrder(Supplier supplier) {
    if (currentItems.isEmpty()) {
      throw new IllegalStateException("Cannot create an order with no items");
    }

    PurchaseOrder order = new PurchaseOrder();
    order.setOrderNumber(UUID.randomUUID().toString());
    order.setSupplierId(supplier.getId());
    order.setSupplierName(supplier.getName());
    order.setItems(new ArrayList<>(currentItems));
    order.setTotalCost(calculateTotalCost());

    PurchaseOrder created = purchaseOrderService.createOrder(order);

    currentItems.clear();

    return created;
  }

  public void receiveOrder(String id) {
    purchaseOrderService.receiveOrder(id);
  }

  public void cancelOrder(String id) {
    purchaseOrderService.cancelOrder(id);
  }

  public void clearItems() {
    currentItems.clear();
  }
}
