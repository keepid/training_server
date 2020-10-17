package Config;

import Logger.LogFactory;
import User.UserController;
import com.mongodb.client.MongoDatabase;
import io.javalin.Javalin;
import io.javalin.core.compression.Brotli;
import io.javalin.core.compression.Gzip;

public class AppConfig {
  public static Long ASYNC_TIME_OUT = 10L;
  public static int SERVER_PORT = Integer.parseInt(System.getenv("PORT"));
  public static int SERVER_TEST_PORT = Integer.parseInt(System.getenv("TEST_PORT"));

  public static Javalin appFactory(DeploymentLevel deploymentLevel) {
    System.setProperty("logback.configurationFile", "../Logger/Resources/logback.xml");
    Javalin app = AppConfig.createJavalinApp(deploymentLevel);
    MongoConfig.getMongoClient();
    MongoDatabase db = MongoConfig.getDatabase(deploymentLevel);
    setApplicationHeaders(app);

    /* Utilities to pass to route handlers */
    LogFactory l = new LogFactory();
    l.createLogger();

    // We need to instantiate the controllers with the database.
    UserController userController = new UserController(db);

    /* -------------- DUMMY PATHS ------------------------- */
    app.get("/", ctx -> ctx.result("Welcome to the Keep.id Server"));

    /* -------------- USER AUTHENTICATION/USER RELATED ROUTES-------------- */
    app.post("/login", userController.loginUser);
    app.get("/logout", userController.logout);
    return app;
  }

  public static void setApplicationHeaders(Javalin app) {
    app.before(
        ctx -> {
          ctx.header("Content-Security-Policy", "script-src 'self' 'unsafe-inline';");
          ctx.header("X-Frame-Options", "SAMEORIGIN");
          ctx.header("X-Xss-Protection", "1; mode=block");
          ctx.header("X-Content-Type-Options", "nosniff");
          ctx.header("Referrer-Policy", "no-referrer-when-downgrade");
          ctx.header("Access-Control-Allow-Credentials", "true");
        });
  }

  public static Javalin createJavalinApp(DeploymentLevel deploymentLevel) {
    int port;
    switch (deploymentLevel) {
      case STAGING:
      case PRODUCTION:
        port = SERVER_PORT;
        break;
      case TEST:
        port = SERVER_TEST_PORT;
        break;
      default:
        throw new IllegalStateException(
            "Remember to config your port according to: " + deploymentLevel);
    }
    return Javalin.create(
            config -> {
              config.asyncRequestTimeout =
                  ASYNC_TIME_OUT; // timeout for async requests (default is 0, no timeout)
              config.autogenerateEtags = false; // auto generate etags (default is false)
              config.compressionStrategy(
                  new Brotli(4),
                  new Gzip(6)); // set the compression strategy and levels - since 3.2.0
              config.contextPath = "/"; // context path for the http servlet (default is "/")
              config.defaultContentType =
                  "text/plain"; // content type to use if no content type is set (default is
              // "text/plain")

              config.enableCorsForAllOrigins(); // enable cors for all origins

              //              config.enableDevLogging(); // enable extensive development logging for
              // http and
              // websocket
              config.enforceSsl = false;
              config.logIfServerNotStarted = true;
              config.showJavalinBanner = false;
              config.prefer405over404 = false;
              config.sessionHandler(
                  () -> {
                    try {
                      return SessionConfig.getSessionHandlerInstance(deploymentLevel);
                    } catch (Exception e) {
                      System.err.println("Unable to instantiate session handler.");
                      e.printStackTrace();
                      System.exit(1);
                      return null;
                    }
                  });
            })
        .start(port);
  }
}