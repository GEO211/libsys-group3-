package com.library.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.io.*;

public class ConfigurationUtil {
    private static final Logger logger = LogManager.getLogger(ConfigurationUtil.class);
    private static final Map<String, String> settingsCache = new HashMap<>();
    private static final long CACHE_DURATION = 300000; // 5 minutes
    private static long lastCacheUpdate = 0;
    
    public static String getSetting(String key) {
        return getSetting(key, null);
    }
    
    public static String getSetting(String key, String defaultValue) {
        refreshCacheIfNeeded();
        return settingsCache.getOrDefault(key, defaultValue);
    }
    
    public static boolean getSettingAsBoolean(String key) {
        return Boolean.parseBoolean(getSetting(key, "false"));
    }
    
    public static int getSettingAsInt(String key, int defaultValue) {
        try {
            return Integer.parseInt(getSetting(key, String.valueOf(defaultValue)));
        } catch (NumberFormatException e) {
            logger.error("Error parsing setting {} as integer: {}", key, e.getMessage());
            return defaultValue;
        }
    }
    
    public static double getSettingAsDouble(String key, double defaultValue) {
        try {
            return Double.parseDouble(getSetting(key, String.valueOf(defaultValue)));
        } catch (NumberFormatException e) {
            logger.error("Error parsing setting {} as double: {}", key, e.getMessage());
            return defaultValue;
        }
    }
    
    public static void setSetting(String key, String value) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                 "INSERT INTO settings (setting_key, setting_value) VALUES (?, ?) " +
                 "ON DUPLICATE KEY UPDATE setting_value = ?")) {
            
            pstmt.setString(1, key);
            pstmt.setString(2, value);
            pstmt.setString(3, value);
            pstmt.executeUpdate();
            
            settingsCache.put(key, value);
            
        } catch (SQLException e) {
            logger.error("Error saving setting: {}", e.getMessage());
            throw new RuntimeException("Failed to save setting", e);
        }
    }
    
    public static Map<String, String> getAllSettings() {
        refreshCacheIfNeeded();
        return new HashMap<>(settingsCache);
    }
    
    private static void refreshCacheIfNeeded() {
        if (System.currentTimeMillis() - lastCacheUpdate > CACHE_DURATION) {
            loadSettingsFromDatabase();
        }
    }
    
    private static void loadSettingsFromDatabase() {
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT setting_key, setting_value FROM settings")) {
            
            settingsCache.clear();
            while (rs.next()) {
                settingsCache.put(rs.getString("setting_key"), rs.getString("setting_value"));
            }
            lastCacheUpdate = System.currentTimeMillis();
            
        } catch (SQLException e) {
            logger.error("Error loading settings: {}", e.getMessage());
        }
    }
    
    public static void exportSettings(String filePath) {
        try {
            Map<String, String> settings = getAllSettings();
            Properties properties = new Properties();
            properties.putAll(settings);
            properties.store(new FileOutputStream(filePath), "Library Settings");
            logger.info("Settings exported to: {}", filePath);
        } catch (Exception e) {
            logger.error("Error exporting settings: {}", e.getMessage());
            throw new RuntimeException("Failed to export settings", e);
        }
    }
    
    public static void importSettings(String filePath) {
        try {
            Properties properties = new Properties();
            properties.load(new FileInputStream(filePath));
            
            for (Map.Entry<Object, Object> entry : properties.entrySet()) {
                setSetting(entry.getKey().toString(), entry.getValue().toString());
            }
            
            logger.info("Settings imported from: {}", filePath);
        } catch (Exception e) {
            logger.error("Error importing settings: {}", e.getMessage());
            throw new RuntimeException("Failed to import settings", e);
        }
    }
} 