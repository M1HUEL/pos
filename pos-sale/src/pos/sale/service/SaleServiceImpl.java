package pos.sale.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import pos.inventory.service.InventoryService;
import pos.sale.exception.SaleException;
import pos.sale.model.Sale;
import pos.sale.model.SaleItem;
import pos.sale.model.SaleStatus;
import pos.sale.repository.SaleRepository;
import pos.sale.validation.SaleValidator;

public class SaleServiceImpl implements SaleService {

  private final SaleRepository saleRepository;
  private final SaleValidator saleValidator;
  private final InventoryService inventoryService;

  public SaleServiceImpl(SaleRepository saleRepository, SaleValidator saleValidator,
    InventoryService inventoryService) {
    this.saleRepository = saleRepository;
    this.saleValidator = saleValidator;
    this.inventoryService = inventoryService;
  }

  @Override
  public List<Sale> getAllSales() {
    return saleRepository.findAll();
  }

  @Override
  public Optional<Sale> getSaleById(String id) {
    saleValidator.validateId(id);
    return saleRepository.findById(id);
  }

  @Override
  public Optional<Sale> getSaleByNumber(String saleNumber) {
    saleValidator.validateSaleNumber(saleNumber);
    return saleRepository.findBySaleNumber(saleNumber);
  }

  @Override
  public Sale createSale(Sale sale) {
    if (sale.getSaleNumber() == null || sale.getSaleNumber().trim().isEmpty()) {
      sale.setSaleNumber(UUID.randomUUID().toString());
    }
    saleValidator.validate(sale);
    if (saleRepository.findBySaleNumber(sale.getSaleNumber()).isPresent()) {
      throw new SaleException("Sale with number '" + sale.getSaleNumber() + "' already exists");
    }
    List<SaleItem> processedItems = new ArrayList<>();
    try {
      for (SaleItem item : sale.getItems()) {
        inventoryService.reduceStock(item.getProductId(), item.getQuantity());
        processedItems.add(item);
      }
    } catch (RuntimeException e) {
      for (SaleItem processed : processedItems) {
        inventoryService.increaseStock(processed.getProductId(), processed.getQuantity());
      }
      throw new SaleException("Failed to process sale item for product ID. " + e.getMessage(), e);
    }
    if (sale.getDateTime() == null) {
      sale.setDateTime(LocalDateTime.now());
    }
    sale.setStatus(SaleStatus.COMPLETED);
    return saleRepository.create(sale);
  }

  @Override
  public void cancelSale(String id) {
    saleValidator.validateId(id);
    Sale sale = saleRepository.findById(id)
      .orElseThrow(() -> new SaleException("Cannot cancel non-existing sale with ID: " + id));
    if (SaleStatus.CANCELED == sale.getStatus()) {
      throw new SaleException("Sale with ID: " + id + " is already canceled");
    }
    List<SaleItem> restoredItems = new ArrayList<>();
    try {
      for (SaleItem item : sale.getItems()) {
        inventoryService.increaseStock(item.getProductId(), item.getQuantity());
        restoredItems.add(item);
      }
    } catch (RuntimeException e) {
      for (SaleItem restored : restoredItems) {
        inventoryService.reduceStock(restored.getProductId(), restored.getQuantity());
      }
      throw new SaleException("Failed to restore stock during cancellation. " + e.getMessage(), e);
    }
    sale.setStatus(SaleStatus.CANCELED);
    saleRepository.update(sale);
  }
}
