package pos.app;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class AppConfig {

  private static final String CONFIG_FILE = "app.properties";
  private final Properties properties = new Properties();

  public AppConfig() {
    try (InputStream input = findConfigFile()) {
      properties.load(input);
    } catch (IOException e) {
      throw new IllegalStateException("Failed to load configuration file: " + CONFIG_FILE, e);
    }
  }

  private InputStream findConfigFile() {
    InputStream input = getClass().getClassLoader().getResourceAsStream(CONFIG_FILE);
    if (input != null) {
      return input;
    }
    try {
      return new FileInputStream(CONFIG_FILE);
    } catch (IOException e) {
      throw new IllegalStateException("Configuration file not found: " + CONFIG_FILE);
    }
  }

  public String getMongoUri() {
    return getRequired("mongo.uri");
  }

  public String getMongoDatabase() {
    return getRequired("mongo.database");
  }

  private String getRequired(String key) {
    String value = properties.getProperty(key);
    if (value == null || value.trim().isEmpty()) {
      throw new IllegalStateException("Missing required configuration property: " + key);
    }
    return value.trim();
  }
}
