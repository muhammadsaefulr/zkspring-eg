package com.zkspringboot.model;

public class UserModel {

    private int userId;
    private String username;
    private String email;
    private String tanggal;

    public UserModel(int i, String username, String email, String tanggal) {
        this.userId = i;
        this.email = email;
        this.username = username;
        this.tanggal = tanggal;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getTanggal() {
        return tanggal;
    }

    public void setTanggal(String tanggal) {
        this.tanggal = tanggal;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}
