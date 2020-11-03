package TestUtils;

import static org.assertj.core.api.Assertions.assertThat;

import Config.AppConfig;
import Config.DeploymentLevel;
import Config.MongoConfig;
import io.javalin.Javalin;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import org.json.JSONObject;

public class TestUtils {
  private static final int SERVER_TEST_PORT = Integer.parseInt(System.getenv("TEST_PORT"));
  private static final String SERVER_TEST_URL = "http://localhost:" + SERVER_TEST_PORT;
  private static Javalin app;

  public static void startServer() {
    if (app == null) {
      try {
        MongoConfig.getMongoClient();
      } catch (Exception e) {
        System.err.println(e.getStackTrace());
        System.exit(0);
      }
      // never deploy with higher than TEST
      app = AppConfig.appFactory(DeploymentLevel.TEST);
    }
  }

  public static String getServerUrl() {
    return SERVER_TEST_URL;
  }

  // Tears down the test database by clearing all collections.
  public static void tearDownTestDB() {
    MongoConfig.dropDatabase(DeploymentLevel.TEST);
  }

  public static void login(String username, String password) {
    JSONObject body = new JSONObject();
    body.put("password", password);
    body.put("username", username);
    HttpResponse<String> loginResponse =
        Unirest.post(SERVER_TEST_URL + "/login").body(body.toString()).asString();
    JSONObject loginResponseJSON = TestUtils.responseStringToJSON(loginResponse.getBody());
    assertThat(loginResponseJSON.getString("status")).isEqualTo("AUTH_SUCCESS");
  }

  public static void logout() {
    HttpResponse<String> logoutResponse = Unirest.get(SERVER_TEST_URL + "/logout").asString();
    JSONObject logoutResponseJSON = TestUtils.responseStringToJSON(logoutResponse.getBody());
    assertThat(logoutResponseJSON.getString("status")).isEqualTo("SUCCESS");
  }

  public static JSONObject responseStringToJSON(String response) {
    if (response.charAt(0) == '"') {
      String strippedResponse = response.substring(1, response.length() - 1).replace("\\", "");
      return new JSONObject(strippedResponse);
    }
    return new JSONObject(response);
  }
}
