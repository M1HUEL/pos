package pos.ui.controller;

import java.util.ArrayList;
import java.util.List;
import pos.supplier.model.Supplier;
import pos.supplier.service.SupplierService;
import pos.ui.listener.SupplierChangeListener;

public class SupplierController {

  private final SupplierService supplierService;
  private final List<SupplierChangeListener> changeListeners = new ArrayList<>();

  public SupplierController(SupplierService supplierService) {
    this.supplierService = supplierService;
  }

  public void addChangeListener(SupplierChangeListener listener) {
    changeListeners.add(listener);
  }

  private void notifySuppliersChanged() {
    changeListeners.forEach(SupplierChangeListener::onSuppliersChanged);
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

    Supplier created = supplierService.createSupplier(supplier);

    notifySuppliersChanged();

    return created;
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

    Supplier updated = supplierService.updateSupplier(supplier);

    notifySuppliersChanged();

    return updated;
  }

  public void deleteSupplier(String id) {
    supplierService.deleteSupplier(id);

    notifySuppliersChanged();
  }
}
