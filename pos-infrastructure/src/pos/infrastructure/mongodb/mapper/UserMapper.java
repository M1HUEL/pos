package pos.infrastructure.mongodb.mapper;

import org.bson.Document;
import org.bson.types.ObjectId;
import pos.user.model.Role;
import pos.user.model.User;

public class UserMapper {

  public static User toEntity(Document doc) {
    if (doc == null) {
      return null;
    }

    User user = new User();

    if (doc.getObjectId("_id") != null) {
      user.setId(doc.getObjectId("_id").toHexString());
    }

    user.setUsername(doc.getString("username"));
    user.setPasswordHash(doc.getString("passwordHash"));
    user.setFullName(doc.getString("fullName"));

    if (doc.getString("role") != null) {
      user.setRole(Role.valueOf(doc.getString("role")));
    }

    user.setActive(doc.getBoolean("active"));

    return user;
  }

  public static Document toDocument(User user) {
    if (user == null) {
      return null;
    }

    Document doc = new Document();

    if (user.getId() != null && ObjectId.isValid(user.getId())) {
      doc.append("_id", new ObjectId(user.getId()));
    }

    doc.append("username", user.getUsername());
    doc.append("passwordHash", user.getPasswordHash());
    doc.append("fullName", user.getFullName());

    if (user.getRole() != null) {
      doc.append("role", user.getRole().name());
    }

    doc.append("active", user.getActive());

    return doc;
  }
}
