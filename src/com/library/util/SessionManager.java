package com.library.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.sql.*;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

public class SessionManager {
    private static final Logger logger = LogManager.getLogger(SessionManager.class);
    private static final Map<String, Session> sessions = new ConcurrentHashMap<>();
    private static final long SESSION_TIMEOUT = 30; // minutes
    
    public static String createSession(int adminId, String username, String role) {
        cleanExpiredSessions();
        String token = SecurityUtil.generateSecureToken();
        Session session = new Session(adminId, username, role);
        sessions.put(token, session);
        logger.info("New session created for user: {}", username);
        return token;
    }
    
    public static Session getSession(String token) {
        if (token == null) return null;
        Session session = sessions.get(token);
        if (session != null && !session.isExpired()) {
            session.updateLastAccess();
            return session;
        }
        if (session != null) {
            sessions.remove(token);
            logger.info("Session expired and removed for user: {}", session.getUsername());
        }
        return null;
    }
    
    public static void invalidateSession(String token) {
        Session session = sessions.remove(token);
        if (session != null) {
            logger.info("Session invalidated for user: {}", session.getUsername());
            AuditLogger.logAction("LOGOUT", "User logged out", session.getAdminId());
        }
    }
    
    public static void invalidateAllSessions() {
        sessions.clear();
        logger.info("All sessions invalidated");
    }
    
    private static void cleanExpiredSessions() {
        sessions.entrySet().removeIf(entry -> {
            if (entry.getValue().isExpired()) {
                logger.info("Expired session removed for user: {}", entry.getValue().getUsername());
                return true;
            }
            return false;
        });
    }
    
    public static class Session {
        private final int adminId;
        private final String username;
        private final String role;
        private Instant lastAccess;
        private final Map<String, Object> attributes;
        
        public Session(int adminId, String username, String role) {
            this.adminId = adminId;
            this.username = username;
            this.role = role;
            this.lastAccess = Instant.now();
            this.attributes = new ConcurrentHashMap<>();
        }
        
        public int getAdminId() {
            return adminId;
        }
        
        public String getUsername() {
            return username;
        }
        
        public String getRole() {
            return role;
        }
        
        public void setAttribute(String key, Object value) {
            attributes.put(key, value);
        }
        
        public Object getAttribute(String key) {
            return attributes.get(key);
        }
        
        public void removeAttribute(String key) {
            attributes.remove(key);
        }
        
        public boolean hasRole(String requiredRole) {
            return role.equals(requiredRole) || role.equals("Super Admin");
        }
        
        private boolean isExpired() {
            return lastAccess.plus(SESSION_TIMEOUT, ChronoUnit.MINUTES).isBefore(Instant.now());
        }
        
        private void updateLastAccess() {
            this.lastAccess = Instant.now();
        }
    }
} 