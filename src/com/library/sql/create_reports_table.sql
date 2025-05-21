CREATE TABLE IF NOT EXISTS reports (
    report_id INT PRIMARY KEY AUTO_INCREMENT,
    report_type VARCHAR(50) NOT NULL,
    book_id INT,
    person_type VARCHAR(20),
    person_id VARCHAR(50),
    description TEXT,
    status VARCHAR(20) DEFAULT 'Pending',
    processed_by INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    resolved_at TIMESTAMP,
    FOREIGN KEY (book_id) REFERENCES books(book_id),
    FOREIGN KEY (processed_by) REFERENCES admins(admin_id)
); 