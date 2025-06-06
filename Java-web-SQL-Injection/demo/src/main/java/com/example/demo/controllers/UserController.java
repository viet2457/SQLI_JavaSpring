package com.example.demo.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

import java.sql.*;

@RestController
@RequestMapping("/api")
public class UserController {

    // Thông tin kết nối Database
    private final String url = "jdbc:sqlserver://localhost:1433;databaseName=UserDB;encrypt=true;trustServerCertificate=true";
    private final String user = "sa";
    private final String pass = "1234";

     // 🚨 LỖ HỔNG: Cập nhật Bio mà không kiểm soát đầu vào
     @PostMapping("/update-bio")
     public ResponseEntity<String> updateBio(@RequestBody SQLInjectionRequest request) {
         try (Connection conn = DriverManager.getConnection(url, user, pass)) {
             // ❌ Không dùng PreparedStatement -> Dễ bị SQL Injection
             String sql = "UPDATE UserLogin2 SET Bio = '" + request.getBio() + "' WHERE Username = '" + request.getUsername() + "'";
             Statement stmt = conn.createStatement();
             int rowsUpdated = stmt.executeUpdate(sql);
 
             if (rowsUpdated > 0) {
                 return ResponseEntity.ok("Bio updated successfully.");
             } else {
                 return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
             }
         } catch (SQLException e) {
             return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                     .body("Error: " + e.getMessage());
         }
     }

    @PostMapping("/view-bio")
    public ResponseEntity<String> viewBio(@RequestBody Map<String, String> payload) {
        String username = payload.get("username");

        try (Connection conn = DriverManager.getConnection(url, user, pass)) {
            String sql = "SELECT Bio FROM UserLogin2 WHERE Username = '" + username + "'";
            Statement stmt = conn.createStatement(); // ❌ Không dùng PreparedStatement
            ResultSet rs = stmt.executeQuery(sql);

            if (rs.next()) {
                return ResponseEntity.ok(rs.getString("Bio")); // 🚨 Hiển thị trực tiếp dữ liệu từ DB
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
            }
        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error: " + e.getMessage());
        }
    }

}
