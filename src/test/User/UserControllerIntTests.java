package User;

import Config.DeploymentLevel;
import Database.UserDao;
import Database.UserDaoImpl;
import TestUtils.EntityFactory;
import TestUtils.TestUtils;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class UserControllerIntTests {
  UserDao userDao;

  @Before
  public void configureDatabase() {
    TestUtils.startServer();
    userDao = new UserDaoImpl(DeploymentLevel.TEST);
  }

  @After
  public void clearDatabase() {
    userDao.clear();
  }

  @Test
  public void login_success() {
    String username = "username1";
    String password = "password1234";
    EntityFactory.createUser()
        .withUsername(username)
        .withPasswordToHash(password)
        .buildAndPersist(userDao);

    JSONObject body = new JSONObject();
    body.put("username", username);
    body.put("password", password);
    HttpResponse<String> loginResponse =
        Unirest.post(TestUtils.getServerUrl() + "/login").body(body.toString()).asString();
    JSONObject loginResponseJSON = TestUtils.responseStringToJSON(loginResponse.getBody());
    assertThat(loginResponseJSON.getString("status")).isEqualTo("AUTH_SUCCESS");
  }

  @Test
  public void login_failure() {
    String username = "username1";
    String password = "password1234";
    String incorrectPassword = "badPassword123";
    EntityFactory.createUser()
        .withUsername(username)
        .withPasswordToHash(password)
        .buildAndPersist(userDao);

    JSONObject body = new JSONObject();
    body.put("username", username);
    body.put("password", incorrectPassword);
    HttpResponse<String> loginResponse =
        Unirest.post(TestUtils.getServerUrl() + "/login").body(body.toString()).asString();
    JSONObject loginResponseJSON = TestUtils.responseStringToJSON(loginResponse.getBody());
    assertThat(loginResponseJSON.getString("status")).isEqualTo("AUTH_FAILURE");
  }
}
