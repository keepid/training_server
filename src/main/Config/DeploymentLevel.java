package Config;

// Deployment level represents what level of importance the application runs in.
// Production = what is live and exposed to actual users
// Staging = a live sandbox that we can have users ourselves
// Test = using our MongoDB Database but is cleared every run
// In Memory = everything is now local. You can run the application without internet and all the
// functionality should still be the same.
public enum DeploymentLevel {
  PRODUCTION,
  STAGING,
  TEST,
  IN_MEMORY
};
