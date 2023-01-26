package com.example.aggregate;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Users {
    private int id;
    private String username;
    private int age;
    private String email;
    private String password;


    public Users(Users userClass) {
        this.id = userClass.id;
        this.username= userClass.username;
        this.age =userClass.age;
        this.email = userClass.email;
        this.password = userClass.password; }

    public Users(int id, String username, int age, String email, String password) {
        this.id = id;
        this.username = username;
        this.age = age;
        this.email = email;
        this.password = password;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
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
}