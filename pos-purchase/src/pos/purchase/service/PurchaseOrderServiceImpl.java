package pos.purchase.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import pos.inventory.service.InventoryService;
import pos.purchase.exception.PurchaseOrderException;
import pos.purchase.model.PurchaseOrder;
import pos.purchase.model.PurchaseOrderItem;
import pos.purchase.model.PurchaseOrderStatus;
import pos.purchase.repository.PurchaseOrderRepository;
import pos.purchase.validation.PurchaseOrderValidator;
import pos.supplier.service.SupplierService;

public class PurchaseOrderServiceImpl implements PurchaseOrderService {

  private final PurchaseOrderRepository purchaseOrderRepository;
  private final PurchaseOrderValidator purchaseOrderValidator;
  private final InventoryService inventoryService;
  private final SupplierService supplierService;

  public PurchaseOrderServiceImpl(PurchaseOrderRepository purchaseOrderRepository, PurchaseOrderValidator purchaseOrderValidator, InventoryService inventoryService, SupplierService supplierService) {
    this.purchaseOrderRepository = purchaseOrderRepository;
    this.purchaseOrderValidator = purchaseOrderValidator;
    this.inventoryService = inventoryService;
    this.supplierService = supplierService;
  }

  @Override
  public List<PurchaseOrder> getAllOrders() {
    return purchaseOrderRepository.findAll();
  }

  @Override
  public Optional<PurchaseOrder> getOrderById(String id) {
    purchaseOrderValidator.validateId(id);
    return purchaseOrderRepository.findById(id);
  }

  @Override
  public PurchaseOrder createOrder(PurchaseOrder order) {
    purchaseOrderValidator.validate(order);
    supplierService.getSupplierById(order.getSupplierId()).orElseThrow(() -> new PurchaseOrderException("Supplier not found with ID: " + order.getSupplierId()));

    if (order.getDate() == null) {
      order.setDate(LocalDateTime.now());
    }

    order.setStatus(PurchaseOrderStatus.PENDING);

    return purchaseOrderRepository.create(order);
  }

  @Override
  public void receiveOrder(String id) {
    purchaseOrderValidator.validateId(id);
    PurchaseOrder order = purchaseOrderRepository.findById(id).orElseThrow(() -> new PurchaseOrderException("Purchase order not found with ID: " + id));

    if (PurchaseOrderStatus.PENDING != order.getStatus()) {
      throw new PurchaseOrderException("Only PENDING orders can be received. Current status: " + order.getStatus());
    }

    List<PurchaseOrderItem> processedItems = new ArrayList<>();

    try {
      for (PurchaseOrderItem item : order.getItems()) {
        inventoryService.increaseStock(item.getProductId(), item.getQuantity());

        processedItems.add(item);
      }
    } catch (RuntimeException e) {
      for (PurchaseOrderItem processed : processedItems) {
        inventoryService.reduceStock(processed.getProductId(), processed.getQuantity());
      }

      throw new PurchaseOrderException("Failed to receive order. " + e.getMessage(), e);
    }

    order.setStatus(PurchaseOrderStatus.RECEIVED);

    purchaseOrderRepository.update(order);
  }

  @Override
  public void cancelOrder(String id) {
    purchaseOrderValidator.validateId(id);

    PurchaseOrder order = purchaseOrderRepository.findById(id).orElseThrow(() -> new PurchaseOrderException("Purchase order not found with ID: " + id));

    if (PurchaseOrderStatus.PENDING != order.getStatus()) {
      throw new PurchaseOrderException("Only PENDING orders can be canceled. Current status: " + order.getStatus());
    }

    order.setStatus(PurchaseOrderStatus.CANCELED);

    purchaseOrderRepository.update(order);
  }
}
