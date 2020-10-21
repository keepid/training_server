package Security;

import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.apache.commons.lang3.RandomStringUtils;

import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.io.IOException;
import java.security.Key;
import java.security.SecureRandom;
import java.util.Date;
import java.util.Objects;

/*
   Methods for handling JSON Web Tokens (JWTs)
*/
public class SecurityUtils {
  private static final int ID_LENGTH = 25;
  private static final int ID_START_CHARACTERS = 48;
  private static final int ID_END_CHARACTERS = 122;
  private static final boolean INCLUDE_LETTERS = true;
  private static final boolean INCLUDE_NUMBERS = true;
  private static final char[] ID_CHARACTERS = null;

  public enum PassHashEnum {
    SUCCESS,
    FAILURE,
    ERROR;
  }

  public static String generateRandomStringId() {
    return RandomStringUtils.random(
        ID_LENGTH,
        ID_START_CHARACTERS,
        ID_END_CHARACTERS,
        INCLUDE_LETTERS,
        INCLUDE_NUMBERS,
        ID_CHARACTERS,
        new SecureRandom());
  }

  // Tests testPass against realPassHash, the hash of the real password.
  public static PassHashEnum verifyPassword(String testPass, String realPassHash) {
    Argon2 argon2 = Argon2Factory.create();
    char[] passwordArr = testPass.toCharArray();
    try {
      if (argon2.verify(realPassHash, passwordArr)) { // Hash matches password
        argon2.wipeArray(passwordArr);
        return PassHashEnum.SUCCESS;
      } else {
        argon2.wipeArray(passwordArr);
        return PassHashEnum.FAILURE;
      }
    } catch (Exception e) {
      e.printStackTrace();
      argon2.wipeArray(passwordArr);
      return PassHashEnum.ERROR;
    }
  }

  // Hashes a password using Argon2.
  // Returns hashed password, or null if Argon2 fails.
  public static String hashPassword(String plainText) {
    Argon2 argon2 = Argon2Factory.create();
    char[] passwordArr = plainText.toCharArray();
    String passwordHash;
    try {
      passwordHash = argon2.hash(10, 65536, 1, passwordArr);
      argon2.wipeArray(passwordArr);
      return passwordHash;
    } catch (Exception e) {
      argon2.wipeArray(passwordArr);
      return null;
    }
  }
}
