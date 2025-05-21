package com.library.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.sql.*;
import java.io.*;
import java.util.*;
import java.nio.file.*;

public class DatabaseMigration {
    private static final Logger logger = LogManager.getLogger(DatabaseMigration.class);
    private static final String MIGRATIONS_PATH = "src/main/resources/migrations";
    private static final String VERSION_TABLE = "schema_version";
    
    public static void initialize() {
        createVersionTable();
        runMigrations();
    }
    
    private static void createVersionTable() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "CREATE TABLE IF NOT EXISTS " + VERSION_TABLE + " (" +
                        "version INT PRIMARY KEY," +
                        "script_name VARCHAR(255) NOT NULL," +
                        "applied_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                        "success BOOLEAN NOT NULL" +
                        ")";
            
            Statement stmt = conn.createStatement();
            stmt.execute(sql);
            logger.info("Schema version table created or verified");
            
        } catch (SQLException e) {
            logger.error("Error creating version table: {}", e.getMessage());
            throw new RuntimeException("Failed to create version table", e);
        }
    }
    
    private static void runMigrations() {
        try {
            List<MigrationScript> pendingScripts = getPendingMigrations();
            if (pendingScripts.isEmpty()) {
                logger.info("Database schema is up to date");
                return;
            }
            
            logger.info("Found {} pending migrations", pendingScripts.size());
            
            for (MigrationScript script : pendingScripts) {
                applyMigration(script);
            }
            
        } catch (Exception e) {
            logger.error("Error running migrations: {}", e.getMessage());
            throw new RuntimeException("Failed to run migrations", e);
        }
    }
    
    private static List<MigrationScript> getPendingMigrations() throws IOException {
        List<MigrationScript> pendingScripts = new ArrayList<>();
        Set<Integer> appliedVersions = getAppliedVersions();
        
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(
                Paths.get(MIGRATIONS_PATH), "V*__*.sql")) {
            
            for (Path path : stream) {
                String fileName = path.getFileName().toString();
                if (fileName.matches("V\\d+__.*\\.sql")) {
                    int version = extractVersion(fileName);
                    if (!appliedVersions.contains(version)) {
                        pendingScripts.add(new MigrationScript(version, fileName, path));
                    }
                }
            }
        }
        
        Collections.sort(pendingScripts);
        return pendingScripts;
    }
    
    private static Set<Integer> getAppliedVersions() {
        Set<Integer> versions = new HashSet<>();
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT version FROM " + VERSION_TABLE + " WHERE success = true";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            
            while (rs.next()) {
                versions.add(rs.getInt("version"));
            }
            
        } catch (SQLException e) {
            logger.error("Error getting applied versions: {}", e.getMessage());
            throw new RuntimeException("Failed to get applied versions", e);
        }
        
        return versions;
    }
    
    private static void applyMigration(MigrationScript script) {
        logger.info("Applying migration: {}", script.fileName);
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);
            
            try {
                // Execute migration script
                String sqlContent = new String(Files.readAllBytes(script.path));
                Statement stmt = conn.createStatement();
                stmt.execute(sqlContent);
                
                // Record successful migration
                String sql = "INSERT INTO " + VERSION_TABLE + 
                           " (version, script_name, success) VALUES (?, ?, true)";
                PreparedStatement pstmt = conn.prepareStatement(sql);
                pstmt.setInt(1, script.version);
                pstmt.setString(2, script.fileName);
                pstmt.executeUpdate();
                
                conn.commit();
                logger.info("Migration applied successfully: {}", script.fileName);
                
            } catch (Exception e) {
                conn.rollback();
                logger.error("Error applying migration {}: {}", script.fileName, e.getMessage());
                throw new RuntimeException("Migration failed: " + script.fileName, e);
            } finally {
                conn.setAutoCommit(true);
            }
            
        } catch (SQLException e) {
            logger.error("Database error during migration: {}", e.getMessage());
            throw new RuntimeException("Migration failed due to database error", e);
        }
    }
    
    private static int extractVersion(String fileName) {
        return Integer.parseInt(fileName.substring(1, fileName.indexOf("__")));
    }
    
    private static class MigrationScript implements Comparable<MigrationScript> {
        final int version;
        final String fileName;
        final Path path;
        
        MigrationScript(int version, String fileName, Path path) {
            this.version = version;
            this.fileName = fileName;
            this.path = path;
        }
        
        @Override
        public int compareTo(MigrationScript other) {
            return Integer.compare(this.version, other.version);
        }
    }
} 