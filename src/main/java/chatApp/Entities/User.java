package chatApp.Entities;

import chatApp.Entities.Enums.Prefix;
import chatApp.Entities.Enums.PrivacyStatus;
import chatApp.Entities.Enums.UserStatus;
import chatApp.Entities.Enums.UserType;
import com.sun.istack.NotNull;
import javax.persistence.*;
import java.time.LocalDate;
import java.util.Objects;

@Entity
@Table(name = "user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "user_Id")
    private int id;
    @Column(name = "first_name")
    private String firstName;
    @Column(name = "last_name")
    private String lastname;
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
    @Column(name = "user_prefix")
    private Prefix prefix;
    @Column(name = "user_is_Muted")
    private boolean isMuted;


    public User() {
    }

    public int getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
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

    public Prefix getPrefix() {
        return prefix;
    }

    public void setPrefix(Prefix prefix) {
        this.prefix = prefix;
    }

    public boolean isMuted() {
        return isMuted;
    }

    public void setMuted(boolean muted) {
        isMuted = muted;
    }

    public void adminMuteUser(User user){
        if(this.getUserType() == UserType.ADMIN && user.getUserType() != UserType.ADMIN
        && user.isMuted() == false){
            user.setMuted(true);
        }
    }

    public void adminUnmuteUser(User user){
        if(this.getUserType() == UserType.ADMIN && user.getUserType() != UserType.ADMIN
                && user.isMuted() == true){
            user.setMuted(false);
        }
    }

    public void switchUserStatus(UserStatus userStatus){
        this.userStatus = userStatus;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return id == user.id && Objects.equals(firstName, user.firstName) && Objects.equals(lastname, user.lastname)
                && Objects.equals(nickName, user.nickName) && Objects.equals(email, user.email)
                && Objects.equals(password, user.password) && Objects.equals(profilePhoto, user.profilePhoto)
                && Objects.equals(dateOfBirth, user.dateOfBirth) && Objects.equals(description, user.description)
                && userType == user.userType && userStatus == user.userStatus && privacyStatus == user.privacyStatus
                && prefix == user.prefix;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, firstName, lastname, nickName, email, password, profilePhoto, dateOfBirth,
                description, userType, userStatus, privacyStatus, prefix);
    }

    @Override
    public String toString() {
        return "User: \n" +
                "-----Required Information-----" +
                "\nEmail: " + email + "\nPassword: " + password + "\nNickname: " + nickName +
                "\n-----Optional Information-----\n" +
                "First name: " + firstName +
                "\nLast name: " + lastname +
                "\nDateOfBirth: " + dateOfBirth +
                "\nDescription: " + description +
                "\nUser type: " + userType +
                "\nUser status: " + userStatus +
                "\nPrivacy status: " + privacyStatus +
                "\nPrefix: " + prefix;
    }

    public static class UserBuilder {

        //Required Parameters
        private String email;
        private String password;
        private String nickName;

        //Optional Parameters
        private String description;
        private String firstName;
        private String lastname;
        private byte[] profilePhoto;
        private LocalDate dateOfBirth;
        private UserType userType = UserType.REGISTERED;
        private UserStatus userStatus = UserStatus.OFFLINE;
        private PrivacyStatus privacyStatus = PrivacyStatus.PUBLIC;
        private Prefix prefix;
        private boolean isMuted = false;

        public UserBuilder(String email, String password, String nickName) {
            this.email = email;
            this.password = password;
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

        public UserBuilder lastname(String lastname) {
            this.lastname = lastname;
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

        public UserBuilder dateOfBirth(Prefix prefix) {
            this.prefix = prefix;
            return this;
        }

        public UserBuilder dateOfBirth(LocalDate dateOfBirth) {
            this.dateOfBirth = dateOfBirth;
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
        this.prefix = builder.prefix;
        this.dateOfBirth = builder.dateOfBirth;
        this.firstName = builder.firstName;
        this.lastname = builder.lastname;
        this.userStatus = builder.userStatus;
        this.userType = builder.userType;
        this.privacyStatus = builder.privacyStatus;
        this.profilePhoto = builder.profilePhoto;
        this.isMuted = builder.isMuted;
    }
}
