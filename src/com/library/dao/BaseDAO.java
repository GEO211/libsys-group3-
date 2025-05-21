package com.library.dao;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.library.util.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public abstract class BaseDAO<T> {
    private static final Logger logger = LogManager.getLogger(BaseDAO.class);
    protected final String tableName;
    
    protected BaseDAO(String tableName) {
        this.tableName = tableName;
    }
    
    protected abstract T mapResultSetToEntity(ResultSet rs) throws SQLException;
    
    protected List<T> executeQuery(String sql, Object... params) {
        List<T> results = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            for (int i = 0; i < params.length; i++) {
                stmt.setObject(i + 1, params[i]);
            }
            
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                results.add(mapResultSetToEntity(rs));
            }
            
        } catch (SQLException e) {
            logger.error("Error executing query: {} - {}", sql, e.getMessage());
            throw new RuntimeException("Database query failed", e);
        }
        return results;
    }
    
    protected T executeSingleResultQuery(String sql, Object... params) {
        List<T> results = executeQuery(sql, params);
        return results.isEmpty() ? null : results.get(0);
    }
    
    protected int executeUpdate(String sql, Object... params) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            for (int i = 0; i < params.length; i++) {
                stmt.setObject(i + 1, params[i]);
            }
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                logger.warn("Update operation affected no rows: {}", sql);
            }
            
            return affectedRows;
            
        } catch (SQLException e) {
            logger.error("Error executing update: {} - {}", sql, e.getMessage());
            throw new RuntimeException("Database update failed", e);
        }
    }
    
    protected int executeInsert(String sql, Object... params) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            for (int i = 0; i < params.length; i++) {
                stmt.setObject(i + 1, params[i]);
            }
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("Creating record failed, no rows affected.");
            }
            
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                } else {
                    throw new SQLException("Creating record failed, no ID obtained.");
                }
            }
            
        } catch (SQLException e) {
            logger.error("Error executing insert: {} - {}", sql, e.getMessage());
            throw new RuntimeException("Database insert failed", e);
        }
    }
    
    protected <R> R executeTransaction(Function<Connection, R> operation) {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);
            
            R result = operation.apply(conn);
            
            conn.commit();
            return result;
            
        } catch (Exception e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    logger.error("Error rolling back transaction: {}", ex.getMessage());
                }
            }
            logger.error("Transaction failed: {}", e.getMessage());
            throw new RuntimeException("Transaction failed", e);
            
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    logger.error("Error closing connection: {}", e.getMessage());
                }
            }
        }
    }
    
    protected void executeBatch(String sql, List<Object[]> paramsList) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            conn.setAutoCommit(false);
            
            for (Object[] params : paramsList) {
                for (int i = 0; i < params.length; i++) {
                    stmt.setObject(i + 1, params[i]);
                }
                stmt.addBatch();
            }
            
            int[] results = stmt.executeBatch();
            conn.commit();
            
            logger.debug("Batch execution completed - {} operations", results.length);
            
        } catch (SQLException e) {
            logger.error("Error executing batch: {} - {}", sql, e.getMessage());
            throw new RuntimeException("Batch operation failed", e);
        }
    }
    
    protected boolean exists(String column, Object value) {
        String sql = "SELECT COUNT(*) FROM " + tableName + " WHERE " + column + " = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setObject(1, value);
            ResultSet rs = stmt.executeQuery();
            
            return rs.next() && rs.getInt(1) > 0;
            
        } catch (SQLException e) {
            logger.error("Error checking existence: {} - {}", sql, e.getMessage());
            throw new RuntimeException("Existence check failed", e);
        }
    }
    
    protected int count(String whereClause, Object... params) {
        String sql = "SELECT COUNT(*) FROM " + tableName;
        if (whereClause != null && !whereClause.isEmpty()) {
            sql += " WHERE " + whereClause;
        }
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            for (int i = 0; i < params.length; i++) {
                stmt.setObject(i + 1, params[i]);
            }
            
            ResultSet rs = stmt.executeQuery();
            return rs.next() ? rs.getInt(1) : 0;
            
        } catch (SQLException e) {
            logger.error("Error counting records: {} - {}", sql, e.getMessage());
            throw new RuntimeException("Count operation failed", e);
        }
    }
} 