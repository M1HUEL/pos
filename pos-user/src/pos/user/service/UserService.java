package pos.user.service;

import java.util.List;
import java.util.Optional;
import pos.user.model.User;

public interface UserService {

  List<User> getAllUsers();

  Optional<User> getUserById(String id);

  Optional<User> authenticate(String username, String password);

  User createUser(User user, String rawPassword);

  User updateUser(User user);

  void changePassword(String id, String newPassword);

  void deleteUser(String id);

  boolean hasUsers();
}
