-- Create Councils table if not exists
CREATE TABLE IF NOT EXISTS councils (
    council_id INT PRIMARY KEY AUTO_INCREMENT,
    council_name VARCHAR(50) NOT NULL,
    description TEXT
);

-- Create Courses table if not exists
CREATE TABLE IF NOT EXISTS courses (
    course_id INT PRIMARY KEY AUTO_INCREMENT,
    council_id INT,
    course_name VARCHAR(100) NOT NULL,
    course_code VARCHAR(20) NOT NULL,
    FOREIGN KEY (council_id) REFERENCES councils(council_id)
);

-- Insert default councils
INSERT IGNORE INTO councils (council_name, description) VALUES
('CEAC', 'College of Engineering, Architecture and Computing'),
('CAS', 'College of Arts and Sciences'),
('CBA', 'College of Business Administration'),
('BPED', 'College of Education'),
('Personal', 'Faculty and Staff'),
('Others', 'External Users'); 