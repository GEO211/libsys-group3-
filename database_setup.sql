-- Create the database
CREATE DATABASE IF NOT EXISTS library_system;
USE library_system;

-- Create Councils table
CREATE TABLE councils (
    council_id INT PRIMARY KEY AUTO_INCREMENT,
    council_name VARCHAR(50) NOT NULL,
    description TEXT
);

-- Create Courses table
CREATE TABLE courses (
    course_id INT PRIMARY KEY AUTO_INCREMENT,
    council_id INT,
    course_name VARCHAR(100) NOT NULL,
    course_code VARCHAR(20) NOT NULL,
    FOREIGN KEY (council_id) REFERENCES councils(council_id)
);

-- Create Students table
CREATE TABLE students (
    student_id INT PRIMARY KEY AUTO_INCREMENT,
    id_number VARCHAR(20) UNIQUE NOT NULL,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    course_id INT,
    council_id INT,
    school_year VARCHAR(20) NOT NULL,
    contact_number VARCHAR(20),
    email VARCHAR(100),
    registration_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    status ENUM('Active', 'Inactive', 'Graduated', 'On Leave') DEFAULT 'Active',
    FOREIGN KEY (course_id) REFERENCES courses(course_id),
    FOREIGN KEY (council_id) REFERENCES councils(council_id)
);

-- Create Admins table
CREATE TABLE IF NOT EXISTS admins (
    admin_id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    role ENUM('Super Admin', 'Librarian', 'Assistant') NOT NULL,
    email VARCHAR(100),
    contact_number VARCHAR(20),
    last_login DATETIME,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- Create Books table
CREATE TABLE books (
    book_id INT PRIMARY KEY AUTO_INCREMENT,
    isbn VARCHAR(20) UNIQUE,
    title VARCHAR(255) NOT NULL,
    author VARCHAR(100) NOT NULL,
    publisher VARCHAR(100),
    publication_year INT,
    category VARCHAR(50),
    quantity INT DEFAULT 1,
    available_quantity INT DEFAULT 1,
    location VARCHAR(50),
    added_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    status ENUM('Available', 'Reserved', 'Maintenance') DEFAULT 'Available'
);

-- Create Borrowing table
CREATE TABLE borrowings (
    borrowing_id INT PRIMARY KEY AUTO_INCREMENT,
    student_id INT,
    book_id INT,
    admin_id INT,
    borrow_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    due_date DATETIME NOT NULL,
    return_date DATETIME,
    status ENUM('Borrowed', 'Returned', 'Overdue') DEFAULT 'Borrowed',
    fine_amount DECIMAL(10,2) DEFAULT 0.00,
    FOREIGN KEY (student_id) REFERENCES students(student_id),
    FOREIGN KEY (book_id) REFERENCES books(book_id),
    FOREIGN KEY (admin_id) REFERENCES admins(admin_id)
);

-- Create Settings table
CREATE TABLE settings (
    setting_key VARCHAR(50) PRIMARY KEY,
    setting_value TEXT,
    description TEXT,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Insert default councils
INSERT INTO councils (council_name, description) VALUES
('CEAC', 'College of Engineering, Architecture and Computing'),
('CAS', 'College of Arts and Sciences'),
('CBA', 'College of Business Administration'),
('BPED', 'College of Education'),
('Personal', 'Faculty and Staff'),
('Others', 'External Users');

-- Insert default admin account
INSERT IGNORE INTO admins (username, password, first_name, last_name, role)
VALUES ('admin', 'admin123', 'System', 'Administrator', 'Super Admin');

-- Insert default settings
INSERT INTO settings (setting_key, setting_value, description) VALUES
('fine_rate', '1.00', 'Fine rate per day for overdue books'),
('borrow_days', '14', 'Default number of days for borrowing books'),
('email_notifications', 'true', 'Enable/disable email notifications'),
('smtp_host', 'smtp.gmail.com', 'SMTP server hostname'),
('smtp_port', '587', 'SMTP server port'),
('smtp_username', 'your.email@gmail.com', 'SMTP username'),
('smtp_password', 'your-app-password', 'SMTP password (for Gmail, use App Password)');

-- Create audit log table
CREATE TABLE audit_logs (
    log_id INT PRIMARY KEY AUTO_INCREMENT,
    action_type VARCHAR(50) NOT NULL,
    action_description TEXT,
    performed_by INT,
    timestamp DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (performed_by) REFERENCES admins(admin_id)
);

-- Create views for easy reporting
CREATE VIEW vw_available_books AS
SELECT b.*, (b.quantity - b.available_quantity) as borrowed_count
FROM books b
WHERE b.status = 'Available';

CREATE VIEW vw_student_borrowings AS
SELECT 
    b.borrowing_id,
    CONCAT(s.first_name, ' ', s.last_name) as student_name,
    s.id_number,
    bk.title as book_title,
    b.borrow_date,
    b.due_date,
    b.return_date,
    b.status,
    b.fine_amount
FROM borrowings b
JOIN students s ON b.student_id = s.student_id
JOIN books bk ON b.book_id = bk.book_id; 