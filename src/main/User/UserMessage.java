package User;

import Config.Message;
import org.json.JSONObject;

public enum UserMessage implements Message {
  AUTH_SUCCESS("AUTH_SUCCESS:Successfully Authenticated User"),
  AUTH_FAILURE("AUTH_FAILURE:Wrong Credentials"),
  HASH_FAILURE("HASH_FAILURE:Check Argon2 documentation"),
  USER_NOT_FOUND("USER_NOT_FOUND:User does not exist in database."),
  SESSION_TOKEN_FAILURE("SESSION_TOKEN_FAILURE:Session tokens are incorrect."),
  INVALID_PARAMETER("INVALID_PARAMETER:Please check your parameter"),
  USERNAME_ALREADY_EXISTS("USERNAME_ALREADY_EXISTS:This username is taken."),
  SERVER_ERROR("SERVER_ERROR:There was an error with the server."),
  SUCCESS("SUCCESS:Success.");

  private String errorMessage;

  UserMessage(String errorMessage) {
    this.errorMessage = errorMessage;
  }

  public String toResponseString() {
    return toJSON().toString();
  }

  public Message withMessage(String message) {
    this.errorMessage = getErrorName() + ":" + message;
    return this;
  }

  public String getErrorName() {
    return this.errorMessage.split(":")[0];
  }

  public String getErrorDescription() {
    return this.errorMessage.split(":")[1];
  }

  public JSONObject toJSON() {
    JSONObject res = new JSONObject();
    res.put("status", getErrorName());
    res.put("message", getErrorDescription());
    return res;
  }

  public JSONObject toJSON(String message) {
    JSONObject res = new JSONObject();
    res.put("status", getErrorName());
    res.put("message", message);
    return res;
  }
}
