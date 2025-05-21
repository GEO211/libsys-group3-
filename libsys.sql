-- --------------------------------------------------------
-- Host:                         127.0.0.1
-- Server version:               10.4.32-MariaDB - mariadb.org binary distribution
-- Server OS:                    Win64
-- HeidiSQL Version:             12.10.0.7000
-- --------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;


-- Dumping database structure for library_system
DROP DATABASE IF EXISTS `library_system`;
CREATE DATABASE IF NOT EXISTS `library_system` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci */;
USE `library_system`;

-- Dumping structure for table library_system.admins
DROP TABLE IF EXISTS `admins`;
CREATE TABLE IF NOT EXISTS `admins` (
  `admin_id` int(11) NOT NULL AUTO_INCREMENT,
  `username` varchar(50) NOT NULL,
  `password` varchar(255) NOT NULL,
  `first_name` varchar(50) NOT NULL,
  `last_name` varchar(50) NOT NULL,
  `role` enum('Super Admin','Librarian','Assistant') NOT NULL,
  `email` varchar(100) DEFAULT NULL,
  `contact_number` varchar(20) DEFAULT NULL,
  `last_login` datetime DEFAULT NULL,
  `created_at` datetime DEFAULT current_timestamp(),
  PRIMARY KEY (`admin_id`),
  UNIQUE KEY `username` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- Dumping data for table library_system.admins: ~11 rows (approximately)
INSERT INTO `admins` (`admin_id`, `username`, `password`, `first_name`, `last_name`, `role`, `email`, `contact_number`, `last_login`, `created_at`) VALUES
	(1, 'a', 'a', 'System', 'Administrator', 'Super Admin', 'admin@library.com', NULL, '2025-04-30 14:43:09', '2025-01-29 12:29:01'),
	(4, 'geo21', 'geo21', 'Geoo', 'Development', 'Super Admin', '21geo@gmail.com', NULL, '2025-02-24 18:30:11', '2025-02-24 18:30:01'),
	(5, 'MikoLang', 'tanginamo', 'Miko', 'James', 'Librarian', 'mesteban1@gmail.com', NULL, '2025-02-24 18:31:59', '2025-02-24 18:31:46'),
	(6, 'biboy', 'cutiepanda0', 'Daniel', 'Libunao', 'Librarian', 'libunaodaniel008@gmail.com', NULL, '2025-02-24 18:34:46', '2025-02-24 18:34:31'),
	(7, 'diwata', 'diwatapares', 'diwata', 'pares', 'Librarian', 'diwatapares@gmail.com', NULL, '2025-02-24 18:41:56', '2025-02-24 18:39:05'),
	(8, 'louishin_8', 'louise08', 'Louise', 'Ayco', 'Librarian', 'louisesofe@gmail.com', NULL, '2025-02-24 18:48:03', '2025-02-24 18:46:44'),
	(9, '221GEO', 'QWERT@123', 'Geo', 'Development', 'Librarian', 'Geodevelopment@geodev.xyz', NULL, NULL, '2025-04-02 18:59:58'),
	(10, '21GEo21', 'oQ4MAn2GfV7nV3kjYhDg3pIBudOSCYgI+QqmCrvLl0KR5cPT9tw3pO8NA1wLcWkZ', 'Geoo', 'Dev', 'Librarian', 'geodev@gmail.com', NULL, NULL, '2025-04-30 14:55:31'),
	(11, 'geopogi', '8mitlmGITtcLMSSwXDJ5lR2IW2pidfhRh3lmkcxXL6JZQoLeTqKVi4i0gx4qRWu1', 'Renniel Geo', 'De Guzman Geanga', 'Librarian', 'geo@gmail.com', NULL, '2025-05-20 23:29:45', '2025-04-30 14:58:43'),
	(12, 'louishin', 'jgYo2PxV/Lf2I1Vv3nDf8C/R4cPiYhmcjS7llPZ1DIBbRCrEJWADZGUPLlg+cIqS', 'Louise', 'Ayco', 'Librarian', 'louisesofeayco@gmail.com', NULL, NULL, '2025-05-05 18:19:42'),
	(13, 'samsam', '/mGl2rvAvCzwlzbuAnWicQGA/npFSDEAyQ87OSqra+QeV6LAF21QJ+i+wrkYPYIr', 'Jessa', 'Morte', 'Librarian', 'mortejessa890@gmail.com', NULL, '2025-05-05 18:20:55', '2025-05-05 18:20:27'),
	(14, 'georgie', '8V7Ind7HmwLqENozZbrtj8VaEzbapWvd2HWUvHvXGOetICAG5Ypf1WtsJGaVYJ7V', 'George', 'Salami', 'Librarian', 'Gs@gmail.com', NULL, '2025-05-20 22:27:33', '2025-05-20 22:27:28');

-- Dumping structure for table library_system.audit_log
DROP TABLE IF EXISTS `audit_log`;
CREATE TABLE IF NOT EXISTS `audit_log` (
  `log_id` int(11) NOT NULL AUTO_INCREMENT,
  `timestamp` datetime DEFAULT current_timestamp(),
  `action` varchar(50) NOT NULL,
  `book_id` int(11) DEFAULT NULL,
  `student_id` int(11) DEFAULT NULL,
  `admin_id` int(11) DEFAULT NULL,
  `details` text DEFAULT NULL,
  PRIMARY KEY (`log_id`),
  KEY `book_id` (`book_id`),
  KEY `student_id` (`student_id`),
  KEY `admin_id` (`admin_id`),
  CONSTRAINT `audit_log_ibfk_1` FOREIGN KEY (`book_id`) REFERENCES `books` (`book_id`),
  CONSTRAINT `audit_log_ibfk_2` FOREIGN KEY (`student_id`) REFERENCES `students` (`student_id`),
  CONSTRAINT `audit_log_ibfk_3` FOREIGN KEY (`admin_id`) REFERENCES `admins` (`admin_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- Dumping data for table library_system.audit_log: ~0 rows (approximately)

-- Dumping structure for table library_system.books
DROP TABLE IF EXISTS `books`;
CREATE TABLE IF NOT EXISTS `books` (
  `book_id` int(11) NOT NULL AUTO_INCREMENT,
  `isbn` varchar(20) DEFAULT NULL,
  `title` varchar(255) NOT NULL,
  `author` varchar(100) NOT NULL,
  `publisher` varchar(100) DEFAULT NULL,
  `publication_year` int(11) DEFAULT NULL,
  `category` varchar(50) DEFAULT NULL,
  `quantity` int(11) DEFAULT 1,
  `available_quantity` int(11) DEFAULT 1,
  `location` varchar(50) DEFAULT NULL,
  `added_date` datetime DEFAULT current_timestamp(),
  `status` enum('Available','Reserved','Maintenance') DEFAULT 'Available',
  `active` tinyint(1) DEFAULT 1,
  `price` double NOT NULL,
  PRIMARY KEY (`book_id`),
  UNIQUE KEY `isbn` (`isbn`)
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- Dumping data for table library_system.books: ~11 rows (approximately)
INSERT INTO `books` (`book_id`, `isbn`, `title`, `author`, `publisher`, `publication_year`, `category`, `quantity`, `available_quantity`, `location`, `added_date`, `status`, `active`, `price`) VALUES
	(3, '1', 'tea', 'adfs', 'adaw', 121, 'wad', 1, 1, 'adw', '2025-01-29 15:33:29', 'Available', 1, 0),
	(4, '199067', 'My Sample of death', 'Francis Macapagal', 'Retrogal', 2009, 'horror', 2, 2, 'Koronadal City, South Cotabato', '2025-02-24 18:37:03', 'Available', 1, 0),
	(5, '230492', 'A guide how to not become a dictator', 'diwata pares', 'diwata', 2025, 'war crimes', 2, 1, 'koronadal', '2025-02-24 18:45:01', 'Available', 1, 0),
	(6, '12312', '', '', '', 2024, '', 2, 2, '', '2025-04-02 19:01:32', 'Available', 0, 0),
	(7, '123', 'GE', 'GG', 'GG', 2027, 'TECH', 2, 2, 'TE', '2025-04-02 19:02:04', 'Available', 1, 0),
	(9, '11', '', '', '', 2025, '2', 1, 1, '', '2025-04-14 18:46:56', 'Available', 0, 0),
	(12, '12', '', '', '', 2024, '', 2, 2, 'marbel', '2025-04-14 18:50:17', 'Available', 0, 0),
	(13, '23456', '', '', '', 234, '', 12, 12, '', '2025-04-14 18:53:21', 'Available', 0, 0),
	(14, '12321', 'gege', '2342', 'savr', 12321, 'horror', 11, 11, '23r2', '2025-04-14 18:57:08', 'Available', 1, 0),
	(15, '786', 'fyfghj', 'frtfyghvj', 'hjgjhkbk', 67890, 'uhvjhkj', 44, 44, 'marv', '2025-04-14 18:59:51', 'Available', 1, 0),
	(16, '456789', 'Test', 'TEst', 'wer6ew', 20241, 'fghd', 12345, 12345, 'fewegrhtjy', '2025-04-30 15:01:29', 'Available', 1, 0);

-- Dumping structure for table library_system.borrowings
DROP TABLE IF EXISTS `borrowings`;
CREATE TABLE IF NOT EXISTS `borrowings` (
  `borrowing_id` int(11) NOT NULL AUTO_INCREMENT,
  `student_id` int(11) DEFAULT NULL,
  `book_id` int(11) DEFAULT NULL,
  `admin_id` int(11) DEFAULT NULL,
  `borrow_date` datetime DEFAULT current_timestamp(),
  `due_date` datetime NOT NULL,
  `return_date` datetime DEFAULT NULL,
  `status` varchar(20) DEFAULT NULL,
  `fine_amount` decimal(10,2) DEFAULT 0.00,
  PRIMARY KEY (`borrowing_id`),
  KEY `student_id` (`student_id`),
  KEY `book_id` (`book_id`),
  KEY `admin_id` (`admin_id`),
  CONSTRAINT `borrowings_ibfk_1` FOREIGN KEY (`student_id`) REFERENCES `students` (`student_id`),
  CONSTRAINT `borrowings_ibfk_2` FOREIGN KEY (`book_id`) REFERENCES `books` (`book_id`),
  CONSTRAINT `borrowings_ibfk_3` FOREIGN KEY (`admin_id`) REFERENCES `admins` (`admin_id`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- Dumping data for table library_system.borrowings: ~10 rows (approximately)
INSERT INTO `borrowings` (`borrowing_id`, `student_id`, `book_id`, `admin_id`, `borrow_date`, `due_date`, `return_date`, `status`, `fine_amount`) VALUES
	(3, 3, 3, 1, '2025-01-29 15:35:13', '2025-02-12 15:35:13', '2025-01-29 17:34:23', 'Cancelled', 0.00),
	(4, 4, 3, 1, '2025-01-29 18:17:48', '2025-02-12 18:17:48', '2025-02-05 18:26:31', 'Cancelled', 0.00),
	(5, 5, 3, 1, '2025-02-05 18:27:45', '2025-02-19 18:27:45', '2025-02-12 17:15:11', 'Returned', 0.00),
	(6, 7, 5, 1, '2025-04-02 19:02:40', '2025-04-16 19:02:40', '2025-04-30 15:09:22', 'Returned', 14.00),
	(7, 7, 5, 1, '2025-03-21 19:03:02', '2025-04-16 19:03:02', '2025-04-30 15:09:27', 'Returned', 14.00),
	(8, 10, 4, 11, '2025-04-30 15:52:02', '2025-05-14 15:52:02', '2025-04-30 15:52:43', 'Returned', 0.00),
	(9, 9, 5, 13, '2025-05-05 18:21:18', '2025-06-04 18:21:18', '2025-05-05 18:22:08', 'Returned', 0.00),
	(10, 9, 4, 13, '2025-05-05 18:22:22', '2025-05-19 18:22:22', '2025-05-14 18:15:07', 'Returned', 0.00),
	(11, 9, 5, 11, '2025-05-05 19:05:43', '2025-05-19 19:05:02', NULL, 'Borrowed', 0.00),
	(12, 9, 3, 11, '2025-05-05 19:08:04', '2025-05-19 19:07:41', '2025-05-05 19:08:21', 'Returned', 0.00);

-- Dumping structure for table library_system.councils
DROP TABLE IF EXISTS `councils`;
CREATE TABLE IF NOT EXISTS `councils` (
  `council_id` int(11) NOT NULL AUTO_INCREMENT,
  `council_name` varchar(50) NOT NULL,
  `description` text DEFAULT NULL,
  PRIMARY KEY (`council_id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- Dumping data for table library_system.councils: ~7 rows (approximately)
INSERT INTO `councils` (`council_id`, `council_name`, `description`) VALUES
	(1, 'CEAC', 'College of Engineering, Architecture and Computing'),
	(2, 'CAS', 'College of Arts and Sciences'),
	(3, 'CBA', 'College of Business Administration'),
	(4, 'BPED', 'College of Education'),
	(5, 'Personal', 'Faculty and Staff'),
	(6, 'Others', 'External Users'),
	(10, 'GEO DEV', 'test Teadvsf F');

-- Dumping structure for table library_system.courses
DROP TABLE IF EXISTS `courses`;
CREATE TABLE IF NOT EXISTS `courses` (
  `course_id` int(11) NOT NULL AUTO_INCREMENT,
  `council_id` int(11) DEFAULT NULL,
  `course_name` varchar(100) NOT NULL,
  `course_code` varchar(20) NOT NULL,
  `active` tinyint(1) DEFAULT 1,
  PRIMARY KEY (`course_id`),
  KEY `council_id` (`council_id`),
  KEY `idx_courses_active` (`active`),
  CONSTRAINT `courses_ibfk_1` FOREIGN KEY (`council_id`) REFERENCES `councils` (`council_id`)
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- Dumping data for table library_system.courses: ~11 rows (approximately)
INSERT INTO `courses` (`course_id`, `council_id`, `course_name`, `course_code`, `active`) VALUES
	(4, 1, 'BSIT', '2024 - 2025', 1),
	(5, 1, 'BSCS', 'aftc-1222', 1),
	(6, 1, 'test', 'test', 0),
	(7, 1, 'asd', 'afc', 1),
	(8, 2, '', '', 0),
	(9, 4, '', '', 0),
	(10, 4, '', '', 0),
	(11, 4, '', '', 0),
	(12, 4, '', '', 0),
	(13, 4, '', '', 0),
	(14, 10, 'RETSG', 'GEoo', 1);

-- Dumping structure for table library_system.reports
DROP TABLE IF EXISTS `reports`;
CREATE TABLE IF NOT EXISTS `reports` (
  `report_id` int(11) NOT NULL AUTO_INCREMENT,
  `report_type` varchar(50) NOT NULL,
  `book_id` int(11) DEFAULT NULL,
  `person_type` varchar(20) DEFAULT NULL,
  `person_id` varchar(50) DEFAULT NULL,
  `description` text DEFAULT NULL,
  `status` varchar(20) DEFAULT 'Pending',
  `processed_by` int(11) DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp(),
  `resolved_at` timestamp NOT NULL DEFAULT current_timestamp(),
  PRIMARY KEY (`report_id`),
  KEY `book_id` (`book_id`),
  KEY `processed_by` (`processed_by`),
  CONSTRAINT `reports_ibfk_1` FOREIGN KEY (`book_id`) REFERENCES `books` (`book_id`),
  CONSTRAINT `reports_ibfk_2` FOREIGN KEY (`processed_by`) REFERENCES `admins` (`admin_id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- Dumping data for table library_system.reports: ~0 rows (approximately)

-- Dumping structure for table library_system.settings
DROP TABLE IF EXISTS `settings`;
CREATE TABLE IF NOT EXISTS `settings` (
  `setting_key` varchar(50) NOT NULL,
  `setting_value` text DEFAULT NULL,
  `description` text DEFAULT NULL,
  `updated_at` datetime DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  PRIMARY KEY (`setting_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- Dumping data for table library_system.settings: ~7 rows (approximately)
INSERT INTO `settings` (`setting_key`, `setting_value`, `description`, `updated_at`) VALUES
	('borrow_days', '14', 'Default number of days for borrowing books', '2025-01-29 12:29:01'),
	('email_notifications', 'true', 'Enable/disable email notifications', '2025-01-29 12:29:01'),
	('fine_rate', '1.00', 'Fine rate per day for overdue books', '2025-01-29 12:29:01'),
	('smtp_host', 'smtp.gmail.com', 'SMTP server hostname', '2025-01-29 12:29:01'),
	('smtp_password', 'your-app-password', 'SMTP password (for Gmail, use App Password)', '2025-01-29 12:29:01'),
	('smtp_port', '587', 'SMTP server port', '2025-01-29 12:29:01'),
	('smtp_username', 'your.email@gmail.com', 'SMTP username', '2025-01-29 12:29:01');

-- Dumping structure for table library_system.students
DROP TABLE IF EXISTS `students`;
CREATE TABLE IF NOT EXISTS `students` (
  `student_id` int(11) NOT NULL AUTO_INCREMENT,
  `id_number` varchar(20) NOT NULL,
  `first_name` varchar(50) NOT NULL,
  `last_name` varchar(50) NOT NULL,
  `course_id` int(11) DEFAULT NULL,
  `council_id` int(11) DEFAULT NULL,
  `school_year` varchar(20) NOT NULL,
  `contact_number` varchar(20) DEFAULT NULL,
  `email` varchar(100) DEFAULT NULL,
  `registration_date` datetime DEFAULT current_timestamp(),
  `status` enum('Active','Inactive','Graduated','On Leave') DEFAULT 'Active',
  `year_level` varchar(20) DEFAULT NULL,
  `active` tinyint(1) DEFAULT 1,
  PRIMARY KEY (`student_id`),
  UNIQUE KEY `id_number` (`id_number`),
  KEY `course_id` (`course_id`),
  KEY `council_id` (`council_id`),
  CONSTRAINT `students_ibfk_1` FOREIGN KEY (`course_id`) REFERENCES `courses` (`course_id`),
  CONSTRAINT `students_ibfk_2` FOREIGN KEY (`council_id`) REFERENCES `councils` (`council_id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- Dumping data for table library_system.students: ~8 rows (approximately)
INSERT INTO `students` (`student_id`, `id_number`, `first_name`, `last_name`, `course_id`, `council_id`, `school_year`, `contact_number`, `email`, `registration_date`, `status`, `year_level`, `active`) VALUES
	(3, '2023079', 'Renniel Geo', 'Geanga', 4, 1, '2024 - 2025', '09614166722', 'geangarenniel@gmail.com', '2025-01-29 15:35:02', 'Inactive', '1st Year', 0),
	(4, '1', 'Geo', 'Development', 4, 1, '2024 - 2025', '87', 'asfs@gmail.com', '2025-01-29 18:17:35', 'Inactive', '5th Year', 0),
	(5, '2023078', 'Daniel', 'Libunao', 4, 1, '2025', '23253', 'libunao@gmail.com', '2025-02-05 18:25:33', 'Active', '1st Year', 1),
	(6, '2345678', 'poikjhxc', 'cvj', 4, 1, '2007', '0923456788765', 'adsa@rgfhjkl.com', '2025-02-12 17:14:47', 'Inactive', '1st Year', 0),
	(7, '2023604', 'Louise', 'Ayco', 4, 1, '2025', '0912345678', 'ayvo@gmail.com', '2025-02-12 18:44:54', 'Active', '1st Year', 1),
	(8, '20241325', 'Miko', 'James', 4, 1, '2024', '09123456789', 'mesteban111@gmail.com', '2025-02-24 18:33:05', 'Active', '1st Year', 1),
	(9, '2023795', 'Jessa Mae', 'Morte', 4, 1, '2024-2025', '0939293949394', 'morte@gmail.com', '2025-02-24 18:42:44', 'Active', '2nd Year', 1),
	(10, '2023028', 'robert', 'andulana', 4, 1, '2025', '127565446', 'hentailover@gmail.com', '2025-04-30 15:51:35', 'Active', '1st Year', 1);

-- Dumping structure for view library_system.vw_available_books
DROP VIEW IF EXISTS `vw_available_books`;
-- Creating temporary table to overcome VIEW dependency errors
CREATE TABLE `vw_available_books` (
	`book_id` INT(11) NOT NULL,
	`isbn` VARCHAR(1) NULL COLLATE 'utf8mb4_general_ci',
	`title` VARCHAR(1) NOT NULL COLLATE 'utf8mb4_general_ci',
	`author` VARCHAR(1) NOT NULL COLLATE 'utf8mb4_general_ci',
	`publisher` VARCHAR(1) NULL COLLATE 'utf8mb4_general_ci',
	`publication_year` INT(11) NULL,
	`category` VARCHAR(1) NULL COLLATE 'utf8mb4_general_ci',
	`quantity` INT(11) NULL,
	`available_quantity` INT(11) NULL,
	`location` VARCHAR(1) NULL COLLATE 'utf8mb4_general_ci',
	`added_date` DATETIME NULL,
	`status` ENUM('Available','Reserved','Maintenance') NULL COLLATE 'utf8mb4_general_ci',
	`active` TINYINT(1) NULL,
	`price` DOUBLE NOT NULL,
	`borrowed_count` BIGINT(12) NULL
) ENGINE=MyISAM;

-- Dumping structure for view library_system.vw_student_borrowings
DROP VIEW IF EXISTS `vw_student_borrowings`;
-- Creating temporary table to overcome VIEW dependency errors
CREATE TABLE `vw_student_borrowings` (
	`borrowing_id` INT(11) NOT NULL,
	`student_name` VARCHAR(1) NOT NULL COLLATE 'utf8mb4_general_ci',
	`id_number` VARCHAR(1) NOT NULL COLLATE 'utf8mb4_general_ci',
	`book_title` VARCHAR(1) NOT NULL COLLATE 'utf8mb4_general_ci',
	`borrow_date` DATETIME NULL,
	`due_date` DATETIME NOT NULL,
	`return_date` DATETIME NULL,
	`status` VARCHAR(1) NULL COLLATE 'utf8mb4_general_ci',
	`fine_amount` DECIMAL(10,2) NULL
) ENGINE=MyISAM;

-- Removing temporary table and create final VIEW structure
DROP TABLE IF EXISTS `vw_available_books`;
CREATE ALGORITHM=UNDEFINED SQL SECURITY DEFINER VIEW `vw_available_books` AS SELECT b.*, (b.quantity - b.available_quantity) as borrowed_count
FROM books b
WHERE b.status = 'Available' 
;

-- Removing temporary table and create final VIEW structure
DROP TABLE IF EXISTS `vw_student_borrowings`;
CREATE ALGORITHM=UNDEFINED SQL SECURITY DEFINER VIEW `vw_student_borrowings` AS SELECT 
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
JOIN books bk ON b.book_id = bk.book_id 
;

/*!40103 SET TIME_ZONE=IFNULL(@OLD_TIME_ZONE, 'system') */;
/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IFNULL(@OLD_FOREIGN_KEY_CHECKS, 1) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40111 SET SQL_NOTES=IFNULL(@OLD_SQL_NOTES, 1) */;
