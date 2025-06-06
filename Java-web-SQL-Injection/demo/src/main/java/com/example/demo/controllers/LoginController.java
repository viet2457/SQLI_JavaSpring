package com.example.demo.controllers;

import org.springframework.web.bind.annotation.*;
import java.sql.*;

@RestController
@RequestMapping("/api")
public class LoginController {

    @GetMapping("/login")
    public String showLoginPage() {
        return "<h1>404 Not Found<h1>";
    }
    @PostMapping(value = "/login", consumes = {"application/json", "application/x-www-form-urlencoded"})
    public String login(@RequestParam(required = false) String username, 
                        @RequestParam(required = false) String password, 
                        @RequestBody(required = false) LoginRequest loginRequest) {

       
        if (loginRequest != null) {
            username = loginRequest.username;
            password = loginRequest.password;
        }

        
        if (username == null || password == null) {
            return "Please enter username and password";
        }

        String url = "jdbc:sqlserver://localhost:1433;databaseName=UserDB;encrypt=true;trustServerCertificate=true";
        String user = "sa";
        String pass = "1234";

        try (Connection conn = DriverManager.getConnection(url, user, pass)) {
            
            String sql = "SELECT * FROM UserLogin WHERE Username = '" + username + "' AND Password = '" + password + "'";
            System.out.println("SQL Query: " + sql); 

            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            if (rs.next()) {
                return "Login success, Hello " + username;
            } else {
                return "Wrong username or password!";
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return "Error system!";
        }
    }

    // DTO nhận dữ liệu JSON
    public static class LoginRequest {
        public String username;
        public String password;
    }
}
