package chatApp.Entities;

import chatApp.Entities.Enums.PrivacyStatus;
import chatApp.Entities.Enums.UserStatus;
import chatApp.Entities.Enums.UserType;
import com.sun.istack.NotNull;
import org.springframework.web.bind.annotation.RequestBody;

import javax.persistence.*;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.Objects;

import static chatApp.utilities.Utilities.createRandomString;


@Entity
@Table(name = "user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "user_Id")
    private Long id;
    @Column(name = "first_name")
    private String firstName;
    @Column(name = "last_name")
    private String lastName;
    @Column(name = "user_nickname")
    private String nickName;
    @Column(name = "user_email", unique = true)
    @NotNull
    private String email;
    @Column(name = "user_password")
    private String password;
    @Lob
    @Column(name = "profile_photo", columnDefinition = "mediumblob")
    private byte[] profilePhoto;
    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;
    @Column(name = "user_description")
    private String description;
    @Column(name = "user_type")
    private UserType userType;
    @Column(name = "user_status")
    private UserStatus userStatus;
    @Column(name = "user_privacy")
    private PrivacyStatus privacyStatus;
    @Column(name = "user_is_muted")
    private boolean isMuted;
    @Column(name = "user_token")
    private String token;
    @Column(name = "is_verified")
    private boolean isVerified;
    @Column(name = "last_loggedin")
    private Timestamp last_Loggedin;
    @Column(name = "verification_code")
    private String verificationCode;

    public String getVerificationCode() {
        return verificationCode;
    }

    public void setVerificationCode(String verificationCode) {
        this.verificationCode = verificationCode;
    }

    public User() {
    }

    public Long getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public byte[] getProfilePhoto() {
        return profilePhoto;
    }

    public void setProfilePhoto(byte[] profilePhoto) {
        this.profilePhoto = profilePhoto;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public UserType getUserType() {
        return userType;
    }

    public void setUserType(UserType userType) {
        this.userType = userType;
    }

    public UserStatus getUserStatus() {
        return userStatus;
    }

    public void setUserStatus(UserStatus userStatus) {
        this.userStatus = userStatus;
    }

    public PrivacyStatus getPrivacyStatus() {
        return privacyStatus;
    }

    public void setPrivacyStatus(PrivacyStatus privacyStatus) {
        this.privacyStatus = privacyStatus;
    }

    public boolean isMuted() {
        return isMuted;
    }

    public void setMuted(boolean muted) {
        isMuted = muted;
    }

    public boolean isVerified() {
        return isVerified;
    }

    public void setVerified(boolean verified) {
        isVerified = verified;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;

    }

    public Timestamp getLast_Loggedin() {
        return last_Loggedin;
    }

    public void setLast_Loggedin(Timestamp last_Loggedin) {
        this.last_Loggedin = last_Loggedin;
    }

    public void adminMuteUser(User user) {
        if (this.getUserType() == UserType.ADMIN && user.getUserType() != UserType.ADMIN
                && user.isMuted() == false) {
            user.setMuted(true);
        }
    }

    public void adminUnmuteUser(User user) {
        if (this.getUserType() == UserType.ADMIN && user.getUserType() != UserType.ADMIN
                && user.isMuted() == true) {
            user.setMuted(false);
        }
    }

    public void switchUserStatus(UserStatus userStatus) {
        if (!userStatus.equals(UserStatus.OFFLINE)) {
            this.userStatus = userStatus;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return id == user.id && Objects.equals(firstName, user.firstName) && Objects.equals(lastName, user.lastName)
                && Objects.equals(nickName, user.nickName) && Objects.equals(email, user.email)
                && Objects.equals(password, user.password) && Objects.equals(profilePhoto, user.profilePhoto)
                && Objects.equals(dateOfBirth, user.dateOfBirth) && Objects.equals(description, user.description)
                && userType == user.userType && userStatus == user.userStatus && privacyStatus == user.privacyStatus
                && Objects.equals(verificationCode, user.verificationCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, firstName, lastName, nickName, email, password, profilePhoto, dateOfBirth,
                description, userType, userStatus, privacyStatus, verificationCode);
    }

    @Override
    public String toString() {
        return "User: \n" +
                "-----Required Information-----" +
                "\nEmail: " + email + "\nPassword: " + password + "\nNickname: " + nickName +
                "\n-----Optional Information-----\n" +
                "First name: " + firstName +
                "\nLast name: " + lastName +
                "\nDateOfBirth: " + dateOfBirth +
                "\nDescription: " + description +
                "\nUser type: " + userType +
                "\nUser status: " + userStatus +
                "\nPrivacy status: " + privacyStatus +
                "\nIs verified: " + isVerified;
    }

    public static User createNewUser(@RequestBody RequestAddUser request)
    {

        User user = new User.UserBuilder(request.getEmail(), request.getPassword(),
                request.getNickName()).firstName(request.getFirstName()).lastName(request.getLastName()).description(request.getDescription())
                .profilePhoto(null).build();
        return user;
    }


    public static class UserBuilder {

        //Required Parameters
        private String email;
        private String password;
        private String nickName;

        //Optional Parameters
        private String description;
        private String firstName;
        private String lastName;
        private byte[] profilePhoto;
        private LocalDate dateOfBirth;
        private UserType userType = UserType.REGISTERED;
        private UserStatus userStatus = UserStatus.OFFLINE;
        private PrivacyStatus privacyStatus = PrivacyStatus.PUBLIC;
        private boolean isMuted = false;
        private boolean isValidated = false;
        private String token;

        private String verificationCode = createRandomString(8);

        public UserBuilder(String email, String password, String nickName) {
            this.email = email;
            this.password = password;
            this.nickName = nickName;
        }

        public UserBuilder(String nickName) {
            this.nickName = nickName;
        }

        public UserBuilder description(String description) {
            this.description = description;
            return this;
        }

        public UserBuilder firstName(String firstName) {
            this.firstName = firstName;
            return this;
        }

        public UserBuilder lastName(String lastName) {
            this.lastName = lastName;
            return this;
        }

        public UserBuilder profilePhoto(byte[] profilePhoto) {
            this.profilePhoto = profilePhoto;
            return this;
        }

        public UserBuilder userType(UserType userType) {
            this.userType = userType;
            return this;
        }

        public UserBuilder userStatus(UserStatus userStatus) {
            this.userStatus = userStatus;
            return this;
        }

        public UserBuilder privacyStatus(PrivacyStatus privacyStatus) {
            this.privacyStatus = privacyStatus;
            return this;
        }

        public UserBuilder dateOfBirth(LocalDate dateOfBirth) {
            this.dateOfBirth = dateOfBirth;
            return this;
        }

        public UserBuilder token(String token) {
            this.token = token;
            return this;
        }

        public UserBuilder verificationCode() {
            this.verificationCode = createRandomString(8);
            return this;
        }

        public User build() {
            return new User(this);
        }
    }

    private User(UserBuilder builder) {
        this.email = builder.email;
        this.password = builder.password;
        this.nickName = builder.nickName;
        this.description = builder.description;
        this.dateOfBirth = builder.dateOfBirth;
        this.firstName = builder.firstName;
        this.lastName = builder.lastName;
        this.userStatus = builder.userStatus;
        this.userType = builder.userType;
        this.privacyStatus = builder.privacyStatus;
        this.profilePhoto = builder.profilePhoto;
        this.isMuted = builder.isMuted;
        this.isVerified = builder.isValidated;
        this.token = builder.token;
        this.verificationCode = builder.verificationCode;
    }
}
