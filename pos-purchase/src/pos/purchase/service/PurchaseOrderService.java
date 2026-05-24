package pos.purchase.service;

import java.util.List;
import java.util.Optional;
import pos.purchase.model.PurchaseOrder;

public interface PurchaseOrderService {

  List<PurchaseOrder> getAllOrders();

  Optional<PurchaseOrder> getOrderById(String id);

  PurchaseOrder createOrder(PurchaseOrder order);

  void receiveOrder(String id);

  void cancelOrder(String id);
}
