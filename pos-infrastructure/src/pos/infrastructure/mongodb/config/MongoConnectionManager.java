package pos.infrastructure.mongodb.config;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

public class MongoConnectionManager {

  private static MongoClient c;
  private static MongoDatabase db;

  public static synchronized MongoDatabase getDatabase(String connectionString, String dbName) {
    if (db == null) {
      c = MongoClients.create(connectionString);

      db = c.getDatabase(dbName);
    }

    return db;
  }

  public static synchronized void close() {
    if (c != null) {
      c.close();
      c = null;

      db = null;
    }
  }
}
