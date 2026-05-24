package pos.user.validation;

import org.bson.types.ObjectId;
import pos.user.exception.UserException;
import pos.user.model.User;

public class UserValidator {

  public void validateId(String id) {
    if (id == null || id.trim().isEmpty()) {
      throw new UserException("User ID cannot be null or empty");
    }
    if (!ObjectId.isValid(id)) {
      throw new UserException("User ID has an invalid format");
    }
  }

  public void validate(User user) {
    if (user == null) {
      throw new UserException("User data cannot be null");
    }
    if (user.getUsername() == null || user.getUsername().trim().isEmpty()) {
      throw new UserException("Username cannot be null or empty");
    }
    if (user.getFullName() == null || user.getFullName().trim().isEmpty()) {
      throw new UserException("Full name cannot be null or empty");
    }
    if (user.getRole() == null) {
      throw new UserException("Role cannot be null");
    }
  }

  public void validatePassword(String password) {
    if (password == null || password.trim().isEmpty()) {
      throw new UserException("Password cannot be null or empty");
    }
    if (password.length() < 6) {
      throw new UserException("Password must be at least 6 characters");
    }
  }
}
