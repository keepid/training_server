package User.Services;

import Config.Message;
import Config.Service;
import Database.UserDao;
import Security.SecurityUtils;
import User.User;
import User.UserMessage;
import Validation.ValidationUtils;
import org.slf4j.Logger;

import java.util.Objects;
import java.util.Optional;

public class LoginService implements Service {
  private Logger logger;
  private UserDao userDao;
  private String username;
  private String password;
  private User user;

  public LoginService(UserDao userDao, Logger logger, String username, String password) {
    this.userDao = userDao;
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
    Optional<User> optionalUser = userDao.get(this.username);
    if (optionalUser.isEmpty()) {
      return UserMessage.AUTH_FAILURE;
    }
    this.user = optionalUser.get();
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
