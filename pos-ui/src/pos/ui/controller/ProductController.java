package pos.ui.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import pos.product.model.Product;
import pos.product.service.ProductService;
import pos.supplier.model.Supplier;
import pos.supplier.service.SupplierService;
import pos.ui.listener.ProductChangeListener;

public class ProductController {

  private final ProductService productService;
  private final SupplierService supplierService;
  private final List<ProductChangeListener> changeListeners = new ArrayList<>();

  public ProductController(ProductService productService, SupplierService supplierService) {
    this.productService = productService;
    this.supplierService = supplierService;
  }

  public void addChangeListener(ProductChangeListener listener) {
    changeListeners.add(listener);
  }

  private void notifyProductsChanged() {
    changeListeners.forEach(ProductChangeListener::onProductsChanged);
  }

  public List<Product> getAllProducts() {
    return productService.getAllProducts();
  }

  public List<Supplier> getAllSuppliers() {
    return supplierService.getAllSuppliers();
  }

  public Product createProduct(String sku, String name, String description, BigDecimal price, String supplierId, boolean active) {
    Product product = new Product();
    product.setSku(sku);
    product.setName(name);
    product.setDescription(description);
    product.setPrice(price);
    product.setSupplierId(supplierId);
    product.setActive(active);

    Product created = productService.createProduct(product);

    notifyProductsChanged();

    return created;
  }

  public Product updateProduct(String id, String sku, String name, String description, BigDecimal price, String supplierId, boolean active) {
    Product product = new Product();
    product.setId(id);
    product.setSku(sku);
    product.setName(name);
    product.setDescription(description);
    product.setPrice(price);
    product.setSupplierId(supplierId);
    product.setActive(active);

    Product updated = productService.updateProduct(product);

    notifyProductsChanged();

    return updated;
  }

  public void deleteProduct(String id) {
    productService.deleteProduct(id);

    notifyProductsChanged();
  }
}
