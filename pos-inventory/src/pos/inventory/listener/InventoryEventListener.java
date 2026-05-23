package pos.inventory.listener;

import pos.inventory.repository.InventoryRepository;
import pos.product.listener.ProductEventListener;

public class InventoryEventListener implements ProductEventListener {

  private final InventoryRepository inventoryRepository;

  public InventoryEventListener(InventoryRepository inventoryRepository) {
    this.inventoryRepository = inventoryRepository;
  }

  @Override
  public void onProductDeleted(String productId) {
    inventoryRepository.findByProductId(productId).ifPresent(stockItem -> inventoryRepository.deleteById(stockItem.getId()));
  }
}
