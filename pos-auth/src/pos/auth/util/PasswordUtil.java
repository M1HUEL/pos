package pos.auth.util;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import java.util.Base64;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class PasswordUtil {

  private static final int ITERATIONS = 65536;

  private static final int KEY_LENGTH = 256;

  private static final String ALGORITHM = "PBKDF2WithHmacSHA256";

  public static String hash(String password) {
    try {
      byte[] salt = new byte[16];

      new SecureRandom().nextBytes(salt);

      PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, ITERATIONS, KEY_LENGTH);

      byte[] hash = SecretKeyFactory.getInstance(ALGORITHM).generateSecret(spec).getEncoded();

      return Base64.getEncoder().encodeToString(salt) + ":" + Base64.getEncoder().encodeToString(hash);
    } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
      throw new RuntimeException("Error hashing password", e);
    }
  }

  public static boolean verify(String password, String stored) {
    try {
      String[] parts = stored.split(":");

      byte[] salt = Base64.getDecoder().decode(parts[0]);
      byte[] expectedHash = Base64.getDecoder().decode(parts[1]);

      PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, ITERATIONS, KEY_LENGTH);

      byte[] actualHash = SecretKeyFactory.getInstance(ALGORITHM).generateSecret(spec).getEncoded();

      return Arrays.equals(expectedHash, actualHash);
    } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
      throw new RuntimeException("Error verifying password", e);
    }
  }
}
