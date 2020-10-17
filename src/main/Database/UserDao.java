package Database;

import static com.mongodb.client.model.Filters.eq;

import User.User;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

public class UserDao {
  public static User findOneUserOrNull(MongoDatabase db, String username) {
    MongoCollection<User> userCollection = db.getCollection("user", User.class);
    return userCollection.find(eq("username", username)).first();
  }
}
