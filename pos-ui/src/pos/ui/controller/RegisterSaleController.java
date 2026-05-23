package pos.ui.controller;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import pos.product.model.Product;
import pos.product.service.ProductService;
import pos.sale.model.Sale;
import pos.sale.model.SaleItem;
import pos.sale.service.SaleService;

public class RegisterSaleController {

  private final SaleService saleService;
  private final ProductService productService;
  private final List<SaleItem> currentItems = new ArrayList<>();

  public RegisterSaleController(SaleService saleService, ProductService productService) {
    this.saleService = saleService;
    this.productService = productService;
  }

  public List<Product> getAllProducts() {
    return productService.getAllProducts();
  }

  public Product lookupProductBySku(String sku) {
    return productService.getProductBySku(sku)
      .orElseThrow(() -> new IllegalArgumentException("Product not found with SKU: " + sku));
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
    BigDecimal subTotal = product.getPrice()
      .multiply(BigDecimal.valueOf(quantity))
      .subtract(discountAmount)
      .setScale(2, RoundingMode.HALF_UP);
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
        BigDecimal mergedSubTotal = existing.getUnitPrice()
          .multiply(BigDecimal.valueOf(mergedQty))
          .subtract(mergedDiscount)
          .setScale(2, RoundingMode.HALF_UP);
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
    return currentItems.stream()
      .map(SaleItem::getSubTotal)
      .reduce(BigDecimal.ZERO, BigDecimal::add)
      .setScale(2, RoundingMode.HALF_UP);
  }

  public BigDecimal calculateTotal(BigDecimal saleDiscount, BigDecimal taxAmount) {
    if (saleDiscount == null) {
      saleDiscount = BigDecimal.ZERO;
    }
    if (taxAmount == null) {
      taxAmount = BigDecimal.ZERO;
    }
    return calculateItemsTotal()
      .subtract(saleDiscount)
      .add(taxAmount)
      .setScale(2, RoundingMode.HALF_UP);
  }

  public Sale createSale(String saleNumber, String paymentMethod,
    BigDecimal saleDiscount, BigDecimal taxAmount) {
    if (currentItems.isEmpty()) {
      throw new IllegalStateException("Cannot create a sale with no items");
    }
    if (saleDiscount == null) {
      saleDiscount = BigDecimal.ZERO;
    }
    if (taxAmount == null) {
      taxAmount = BigDecimal.ZERO;
    }
    Sale sale = new Sale();
    sale.setSaleNumber(saleNumber);
    sale.setPaymentMethod(paymentMethod);
    sale.setItems(new ArrayList<>(currentItems));
    sale.setDiscountAmount(saleDiscount);
    sale.setTaxAmount(taxAmount);
    sale.setTotalAmount(calculateTotal(saleDiscount, taxAmount));
    sale.setDateTime(LocalDateTime.now());
    Sale created = saleService.createSale(sale);
    currentItems.clear();
    return created;
  }

  public void clearItems() {
    currentItems.clear();
  }

  public String generateSaleNumber() {
    return "SALE-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss"));
  }
}
