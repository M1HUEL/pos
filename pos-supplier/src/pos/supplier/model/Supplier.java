package pos.supplier.model;

public class Supplier {

  private String id;
  private String name;
  private String contactName;
  private String phone;
  private String email;
  private String address;
  private Boolean active;

  public Supplier() {
    // ...
  }

  public Supplier(String id, String name, String contactName, String phone, String email, String address, Boolean active) {
    this.id = id;
    this.name = name;
    this.contactName = contactName;
    this.phone = phone;
    this.email = email;
    this.address = address;
    this.active = active;
  }

  public String getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public String getContactName() {
    return contactName;
  }

  public String getPhone() {
    return phone;
  }

  public String getEmail() {
    return email;
  }

  public String getAddress() {
    return address;
  }

  public Boolean getActive() {
    return active;
  }

  public void setId(String id) {
    this.id = id;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setContactName(String contactName) {
    this.contactName = contactName;
  }

  public void setPhone(String phone) {
    this.phone = phone;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public void setAddress(String address) {
    this.address = address;
  }

  public void setActive(Boolean active) {
    this.active = active;
  }

}
