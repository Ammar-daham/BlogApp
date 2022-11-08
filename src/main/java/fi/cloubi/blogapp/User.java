package fi.cloubi.blogapp;

import java.util.ArrayList;
import java.util.UUID;

public class User {
    
    private UUID userID;
    private String userName;
    private String email;
    private String password;
  

    private Boolean isUser;

    ArrayList<User> users = new ArrayList<>(); 
    
    public ArrayList<User> getUsers() {
        return users;
    }

    public void setUsers(ArrayList<User> users) {
        this.users = users;
    }

    public User() {

    }

    public User(String userName, String email, boolean isUser) {
        this.userID = UUID.randomUUID();
        this.userName = userName;
        this.email = email;
        this.isUser = isUser;
    }
    
    public void addUser(User user) {
        users.add(user);
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Boolean getIsUser() {
        return isUser;
    }

    public void setIsUser(Boolean isUser) {
        this.isUser = isUser;
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

    public String toString() {
        return String.format("Username: %s, Email: %s"
        , this.userName, this.email);
    }
    
    
}