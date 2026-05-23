package pos.supplier.service;

import java.util.List;
import java.util.Optional;
import pos.supplier.exception.SupplierException;
import pos.supplier.model.Supplier;
import pos.supplier.repository.SupplierRepository;
import pos.supplier.validation.SupplierValidator;

public class SupplierServiceImpl implements SupplierService {

  private final SupplierRepository supplierRepository;
  private final SupplierValidator supplierValidator;

  public SupplierServiceImpl(SupplierRepository supplierRepository, SupplierValidator supplierValidator) {
    this.supplierRepository = supplierRepository;
    this.supplierValidator = supplierValidator;
  }

  @Override
  public List<Supplier> getAllSuppliers() {
    return supplierRepository.findAll();
  }

  @Override
  public Optional<Supplier> getSupplierById(String id) {
    supplierValidator.validateId(id);

    return supplierRepository.findById(id);
  }

  @Override
  public Supplier createSupplier(Supplier supplier) {
    supplierValidator.validate(supplier);

    if (supplierRepository.findByName(supplier.getName()).isPresent()) {
      throw new SupplierException("Supplier with name '" + supplier.getName() + "' already exists");
    }

    if (supplier.getActive() == null) {
      supplier.setActive(true);
    }

    return supplierRepository.create(supplier);
  }

  @Override
  public Supplier updateSupplier(Supplier supplier) {
    if (supplier == null) {
      throw new SupplierException("Supplier data cannot be null");
    }

    supplierValidator.validateId(supplier.getId());
    supplierValidator.validate(supplier);

    if (!supplierRepository.findById(supplier.getId()).isPresent()) {
      throw new SupplierException("Supplier not found with ID: " + supplier.getId());
    }

    Optional<Supplier> supplierWithSameName = supplierRepository.findByName(supplier.getName());

    if (supplierWithSameName.isPresent() && !supplierWithSameName.get().getId().equals(supplier.getId())) {
      throw new SupplierException("Another supplier with name '" + supplier.getName() + "' already exists");
    }

    return supplierRepository.update(supplier);
  }

  @Override
  public void deleteSupplier(String id) {
    supplierValidator.validateId(id);

    if (!supplierRepository.findById(id).isPresent()) {
      throw new SupplierException("Cannot delete non-existing supplier with ID: " + id);
    }

    supplierRepository.deleteById(id);
  }
}
