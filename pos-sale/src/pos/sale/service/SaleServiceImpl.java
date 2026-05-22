package pos.sale.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
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

  public SaleServiceImpl(SaleRepository saleRepository, SaleValidator saleValidator, InventoryService inventoryService) {
    this.saleRepository = saleRepository;
    this.saleValidator = saleValidator;
    this.inventoryService = inventoryService;
  }

  @Override
  public List<Sale> getAllSales() {
    return saleRepository.findAll();
  }

  @Override
  public Optional<Sale> getSaleById(Long id) {
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
    saleValidator.validate(sale);

    if (sale.getSaleNumber() == null || sale.getSaleNumber().trim().isEmpty()) {
      throw new SaleException("Sale number is required for creation");
    }

    if (saleRepository.findBySaleNumber(sale.getSaleNumber()).isPresent()) {
      throw new SaleException("Sale with number '" + sale.getSaleNumber() + "' already exists");
    }

    for (SaleItem item : sale.getItems()) {
      try {
        inventoryService.reduceStock(item.getProductId(), item.getQuantity());
      } catch (RuntimeException e) {
        throw new SaleException("Failed to process sale item for product ID: " + item.getProductId() + ". " + e.getMessage(), e);
      }
    }

    if (sale.getDateTime() == null) {
      sale.setDateTime(LocalDateTime.now());
    }

    if (sale.getStatus() == null) {
      sale.setStatus(SaleStatus.COMPLETED);
    }

    return saleRepository.save(sale);
  }

  @Override
  public void cancelSale(Long id) {
    saleValidator.validateId(id);

    Sale sale = saleRepository.findById(id).orElseThrow(() -> new SaleException("Cannot cancel non-existing sale with ID: " + id));

    if (SaleStatus.CANCELED == sale.getStatus()) {
      throw new SaleException("Sale with ID: " + id + " is already canceled");
    }

    for (SaleItem item : sale.getItems()) {
      inventoryService.increaseStock(item.getProductId(), item.getQuantity());
    }

    sale.setStatus(SaleStatus.CANCELED);
    saleRepository.save(sale);
  }
}
