package pos.ui.controller;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import pos.product.model.Product;
import pos.product.service.ProductService;
import pos.sale.config.service.SaleConfigService;
import pos.sale.model.PaymentMethod;
import pos.sale.model.Sale;
import pos.sale.model.SaleItem;
import pos.sale.service.SaleService;

public class NewSaleController {

  private final SaleService saleService;
  private final ProductService productService;
  private final SaleConfigService saleConfigService;
  private final List<SaleItem> currentItems = new ArrayList<>();

  public NewSaleController(SaleService saleService, ProductService productService, SaleConfigService saleConfigService) {
    this.saleService = saleService;
    this.productService = productService;
    this.saleConfigService = saleConfigService;
  }

  public List<Product> getAllProducts() {
    return productService.getAllProducts().stream().filter(p -> Boolean.TRUE.equals(p.getActive())).collect(Collectors.toList());
  }

  public BigDecimal getTaxRatePercent() {
    return saleConfigService.getConfig().getTaxRate().multiply(BigDecimal.valueOf(100)).stripTrailingZeros();
  }

  public BigDecimal getDefaultDiscount() {
    return saleConfigService.getConfig().getDefaultDiscount().setScale(2, RoundingMode.HALF_UP);
  }

  public SaleItem buildItem(Product product, int quantity, BigDecimal discountAmount) {
    if (quantity <= 0) {
      throw new IllegalArgumentException("Quantity must be greater than zero");
    }

    if (discountAmount == null) {
      discountAmount = BigDecimal.ZERO;
    }

    if (discountAmount.compareTo(BigDecimal.ZERO) < 0) {
      throw new IllegalArgumentException("Item discount cannot be negative");
    }

    BigDecimal subTotal = product.getPrice().multiply(BigDecimal.valueOf(quantity)).subtract(discountAmount).setScale(2, RoundingMode.HALF_UP);

    if (subTotal.compareTo(BigDecimal.ZERO) < 0) {
      throw new IllegalArgumentException("Item discount cannot exceed item total");
    }

    SaleItem item = new SaleItem();
    item.setProductId(product.getId());
    item.setProductName(product.getName());
    item.setQuantity(quantity);
    item.setUnitPrice(product.getPrice());
    item.setDiscountAmount(discountAmount);
    item.setSubTotal(subTotal);

    return item;
  }

  public void addItem(SaleItem newItem) {
    for (SaleItem existing : currentItems) {
      if (existing.getProductId().equals(newItem.getProductId())) {
        int mergedQty = existing.getQuantity() + newItem.getQuantity();

        BigDecimal mergedDiscount = existing.getDiscountAmount().add(newItem.getDiscountAmount());
        BigDecimal mergedSubTotal = existing.getUnitPrice().multiply(BigDecimal.valueOf(mergedQty)).subtract(mergedDiscount).setScale(2, RoundingMode.HALF_UP);

        existing.setQuantity(mergedQty);
        existing.setDiscountAmount(mergedDiscount);
        existing.setSubTotal(mergedSubTotal);

        return;
      }
    }
    currentItems.add(newItem);
  }

  public void removeItem(int index) {
    currentItems.remove(index);
  }

  public List<SaleItem> getCurrentItems() {
    return new ArrayList<>(currentItems);
  }

  public BigDecimal calculateItemsTotal() {
    return currentItems.stream().map(SaleItem::getSubTotal).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);
  }

  public BigDecimal calculateTaxAmount() {
    return calculateItemsTotal().multiply(saleConfigService.getConfig().getTaxRate()).setScale(2, RoundingMode.HALF_UP);
  }

  public BigDecimal calculateTotal(BigDecimal saleDiscount) {
    if (saleDiscount == null) {
      saleDiscount = BigDecimal.ZERO;
    }

    return calculateItemsTotal().subtract(saleDiscount).add(calculateTaxAmount()).setScale(2, RoundingMode.HALF_UP);
  }

  public Sale createSale(PaymentMethod paymentMethod, BigDecimal saleDiscount) {
    if (currentItems.isEmpty()) {
      throw new IllegalStateException("Cannot create a sale with no items");
    }

    if (saleDiscount == null) {
      saleDiscount = BigDecimal.ZERO;
    }

    BigDecimal taxAmount = calculateTaxAmount();
    BigDecimal totalAmount = calculateTotal(saleDiscount);

    Sale sale = new Sale();
    sale.setPaymentMethod(paymentMethod);
    sale.setItems(new ArrayList<>(currentItems));
    sale.setDiscountAmount(saleDiscount);
    sale.setTaxAmount(taxAmount);
    sale.setTotalAmount(totalAmount);
    sale.setDateTime(LocalDateTime.now());

    Sale created = saleService.createSale(sale);

    currentItems.clear();

    return created;
  }

  public void clearItems() {
    currentItems.clear();
  }

  public String generateSaleNumber() {
    return UUID.randomUUID().toString();
  }
}
