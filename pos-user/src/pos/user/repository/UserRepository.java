package pos.user.repository;

import java.util.List;
import java.util.Optional;
import pos.user.model.User;

public interface UserRepository {

  List<User> findAll();

  Optional<User> findById(String id);

  Optional<User> findByUsername(String username);

  User create(User user);

  User update(User user);

  void deleteById(String id);
}
