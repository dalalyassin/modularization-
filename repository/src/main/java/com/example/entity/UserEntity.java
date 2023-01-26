package com.example.entity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name="User")
@Data
@NoArgsConstructor
public class UserEntity implements UserDetails {
    @Id
    @GeneratedValue
    private int id;
    @Column(unique = true)
    private String username;
    private int age;
    @Column(unique = true)
    private String email;

    private String password;

    @OneToMany(mappedBy="user", cascade= CascadeType.REMOVE)
    @JsonIgnore
    private List<TaskEntity> tasks ;

    @OneToMany(mappedBy = "user")
    private List<TokenEntity> Token = new ArrayList<>();


    public UserEntity(UserEntity userClass) {
        this.id = userClass.id;
        this.username= userClass.username;
        this.age =userClass.age;
        this.email = userClass.email;
        this.password = userClass.password; }

    public UserEntity(int id, String username, int age, String email, String password) {
        this.id = id;
        this.username = username;
        this.age = age;
        this.email = email;
        this.password = password;
    }

    public void setUsername(String username) {
        this.username = username;
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    public String getPassword() {

        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void addToken(TokenEntity tokens) {
      Token.add(tokens);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public List<TaskEntity> getTasks() {
        return tasks;
    }

    public void setTasks(List<TaskEntity> tasks) {
        this.tasks = tasks;
    }

    public List<TokenEntity> getToken() {
        return Token;
    }

    public void setToken(List<TokenEntity> token) {
        Token = token;
    }
}