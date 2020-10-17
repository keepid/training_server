package User.Services;

import Config.Message;
import Config.Service;
import Database.UserDao;
import Security.SecurityUtils;
import User.User;
import User.UserMessage;
import Validation.ValidationUtils;
import com.mongodb.client.MongoDatabase;
import org.slf4j.Logger;

import java.util.*;

import static com.mongodb.client.model.Filters.eq;

public class LoginService implements Service {
  private Logger logger;
  private MongoDatabase db;
  private String username;
  private String password;
  private User user;

  public LoginService(
      MongoDatabase db,
      Logger logger,
      String username,
      String password) {
    this.db = db;
    this.logger = logger;
    this.username = username;
    this.password = password;
  }

  public Message executeAndGetResponse() {
    if (!ValidationUtils.isValidUsername(this.username)
        || !ValidationUtils.isValidPassword(this.password)) {
      logger.info("Invalid username and/or password");
      return UserMessage.AUTH_FAILURE;
    }
    User user = UserDao.findOneUserOrNull(db, this.username);
    if (user == null) {
      return UserMessage.AUTH_FAILURE;
    }
    Objects.requireNonNull(user);
    this.user = user;
    if (!verifyPassword(this.password, user.getPassword())) {
      return UserMessage.AUTH_FAILURE;
    }
    logger.info("Login Successful!");
    return UserMessage.AUTH_SUCCESS;
  }

  public boolean verifyPassword(String inputPassword, String userHash) {
    SecurityUtils.PassHashEnum verifyPasswordStatus =
        SecurityUtils.verifyPassword(inputPassword, userHash);
    switch (verifyPasswordStatus) {
      case SUCCESS:
        return true;
      case ERROR:
        {
          logger.error("Failed to hash password");
          return false;
        }
      case FAILURE:
        {
          logger.info("Incorrect password");
          return false;
        }
    }
    return false;
  }

  public String getUsername() {
    Objects.requireNonNull(user);
    return user.getUsername();
  }

  public String getFirstName() {
    Objects.requireNonNull(user);
    return user.getFirstName();
  }

  public String getLastName() {
    Objects.requireNonNull(user);
    return user.getLastName();
  }

  public String getFullName() {
    Objects.requireNonNull(user);
    return user.getFirstName() + " " + user.getLastName();
  }
}
