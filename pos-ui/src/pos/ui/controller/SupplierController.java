package pos.ui.controller;

import java.util.List;
import pos.supplier.model.Supplier;
import pos.supplier.service.SupplierService;

public class SupplierController {

  private final SupplierService supplierService;

  public SupplierController(SupplierService supplierService) {
    this.supplierService = supplierService;
  }

  public List<Supplier> getAllSuppliers() {
    return supplierService.getAllSuppliers();
  }

  public Supplier createSupplier(String name, String contactName, String phone, String email, String address, boolean active) {
    Supplier supplier = new Supplier();
    supplier.setName(name);
    supplier.setContactName(contactName);
    supplier.setPhone(phone);
    supplier.setEmail(email);
    supplier.setAddress(address);
    supplier.setActive(active);

    return supplierService.createSupplier(supplier);
  }

  public Supplier updateSupplier(String id, String name, String contactName, String phone, String email, String address, boolean active) {
    Supplier supplier = new Supplier();
    supplier.setId(id);
    supplier.setName(name);
    supplier.setContactName(contactName);
    supplier.setPhone(phone);
    supplier.setEmail(email);
    supplier.setAddress(address);
    supplier.setActive(active);

    return supplierService.updateSupplier(supplier);
  }

  public void deleteSupplier(String id) {
    supplierService.deleteSupplier(id);
  }
}
