package com.example.demo.controllers;

public class SQLInjectionRequest {
    private String input;
    private String table; // Thêm thuộc tính table
    private String bio;
    private String username;
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    // Getter và Setter
    public String getInput() {
        return input;
    }

    public void setInput(String input) {
        this.input = input;
    }

    public String getTable() { // Thêm phương thức này
        return table;
    }

    public void setTable(String table) { // Setter nếu cần
        this.table = table;
    }
    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    
}
