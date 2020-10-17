package User;

import Config.Message;
import Logger.LogFactory;
import User.Services.GetUserInfoService;
import User.Services.LoginService;
import com.mongodb.client.MongoDatabase;
import io.javalin.http.Handler;
import org.json.JSONObject;
import org.slf4j.Logger;

public class UserController {
  Logger logger;
  MongoDatabase db;

  public UserController(MongoDatabase db) {
    this.db = db;
    LogFactory l = new LogFactory();
    logger = l.createLogger("UserController");
    logger = (new LogFactory()).createLogger("UserController");
  }

  public Handler loginUser =
      ctx -> {
        ctx.req.getSession().invalidate();
        JSONObject req = new JSONObject(ctx.body());
        String username = req.getString("username");
        String password = req.getString("password");
        logger.info("Attempting to login " + username);

        LoginService loginService = new LoginService(db, logger, username, password);
        Message response = loginService.executeAndGetResponse();
        logger.info(response.toString() + response.getErrorDescription());

        JSONObject responseJSON = response.toJSON();
        if (response == UserMessage.AUTH_SUCCESS) {
          responseJSON.put("firstName", loginService.getFirstName());
          responseJSON.put("lastName", loginService.getLastName());

          ctx.sessionAttribute("username", loginService.getUsername());
          ctx.sessionAttribute("fullName", loginService.getFullName());
        } else {
          responseJSON.put("userRole", "");
          responseJSON.put("organization", "");
          responseJSON.put("firstName", "");
          responseJSON.put("lastName", "");
          responseJSON.put("twoFactorOn", "");
        }
        ctx.result(responseJSON.toString());
      };

  public Handler logout =
      ctx -> {
        ctx.req.getSession().invalidate();
        logger.info("Signed out");
        ctx.result(UserMessage.SUCCESS.toJSON().toString());
      };

  public Handler getUserInfo =
      ctx -> {
        logger.info("Started getUserInfo handler");
        String username = ctx.sessionAttribute("username");
        GetUserInfoService infoService = new GetUserInfoService(db, logger, username);
        Message response = infoService.executeAndGetResponse();
        if (response != UserMessage.SUCCESS) { // if fail return
          ctx.result(response.toJSON().toString());
        } else {
          JSONObject userInfo = infoService.getUserFields(); // get user info here
          JSONObject mergedInfo = mergeJSON(response.toJSON(), userInfo);
          ctx.result(mergedInfo.toString());
        }
      };

  public static JSONObject mergeJSON(
      JSONObject object1, JSONObject object2) { // helper function to merge 2 json objects
    JSONObject merged = new JSONObject(object1, JSONObject.getNames(object1));
    for (String key : JSONObject.getNames(object2)) {
      merged.put(key, object2.get(key));
    }
    return merged;
  }
}
