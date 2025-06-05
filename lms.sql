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

-- Data exporting was unselected.

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

-- Data exporting was unselected.

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

-- Data exporting was unselected.

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

-- Data exporting was unselected.

-- Dumping structure for table library_system.councils
DROP TABLE IF EXISTS `councils`;
CREATE TABLE IF NOT EXISTS `councils` (
  `council_id` int(11) NOT NULL AUTO_INCREMENT,
  `council_name` varchar(50) NOT NULL,
  `description` text DEFAULT NULL,
  PRIMARY KEY (`council_id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- Data exporting was unselected.

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

-- Data exporting was unselected.

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

-- Data exporting was unselected.

-- Dumping structure for table library_system.settings
DROP TABLE IF EXISTS `settings`;
CREATE TABLE IF NOT EXISTS `settings` (
  `setting_key` varchar(50) NOT NULL,
  `setting_value` text DEFAULT NULL,
  `description` text DEFAULT NULL,
  `updated_at` datetime DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  PRIMARY KEY (`setting_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- Data exporting was unselected.

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

-- Data exporting was unselected.

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
