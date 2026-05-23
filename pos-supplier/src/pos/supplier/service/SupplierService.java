package pos.supplier.service;

import java.util.List;
import java.util.Optional;
import pos.supplier.model.Supplier;

interface SupplierService {

  List<Supplier> getAllSuppliers();

  Optional<Supplier> getSupplierById(String id);

  Supplier createSupplier(Supplier supplier);

  Supplier updateSupplier(Supplier supplier);

  void deleteSupplier(String id);
}
