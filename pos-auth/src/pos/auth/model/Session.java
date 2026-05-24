package pos.auth.model;

import pos.user.model.Role;
import pos.user.model.User;

public class Session {

  private static final Session INSTANCE = new Session();

  private User currentUser;

  private Session() {
    // ...
  }

  public static Session getInstance() {
    return INSTANCE;
  }

  public void login(User user) {
    this.currentUser = user;
  }

  public void logout() {
    this.currentUser = null;
  }

  public User getCurrentUser() {
    return currentUser;
  }

  public boolean isAdmin() {
    return currentUser != null && Role.ADMIN == currentUser.getRole();
  }

  public boolean isManagerOrAbove() {
    return currentUser != null && (Role.ADMIN == currentUser.getRole() || Role.MANAGER == currentUser.getRole());
  }
}
