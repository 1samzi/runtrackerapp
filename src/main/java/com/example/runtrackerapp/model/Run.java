package com.example.runtrackerapp.model;

import com.example.runtrackerapp.dto.RunCreateRequestDTO;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

//results from jakarta persistence @Entity
//management for relational data
@Entity
public class Run{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long run_id;

    private double distanceKM;

    private int durationMinutes;

    private LocalDate date;

    private int rating;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @JsonBackReference
    private User user;

    public Run(double distanceKM, int durationMinutes, LocalDate date, int rating){
        this.distanceKM = distanceKM;
        this.durationMinutes = durationMinutes;
        this.date = date;
        this.rating = rating;
    }

    public Run() {

    }
    //getters and setters
    public long getRun_id() {
        return run_id;
    }

    public double getDistanceKM() {
        return distanceKM;
    }

    public int getDurationMinutes() {
        return durationMinutes;
    }

    public LocalDate getDate() {
        return date;
    }

    public int getRating() {
        return rating;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public void setDistanceKM(double distanceKM) {
        this.distanceKM = distanceKM;
    }

    public void setDurationMinutes(int durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public long getUserId(){
        return this.user.getUser_id();
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setRun_id(long run_id) {
        this.run_id = run_id;
    }

    public User getUser() {
        return user;
    }
}
