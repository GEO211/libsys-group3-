package com.library.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.library.models.AuditLog;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AuditLogger {
    private static final Logger logger = LogManager.getLogger(AuditLogger.class);
    
    // New method for detailed logging
    public static void log(String action, int bookId, int studentId, int adminId, String details) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            createTableIfNotExists(conn);
            
            String query = """
                INSERT INTO audit_log (action, book_id, student_id, admin_id, details)
                VALUES (?, ?, ?, ?, ?)
                """;
                
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setString(1, action);
                pstmt.setInt(2, bookId);
                pstmt.setInt(3, studentId);
                pstmt.setInt(4, adminId);
                pstmt.setString(5, details);
                pstmt.executeUpdate();
                logger.info("Audit log created: {} - {}", action, details);
            }
        } catch (SQLException e) {
            logger.error("Error creating audit log: {}", e.getMessage());
            e.printStackTrace();
        }
    }
    
    // Keep the old method for backward compatibility
    public static void logAction(String actionType, String description, int adminId) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            createTableIfNotExists(conn);
            
            String query = """
                INSERT INTO audit_log (action, admin_id, details)
                VALUES (?, ?, ?)
                """;
                
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setString(1, actionType);
                pstmt.setInt(2, adminId);
                pstmt.setString(3, description);
                pstmt.executeUpdate();
                logger.info("Audit log created: {} - {}", actionType, description);
            }
        } catch (SQLException e) {
            logger.error("Error creating audit log: {}", e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static void createTableIfNotExists(Connection conn) throws SQLException {
        String createTable = """
            CREATE TABLE IF NOT EXISTS audit_log (
                log_id INT PRIMARY KEY AUTO_INCREMENT,
                timestamp DATETIME DEFAULT CURRENT_TIMESTAMP,
                action VARCHAR(50) NOT NULL,
                book_id INT NULL,
                student_id INT NULL,
                admin_id INT NOT NULL,
                details TEXT,
                FOREIGN KEY (book_id) REFERENCES books(id),
                FOREIGN KEY (student_id) REFERENCES students(id),
                FOREIGN KEY (admin_id) REFERENCES admins(admin_id)
            )
            """;
            
        try (Statement stmt = conn.createStatement()) {
            stmt.execute(createTable);
        }
    }
    
    public static List<AuditLog> getRecentLogs(int limit) {
        List<AuditLog> logs = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = """
                SELECT l.*, CONCAT(a.first_name, ' ', a.last_name) as admin_name 
                FROM audit_log l 
                JOIN admins a ON l.admin_id = a.admin_id 
                ORDER BY l.timestamp DESC LIMIT ?
                """;
            
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, limit);
                ResultSet rs = pstmt.executeQuery();
                
                while (rs.next()) {
                    logs.add(new AuditLog(
                        rs.getInt("log_id"),
                        rs.getString("action"),
                        rs.getString("details"),
                        rs.getTimestamp("timestamp"),
                        rs.getInt("admin_id"),
                        rs.getString("admin_name")
                    ));
                }
            }
        } catch (SQLException e) {
            logger.error("Error retrieving audit logs: {}", e.getMessage());
            e.printStackTrace();
        }
        
        return logs;
    }
    
    public static List<AuditLog> searchLogs(String searchTerm, String actionType, 
                                          Timestamp startDate, Timestamp endDate) {
        List<AuditLog> logs = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            StringBuilder sql = new StringBuilder("""
                SELECT l.*, CONCAT(a.first_name, ' ', a.last_name) as admin_name 
                FROM audit_log l 
                JOIN admins a ON l.admin_id = a.admin_id 
                WHERE 1=1
                """);
            
            List<Object> params = new ArrayList<>();
            
            if (searchTerm != null && !searchTerm.trim().isEmpty()) {
                sql.append(" AND (l.details LIKE ? OR a.first_name LIKE ? OR a.last_name LIKE ?)");
                String term = "%" + searchTerm.trim() + "%";
                params.add(term);
                params.add(term);
                params.add(term);
            }
            
            if (actionType != null && !actionType.trim().isEmpty()) {
                sql.append(" AND l.action = ?");
                params.add(actionType);
            }
            
            if (startDate != null) {
                sql.append(" AND l.timestamp >= ?");
                params.add(startDate);
            }
            
            if (endDate != null) {
                sql.append(" AND l.timestamp <= ?");
                params.add(endDate);
            }
            
            sql.append(" ORDER BY l.timestamp DESC");
            
            try (PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {
                for (int i = 0; i < params.size(); i++) {
                    pstmt.setObject(i + 1, params.get(i));
                }
                
                ResultSet rs = pstmt.executeQuery();
                while (rs.next()) {
                    logs.add(new AuditLog(
                        rs.getInt("log_id"),
                        rs.getString("action"),
                        rs.getString("details"),
                        rs.getTimestamp("timestamp"),
                        rs.getInt("admin_id"),
                        rs.getString("admin_name")
                    ));
                }
            }
        } catch (SQLException e) {
            logger.error("Error searching audit logs: {}", e.getMessage());
            e.printStackTrace();
        }
        
        return logs;
    }
} 