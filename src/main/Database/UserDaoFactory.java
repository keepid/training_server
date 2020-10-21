package Database;

import Config.DeploymentLevel;

public class UserDaoFactory {
  public static UserDao create(DeploymentLevel deploymentLevel) {
    if (deploymentLevel == DeploymentLevel.TEST) {
      return new UserDaoTestImpl(deploymentLevel);
    }
    return new UserDaoImpl(deploymentLevel);
  }
}
