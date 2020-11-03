package User;

import static org.junit.Assert.assertEquals;

import Config.DeploymentLevel;
import Database.UserDao;
import Database.UserDaoFactory;
import Logger.LogFactory;
import TestUtils.EntityFactory;
import User.Services.GetUserInfoService;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;

public class GetUserInfoServiceUnitTests {
  public UserDao userDao;
  public Logger logger;

  @Before
  public void initialize() {
    this.userDao = UserDaoFactory.create(DeploymentLevel.IN_MEMORY);
    this.logger = new LogFactory().createLogger();
  }

  @After
  public void reset() {
    userDao.clear();
  }

  @Test
  public void userNotFound() {
    GetUserInfoService getUserInfoService = new GetUserInfoService(userDao, logger, "username2");
    assertEquals(getUserInfoService.executeAndGetResponse(), UserMessage.USER_NOT_FOUND);
  }

  @Test
  public void empty_username() {
    GetUserInfoService getUserInfoService = new GetUserInfoService(userDao, logger, "");
    assertEquals(getUserInfoService.executeAndGetResponse(), UserMessage.USER_NOT_FOUND);
  }

  @Test
  public void successful_getUserFields() {
    String username = "username1";
    String password = "password123";
    String email = "someemail@keep.id";
    String phoneNumber = "1231231234";
    EntityFactory.createUser()
        .withUsername(username)
        .withPasswordToHash(password)
        .withEmail(email)
        .withPhoneNumber(phoneNumber)
        .buildAndPersist(userDao);
    GetUserInfoService getUserInfoService = new GetUserInfoService(userDao, logger, username);
    assertEquals(getUserInfoService.executeAndGetResponse(), UserMessage.SUCCESS);
    JSONObject userFields = getUserInfoService.getUserFields();
    assertEquals(username, userFields.get("username"));
    assertEquals(email, userFields.get("email"));
    assertEquals(phoneNumber, userFields.get("phone"));
  }
}
