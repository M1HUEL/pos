package pos.ui.controller;

import java.util.List;
import java.util.Optional;
import pos.user.model.Role;
import pos.user.model.User;
import pos.user.service.UserService;

public class UserController {

  private final UserService userService;

  public UserController(UserService userService) {
    this.userService = userService;
  }

  public List<User> getAllUsers() {
    return userService.getAllUsers();
  }

  public Optional<User> authenticate(String username, String password) {
    return userService.authenticate(username, password);
  }

  public User createUser(String username, String fullName, Role role, boolean active, String password) {
    User user = new User();
    user.setUsername(username);
    user.setFullName(fullName);
    user.setRole(role);
    user.setActive(active);

    return userService.createUser(user, password);
  }

  public User updateUser(String id, String username, String fullName, Role role, boolean active) {
    User user = new User();
    user.setId(id);
    user.setUsername(username);
    user.setFullName(fullName);
    user.setRole(role);
    user.setActive(active);

    return userService.updateUser(user);
  }

  public void changePassword(String id, String newPassword) {
    userService.changePassword(id, newPassword);
  }

  public void deleteUser(String id) {
    userService.deleteUser(id);
  }
}
