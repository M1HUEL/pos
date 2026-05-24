package pos.purchase.repository;

import java.util.List;
import java.util.Optional;
import pos.purchase.model.PurchaseOrder;
import pos.purchase.model.PurchaseOrderStatus;

public interface PurchaseOrderRepository {

  List<PurchaseOrder> findAll();

  List<PurchaseOrder> findBySupplierId(String supplierId);

  List<PurchaseOrder> findByStatus(PurchaseOrderStatus status);

  Optional<PurchaseOrder> findById(String id);

  PurchaseOrder create(PurchaseOrder order);

  PurchaseOrder update(PurchaseOrder order);
}
