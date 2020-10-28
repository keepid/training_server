package User;

import Config.DeploymentLevel;
import Config.Message;
import Database.UserDao;
import Database.UserDaoTestImpl;
import Logger.LogFactory;
import TestUtils.EntityFactory;
import User.Services.LoginService;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;

import static org.junit.Assert.assertEquals;

public class LoginServiceUnitTests {
  public UserDao userDao;
  public Logger logger;

  @Before
  public void initialize() {
    this.userDao = new UserDaoTestImpl(DeploymentLevel.IN_MEMORY);
    this.logger = new LogFactory().createLogger();
  }

  @Test
  public void userNotFound() {
    EntityFactory.createUser()
        .withUsername("username1")
        .withPasswordToHash("password123")
        .buildAndPersist(userDao);
    LoginService loginService = new LoginService(userDao, logger, "username2", "password2");
    Message message = loginService.executeAndGetResponse();
    assertEquals(message, UserMessage.USER_NOT_FOUND);
  }

  @Test
  public void success_auth() {
    EntityFactory.createUser()
        .withUsername("username1")
        .withPasswordToHash("password123")
        .buildAndPersist(userDao);
    LoginService loginService = new LoginService(userDao, logger, "username1", "password123");
    Message message = loginService.executeAndGetResponse();
    assertEquals(message, UserMessage.AUTH_SUCCESS);
  }

  @Test
  public void invalidPassword() {
    LoginService loginService = new LoginService(userDao, logger, "username1", "");
    assertEquals(loginService.executeAndGetResponse(), UserMessage.AUTH_FAILURE);
  }
}
