package com.example.oauthmailer;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class UserModel {
    @Id
    private int id;
    private String username;
    public String full_name;
    public String email;

    public boolean regisMailSent = false;

    public boolean isRegisMailSent() {
        return regisMailSent;
    }

    public void setRegisMailSent(boolean regisMailSent) {
        this.regisMailSent = regisMailSent;
    }

    public String getUsername() {
        return username;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFull_name() {
        return full_name;
    }

    public void setFull_name(String full_name) {
        this.full_name = full_name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
