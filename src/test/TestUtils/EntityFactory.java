package TestUtils;

import Database.Dao;
import Security.SecurityUtils;
import User.User;
import User.UserType;
import Validation.ValidationException;

import java.util.Date;

public class EntityFactory {
  public static final long TEST_DATE = 1577862000000L; // Jan 1 2020

  public static PartialUser createUser() {
    return new PartialUser();
  }

  public static class PartialUser implements PartialObject<User> {
    private String firstName = "testFirstName";
    private String lastName = "testLastName";
    private String birthDate = "12-14-1997";
    private String email = "testemail@keep.id";
    private String phone = "1231231234";
    private String organization = "testOrganizationName";
    private String address = "123 Test St Av";
    private String city = "Philadelphia";
    private String state = "PA";
    private String zipcode = "19104";
    private String username = "testUser123";
    private String password = "testUser123";
    private boolean twoFactorOn = false;
    private Date creationDate = new Date(TEST_DATE);
    private UserType userType = UserType.Admin;

    @Override
    public User build() {
      try {
        User newUser =
            new User(
                firstName,
                lastName,
                birthDate,
                email,
                phone,
                organization,
                address,
                city,
                state,
                zipcode,
                twoFactorOn,
                username,
                password,
                userType);
        newUser.setCreationDate(creationDate);
        return newUser;
      } catch (ValidationException e) {
        throw new IllegalArgumentException("Illegal Param: " + e.toString());
      }
    }

    @Override
    public User buildAndPersist(Dao<User> dao) {
      User user = this.build();
      dao.save(user);
      return user;
    }

    public PartialUser withFirstName(String firstName) {
      this.firstName = firstName;
      return this;
    }

    public PartialUser withLastName(String lastName) {
      this.lastName = lastName;
      return this;
    }

    public PartialUser withBirthDate(String birthDate) {
      this.birthDate = birthDate;
      return this;
    }

    public PartialUser withEmail(String email) {
      this.email = email;
      return this;
    }

    public PartialUser withPhoneNumber(String phoneNumber) {
      this.phone = phoneNumber;
      return this;
    }

    public PartialUser withOrgName(String orgName) {
      this.organization = orgName;
      return this;
    }

    public PartialUser withAddress(String address) {
      this.address = address;
      return this;
    }

    public PartialUser withCity(String city) {
      this.city = city;
      return this;
    }

    public PartialUser withState(String state) {
      this.state = state;
      return this;
    }

    public PartialUser withZipcode(String zipcode) {
      this.zipcode = zipcode;
      return this;
    }

    public PartialUser withUsername(String username) {
      this.username = username;
      return this;
    }

    public PartialUser withPassword(String password) {
      this.password = password;
      return this;
    }

    public PartialUser withPasswordToHash(String password) {
      this.password = SecurityUtils.hashPassword(password);
      return this;
    }

    public PartialUser withUserType(UserType userType) {
      this.userType = userType;
      return this;
    }

    public PartialUser withTwoFactorState(boolean isTwoFactorOn) {
      this.twoFactorOn = isTwoFactorOn;
      return this;
    }

    public PartialUser withCreationDate(Date creationDate) {
      this.creationDate = creationDate;
      return this;
    }
  }

  public interface PartialObject<T> {
    public T build();

    public T buildAndPersist(Dao<T> dao);
  }
}
