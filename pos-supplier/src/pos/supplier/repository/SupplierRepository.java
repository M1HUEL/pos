package pos.supplier.repository;

import java.util.List;
import java.util.Optional;
import pos.supplier.model.Supplier;

public interface SupplierRepository {

  List<Supplier> findAll();

  Optional<Supplier> findById(String id);

  Optional<Supplier> findByName(String name);

  Supplier create(Supplier supplier);

  Supplier update(Supplier supplier);

  void deleteById(String id);
}
