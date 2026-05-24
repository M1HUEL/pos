package pos.purchase.repository;

import java.util.List;
import java.util.Optional;
import pos.purchase.model.PurchaseOrder;

public interface PurchaseOrderRepository {

  List<PurchaseOrder> findAll();

  Optional<PurchaseOrder> findById(String id);

  PurchaseOrder create(PurchaseOrder order);

  PurchaseOrder update(PurchaseOrder order);
}
