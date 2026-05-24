package pos.user.service;

import java.util.List;
import java.util.Optional;
import pos.auth.util.PasswordUtil;
import pos.user.exception.UserException;
import pos.user.model.User;
import pos.user.repository.UserRepository;
import pos.user.validation.UserValidator;

public class UserServiceImpl implements UserService {

  private final UserRepository userRepository;
  private final UserValidator userValidator;

  public UserServiceImpl(UserRepository userRepository, UserValidator userValidator) {
    this.userRepository = userRepository;
    this.userValidator = userValidator;
  }

  @Override
  public List<User> getAllUsers() {
    return userRepository.findAll();
  }

  @Override
  public Optional<User> getUserById(String id) {
    userValidator.validateId(id);

    return userRepository.findById(id);
  }

  @Override
  public Optional<User> authenticate(String username, String password) {
    if (username == null || password == null) {
      return Optional.empty();
    }

    Optional<User> userOpt = userRepository.findByUsername(username);

    if (!userOpt.isPresent()) {
      return Optional.empty();
    }

    User user = userOpt.get();

    if (!Boolean.TRUE.equals(user.getActive())) {
      return Optional.empty();
    }

    if (!PasswordUtil.verify(password, user.getPasswordHash())) {
      return Optional.empty();
    }

    return Optional.of(user);
  }

  @Override
  public User createUser(User user, String rawPassword) {
    userValidator.validate(user);

    userValidator.validatePassword(rawPassword);

    if (userRepository.findByUsername(user.getUsername()).isPresent()) {
      throw new UserException("Username '" + user.getUsername() + "' already exists");
    }

    if (user.getActive() == null) {
      user.setActive(true);
    }

    user.setPasswordHash(PasswordUtil.hash(rawPassword));

    return userRepository.create(user);
  }

  @Override
  public User updateUser(User user) {
    if (user == null) {
      throw new UserException("User data cannot be null");
    }

    userValidator.validateId(user.getId());
    userValidator.validate(user);

    User existing = userRepository.findById(user.getId()).orElseThrow(() -> new UserException("User not found with ID: " + user.getId()));

    Optional<User> sameUsername = userRepository.findByUsername(user.getUsername());

    if (sameUsername.isPresent() && !sameUsername.get().getId().equals(user.getId())) {
      throw new UserException("Username '" + user.getUsername() + "' already exists");
    }

    user.setPasswordHash(existing.getPasswordHash());

    return userRepository.update(user);
  }

  @Override
  public void changePassword(String id, String newPassword) {
    userValidator.validateId(id);

    userValidator.validatePassword(newPassword);

    User user = userRepository.findById(id).orElseThrow(() -> new UserException("User not found with ID: " + id));
    user.setPasswordHash(PasswordUtil.hash(newPassword));

    userRepository.update(user);
  }

  @Override
  public void deleteUser(String id) {
    userValidator.validateId(id);

    if (!userRepository.findById(id).isPresent()) {
      throw new UserException("Cannot delete non-existing user with ID: " + id);
    }

    userRepository.deleteById(id);
  }

  @Override
  public boolean hasUsers() {
    return !userRepository.findAll().isEmpty();
  }
}
