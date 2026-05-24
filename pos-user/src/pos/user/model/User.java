package pos.user.model;

public class User {

  private String id;
  private String username;
  private String passwordHash;
  private String fullName;
  private Role role;
  private Boolean active;

  public User() {
    // ...
  }

  public User(String id, String username, String passwordHash, String fullName, Role role, Boolean active) {
    this.id = id;
    this.username = username;
    this.passwordHash = passwordHash;
    this.fullName = fullName;
    this.role = role;
    this.active = active;
  }

  public String getId() {
    return id;
  }

  public String getUsername() {
    return username;
  }

  public String getPasswordHash() {
    return passwordHash;
  }

  public String getFullName() {
    return fullName;
  }

  public Role getRole() {
    return role;
  }

  public Boolean getActive() {
    return active;
  }

  public void setId(String id) {
    this.id = id;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public void setPasswordHash(String passwordHash) {
    this.passwordHash = passwordHash;
  }

  public void setFullName(String fullName) {
    this.fullName = fullName;
  }

  public void setRole(Role role) {
    this.role = role;
  }

  public void setActive(Boolean active) {
    this.active = active;
  }

}
