package chatApp.Entities;

import chatApp.Entities.Enums.PrivacyStatus;
import chatApp.Entities.Enums.UserStatus;
import chatApp.Entities.Enums.UserType;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Objects;

public class UserToPresent {

    private Long id;
    private String firstName;
    private String lastName;
    private String nickName;
    private String email;
    private byte[] profilePhoto;
    private LocalDate dateOfBirth;
    private String description;
    private UserType userType;
    private UserStatus userStatus;
    private PrivacyStatus privacyStatus;
    private boolean isMuted;
    private boolean isVerified;
    private Timestamp last_Loggedin;

    public UserToPresent(User user) {
        this.id = user.getId();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.nickName = user.getNickName();
        this.email = user.getEmail();
        this.profilePhoto = user.getProfilePhoto();
        this.dateOfBirth = user.getDateOfBirth();
        this.description = user.getDescription();
        this.userStatus = user.getUserStatus();
        this.userType = user.getUserType();
        this.privacyStatus = user.getPrivacyStatus();
        this.isMuted = user.isMuted();
        this.isVerified = user.isVerified();
        this.last_Loggedin = user.getLast_Loggedin();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Timestamp getLast_Loggedin() {
        return last_Loggedin;
    }

    public void setLast_Loggedin(Timestamp last_Loggedin) {
        this.last_Loggedin = last_Loggedin;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserToPresent that = (UserToPresent) o;
        return isMuted == that.isMuted && isVerified == that.isVerified && Objects.equals(id, that.id) && Objects.equals(firstName, that.firstName) && Objects.equals(lastName, that.lastName) && Objects.equals(nickName, that.nickName) && Objects.equals(email, that.email) && Arrays.equals(profilePhoto, that.profilePhoto) && Objects.equals(dateOfBirth, that.dateOfBirth) && Objects.equals(description, that.description) && userType == that.userType && userStatus == that.userStatus && privacyStatus == that.privacyStatus && Objects.equals(last_Loggedin, that.last_Loggedin);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(id, firstName, lastName, nickName, email, dateOfBirth, description, userType, userStatus, privacyStatus, isMuted, isVerified, last_Loggedin);
        result = 31 * result + Arrays.hashCode(profilePhoto);
        return result;
    }

    @Override
    public String toString() {
        return "User: \n" +
                "-----Required Information-----" +
                "\nEmail: " + email + "\nNickname: " + nickName +
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
}
