-- Create database
CREATE DATABASE IF NOT EXISTS library_system;
USE library_system;

-- Create admin table
CREATE TABLE IF NOT EXISTS admins (
    admin_id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    role VARCHAR(20) NOT NULL
);

-- Insert default admin
INSERT IGNORE INTO admins (username, password, first_name, last_name, role)
VALUES ('admin', 'admin123', 'System', 'Administrator', 'Super Admin');

-- Add audit_log table
CREATE TABLE IF NOT EXISTS audit_log (
    log_id INT PRIMARY KEY AUTO_INCREMENT,
    timestamp DATETIME DEFAULT CURRENT_TIMESTAMP,
    action VARCHAR(50) NOT NULL,
    book_id INT,
    student_id INT,
    admin_id INT,
    details TEXT,
    FOREIGN KEY (book_id) REFERENCES books(id),
    FOREIGN KEY (student_id) REFERENCES students(id),
    FOREIGN KEY (admin_id) REFERENCES admins(admin_id)
); 