package com.example.demo.controllers;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
@RestController
@RequestMapping("/api")
public class SQLInjectionController {

    private final String url = "jdbc:sqlserver://localhost:1433;databaseName=UserDB;encrypt=true;trustServerCertificate=true";
    private final String user = "sa";
    private final String pass = "1234";

    // input
    @PostMapping(value = "/classic", produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> classicSQLInjection(@RequestBody SQLInjectionRequest request) {
        String sql = "SELECT Username FROM UserLogin1 WHERE Username = '" + request.getInput() + "' ORDER BY ID ASC";

        try (Connection conn = DriverManager.getConnection(url, user, pass);
                Statement stmt = conn.createStatement()) {

            ResultSet rs = stmt.executeQuery(sql);
            StringBuilder users = new StringBuilder("Login success, Hello:\n");

            boolean found = false;
            while (rs.next()) {
                users.append(rs.getString("Username")).append("\n");
                found = true;
            }

            if (found) {
                return ResponseEntity.ok().contentType(MediaType.TEXT_PLAIN).body(users.toString());
            } else {
                return ResponseEntity.ok().contentType(MediaType.TEXT_PLAIN).body("User not found");
            }

        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }

    // Union-based SQL Injection
    @PostMapping("/union")
    public ResponseEntity<String> unionSQLInjection(@RequestBody SQLInjectionRequest request) {
        StringBuilder result = new StringBuilder();

        try (Connection conn = DriverManager.getConnection(url, user, pass);
                Statement stmt = conn.createStatement()) {

            // Làm chậm truy vấn 5 giây (SQL Server: WAITFOR DELAY)
            String sql = "SELECT Username FROM UserLogin1 WHERE Username = '" + request.getInput() +
                    "' UNION SELECT @@VERSION, NULL, NULL, NULL; WAITFOR DELAY '00:00:05' -- ";

            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                result.append(rs.getString(1)).append("\n");
            }

            return ResponseEntity.ok().contentType(MediaType.TEXT_PLAIN).body(result.toString());

        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }

    // Thêm data vào cơ sở dữ liệu
    @PostMapping("/union1")
    public ResponseEntity<String> unionSQLInjection1(@RequestBody SQLInjectionRequest request) {
        StringBuilder result = new StringBuilder();

        try (Connection conn = DriverManager.getConnection(url, user, pass);
                Statement stmt = conn.createStatement()) {

            // Xây dựng câu lệnh SQL dựa trên payload
            String sql = "SELECT Username FROM UserLogin1 WHERE Username = '" + request.getInput() +
                    "' UNION SELECT name, type_desc FROM sys.objects; " +
                    "WAITFOR DELAY '00:00:05' -- ";

            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                result.append(rs.getString(1)).append(" | ").append(rs.getString(2)).append("\n");
            }

            return ResponseEntity.ok().contentType(MediaType.TEXT_PLAIN).body(result.toString());

        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }

    // Blind SQL Injection (Boolean-based)
    @PostMapping("/blind-time")
    public ResponseEntity<String> blindTimeBasedSQLInjection(@RequestBody SQLInjectionRequest request) {
        String payload = request.getInput();
        // Tạo câu SQL với điều kiện dùng để kiểm tra và chờ đợi (delay)
        String sql = "IF (" + payload + ") WAITFOR DELAY '00:00:05'";

        try (Connection conn = DriverManager.getConnection(url, user, pass);
                Statement stmt = conn.createStatement()) {

            long start = System.currentTimeMillis(); // Bắt đầu tính thời gian
            stmt.execute(sql); // Thực thi câu lệnh SQL
            long duration = System.currentTimeMillis() - start; // Tính độ trễ

            // Kiểm tra thời gian delay
            if (duration >= 5000) {
                return ResponseEntity.ok("Login success (delayed)");
            } else {
                return ResponseEntity.ok("Login failed");
            }

        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("SQL Error: " + e.getMessage());
        }
    }

    // Time-based SQL Injection (WAITFOR DELAY)
    // @PostMapping("/time")
    // public ResponseEntity<String> timeSQLInjection(@RequestBody SQLInjectionRequest request) {
    //     try (Connection conn = DriverManager.getConnection(url, user, pass);
    //             Statement stmt = conn.createStatement()) {

    //         String sql = "WAITFOR DELAY '0:0:5'; SELECT * FROM UserLogin1 WHERE Username = '" + request.getInput()
    //                 + "'";
    //         stmt.execute(sql);

    //         return ResponseEntity.ok().contentType(MediaType.TEXT_PLAIN)
    //                 .body("Query executed (if delayed, injection worked)");

    //     } catch (SQLException e) {
    //         return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
    //     }
    // }

    // Error-based SQL Injection (SQL Error Message)
    @PostMapping("/error")
    public ResponseEntity<String> errorSQLInjection(@RequestBody SQLInjectionRequest request) {
        StringBuilder result = new StringBuilder();
        try (Connection conn = DriverManager.getConnection(url, user, pass);
                Statement stmt = conn.createStatement()) {

            // Tạo truy vấn SQL có thể gây lỗi
            String sql = "SELECT 1/0 FROM UserLogin1 WHERE Username = '" + request.getInput() + "'";

            // Thực thi truy vấn để kích hoạt lỗi
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                result.append(rs.getString(1)).append("\n");
            }

            return ResponseEntity.ok().contentType(MediaType.TEXT_PLAIN).body(result.toString());

        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("SQL Error: " + e.getMessage());
        }
    }

    // Stacked Queries Injection
    @PostMapping("/stacked")
    public ResponseEntity<String> stackedSQLInjection(@RequestBody SQLInjectionRequest request) {
        try (Connection conn = DriverManager.getConnection(url, user, pass);
                Statement stmt = conn.createStatement()) {

            String sql = "INSERT INTO Logs (Message) VALUES ('Hacked'); " +
                    "SELECT Username FROM UserLogin1 WHERE Username = '" + request.getInput() + "'";

            stmt.execute(sql);
            return ResponseEntity.ok().contentType(MediaType.TEXT_PLAIN)
                    .body("Query executed (Check if log was inserted)");

        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }

}
