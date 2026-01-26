package com.example.runtrackerapp.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

//name "User" is reserved in post gre
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long user_id;

    //One to Many - A user can have many run objects
    //mappedBy User, JPA now knows the Run entity has the foreign key pointing back to User
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Run> runs = new ArrayList<>();

    @Column (nullable = false, unique = true)
    private String username;

    @CreationTimestamp
    private Instant createdAt;

    @UpdateTimestamp
    private Instant updatedAt;

    public User(String username){
        if (username == null || username.isBlank()){
            throw new IllegalArgumentException("Username cannot be blank");
        }
        this.username = username;
    }

    protected User(){
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public List<Run> getRuns() {
        return runs;
    }

    public String getUsername() {
        return username;
    }

    public long getUser_id() {
        return user_id;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public void setRuns(List<Run> runs) {
        this.runs = runs;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setUser_id(long user_id) {
        this.user_id = user_id;
    }

    public void addRun(Run run){
        run.setUser(this);
    }

    public void addRuns(List<Run> runs){
        runs.forEach(run -> {this.addRun(run);});
    }

}
