
SELECT name, is_disabled FROM sys.sql_logins WHERE name = 'sa'; 
GO
SELECT SERVERPROPERTY('IsIntegratedSecurityOnly');
GO
EXEC sp_configure 'show advanced options', 1;
GO
RECONFIGURE;
GO
EXEC sp_configure 'remote access', 1;
GO
RECONFIGURE;
GO
ALTER LOGIN sa WITH PASSWORD = '1234';
GO
EXEC xp_instance_regread
    N'HKEY_LOCAL_MACHINE',
    N'Software\Microsoft\MSSQLServer\MSSQLServer',
    N'LoginMode';
GO
SELECT @@VERSION;
GO
Use UserDB;
CREATE TABLE UserLogin1 (
    ID INT IDENTITY(1,1) PRIMARY KEY,
    Username NVARCHAR(50) NOT NULL UNIQUE,
    Password NVARCHAR(50) NOT NULL,
    Email NVARCHAR(100),
    Role NVARCHAR(20)
);
INSERT INTO UserLogin1 (Username, Password, Email, Role) VALUES
('admin', 'admin123', 'admin@example.com', 'admin'),
('user1', 'password1', 'user1@example.com', 'user'),
('user2', 'password2', 'user2@example.com', 'user');
Go 
SELECT name FROM sys.sql_logins WHERE name = 'test_user';
CREATE TABLE Logs (
    ID INT PRIMARY KEY IDENTITY(1,1),  -- Dùng IDENTITY thay vì AUTO_INCREMENT
    Message TEXT NOT NULL,
    CreatedAt DATETIME DEFAULT GETDATE() -- Dùng GETDATE() thay vì CURRENT_TIMESTAMP
);
EXEC sp_configure 'Ad Hoc Distributed Queries';
CREATE TABLE UserLogin2 (
    ID INT IDENTITY(1,1) PRIMARY KEY,
    Username NVARCHAR(255) UNIQUE NOT NULL,
    PasswordHash NVARCHAR(255) NOT NULL,
    Bio NVARCHAR(MAX) NULL
);
INSERT INTO UserLogin2 (Username, PasswordHash, Bio) VALUES
('admin', '5f4dcc3b5aa765d61d8327deb882cf99', 'Administrator account'),
('john_doe', '25d55ad283aa400af464c76d713c07ad', 'I love coding!'),
('alice', '7c6a180b36896a0a8c02787eeafb0e4c', 'Security enthusiast'),
('bob', 'e99a18c428cb38d5f260853678922e03', 'Pentester & CTF player'),
('test_user', '098f6bcd4621d373cade4e832627b4f6', 'Test bio');
INSERT INTO UserLogin2 (Username, PasswordHash, Bio) 
VALUES ('attacker', 'fake_hashed_password', 'This is a test bio');
