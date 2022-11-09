package fi.cloubi.blogapp;

import java.util.ArrayList;
import java.util.UUID;

public class User {
    
    private UUID userID;

    private String userName;

    private String email;

    private String password;

    private Boolean isLoggedIn;

    ArrayList<User> users = new ArrayList<User>();

    public User() {
    }

    public User(UUID userID, String userName, String email, String password, Boolean isLoggedIn) {
        this.userID = userID;
        this.userName = userName;
        this.email = email;
        this.password = password;
        this.isLoggedIn = isLoggedIn;
    }

    public UUID getUserID() {
        return userID;
    }

    public void setUserID(UUID userID) {
        this.userID = userID;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
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

    public Boolean getLoggedIn() {
        return isLoggedIn;
    }

    public void setLoggedIn(Boolean loggedIn) {
        isLoggedIn = loggedIn;
    }

    @Override
    public String toString() {
        return "{" +
                "userID=" + userID +
                ", userName='" + userName + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", isLoggedIn=" + isLoggedIn +
                '}';
    }
}
