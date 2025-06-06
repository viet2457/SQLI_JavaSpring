package com.example.demo.model;

import jakarta.persistence.*;

@Entity
@Table(name = "UserLogin")  // Tên bảng trong SQL Server
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username", unique = true, nullable = false)  // ✅ Đổi về chữ thường
    private String username;

    @Column(name = "password", nullable = false)
    private String password;

    // Getters và Setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
