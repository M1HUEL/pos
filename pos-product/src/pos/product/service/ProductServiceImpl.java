package pos.product.service;

import java.util.List;
import java.util.Optional;
import pos.product.exception.ProductException;
import pos.product.model.Product;
import pos.product.repository.ProductRepository;
import pos.product.validation.ProductValidator;

public class ProductServiceImpl implements ProductService {

  private final ProductRepository productRepository;
  private final ProductValidator productValidator;

  public ProductServiceImpl(ProductRepository productRepository, ProductValidator productValidator) {
    this.productRepository = productRepository;
    this.productValidator = productValidator;
  }

  @Override
  public List<Product> getAllProducts() {
    return productRepository.findAll();
  }

  @Override
  public Optional<Product> getProductById(Long id) {
    productValidator.validateId(id);
    return productRepository.findById(id);
  }

  @Override
  public Optional<Product> getProductBySku(String sku) {
    productValidator.validateSku(sku);
    return productRepository.findBySku(sku);
  }

  @Override
  public Product createProduct(Product product) {
    productValidator.validate(product);
    productValidator.validateSku(product.getSku());

    if (productRepository.findBySku(product.getSku()).isPresent()) {
      throw new ProductException("Product with SKU '" + product.getSku() + "' already exists");
    }

    if (product.getActive() == null) {
      product.setActive(true);
    }

    return productRepository.save(product);
  }

  @Override
  public Product updateProduct(Product product) {
    if (product == null) {
      throw new ProductException("Product data cannot be null");
    }

    productValidator.validateId(product.getId());
    productValidator.validate(product);
    productValidator.validateSku(product.getSku());

    if (!productRepository.findById(product.getId()).isPresent()) {
      throw new ProductException("Product not found with ID: " + product.getId());
    }

    Optional<Product> productWithSameSku = productRepository.findBySku(product.getSku());

    if (productWithSameSku.isPresent() && !productWithSameSku.get().getId().equals(product.getId())) {
      throw new ProductException("Another product with SKU '" + product.getSku() + "' already exists");
    }

    return productRepository.save(product);
  }

  @Override
  public void deleteProduct(Long id) {
    productValidator.validateId(id);

    if (!productRepository.findById(id).isPresent()) {
      throw new ProductException("Cannot delete non-existing product with ID: " + id);
    }

    productRepository.deleteById(id);
  }
}
