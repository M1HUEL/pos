package pos.infrastructure.mongodb.repository;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.bson.Document;
import org.bson.types.ObjectId;
import pos.infrastructure.mongodb.mapper.UserMapper;
import pos.user.model.User;
import pos.user.repository.UserRepository;

public class UserMongoRepository implements UserRepository {

  private final MongoCollection<Document> collection;

  public UserMongoRepository(MongoDatabase database) {
    this.collection = database.getCollection("user");
  }

  @Override
  public List<User> findAll() {
    List<User> users = new ArrayList<>();

    for (Document doc : collection.find()) {
      users.add(UserMapper.toEntity(doc));
    }

    return users;
  }

  @Override
  public Optional<User> findById(String id) {
    if (id == null || !ObjectId.isValid(id)) {
      return Optional.empty();
    }

    Document doc = collection.find(Filters.eq("_id", new ObjectId(id))).first();

    return Optional.ofNullable(doc).map(UserMapper::toEntity);
  }

  @Override
  public Optional<User> findByUsername(String username) {
    if (username == null) {
      return Optional.empty();
    }

    Document doc = collection.find(Filters.eq("username", username)).first();

    return Optional.ofNullable(doc).map(UserMapper::toEntity);
  }

  @Override
  public User create(User user) {
    if (user.getId() == null || user.getId().trim().isEmpty()) {
      user.setId(new ObjectId().toHexString());
    }

    collection.insertOne(UserMapper.toDocument(user));

    return user;
  }

  @Override
  public User update(User user) {
    if (user.getId() == null || !ObjectId.isValid(user.getId())) {
      throw new IllegalArgumentException("Cannot update a User without a valid hex ObjectId");
    }

    collection.replaceOne(Filters.eq("_id", new ObjectId(user.getId())), UserMapper.toDocument(user));

    return user;
  }

  @Override
  public void deleteById(String id) {
    if (id == null || !ObjectId.isValid(id)) {
      throw new IllegalArgumentException("Cannot delete: Invalid hex ObjectId format");
    }

    collection.deleteOne(Filters.eq("_id", new ObjectId(id)));
  }
}
