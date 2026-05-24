package pos.purchase.service;

import java.util.List;
import java.util.Optional;
import pos.purchase.model.PurchaseOrder;
import pos.purchase.model.PurchaseOrderStatus;

public interface PurchaseOrderService {

  List<PurchaseOrder> getAllOrders();

  List<PurchaseOrder> getOrdersBySupplierId(String supplierId);

  List<PurchaseOrder> getOrdersByStatus(PurchaseOrderStatus status);

  Optional<PurchaseOrder> getOrderById(String id);

  PurchaseOrder createOrder(PurchaseOrder order);

  void receiveOrder(String id);

  void cancelOrder(String id);
}
